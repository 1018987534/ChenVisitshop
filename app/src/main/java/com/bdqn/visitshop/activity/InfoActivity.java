package com.bdqn.visitshop.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.adapters.InfoListAdapter;
import com.bdqn.visitshop.bean.InfoResult;
import com.bdqn.visitshop.bean.InfoResultBody;
import com.bdqn.visitshop.net.OkHttpManager;
import com.bdqn.visitshop.utils.Constant;
import com.bdqn.visitshop.utils.LogUtils;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

/**
 * 资讯activity
 */
public class InfoActivity extends BaseActivity {
    private final String VISITSHOP = "InfoActivity";
    private XRecyclerView mRecyclerView;
    private final int HANDLERCODEFIRST = 100;
    private final int HANDLERCODEREFUSH = 101;
    private final int HANDLERCODELOADMORE = 102;
    private final int HttpFail = 103;
    private final int TYPECOMPANY = 0;//公司动态
    private final int TYPEINDUSTY = 1;//行业资讯
    private int mCurrentPage = 1;//默认当前第一页
    private int type = 0;
    private InfoResult mInfo = new InfoResult();
    private InfoListAdapter mInfoAdapter;
    private RadioGroup mRg;
    private RelativeLayout mRelLoading;
    private RelativeLayout mRelRefush;

    private Handler infoHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLERCODEFIRST:/*资讯返回码*/
                    mInfo = getDataFromJson(msg);/*json解析数据 但是还不是可以显示的表单数据*/
                    if(mInfo==null){
                        mInfo = new InfoResult();
                    }
                    if(mInfo.getBody()==null){
                        //没有获取到数据，默认加载缓存
                        mInfo.setBody(getDB());
                    }
                    if (mInfo.getBody().size() < 1) {
                        //没有获取到数据，默认加载缓存
                        mInfo.setBody(getDB());
                    }
                    LogUtils.i(VISITSHOP, "任务获取成功--");
                    mInfoAdapter.setmInfo(mInfo);/*设置适配器this.mInfo = mInfo拿到具体的表单*/
                    mRecyclerView.setAdapter(mInfoAdapter);/*适配器传入表单显示数据*/
                    mRecyclerView.refreshComplete();/*清除上拉刷新的状态*/
                    setShowLoading(false);/*不显示加载图标*/
                    setShowRefush(false);/*不现实联网失败图标*/
                    //第一次获取数据,保存缓存
                    saveDB(mInfo.getBody());
                    break;
                case HANDLERCODEREFUSH:/*上拉刷新*/
                    mInfo = getDataFromJson(msg);/*json解析拿到列表集合*/
                    mInfoAdapter.setmInfo(mInfo);/*设置适配器*/
                    mInfoAdapter.notifyDataSetChanged();/*调用适配器显示数据*/
                    mRecyclerView.refreshComplete();/*清除上拉刷新的状态*/
                    //刷新完毕，更新缓存，先删除后保存
                    delDB();
                    saveDB(mInfo.getBody());
                    setShowLoading(false);/*不显示加载图标*/
                    LogUtils.i(VISITSHOP, "任务刷新成功--");
                    mRecyclerView.scrollToPosition(0);/*在点击更换type的情况下 将列表界面指向顶部 */
                    break;
                case HANDLERCODELOADMORE:/*下拉加载更多*/
                    InfoResult infoMore = getDataFromJson(msg);/*json解析数据*/
                    if(infoMore==null){
                        infoMore = new InfoResult();
                    }
                    if(infoMore.getBody()==null){/*如果为空 就不执行 但是因为刚刚加了 所以要减回去*/
                        mCurrentPage--;
                        Toast.makeText(getApplicationContext(), "没有更多数据", Toast.LENGTH_LONG).show();
                    }else if (infoMore.getBody().size() < 1) {
                        mCurrentPage--;
                        Toast.makeText(getApplicationContext(), "没有更多数据", Toast.LENGTH_LONG).show();
                    } else {
                        mInfo.getBody().addAll(infoMore.getBody());
                    }
                    mInfoAdapter.setmInfo(mInfo);/*设置适配器*/
                    mInfoAdapter.notifyDataSetChanged();/*调用适配器*/
                    mRecyclerView.loadMoreComplete();/*将下拉加载更多的状态清除*/
                    setShowLoading(false);/*不显示加载图标*/
                    LogUtils.i(VISITSHOP, "任务加载更多成功--");
                    break;
                case HttpFail:/*服务端数据获取失败 默认加载本地数据库缓存*/
                    Toast.makeText(getApplicationContext(), "访问服务器失败，请稍后再试", Toast.LENGTH_LONG).show();
                    //访问服务器失败，默认加载缓存
                    List<InfoResultBody> listChace = getDB();
                    setShowLoading(false);
                    mRecyclerView.loadMoreComplete();/*清除下拉加载更多的状态*/
                    mRecyclerView.refreshComplete();/*清除上拉刷新的状态*/
                    if (null == listChace || listChace.size() < 1) {
                        //没有缓存，显示重新加载
                        setShowRefush(true);/*显示联网失败的图标*/
                    } else {
                        setShowRefush(false);
                        //加载缓存
                        mInfo.setBody(listChace);/*得到表单集合*/
                        mInfoAdapter.setmInfo(mInfo);/*设置适配器*/
                        if (null == mRecyclerView.getAdapter()) {
                            mRecyclerView.setAdapter(mInfoAdapter);/*适配器传入表单显示数据*/
                        } else {
                            mInfoAdapter.notifyDataSetChanged();/*调用适配器*/
                        }
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setTitleName("资讯");
        initView();
        initListener();
        mInfoAdapter = new InfoListAdapter(getApplicationContext(), mInfo);/*封装一个适配器*/
        //默认获取公司动态信息
        getDataFromServer(mCurrentPage, HANDLERCODEFIRST, type);/*传入参数 资讯返回码 和页码 类型 联网从服务端获取数据*/
    }

    private void initListener() {
        //设置加载风格 上拉刷新 下拉加载更多
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);/*加载更多的动画*/
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);/*下拉刷新的动画*/
        //设置展示风格
        mRecyclerView.setLayoutManager(
                /*垂直线性的*/new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {/*调用XRecyclerView适配器 */
            @Override
            public void onRefresh() {/*上拉刷新*/
                mCurrentPage = 1;
                getDataFromServer(mCurrentPage, HANDLERCODEREFUSH, type);/*传入参数 页码 返回码 类型 联网从服务端获取数据*/
                setShowLoading(false);
                setShowRefush(false);
            }

            @Override
            public void onLoadMore() {/*下拉加载更多*/
                LogUtils.i(VISITSHOP, "more");
                mCurrentPage++;
                getDataFromServer(mCurrentPage, HANDLERCODELOADMORE, type);/*传入参数 资讯返回码 和页码 类型 联网从服务端获取数据*/
                setShowLoading(false);
                setShowRefush(false);
            }
        });

        mRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {/*单按钮点击事件*/
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_company) {
                    type = TYPECOMPANY;
                } else {
                    type = TYPEINDUSTY;
                }
                mCurrentPage=1;

                getDataFromServer(mCurrentPage, HANDLERCODEREFUSH, type);/*调用getDataFromServer()方法 连接服务端显示数据*/

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
                getDataFromServer(mCurrentPage, HANDLERCODEFIRST, type);
            }
        });
    }

    /**
     * 从服务端获取数据
     * @param currentPage
     * @param handlerCode
     * @param type
     */
    private void getDataFromServer(int currentPage, final int handlerCode, int type) {
        setShowLoading(true);
        OkHttpManager.getInstance().getNet(Constant.Info + "?pagenum=" + currentPage + "&type=" + type, new OkHttpManager.ResultCallback() {
            int code = handlerCode;
            @Override
            public void onFailed(Request request, IOException e) {
                infoHandler.sendEmptyMessage(HttpFail);
            }
            @Override
            public void onSuccess(String response) {
                Message message = new Message();
                message.obj = response;
                message.what = code;
                infoHandler.sendMessage(message);
            }
        });
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        mRecyclerView = (XRecyclerView) findViewById(R.id.lv_info);
        mRg = (RadioGroup) findViewById(R.id.rg);
        mRelLoading = (RelativeLayout) findViewById(R.id.loading);/*正在加载图标*/
        mRelRefush = (RelativeLayout) findViewById(R.id.refush);/*联网失败刷新图标*/
    }

    /**
     * 返回数据组装
     * @param msg
     * @return
     */
    private InfoResult getDataFromJson(Message msg) {
        Gson gson = new Gson();
        InfoResult info = gson.fromJson(msg.obj.toString(), InfoResult.class);
        return info;
    }

    /**
     * 是否显示加载界面
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
     * @param flag
     */
    public void setShowRefush(boolean flag) {
        if (flag) {
            mRelRefush.setVisibility(View.VISIBLE);
        } else {
            mRelRefush.setVisibility(View.GONE);
        }
    }

    /**
     * 将数据保存到本地数据库中
     * @param list
     */
    public void saveDB(List<InfoResultBody> list) {
        if(list == null){
            list = new ArrayList<InfoResultBody>();
        }
        for (InfoResultBody bean : list) {
            bean.save();
        }
        LogUtils.i("VISITSHOP", "数据保存成功");
    }

    /**
     * 从数据库中获取缓存数据
     * @return
     */
    public List<InfoResultBody> getDB() {
        List<InfoResultBody> listDB = DataSupport.findAll(InfoResultBody.class);
        return listDB;
    }

    /**
     * 清除缓存数据
     */
    public void delDB() {
        DataSupport.deleteAll(InfoResultBody.class);
    }

}
