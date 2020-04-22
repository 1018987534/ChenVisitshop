package com.bdqn.visitshop.utils;

import android.util.Log;

public class LogUtils {
    public static boolean ISLogCat;
    public static void i(String tag,String text){
        if(ISLogCat){
            Log.i(tag,text);
        }
    }

    public static boolean ISLogCat() {
        return ISLogCat;
    }

    public static void setISLogCat(boolean ISLogCat) {
        LogUtils.ISLogCat = ISLogCat;
    }
}
