package com.lxm.txtapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridViewAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private int[] mPictures;
	private String[] mTitles;
	private int layout_id = 0;

	public GridViewAdapter(Context context, int[] pictures, String[] titles,
			int layoutid) {
		this.mContext = context;
		this.mPictures = pictures;
		this.mTitles = titles;
		mInflater = LayoutInflater.from(context);
		layout_id =layoutid;// R.layout.grid;
	}

	@Override
	public int getCount() {

		return mPictures.length;
	}

	@Override
	public Object getItem(int position) {

		return null;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		int picture = mPictures[position];
		String title = mTitles[position];

		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(layout_id, null);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.grid_img);
			viewHolder.textView = (TextView) convertView
					.findViewById(R.id.grid_tv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.imageView.setBackgroundResource(picture);
		viewHolder.textView.setText(title);
		return convertView;
	}

	private final class ViewHolder {
		public ImageView imageView;
		public TextView textView;
	}
}