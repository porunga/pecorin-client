package com.porunga.pecorin;

import java.util.regex.Pattern;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class FacebookAuthActivity extends Activity {

	private static final String LOGIN_PATH = "http://localhost/login";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebook_auth_activity);

		WebView webView = (WebView) findViewById(R.id.webView);
		webView.loadUrl(LOGIN_PATH);
	
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon){
			}
			
		    @Override
		    public boolean shouldOverrideUrlLoading(WebView view, String url) {		    	
		      return super.shouldOverrideUrlLoading(view, url);
		    }
		});
	}
}
