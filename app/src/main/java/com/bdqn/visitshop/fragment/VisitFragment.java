package com.bdqn.visitshop.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.adapters.InterviewListAdapter;
import com.bdqn.visitshop.bean.InterviewResult;
import com.bdqn.visitshop.bean.InterviewResultBody;
import com.bdqn.visitshop.bean.User;
import com.bdqn.visitshop.net.OkHttpManager;
import com.bdqn.visitshop.utils.Constant;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

/**
 * 拜访
 */
public class VisitFragment extends BaseFragment {/*继承总BaseFragment 跳转动画*/

    private XRecyclerView recyclerView;
    private final int HANDLERCODEFIRST = 100;//第一次加载
    private final int HANDLERCODEREFUSH = 101;//刷新
    private final int HANDLERCODELOADMORE = 102;//加载更多
    public static final int HttpFail = 7001;//失败
    private int mCurrentPage = 1;//默认当前第一页
    private InterviewResult mInterviewResult = new InterviewResult();//服务端返回结果对象
    private InterviewListAdapter mInterviewListAdapter;
    private View mView;
    private RelativeLayout mRelLoading;
    private RelativeLayout mRelRefush;
    private User mUser;
    private TextView mTextNone;
    private boolean mIsShowNone = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_visit, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //获取数据库用户
        List<User> list = DataSupport.findAll(User.class);
        if (null != list && list.size() > 0) {
            mUser = list.get(0);
        }
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        //刷新数据
        if (null == mUser) {
            return;
        }
        //刷新界面，将当前页码置为第一页
        mCurrentPage = 1;
        getDataFromServer(mCurrentPage, HANDLERCODEREFUSH);
        //隐藏加载更多
        setShowLoading(false);
        //隐藏刷新界面
        setShowRefush(false);
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        recyclerView = (XRecyclerView) mView.findViewById(R.id.lv_interview);
        //设置加载风格
        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
        recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        //设置展示风格
        recyclerView.setLayoutManager(
                new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        mRelLoading = (RelativeLayout) mView.findViewById(R.id.message_fregment_progress);/*加载图标*/
        mRelRefush = (RelativeLayout) mView.findViewById(R.id.refush);/*联网失败图标*/
        mTextNone = (TextView) mView.findViewById(R.id.fragment_interview_none);
        //添加控件监听器
        initListener();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 添加监听器
     */
    private void initListener() {
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                if (null == mUser) {
                    return;
                }
                //刷新界面，将当前页码置为第一页
                mCurrentPage = 1;
                getDataFromServer(mCurrentPage, HANDLERCODEREFUSH);
                //隐藏加载更多
                setShowLoading(false);
                //隐藏刷新界面
                setShowRefush(false);

            }
            @Override
            public void onLoadMore() {
                //加载更多
                mCurrentPage++;
                getDataFromServer(mCurrentPage, HANDLERCODELOADMORE);
                //隐藏加载更多
                setShowLoading(false);
                //隐藏刷新界面
                setShowRefush(false);
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
     * @param currentPage
     * @param handlerCode
     */
    private void getDataFromServer(int currentPage, final int handlerCode) {
        setShowLoading(true);
        mTextNone.setVisibility(View.GONE);/*联网失败图标隐藏*/
        recyclerView.setVisibility(View.VISIBLE);/*展示表单布局*/
        //组装请求参数
        OkHttpManager.getInstance().getNet(Constant.HistoryInterview
                +"?pagenum="+currentPage+"&userid="+mUser.getUserId(), new OkHttpManager.ResultCallback() {
            private int code = handlerCode;
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
     * 返回数据组装
     *
     * @param msg
     * @return
     */
    private InterviewResult getDataFromJson(Message msg) {
        Gson gson = new Gson();
        InterviewResult info = gson.fromJson(msg.obj.toString(), InterviewResult.class);
        return info;
    }

    private Handler infoHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLERCODEFIRST:/*第一次加载*/
                    mInterviewResult = getDataFromJson(msg);/*Result类型的结果对象*/
                    if (mInterviewResult.getBody().size() < 1) {
                        //没有获取到数据，默认加载缓存
                        List<InterviewResultBody> list = getDB();
                        if (null == list || list.size() < 1) {
                            //没有缓存,空数据
                            mInterviewResult.setBody(new ArrayList<InterviewResultBody>());
                        } else {
                            mInterviewResult.setBody(list);
                        }
                    }
                    mInterviewListAdapter = new InterviewListAdapter(mActivity.getApplicationContext(), mInterviewResult);
                    mInterviewListAdapter.setmInterview(mInterviewResult);
                    recyclerView.setAdapter(mInterviewListAdapter);
                    setShowLoading(false);
                    setShowRefush(false);
                    //第一次获取数据,保存缓存
                    saveDB(mInterviewResult.getBody());
                    break;
                case HANDLERCODEREFUSH:
                    mInterviewResult = getDataFromJson(msg);
                    if (null == mInterviewResult.getBody() || mInterviewResult.getBody().size() < 1) {
                        mTextNone.setVisibility(View.VISIBLE);
                    }
                    if (null == mInterviewListAdapter) {
                        mInterviewListAdapter = new InterviewListAdapter(mActivity.getApplicationContext(), mInterviewResult);
                    }
                    if (recyclerView.getAdapter() == null) {
                        recyclerView.setAdapter(mInterviewListAdapter);
                    }
                    mInterviewListAdapter.setmInterview(mInterviewResult);
                    mInterviewListAdapter.notifyDataSetChanged();
                    recyclerView.refreshComplete();
                    //刷新完毕，更新缓存，先删除后保存
                    delDB();
                    saveDB(mInterviewResult.getBody());
                    break;
                case HANDLERCODELOADMORE:
                    InterviewResult interviewMore = getDataFromJson(msg);
                    if (interviewMore.getBody().size() < 1) {
                        //如果返回数据为空，则没有更多数据，将之前加1的页码恢复
                        mCurrentPage--;
                        Toast.makeText(mContext, "没有更多数据", Toast.LENGTH_LONG).show();
                    } else {
                        //将新返回的数据加入到结果集中显示
                        mInterviewResult.getBody().addAll(interviewMore.getBody());
                    }
                    mInterviewListAdapter.setmInterview(mInterviewResult);
                    mInterviewListAdapter.notifyDataSetChanged();
                    recyclerView.loadMoreComplete();
                    break;
                case HttpFail:
                    Toast.makeText(mActivity.getApplicationContext(), "访问服务器失败，请稍后再试", Toast.LENGTH_LONG).show();
                    //访问服务器失败，默认加载缓存
                    List<InterviewResultBody> listChace = getDB();
                    setShowLoading(false);
                    recyclerView.refreshComplete();
                    recyclerView.loadMoreComplete();
                    if (null == listChace || listChace.size() < 1) {
                        //没有缓存，显示重新加载
                        setShowRefush(true);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        setShowRefush(false);
                        //加载缓存
                        mInterviewResult.setBody(listChace);
                        if (mInterviewListAdapter == null) {
                            mInterviewListAdapter = new InterviewListAdapter(mActivity.getApplicationContext(), mInterviewResult);
                        }
                        mInterviewListAdapter.setmInterview(mInterviewResult);
                        if (null == recyclerView.getAdapter()) {
                            recyclerView.setAdapter(mInterviewListAdapter);
                        } else {
                            mInterviewListAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
            }
            return false;
        }
    });

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


    /**
     * 将数据保存到本地数据库中
     *
     * @param list
     */
    public void saveDB(List<InterviewResultBody> list) {
        DataSupport.saveAll(list);
//        for (InterviewResult.BodyBean bean : list) {
//            bean.save();
//            LogUtils.i("VISITSHOP", "数据保存成功");
//        }
    }

    /**
     * 从数据库中获取缓存数据
     *
     * @return
     */
    public List<InterviewResultBody> getDB() {
        List<InterviewResultBody> listDB = DataSupport.findAll(InterviewResultBody.class);
        return listDB;
    }

    /**
     * 清除缓存数据
     */
    public void delDB() {
        DataSupport.deleteAll(InterviewResultBody.class);
    }

}
