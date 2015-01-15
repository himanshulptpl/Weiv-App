package com.weivapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GaleryListAdapter extends BaseAdapter
{

	int[]	interfaces;
	String	className;
	Context	context;

	public GaleryListAdapter(int[] interfaces, String className, Context context)
	{
		// TODO Auto-generated constructor stub
		this.interfaces = interfaces;
		this.className = className;
		this.context = context;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return interfaces.length;
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return interfaces[position];

	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{

		ImageView imageView = null;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageView = (ImageView) inflater.inflate(R.layout.cell, null);
		try
		{
			imageView.setImageDrawable(context.getResources().getDrawable(interfaces[position]));
		}
		catch (Exception e)
		{
			System.out.println("");
		}
		imageView.setTag(interfaces[position]);

		return imageView;
	}

}
