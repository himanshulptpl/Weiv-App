package com.weivapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.JsonWriter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.weivapp.R;
import com.weivapp.constants.PhotoGalleryConstants;
import com.weivapp.model.UserActivityData;
import com.weivapp.model.UserInfo;
import com.weivapp.utils.AccountUtils;

public class UserSelectionActivity extends Activity {

	ListView userListView;
	List<UserInfo> userList;
	List<UserActivityData> userActivityList;

	private TextView emptyText;
	// private ImageView rightNavigation;
	// private ImageView leftNavigation;
	private String imageUrl;
	private String userComment;
	private String path;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//
		// //Remove notification bar
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_user_selection);

		userListView = (ListView) findViewById(R.id.list_user);
		emptyText = (TextView) findViewById(R.id.txtEmpty);

		imageUrl = getIntent().getStringExtra("fileName");
		userComment = getIntent().getStringExtra("userComment");

		// userActivityList = new ArrayList<UserInfo>();
		path = Environment.getExternalStorageDirectory() + "/"
				+ PhotoGalleryConstants.DIRECTORY_NAME + "/"
				+ PhotoGalleryConstants.USER_ACTIVITY_JSON_FILE_NAME;
		File directory = new File(path.replace("/"
				+ PhotoGalleryConstants.USER_ACTIVITY_JSON_FILE_NAME, ""));
		if (!directory.exists()) {
			SharedPreferences preferences = getSharedPreferences(
					PhotoGalleryConstants.KEY_PREFERENCE, 0);
			Editor editPreference = preferences.edit();
			editPreference.putBoolean(
					PhotoGalleryConstants.KEY_PREFERENCE_NAME, true).commit();
			// firstTime =
			// preferences.getBoolean(PhotoGalleryConstants.KEY_PREFERENCE_NAME,
			// false);

			directory.mkdir();
		}
		File file = new File(path);
		if (!file.exists()) {
			try {

				file.createNewFile();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		new GetUserActivityList().execute();

		userList = getContactList(this);

	/*	userList.add(new UserInfo("Sam0", null, null, false, 0));
		userList.add(new UserInfo("Sam1", null, null, false, 1));
		userList.add(new UserInfo("Sam2", null, null, false, 2));
		userList.add(new UserInfo("Sam3", null, null, false, 3));
		userList.add(new UserInfo("Sam4", null, null, false, 4));*/

		userListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CheckBox chk = (CheckBox) view.findViewById(R.id.chk_user);
				chk.callOnClick();

				String message = ""
				/* + PhotoGalleryConstants.USER_IMAGE_DESCIPTION */
				+ userList.get(position).getName()/* + "\n"+ "" + userComment */;

				userActivityList.add(new UserActivityData(message, userComment,
						imageUrl, false, Long.valueOf(
								System.currentTimeMillis()).intValue()));

				new WriteUserActivityList().execute();
			}
		});

		// leftNavigation = (ImageView) findViewById(R.id.left_navigation);
		// rightNavigation = (ImageView) findViewById(R.id.right_navigation);
		//
		// rightNavigation.setImageResource(R.drawable.ic_action_accept);
		// leftNavigation.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// finish();
		//
		// }
		// });
		//
		//
		// rightNavigation.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// nextOrPrevious.setClass(UserSelectionActivity.this,
		// HomeActivity.class);
		// nextOrPrevious.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(nextOrPrevious);
		// finish();
		// }
		// });

		userListView.setEmptyView(emptyText);
		userListView.setAdapter(new UserAdapter());
		if (!userList.isEmpty()) {
			// emptyText.setVisibility(View.GONE);
		}
	}

	class LoadUserList extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}

	class GetUserActivityList extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			userActivityList = new ArrayList<UserActivityData>();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				StringBuilder buf = new StringBuilder();
				InputStream json = new FileInputStream(path);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						json, "UTF-8"));
				String str;

				while ((str = in.readLine()) != null) {
					buf.append(str);
				}
				in.close();

				if (buf.length() > 0) {
					try {
						JSONArray jsonList = new JSONArray(buf.toString());
						JSONObject jsonObject;
						String comment, url,message;
						// int id;
						for (int index = 0; index < jsonList.length(); index++) {

							jsonObject = jsonList.getJSONObject(index);
							comment = jsonObject
									.getString(PhotoGalleryConstants.USER_ACTIVITY_NAME_KEY);
							url = jsonObject
									.getString(PhotoGalleryConstants.USER_ACTIVITY_IMAGE_KEY);
							
							message= jsonObject
									.getString(PhotoGalleryConstants.USER_ACTIVITY_MESSAGE_KEY);
							// id =
							// jsonObject.getInt(PhotoGalleryConstants.USER_ACTIVITY_ID_KEY);
							userActivityList.add(new UserActivityData(comment,message, url,
									false, index));

						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			super.onPostExecute(result);
		}
	}

	class WriteUserActivityList extends AsyncTask<Void, Void, Void> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(UserSelectionActivity.this, "",
					"Loading...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				OutputStream out = new FileOutputStream(path);
				writeJsonStream(out, userActivityList);
				out.flush();
				out.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			dialog.dismiss();
			final Intent nextOrPrevious = new Intent();
			nextOrPrevious.setClass(UserSelectionActivity.this,
					HomeActivity.class);

			nextOrPrevious.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(nextOrPrevious);
			finish();

			super.onPostExecute(result);
		}
	}

	public void writeJsonStream(OutputStream out, List<UserActivityData> messages)
			throws IOException {
		JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
		writer.setIndent("  ");
		writeMessagesArray(writer, messages);
		writer.close();

	}

	public void writeMessagesArray(JsonWriter writer, List<UserActivityData> messages)
			throws IOException {
		writer.beginArray();
		for (UserActivityData message : messages) {
			writeMessage(writer, message);
		}
		writer.endArray();
	}

	public void writeMessage(JsonWriter writer, UserActivityData message)
			throws IOException {
		writer.beginObject();
		writer.name(PhotoGalleryConstants.USER_ACTIVITY_ID_KEY).value(
				message.getId());
		writer.name(PhotoGalleryConstants.USER_ACTIVITY_NAME_KEY).value(
				message.getName());
		writer.name(PhotoGalleryConstants.USER_ACTIVITY_MESSAGE_KEY).value(
				message.getMessage());
		
		// if (message.getGeo() != null) {
		// writer.name("geo");
		// writeDoublesArray(writer, message.getGeo());
		// } else {
		// writer.name("geo").nullValue();
		// }
		writer.name(PhotoGalleryConstants.USER_ACTIVITY_IMAGE_KEY).value(
				message.getImgeUrl());
		// writeUser(writer, message.getUser());
		writer.endObject();
	}

	// public void writeUser(JsonWriter writer, User user) throws IOException {
	// writer.beginObject();
	// writer.name("name").value(user.getName());
	// writer.name("followers_count").value(user.getFollowersCount());
	// writer.endObject();
	// }
	//
	// public void writeDoublesArray(JsonWriter writer, List doubles) throws
	// IOException {
	// writer.beginArray();
	// for (Double value : doubles) {
	// writer.value(value);
	// }
	// writer.endArray();
	// }

	private class UserAdapter extends BaseAdapter {
		ImageView imgUser;
		TextView userName;
		CheckBox checkBox;

		// public UserAdapter(Context context) {
		// super();
		//
		// }

		@Override
		public int getCount() {
			return userList.size();
		}

		@Override
		public UserInfo getItem(int position) {
			return userList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return userList.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder viewHolder;
			if (convertView == null) {
				final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				convertView = inflater.inflate(R.layout.user_list_row, parent,
						false);
				
				viewHolder = new ViewHolder();
				viewHolder.imgUser = (ImageView) convertView.findViewById(R.id.img_user);
				viewHolder.userName = (TextView) convertView.findViewById(R.id.txt_name);
				viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.chk_user);

				// store the holder with the view.
				convertView.setTag(viewHolder);
			}
			else{
				// we've just avoided calling findViewById() on resource everytime
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.imgUser.setImageResource(R.drawable.ic_launcher);
			viewHolder.userName.setText(userList.get(position).getName());
			viewHolder.checkBox.setChecked(userList.get(position).getStatus());

			return convertView;
		}

	}
	
	static class ViewHolder {
		public CheckBox checkBox;
		public TextView userName;
		public ImageView imgUser;
	}
	
	public static List<UserInfo> getContactList(Context ctx){
		List<UserInfo> phoneBookContact = AccountUtils.getInstance().getCachedContacts();
		if(phoneBookContact== null){
			phoneBookContact = new ArrayList<UserInfo>();
			Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
			String[] projection    = new String[] {Phone.CONTACT_ID,Phone.DISPLAY_NAME,	Phone.NUMBER};

			Cursor people = ctx.getContentResolver().query(uri, projection, null, null, Phone.DISPLAY_NAME + " ASC");

			int indexId = people.getColumnIndex(Phone.CONTACT_ID);
			int indexName = people.getColumnIndex(Phone.DISPLAY_NAME);
			int indexNumber = people.getColumnIndex(Phone.NUMBER);
			ArrayList<String> phoneNumList = new ArrayList<String>();
			String phoneNo;
			people.moveToFirst();
			do {
				try{
					phoneNo = people.getString(indexNumber);
					phoneNo = com.weivapp.utils.Utils.massagePhoneNo(ctx, phoneNo);
					if(!phoneNumList.contains(phoneNo)){
						phoneNumList.add(phoneNo);
						phoneBookContact.add(new UserInfo(Integer.parseInt(people.getString(indexId)),people.getString(indexName),
								phoneNo,"",false));
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			} while (people.moveToNext());

			AccountUtils.getInstance().setCachedContacts(phoneBookContact);
		}
		return phoneBookContact;
	}

}
