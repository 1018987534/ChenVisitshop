package com.bdqn.visitshop.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bdqn.visitshop.MainActivity;
import com.bdqn.visitshop.R;

import java.io.File;

/**
 * 资讯详情activity
 */
public class InfoDetailActivity extends BaseActivity {

    private WebView mWebView;
    private String mDetailUrl;
    private RelativeLayout mRelLoading;
    private RelativeLayout mRelRefush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_detail);
        setTitleName("资讯详情");
        //获取资讯详情地址URL
        mDetailUrl = getIntent().getStringExtra("url");
        mWebView = (WebView) findViewById(R.id.webview);
        mRelLoading = (RelativeLayout) findViewById(R.id.loading);
        mRelRefush = (RelativeLayout) findViewById(R.id.refush);
        //设置webview相关设置
        setWebView();
        if (!TextUtils.isEmpty(mDetailUrl)) {
            //显示加载中界面
            setShowLoading(true);
            //加载网页
            mWebView.loadUrl(mDetailUrl);
        }
        mRelRefush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShowLoading(true);
                setShowRefush(false);
                //重新加载网页
                mWebView.loadUrl(mDetailUrl);
            }
        });
    }



    /**
     * webview相关参数设置
     */
    private void setWebView() {
        //优先使用缓存:
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //支持js脚本
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //加载完成，关闭加载中界面
                setShowLoading(false);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                //加载失败，显示刷新界面
                setShowLoading(false);
                setShowRefush(true);
            }
        });
    }

    /**
     * 处理返回按键，浏览器回退，不是直接关闭
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 是否显示加载界面
     *
     * @param flag
     */
    public void setShowLoading(boolean flag) {
        if (flag) {
            mRelLoading.setVisibility(View.VISIBLE);
        } else {
            mRelLoading.setVisibility(View.GONE);
        }
    }

    /**
     * 是否显示联网失败 刷新界面
     *
     * @param flag
     */
    public void setShowRefush(boolean flag) {
        if (flag) {
            mRelRefush.setVisibility(View.VISIBLE);
        } else {
            mRelRefush.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清除缓存
        mWebView.clearCache(true);
        //清除历史记录
        mWebView.clearHistory();
    }
}
