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
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.style.LeadingMarginSpan.LeadingMarginSpan2;
import android.util.JsonWriter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.weivapp.cacheofimages.ImageLoader;
import com.weivapp.constants.PhotoGalleryConstants;
import com.weivapp.model.RoundedImageView;
import com.weivapp.model.UserActivityData;

public class HomeActivity extends Activity {

	private ImageView camera/* , gallery */;
	// private TextView titleText;
	private ListView contentList;
	public List<UserActivityData> userList;
	public List<UserActivityData> userListToSave;
	private TextView textEmpty;
	private String path;
	private Activity activity;
	public String temp_file_path = null;
	private final int CAMERA_PHOTO_REQUEST_CODE = 1;
	private final int GALLERY_PHOTO_REQUEST_CODE = 2;
	private File imageFileName = null;
	private File directory;
	public static final String KEY_FILE_NAME = "fileName";
	UserAdapter adapter;
	File ff;
	private UiLifecycleHelper uiHelper;
	private boolean canPresentShareDialogWithPhotos;
	private LoginButton loginButton;
	GraphUser user;
	Dialog ImgPost_dialog;
	Bitmap sharefbbmp;

	private static final String PERMISSION = "publish_actions";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		// Remove title bar
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		ColorDrawable colorDrawable = new ColorDrawable(0xff006699);
		ActionBar ab = getActionBar();
		ab.setBackgroundDrawable(colorDrawable);
		// ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		setContentView(R.layout.activity_home);
		activity = this;
		camera = (ImageView) findViewById(R.id.go_to_camera);
		// gallery = (ImageView) findViewById(R.id.go_to_gallary);
		// camera.setImageResource(android.R.drawable.ic_menu_camera);
		directory = new File(Environment.getExternalStorageDirectory() + "/"
				+ PhotoGalleryConstants.DIRECTORY_NAME);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		path = Environment.getExternalStorageDirectory() + "/"
				+ PhotoGalleryConstants.DIRECTORY_NAME + "/"
				+ PhotoGalleryConstants.USER_ACTIVITY_JSON_FILE_NAME;

		// titleText = (TextView) findViewById(R.id.cam_title);
		// titleText.setText(R.string.title_activity_home);

		textEmpty = (TextView) findViewById(R.id.txtEmpty);

		userList = new ArrayList<UserActivityData>();
		userListToSave = new ArrayList<UserActivityData>();
		// userList.add(new
		// UserInfo("user Sam0 is great work out here you gonna find it right now. Check it now.......is great work out here you gonna find it right now. Check it now..is great work out here you gonna find it right now. Check it now",
		// null, true, 0));
		// userList.add(new
		// UserInfo("user Sam1 is great work out here you gonna find it right now. Check it now.......is great work out here you gonna find it right now. Check it now..is great work out here you gonna find it right now. Check it now",
		// null, true, 1));
		// userList.add(new
		// UserInfo("user Sam2 is great work out here you gonna find it right now",
		// null, false, 2));
		// userList.add(new
		// UserInfo("user sam3Check it now.......is great work out here you gonna find it right now. ",
		// null, false, 3));
		// userList.add(new
		// UserInfo("Sam4Check it now..is great work out here you gonna find it right now. Check it now",
		// null, true, 4));
		//
		contentList = (ListView) findViewById(R.id.user_activity_list);
		new GetUserActivityList().execute();
		camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				launchCamera();
				// Intent nextOrPrevious = new Intent();
				// nextOrPrevious.setClass(HomeActivity.this,
				// CameraActivity.class);
				// nextOrPrevious.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// startActivity(nextOrPrevious);
				// finish();
			}
		});

		// gallery.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// launchGallery();
		//
		// }
		// });

		canPresentShareDialogWithPhotos = FacebookDialog.canPresentShareDialog(
				this, FacebookDialog.ShareDialogFeature.PHOTOS);

		// Thread.currentThread().setDefaultUncaughtExceptionHandler(new
		// MyUncaughtExceptionHandler());
	}

	private void launchCamera() {
		Intent takePictureIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		File photoFile = null;
		photoFile = savePhoto();
		ff = photoFile;
		if (photoFile != null) {
			takePictureIntent.putExtra(
					android.provider.MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(photoFile));
			startActivityForResult(takePictureIntent, CAMERA_PHOTO_REQUEST_CODE);
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		if (ff != null) {
			outState.putString("photopath", ff.getAbsolutePath());
			System.out.println("savePhoto().getAbsolutePath()"
					+ ff.getAbsolutePath());
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("photopath")) {
				imageFileName = new File(
						savedInstanceState.getString("photopath"));
			}
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	private void launchGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, GALLERY_PHOTO_REQUEST_CODE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == R.id.gallery)
			launchGallery();
		return super.onOptionsItemSelected(item);
	}

	class GetUserActivityList extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			userList = new ArrayList<UserActivityData>();
			userListToSave = new ArrayList<UserActivityData>();
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
						int id;
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
							id = jsonObject
									.getInt(PhotoGalleryConstants.USER_ACTIVITY_ID_KEY);
							//
							// id =
							// jsonObject.getInt(PhotoGalleryConstants.USER_ACTIVITY_ID_KEY);
							userListToSave.add(new UserActivityData(comment,
									message, url, status, id));
							if (status)
								userList.add(new UserActivityData(comment,
										message, url, status, id));
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
			contentList.setEmptyView(textEmpty);
			Collections.reverse(userList);
			Collections.reverse(userListToSave);
			adapter = new UserAdapter(HomeActivity.this);
			contentList.setAdapter(adapter);

			super.onPostExecute(result);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK) {
			System.out.println("resultCode is" + resultCode);
			switch (requestCode) {
			case CAMERA_PHOTO_REQUEST_CODE:
				// Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
				// savePhoto(thumbnail);
				try {
					Intent intent = new Intent(HomeActivity.this,
							StikkerActivity.class);
					intent.putExtra("delete", true);
					intent.putExtra(KEY_FILE_NAME,
							imageFileName.getAbsolutePath());
					System.out.println("onactivity result"
							+ savePhoto().getAbsolutePath());
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case GALLERY_PHOTO_REQUEST_CODE:
				Uri selectedImageUri = data.getData();

				Intent showPreview = new Intent(this, StikkerActivity.class);
				showPreview.putExtra("delete", false);
				showPreview.putExtra(KEY_FILE_NAME,
						getRealPathFromURI(this, selectedImageUri));
				startActivity(showPreview);
				break;

			default:
				uiHelper.onActivityResult(requestCode, resultCode, data,
						dialogCallback);
				break;
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public String getRealPathFromURI(Context context, Uri contentUri) {
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

	public File savePhoto() {

		// FileOutputStream out = null;
		Calendar c = Calendar.getInstance();
		String date = fromInt(c.get(Calendar.MONTH))
				+ fromInt(c.get(Calendar.DAY_OF_MONTH))
				+ fromInt(c.get(Calendar.YEAR))
				+ fromInt(c.get(Calendar.HOUR_OF_DAY))
				+ fromInt(c.get(Calendar.MINUTE))
				+ fromInt(c.get(Calendar.SECOND));
		imageFileName = new File(directory, date.toString() + ".jpg");

		try {
			if (!imageFileName.exists())
				imageFileName.createNewFile();

			// Matrix mat = new Matrix();
			// mat.postRotate(0);
			// Bitmap bitm = ImageHelper.getResizedBitmap(bmp, 400, 400);
			// Bitmap correctBmp = Bitmap.createBitmap(bmp, 0, 0,
			// bmp.getWidth(),bmp.getHeight(), mat, true);
			// out = new FileOutputStream(imageFileName);
			// correctBmp.compress(Bitmap.CompressFormat.PNG, 100, out);
			//
			// out.flush();
			// out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imageFileName;

	}

	public String fromInt(int val) {
		return String.valueOf(val);
	}

	class UserAdapter extends BaseAdapter {
		ImageView imgUser;
		TextView userName;
		TextView userMessage;
		CheckBox checkBox;
		com.weivapp.model.RoundedImageView roundeduserimage;
		// private Bitmap myBitmap;
		ImageLoader imageLoader;

		// boolean getdim = true;

		public UserAdapter(Context context) {
			imageLoader = new ImageLoader(context);

		}

		// public UserAdapter(Context context) {
		// super();
		//
		// }

		@Override
		public int getCount() {
			return userList.size();
		}

		@Override
		public Object getItem(int position) {
			return userList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return userList.get(position).getId();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			ViewHolder viewHolder;
			if (convertView == null) {
				final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.user_activity_list_row,
						parent, false);

				viewHolder = new ViewHolder();
				viewHolder.imgUser = (ImageView) convertView
						.findViewById(R.id.img_user);
				viewHolder.userName = (TextView) convertView
						.findViewById(R.id.txt_name);
				viewHolder.userMessage = (TextView) convertView
						.findViewById(R.id.txt_message);
				viewHolder.roundeduserimage = (com.weivapp.model.RoundedImageView) convertView
						.findViewById(R.id.imageView_user);
				viewHolder.share = convertView.findViewById(R.id.share);
				viewHolder.fbshare = convertView.findViewById(R.id.fbshare);
				viewHolder.delete = convertView.findViewById(R.id.delete);
				// store the holder with the view.
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			final String imageurl = userList.get(position).getImgeUrl();
			// myBitmap = BitmapFactory.decodeFile(imageurl);
			// myBitmap = BitmapUtility.decodeSampledBitmapFromFile(imageurl,
			// 400,
			// 400);//BitmapFactory.decodeFile(userList.get(position).getImgeUrl());
			// }else{
			// myBitmap = null;
			// myBitmap =
			// BitmapFactory.decodeFile(userList.get(position).getImgeUrl());
			// }

			viewHolder.share.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					openShareIntent(imageurl);

				}
			});

			viewHolder.fbshare.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// shareToFb(imageurl);
					sharefbbmp = BitmapFactory.decodeFile(imageurl);
					fbPostDialog();

				}
			});

			viewHolder.delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					AlertDialog.Builder builder = new Builder(HomeActivity.this);
					builder.setTitle("Delete");
					builder.setMessage("Are you really want to delete it.");
					builder.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									// TODO Auto-generated method stub

									try {
										UserActivityData info = userList
												.get(position);
										int id = info.getId();
										userList.remove(position);
										adapter.notifyDataSetChanged();
										for (int i = 0; i < userListToSave
												.size(); i++) {
											info = userListToSave.get(i);
											if (info.getId() == id) {
												info.setStatus(false);
												userListToSave.set(i, info);
												break;

											}
										}
										new WriteUserActivityList().execute();

									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							});

					builder.setNegativeButton("Cancel", null);

					builder.show();

				}
			});
			// DisplayMetrics displayMetrics =
			// getResources().getDisplayMetrics();
			// int width = displayMetrics.widthPixels;
			// viewHolder.imgUser.getLayoutParams().height = width;

			// if(getdim){
			// try {
			// //myBitmap = BitmapUtility.decodeSampledBitmapFromFile(imageurl,
			// 500, 500);
			// //myBitmap = BitmapFactory.decodeFile(imageurl);
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// getdim = false;
			// }

			imageLoader.DisplayImage(imageurl, viewHolder.imgUser);
			// viewHolder.imgUser.setImageBitmap(myBitmap);

			// viewHolder.roundeduserimage.setImageBitmap(myBitmap);
			viewHolder.roundeduserimage.setImageDrawable(getResources()
					.getDrawable(R.drawable.human));

			// int leftMargin =
			// getResources().getDrawable(R.drawable.ic_launcher).getIntrinsicWidth()
			// + 10;
			// SpannableString ss = new
			// SpannableString(userList.get(position).getName());
			// Exhibiting indentation for the first track lines abaztsa
			// ss.setSpan(new MyLeadingMarginSpan2(3, leftMargin), 0,
			// ss.length(), 0);

			viewHolder.userName.setText(userList.get(position).getName());
			viewHolder.userMessage.setText(userList.get(position).getMessage());
			// View view = (View) convertView.findViewById(R.id.itemshow);
			// if(userList.get(position).getStatus()) {
			// view.setVisibility(View.VISIBLE);
			// convertView.setVisibility(View.VISIBLE);
			// } else {
			// view.setVisibility(View.GONE);
			// convertView.setVisibility(View.GONE);
			// }
			//
			// checkBox.setVisibility(View.GONE);
			// checkBox.setChecked(userList.get(position).getStatus());
			//
			return convertView;
		}

		// private void shareToFb(String imageurl) {
		// Intent fbIntent = new Intent(android.content.Intent.ACTION_SEND);
		// fbIntent.setType("image/*");
		// temp_file_path = imageurl +"";
		// fbIntent.putExtra(Intent.EXTRA_STREAM,
		// Uri.parse("file://"+imageurl));
		//
		// PackageManager packManager = getPackageManager();
		// List<ResolveInfo> resolvedInfoList =
		// packManager.queryIntentActivities(fbIntent,
		// PackageManager.MATCH_DEFAULT_ONLY);
		//
		// boolean resolved = false;
		// for (ResolveInfo resolveInfo : resolvedInfoList) {
		// if
		// (resolveInfo.activityInfo.packageName.startsWith("com.facebook.katana"))
		// {
		// fbIntent.setClassName(resolveInfo.activityInfo.packageName,resolveInfo.activityInfo.name);
		// resolved = true;
		// break;
		// }
		// }
		// if (resolved) {
		// startActivity(fbIntent);
		// } else {
		// Toast.makeText(HomeActivity.this, "Facebbok app isn't found",
		// Toast.LENGTH_LONG).show();
		// }
		// }

		private void openShareIntent(String imageurl) {

			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("image/png");
			temp_file_path = imageurl + "";
			shareIntent.putExtra(Intent.EXTRA_STREAM,
					Uri.parse("file://" + imageurl));

			activity.startActivityForResult(
					Intent.createChooser(shareIntent, "Share Via"), 1);
		}

	}

	static class ViewHolder {
		public View share, fbshare, delete;
		public RoundedImageView roundeduserimage;
		public TextView userMessage;
		public CheckBox checkBox;
		public TextView userName;
		public ImageView imgUser;
	}

	class MyLeadingMarginSpan2 implements LeadingMarginSpan2 {
		private int margin;
		private int lines;

		MyLeadingMarginSpan2(int lines, int margin) {
			this.margin = margin;
			this.lines = lines;
		}

		/* Returns the value to which indentation must be added */
		@Override
		public int getLeadingMargin(boolean first) {
			if (first) {
				/*
				 * This margin will be applied to the number of rows returned by
				 * getLeadingMarginLineCount ()
				 */
				return margin;
			} else {
				// Indent all other rows
				return 0;
			}
		}

		@Override
		public void drawLeadingMargin(Canvas c, Paint p, int x, int dir,
				int top, int baseline, int bottom, CharSequence text,
				int start, int end, boolean first, Layout layout) {

		}

		/*
		 * Returns the number of rows to which is to be applied indent returned
		 * by getLeadingMargin (true) Remark. Indent only applies to the N rows
		 * of one section.
		 */
		@Override
		public int getLeadingMarginLineCount() {
			return lines;
		}
	};

	class WriteUserActivityList extends AsyncTask<Void, Void, Void> {
		// ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			// dialog = ProgressDialog.show(HomeActivity.this, "",
			// "Deleting...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				OutputStream out = new FileOutputStream(path);
				Collections.reverse(userListToSave);
				writeJsonStream(out, userListToSave);
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

			// dialog.dismiss();
			// final Intent nextOrPrevious = new Intent();
			// nextOrPrevious.setClass(StikkerActivity.this,
			// HomeActivity.class);
			//
			// nextOrPrevious.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
			// Intent.FLAG_ACTIVITY_CLEAR_TASK);
			// startActivity(nextOrPrevious);
			// finish();

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

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if ((exception instanceof FacebookOperationCanceledException || exception instanceof FacebookAuthorizationException)) {
			new AlertDialog.Builder(HomeActivity.this)
					.setTitle(R.string.cancelled)
					.setMessage(R.string.permission_not_granted)
					.setPositiveButton(R.string.ok, null).show();
		} else if (state == SessionState.OPENED && sharefbbmp != null) {
			postPhoto();
		}
	}

	private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
		@Override
		public void onError(FacebookDialog.PendingCall pendingCall,
				Exception error, Bundle data) {
			Log.d(getPackageName(),
					String.format("Error: %s", error.toString()));
		}

		@Override
		public void onComplete(FacebookDialog.PendingCall pendingCall,
				Bundle data) {
			Log.d(getPackageName(), "Success!");
		}
	};

	public void fbPostDialog() {
		user = null;
		ImgPost_dialog = new Dialog(HomeActivity.this);
		ImgPost_dialog.setContentView(R.layout.postimage);
		ImgPost_dialog.setTitle("Share Image Via Facebook");
		ImgPost_dialog.show();
		final Button share = (Button) ImgPost_dialog
				.findViewById(R.id.fb_login_button);
		final Button cancel = (Button) ImgPost_dialog.findViewById(R.id.cancel);
		loginButton = (LoginButton) ImgPost_dialog
				.findViewById(R.id.login_button);
		loginButton
				.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
					@Override
					public void onUserInfoFetched(GraphUser user) {
						HomeActivity.this.user = user;
						System.out.println("HomeActivity.this.user :: "
								+ HomeActivity.this.user);
					}
				});

		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ImgPost_dialog.dismiss();

			}
		});

		share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (user != null) {
					performPublish(1, canPresentShareDialogWithPhotos);
				} else {
					loginButton.performClick();
					// if (user != null)
					// performPublish(1,canPresentShareDialogWithPhotos);
				}
				ImgPost_dialog.dismiss();
			}
		});

	}

	private void postPhoto() {
		Bitmap image = sharefbbmp;
		if (hasPublishPermission()) {
			Request request = Request.newUploadPhotoRequest(
					Session.getActiveSession(), image, new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							showPublishResult(getString(R.string.photo_post),
									response.getGraphObject(),
									response.getError());
						}
					});
			request.executeAsync();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		uiHelper.onResume();
		AppEventsLogger.activateApp(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();

		AppEventsLogger.deactivateApp(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	private boolean hasPublishPermission() {
		Session session = Session.getActiveSession();
		return session != null
				&& session.getPermissions().contains("publish_actions");
	}

	private void performPublish(int action, boolean allowNoSession) {
		Session session = Session.getActiveSession();
		if (session != null) {
			if (hasPublishPermission() && sharefbbmp != null) {
				postPhoto();
				return;
			} else if (session.isOpened()) {
				// We need to get new permissions, then complete the action when
				// we get called back.
				session.requestNewPublishPermissions(new Session.NewPermissionsRequest(
						this, PERMISSION));
				return;
			}
		}

		if (allowNoSession && sharefbbmp != null) {
			postPhoto();
		}
	}

	private interface GraphObjectWithId extends GraphObject {
		String getId();
	}

	private void showPublishResult(String message, GraphObject result,
			FacebookRequestError error) {
		// String title = null;
		String alertMessage = null;
		if (error == null) {

			// title = getString(R.string.success);
			String id = result.cast(GraphObjectWithId.class).getId();
			alertMessage = getString(R.string.successfully_posted_post,
					message, id);
			Toast.makeText(HomeActivity.this, alertMessage, Toast.LENGTH_LONG)
					.show();
		} else {
			alertMessage = error.getErrorMessage();
			Toast.makeText(HomeActivity.this, alertMessage, Toast.LENGTH_LONG)
					.show();
			// title = getString(R.string.error);
			// alertMessage = error.getErrorMessage();
		}

		// new
		// AlertDialog.Builder(this).setTitle(title).setMessage(alertMessage)
		// .setPositiveButton(R.string.ok, null).show();
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
