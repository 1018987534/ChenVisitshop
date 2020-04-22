package com.bdqn.visitshop.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.bdqn.visitshop.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownLoadUtils {
    static NotificationManager mNotifyManager;
    static NotificationCompat.Builder mBuilder;
/*这里就是具体下载的实现 动态的更新通知栏*/
    public static void DownLoad(String url, final String path, final Context context) {
        //获取通知栏管理器
        mNotifyManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        //通知栏参数设置，标题，正文，图标等
        mBuilder.setContentTitle("版本更新")
                .setContentText("正在下载...")
                .setContentInfo("0%")
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.xiazai);/*通知栏左上角小图标显示*/
        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient mOkHttpClient = new OkHttpClient();/*直接声明一个mOkHttpClient对象*/
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback()/*调取一个异步方法 去发送请求*/
        {
            @Override
            public void onFailure(Call call, final IOException e){}
            @Override
            public void onResponse(Call call,Response response)/*获取成功之后拿到response对象 解析出来 然后保存成文件*/
            {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                int current = len;
                long total = 0;
                try
                {
                    is = response.body().byteStream();/*具体的流数据*/
                    total = response.body().contentLength();/*拿到全部数据内容的长度*/
                    File file = new File(path);/*拿到下载的apk包的本地存储地址*/
                    fos = new FileOutputStream(file);/*把收到的流数据写成file类型的文件*/
                    while ((len = is.read(buf)) != -1)
                    {
                        current += len;/*当前得到的数据*/
                        fos.write(buf, 0, len);
                        //计算下载进度，将当前下载的数据大小和总大小计算，得到进度百分比，并在通知栏更新显示
                        BigDecimal b = new BigDecimal((float) current / (float) total);/*当前比上所有的数据 得到一个分数*/
                        float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                        mBuilder.setProgress(100, (int) (f1 * 100), false);/*把数据拼接成一个百分比的形式*/
                        mBuilder.setContentInfo((int) (f1 * 100) + "%");
                        mNotifyManager.notify(1, mBuilder.build());/*最后调取notify 就可以不停的发送这个通知 显示下载的百分比*/
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    installApp(context,path);/*安装APP*/
                } catch (IOException e)
                {} finally
                {
                    try{
                        if (is != null) is.close();
                    } catch (IOException e){}
                    try{
                        if (fos != null) fos.close();
                    } catch (IOException e){}
                }

            }
        });
    }

    /**
     * 安装指定APK文件
     *
     * @param context
     * @param filePath
     */
    public static void installApp(Context context, String filePath) {
        File _file = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);/*指定action*/
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);/*指定flag*/
        intent.setDataAndType(Uri.fromFile(_file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);/*调取intent 就可以显示安装的界面了*/
    }
}
