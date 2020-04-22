package com.bdqn.visitshop.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.bdqn.visitshop.utils.DownLoadUtils;
import com.bdqn.visitshop.utils.LogUtils;

/*它继承了一个IntentService 它可以帮助我们管理Service的生命周期 当任务执行完毕之后 它可以直接帮助我们关闭*/
public class UpdateService extends IntentService {

    public UpdateService() {
        super("UpdateService");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i("VISITSHOP", "下载服务开启");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("VISITSHOP","下载服务--onHandleIntent");
        String downloadUrl = intent.getStringExtra("url");/*拿到两个传来的两个数据*/
        String path = intent.getStringExtra("path");
        DownLoadUtils.DownLoad(downloadUrl, path, getApplicationContext().getApplicationContext());/*直接调用DownLoadUtils这个工具类*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i("VISITSHOP", "下载服务执行完毕");
    }
}
