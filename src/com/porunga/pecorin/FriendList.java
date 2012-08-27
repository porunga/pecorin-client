package com.porunga.pecorin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendList extends Activity {

	private User mPecorer;
	private String mPecorinServerURL;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.friend_list_activity);
		mPecorinServerURL = getString(R.string.PecorinServerURL);

		SharedPreferences sharedpref =  getSharedPreferences("preference", MODE_PRIVATE);
		final String myFacebookId = sharedpref.getString("facebook_id", "");
		if(myFacebookId.equals("")){
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		} else {
			mPecorer = new User(myFacebookId);
		}
		ListView listView = (ListView) findViewById(R.id.listView1);
		
		ArrayList<User> friendList = new ArrayList<User>();
		
		String target = "near";
		friendList = getUserList(target);
				
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

		final Button pecoriButton3 = (Button) inputView.findViewById(R.id.pecori_button3);
		pecoriButton3.setText(getString((R.string.pecori_button3)));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher).setTitle("ぺこりしよう!!").setCancelable(true).setView(inputView);
		final AlertDialog dialog = builder.create();
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
						dialog.dismiss();
						String type = "c2dm";
						Level level = mPecorer.pecori(pecoreeFacebookId, type, mPecorinServerURL);
						String currentPoint = level.getCurrentPoint();
						String leveledUp = level.getLeveledUp();
						Toast.makeText(getApplicationContext(), "Your Point is " + currentPoint, Toast.LENGTH_SHORT).show();
						if (leveledUp.equals("true")){
							showBadgeDialog(level);
							
						}
					}
				});

				pecoriButton2.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						// とりあえずdialogを表示
						//Toast.makeText(getApplicationContext(), myFacebookId, Toast.LENGTH_SHORT).show();
						//Toast.makeText(getApplicationContext(), "Name: " + pecoreeName + "(ID: " + pecoreeFacebookId + ")", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(FriendList.this, DirectPecoriActivity.class);
						startActivity(intent);
					}
				});
				
				pecoriButton3.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				dialog.show();
				
			}
		});
		
	}
	
	private void showBadgeDialog(Level level){
		String levelName = level.getLevelName();
		Toast.makeText(getApplicationContext(), "Congratulations!!! \n New Badge Unlocked!\n\n" + "[ " + levelName + " ]", Toast.LENGTH_LONG).show();
		View dialogView = getLayoutInflater().inflate(R.layout.badge_dialog, null);
		ImageView badgeImage = (ImageView)dialogView.findViewById(R.id.badgeImage);
		TextView badgeName = (TextView)dialogView.findViewById(R.id.badgeName);
		badgeName.setText("Level: " + levelName);
		AlertDialog.Builder builder = new AlertDialog.Builder(FriendList.this);
		builder.setTitle("New Badge UnLocked!!");
		builder.setView(dialogView);
		builder.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
			
		});
		ImageLoader loader = new ImageLoader(badgeImage);
		loader.execute(level.getImageUrl());
		builder.create().show();
	
	}
	
  @Override
  public void onResume() {
    super.onResume();
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

	private ArrayList<User> getUserList(String target){
		HttpClient client = new DefaultHttpClient();
		
		SharedPreferences sharedpref =  getSharedPreferences("preference", MODE_PRIVATE);
		String pecorinToken = sharedpref.getString("pecorin_token", "");
		
		String url = mPecorinServerURL + "/users" + "?auth=" + pecorinToken + "&target=" + target;
		
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
