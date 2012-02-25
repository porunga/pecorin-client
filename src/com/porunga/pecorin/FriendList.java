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
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendList extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.friend_list_activity);
		
		SharedPreferences sharedpref =  getSharedPreferences("preference", MODE_PRIVATE);
		final String myFacebookId = sharedpref.getString("facebook_id", "");
		
		ListView listView = (ListView) findViewById(R.id.listView1);
		
		ArrayList<User> friendList = new ArrayList<User>();
		
		friendList = getUserList();
				
//		For debug
//		friendList.add(new User("100", "Taro"));
//		friendList.add(new User("200", "Jiro"));
		
		UserListAdapter adapter = new UserListAdapter(this, R.layout.friend_list_item, friendList);
		listView.setAdapter(adapter);
		
		LayoutInflater factory = LayoutInflater.from(this);
		final View inputView = factory.inflate(R.layout.pecori_dialog, null);
		final Button pecoriButton1 = (Button) inputView.findViewById(R.id.pecori_button1);
		pecoriButton1.setText(getString((R.string.pecori_button1)));

		final Button pecoriButton2 = (Button) inputView.findViewById(R.id.pecori_button2);
		pecoriButton2.setText(getString((R.string.pecori_button2)));

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher).setTitle("ぺこりしよう！").setCancelable(true).setView(inputView);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView listView = (ListView) parent;
				User item = (User) listView.getItemAtPosition(position);

				final String pecoreeFacebookId = item.getFacebookId();
				final String pecoreeName = item.getName();

				pecoriButton1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// とりあえずdialogを表示
						Toast.makeText(getApplicationContext(), myFacebookId, Toast.LENGTH_SHORT).show();
						Toast.makeText(getApplicationContext(), "Name: " + pecoreeName + "(ID: " + pecoreeFacebookId + ")", Toast.LENGTH_SHORT).show();
					}
				});

				pecoriButton2.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// とりあえずdialogを表示
						Toast.makeText(getApplicationContext(), myFacebookId, Toast.LENGTH_SHORT).show();
						Toast.makeText(getApplicationContext(), "Name: " + pecoreeName + "(ID: " + pecoreeFacebookId + ")", Toast.LENGTH_SHORT).show();
					}
				});

				builder.show();
			}
		});
		
		Intent intent = new Intent(this, LocationDetectService.class);
		startService(intent);
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
