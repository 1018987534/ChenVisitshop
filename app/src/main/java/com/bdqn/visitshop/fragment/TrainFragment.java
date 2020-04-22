package com.bdqn.visitshop.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.adapters.TrainListAdapter;
import com.bdqn.visitshop.bean.TrainListBody;
import com.bdqn.visitshop.bean.TrainListResult;
import com.bdqn.visitshop.net.OkHttpManager;
import com.bdqn.visitshop.utils.Constant;
import com.bdqn.visitshop.utils.SharePreUtil;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

/**
 * 培训
 */
public class TrainFragment extends BaseFragment implements XRecyclerView.LoadingListener,View.OnClickListener{

	private View view;
	private XRecyclerView recyclerView;
	private TextView none;
	private TrainListAdapter adapter;
	private RelativeLayout progress,fragment_activity_refresh;
	private List<TrainListBody> info_list;
	private int pagenum;//加载页数
	public static boolean isFirst;//是否是首次登录
	private boolean isGetDb;//是否展示的本地数据
	private String userid;//用户id
	private Boolean isLoad;//是否登录

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFirst = true;
		isGetDb = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_train, container, false);
		initView();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		userid = SharePreUtil.GetShareString(mContext,"userid");
		if("".equals(userid)){
			isLoad = false;
			Toast.makeText(mContext,R.string.please_login,Toast.LENGTH_SHORT).show();
		}else{
			isLoad = true;
		//	if(isFirst){
			//	isFirst = false;
				pagenum = 1;
				initData();
				progress.setVisibility(View.VISIBLE);
		//	}else{
			//	if(info_list==null){
			//		none.setVisibility(View.VISIBLE);
			//	}else{
			//		if(info_list.size()==0){
			//			none.setVisibility(View.VISIBLE);
			//		}
		//		}
		//	}
		}
	}

	private void initData() {
		userid = SharePreUtil.GetShareString(mContext,"userid");
		if(info_list==null){
			info_list = new ArrayList<TrainListBody>();
		}
		OkHttpManager.getInstance().getNet(Constant.Train+"?userid="
				+userid+"&pagenum="+pagenum, new OkHttpManager.ResultCallback() {
			@Override
			public void onFailed(Request request, IOException e) {
				mHandler.sendEmptyMessage(Constant.HttpFail);
			}
			@Override
			public void onSuccess(String response) {
				Message message = new Message();
				message.obj = response;
				message.what = Constant.HttpGetTrainInfo;
				mHandler.sendMessage(message);
			}
		});
	}

	private void initView() {
		if(info_list==null){
			info_list = new ArrayList<TrainListBody>();
		}
		recyclerView = (XRecyclerView) view.findViewById(R.id.activity_list);
		recyclerView.setLoadingListener(this);
		none = (TextView) view.findViewById(R.id.activity_none_text);
		progress = (RelativeLayout) view.findViewById(R.id.fragment_activity_progress);
		fragment_activity_refresh = (RelativeLayout) view.findViewById(R.id.fragment_activity_refresh);
		fragment_activity_refresh.setOnClickListener(this);
		//设置加载风格
		recyclerView.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
		recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
		//设置展示风格
		recyclerView.setLayoutManager(
				new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
		adapter = new TrainListAdapter(mContext,info_list);
		recyclerView.setAdapter(adapter);
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case Constant.HttpFail://接收培训请求失败信息
					progress.setVisibility(View.GONE);
					onLoad();
					Toast.makeText(mContext,R.string.http_failed,Toast.LENGTH_LONG).show();
					if(info_list==null||info_list.size()==0){
						info_list = DataSupport.findAll(TrainListBody.class);
						if(info_list==null){
							info_list = new ArrayList<TrainListBody>();
                            none.setVisibility(View.VISIBLE);
						}else{
							isGetDb = true;
						}
						if(info_list.size()!=0){
							fragment_activity_refresh.setVisibility(View.GONE);
							none.setVisibility(View.GONE);
							adapter.setInfo_list(info_list);
							adapter.notifyDataSetChanged();
						}else{
							fragment_activity_refresh.setVisibility(View.VISIBLE);
						}
					}
					break;
				case Constant.HttpGetTrainInfo://接收培训请求成功信息
					fragment_activity_refresh.setVisibility(View.GONE);/*隐藏联网失败点击刷新的图标*/
					progress.setVisibility(View.GONE);/*隐藏正在加载的图标*/
					onLoad();/*结束下拉刷新 上拉查看更多的状态*/
					String resultStr = (String)msg.obj;
					if(resultStr!=null&&!"".equals(resultStr)){
						Gson gson = new Gson();
						TrainListResult infos = gson.fromJson(resultStr,TrainListResult.class);
						if(infos!=null){
							if(infos.getBody()!=null){
								if(info_list==null||isGetDb){
									info_list = new ArrayList<TrainListBody>();
								}
								if(pagenum==1){
									info_list.clear();
								}
								if(infos.getBody().size()==0){
									Toast.makeText(mContext,"没有更多数据",Toast.LENGTH_SHORT).show();
								}else{
									info_list.addAll(infos.getBody());/*数据传给info_list*/
								}
								if(info_list.size()==0){
									none.setVisibility(View.VISIBLE);
								}else{
									pagenum++;
									adapter.setInfo_list(info_list);/*设置适配器*/
									adapter.notifyDataSetChanged();/*调用适配器*/
									//从数据库清除数据保存
									DataSupport.deleteAll(TrainListBody.class);
									//添加新数据到数据库
									DataSupport.saveAll(infos.getBody());
//									for (TrainListBody bean : infos.getBody()) {
//										bean.save();
//									}
								}
							}
						}
					}
					break;
				default:
					break;
			}
		}
	};

	@Override
	public void onRefresh() {//下拉刷新功能
//		none.setVisibility(View.GONE);
		pagenum = 1;
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				initData();
			}
		}, 2000);
	}
	@Override
	public void onLoadMore() {//查看更多功能
//		none.setVisibility(View.GONE);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				initData();
			}
		}, 2000);
	}

	private void onLoad() {//结束下拉刷新，查看更多功能
		recyclerView.refreshComplete();
		recyclerView.loadMoreComplete();
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//清空静态变量
		info_list = null;
		isFirst = false;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.fragment_activity_refresh://加载失败后点击刷新
				fragment_activity_refresh.setVisibility(View.GONE);
				initData();
				break;
			default:
				break;
		}
	}

}
