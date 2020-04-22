package com.bdqn.visitshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bdqn.visitshop.activity.CreateVisitShopActivity;
import com.bdqn.visitshop.activity.InterviewDetailActivity;
import com.bdqn.visitshop.activity.VisitShopActivity;
import com.bdqn.visitshop.fragment.HomeFragment;
import com.bdqn.visitshop.fragment.MeFragment;
import com.bdqn.visitshop.fragment.ShopFragment;
import com.bdqn.visitshop.fragment.TrainFragment;
import com.bdqn.visitshop.fragment.VisitFragment;
import com.bdqn.visitshop.utils.Constant;
import com.bdqn.visitshop.utils.SharePreUtil;
/*这边封装了一个BaseFragmentActivity 用来设置标题栏名字 隐藏顶部导航栏 返回点击事件*/
public class MainActivity extends BaseFragmentActivity implements OnClickListener{

    private ViewPager viewPager_content;
    private TextView txt_menu_bottom_home;
    private TextView txt_menu_bottom_shop;
    private TextView txt_menu_bottom_visit;
    private TextView txt_menu_bottom_train;
    private TextView txt_menu_bottom_me;
    private final int TAB_HOME = 0;  /*不会变化的常量 定义成final*/
    private final int TAB_SHOP = 1;
    private final int TAB_VISIT = 2;
    private final int TAB_TRAIN = 3;
    private final int TAB_ME = 4;
    private int IsTab;

    private HomeFragment homeFragment;
    private ShopFragment shopFragment;
    private VisitFragment visitFragment;
    private MeFragment meFragment;
    private TrainFragment trainFragment;

    private FragmentAdapter adapter;
    private ImageView title_bar_more,title_bar_change;

    private String userid;//用户id
    private Boolean isLoad;//是否登录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);/*调用了方法 使得状态栏透明*/

        initID();//初始化绑定组件id
        initView();//初始化视图
    }

    @Override
    public void onResume() {
        userid = SharePreUtil.GetShareString(mContext,"userid");//获取保存id
        if("".equals(userid)){//判断用户是否登录
            isLoad = false;
            Toast.makeText(mContext,R.string.please_login,Toast.LENGTH_SHORT).show();
        }else{
            isLoad = true;
        }
        super.onResume();
    }

    public void initID(){
        TextView title_bar_back = (TextView) findViewById(R.id.title_bar_back);//拿到返回图标的布局
        title_bar_back.setVisibility(View.GONE);
/*findViewById找到对象 取到这些按钮 然后设置监听事件*/
        viewPager_content = (ViewPager) findViewById(R.id.viewPager_content);//主显示界面 viewpager
        txt_menu_bottom_home = (TextView) findViewById(R.id.txt_menu_bottom_home);//底部五个按钮
        txt_menu_bottom_shop = (TextView) findViewById(R.id.txt_menu_bottom_shop);
        txt_menu_bottom_visit = (TextView) findViewById(R.id.txt_menu_bottom_visit);
        txt_menu_bottom_train = (TextView) findViewById(R.id.txt_menu_bottom_train);
        txt_menu_bottom_me = (TextView) findViewById(R.id.txt_menu_bottom_me);
        title_bar_more = (ImageView) findViewById(R.id.title_bar_more);//加号按钮
        title_bar_change = (ImageView) findViewById(R.id.title_bar_change);//暂时保存按钮
        txt_menu_bottom_home.setOnClickListener(this);
        txt_menu_bottom_shop.setOnClickListener(this);
        txt_menu_bottom_visit.setOnClickListener(this);
        txt_menu_bottom_train.setOnClickListener(this);
        txt_menu_bottom_me.setOnClickListener(this);
        title_bar_more.setOnClickListener(this);
        title_bar_change.setOnClickListener(this);
/*还有这个viewPager_content监听事件 滑动监听 切换界面*/
        viewPager_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {/*实现了onPageSelected这个方法 当前你最终滑到了哪一个界面 然后来进行操作*/
                Log.i("main_viewpager","position--"+position);
                switch (position) {
                    case TAB_HOME://点击首页模块执行 将UI图标动态隐藏
                        IsTab = 1;
                        title_bar_more.setVisibility(View.GONE);
                        title_bar_change.setVisibility(View.GONE);
                        setSelected(txt_menu_bottom_home);
                        viewPager_content.setCurrentItem(TAB_HOME, false);/*设置下标 false：代表快速切换 */
                        setTitleName("首页");
                        break;
                    case TAB_SHOP://点击巡店模块执行
                        IsTab = 2;
                        jumpShopFragment();
                        break;
                    case TAB_VISIT://点击拜访模块执行
                        IsTab = 3;
                        jumpVisitsFragment();
                        break;
                    case TAB_TRAIN://点击培训模块执行
                        IsTab = 4;
                        jumpTrainFragment();
                        break;
                    case TAB_ME://点击个人中心模块执行
                        IsTab = 5;
                        junpMeFragment();
                        break;
                }
            }

            private void junpMeFragment() {
                title_bar_more.setVisibility(View.GONE);
                title_bar_change.setVisibility(View.GONE);
                setSelected(txt_menu_bottom_me);
                viewPager_content.setCurrentItem(TAB_ME, false);
                setTitleName("个人中心");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    public void initView() {//初始化监听
        isLoad = false;
/*Fragment具体实现了 这边是五个界面 每一个界面就是一个Fragment new五个*/
        homeFragment = new HomeFragment();
        shopFragment = new ShopFragment();
        visitFragment = new VisitFragment();
        trainFragment = new TrainFragment();
        meFragment = new MeFragment();
        adapter = new FragmentAdapter(getSupportFragmentManager());/*适配器写好直接赋值给viewPager*/
        viewPager_content.setAdapter(adapter);/*设置显示在哪一个界面*/
        setSelected(txt_menu_bottom_home);/*一开始首页设为点击状态*/
        viewPager_content.setCurrentItem(TAB_HOME, false);/*设置下标为0 即显示在首界面*/
        setTitleName("首页");
    }

    @Override
    public void onClick(View v) { /*刚刚设置了很多点击事件 在这里处理这些点击事件 点击后将一些UI隐藏*/
        if(!isLoad){/*onResume方法来检测是否登陆*/
            Toast.makeText(mContext,R.string.please_login,Toast.LENGTH_SHORT).show();
        }
        switch (v.getId()) {
            case R.id.txt_menu_bottom_home://点击首页模块执行 这里点击了首页按钮
                IsTab = 1;/*当前选中的页面位数 第一位*/
                title_bar_more.setVisibility(View.GONE);/*将右上角加号隐藏 有的需要展示 有的需要隐藏 动态的进行*/
                title_bar_change.setVisibility(View.GONE);/*将右上角勾号隐藏*/
                setSelected(txt_menu_bottom_home);/*将选中的按钮字体颜色变化*/
                viewPager_content.setCurrentItem(TAB_HOME, false);/*将选中的下标当前界面修改为1*/
                setTitleName("首页");
                break;
            case R.id.txt_menu_bottom_shop://点击巡店模块执行
                IsTab = 2;
                title_bar_more.setVisibility(View.VISIBLE);
                title_bar_change.setVisibility(View.VISIBLE);
                setSelected(txt_menu_bottom_shop);
                viewPager_content.setCurrentItem(TAB_SHOP, false);
                setTitleName("巡店");
                break;
            case R.id.txt_menu_bottom_visit://点击拜访模块执行
                IsTab = 3;
                title_bar_more.setVisibility(View.VISIBLE);
                title_bar_change.setVisibility(View.GONE);
                setSelected(txt_menu_bottom_visit);
                viewPager_content.setCurrentItem(TAB_VISIT, false);
                setTitleName("拜访");
                break;
            case R.id.txt_menu_bottom_train://点击培训模块执行
                IsTab = 4;
                title_bar_more.setVisibility(View.GONE);
                title_bar_change.setVisibility(View.GONE);
                setSelected(txt_menu_bottom_train);
                viewPager_content.setCurrentItem(TAB_TRAIN, false);
                setTitleName("培训");
                break;
            case R.id.txt_menu_bottom_me://点击个人中心模块执行
                IsTab = 5;
                title_bar_more.setVisibility(View.GONE);
                title_bar_change.setVisibility(View.GONE);
                setSelected(txt_menu_bottom_me);
                viewPager_content.setCurrentItem(TAB_ME, false);
                setTitleName("个人中心");
                break;
            case R.id.title_bar_more:
                if(isLoad){
                    if(IsTab==2){//新建巡店
                        startActivity(new Intent(mActivity,CreateVisitShopActivity.class));
                    }else if(IsTab==3){//新建拜访
                        startActivity(new Intent(mActivity, InterviewDetailActivity.class));
                    }
                }else{
                    Toast.makeText(mContext,R.string.please_login,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.title_bar_change://展示未完成巡店信息
                if(isLoad){
                    startActivity(new Intent(mActivity,VisitShopActivity.class));
                }else{
                    Toast.makeText(mContext,R.string.please_login,Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    //当选中的时候变色 文字颜色变化 和图标
    public void setSelected(TextView textView){
        txt_menu_bottom_home.setSelected(false);
        txt_menu_bottom_shop.setSelected(false);
        txt_menu_bottom_visit.setSelected(false);
        txt_menu_bottom_train.setSelected(false);
        txt_menu_bottom_me.setSelected(false);
        textView.setSelected(true);
    }

    /*
     * 模块Fragment适配器 主显示界面
     */
    /*继承一个谷歌提供的*/
    public class FragmentAdapter extends FragmentPagerAdapter {
        private final int TAB_COUNT = 5;
        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }
        /*实现方法根据Id显示你想要显示的内容 返回不同的Fragment 适配器写好直接赋值给它
        *
        * 实现一个getItem方法 这个方法给你返回一个id 就是当前viewpage的下标 你根据这个下标选择来显示你的内容 然后witchid*/
        @Override
                    public Fragment getItem(int id) {
                    switch (id) {
                        case TAB_HOME:
                            homeFragment = new HomeFragment();
                            return homeFragment;
                        case TAB_SHOP:
                            return shopFragment;
                        case TAB_VISIT:
                            return visitFragment;
                        case TAB_TRAIN:
                            return trainFragment;
                        case TAB_ME:
                            return meFragment;
                        default:
                    return null;
            }
        }
        @Override
        public int getCount() {
            return TAB_COUNT;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharePreUtil.SetShareString(mContext,"userid","");//Activity死亡清空id保存
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("6", "onCreate: " + "debug");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {//如果按了返回键，resultCode参数等于0 没有收到返回码requestCode
            Log.d("7", "onCreate: " + "debug");
            return;
        }
        switch (requestCode){
            case Constant.ForResultShopBack://新建巡店完成后跳转到巡店界面
                jumpShopFragment();
                break;
            case Constant.ForResultVisitBack://新建拜访完成后跳转到拜访界面
                Log.d("5", "onCreate: " + "debug");
                jumpVisitsFragment();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){//手机返回事件
            if(IsTab!=1){//当点击返回时，如果当前界面不是首界面，或没切换过界面，切到首界面
                IsTab = 1;
                title_bar_more.setVisibility(View.GONE);
                title_bar_change.setVisibility(View.GONE);
                setSelected(txt_menu_bottom_home);
                viewPager_content.setCurrentItem(TAB_HOME, false);
                setTitleName("首页");
                return true;
            }
        }
        //继续执行父类的点击事件
        return super.onKeyDown(keyCode, event);
    }

    public void jumpShopFragment(){//切换TrainFragment,提供给HomeFragment的巡店查看调用
        title_bar_more.setVisibility(View.VISIBLE);
        title_bar_change.setVisibility(View.VISIBLE);
        setSelected(txt_menu_bottom_shop);
        viewPager_content.setCurrentItem(TAB_SHOP, false);
        setTitleName("巡店");
    }

    public void jumpTrainFragment(){//切换TrainFragment,提供给HomeFragment的培训查看调用
        title_bar_more.setVisibility(View.GONE);
        title_bar_change.setVisibility(View.GONE);
        setSelected(txt_menu_bottom_train);
        viewPager_content.setCurrentItem(TAB_TRAIN, false);
        setTitleName("培训");
    }
    public void jumpVisitsFragment(){//切换VisitFragment,提供给新建拜访完成后调用
        title_bar_more.setVisibility(View.VISIBLE);
        title_bar_change.setVisibility(View.GONE);
        setSelected(txt_menu_bottom_visit);
        viewPager_content.setCurrentItem(TAB_VISIT, false);
        setTitleName("拜访");
    }

}
