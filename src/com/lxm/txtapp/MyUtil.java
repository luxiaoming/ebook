/**
 *  Author :  hmg25
 *  Description :
 */
package com.lxm.txtapp;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.SeekBar;

/**
 * hmg25's android Type
 * 
 * @author Administrator
 * 
 */
public class MyUtil {

	static void fenxiang(Context context) {

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain"); // "image/*"
		intent.putExtra(Intent.EXTRA_SUBJECT, "共享软件");
		intent.putExtra(Intent.EXTRA_TEXT, "我正在看 "
				+ context.getResources().getString(R.string.app_name)
				+ "，很好看，给你推荐下");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(Intent.createChooser(intent, "选择分享类型"));
	}

}
