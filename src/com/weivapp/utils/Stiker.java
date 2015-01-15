package com.weivapp.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;

public class Stiker
{
	private Bitmap	stiker;
	PointF point=new PointF();
	RectF map=new RectF();
	RectF tmap=new RectF();
	public RectF getTmap()
	{
		return tmap;
	}
	public void setTmap(RectF tmap)
	{
		this.tmap = tmap;
	}
	public RectF getMap()
	{
		return map;
	}
	public void setMap(RectF map)
	{
		this.map = map;
	}
	PointF scale=new PointF();
	public PointF getScale()
	{
		return scale;
	}
	public void setScale(PointF scale)
	{
		this.scale = scale;
	}
	public PointF getPoint()
	{
		return point;
	}
	public void setPoint(PointF point)
	{
		this.point = point;
	}
	public Bitmap getStiker()
	{
		return stiker;
	}
	public void setStiker(Bitmap stiker)
	{
		this.stiker = stiker;
	}
	public Matrix getMatrix()
	{
		return matrix;
	}
	public void setMatrix(Matrix matrix)
	{
		this.matrix = matrix;
	}
	public boolean isSelected()
	{
		return isSelected;
	}
	public void setSelected(boolean isSelected)
	{
		this.isSelected = isSelected;
	}
	private Matrix matrix;
	boolean isSelected;
}
