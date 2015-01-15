package com.weivapp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Utils
{

	public static String massagePhoneNo(Context context, String phoneNo)
	{
		try
		{
			CharSequence seq = phoneNo.subSequence(0, 3);
			if (seq.equals("000"))
			{
				phoneNo = phoneNo.replace(seq, "+");
			}
			phoneNo = phoneNo.replace("(", "");
			phoneNo = phoneNo.replace(")", "");
			phoneNo = phoneNo.replace("-", "");
			phoneNo = phoneNo.replace(" ", "");
			phoneNo = phoneNo.replace("+", "");

			char startChar = phoneNo.charAt(0);
			if (startChar == '0')
			{
				phoneNo = phoneNo.substring(1);
			}
			if (phoneNo.length() >= 10)
			{
				phoneNo = phoneNo.substring(phoneNo.length() - 10);
				/*
				 * String country_code = "+91";
				 * if(!TextUtils.isEmpty(AccountUtils
				 * .getInstance().getLoggedInUser().getCountryCode())) {
				 * country_code =
				 * AccountUtils.getInstance().getLoggedInUser().getCountryCode
				 * (); } else { country_code =
				 * UIUtils.getCurrentNumericCountryCode(context); } phoneNo =
				 * country_code + phoneNo;
				 */
				// phoneNo = phoneNo.replace("+", "");
			}
		}
		catch (Exception e)
		{
		}

		return phoneNo;
	}

	public static void scanPhoto(Context context, String imageFileName)
	{

		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(imageFileName);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		context.sendBroadcast(mediaScanIntent);

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

	public static void copyFile(File sourceFile, File destFile)
	{
		if (!sourceFile.exists())
		{
			System.out.println("sourceFile doesn't exist");
			return;
		}

		try
		{
			FileChannel source = null;
			FileChannel destination = null;

			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			if (destination != null && source != null)
			{
				destination.transferFrom(source, 0, source.size());
			}
			if (source != null)
			{
				source.close();
			}
			if (destination != null)
			{
				destination.close();
			}
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
