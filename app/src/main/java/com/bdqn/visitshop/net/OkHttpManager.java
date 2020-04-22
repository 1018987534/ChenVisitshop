package com.bdqn.visitshop.net;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 网络请求管理
 * 对OkHttp请求进行封装
 */
public class OkHttpManager {

    private static OkHttpManager instance;
    private OkHttpClient mOkHttpClient;
    private Handler okHandler;

    public OkHttpManager() {
        okHandler = new Handler(Looper.getMainLooper());
        int cacheSize = 10 * 1024 * 1024;/*缓存大小*/
        OkHttpClient.Builder builder = new OkHttpClient.Builder()/*定义OkHttpClient对象*/
                .connectTimeout(15, TimeUnit.SECONDS)/*指定连接超时时间*/
                .writeTimeout(20, TimeUnit.SECONDS)/*指定写的超时时间*/
                .readTimeout(20, TimeUnit.SECONDS);/*指定读的超时时间*/
        mOkHttpClient = builder.build();/*通过build方法构造了一个全局的mOkHttpClient对象*/
    }

    public static OkHttpManager getInstance() {
        if (instance == null) {
            synchronized (OkHttpManager.class) {
                if (instance == null) {
                    instance = new OkHttpManager();
                }
            }
        }
        return instance;
    }

    public void getNet(String url, ResultCallback resultCallback) {
        Log.i("okhttp","url--"+url);
        Request request = new Request.Builder()
                .url(url)
                .method("GET",null)//此设置默认为get,可以不设置
                .build();
        dealNet(request, resultCallback);
    }

    public void postNet(String url, ResultCallback resultCallback, Param... param) {
        if (param == null) {
            param = new Param[0];
        }
        FormBody.Builder formBody = new FormBody.Builder();/*通过FormBody来构建表单*/
        for (Param p : param) {/*将数组数据传给表单*/
            formBody.add(p.key, p.value);
        }
        RequestBody requestBody = formBody.build();/*通过.bulid()方法拿到requestBody对象*/
        Request request = new Request.Builder()/*将url地址和requestBody封装成一个request*/
                .url(url)
                .post(requestBody)
                .build();
        dealNet(request, resultCallback);
    }

    public void postNetJson(String url, ResultCallback resultCallback,String parms) {
         MediaType JSON = MediaType.parse("application/json;charset=utf-8");
         RequestBody body = RequestBody.create(JSON,parms);

        Request request = new Request.Builder()/*将url地址和requestBody封装成一个request*/
                .url(url)
                .post(body)
                .build();
        dealNet(request, resultCallback);
    }

/*有文件的时候用这个*/
    public void upFileNet(String url, ResultCallback resultCallback, File[] files,
                          String fileKeys, Param... param) {/*Param... param字符串数量不一定 所以用了一个不定参数*/
        Request request = buildMultipartFormRequest(url, files, fileKeys, param);/*url地址 files数组 file数组对应的字符串 param */

        dealNet(request, resultCallback);
    }

    private void dealNet(final Request request, final ResultCallback resultCallback) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {/*通过传给它的request 拿到request后就发送异步请求 然后进行联网*/
            @Override
            public void onFailure(Call call, final IOException e) {
                okHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        resultCallback.onFailed(request, e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = "";
                try {
                    str = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final String finalStr = str;
                okHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        resultCallback.onSuccess(finalStr);

                    }
                });
            }
        });
    }

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    private Request buildMultipartFormRequest(String url, File[] files,
                                              String fileKeys, Param[] params) {
        params = validateParam(params);/*判断对数组是否为空*/

        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();/*通过构建一个okhttp里面提供MultipartBody对象*/
        multipartBodyBuilder.setType(MultipartBody.FORM);/*这里给它指定一个类型是FORM表单 这个FORM类型就是form-date*/

        for (Param param : params) {/*刚刚的params 写一个循环*/
            multipartBodyBuilder.addFormDataPart(param.key, param.value);/*addFormDataPart把这个参数添加进去*/
        }
        if (files != null) {/*然后上传文件 先判断不为空*/
            for (int i = 0; i < files.length; i++) {
                File file = files[i];/*拿到每一个文件具体的对象*/
                multipartBodyBuilder.addFormDataPart(fileKeys, file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));/*addFormDataPart调取 参数fileKeys 是之前传的key值  然后是文件名 文件名可以通过.getName()来获取 然后通过RequestBody.create来指明它是.png图片 最后file就是文件对象*/
            }
        }

        RequestBody requestBody = multipartBodyBuilder.build();/*通过它的.build()方法 拿到RequestBody对象*/
        return new Request.Builder()/*这里开始跟get是一样的*/
                .url(url)/*具体的地址*/
                .post(requestBody)/*post传的时候的请求体对象*/
                .build();
    }

    private Param[] validateParam(Param[] params) {
        if (params == null)/*对传进来的数组做一个判断*/
            return new Param[0];/*如果等于空 就给它创建一个新的对象*/
        else return params;
    }

    public static abstract class ResultCallback {
        public abstract void onFailed(Request request, IOException e);

        public abstract void onSuccess(String response);
    }

    public static class Param {
        String key;
        String value;

        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
