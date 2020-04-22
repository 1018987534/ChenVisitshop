package com.bdqn.visitshop.activity;

import android.os.Bundle;

import com.bdqn.visitshop.R;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitleName("关于");
    }
}
