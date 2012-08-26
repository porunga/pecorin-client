package com.porunga.pecorin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.porunga.pecorin.ssl.HttpManager;

public class FacebookAuthActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebook_auth_activity);

		WebView webView = (WebView) findViewById(R.id.webView);
		webView.loadUrl(getString(R.string.PecorinServerURL) + "/login");

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				setProgressBarIndeterminateVisibility(true);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Pattern pattern = Pattern.compile("https://.*success.*auth=(.*)");
				Matcher matcher = pattern.matcher(url);
				if (matcher.find()) {

					JSONObject json = getUserData(url);
					
					String pecorinToken = null;
					String facebookId = null;
					Intent intent = new Intent(getApplicationContext(), DeviceRegistrationActivity.class);
					
					try {
						pecorinToken = matcher.group(1);
						facebookId = json.getString("facebook_id");

						intent.putExtra("pecorin_token", pecorinToken);						
						intent.putExtra("facebook_id", facebookId);
						intent.putExtra("name", json.getString("name"));
						intent.putExtra("image_url", json.getString("image_url"));
						
						SharedPreferences preferences = getSharedPreferences("preference", Activity.MODE_PRIVATE);
						Editor editor = preferences.edit();
						editor.putString("pecorin_token", pecorinToken);
						editor.putString("facebook_id", facebookId);
						editor.commit();
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					startActivity(intent);
				}
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				handler.proceed();
			}
		});
	}
	
	private JSONObject getUserData(String url){
//		HttpClient client = new DefaultHttpClient();
		
		HttpGet request = new HttpGet(url);
		StringBuilder builder = new StringBuilder();

		try {
//			HttpResponse response = client.execute(request);
			HttpResponse response = HttpManager.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpURLConnection.HTTP_OK) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		JSONObject json = null;
		try {
			json = new JSONObject(builder.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
}
