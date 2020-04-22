package com.bdqn.visitshop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class BaseFragmentActivity extends FragmentActivity {

	protected Context mContext;
	protected Activity mActivity;
	private TextView title_bar_back,title_bar_name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mActivity = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏标题栏
	}

	@Override
	public void finish() {
		super.finish();
//		overridePendingTransition(0, R.anim.activity_out);
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
//		overridePendingTransition(R.anim.activity_in, 0);
	}

	@SuppressLint("NewApi")
	@Override
	public void setContentView(int layoutResID) { /*比对当前版本*/
		super.setContentView(layoutResID);
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
			// WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}

		if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
			View v = findViewById(R.id.root_layout);
			if (v != null) {
				v.setFitsSystemWindows(true);
			}
		}
		
		if(VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) { 
            Window window = getWindow();  
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN  
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);  
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);  
            window.setStatusBarColor(Color.TRANSPARENT);/*状态栏透明*/
        }  
		title_bar_back = (TextView) findViewById(R.id.title_bar_back);
		title_bar_name = (TextView) findViewById(R.id.title_bar_name);
		if(title_bar_back != null){
			title_bar_back.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();/*返回*/
				}
			});
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public void setTitleName(String str){/*设置标题栏名字*/
		if(title_bar_name != null){
			title_bar_name.setText(str);
		}
	}
}
