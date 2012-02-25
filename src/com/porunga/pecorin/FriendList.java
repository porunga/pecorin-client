package com.porunga.pecorin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FriendList extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.friend_list_activity);
		
		ListView listView = (ListView) findViewById(R.id.listView1);
		
		ArrayList<User> friendList = new ArrayList<User>();
		
		friendList = getUserList();
				
//		For debug
//		friendList.add(new User("100", "Taro"));
//		friendList.add(new User("200", "Jiro"));
		
		UserListAdapter adapter = new UserListAdapter(this, R.layout.friend_list_item, friendList);
		listView.setAdapter(adapter);
	}

	// ListViewカスタマイズ用のArrayAdapter
	public class UserListAdapter extends ArrayAdapter<User> {
		private LayoutInflater inflater;

		public UserListAdapter(Activity activity, int textViewResourceId, ArrayList<User> items) {
			super(activity, textViewResourceId, items);
			// this.items = items;
			this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (convertView == null) {
				view = inflater.inflate(R.layout.friend_list_item, null);
			}
			User user = (User) this.getItem(position);
			
			TextView name = null;

			if (user != null) {
				name = (TextView) view.findViewById(R.id.friendNameView);
			}
			if (name != null) {
				name.setText(user.getName());
			}
			return view;
		}
	}

	private ArrayList<User> getUserList(){
		HttpClient client = new DefaultHttpClient();
		
		SharedPreferences sharedpref =  getSharedPreferences("preference", MODE_PRIVATE);
		String pecorinToken = sharedpref.getString("pecorin_token", "");
		
		String url = getString(R.string.PecorinServerURL) + "/users" + "?auth=" + pecorinToken;
		
		HttpGet request = new HttpGet(url);
		StringBuilder builder = new StringBuilder();

		try {
			HttpResponse response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpURLConnection.HTTP_OK) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONArray jsons;
		ArrayList<User> dataList = new ArrayList<User>();

		try {
			jsons = new JSONArray(builder.toString());

			for (int i = 0; i < jsons.length(); i++) {
				JSONObject jObject = jsons.getJSONObject(i);

				String facebook_id = jObject.getString("facebook_id");
				String name = jObject.getString("name");
				User user = new User(facebook_id, name);

				dataList.add(user);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataList;
	}

}
