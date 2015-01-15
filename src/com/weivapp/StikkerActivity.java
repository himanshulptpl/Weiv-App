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
import java.util.Calendar;
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
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.aviary.android.feather.FeatherActivity;
import com.aviary.android.feather.library.Constants;
import com.meetme.android.horizontallistview.HorizontalListView;
import com.weivapp.constants.PhotoGalleryConstants;
import com.weivapp.model.BitmapUtility;
import com.weivapp.model.UserActivityData;
import com.weivapp.utils.Stiker;
import com.weivapp.utils.Utils;

public class StikkerActivity extends Activity implements OnClickListener {
	private View edit_button, add_comment_button;
	private EditText userComment;
	private int[] images;
	ImageView share, next, back, delete;
	Bitmap bitmap;
	HorizontalListView sview;
	ResizableRectangle main;
	// ImageView delete;
	static String imagefileName;
	private String path;
	Bitmap myBitmap = null;
	List<UserActivityData> userActivityList;
	public static final String KEY_FILE_NAME = "fileName";
	ProgressDialog dialog;
	static boolean toDelete, edit = false;

	@Override
	public void onBackPressed() {
		if (toDelete)
			new File(imagefileName).delete();
		// Intent intent = new Intent(this, CameraActivity.class);
		Intent intent = new Intent(this, HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
		super.onBackPressed();
	}

	public static Bitmap mark(Bitmap src, String watermark) {// , "Weiv App")
		int w = src.getWidth();
		int h = src.getHeight();
		Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(src, 0, 0, null);
		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);
		paint.setColor(0x66FFFFCC);
		canvas.drawRect(new Rect(0, h - 30, w, h - 5), paint);
		paint.setColor(0xff006699);
		paint.setTextSize(15);

		float tw = paint.measureText(watermark);

		canvas.drawText(watermark, w - tw - 20, h - 12, paint);

		return result;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// ActionBar mActionBar = getActionBar();
		// mActionBar.setDisplayShowHomeEnabled(false);
		// mActionBar.setDisplayShowTitleEnabled(false);
		// //LayoutInflater mInflater = LayoutInflater.from(this);
		//
		// //View mCustomView = mInflater.inflate(R.layout.actionbar_layout,
		// null);
		// //mCustomView.findViewById(R.id.next).setOnClickListener(this);
		// //mCustomView.findViewById(R.id.back).setOnClickListener(this);
		// //mActionBar.setCustomView(mCustomView);
		//
		// mActionBar.setDisplayShowCustomEnabled(true);

		// /ab.setCustomView(R.layout.actionbar_layout);
		setContentView(R.layout.gallery);
		next = (ImageView) findViewById(R.id.next);
		back = (ImageView) findViewById(R.id.next);
		delete = (ImageView) findViewById(R.id.delete);
		next.setOnClickListener(this);
		back.setOnClickListener(this);
		delete.setOnClickListener(this);
		edit_button = findViewById(R.id.img_edit);
		add_comment_button = findViewById(R.id.add_comment_button);
		userComment = (EditText) findViewById(R.id.txt_comment);
		userComment.setVisibility(View.GONE);

		edit_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editImage();
			}
		});

		add_comment_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				userComment.setVisibility(View.VISIBLE);
			}
		});

		main = (ResizableRectangle) findViewById(R.id.resizableRectangle1);
		sview = (HorizontalListView) findViewById(R.id.sview);
		delete.setVisibility(View.GONE);

		images = new int[38];
		for (int i = 0; i < images.length; i++) {
			if (i <= 9)
				images[i] = getResources().getIdentifier("f00" + i, "drawable",
						getPackageName());
			else if (i > 9 && i <= 99)
				images[i] = getResources().getIdentifier("f0" + i, "drawable",
						getPackageName());
			else
				images[i] = getResources().getIdentifier("f" + i, "drawable",
						getPackageName());
		}
		imagefileName = getIntent().getStringExtra(HomeActivity.KEY_FILE_NAME);

		toDelete = getIntent().getBooleanExtra("delete", true);

		// rectangle = new ResizableRectangle(StikkerActivity.this);

		// myBitmap = BitmapFactory.decodeFile(imagefileName);
		// Bitmap original = BitmapFactory.decodeFile(imagefileName);
		DisplayMetrics metrics = getApplicationContext().getResources()
				.getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;

		myBitmap = BitmapUtility.decodeSampledBitmapFromFile(imagefileName,
				width / 2, height / 2);
		// main.setImageBitmap(mark(myBitmap, "\u00A9 AWeiv App"));

		bitmapOrientation();
		// width = original.getWidth();
		// height = original.getHeight();
		setbackground(myBitmap);
		// main.setBackgroundBitmap(myBitmap);
		sview.setAdapter(new GaleryListAdapter(images, "", this));
		sview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				Stiker stiker = new Stiker();
				stiker.setMatrix(new Matrix());
				stiker.setStiker(BitmapFactory.decodeResource(getResources(),
						images[position]));
				stiker.setSelected(true);
				RectF map = new RectF(0, 0, stiker.getStiker().getWidth(),
						stiker.getStiker().getHeight());
				stiker.setMap(map);
				main.getStikerlst().add(stiker);
				main.setSelectedstiker(main.getStikerlst().size() - 1);
				main.invalidate();
				delete.setVisibility(View.VISIBLE);
			}
		});

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

		// Thread.currentThread().setDefaultUncaughtExceptionHandler(new
		// MyUncaughtExceptionHandler());
	}

	void setbackground(Bitmap bitmap) {

		Stiker stiker = new Stiker();
		stiker.setMatrix(new Matrix());
		stiker.setStiker(bitmap);
		stiker.setSelected(true);
		RectF map = new RectF(0, 0, stiker.getStiker().getWidth(), stiker
				.getStiker().getHeight());
		stiker.setMap(map);
		main.getStikerlst().add(0, stiker);
		// main.getStikerlst().add(stiker);
		main.setSelectedstiker(main.getStikerlst().size() - 1);
		main.invalidate();
	}

	private void bitmapOrientation() {
		try {
			ExifInterface exif = new ExifInterface(imagefileName);
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, 1);
			Log.d("EXIF", "Exif: " + orientation);
			Matrix matrix = new Matrix();
			if (orientation == 6) {
				matrix.postRotate(90);
			} else if (orientation == 3) {
				matrix.postRotate(180);
			} else if (orientation == 8) {
				matrix.postRotate(270);
			}
			myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(),
					myBitmap.getHeight(), matrix, true); // rotating bitmap
		} catch (Exception e) {

		}
	}

	private void editImage() {
		saveCreation();
		Intent newIntent = new Intent(this, FeatherActivity.class);
		newIntent.setData(Uri.parse(imagefileName));
		newIntent.putExtra(Constants.EXTRA_IN_API_KEY_SECRET,
				"20d83c2d930af300");
		startActivityForResult(newIntent, 1);
	}

	public static String getRealPathFromUri(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null,
					null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1:
				// output image path
				Uri mImageUri = data.getData();
				String mImageString = getRealPathFromUri(this, mImageUri);
				// imagefileName = mImageUri.toString();
				Bundle extra = data.getExtras();
				if (null != extra) {
					// image has been changed by the user?
					boolean changed = extra
							.getBoolean(Constants.EXTRA_OUT_BITMAP_CHANGED);
					if (changed) {
						myBitmap = BitmapFactory.decodeFile(mImageString);
						main.getStikerlst().clear();
						// main.isEdit = true;
						// main.saveBitmap(true);
						edit = true;
						setbackground(myBitmap);
						// main.setBackgroundBitmap(myBitmap);
						Utils.copyFile(new File(mImageString), new File(
								imagefileName));
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

	public String fromInt(int val) {
		return String.valueOf(val);
	}

	public int dpToPx(int dp) {
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		int px = Math.round(dp
				* (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;
	}

	private void saveCreation() {
		main.isEdit = false;
		main.invalidate();
		Calendar c = Calendar.getInstance();
		String date = fromInt(c.get(Calendar.MONTH))
				+ fromInt(c.get(Calendar.DAY_OF_MONTH))
				+ fromInt(c.get(Calendar.YEAR))
				+ fromInt(c.get(Calendar.HOUR_OF_DAY))
				+ fromInt(c.get(Calendar.MINUTE))
				+ fromInt(c.get(Calendar.SECOND));

		main.setDrawingCacheEnabled(true);
		Bitmap pic = Bitmap.createBitmap(main.getDrawingCache());
		File dir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
						+ PhotoGalleryConstants.DIRECTORY_NAME);
		dir.mkdirs();
		File file = new File(dir, "weiv-" + date.toString() + ".png");
		if (file.exists())
			file.delete();
		try {
			FileOutputStream out = new FileOutputStream(file);
			pic.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			Utils.scanPhoto(this, file.toString());
			imagefileName = file.getAbsolutePath();
			// myBitmap = BitmapFactory.decodeFile(imagefileName);
			// main.setImageBitmap(mark(myBitmap, "\u00A9 AWeiv App"));
			// main.getStikerlst().clear();
			// setbackground(myBitmap);
			// main.setBackgroundBitmap(myBitmap);
			// Intent showPreview = new Intent(this,
			// ImagePreviewActivity.class);
			// showPreview.putExtra(KEY_FILE_NAME,
			// file.getAbsolutePath());
			// startActivity(showPreview);
			// finish();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.delete:
			if (main.removeSelectedStiker() < 1)
				delete.setVisibility(View.GONE);
			else
				delete.setVisibility(View.VISIBLE);
			break;
		case R.id.back:
			onBackPressed();
			break;

		case R.id.next:
			dialog = ProgressDialog
					.show(StikkerActivity.this, "", "Loading...");
			main.printlogo(true);
			saveCreation();
			userActivityList.add(new UserActivityData("weiv", userComment
					.getText().toString(), imagefileName, true, Long.valueOf(
					System.currentTimeMillis()).intValue()));

			new WriteUserActivityList().execute();
			break;
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
						String comment, url, message;
						boolean status;
						// int id;
						for (int index = 0; index < jsonList.length(); index++) {

							jsonObject = jsonList.getJSONObject(index);
							comment = jsonObject
									.getString(PhotoGalleryConstants.USER_ACTIVITY_NAME_KEY);
							url = jsonObject
									.getString(PhotoGalleryConstants.USER_ACTIVITY_IMAGE_KEY);

							message = jsonObject
									.getString(PhotoGalleryConstants.USER_ACTIVITY_MESSAGE_KEY);
							status = jsonObject
									.getBoolean(PhotoGalleryConstants.USER_IMAGE_STATUS);
							// id =
							// jsonObject.getInt(PhotoGalleryConstants.USER_ACTIVITY_ID_KEY);
							userActivityList.add(new UserActivityData(comment,
									message, url, status, index));

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

		@Override
		protected void onPreExecute() {
			// dialog = ProgressDialog.show(StikkerActivity.this, "",
			// "Loading...");
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
			nextOrPrevious.setClass(StikkerActivity.this, HomeActivity.class);

			nextOrPrevious.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(nextOrPrevious);
			finish();

			super.onPostExecute(result);
		}
	}

	public void writeJsonStream(OutputStream out,
			List<UserActivityData> messages) throws IOException {
		JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
		writer.setIndent("  ");
		writeMessagesArray(writer, messages);
		writer.close();

	}

	public void writeMessagesArray(JsonWriter writer,
			List<UserActivityData> messages) throws IOException {
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
		writer.name(PhotoGalleryConstants.USER_IMAGE_STATUS).value(
				message.getStatus());

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

	// public static class MyUncaughtExceptionHandler implements
	// Thread.UncaughtExceptionHandler {
	// @Override
	// public void uncaughtException(Thread thread, Throwable ex) {
	// if(ex.getClass().equals(OutOfMemoryError.class))
	// {
	// try {
	// android.os.Debug.dumpHprofData("/sdcard/dump.hprof");
	// }
	// catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// ex.printStackTrace();
	// }
	// }

}