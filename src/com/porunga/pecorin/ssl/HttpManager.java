package com.porunga.pecorin.ssl;

import java.io.IOException;
import java.security.KeyStore;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

/**
 * HttpManager
 * @author cube
 * @see http://www.glamenv-septzen.net/view/981
 */
public class HttpManager {
	private static final String TAG = "HttpManager";
	private static HttpClient httpclient = new DefaultHttpClient();
    private static HttpContext httpcontext = new BasicHttpContext();
    
	static {
        KeyStore trustStore;
        SSLSocketFactory sf;
		try {
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			sf = new MySSLSocketFactory(trustStore);
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
			throw new RuntimeException(e);
		}
        
        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        Scheme http = new Scheme("http", PlainSocketFactory.getSocketFactory(), 80);
        httpclient.getConnectionManager().getSchemeRegistry().register(http);
        Scheme https = new Scheme("https", sf, 443);
        httpclient.getConnectionManager().getSchemeRegistry().register(https);
 
        httpcontext.setAttribute(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        httpcontext.setAttribute(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
        httpcontext.setAttribute(CoreProtocolPNames.HTTP_CONTENT_CHARSET, HTTP.UTF_8);
	}

	private HttpManager() {
	}

	public static HttpResponse execute(HttpHead head) throws IOException {
		return httpclient.execute(head, httpcontext);
	}

	public static HttpResponse execute(HttpHost host, HttpGet get)
			throws IOException {
		return httpclient.execute(host, get, httpcontext);
	}

	public static HttpResponse execute(HttpGet get) throws IOException {
		return httpclient.execute(get, httpcontext);
	}

	public static HttpResponse execute(HttpPost post) throws IOException {
		return httpclient.execute(post, httpcontext);
	}
	
	public static HttpResponse execute(HttpPut put) throws IOException {
		return httpclient.execute(put, httpcontext);
	}
}