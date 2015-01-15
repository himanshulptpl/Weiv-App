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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.JsonWriter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.aviary.android.feather.FeatherActivity;
import com.aviary.android.feather.library.Constants;
import com.weivapp.constants.PhotoGalleryConstants;
import com.weivapp.model.UserActivityData;
import com.weivapp.utils.Utils;

public class ImagePreviewActivity extends Activity
{

	private String					imagefileName;
	private Bitmap					myBitmap;
	private ImageView				previewImage, leftNavigation, rightNavigation;	
	private View					edit_button, add_comment_button;
	private EditText				userComment;
	private MediaScannerConnection	msConn;
	FileOutputStream				out		= null;
	int								angle	= 0;
	List<UserActivityData>			userActivityList;
	private String					path;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_image_preview);
		imagefileName = getIntent().getStringExtra(CameraActivity.KEY_FILE_NAME);

		previewImage = (ImageView) findViewById(R.id.img_preview);
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		int width = displayMetrics.widthPixels;
		previewImage.getLayoutParams().height = width;
		if (myBitmap == null) myBitmap = BitmapFactory.decodeFile(imagefileName);
		previewImage.setImageBitmap(myBitmap);
		// img_rotate=(ImageView) findViewById(R.id.img_rotate);
		edit_button = findViewById(R.id.img_edit);
		add_comment_button = findViewById(R.id.add_comment_button);
		userComment = (EditText) findViewById(R.id.txt_comment);
		userComment.setVisibility(View.GONE);

		/*
		 * img_rotate.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub rotateimage(myBitmap,imagefileName); } });
		 */

		edit_button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				editImage();
			}
		});

		add_comment_button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				userComment.setVisibility(View.VISIBLE);
			}
		});

		final Intent nextOrPrevious = new Intent();

		leftNavigation = (ImageView) findViewById(R.id.left_navigation);
		rightNavigation = (ImageView) findViewById(R.id.right_navigation);

		leftNavigation.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				new File(imagefileName).delete();
				nextOrPrevious.setClass(ImagePreviewActivity.this, CameraActivity.class);
				nextOrPrevious.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(nextOrPrevious);
				finish();

			}
		});

		rightNavigation.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				userActivityList.add(new UserActivityData("weiv", userComment.getText().toString(), imagefileName, false, Long.valueOf(System.currentTimeMillis()).intValue()));

				new WriteUserActivityList().execute();
				/*
				 * nextOrPrevious.setClass(ImagePreviewActivity.this,
				 * UserSelectionActivity.class);
				 * nextOrPrevious.putExtra("fileName", imagefileName);
				 * nextOrPrevious.putExtra("userComment",
				 * userComment.getText().toString());
				 * nextOrPrevious.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				 * startActivity(nextOrPrevious);
				 */
			}
		});

		path = Environment.getExternalStorageDirectory() + "/" + PhotoGalleryConstants.DIRECTORY_NAME + "/" + PhotoGalleryConstants.USER_ACTIVITY_JSON_FILE_NAME;
		File directory = new File(path.replace("/" + PhotoGalleryConstants.USER_ACTIVITY_JSON_FILE_NAME, ""));
		if (!directory.exists())
		{
			SharedPreferences preferences = getSharedPreferences(PhotoGalleryConstants.KEY_PREFERENCE, 0);
			Editor editPreference = preferences.edit();
			editPreference.putBoolean(PhotoGalleryConstants.KEY_PREFERENCE_NAME, true).commit();
			// firstTime =
			// preferences.getBoolean(PhotoGalleryConstants.KEY_PREFERENCE_NAME,
			// false);

			directory.mkdir();
		}
		File file = new File(path);
		if (!file.exists())
		{
			try
			{
				file.createNewFile();

			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		new GetUserActivityList().execute();
	}

	private void editImage()
	{
		Intent newIntent = new Intent(this, FeatherActivity.class);
		newIntent.setData(Uri.parse(imagefileName));
		newIntent.putExtra(Constants.EXTRA_IN_API_KEY_SECRET, "20d83c2d930af300");
		startActivityForResult(newIntent, 1);
	}

//	@Override
//	public void onClick(View v)
//	{
//		//
//
//		switch (v.getId())
//		{
//
//		// case R.id.left_navigation:
//		// nextOrPrevious.setClass(this, CameraActivity.class);
//		// nextOrPrevious.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		// startActivity(nextOrPrevious);
//		// finish();
//		// break;
//		// case R.id.right_navigation:
//		// nextOrPrevious.setClass(this, UserSelectionActivity.class);
//		// nextOrPrevious.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		// startActivity(nextOrPrevious);
//		//
//		// break;
//
//			default:
//				break;
//		}
//
//	}

	/*
	 * public void scanPhoto(final String imageFileName) {
	 * 
	 * if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
	 * msConn = new MediaScannerConnection(ImagePreviewActivity.this, new
	 * MediaScannerConnectionClient() { public void onMediaScannerConnected() {
	 * msConn.scanFile(imageFileName, null);
	 * Log.i("msClient obj  in Photo Utility", "connection established"); }
	 * 
	 * public void onScanCompleted(String path, Uri uri) { msConn.disconnect();
	 * Log.i("msClient obj in Photo Utility", "scan completed"); } });
	 * msConn.connect(); }
	 * 
	 * }
	 */
	/*
	 * public void rotateimage(Bitmap bmp,String imageFileName) { try { Matrix
	 * mat = new Matrix();
	 * 
	 * 
	 * mat.postRotate(90+angle); Bitmap correctBmp = Bitmap.createBitmap(bmp, 0,
	 * 0, bmp.getWidth(), bmp.getHeight(), mat, true); out = new
	 * FileOutputStream(imageFileName);
	 * correctBmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
	 * 
	 * out.flush(); out.close();
	 * 
	 * scanPhoto(imageFileName.toString());
	 * previewImage.setImageBitmap(correctBmp); out = null; angle=angle+90;
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK)
		{
			switch (requestCode)
			{
				case 1:
					// output image path
					Uri mImageUri = data.getData();
					String mImageString = getRealPathFromUri(this, mImageUri);
					// imagefileName = mImageUri.toString();
					Bundle extra = data.getExtras();
					if (null != extra)
					{
						// image has been changed by the user?
						boolean changed = extra.getBoolean(Constants.EXTRA_OUT_BITMAP_CHANGED);
						if (changed)
						{
							myBitmap = BitmapFactory.decodeFile(mImageString);
							previewImage.setImageBitmap(myBitmap);
							Utils.copyFile(new File(mImageString), new File(imagefileName));
						}

						// / MediaScannerConnection.scanFile(this, new String[]
						// {
						// imagefileName.toString() }, new String[] {
						// "image/jpeg"
						// }, null);

					}
					break;
			}
		}
	}

	public static String getRealPathFromUri(Context context, Uri contentUri)
	{
		Cursor cursor = null;
		try
		{
			String[] proj =
			{ MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		finally
		{
			if (cursor != null)
			{
				cursor.close();
			}
		}
	}

	class GetUserActivityList extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected void onPreExecute()
		{
			userActivityList = new ArrayList<UserActivityData>();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params)
		{

			try
			{
				StringBuilder buf = new StringBuilder();
				InputStream json = new FileInputStream(path);
				BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
				String str;

				while ((str = in.readLine()) != null)
				{
					buf.append(str);
				}
				in.close();

				if (buf.length() > 0)
				{
					try
					{
						JSONArray jsonList = new JSONArray(buf.toString());
						JSONObject jsonObject;
						String comment, url, message;
						// int id;
						for (int index = 0; index < jsonList.length(); index++)
						{

							jsonObject = jsonList.getJSONObject(index);
							comment = jsonObject.getString(PhotoGalleryConstants.USER_ACTIVITY_NAME_KEY);
							url = jsonObject.getString(PhotoGalleryConstants.USER_ACTIVITY_IMAGE_KEY);

							message = jsonObject.getString(PhotoGalleryConstants.USER_ACTIVITY_MESSAGE_KEY);
							// id =
							// jsonObject.getInt(PhotoGalleryConstants.USER_ACTIVITY_ID_KEY);
							userActivityList.add(new UserActivityData(comment, message, url, false, index));

						}

					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}
				}

			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{

			super.onPostExecute(result);
		}
	}

	class WriteUserActivityList extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog	dialog;

		@Override
		protected void onPreExecute()
		{
			dialog = ProgressDialog.show(ImagePreviewActivity.this, "", "Loading...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			try
			{
				OutputStream out = new FileOutputStream(path);
				writeJsonStream(out, userActivityList);
				out.flush();
				out.close();

			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{

			dialog.dismiss();
			final Intent nextOrPrevious = new Intent();
			nextOrPrevious.setClass(ImagePreviewActivity.this, HomeActivity.class);

			nextOrPrevious.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(nextOrPrevious);
			finish();

			super.onPostExecute(result);
		}
	}

	public void writeJsonStream(OutputStream out, List<UserActivityData> messages) throws IOException
	{
		JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
		writer.setIndent("  ");
		writeMessagesArray(writer, messages);
		writer.close();

	}

	public void writeMessagesArray(JsonWriter writer, List<UserActivityData> messages) throws IOException
	{
		writer.beginArray();
		for (UserActivityData message : messages)
		{
			writeMessage(writer, message);
		}
		writer.endArray();
	}

	public void writeMessage(JsonWriter writer, UserActivityData message) throws IOException
	{
		writer.beginObject();
		writer.name(PhotoGalleryConstants.USER_ACTIVITY_ID_KEY).value(message.getId());
		writer.name(PhotoGalleryConstants.USER_ACTIVITY_NAME_KEY).value(message.getName());
		writer.name(PhotoGalleryConstants.USER_ACTIVITY_MESSAGE_KEY).value(message.getMessage());

		// if (message.getGeo() != null) {
		// writer.name("geo");
		// writeDoublesArray(writer, message.getGeo());
		// } else {
		// writer.name("geo").nullValue();
		// }
		writer.name(PhotoGalleryConstants.USER_ACTIVITY_IMAGE_KEY).value(message.getImgeUrl());
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
}
