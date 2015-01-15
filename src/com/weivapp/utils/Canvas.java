package com.ar.photobooth;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

@SuppressLint("DrawAllocation")
public class Canvas extends View {

	Paint paint;
	Paint paint1;
	boolean flag = false;

	// new code attributes
	float x_down = 0;
	float y_down = 0;
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	float oldRotation = 0;
	Matrix matrix = new Matrix();
	Matrix matrix1 = new Matrix();
	Matrix savedMatrix = new Matrix();

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	int mode = NONE;
	RectF rect2,rect3;
	boolean matrixCheck = false;

	Bitmap background,foreground;
	static Bitmap Capfground,Goggfground,MHeirfground,Lipsfground,Beardfground,WHeirfground;
	static Matrix cmatrix,gmatrix,mhmatrix,lmatrix,bmatrix,whmatrix;
	static int widthScreen;
	static int heightScreen;
	static Bitmap gintam;
	Context context;

	// end
	public Canvas(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	public Canvas(Context context) {
		super(context);
		this.context = context;
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
		.getMetrics(dm);        
		widthScreen = dm.widthPixels;
		heightScreen = dm.heightPixels;
		rect2=new RectF();
		rect3=new RectF();
		rect2.set(0,0,widthScreen,heightScreen-100);
		matrix = new Matrix();

	}

	public static int loop = 1;

	public Bitmap getViewBackground() {
		return background;
	}

	public void setBackground(Bitmap background) {
		this.background = background;
		gintam = background;
		flag = false;
		loop = 1;

	}

	public Bitmap getForeground() {

		return foreground;

	}

	public void setForeground(Bitmap foreground) {
		this.foreground = foreground;
		// gintam = foreground;
	//	matrix.set(null);
		flag = true;
		loop = 0;
	}

	float t1, t2, t3, t4, w = 100, h = 100;
	float k1, k2;

	float x = 160, y = 240;
	int pointer_first;
	int pointer_id_first;
	int pointer_second;
	int pointer_id_second;

	boolean isTransform;
	boolean isTranslate = true;

	public Canvas(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	float tx, ty, sx, sy;
	Rect rect = new Rect();
	float transx, transy, width, height, scaleX, scaleY, skewX, skewY;
	Matrix myNeo;

	// Matrix newForeGround;

	@Override
	protected void onDraw(android.graphics.Canvas canvas) {
		// TODO Auto-generated method stub
		deleteItemsforchnage();
		selectedItemforchange();
		saveMatrixOfItemas();
        
		try {
			if (flag == false) {
				myNeo = new Matrix(matrix);

				// if (EditorActivity.bagFlag == false) {
				if(gintam!=null)
				{
					if(EditorActivity.isFirstImage)
		
						setFirstImageMatrix();

				}
				canvas.drawBitmap(gintam, matrix, null);

			} else if (flag == true) {

				int i = 1;
				canvas.drawBitmap(gintam, myNeo, null);
				canvas.drawBitmap(foreground, matrix, null);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		if(Goggfground!=null )
		{
			canvas.drawBitmap(Goggfground, gmatrix, null);
		}
		if(MHeirfground!=null)
		{
			canvas.drawBitmap(MHeirfground, mhmatrix, null);
		}
		if(Capfground!=null)
		{
			canvas.drawBitmap(Capfground, cmatrix, null);
		}
		if(Lipsfground!=null)
		{
			canvas.drawBitmap(Lipsfground, lmatrix, null);
		}
		if(Beardfground!=null )
		{
			canvas.drawBitmap(Beardfground, bmatrix, null);
		}
		if(WHeirfground!=null)
		{
			canvas.drawBitmap(WHeirfground, whmatrix, null);
		}
	}
	private void deleteItemsforchnage() {
		if(EditorActivity.capdelete)
		{
			Capfground=null;
			cmatrix=null;
			foreground=EditorActivity.hideback;
			EditorActivity.capdelete=false;
			EditorActivity.Iscapselected=false;

		}
		else if (EditorActivity.googgledelete) {
			Goggfground=null;
			gmatrix=null;
			foreground=EditorActivity.hideback;
			EditorActivity.googgledelete=false;
			EditorActivity.isGoggleSelected=false;
		}
		else if (EditorActivity.bearddelete) {
			Beardfground=null;
			bmatrix=null;
			foreground=EditorActivity.hideback;
			EditorActivity.bearddelete=false;
			EditorActivity.isBeardSelected=false;
		}
		else if (EditorActivity.lipsdelete) {
			Lipsfground=null;
			lmatrix=null;
			foreground=EditorActivity.hideback;
			EditorActivity.lipsdelete=false;
			EditorActivity.isLipsSelected=false;
		}
		else if (EditorActivity.manheirdelete) {
			MHeirfground=null;
			mhmatrix=null;
			foreground=EditorActivity.hideback;
			EditorActivity.manheirdelete=false;
			EditorActivity.isManHeirSelected=false;
		}
		else if (EditorActivity.womanheirdelete) {
			WHeirfground=null;
			whmatrix=null;
			foreground=EditorActivity.hideback;
			EditorActivity.womanheirdelete=false;
			EditorActivity.isWomanHeirSElected=false;
		}

		// TODO Auto-generated method stub

	}

	private void selectedItemforchange() {
		// TODO Auto-generated method stub
		if(EditorActivity.IscapDeleted)
		{
			if(Capfground!=null)
			{
				matrix=cmatrix;
				foreground=Capfground;
				EditorActivity.IscapDeleted=false;
				Capfground=null;
				EditorActivity.Iscapselected=true;
			}
			else
				foreground=EditorActivity.hideback;
		}
		else if(EditorActivity.isBeardDeleted)
		{
			if(Beardfground!=null)
			{
				matrix=bmatrix;
				foreground=Beardfground;
				EditorActivity.isBeardDeleted=false;
				Beardfground=null;
				EditorActivity.isBeardSelected=true;
			}else
				foreground=EditorActivity.hideback;

		}
		else if(EditorActivity.isGoggleDeleted)
		{
			if(Goggfground!=null)
			{
				matrix=gmatrix;
				foreground=Goggfground;
				EditorActivity.isGoggleDeleted=false;
				Goggfground=null;
				EditorActivity.isGoggleSelected=true;
			}else
				foreground=EditorActivity.hideback;

		}
		else if(EditorActivity.isLipsDeleted)
		{
			if(Lipsfground!=null)
			{
				matrix=lmatrix;
				foreground=Lipsfground;
				EditorActivity.isLipsDeleted=false;
				Lipsfground=null;
				EditorActivity.isLipsSelected=true;
			}else
				foreground=EditorActivity.hideback;

		}
		else if(EditorActivity.isManHeirDeleted)
		{
			if(MHeirfground!=null)
			{
				matrix=mhmatrix;
				foreground=MHeirfground;
				EditorActivity.isManHeirDeleted=false;
				MHeirfground=null;
				EditorActivity.isManHeirSelected=true;
			}else
				foreground=EditorActivity.hideback;

		}
		else if(EditorActivity.isWomanHeirDeleted)
		{
			if(WHeirfground!=null)
			{
				matrix=whmatrix;
				foreground=WHeirfground;
				EditorActivity.isWomanHeirDeleted=false;
				WHeirfground=null;
				EditorActivity.isWomanHeirSElected=true;
			}else
				foreground=EditorActivity.hideback;

		}
	}

	private void saveMatrixOfItemas() {
		// TODO Auto-generated method stub

		if(EditorActivity.Iscapselected)
		{
			cmatrix=new Matrix(matrix);
			Capfground=foreground;
		}
		else if(EditorActivity.isBeardSelected)
		{
			bmatrix=new Matrix(matrix);;
			Beardfground=foreground;
		}
		else if(EditorActivity.isGoggleSelected)
		{
			gmatrix=new Matrix(matrix);
			Goggfground=foreground;
		}
		else if(EditorActivity.isLipsSelected)
		{
			lmatrix=new Matrix(matrix);
			Lipsfground=foreground;
		}
		else if(EditorActivity.isManHeirSelected)
		{
			mhmatrix=new Matrix(matrix);
			MHeirfground=foreground;
		}
		else if(EditorActivity.isWomanHeirSElected)
		{
			whmatrix=new Matrix(matrix);
			WHeirfground=foreground;
		}

	}

	private void setFirstImageMatrix() {
		// TODO Auto-generated method stub
		Bitmap resizedBitmap = Bitmap.createScaledBitmap(gintam, widthScreen,heightScreen-100, true);
		gintam=resizedBitmap;
		rect3.set(0,0,gintam.getWidth(),gintam.getHeight());
		matrix.setRectToRect(rect3, rect2, Matrix.ScaleToFit.CENTER);
		//matrix.set(savedMatrix);
	}
	//rememberise
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction() & MotionEvent.ACTION_MASK) 
		{
		case MotionEvent.ACTION_DOWN:
			mode = DRAG;
			x_down = event.getX();
			y_down = event.getY();
			savedMatrix.set(matrix);
			EditorActivity.isimagesaved=false;
			EditorActivity.isFirstImage=false;
			EditorActivity.updownscroll1.setVisibility(INVISIBLE);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			mode = ZOOM;
			oldDist = spacing(event);
			oldRotation = rotation(event);
			savedMatrix.set(matrix);
			midPoint(mid, event);
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == ZOOM) {
				matrix1.set(savedMatrix);
				float rotation = rotation(event) - oldRotation;
				float newDist = spacing(event);
				float scale = newDist / oldDist;
				matrix1.postScale(scale, scale, mid.x, mid.y);//
				matrix1.postRotate(rotation, mid.x, mid.y);//
				matrixCheck = matrixCheck();
				if (matrixCheck == false) {
					matrix.set(matrix1);
					invalidate();
				}
			} else if (mode == DRAG) {
				matrix1.set(savedMatrix);
				matrix1.postTranslate(event.getX() - x_down, event.getY()
						- y_down);//
				matrixCheck = matrixCheck();
				matrixCheck = matrixCheck();
				if (matrixCheck == false) {
					matrix.set(matrix1);
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:

			mode = NONE;
			break;
		}
		return true;
	}

	private boolean matrixCheck() {
		float[] f = new float[9];
		matrix1.getValues(f);
		// 4
		float x1 = f[0] * 0 + f[1] * 0 + f[2];
		float y1 = f[3] * 0 + f[4] * 0 + f[5];
		float x2 = f[0] * gintam.getWidth() + f[1] * 0 + f[2];
		float y2 = f[3] * gintam.getWidth() + f[4] * 0 + f[5];
		float x3 = f[0] * 0 + f[1] * gintam.getHeight() + f[2];
		float y3 = f[3] * 0 + f[4] * gintam.getHeight() + f[5];
		float x4 = f[0] * gintam.getWidth() + f[1] * gintam.getHeight() + f[2];
		float y4 = f[3] * gintam.getWidth() + f[4] * gintam.getHeight() + f[5];
		//
		double width = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
		//
		if (width < widthScreen / 8 || width > widthScreen * 4) {
			return true;
		}
		//
		if ((x1 < widthScreen / 6 && x2 < widthScreen / 6
				&& x3 < widthScreen / 6 && x4 < widthScreen / 6)
				|| (x1 > widthScreen * 2 / 3 && x2 > widthScreen * 2 / 3
						&& x3 > widthScreen * 2 / 3 && x4 > widthScreen * 2 / 3)
						|| (y1 < heightScreen / 3 && y2 < heightScreen / 3
								&& y3 < heightScreen / 6 && y4 < heightScreen / 6)
								|| (y1 > heightScreen * 4 / 6 && y2 > heightScreen * 4 / 6
										&& y3 > heightScreen * 2 / 3 && y4 > heightScreen * 2 / 3)) {
			return true;
		}
		return false;
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	//
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	//
	private float rotation(MotionEvent event) {
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(radians);
	}

}
