package com.porunga.pecorin;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceRegistrationActivity extends Activity {
	
	final String TAG = "MyAPP_DeviceRegistrationActivity";
	//FacebookAuth‚©‚çŽæ“¾‚µ‚½facebook_name, image_url‚ðŽg‚¤
	final String name = "my_facebook_name";
	final String image_url = "http://graph.facebook.com/my_facebook_id/picture?type=square";
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_registration);
    	Log.d(TAG, "onCreate");
		
    	Button button = (Button)findViewById(R.id.registration_button);
    	TextView welcomeMessageTextView = (TextView)findViewById(R.id.welcom_message);
    	TextView facebookNameTextView = (TextView)findViewById(R.id.facebook_name);
    	TextView facebookImageUrlTextView = (TextView)findViewById(R.id.facebook_image_url);
    	
    	button.setText(getString(R.string.registration_button));
    	welcomeMessageTextView.setText(getString(R.string.welcome_message));
    	facebookNameTextView.setText(name + "‚³‚ñ");
    	facebookImageUrlTextView.setText(image_url);
    	
    	button.setOnClickListener(new OnClickListener(){
    	@Override
   		public void onClick(View v) {
				registeringForC2DM();
	    		Toast.makeText(getApplicationContext(), "registering", Toast.LENGTH_SHORT).show();
    		}});	
	}
	
    private void registeringForC2DM(){
    	Log.d(TAG, "start");
        Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
        registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
        registrationIntent.putExtra("sender", getString(R.string.email_of_sender));
        startService(registrationIntent);
    }

    private void unRegisteringFromC2DM(){
        Intent unRegistrationIntent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
        unRegistrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
        startService(unRegistrationIntent);
    }

}
