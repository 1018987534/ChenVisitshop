package com.bdqn.visitshop.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.adapters.VisitShopListAdapter;
import com.bdqn.visitshop.bean.DateList;
import com.bdqn.visitshop.bean.HistoryShopResult;
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
 * 巡店
 */
/*实现一个上拉刷新 下拉加载更多 的监听 接口LoadingListener 还有一个搜索功能的接口OnEditorActionListener*/
public class ShopFragment extends BaseFragment implements XRecyclerView.LoadingListener,
												View.OnClickListener,TextView.OnEditorActionListener{
	private View view;
	private XRecyclerView recyclerView;
	private RelativeLayout progress,rl_http_failed;
	public static List<DateList> info_list;
	private VisitShopListAdapter adapter;
	private TextView none;
	private EditText search;
	private String shop_name;
	private int pagenum=1;//加载页数

	private boolean IsDbData;//是否是数据库信息
	private Boolean IsSearch;//是否进入搜索模式
	private String userid;//用户id
	private Boolean isLoad;//是否登录
	public static boolean isFirst;//是否是首次登录

	public final int GetShopInfos = 106;//历史店面
	public final int HttpFail = 7001;//失败

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFirst = true;/*是否首次登录*/
		IsDbData = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/*将布局文件fragment_shop加载进来*/
		view = inflater.inflate(R.layout.fragment_shop, container, false);
		initView();//初始化视图
		return view;
	}
	
	private void initView() {
		IsSearch = false;
		recyclerView = (XRecyclerView) view.findViewById(R.id.activity_visitshop_list);
		/*设置上拉刷新 下拉加载更多的 监听*/
		recyclerView.setLoadingListener(this);
		rl_http_failed = (RelativeLayout) view.findViewById(R.id.activity_visitshop_refresh);
		progress = (RelativeLayout) view.findViewById(R.id.activity_visitshop_progress);
		rl_http_failed.setOnClickListener(this);
		none = (TextView) view.findViewById(R.id.activity_visitshop_none);
		search = (EditText) view.findViewById(R.id.et_search_shop);
		/*点击出现输入框 右下角出现一个搜索图标 监听这个图标*/
		search.setOnEditorActionListener(this);
		if(info_list==null){
			info_list = new ArrayList<DateList>();
		}
		/*创建recyclerView的适配器*/
		adapter = new VisitShopListAdapter(mContext,info_list);
		//设置加载动画 这里就是XRecyclerView 不一样的地方了
		recyclerView.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);/*加载更多的动画*/
		recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);/*下拉刷新的动画*/
		//设置展示风格
		recyclerView.setLayoutManager(
				/*线性的*/new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
		recyclerView.setAdapter(adapter); /*调用适配器*/
	}
	/*服务端获取数据*/
	@Override
	public void onResume() {
		super.onResume();
		userid = SharePreUtil.GetShareString(mContext,"userid");/*拿到userid*/
		if("".equals(userid)){
			isLoad = false;
			Toast.makeText(mContext,R.string.please_login,Toast.LENGTH_SHORT).show();
		}else{
			isLoad = true;
			if(isFirst){/*如果是第一次登录 就把页码pagenum设置成1 代表第一页*/
				isFirst = false;
				pagenum = 1;
				initData();//初始化数据
				progress.setVisibility(View.VISIBLE);/*跳出一个等待加载的框*/
			}else{
				if(info_list==null){
					none.setVisibility(View.VISIBLE);
				}else{
					if(info_list.size()==0){
						none.setVisibility(View.VISIBLE);
					}
				}
			}
		}
	}

	private void initData() {
		//店面查询请求
		String urlString = "";
		if(IsSearch){/*接口都是HistroyShop 参数也是一样的 唯一区别就是如果是搜索模式 就要添加一个shop_name字段 使用IsSearch来判断*/
			urlString = Constant.HistroyShop+"?userid="+userid+"&pagenum="+pagenum+"&keyword="+shop_name;
		}else{
			urlString = Constant.HistroyShop+"?userid="+userid+"&pagenum="+pagenum;
		}/*联网 传入url地址 再下面进行调用*/
		OkHttpManager.getInstance().getNet(urlString, new OkHttpManager.ResultCallback() {
			@Override
			public void onFailed(Request request, IOException e) {
				mHandler.sendEmptyMessage(Constant.HttpFail);
			}
			@Override
			public void onSuccess(String response) {
				Message m = new Message();
				m.obj = response;
				m.what = GetShopInfos;

				mHandler.sendMessage(m);
			}
		});
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case HttpFail://接收历史店面请求失败信息
					onLoad();
					progress.setVisibility(View.GONE);
					none.setVisibility(View.GONE);
					Toast.makeText(mContext,R.string.http_failed,Toast.LENGTH_LONG).show();
					//获取本地数据库信息
					if(info_list==null||info_list.size()==0){
						info_list = DataSupport.findAll(DateList.class);/*获取本地数据库*/
						if(info_list==null){
							info_list = new ArrayList<DateList>();
						}
						if(info_list.size()>0){
							if(userid.equals(info_list.get(0).getUserid())){
								IsDbData = true;
							}else{
								info_list.clear();
							}
							rl_http_failed.setVisibility(View.GONE);
							none.setVisibility(View.GONE);
							adapter.setList(info_list);
							adapter.notifyDataSetChanged();
						}else{
							rl_http_failed.setVisibility(View.VISIBLE);
						}
					}
					break;
				case GetShopInfos://接收历史店面请求成功信息
					onLoad();/*结束下拉刷新*/
					rl_http_failed.setVisibility(View.GONE);/*把联网失败点击刷新的图标 消失 点击刷新可以再次刷新的界面*/
					none.setVisibility(View.GONE);/*加载失败的界面 消失*/
					progress.setVisibility(View.GONE);/*正在加载的图标 消失*/
					String resultStr = (String)msg.obj;
					if(resultStr!=null&&!"".equals(resultStr)){
						Gson gson = new Gson();/*通过gson跟gsonFormat的插件解析成具体的实例化对象 get set*/
						HistoryShopResult infos = gson.fromJson(resultStr,HistoryShopResult.class);/*之后就是面向对象的操作了*/
						if(infos!=null){/*判断服务端返回的数据是否为空*/
							if(infos.getCode()==0){/*根据服务端返回的成功标识码 等于0代表成功*/
								if(infos.getDatelist()!=null){/*如果不为空 就在里面加载数据*/
									if(info_list==null||IsDbData){
										info_list = new ArrayList<DateList>();
									}
									if(pagenum==1){/*如果是第一页的数据，则把之前数据清空*/
										info_list.clear();
									}
									if(infos.getDatelist().size()==0){/*如果服务端发来的数据等于0 就跳出吐司*/
										Toast.makeText(mContext,"没有更多数据",Toast.LENGTH_SHORT).show();
									}else{
										info_list.addAll(infos.getDatelist());/*否则就把服务端发来的数据添加到之前的列表里面去*/
									}
									if(info_list.size()==0){/*如果列表没有数据*/
										adapter.setList(info_list);/*设置适配器*/
										adapter.notifyDataSetChanged();/*调用适配器*/
										none.setVisibility(View.VISIBLE);/*加载失败的界面 显示*/
									}else{
										pagenum++;/*最后！！！！ 把页码加一 现在加一暂时没用 但是轮到下次就有用*/
										none.setVisibility(View.GONE);
										adapter.setList(info_list);/*下拉刷新后更新适配器的数据源*/
										adapter.notifyDataSetChanged();/*调用数据源显示数据*/
										//从数据库清除数据保存    缓存
										DataSupport.deleteAll(DateList.class);
										//添加新数据到数据库
										DataSupport.saveAll(infos.getDatelist());
//										for (DateList bean : infos.getDatelist()) {
//											bean.save();
//										}
									}
								}
							}else{
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
	public void onRefresh() {//下拉刷新
		none.setVisibility(View.GONE);
		pagenum = 1;
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				//填写刷新请求yukun
				initData();/*调用总方法*/
			}
		}, 2000);/*加延迟*/
	}
	@Override
	public void onLoadMore() {//加载更多
		none.setVisibility(View.GONE);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				//填写刷新请求yukun
				initData();
			}
		}, 2000);
	}

	/**
	 * 结束上下拉刷新 服务端请求完成之后 把这些都给归位
	 */
	private void onLoad() {
		recyclerView.refreshComplete();
		recyclerView.loadMoreComplete();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.activity_visitshop_refresh://加载 联网失败后点击刷新
				rl_http_failed.setVisibility(View.GONE);
				none.setVisibility(View.GONE);
				initData();
				break;
			default:
				break;
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		/**
		 * 当点击搜索按钮时
		 */
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			hideKeyboard();/*隐藏小键盘 当成一个工具类来调用的*/
			shop_name = search.getText().toString().trim();/*拿到输入控件的text*/
			progress.setVisibility(View.VISIBLE);/*加载图标*/
			pagenum = 1;/*把页码设置成1 因为每次搜索肯定都是从第一页开始 */
			//店面查询请求url地址 把这个关键字shop_name封装进来下面都是一样 调取方法就可以了
			String urlString = Constant.HistroyShop+"?userid="+userid+"&pagenum="+pagenum+"&keyword="+shop_name;
			OkHttpManager.getInstance().getNet(urlString, new OkHttpManager.ResultCallback() {
				@Override
				public void onFailed(Request request, IOException e) {
					mHandler.sendEmptyMessage(Constant.HttpFail);
				}
				@Override
				public void onSuccess(String response) {
					Message m = new Message();
					m.obj = response;
					m.what = GetShopInfos;
					mHandler.sendMessage(m);
				}
			});
			IsSearch = true;/*这里其实代码重复了*/
		}
		return false;
	}

	public void hideKeyboard(){//隐藏软键盘
		InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(mActivity.INPUT_METHOD_SERVICE);
		if(inputMethodManager.isActive()){
			inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		/**
		 * 清空静态变量
		 */
		info_list = null;
		isFirst = false;
	}

}
