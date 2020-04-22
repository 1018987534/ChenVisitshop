package com.bdqn.visitshop.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.adapters.TaskListAdapter;
import com.bdqn.visitshop.bean.Task;
import com.bdqn.visitshop.net.OkHttpManager;
import com.bdqn.visitshop.utils.Constant;
import com.bdqn.visitshop.utils.LogUtils;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.io.IOException;

import okhttp3.Request;

/**
 * 任务展示
 */
public class TaskActivity extends BaseActivity {

    private final String VISITSHOP = "VISITSHOP";
    private XRecyclerView mRecyclerView;
    private final int HANDLERCODEFIRST = 100;//Handler处理码，第一次访问
    private final int HANDLERCODEREFUSH = 101;//Handler处理码，刷新
    private final int HANDLERCODELOADMORE = 102;//Handler处理码，加载更多
    private final int HttpFail = 103;
    private int mCurrentPage = 1;//默认当前第一页
    private TaskListAdapter mTaskAdapter;
    private Task mTask;
    private RelativeLayout mRelLoading;
    private RelativeLayout mRelRefush;
    private Handler taskHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLERCODEFIRST:
                    LogUtils.i(VISITSHOP, "任务获取成功");
                    mTask = getDataFromJson(msg);//获取组装数据
                    mTaskAdapter = new TaskListAdapter(getApplicationContext(), mTask);//填充适配器
                    mRecyclerView.setAdapter(mTaskAdapter);
                    setShowLoading(false);//隐藏加载等待界面
                    setShowRefush(false);//隐藏刷新界面
                    break;
                case HANDLERCODEREFUSH:
                    LogUtils.i(VISITSHOP, "任务刷新成功");
                    mTask = getDataFromJson(msg);
                    mTaskAdapter.setmTask(mTask);
                    mTaskAdapter.notifyDataSetChanged();
                    mRecyclerView.refreshComplete();//停止刷新
                    break;
                case HANDLERCODELOADMORE:
                    LogUtils.i(VISITSHOP, "任务加载更多成功");
                    Task taskMore = getDataFromJson(msg);
                    if (taskMore.getBody()==null||taskMore.getBody().size() < 1) {
                        //如果已经是最后一页，则去掉之前的+1操作
                        mCurrentPage--;
                        Toast.makeText(getApplicationContext(), "没有更多数据", Toast.LENGTH_LONG).show();
                    } else {
                        //将新加载的数据加入到之前的数据集中
                        mTask.getBody().addAll(taskMore.getBody());
                    }
                    mTaskAdapter.setmTask(mTask);
                    mTaskAdapter.notifyDataSetChanged();
                    mRecyclerView.loadMoreComplete();//停止加载更多
                    break;
                case HttpFail:
                    Toast.makeText(getApplicationContext(), "访问服务器失败，请稍后再试", Toast.LENGTH_LONG).show();
                    setShowLoading(false);//隐藏加载中界面
                    setShowRefush(true);//显示刷新界面
                    mRecyclerView.loadMoreComplete();
                    mRecyclerView.refreshComplete();
                    break;
            }
            return false;
        }
    });

    /**
     * 返回数据组装
     *
     * @param msg
     * @return
     */
    private Task getDataFromJson(Message msg) {
        Gson gson = new Gson();
        Task task = gson.fromJson(msg.obj.toString(), Task.class);
        if(task==null){
            task = new Task();
        }
        return task;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        setTitleName("任务");
        initView();
        initListener();
        getDataFromServer(mCurrentPage, HANDLERCODEFIRST);
    }

    /**
     * 初始化控件监听器
     */
    private void initListener() {
        //设置加载风格
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        //设置展示风格
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setLoadingMoreEnabled(true);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                //刷新操作，将当前页码置为1
                mCurrentPage = 1;
                getDataFromServer(mCurrentPage, HANDLERCODEREFUSH);
            }
            @Override
            public void onLoadMore() {
                //加载更多，当前页码加1
                mCurrentPage++;
                getDataFromServer(mCurrentPage, HANDLERCODELOADMORE);
            }
        });

        /**
         * 第一次加载失败，没有缓存显示，则显示再次刷新界面
         */
        mRelRefush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShowLoading(true);
                setShowRefush(false);
                getDataFromServer(mCurrentPage, HANDLERCODEFIRST);
            }
        });
    }

    /**
     * 从服务端获取数据
     *
     * @param currentPage 当前页码
     * @param handlerCode handler处理码
     */
    private void getDataFromServer(int currentPage, final int handlerCode) {
        setShowLoading(true);
        //组装请求参数
        OkHttpManager.getInstance().getNet(Constant.Task+"?pagenum="+currentPage, new OkHttpManager.ResultCallback() {
            private int code = handlerCode;
            @Override
            public void onFailed(Request request, IOException e) {
                taskHandler.sendEmptyMessage(HttpFail);
            }
            @Override
            public void onSuccess(String response) {
                Message message = new Message();
                message.what = code;
                message.obj = response;
                taskHandler.sendMessage(message);
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mRecyclerView = (XRecyclerView) findViewById(R.id.lv_task);
        mRelLoading = (RelativeLayout) findViewById(R.id.loading);
        mRelRefush = (RelativeLayout) findViewById(R.id.refush);
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
     * 是否显示刷新界面
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
}
