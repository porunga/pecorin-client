package com.porunga.pecorin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.walkbase.positioning.Positioning;
import com.walkbase.positioning.data.Recommendation;

public class LocationDetectService extends Service {
  private static final String WALKBASE_API_KEY = "631e3f9af6cf653d96b578b44c4b9b519dd66c9f";
  private static final int FIVE_MINUTES = 300000;
  private static final String TAG = "PecorinerLocationDetect";
  private static final String LOCATION_ID_UPDATE_URL = "";
  
  private ThreadGroup svrThreads = new ThreadGroup("ServiceWorker");
  private Positioning positioning;
  private VerificationReceiver verificationReceiver;
  private ArrayList<Recommendation> recommendations;

  private boolean continueScanning;

  public void onCreate() {
    super.onCreate();
    this.continueScanning = true;

    positioning = new Positioning(this, WALKBASE_API_KEY);
    this.registerReceiver(verificationReceiver, new IntentFilter(positioning.getPositioningIntentString()));
    
  }
  
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);
        
    new Thread(svrThreads, new ServiceWorker(), "PecorinerLocationDetect").start();
    return START_STICKY; 
  }

  class ServiceWorker implements Runnable {
    public void run() {
      continueScanning = true;
      try { 
        while(continueScanning) {
          positioning.fetchRecommendations(new String[]{"244a80bc944707c20d3df1ed1fbd416211baae32"});
          Thread.sleep(FIVE_MINUTES);
        }
      } catch(Exception e) {
        Log.v("LocationDetectService",e.getMessage());
      }
    }
  }

  public void onDestroy() {
    continueScanning = false;
    positioning.finish();
    this.unregisterReceiver(verificationReceiver);
    super.onDestroy();    
  }

  public IBinder onBind(Intent arg0) {
    return null;
  }

  public class VerificationReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
      int intentType = intent.getIntExtra("type",0);
      if (intentType == Positioning.NORMAL_RECOMMENDATION) {
        recommendations = positioning.getRecommendations();
        if(recommendations != null || recommendations.size() > 0){
          Recommendation recommend = (Recommendation)recommendations.get(0);
          //このlocationIdをサーバに送る？
          String locationId = recommend.getLocationId();
          HttpPut method = new HttpPut(LOCATION_ID_UPDATE_URL);
          ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
          params.add(new BasicNameValuePair("current_location_id", locationId));
          try {
            method.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpClient.execute(method);
          } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
          } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
          } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
          }
        }          
      }
      
    }
  }
}
