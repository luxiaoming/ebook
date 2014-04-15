package com.lxm.txtapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import cn.waps.AppConnect;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		GridView gridView = (GridView) findViewById(R.id.popup_grid);
		GridViewAdapter adapter = new GridViewAdapter(this, mPictures, mTitles,
				R.layout.grid2);
		gridView.setAdapter(adapter);
		// 互动广告调用方式
		LinearLayout layout = (LinearLayout) this
				.findViewById(R.id.AdLinearLayout);
		AppConnect.getInstance(this).showBannerAd(this, layout);
		gridView.setOnItemClickListener(new ItemClickListener());
		gridView.setOnItemSelectedListener(new OnItemSelectedListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.widget.AdapterView.OnItemSelectedListener#onItemSelected
			 * (android.widget.AdapterView, android.view.View, int, long)
			 */
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.widget.AdapterView.OnItemSelectedListener#onNothingSelected
			 * (android.widget.AdapterView)
			 */
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private int[] mPictures = { R.drawable.cover_txt, R.drawable.cover_txt,
			R.drawable.cover_txt, R.drawable.cover_txt, R.drawable.cover_txt,
			R.drawable.cover_txt, };
	private String[] mTitles = { "Bookmarks", "Font Size", "Brightness",
			"Read Style", "Recreation", "About" };
	public static String[] bookslist = { "data.txt", "data1.txt", "data2.txt",
			"data3.txt", "data4.txt", "data5.txt" };
	public static int[] bookslistid = { R.raw.data, R.raw.data1, R.raw.data2,
			R.raw.data3, R.raw.data4, R.raw.data5 };

	private final class ItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (position < bookslist.length) {
				Intent i = new Intent();
				i.setClassName("com.lxm.txtapp", "com.lxm.txtapp.turntest");
				i.putExtra("bookname", position);
				startActivity(i);

			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
