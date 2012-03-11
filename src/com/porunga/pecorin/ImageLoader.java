package com.porunga.pecorin;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageLoader extends AsyncTask<String, Void, Bitmap> {
  private ImageView imageView;
  public ImageLoader(ImageView imageView) {
    this.imageView = imageView;
  }

  @Override
  protected Bitmap doInBackground(String... urls) {
    Bitmap image = null;
    DefaultHttpClient dhc = new DefaultHttpClient();
    try {
      HttpResponse res = dhc.execute(new HttpGet(urls[0]));
      if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        HttpEntity entity = res.getEntity();
        InputStream in = entity.getContent();
        image = BitmapFactory.decodeStream(in);
      }
    } catch (ClientProtocolException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return image;
  }
  @Override
  protected void onPostExecute(Bitmap result) {
    this.imageView.setImageBitmap(result);
    this.imageView.postInvalidate();
  }
}
