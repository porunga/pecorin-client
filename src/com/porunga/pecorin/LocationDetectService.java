package com.porunga.pecorin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.porunga.pecorin.ssl.HttpManager;
import com.walkbase.positioning.Positioning;
import com.walkbase.positioning.data.Recommendation;

public class LocationDetectService extends Service {
  private static final String WALKBASE_API_KEY = "631e3f9af6cf653d96b578b44c4b9b519dd66c9f";
  private static final int FIVE_MINUTES = 60000;
  private static final String TAG = "PecorinerLocationDetect";
  private String FACEBOOK_ID = "";
  private String PECORIN_TOKEN = "";
  
  private ThreadGroup svrThreads = new ThreadGroup("ServiceWorker");
  private Positioning positioning;
  private VerificationReceiver verificationReceiver;
  private ArrayList<Recommendation> recommendations;

  private boolean continueScanning;
  
  public void onCreate() {
    super.onCreate();
    this.continueScanning = true;

    positioning = new Positioning(this, WALKBASE_API_KEY);
    verificationReceiver = new VerificationReceiver();
    this.registerReceiver(verificationReceiver, new IntentFilter(positioning.getPositioningIntentString()));
    SharedPreferences preferences = getSharedPreferences("preference", Activity.MODE_PRIVATE);
    FACEBOOK_ID = preferences.getString("facebook_id", "");
    PECORIN_TOKEN = preferences.getString("pecorin_token", "");
  }
  
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);
        
    new Thread(svrThreads, new ServiceWorker(), "PecorinerLocationDetect").start();
    //Toast.makeText(this, "start calling", Toast.LENGTH_SHORT).show();
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
          String locationId = recommend.getLocationId();
//String locationId = "dummy";
          HttpPut method = new HttpPut(getString(R.string.PecorinServerURL)+"/user/"+FACEBOOK_ID+"/location");
          ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
          params.add(new BasicNameValuePair("current_location_id", locationId));
          params.add(new BasicNameValuePair("facebook_id", FACEBOOK_ID));
          params.add(new BasicNameValuePair("pecorin_token", PECORIN_TOKEN));
          try {
            method.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
//            DefaultHttpClient httpClient = new DefaultHttpClient();
//            HttpResponse res = httpClient.execute(method);
            HttpResponse res = HttpManager.execute(method);
            Log.i(TAG, "locationId:"+locationId);
            Log.i(TAG, "response:"+res.getEntity().toString());
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
