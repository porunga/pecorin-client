package com.porunga.pecorin;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

public class C2DMReceiver extends BroadcastReceiver {
	
	final String TAG = "MyAPP_C2DMReceiver";
	//FacebookAuthからfacebook_idを取得する
	String facebook_id = null;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")){
			handleRegistration(context, intent);
		} else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")){
			handleMessage(context, intent);
		}
	}
	
	private void handleRegistration(Context context, Intent intent) {
    	Log.d(TAG, "handleRegistration");
	    String registration_id = intent.getStringExtra("registration_id");
    	Log.d(TAG, "registration_id = " + registration_id );
    	    	
	    if (intent.getStringExtra("error") != null) {
	        // 登録の失敗、後で再度トライの必要あり。
	    } else if (intent.getStringExtra("unregistered") != null) {
	        // 登録解除が完了、認証済みセンダーからの新しいメッセージは拒否される。
	    } else if (registration_id != null) {
		    // 登録 ID をメッセージ送信するサードパーティーのサーバに送信。
		    // これは分離されたスレッドで行う必要がある。

			SharedPreferences sharedpref =  context.getSharedPreferences("preference", context.MODE_PRIVATE);
			facebook_id = sharedpref.getString("facebook_id", null);
	    	
	    	HttpResponse objResponse = postRegistrationId(context.getString(R.string.PecorinServerURL), facebook_id, registration_id);
			int statusCode = objResponse.getStatusLine().getStatusCode();
			String result = "";
			try {
				result = EntityUtils.toString(objResponse.getEntity(), "UTF-8");
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
	    	Toast.makeText(context, statusCode + ":" + result, Toast.LENGTH_SHORT).show();
			
			Log.d(TAG, String.valueOf(statusCode));			
		    // 完了したら、すべての登録が完了したことを憶えておく。 
			
			if (statusCode == 201) {
				Intent newIntent = new Intent(context, FriendList.class);
				newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(newIntent);
			}

	    }
	}
	
	private void handleMessage(Context context, Intent intent) {
	    String message = intent.getStringExtra("message");
	    	    
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher, message, System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		//とりあえず DeviceRegistrationActivity を呼ぶようににしている
		Intent remoteIntent = new Intent(context.getApplicationContext(), DeviceRegistrationActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, remoteIntent, 0);
		notification.setLatestEventInfo(context.getApplicationContext(), context.getString(R.string.app_name), message, contentIntent);
		notificationManager.notify(R.string.app_name, notification);
		
	}
	
	private HttpResponse postRegistrationId(String ServerURL, String facebook_id, String registration_id){
		
		HttpResponse objResponse = null;
//		String api = ServerURL + "/user/" + facebook_id + "/device_registration/" + registration_id;
		String api = ServerURL + "/device_registrations";
		
		HttpClient objHttp = new DefaultHttpClient();
		HttpPost objPost = new HttpPost(api);
				
		final List <NameValuePair> params = new ArrayList <NameValuePair>();
        params.add(new BasicNameValuePair("registration_id", registration_id));
        params.add(new BasicNameValuePair("facebook_id", facebook_id));
        
        try {
        	
			objPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			objResponse = objHttp.execute(objPost);

		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return objResponse;
	}
	
}
