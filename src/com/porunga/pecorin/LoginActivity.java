package com.porunga.pecorin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
    }
    
    public void onFacebookLogin(View view){
    	Intent intent = new Intent(this,FacebookAuthActivity.class);
    	startActivity(intent);
    }
}
