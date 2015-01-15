package com.weivapp;

import java.io.ByteArrayOutputStream;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class CameraPreview extends SurfaceView implements SurfaceHolder.Callback
{

	private SurfaceHolder		mHolder;
	private Camera				mCamera;

	private Camera.Parameters	mParameters;
	private byte[]				mBuffer;
	protected boolean			mReleased	= true;

	private String				TAG			= "com.camera.recorder.Preview";
	Context						context;

	// this constructor used when requested as an XML resource
	public CameraPreview(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public CameraPreview(Context context)
	{
		super(context);
		this.context = context;
		init();
	}

	public void init()
	{
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public Bitmap getPic(int x, int y, int width, int height)
	{
		System.gc();
		Bitmap b = null;
		Size s = mParameters.getPreviewSize();

		YuvImage yuvimage = new YuvImage(mBuffer, ImageFormat.NV21, s.width, s.height, null);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		yuvimage.compressToJpeg(new Rect(x, y, width, height), 100, outStream); // make
																				// JPG
		b = BitmapFactory.decodeByteArray(outStream.toByteArray(), 0, outStream.size()); // decode
																							// JPG
		if (b != null)
		{
			Log.i(TAG, "getPic() WxH:" + b.getWidth() + "x" + b.getHeight());
		}
		else
		{
			Log.i(TAG, "getPic(): Bitmap is null..");
		}
		yuvimage = null;
		outStream = null;
		System.gc();
		return b;
	}

	private List<Camera.Size>	mSupportedPreviewSizes;
	private Camera.Size			mPreviewSize;

	private void updateBufferSize()
	{
		mBuffer = null;
		System.gc();

		// prepare a buffer for copying preview data to

		if (!mReleased && mCamera != null)
		{
			int h = mCamera.getParameters().getPreviewSize().height;
			int w = mCamera.getParameters().getPreviewSize().width;
			int bitsPerPixel = ImageFormat.getBitsPerPixel(mCamera.getParameters().getPreviewFormat());
			mBuffer = new byte[w * h * bitsPerPixel / 8];
			Log.i("surfaceCreated", "buffer length is " + mBuffer.length + " bytes");
		}
		else
		{
			mBuffer = new byte[1384200];
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
		if (mSupportedPreviewSizes != null)
		{
			mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
		}

		float ratio;
		// if(mPreviewSize.height >= mPreviewSize.width)
		// ratio = (float) mPreviewSize.height / (float) mPreviewSize.width;
		// else
		ratio = (float) mPreviewSize.width / (float) mPreviewSize.height;

		// One of these methods should be used, second method squishes preview
		// slightly
		setMeasuredDimension(width,height);
		// setMeasuredDimension((int) (height * ratio), height);
	}

	private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h)
	{
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) h / w;

		if (sizes == null) return null;

		Camera.Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		for (Camera.Size size : sizes)
		{
			double ratio = (double) size.height / size.width;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE&&w>size.width) continue;

			if (Math.abs(size.height - targetHeight) < minDiff)
			{
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		if (optimalSize == null)
		{
			minDiff = Double.MAX_VALUE;
			for (Camera.Size size : sizes)
			{
				if (Math.abs(size.height - targetHeight) < minDiff)
				{
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}

		return optimalSize;
	}

	public Camera getCamera()
	{
		return mCamera;
	}

	public SurfaceHolder getMyHolder()
	{
		return mHolder;
	}

	public void aquireCamera(int id, SurfaceHolder holder)
	{

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		// The Surface has been created, acquire the camera and tell it where to
		// draw.
		try
		{

			if (CameraActivity.id == 0) mCamera = Camera.open(); // WARNING:
																	// without
																	// permission
																	// in
																	// Manifest.xml,
																	// crashes
			else mCamera = Camera.open(CameraActivity.id);

		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			Log.i(TAG, "Exception on Camera.open(): " + exception.toString());
			// Toast.makeText(getContext(),
			// "Camera broken, quitting :(",Toast.LENGTH_LONG).show();
			// TODO: exit program
		}

		try
		{
			mCamera.setPreviewDisplay(holder);
			mCamera.setDisplayOrientation(90);
			updateBufferSize();
			mCamera.addCallbackBuffer(mBuffer); // where we'll store the image
												// data
			mCamera.setPreviewCallbackWithBuffer(new PreviewCallback()
			{
				public synchronized void onPreviewFrame(byte[] data, Camera c)
				{

					if (mCamera != null)
					{ // there was a race condition when onStop() was called..
						mCamera.addCallbackBuffer(mBuffer); // it was consumed
															// by the call, add
															// it back
					}
				}
			});
		}
		catch (Exception exception)
		{
			Log.e(TAG, "Exception trying to set preview");
			if (mCamera != null)
			{
				mCamera.release();
				mCamera = null;
			}
			// TODO: add more exception handling logic here
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		// Surface will be destroyed when we return, so stop the preview.
		// Because the CameraDevice object is not a shared resource, it's very
		// important to release it when the activity is paused.
		Log.i(TAG, "SurfaceDestroyed being called");

		if (!mReleased)
		{
			mCamera.stopPreview();
			System.out.println("camera released from surface destroyed");
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mReleased = true;
			// Toast.makeText(context, "Recording stopped", Toast.LENGTH_LONG);
		}
		mCamera = null;
	}

	// FYI: not called for each frame of the camera preview
	// gets called on my phone when keyboard is slid out
	// requesting landscape orientation prevents this from being called as
	// camera tilts
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
	{
		Log.i(TAG, "Preview: surfaceChanged() - size now " + w + "x" + h);
		// Now that the size is known, set up the camera parameters and begin
		// the preview.

		try
		{
			mParameters = mCamera.getParameters();

			// ////rotation handling code///////

			mCamera.setDisplayOrientation(CameraActivity.getOrientation(mCamera));

			// ///////////

			// mParameters.set("orientation","landscape");
			for (Integer i : mParameters.getSupportedPreviewFormats())
			{
				Log.i(TAG, "supported preview format: " + i);
			}

			List<Size> sizes = mParameters.getSupportedPreviewSizes();
			for (Size size : sizes)
			{
				Log.i(TAG, "supported preview size: " + size.width + "x" + size.height);
			}
			mCamera.setParameters(mParameters); // apply the changes
		}
		catch (Exception e)
		{
			// Toast.makeText(context, "exception in surface changed: "+e,
			// Toast.LENGTH_LONG).show();
			// older phone - doesn't support these calls
		}

		// updateBufferSize(); // then use them to calculate
		if (mCamera != null)
		{
			Size p = mCamera.getParameters().getPreviewSize();
			Log.i(TAG, "Preview: checking it was set: " + p.width + "x" + p.height); // DEBUG
			mCamera.startPreview();
		}
	}

	public Parameters getCameraParameters()
	{
		return mCamera.getParameters();
	}

	public void setCameraFocus(AutoFocusCallback autoFocus)
	{
		if (mCamera != null && !mReleased) if (mCamera.getParameters().getFocusMode().equals(mCamera.getParameters().FOCUS_MODE_AUTO)
				|| mCamera.getParameters().getFocusMode().equals(mCamera.getParameters().FOCUS_MODE_MACRO))
		{
			mCamera.autoFocus(autoFocus);
		}
	}

	public void setFlash(boolean flash)
	{
		if (flash)
		{
			mParameters.setFlashMode(Parameters.FLASH_MODE_ON);
			mCamera.setParameters(mParameters);
		}
		else
		{
			mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
			mCamera.setParameters(mParameters);
		}
	}
}