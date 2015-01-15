package com.weivapp.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtility {

	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	 {
	  final int height = options.outHeight;
	  final int width = options.outWidth;
	  int inSampleSize = 1;
	  if (height > reqHeight || width > reqWidth)
	  {
	   final int halfHeight = height / 2;
	   final int halfWidth = width / 2;
	   while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth)
	   {
	    inSampleSize *= 2;
	   }
	  }

	  return inSampleSize;
	 }
	 
	public static Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight)
	 {
	  final BitmapFactory.Options options = new BitmapFactory.Options();
	  options.inJustDecodeBounds = true;
	  BitmapFactory.decodeFile(filePath, options);
	  options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	  options.inJustDecodeBounds = false;
	  return BitmapFactory.decodeFile(filePath, options);
	 }
	public static Bitmap decodeSampledBitmapFromByteArray(byte[] data, int reqWidth, int reqHeight)
	{
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
//		BitmapFactory.decodeFile(respath, options);
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;
//		return BitmapFactory.decodeFile(respath, options);
		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}
}
