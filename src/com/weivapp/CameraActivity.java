package com.weivapp;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.weivapp.constants.PhotoGalleryConstants;
import com.weivapp.model.BitmapUtility;

public class CameraActivity extends Activity implements OnClickListener {

	// private SurfaceView preview = null;
	private SurfaceHolder previewHolder = null;
	private boolean isFront;
	private Camera camera = null;
	private byte[] mBuffer;
	private boolean inPreview = false;
	private ImageView image, /* close, */btn_glry,btn_home, img_front_back, img_flash;
	private Bitmap bmp/* , itembmp */;
	private static Bitmap mutableBitmap;
	private static Activity myActivity;
	public static int id;
	boolean FlashCanOn = false;
	boolean isflashon;
	CameraPreview camprevw;
	private boolean mFlashBoolean = false;
	// private PointF start = new PointF();
	// private PointF mid = new PointF();
	// private float oldDist = 1f;
	private File imageFileName = null;
	// File imageFileFolder = null;
	// static public final String DIRECTORY_NAME = "photGallery";
	public static final String KEY_FILE_NAME = "fileName";
	private MediaScannerConnection msConn;
	MediaPlayer _shootMP;
	// private Display d;
	// private int scrnHeight, scrnWidth;
	private ProgressDialog dialog;
	private File directory;
	// private TextView titleText;
	protected int camOrientation;

	// private Camera.Parameters mParameters;
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		int itemId = item.getItemId();
		switch (itemId) {
		case android.R.id.home:
			Intent nextOrPrevious = new Intent();
			nextOrPrevious.setClass(CameraActivity.this, HomeActivity.class);
			nextOrPrevious.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(nextOrPrevious);
			finish();
			break;

		}

		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//
		// // Remove notification bar
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_camera);
		ColorDrawable colorDrawable = new ColorDrawable(0xff006699);

	ActionBar ab = getActionBar();
		ab.setBackgroundDrawable(colorDrawable);
		// ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);

		setContentView(R.layout.activity_camera);
		isFront = false;
		_shootMP = new MediaPlayer();
		camprevw = (CameraPreview) findViewById(R.id.camPreview);
		image = (ImageView) findViewById(R.id.captureButton);
		img_front_back = (ImageView) findViewById(R.id.imageView_front_back);
		btn_glry = (ImageView) findViewById(R.id.btn_glry);
		btn_glry.setOnClickListener(this);
		btn_home = (ImageView) findViewById(R.id.btn_home);
		btn_home.setOnClickListener(this);
		img_flash = (ImageView) findViewById(R.id.imageView_flash);
		img_flash.setBackgroundResource(R.drawable.on_press);
		// close = (ImageView) findViewById(R.id.go_to_camera);
		// close.setImageResource(R.drawable.ic_action_cancel);

		// if (getIntent().getBooleanExtra("firstTime", false)) {
		//
		// findViewById(R.id.cam_header).setVisibility(View.GONE);
		//
		// }

		// close.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent nextOrPrevious = new Intent();
		// nextOrPrevious
		// .setClass(CameraActivity.this, HomeActivity.class);
		// nextOrPrevious.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(nextOrPrevious);
		// finish();
		// }
		// });

		img_front_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				id = getFrontCameraId();
				if (id == -1) {
					Toast.makeText(CameraActivity.this,
							"Front Camera is not available in the device.",
							Toast.LENGTH_SHORT).show();
					id = 0;
				} else {
					changeCameraView();
				}
			}
		});

		img_flash.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isFlashSupported(getPackageManager()) && !isFront) {
					setFlash();
				} else
					Toast.makeText(
							CameraActivity.this,
							"Flash is not supported by device or Front Camera is opened.",
							Toast.LENGTH_SHORT).show();

			}

		});
		// titleText = (TextView) findViewById(R.id.cam_title);
		// titleText.setText("");
//////
		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBack();

			}
		});
		// preview = (SurfaceView) findViewById(R.id.camPreview);

		previewHolder = camprevw.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		previewHolder.setFixedSize(getWindow().getWindowManager()
				.getDefaultDisplay().getWidth(), getWindow().getWindowManager()
				.getDefaultDisplay().getHeight());
		directory = new File(Environment.getExternalStorageDirectory() + "/"
				+ PhotoGalleryConstants.DIRECTORY_NAME);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		myActivity = this;
	}

	int getFrontCameraId() {
		CameraInfo ci = new CameraInfo();
		for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
			Camera.getCameraInfo(i, ci);
			if (ci.facing == CameraInfo.CAMERA_FACING_FRONT)
				return i;
		}
		return -1; // No front-facing camera found
	}

	public void setFlash() {
		try {
			if (mFlashBoolean) {
				camprevw.setFlash(false);
				img_flash.setBackgroundResource(R.drawable.on_press);
			} else {

				camprevw.setFlash(true);
				img_flash.setBackgroundResource(R.drawable.off_press);
			}
			mFlashBoolean = !mFlashBoolean;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void changeCameraView() {
		if (camera != null)
			camprevw.mReleased = false;
		camprevw.surfaceDestroyed(camprevw.getHolder());
		camprevw.init();
		if (isFront == true)
			id = 0;
		else {
		}
		camprevw.surfaceCreated(camprevw.getHolder());
		camera = camprevw.getCamera();
		camera.startPreview();
		isFront = !isFront;
	}

	@Override
	public void onResume() {
		super.onResume();
		releaseCamera();
		if (camera != null)
			camprevw.surfaceDestroyed(camprevw.getHolder());
		camprevw.init();
		id = 0;
		isFront = false;
		camprevw.surfaceCreated(camprevw.getHolder());
		camera = camprevw.getCamera();
		camera.startPreview();
	}

	public static int getOrientation(Camera mCamera2) {
		Camera.CameraInfo info = new CameraInfo();
		Camera.getCameraInfo(0, info);
		int rotation = myActivity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		return result;
	}

	@Override
	public void onPause() {
		if (inPreview) {
			camera.stopPreview();
		}

		camera.release();
		camera = null;
		inPreview = false;
		super.onPause();
	}

	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;
		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;
					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}
		return (result);
	}

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camOrientation = getOrientation(camera);
				camera.setDisplayOrientation(camOrientation);
				camera.setPreviewDisplay(camprevw.getHolder());
			} catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
				Toast.makeText(CameraActivity.this, t.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Camera.Parameters parameters = camera.getParameters();
			Camera.Size size = getBestPreviewSize(width, height, parameters);

			if (size != null) {
				parameters.setPreviewSize(size.width, size.height);
				camera.setParameters(parameters);
				camera.startPreview();
				inPreview = true;
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// no-op
		}
	};

	Camera.PictureCallback photoCallback = new Camera.PictureCallback() {
		public void onPictureTaken(final byte[] data, final Camera camera) {
			dialog = ProgressDialog.show(CameraActivity.this, "",
					"Saving Photo");
			new Thread() {
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (Exception ex) {
						Log.e("photocallback", "error in: " + ex);
					}
					onPictureTake(data, camera);
				}
			}.start();
		}
	};

	public void onPictureTake(byte[] data, Camera camera) {

		bmp = BitmapUtility.decodeSampledBitmapFromByteArray(data, 400, 400);// BitmapFactory.decodeByteArray(data,
		// 0,
		// data.length);
		mutableBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
		savePhoto(mutableBitmap);
		dialog.dismiss();
	}

	/*
	 * private class SavePhotoTask extends AsyncTask < byte[], String, String >
	 * { String filename;
	 * 
	 * @Override protected String doInBackground(byte[]...jpeg) { // Date
	 * currentDate = new Date(System.currentTimeMillis()); Calendar cal =
	 * Calendar.getInstance();
	 * 
	 * filename = (cal.get(Calendar.YEAR) -
	 * 1900)+"_"+(cal.get(Calendar.MONTH))+"_"
	 * +(cal.get(Calendar.HOUR_OF_DAY))+"_"
	 * +(cal.get(Calendar.MINUTE))+"_"+(cal.get(Calendar.SECOND))+".png";
	 * 
	 * File photo = new File(directory, filename);
	 * 
	 * // if (photo.exists()) { // //prompt // photo.delete(); // // } try {
	 * FileOutputStream fos = new FileOutputStream(photo.getAbsolutePath());
	 * fos.write(jpeg[0]); fos.close(); } catch (java.io.IOException e) {
	 * Log.e("PictureDemo", "Exception in photoCallback", e); } return (null); }
	 * }
	 */

	public void savePhoto(Bitmap bmp) {
		// imageFileFolder = new File(Environment.getExternalStorageDirectory(),
		// "Rotate");
		// imageFileFolder.mkdir();
		FileOutputStream out = null;
		Calendar c = Calendar.getInstance();
		String date = fromInt(c.get(Calendar.MONTH))
				+ fromInt(c.get(Calendar.DAY_OF_MONTH))
				+ fromInt(c.get(Calendar.YEAR))
				+ fromInt(c.get(Calendar.HOUR_OF_DAY))
				+ fromInt(c.get(Calendar.MINUTE))
				+ fromInt(c.get(Calendar.SECOND));
		System.out.println("imageFileName::" + imageFileName);
		imageFileName = new File(directory, date.toString() + ".png");

		try 
		{
			Matrix mat = new Matrix();
			mat.postRotate(camOrientation);

			// /////////////////////////////////////////////////

			if (android.os.Build.VERSION.SDK_INT > 13 && isFront) {
				float[] mirrorY = { -1, 0, 0, 0, 1, 0, 0, 0, 1 };
				mat = new Matrix();
				Matrix matrixMirrorY = new Matrix();
				matrixMirrorY.setValues(mirrorY);

				mat.postConcat(matrixMirrorY);

				mat.preRotate(270);

			}

			// //////////////////////////////////////////////////////

			Bitmap correctBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
					bmp.getHeight(), mat, true);
			out = new FileOutputStream(imageFileName);
			correctBmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();

			scanPhoto(imageFileName.toString());

			// MediaScannerConnection.scanFile(this, new String[] {
			// imageFileName.toString() }, new String[] { "image/jpeg" }, null);

			out = null;

			Intent showPreview = new Intent(this, StikkerActivity.class);
			showPreview.putExtra(KEY_FILE_NAME, imageFileName.getAbsolutePath());
			startActivity(showPreview);
			finish();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String fromInt(int val) {
		return String.valueOf(val);
	}

	private boolean isFlashSupported(PackageManager packageManager) {
		// if device support camera flash?
		if (packageManager
				.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
			return true;
		}
		return false;
	}

	public void scanPhoto(final String imageFileName) {

		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(imageFileName);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);

		/*
		 * if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
		 * msConn = new MediaScannerConnection(CameraActivity.this, new
		 * MediaScannerConnectionClient() { public void
		 * onMediaScannerConnected() { msConn.scanFile(imageFileName, null);
		 * Log.i("msClient obj  in Photo Utility", "connection established"); }
		 * 
		 * public void onScanCompleted(String path, Uri uri) {
		 * msConn.disconnect(); Log.i("msClient obj in Photo Utility",
		 * "scan completed"); } }); msConn.connect(); }
		 */

	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
	// onBack();
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	public void onBack() {
		Log.e("onBack :", "yes");
		camera.takePicture(null, null, photoCallback);
		inPreview = false;
		shootSound();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		releaseCamera();
	}

	private void releaseCamera() {
		if (camera != null) {
			camera.stopPreview();

			camera.setPreviewCallback(null);
			camprevw.getHolder().removeCallback(camprevw);
			camera.release(); // release the camera for other applications
			camera = null;
		}
	}

	public void shootSound() {

		AudioManager meng = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		int volume = meng.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

		if (volume != 0) {
			_shootMP = MediaPlayer.create(this, R.raw.unlock);
			_shootMP.start();

		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_home:
				Intent nextOrPrevious = new Intent();
				nextOrPrevious.setClass(CameraActivity.this, HomeActivity.class);
				nextOrPrevious.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(nextOrPrevious);
				finish();
				break;
		case R.id.btn_glry:
//			 Intent intent = new Intent();
//             intent.setType("image/*");
//             intent.setAction(Intent.ACTION_GET_CONTENT);
//             startActivityForResult(Intent.createChooser(intent, ""), PICK_IMAGE);

			Intent intent = new Intent(Intent.ACTION_PICK);
			 intent.setType("image/*");
			startActivityForResult(intent, 100);
			break;

		default:
			break;
		}
	}
	public String getRealPathFromURI(Context context, Uri contentUri) {
		  Cursor cursor = null;
		  try { 
		    String[] proj = { MediaStore.Images.Media.DATA };
		    cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
		    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    cursor.moveToFirst();
		    return cursor.getString(column_index);
		  } finally {
		    if (cursor != null) {
		      cursor.close();
		    }
		  }
		}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (resultCode != Activity.RESULT_CANCELED) 
		{	
			if (requestCode == 100) 
			{
            Uri selectedImageUri = data.getData();
            
            Intent showPreview = new Intent(this, StikkerActivity.class);
			showPreview
					.putExtra(KEY_FILE_NAME, getRealPathFromURI(this ,selectedImageUri));
			startActivity(showPreview);
			finish();
			} 
		}
	}
	/*
	 * 
	 * private CameraPreview mPreview; private Camera mCamera; private ImageView
	 * mCapture;
	 * 
	 * 
	 * @Override protected void onCreate(Bundle savedInstanceState) {
	 * super.onCreate(savedInstanceState);
	 * setContentView(R.layout.activity_camera); mPreview =
	 * (CameraPreview)findViewById(R.id.camPreview); mCapture =
	 * (ImageView)findViewById(R.id.captureButton);
	 * 
	 * if (!safeCameraOpen(0)) { //open home activity } }
	 * 
	 * private boolean safeCameraOpen(int id) { boolean qOpened = false;
	 * 
	 * try { releaseCameraAndPreview(); mCamera = Camera.open(); qOpened =
	 * (mCamera != null); } catch (Exception e) {
	 * Log.e(getString(R.string.app_name), "failed to open Camera");
	 * e.printStackTrace(); }
	 * 
	 * return qOpened; }
	 * 
	 * private void releaseCameraAndPreview() { mPreview.setCamera(null); if
	 * (mCamera != null) { mCamera.release(); mCamera = null; } }
	 * 
	 * @Override public void onPictureTaken(byte[] data, Camera camera) {
	 * 
	 * 
	 * }
	 * 
	 * @Override public void onClick(View v) { switch (v.getId()) { case
	 * R.id.captureButton:
	 * 
	 * break;
	 * 
	 * default:
	 * 
	 * break; }
	 * 
	 * }
	 */
}
