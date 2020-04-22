package com.bdqn.visitshop.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bdqn.visitshop.MainActivity;
import com.bdqn.visitshop.R;
import com.bdqn.visitshop.activity.CreateVisitShopActivity;
import com.bdqn.visitshop.activity.InfoActivity;
import com.bdqn.visitshop.activity.InfoDetailActivity;
import com.bdqn.visitshop.activity.InterviewDetailActivity;
import com.bdqn.visitshop.activity.TaskActivity;
import com.bdqn.visitshop.adapters.InfoListBaseAdapter;
import com.bdqn.visitshop.adapters.TaskListBaseAdapter;
import com.bdqn.visitshop.bean.AnnImageResult;
import com.bdqn.visitshop.bean.AnnImgs;
import com.bdqn.visitshop.bean.InfoResult;
import com.bdqn.visitshop.bean.InfoResultBody;
import com.bdqn.visitshop.bean.Task;
import com.bdqn.visitshop.bean.TaskBody;
import com.bdqn.visitshop.net.OkHttpManager;
import com.bdqn.visitshop.utils.Constant;
import com.bdqn.visitshop.utils.SharePreUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Request;

/**
 * 首页--默认展示
 */

public class HomeFragment extends BaseFragment implements OnClickListener {

    private View view;
    private RelativeLayout progress, home_fragment_task;
    private TextView task_more, info_more;
    private TextView sort_shop, sort_visit, sort_train;
    private ViewPager vp;
    private LinearLayout layout, rl_http_failed;
    private RecyclerView task_lv, info_lv;
    private InfoListBaseAdapter info_adapter;
    private TaskListBaseAdapter task_adapter;
    private List<View> views;/*这个集合定义就是轮播图要展示的图片view 这里面给他定义一个基类view 通过views的.saze()就可以动态创建这个getCount()的数量了*/
    private int count = 0; /*定义轮播图的当前下标*/
    private List<String> url;
    private MViewpager vp_adapter;
    private Timer timer;
    private MainActivity mainActivity;
    private final int HttpTask = 1010;//获取任务信息返回码
    private final int AnnFaild = 1011;//获取广告图失败返回码
    private final int TaskFaild = 1012;//获取任务信息失败返回码
    public final int HttpGetInfo = 1013;//获取资讯信息返回码
    public final int GetImags = 1014;//获取广告图返回码
    private final int HttpFail = 1015;

    private String userid;//用户id
    private Boolean isLoad;//是否登录
    private List<InfoResultBody> info_list;
    private List<TaskBody> task_list;
     private   List<AnnImgs> imgs_list ;

    @Override
    public void onAttach(Context context) {/*设置父布局*/
        mainActivity = (MainActivity) getActivity();//获取当前MainActivity
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
/*onCreateView这里加载了一个布局*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initView();//初始化UI组件
        initdata();//初始化数据

        return view;
    }

    private void initView() {
        if (info_list == null) {
            info_list = new ArrayList<InfoResultBody>();
        }
        home_fragment_task = (RelativeLayout) view.findViewById(R.id.home_fragment_task);
        rl_http_failed = (LinearLayout) view.findViewById(R.id.rl_http_failed);
        rl_http_failed.setOnClickListener(this);
        progress = (RelativeLayout) view.findViewById(R.id.message_fregment_progress);
        task_more = (TextView) view.findViewById(R.id.fragment_home_task_more);
        task_more.setOnClickListener(this);
        info_more = (TextView) view.findViewById(R.id.fragment_home_info_more);
        info_more.setOnClickListener(this);
        sort_shop = (TextView) view.findViewById(R.id.fragment_sort_shop);
        sort_shop.setOnClickListener(this);
        sort_visit = (TextView) view.findViewById(R.id.fragment_sort_visit);
        sort_visit.setOnClickListener(this);
        sort_train = (TextView) view.findViewById(R.id.fragment_sort_train);
        sort_train.setOnClickListener(this);
        vp = (ViewPager) view.findViewById(R.id.fragment_img_viewpager);
        layout = (LinearLayout) view.findViewById(R.id.fragment_point_subscript);/*小圆点布局*/
        task_lv = (RecyclerView) view.findViewById(R.id.fragment_home_task_list);/*任务RecyclerView */
        info_lv = (RecyclerView) view.findViewById(R.id.fragment_home_info_list);/*资讯RecyclerView*/
/*可以添加LayoutManager 谷歌提供好的LinearLayoutManager 直接用  这里参数 1上下文环境 2使RecyclerView竖向展示 3 让参数列表正向展示或者反向展示 */
        task_lv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        info_lv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, true));
    }

    private void initdata() {
        //获取保存用户id，并判断用户是否登录
        userid = SharePreUtil.GetShareString(mContext, "userid");
        if ("".equals(userid)) {
            isLoad = false;
            Toast.makeText(mContext, R.string.please_login, Toast.LENGTH_SHORT).show();
        } else {
            isLoad = true;
        }
        if (views == null) {/*如果views为空 只是创建一个集合列表 但是没有数据*/
            views = new ArrayList<View>();
        }
        vp_adapter = new MViewpager();/*创建轮播图适配器*/
        vp.setAdapter(vp_adapter);/*设置给viewpager*/
        //添加界面滚动监听
        vp.addOnPageChangeListener(vp_adapter);
        //首页导航图获取 直接一个get方法 这边获取接口 这边是不需要传参数的 所以直接定义了一个回调
        OkHttpManager.getInstance().getNet(Constant.Announcement, new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) { /*服务端获取数据失败的时候*/
                mHandler.sendEmptyMessage(AnnFaild);
            }

            @Override
            public void onSuccess(String response) { /*服务端获取数据成功的时候*/
                Message message = new Message();
                message.obj = response;
                message.what = GetImags;/*获取广告图 GetImags为广告图的返回码*/
                mHandler.sendMessage(message);/*子线程可以通过Handler来通知主线程进行UI更新*/
            }
        });
        //请求更多资讯 用户每次下拉加载 就让pagenum +1 .分页给用户显示数据
        OkHttpManager.getInstance().getNet(Constant.Info + "?pagenum=" + 1 + "&type=" + 0, new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                mHandler.sendEmptyMessage(HttpFail);
            }

            @Override
            public void onSuccess(String response) {
                Message message = new Message();
                message.obj = response;
                message.what = HttpGetInfo;
                mHandler.sendMessage(message);
            }
        });
        if (isLoad) {
            //请求等多任务 用户每次下拉加载 就让pagenum +1 .分页给用户显示数据
            OkHttpManager.getInstance().getNet(Constant.Task + "?pagenum=" + 1, new OkHttpManager.ResultCallback() {
                @Override
                public void onFailed(Request request, IOException e) {
                    mHandler.sendEmptyMessage(TaskFaild);
                }

                @Override
                public void onSuccess(String response) {
                    Message message = new Message();
                    message.obj = response;
                    message.what = HttpTask;
                    mHandler.sendMessage(message);
                }
            });
            home_fragment_task.setVisibility(View.VISIBLE);
        }
        progress.setVisibility(View.VISIBLE);
    }
/*handler是Android给我们提供用来更新UI的一套机制，也是一套消息处理机制，我们可以发消息，也可以通过它处理消息*/
@SuppressLint("HandlerLeak")
Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case HttpFail://接收资讯信息请求失败信息
                    progress.setVisibility(View.GONE);
                    rl_http_failed.setVisibility(View.VISIBLE);
                    //展示保存数据
                    info_list = DataSupport.findAll(InfoResultBody.class);
                    if (info_list != null) {
                        if (isLoad) {
                            info_adapter = new InfoListBaseAdapter(mContext, info_list, 2);
                        } else {
                            info_adapter = new InfoListBaseAdapter(mContext, info_list, 5);
                        }
                        info_lv.setAdapter(info_adapter);
                    }
                    break;
                case HttpGetInfo://接收资讯信息请求成功信息
                    progress.setVisibility(View.GONE); /*先把等待加载框干掉*/
                    if (null != msg.obj && !"".equals(msg.obj)) {
                        Gson gson1 = new Gson(); /*解析数据通过插件生成的实体类拿到info对象*/
                        InfoResult info = gson1.fromJson(msg.obj.toString(), InfoResult.class);
                        //适配资讯列表 写适配器
                        if (info.getBody() != null) {
                            info_list = info.getBody();/*通过getBody()就拿到具体的返回的资讯列表*/
                            if (isLoad) {/*定义每个条目的适配器 定义首页数量为2*/
                                info_adapter = new InfoListBaseAdapter(mContext, info_list, 2);
                            } else {
                                info_adapter = new InfoListBaseAdapter(mContext, info_list, 5);
                            }
                            info_lv.setAdapter(info_adapter);/*设置适配器 将数据展现出来 最后更新缓存*/
                            //从数据库清除数据保存
                            DataSupport.deleteAll(InfoResultBody.class);
                            //添加新数据到数据库
                            DataSupport.saveAll(info.getBody());
                        }
                    }
                    break;
                case AnnFaild://接收公告图片请求失败信息
                    //展示保存数据
                    List<AnnImgs> imgs_dblist = DataSupport.findAll(AnnImgs.class); /*直接从数据库表里拿数据*/
                    if (imgs_dblist != null) {
                        //创建轮播标题
                        views.clear();
                        for (int i = 0; i < imgs_dblist.size(); i++) {
                            ImageView img = new ImageView(mContext);
                            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            Picasso.with(mContext)
                                    .load(Constant.BaseUrl + imgs_dblist.get(i).getImgUrl())
                                    .into(img);
                            views.add(img);
                        }
                        vp_adapter = new MViewpager();
                        vp_adapter.notifyDataSetChanged();
                        initPoint();
                        if (timer == null) {
                            timer = new Timer();
                            timer.schedule(task, 0, 4000);
                        }
                    }
                    ;
                case GetImags://接收公告图片请求成功信息
                    String resultImgs = (String) msg.obj;/*拿到服务端的数据*/
                    if (resultImgs != null && !"".equals(resultImgs)) {/*首先判断它不为空*/
                        Gson gson = new Gson();/*然后去解析数据 通过gson和gson插件快速生成一个这样AnnImageResult集合类*/
                        AnnImageResult air = gson.fromJson(resultImgs, AnnImageResult.class);
                         imgs_list = air.getBody();/*通过这个实体类再使用getBody() 就可以拿到具体的集合信息了*/
                        if (imgs_list == null) {
                            imgs_list = new ArrayList<AnnImgs>();
                        }/*避免空指针报错*/
                        //创建轮播标题
                        views.clear();/*这里就是刚才定义好的list集合 就是我们用来动态展示图片的集合 先把它清空*/
                        for (int i = 0; i < imgs_list.size(); i++) {/*后台返回的图片数量是不一定的 所以这边通过一个for循环 这边就没有写死*/
                            ImageView img = new ImageView(mContext); /*每一个图片单独展示 所以定义一个ImageView对象*/
                            img.setScaleType(ImageView.ScaleType.CENTER_CROP);/*也可以通过它的参数设置它展示的形式 这里让他居中放大*/
                            Picasso.with(mContext)/*通过Picasso去展示图片 用之前需要导入包*/
                                    .load(Constant.BaseUrl + imgs_list.get(i).getImgUrl()).into(img);
                            views.add(img);/*添加到之前的list集合*/

                        }/*通过一个循环 就可以展示多个ImageView 就可以动态显示网络图片*/

                        vp_adapter.notifyDataSetChanged();/*通过notifyDataSetChanged 就可以动态更改数据了*/
                        initPoint();//初始化创建下标小点
                        if (timer == null) {/*从安坐中开启一个任务计时器 每隔三秒去切换viewpager的选中状态就可以了*/
                            timer = new Timer();
                            timer.schedule(task, 0, 4000);
                        }
                        if (imgs_list.size() > 0) {
                            //从数据库清除数据保存
                            DataSupport.deleteAll(AnnImgs.class);

                            //添加新数据到数据库
                            DataSupport.saveAll(imgs_list);
                            for (AnnImgs bean : imgs_list) {
                                bean.save();
                            }
                        }
                    }
                    break;
                case HttpTask://接收获取任务成功消息
                    progress.setVisibility(View.GONE);
                    if (msg.obj != null) {
                        Gson gson = new Gson();
                        Task task = gson.fromJson(msg.obj.toString(), Task.class);
                        //适配资讯列表
                        if (task.getBody() != null) {
                            task_list = task.getBody();
                            task_adapter = new TaskListBaseAdapter(mContext, task_list);
                            task_lv.setAdapter(task_adapter);
                            //从数据库清除数据保存
                            DataSupport.deleteAll(TaskBody.class);
                            //添加新数据到数据库
                            DataSupport.saveAll(task.getBody());
//                            for (Task.BodyBean bean : task.getBody()) {
//                                bean.save();
//                            }
                        }
                    }
                    break;
                case TaskFaild://接收获取任务信息失败消息
                    progress.setVisibility(View.GONE);
                    rl_http_failed.setVisibility(View.VISIBLE);
                    //展示保存数据
                    task_list = DataSupport.findAll(TaskBody.class);
                    if (task_list != null) {
                        task_adapter = new TaskListBaseAdapter(mContext, task_list);
                        task_lv.setAdapter(task_adapter);
                    }
                    break;
                case Constant.Scroll://接收滚动消息，并执行
                    vp.setCurrentItem(count);/*vp就是viewpager 这里setCurrentItem设置viewpager当前选中项 通过这个方法 让他自动跳转图片 但是跳转的时候图标也要变化ViewPager.OnPageChangeListener */
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_http_failed://点击联网失败提示图标
                rl_http_failed.setVisibility(View.GONE);
                initdata();
                break;
            case R.id.fragment_home_info_more://更多资讯
                startActivity(new Intent(mActivity, InfoActivity.class));
                break;
            case R.id.fragment_home_task_more://更多任务
                startActivity(new Intent(mActivity, TaskActivity.class));
                break;
            case R.id.fragment_sort_shop://新建巡店
                if (isLoad) {
                    mActivity.startActivityForResult(new Intent(mActivity, CreateVisitShopActivity.class), Constant.ForResultShopBack);
                } else {
                    Toast.makeText(mContext, R.string.please_login, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.fragment_sort_visit://新建拜访
                if (isLoad) {
                    Intent inte = new Intent(mActivity, InterviewDetailActivity.class);
                    mActivity.startActivityForResult(inte, Constant.ForResultVisitBack);/*同时把拜访返回码传过去*/
                } else {
                    Toast.makeText(mContext, R.string.please_login, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.fragment_sort_train://训练查看
                if (isLoad) {
                    mainActivity.jumpTrainFragment();
                } else {
                    Toast.makeText(mContext, R.string.please_login, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    // 创建记时器发送图片轮播消息
    TimerTask task = new TimerTask() {
        @Override
        public void run() { /*这里面就是具体的执行任务了*/
            mHandler.sendEmptyMessage(Constant.Scroll); /*因为run()也是子线程这里让他在主线程去更新界面 也是通过一个Handler 发送一个空消息 这个空消息的标识符 我们给它定义成了一个常量 通知图片进行轮播 这里Handler就肯定有Handler来接受*/
            if (count == views.size()) {
                count = 0;
            } else {
                count = count + 1;
            }
        }
    };

    //创建viewpager适配器 直接继承PagerAdapter 四个方法是固定写法 getCount（）就是获取下标的数量 ViewPager.OnPageChangeListener就是跳转的时候小圆点也变化 给它实现了一个接口 用下面onPageSelected方法实现
    class MViewpager extends PagerAdapter implements ViewPager.OnPageChangeListener {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return views.size(); /*这里用了一个集合views 通过views的.saze()就可以动态创建这个getCount()的数量了*/
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));/*这里就是销毁的时候把views对象从容器中移除*/
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = views.get(position);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), InfoDetailActivity.class);
                    intent.putExtra("url", imgs_list.get(position).getDetailUrl());
                    startActivity(intent);

                }
            });
            container.addView(views.get(position));//将对应的view添加到container中才能够显示呀
            return views.get(position);
        }
/*滚动监听 动态更改指示小标的选中状态*/
        @Override
        public void onPageSelected(int position) {
            count=position;
            for (int i = 0; i < layout.getChildCount(); i++) { /*通过一个循环拿到当前所有的线性布局的小圆点*/
                ImageView image = (ImageView) layout.getChildAt(i);
                if (i == position) { /*如果下标等于当前选中项*/
                    image.setSelected(true);
                } else {
                    image.setSelected(false);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

    //创建图片变化下标图标
    public void initPoint() {
        layout.removeAllViews();/*首先这些小点都是在一个线性布局中 通过removeAllViews 清除所有下标图标*/
        for (int i = 0; i < views.size(); i++) {
            ImageView img = new ImageView(mContext);/*有几张图片就有几个指示下标*/
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(/*通过LayoutParams设置它的参数 */
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 5;/*设置每个小球的左右间距*/
            params.rightMargin = 5;
            img.setLayoutParams(params);/*封装好参数 就可以设置给每一个ImageView*/
            img.setImageResource(R.drawable.sns_v2_page_point);/*setImageResource就是设置小圆点的图片资源*/
            if (i == 0) {/*默认显示第一个小圆点*/
                img.setSelected(true);
            }
            layout.addView(img);/*定义好了之后 最后添加到linerlayout线性布局里面去*/
        }
    }

}
