package com.porunga.pecorin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.porunga.pecorin.ssl.HttpManager;

public class User {

	private String facebookId;
	private String name;
	
	public User(String facebookId, String name) {
		this.facebookId = facebookId;
		this.name = name;
	}
	
	public User(String facebookId) {
		this.facebookId = facebookId;
	}
	
	public String getFacebookId() {
		return facebookId;
	}
	public void setFacebook_id(String facebookId) {
		this.facebookId = facebookId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Level pecori(String pecoreeId, String type, String pecorinServerUrl) {
		HttpResponse objResponse = null;
		String api = pecorinServerUrl + "/pecori";

//		HttpClient objHttp = new DefaultHttpClient();
		HttpPost objPost = new HttpPost(api);

		final List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("pecorer_facebook_id", this.facebookId));
		params.add(new BasicNameValuePair("pecoree_facebook_id", pecoreeId));
		params.add(new BasicNameValuePair("type", type));

		StringBuilder builder = new StringBuilder();

		try {

			objPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
//			objResponse = objHttp.execute(objPost);
			objResponse = HttpManager.execute(objPost);

			int statusCode = objResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpURLConnection.HTTP_OK) {
				HttpEntity entity = objResponse.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject json = null;
		String result = builder.toString();
		Level level = null;
		try {
			json = new JSONObject(result);
			String currentPoint = json.getString("current_point");
			String leveledUp = json.getString("leveled_up");
			String levelName = json.getString("level_name");
			String imageUrl = json.getString("image_url");
			String badgeType = json.getString("badge_type");

			level = new Level(currentPoint, levelName, imageUrl, badgeType, leveledUp);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return level;

	}

}
