package com.porunga.pecorin;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceRegistrationActivity extends Activity {
	
	final String TAG = "MyAPP_DeviceRegistrationActivity";
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_registration);
    	Log.d(TAG, "onCreate");
		Intent intent = getIntent();
		String name = intent.getStringExtra("name");
		String image_url = intent.getStringExtra("image_url");
    	Button button = (Button)findViewById(R.id.registration_button);
    	TextView welcomeMessageTextView = (TextView)findViewById(R.id.welcom_message);
    	TextView facebookNameTextView = (TextView)findViewById(R.id.facebook_name);
    	TextView facebookImageUrlTextView = (TextView)findViewById(R.id.facebook_image_url);
    	ImageView facebookImage = (ImageView)findViewById(R.id.facebook_image);
    	
    	button.setText(getString(R.string.registration_button));
    	welcomeMessageTextView.setText(getString(R.string.welcome_message));
    	facebookNameTextView.setText(name + "さん");
    	facebookImageUrlTextView.setText(image_url);
    	ImageLoader imgLd = new ImageLoader(facebookImage);
    	imgLd.execute(image_url);
    	
    	button.setOnClickListener(new OnClickListener(){
    	@Override
   		public void onClick(View v) {
				registeringForGCM();
	    		Toast.makeText(getApplicationContext(), "registering", Toast.LENGTH_SHORT).show();
    		}});	
	}

	private void registeringForGCM() {
    	Log.d(TAG, "start");
    	Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
    	registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
    	registrationIntent.putExtra("sender", getString(R.string.senderId));
    	startService(registrationIntent);
	}
	
    private void unRegisteringFromGCM(){
        Intent unRegistrationIntent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
        unRegistrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
        startService(unRegistrationIntent);
    }

}
