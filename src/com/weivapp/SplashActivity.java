package com.weivapp;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.weivapp.constants.PhotoGalleryConstants;
import com.weivapp.utils.AccountUtils;

public class SplashActivity extends Activity {

	private String path;
	private boolean isDestroyed;
	private boolean firstTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//        getWindow().getWindowStyle().
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		//Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		SharedPreferences preferences = getSharedPreferences(PhotoGalleryConstants.KEY_PREFERENCE, 0);
		firstTime = preferences.getBoolean(PhotoGalleryConstants.KEY_PREFERENCE_NAME, false);

		setContentView(R.layout.activity_splash);
		AccountUtils.init();
		path = Environment.getExternalStorageDirectory() + "/"
				+ PhotoGalleryConstants.DIRECTORY_NAME + "/"
				+ PhotoGalleryConstants.USER_ACTIVITY_JSON_FILE_NAME;

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if(!SplashActivity.this.isDestroyed){
					Intent newIntent = new Intent();
					File dir = new File(path.replace("/"+ PhotoGalleryConstants.USER_ACTIVITY_JSON_FILE_NAME, ""));
					/*if(firstTime){
						newIntent.setClass(SplashActivity.this, HomeActivity.class);
					}else{
						if(dir.exists()){
							new File(path).delete();
							for(File f: dir.listFiles()){
								f.delete();
							}
							dir.delete();
						}*/

					newIntent.setClass(SplashActivity.this, HomeActivity.class);
					newIntent.putExtra("firstTime", true);
					//	}

					startActivity(newIntent);
					finish();
				}
			}
		}, 2000);
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		isDestroyed = true;
		super.onDestroy();
	}
}
