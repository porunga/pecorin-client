package com.porunga.pecorin;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
//		For debug
//		friendList.add(new User("100", "Taro"));
//		friendList.add(new User("200", "Jiro"));
		
		UserListAdapter adapter = new UserListAdapter(this, R.layout.friend_list_item, friendList);
		listView.setAdapter(adapter);
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


}
