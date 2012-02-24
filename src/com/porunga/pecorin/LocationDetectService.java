package com.porunga.pecorin;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

  private void displayNotificationMessage(String message) {
    Notification notification =
        new Notification(R.drawable.pecorin_notify,
                         message, System.currentTimeMillis());
    notification.flags = Notification.FLAG_NO_CLEAR;
        
    PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                                                                new Intent(this, PecorinActivity.class), 0);
    notification.setLatestEventInfo(this, TAG, message, contentIntent);
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
          String locationName = recommend.getLocationName();
        }          
      }
      
    }
  }
}
