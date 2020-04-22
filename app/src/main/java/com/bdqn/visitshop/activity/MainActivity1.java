package com.bdqn.visitshop.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.bean.FeedBackResult;
import com.bdqn.visitshop.bean.SubmitResult;
import com.bdqn.visitshop.bean.User;
import com.bdqn.visitshop.net.OkHttpManager;
import com.bdqn.visitshop.utils.Constant;
import com.bdqn.visitshop.utils.LogUtils;
import com.google.gson.Gson;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Request;

public class MainActivity1 extends BaseActivity  {

    private ListView msgListView;
    private EditText inputText;
    private SubmitResult mSubmitResult;
    private FeedBackResult feedBackResult;
    private User mUser;
    private Integer curItem;
    private View mView;
    private int scrolledX =0;
    private int scrolledY;
    private int mListFocus ;
    private int firstVisiblePositionTop ;
    private Button send;
    private MsgAdapter adapter;
    private RelativeLayout mRelLoading;
    private List<FeedBackResult.BodyBean> msgList = new ArrayList<FeedBackResult.BodyBean>();
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    update();
                    break;
            }
            super.handleMessage(msg);
        }
        void update() {
            initMsgs();
        }
    };
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        setTitleName("意见反馈");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mRelLoading = (RelativeLayout) findViewById(R.id.loading);
        msgListView = (ListView)findViewById(R.id.msg_list_view);
        //获取数据库用户
        List<User> list = DataSupport.findAll(User.class);
        if (null != list && list.size() > 0) {
            mUser = list.get(0);
        }
        OkHttpManager.getInstance().getNet(Constant.HistoryFeedBack+ "?userid="+mUser.getUserId() , new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                mRelLoading.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "服务连接异常,稍后再试", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String response) {
                feedBackResult = getDataFromJson(response);
                if (feedBackResult.getCode() == 0) {
                    if (feedBackResult.getBody()!=null) {
                        msgList = feedBackResult.getBody();
                        adapter = new MsgAdapter(MainActivity1.this, R.layout.msg_item, msgList);
                        msgListView.setAdapter(adapter);
                    }else{
                        FeedBackResult.BodyBean bodyBean1 = new FeedBackResult.BodyBean();
                        bodyBean1.setFeedbackType(1);
                        bodyBean1.setReplyContent("欢迎访问客服!");
                        msgList.add(bodyBean1);
                        adapter = new MsgAdapter(MainActivity1.this, R.layout.msg_item, msgList);
                        msgListView.setAdapter(adapter);
                    }
                    msgListView.setSelection(msgList.size());
                } else {
                    Toast.makeText(getApplicationContext(), "提交异常，请稍后再试", Toast.LENGTH_LONG).show();
                }
            }
        });
        //设置滑动监听
        msgListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                scrolledX++;
                mListFocus = msgListView.getFirstVisiblePosition();
                View item = msgListView.getChildAt(0);
                firstVisiblePositionTop = (item == null) ? 0 : item.getTop();
            }
        });

        timer.schedule(task, 1000 * 4, 1000 * 3); //启动timer
        inputText = (EditText)findViewById(R.id.input_text);
            send = (Button)findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = inputText.getText().toString();
                if(!"".equals(content)) {
                    //获取数据库用户
                    List<User> list = DataSupport.findAll(User.class);
                    if (null != list && list.size() > 0) {
                        mUser = list.get(0);
                    }
                    //组装提交参数
                    OkHttpManager.Param[] params;
                    if (null == mUser) {
                        params = new OkHttpManager.Param[]{
                                new OkHttpManager.Param("userid", ""),
                                new OkHttpManager.Param("feedback", content)};
                    } else {
                        params = new OkHttpManager.Param[]{
                                new OkHttpManager.Param("userid", mUser.getUserId()),
                                new OkHttpManager.Param("feedback", content)};
                    }
                    mRelLoading.setVisibility(View.VISIBLE);/*显示加载图标*/
                    OkHttpManager.getInstance().postNet(Constant.FeedBack, new OkHttpManager.ResultCallback() {
                        @Override
                        public void onFailed(Request request, IOException e) {
                            mRelLoading.setVisibility(View.GONE);
                            send.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "服务连接异常,稍后再试", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onSuccess(String response) {
                            feedBackResult = getDataFromJson(response);/*拿到服务端提交返回数据*/
                            send.setEnabled(true);/*按钮可以点击*/
                            mRelLoading.setVisibility(View.GONE);/*干掉加载图标*/
                            if (feedBackResult.getCode() == 0) {
                                msgList.add(feedBackResult.getBody().get(0));
                                adapter.notifyDataSetChanged();
                                msgListView.setSelection(msgList.size());
                                mListFocus=msgList.size();


                                inputText.setText("");
                            } else {
                                Toast.makeText(getApplicationContext(), "提交异常，请稍后再试", Toast.LENGTH_LONG).show();
                            }
                        }
                    },params);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {// 停止timer
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }
    private void initMsgs() {

        //获取数据库用户
        List<User> list = DataSupport.findAll(User.class);
        if (null != list && list.size() > 0) {
            mUser = list.get(0);
        }
        OkHttpManager.getInstance().getNet(Constant.HistoryFeedBack+ "?userid="+mUser.getUserId() , new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                mRelLoading.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "服务连接异常,稍后再试", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String response) {
                Integer kk = 0;
                feedBackResult = getDataFromJson(response);
                if (feedBackResult.getCode() == 0) {
                    if (feedBackResult.getBody()!=null) {
                        if (msgList.size()!=feedBackResult.getBody().size()){
                            //当接收到最新消息时 将列表滑倒最底部
                             kk = 1;
                        }
                        msgList = feedBackResult.getBody();
                        adapter = new MsgAdapter(MainActivity1.this, R.layout.msg_item, msgList);
                        msgListView.setAdapter(adapter);
                        //首次默认滑动到最底部
                        msgListView.setSelection(msgList.size());
                        if (scrolledX!=0) {
                            //如果滑动过 就走这条
                            msgListView.setSelectionFromTop(mListFocus, firstVisiblePositionTop);
                        }
                        if (kk==1){
                            //当接收到最新消息时 将列表滑倒最底部
                            msgListView.setSelection(msgList.size());
                        }
                    }else{
                        FeedBackResult.BodyBean bodyBean1 = new FeedBackResult.BodyBean();
                        bodyBean1.setFeedbackType(1);
                        bodyBean1.setReplyContent("欢迎访问客服!");
                        msgList.add(bodyBean1);
                        adapter = new MsgAdapter(MainActivity1.this, R.layout.msg_item, msgList);
                        msgListView.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "提交异常，请稍后再试", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    /**
     * 返回数据组装
     *
     * @param msg
     * @return
     */
    private FeedBackResult getDataFromJson(String msg) {
        Gson gson = new Gson();
        FeedBackResult feedBackResult = gson.fromJson(msg, FeedBackResult.class);
        return feedBackResult;
    }
}
