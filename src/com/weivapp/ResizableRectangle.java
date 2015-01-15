package com.weivapp;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.weivapp.utils.Stiker;

public class ResizableRectangle extends View
{
	Actions				act;

	float				X0				= -1, Y0 = -1;
	float				oldDist			= 1f;
	float				oldRotation		= 0;
	PointF				mid				= new PointF();
	Matrix				savedMat		= new Matrix();
	ArrayList<Stiker>	stikerlst		= new ArrayList<Stiker>();
	private int			selectedstiker	= 0;
	double				w, h;
	float				scale;
	boolean				isEdit			= true;
	private Bitmap		backgroundBitmap,logo;
	boolean 			savebmp 		= true;
	boolean				islogoprinted 	= false;
	

	public Bitmap getBackgroundBitmap()
	{
		return backgroundBitmap;
	}

	public void setBackgroundBitmap(Bitmap backgroundBitmap)
	{
		this.backgroundBitmap = backgroundBitmap;
		stikerlst.clear();
		asp = (float) backgroundBitmap.getWidth() / (float) backgroundBitmap.getHeight();
		logo=BitmapFactory.decodeResource(getContext().getResources(), R.drawable.logo);
		selectedstiker=-1;
	}
	
	public void printlogo(boolean islogoprinted) 
	{
		this.islogoprinted = islogoprinted;
		
	}
	
	public void saveBitmap(boolean savebmp) 
	{
		this.savebmp = savebmp;
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension(stikerlst.get(0).getStiker().getWidth(), stikerlst.get(0).getStiker().getHeight());
		//setMeasuredDimension(backgroundBitmap.getWidth() , backgroundBitmap.getHeight());
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	public enum Actions
	{
		Move, Rotete, NULL
	};

	public ArrayList<Stiker> getStikerlst()
	{
		return stikerlst;
	}

	public void setStikerlst(ArrayList<Stiker> stikerlst)
	{
		this.stikerlst = stikerlst;
	}

	public int getSelectedstiker()
	{
		return selectedstiker;
	}

	public void setSelectedstiker(int selectedstiker)
	{
		this.selectedstiker = selectedstiker;
	}

	float	asp;

	private void init()
	{
	
		act = Actions.Move;
		setFocusable(true);
	}

	public ResizableRectangle(Context context)
	{
		super(context);
		init();
	}

	public ResizableRectangle(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public ResizableRectangle(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}
	
	 public static enum ScalingLogic {
	        CROP, FIT
	    }


	@Override
	protected void onDraw(Canvas canvas)
	{
		int w =canvas.getWidth();
		int h = canvas.getHeight();
		if (backgroundBitmap != null) 
		{
			//ScalingLogic scalingLogic = ScalingLogic.FIT; 
//			int w =canvas.getWidth();
//			int h = canvas.getHeight();
			Rect srcRect = calculateSrcRect(backgroundBitmap.getWidth(), backgroundBitmap.getHeight(), w, h, ScalingLogic.FIT);
		    Rect dstRect = calculateDstRect(backgroundBitmap.getWidth(), backgroundBitmap.getHeight(), w, h, ScalingLogic.CROP);
		    canvas.drawBitmap(backgroundBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
			
//			int w =canvas.getWidth();
//			int h = canvas.getHeight();
//			Rect s = new Rect(0, 0, w,(int) ((float) w / asp));
////			Rect s=new Rect(0,0,backgroundBitmap.getWidth(),backgroundBitmap.getHeight());
////			RectF d=new RectF(0,0,w,h);
//			canvas.drawBitmap(backgroundBitmap,null,s ,new Paint());
			
			
//			Paint paint = new Paint();
//			paint.setStyle(Style.FILL);
//			paint.setAntiAlias(true);
//			paint.setColor(0x66FFFFCC);
//			canvas.drawRect(new Rect(0, h - 30, w, h - 5), paint);
//			paint.setColor(0xff006699);
//			paint.setTextSize(12);
//			float tw = paint.measureText(" Weiv  ");
//			canvas.drawText("Weiv", w - tw - 20, h - 12, paint);
			canvas.drawBitmap(logo,null,new Rect(w-logo.getWidth()-5,h-logo.getHeight()-5,w-5,h-5) ,new Paint());
		}
		for (int i = 0; i < stikerlst.size(); i++)
		{
			Stiker stiker = stikerlst.get(i);
			Matrix mat = stiker.getMatrix();

			float[] f = new float[9];
			mat.getValues(f);

			float scaleX = f[Matrix.MSCALE_X];
			float scaleY = f[Matrix.MSCALE_Y];
			float globalX = f[Matrix.MTRANS_X];
			float globalY = f[Matrix.MTRANS_Y];
			if(i == 0 && StikkerActivity.edit){ 
				StikkerActivity.edit = false;
				int w1 = (w - stiker.getStiker().getWidth()) / 2;
				int h1 = (h - stiker.getStiker().getHeight()) / 2;
				
				if (globalX <= 0) 
					mat.postTranslate(-globalX, h1);
				if (globalY <= 0)
					mat.postTranslate(w1, -globalY);
				if (globalX >= getWidth()) 
					mat.postTranslate(-globalX + getWidth() -w1, 0);
				if (globalY >= getHeight())
					mat.postTranslate(0, getHeight() - globalY - +-h1);
			} else {
				if (globalX <= 0) 
					mat.postTranslate(-globalX /*+ 5*/, 0);
				if (globalY <= 0) 
					mat.postTranslate(0, -globalY /*+ 5*/);
				if (globalX >= getWidth()) 
					mat.postTranslate(-globalX + getWidth() /*- 5*/, 0);
				if (globalY >= getHeight()) 
					mat.postTranslate(0, getHeight() - globalY /*- +-5*/);
			}

			if(i == 0 && isEdit) {
				Rect srcRect = calculateSrcRect(stiker.getStiker().getWidth(), stiker.getStiker().getHeight(), w, h, ScalingLogic.FIT);
			    Rect dstRect = calculateDstRect(stiker.getStiker().getWidth(), stiker.getStiker().getHeight(), w, h, ScalingLogic.CROP);
			    canvas.drawBitmap(stiker.getStiker(), srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
			    
//			} else if(i == 0 && selectedstiker != 0){
//				Rect srcRect = calculateSrcRect(stiker.getStiker().getWidth(), stiker.getStiker().getHeight(), w, h, ScalingLogic.CROP);
//			    Rect dstRect = calculateDstRect(stiker.getStiker().getWidth(), stiker.getStiker().getHeight(), w, h, ScalingLogic.CROP);
//			    canvas.drawBitmap(stiker.getStiker(), srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
			} else {
				canvas.drawBitmap(stiker.getStiker(), mat, null);
			}
			RectF r = new RectF(stiker.getMap());
			mat.mapRect(r);
			stiker.setTmap(r);
			// if (isEdit)
			// {
			// Paint paint = new Paint();
			// paint.setColor(0xff0000ff);
			// paint.setStyle(Style.STROKE);
			// if (selectedstiker == i) paint.setColor(0xffff00ff);
			// canvas.drawRect(stiker.getTmap(), paint);
			//
			// }
			stiker.getPoint().set(globalX, globalY);
			stiker.getScale().set(scaleX, scaleY);
			if(savebmp) {
				savebmp = false;
		    	setDrawingCacheEnabled(true);
		    	Bitmap mainbmp = Bitmap.createBitmap(getDrawingCache());
		    	stiker.setStiker(mainbmp);
		    	
		    }
			
			if(islogoprinted) {
				logo=BitmapFactory.decodeResource(getContext().getResources(), R.drawable.logo);
				canvas.drawBitmap(logo,null,new Rect(canvas.getWidth()-logo.getWidth()-5,canvas.getHeight()-logo.getHeight()-5,canvas.getWidth()-5,canvas.getHeight()-5) ,new Paint());
				islogoprinted = false;
			}
				

		}
		
	}
	
	public static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
	    if (scalingLogic == ScalingLogic.CROP) {
	        final float srcAspect = (float)srcWidth / (float)srcHeight;
	        final float dstAspect = (float)dstWidth / (float)dstHeight;
	 
	        if (srcAspect > dstAspect) {
	            final int srcRectWidth = (int)(srcHeight * dstAspect);
	            final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
	            return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
	        } else {
	            final int srcRectHeight = (int)(srcWidth / dstAspect);
	            final int scrRectTop = (int)(srcHeight - srcRectHeight) / 2;
	            return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
	        }
	    } else {
	        return new Rect(0, 0, srcWidth, srcHeight);
	    }
	}
	
	public static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
	    if (scalingLogic == ScalingLogic.FIT) {
	        final float srcAspect = (float)srcWidth / (float)srcHeight;
	        final float dstAspect = (float)dstWidth / (float)dstHeight;
	 
	        if (srcAspect > dstAspect) {
	            return new Rect(0, 0, dstWidth, (int)(dstWidth / srcAspect));
	        } else {
	            return new Rect(0, 0, (int)(dstHeight * srcAspect), dstHeight);
	        }
	    } else {
	        return new Rect(0, 0, dstWidth, dstHeight);
	    }
	}

	private void selectStiker(float x, float y)
	{
		for (int i = 0; i < stikerlst.size(); i++)
		{
			Stiker stiker = stikerlst.get(i);
			if (stiker.getTmap().contains(x, y)) selectedstiker = i;
		}
	}

	long	lastTapTime;

	public int removeSelectedStiker()
	{
		if (selectedstiker >= 1)
		{
			stikerlst.remove(selectedstiker);
			stikerlst.trimToSize();
			selectedstiker = stikerlst.size() - 1;
			invalidate();
			return stikerlst.size();
		}
		else return selectedstiker;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		
		if (stikerlst.size() >= 0 && selectedstiker >= 0)
		{
			isEdit = false;
			switch (event.getAction() & MotionEvent.ACTION_MASK)
			{
				case MotionEvent.ACTION_DOWN:
					long tapInterval = System.currentTimeMillis() - lastTapTime;
					lastTapTime = System.currentTimeMillis();
					act = Actions.Move;
					X0 = event.getX();
					Y0 = event.getY();
					selectStiker(X0, Y0);
					// if (tapInterval <= 500)
					// {
					// stikerlst.remove(selectedstiker);
					// stikerlst.trimToSize();
					// selectedstiker = stikerlst.size() - 1;
					// invalidate();
					// return false;
					//
					// }
					savedMat.set(stikerlst.get(selectedstiker).getMatrix());

					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					act = Actions.Rotete;
					oldDist = spacing(event);
					oldRotation = rotation(event);

					savedMat.set(stikerlst.get(selectedstiker).getMatrix());

					// Matrix mat = stikerlst.get(selectedstiker).getMatrix();
					// float[] f = new float[9];
					// mat.getValues(f);
					//
					// float scaleX = f[Matrix.MSCALE_X];
					// float scaleY = f[Matrix.MSCALE_Y];
					// float globalX = f[Matrix.MTRANS_X];
					// float globalY = f[Matrix.MTRANS_Y];
					//
					// float h = stikerlst.get(selectedstiker).getStiker()
					// .getHeight()
					// * scaleY
					// / 2
					// + stikerlst.get(selectedstiker).getScale().y;
					// float w = stikerlst.get(selectedstiker).getStiker()
					// .getWidth()
					// * scaleX
					// / 2
					// + stikerlst.get(selectedstiker).getScale().x;
					// float dx=0,dy=0;
					// if(lastglobal.x!=-1&&lastglobal.y!=-1)
					// {
					// dx=globalX-lastglobal.x;
					// dy=globalY-lastglobal.y;
					// }
					// mid.set(globalX + w, globalY + h);
					// lastglobal.set(globalX, globalY);
					midPoint(mid, event);
					break;
				case MotionEvent.ACTION_MOVE:
					switch (act)
					{
						case Move:

							stikerlst.get(selectedstiker).getMatrix().set(savedMat);
							stikerlst.get(selectedstiker).getMatrix().postTranslate(event.getX() - X0, event.getY() - Y0);
							break;
						case Rotete:
							try
							{
								float rotation = rotation(event) - oldRotation;

								// stikerlst.get(selectedstiker).getMatrix().mapRect(stikerlst.get(selectedstiker).getMap());
								float newDist = spacing(event);
								scale = newDist / oldDist;
								stikerlst.get(selectedstiker).getMatrix().set(savedMat);
								stikerlst.get(selectedstiker).getMatrix().postScale(scale, scale, mid.x, mid.y);
								stikerlst.get(selectedstiker).getMatrix().postRotate(rotation, mid.x, mid.y);//
								// rotatepoint(rotation,stikerlst.get(selectedstiker));
							}
							catch (Exception e)
							{

							}
							break;
						default:
							break;
					}
					invalidate();
					break;
				case MotionEvent.ACTION_UP:
					break;
				default:
					break;
			}
		}
		return true;
	}

	private void rotatepoint(float rotate, Stiker st)
	{
		float x = (float) (Math.acos(rotate) * (st.getPoint().x - mid.x) - Math.asin(rotate) * (st.getPoint().y - mid.y) + mid.x);
		float y = (float) (Math.asin(rotate) * (st.getPoint().x - mid.x) + Math.acos(rotate) * (st.getPoint().y - mid.y) + mid.y);
		mid.set(x, y);
	}

	private float spacing(MotionEvent event)
	{
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event)
	{
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	private float rotation(MotionEvent event)
	{
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(radians);
	}

}