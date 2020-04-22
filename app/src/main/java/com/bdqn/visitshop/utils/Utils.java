package com.bdqn.visitshop.utils;

import android.view.View;

import java.util.Calendar;

public abstract class Utils implements View.OnClickListener{
    public static final int MIN_CLICK_TIME=1000;
    private long lastTime = 0;

    @Override
    public void onClick(View view){
        long cTime = Calendar.getInstance().getTimeInMillis();
        if (cTime - lastTime >MIN_CLICK_TIME){
            lastTime = cTime;
            forbidClick(view);
        }
    }

    public abstract void forbidClick(View view);
}
