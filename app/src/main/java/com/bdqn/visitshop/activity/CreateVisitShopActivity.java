package com.bdqn.visitshop.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bdqn.visitshop.R;
import com.bdqn.visitshop.adapters.SelectShopAdapter;
import com.bdqn.visitshop.bean.SelectShop;
import com.bdqn.visitshop.bean.ShopList;
import com.bdqn.visitshop.bean.SubmitResult;
import com.bdqn.visitshop.fragment.ShopFragment;
import com.bdqn.visitshop.net.OkHttpManager;
import com.bdqn.visitshop.utils.CacheFileUtils;
import com.bdqn.visitshop.utils.Constant;
import com.bdqn.visitshop.utils.ImageTools;
import com.bdqn.visitshop.utils.LogUtils;
import com.bdqn.visitshop.utils.SharePreUtil;
import com.bdqn.visitshop.view.MyLinearLayoutManager;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;


/**
 * 创建巡店
 */
/*TextView.OnEditorActionListener是搜索监听接口*/
public class CreateVisitShopActivity extends BaseActivity implements View.OnClickListener,
        TextView.OnEditorActionListener, BDLocationListener {
    //布局组件
    private LinearLayout layout_hide;
    private RecyclerView search_lv;
    private RatingBar rb_shop, rb_person, rb_product;
    private TextView tv_save, tv_submit, tv_shopId, tv_shopWhere;
    private EditText et_suggest, et_shopName;
    private RelativeLayout rl_shopId, rl_shopWhere, progress;
    private LinearLayout gallery;
    //变量值
    private float shop_number, person_number, product_number;
    private String shop_name, str_suggest, shop_id, shop_where;
    private String filePath;
    //百度地图定位方法类
    public LocationClient mLocationClient = null;
    //集合
    private List<SelectShop.ShopLists> list_shop;/*点击搜索后的所有 店铺名称和ID 的数据集合*/
    public static ArrayList<String> filePaths;/*每次拍完照把图片保存到这个集合里面去*/

    private ShopList sl;/*创建一个 临时保存数据展示集合用来直接显示数据 里面包括是否是搜索的店面名称 如果改变就是false */
    private String userid;//用户id
    private Boolean IsShop;//是否是搜索的店面信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_visit_shop);/*首先找到布局文件*/
        setTitleName("新增巡店");
        //版本26以后 调用系统拍照要加这三条
       // StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
       // StrictMode.setVmPolicy(builder.build());
       // builder.detectFileUriExposure();
        //----------------------------------------
        mLocationClient = new LocationClient(getApplicationContext());//声明LocationClient类
        mLocationClient.registerLocationListener(this);//注册定位监听
        initLocation();//初始化定位设置


        initView();//初始化视图
        initData();//初始化数据
    }
/*添加图片*/
    @Override
    public void onResume() {
        super.onResume();/*filePaths就是你最终保存图片的url地址集合 先判断不为空 就展示出来*/
        if (filePaths != null) {//浏览图片后刷新画廊
            gallery.removeAllViews();/*先把图片清空*/
            gallery.setVisibility(View.VISIBLE);/*显示出来*/
            /*根据保存的地址去动态创建图片*/
            for (int i = 0; i <= filePaths.size(); i++) {
                //创建一个ImageView 通过动态的方式去创建imageView对象
                ImageView imageView = new ImageView(mContext);
                //设置ImageView的缩放类型 这里是伸缩 填充XY轴
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(180, 220);/*设置宽和高*/
                layout.leftMargin = 10;/*设置图片的左间距*/
                layout.rightMargin = 10;/*设置图片的右间距*/
                imageView.setLayoutParams(layout);/*把这些属性设置给imageView*/
                //为视图适配图片 设置它的图片源 本地显示图片
                if (i < filePaths.size()) { /*首先根据下标 判断现在生成的是不是最后一张图片（也就是加号）*/
                    File file = new File(filePaths.get(i));/*申明成file类型生成图片文件  根据filePaths.get(i)获取图片的地址  这个就是你具体拍完照生成的图片文件*/
                    if (file.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(filePaths.get(i));/*通过BitmapFactory.decodeFile 从文件拿到这个bitmap*/
                        imageView.setImageBitmap(bitmap);/*然后设置进去 现在图片就设置好了 然后就把这个东西去添加到横向滑动的布局当中去*/
                    } else {/*如果因为某些原因文件丢失 则显示默认图片*/
                        imageView.setImageResource(R.mipmap.default_img);
                    }
                } else {/*最后一张图片是显示添加图片*/
                    imageView.setImageResource(R.mipmap.btn_add_img);
                }
                gallery.addView(imageView, i);/*gallery这个是LinearLayout  然后addView动态添加 添加的时候要注意顺序 就是新拍的要放到后面去 所以引入了i*/
                imgClickListener(imageView, i);/*点击图片有一个事件 也是抽象出一个方法 当前图片下标i也传进去*/
            }
        }
    }

    private void initData() {
        userid = SharePreUtil.GetShareString(mContext, "userid");
        if (filePaths == null) {
            filePaths = new ArrayList<String>();
        }
        sl = (ShopList) getIntent().getSerializableExtra("info");/*拿到传过来的 临时保存巡店的表单的一条数据*/
        if (sl != null) {
            if (!"".equals(sl.getShopid())) {
                tv_shopId.setText(sl.getShopid());/*插入店铺ID数据*/
                rl_shopId.setVisibility(View.VISIBLE);/*显示店铺ID*/
            }
            if (!"".equals(sl.getName())) {
                et_shopName.setText(sl.getName());/*插入数据*/
            }
            if (!"".equals(sl.getShoplocation())) {
                tv_shopWhere.setText(sl.getShoplocation());
            }
            if (!"".equals(sl.getShoplevel())) {
                try {
                    String[] strCode = sl.getShoplevel().split(";");
                    rb_shop.setRating(Float.parseFloat(strCode[0]));
                    rb_person.setRating(Float.parseFloat(strCode[1]));
                    rb_product.setRating(Float.parseFloat(strCode[2]));
                } catch (Exception e) {
                    LogUtils.i("tag", "拆分字符串异常，请查看后台数据");
                }
            }
            if (!"".equals(sl.getFeedback())) {
                et_suggest.setText(sl.getFeedback());
            }
            if (!"".equals(sl.getImgname())) {
                try {
                    String[] imgs = sl.getImgname().split(";");
                    filePaths.clear();
                    for (int i = 0; i < imgs.length; i++) {
                        filePaths.add(imgs[i]);
                    }
                } catch (Exception e) {
                    LogUtils.i("tag", "拆分字符串异常，请查看后台数据");
                }
            }
        }
    }

    private void initView() {
        progress = (RelativeLayout) this.findViewById(R.id.activity_createvisit_progress);
        layout_hide = (LinearLayout) this.findViewById(R.id.activity_createvisit_hide);
        search_lv = (RecyclerView) this.findViewById(R.id.activity_createvisit_list);
        rb_shop = (RatingBar) this.findViewById(R.id.activity_createvisit_score_shop);
        rb_person = (RatingBar) this.findViewById(R.id.activity_createvisit_score_person);
        rb_product = (RatingBar) this.findViewById(R.id.activity_createvisit_score_product);
        tv_save = (TextView) this.findViewById(R.id.activity_createvisit_save);
        tv_submit = (TextView) this.findViewById(R.id.activity_createvisit_submit);
        tv_shopId = (TextView) this.findViewById(R.id.activity_createvisit_shopid);
        tv_shopWhere = (TextView) this.findViewById(R.id.activity_createvisit_shopwhere);/*要定位的时候把EditText改成TextView xml那里也要改*/
        et_suggest = (EditText) this.findViewById(R.id.activity_createvisit_et);
        et_shopName = (EditText) this.findViewById(R.id.activity_createvisit_shopname);
        rl_shopId = (RelativeLayout) this.findViewById(R.id.activity_createvisit_shopid_rl);
        rl_shopWhere = (RelativeLayout) this.findViewById(R.id.activity_createvisit_shopwhere_rl);
        gallery = (LinearLayout) this.findViewById(R.id.activity_createvisit_gallery);/*这里创建了一个控件 给它起名为gallery画廊 */

        //监听事件
        viewEvents();
    }

    private void viewEvents() {
        tv_submit.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        tv_shopWhere.setOnClickListener(this);
        et_shopName.setOnEditorActionListener(this);
        et_shopName.addTextChangedListener(new TextWatcher() {/*输入店铺关键字然后点击搜索后发生的事情*/
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (sl != null) {/*如果临时保存拿来的数据不为空   文本发生了改变 就设置为false*/
                    sl.setIsSelsct(true);/*设置为false*/
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        rb_shop.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                shop_number = rating;
            }
        });
        rb_person.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                person_number = rating;
            }
        });
        rb_product.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                product_number = rating;
            }
        });

        search_lv.setLayoutManager(new MyLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, true));
    }

    public void selectShop(SelectShop.ShopLists ss) {/*点击搜索后显示表单 然后点击表单数据后调用selectShop这个方法*/
        tv_shopId.setText(ss.getId());/*把表单拿到的数据插入布局显示*/
        et_shopName.setText(ss.getName());/*把表单拿到的数据插入布局显示*/
        rl_shopId.setVisibility(View.VISIBLE);/*显示店铺ID布局*/
        rl_shopWhere.setVisibility(View.VISIBLE);/*显示地址布局*/
        layout_hide.setVisibility(View.VISIBLE);/*把图片采集开始到下面整个框的布局都显示*/
        search_lv.setVisibility(View.GONE);/*把表单数据布局隐藏*/
    }

    /*
     * 图片点击查看事件
     */
    private void imgClickListener(View imagView, final int position) {
        imagView.setOnClickListener(new View.OnClickListener() {/*就是给imagView添加了一个监听事件*/
            @Override
            public void onClick(View v) {
                if (filePaths == null) {
                    filePaths = new ArrayList<String>();
                }/*判断是否为空 如果为空 就初始化一下*/
                if (position == filePaths.size()) {/*这个判断你是不是点击的最后一张图片（加号）*/
                    if (filePaths == null) {
                        filePaths = new ArrayList<String>();
                    }
                    if (filePaths.size() < 4) {
                        goTakePhoto();
                    } else {
                        Toast.makeText(mContext, "最多拍摄4张照片", Toast.LENGTH_SHORT).show();
                    }
                } else {/*如果不是点击最后一张图片 就跳转到图片查看的界面*/
                    Intent intent = new Intent(mContext, ImageViewActivity.class);/*通过一个intent跳转*/
                    intent.putExtra("type", Constant.ShopImgUp);/*传入两个参数type图片类型 这里类型是有两种 一种是从首页点进去查看图片 这种情况图片是不能删除的 还有一种就是现在新建的图片 position当前图片的位置 比如说点击第二张进去 进去后就要定位到第二张显示*/
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        //当用户点击定位时，启动定位
        if (v.getId() == R.id.activity_createvisit_shopwhere) {
            if (mLocationClient != null) {
                if (!mLocationClient.isStarted()) {
                    mLocationClient.start();//启动定位
                }
            }
        } else {
            //获取填写数据
            shop_name = et_shopName.getText().toString().trim();
            shop_id = tv_shopId.getText().toString().trim();
            shop_where = tv_shopWhere.getText().toString().trim();
            str_suggest = et_suggest.getText().toString().trim();
            shop_number = rb_shop.getRating();
            person_number = rb_person.getRating();
            product_number = rb_product.getRating();
            switch (v.getId()) {
                case R.id.activity_createvisit_save://新增巡店临时保存
                    SaveDataInDb();/*将当前页面填写的信息保存到本地数据库 将原来的数据删除*/
                    if (sl != null) {//判断之前数据库里面的集合sl有没有删掉
                        finish();
                    } else {
                        startActivity(new Intent(mActivity, VisitShopActivity.class));
                        finish();
                    }
                    break;
                case R.id.activity_createvisit_submit://新增巡店提交
                    IsShop = false;
                    shop_name = et_shopName.getText().toString().trim();
                    //初步判断
                    if ("".equals(shop_name) || shop_name.equals(R.string.activity_createvisit_shopname_hint)) {/*判断店面是否对应搜索到的店名 防止用户不小心修改掉*/
                        Toast.makeText(mContext, "请对店面进行搜索并添加", Toast.LENGTH_SHORT).show();
                    } else if ("".equals(shop_id)) {/*店面名称不能为空*/
                        Toast.makeText(mContext, "请对店面进行搜索并添加", Toast.LENGTH_SHORT).show();
                    }
//                    else if("".equals(shop_where)||"null".equals(shop_where)||shop_where.equals(R.string.activity_createvisit_shopwhere_hint)){
//                        Toast.makeText(mContext, "店面位置为空，请点击定位", Toast.LENGTH_SHORT).show();
//                    }
                    else if (filePaths.size() < 2 || filePaths.size() > 4) {/*图片要2-4张*/
                        Toast.makeText(mContext, "请为店面拍摄2-4张图片", Toast.LENGTH_SHORT).show();
                    } else if (shop_number < 1 || person_number < 1 || product_number < 1) {
                        Toast.makeText(mContext, "请为店面打分，最低分1分", Toast.LENGTH_SHORT).show();
                    } else if ("".equals(str_suggest)) {
                        Toast.makeText(mContext, "请为店面点评", Toast.LENGTH_SHORT).show();
                    } else {
                        if (sl != null) {
                            IsShop = sl.getIsSelsct();/*判断是不是临时巡店拿来的数据*/
                        }
                        if (list_shop != null) {
                            for (int i = 0; i < list_shop.size(); i++) {
                                if (list_shop.get(i).getName().equals(shop_name)) {/*判断店铺名称在总列表里面是否存在*/
                                    IsShop = true;/*已经搜索了*/
                                    break;
                                }else{
                                    IsShop=false;
                                }
                            }
                        }
                        if (IsShop) {
                            et_shopName.setFocusableInTouchMode(false);/*取消 店铺名称输入框的焦点*/
                            tv_save.setClickable(false);/*让按钮无法点击 防止多次提交*/
                            tv_submit.setClickable(false);/*让按钮无法点击*/
                            progress.setVisibility(View.VISIBLE);
                            //店面数据提交请求封装 这里把数据提交也封装到OkHttpManager这个接口里面 首先声明一个数组Param[][ˌpærəˈm]  然后添加接口文档里的相应的数据
                            OkHttpManager.Param[] params = new OkHttpManager.Param[]{
                                    new OkHttpManager.Param("userId", userid),
                                    new OkHttpManager.Param("shopId", shop_id),
                                    new OkHttpManager.Param("shopName", shop_name),
                                    new OkHttpManager.Param("feedback", str_suggest),
                                    new OkHttpManager.Param("shopLevel", (int) shop_number + ";" + (int) person_number + ";" + (int) product_number),
                                    new OkHttpManager.Param("shopAddress", shop_where)};
                            /*同时把照片的【文件】传到服务端 首先定义了一个File[]数组*/
                            File[] files = new File[filePaths.size()];/*filePaths是之前拍照的时候保存的图片地址集合*/
                            for (int i = 0; i < filePaths.size(); i++) {
                                String sName = filePaths.get(i);/*通过每一个对象对应的地址构建出来一个新的File对象*/
                                File imgFile = new File(sName);
                                files[i] = imgFile;/*把它保存到files数组里面*/
                            }
                            /*然后这里就是具体用OkHttp来提交数据到服务端 通过upFileNet ResultCallback来回调*/
                            OkHttpManager.getInstance().upFileNet(Constant.VisitShopSubmit, new OkHttpManager.ResultCallback() {
                                @Override
                                public void onFailed(Request request, IOException e) {
                                    SaveDataInDb();/*把数据保存到数据库*/
                                    tv_submit.setClickable(true);/*修改让它可以重复提交*/
                                    et_shopName.setFocusableInTouchMode(true);/*焦点重新放在店铺名称那里*/
                                    tv_save.setClickable(true);
                                    progress.setVisibility(View.GONE);/*把加载框消失*/
                                    Toast.makeText(mContext, R.string.http_failed, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onSuccess(String response) {
                                    tv_submit.setClickable(true);/*无所谓*/
                                    et_shopName.setFocusableInTouchMode(true);
                                    tv_save.setClickable(true);
                                    progress.setVisibility(View.GONE);
                                    Gson gsr = new Gson();/*拿到服务端返回的数据*/
                                    SubmitResult sr = gsr.fromJson(response, SubmitResult.class);/*解析到我们这个提交的返回码*/
                                    if (sr.getCode() == 0) {/*返回成功*/
                                        if (sl != null) {
                                            DataSupport.delete(ShopList.class, sl.getId());/*删除从临时保存界面自动插入的sl集合*/
                                        }
                                        ShopFragment.isFirst = true;
                                        mActivity.setResult(RESULT_OK);/*传一个返回码 如果是从首页点进去的 就回到首页调用onActivityResult()方法来跳转到巡店界面 如果是巡店界面点加号进去的 就直接返回就可以了*/
                                        Toast.makeText(mContext, "巡店信息提交成功", Toast.LENGTH_SHORT).show();
                                        finish();/*关闭界面 返回*/
                                    } else {
                                        SaveDataInDb();
                                        Toast.makeText(mContext, R.string.please_resubmit, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, files, "file", params);/*files是图片文件的数组 file是一个字符串参数 params字符串的数组*/
                        } else {
                            Toast.makeText(mContext, "请对店面进行搜索并添加", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 临时保存数据到 本地数据库
     */
    private void SaveDataInDb() {
        IsShop = false;
        ShopList dl = new ShopList();
        dl.setFeedback(str_suggest);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < filePaths.size(); i++) {
            if (i != 0) {
                sb.append(";");
            }
            sb.append(filePaths.get(i));
        }
        dl.setUserid(userid);
        dl.setImgname(sb.toString());
        dl.setShopid(shop_id);
        dl.setShoplocation(shop_where);
        dl.setName(shop_name);
        dl.setShoplevel((int) shop_number + ";" + (int) person_number + ";" + (int) product_number);
        if (list_shop == null) {
            list_shop = new ArrayList<SelectShop.ShopLists>();
        }
        if (sl != null) {
            IsShop = sl.getIsSelsct();/*判断上一次通过临时保存显示在新增巡店界面的数据 的店铺名称是否修改过*/
        }
        if (list_shop != null) {/*如果 点击搜索后的所有 店铺名称和ID 的数据集合 不为空*/
            for (int i = 0; i < list_shop.size(); i++) {
                if (list_shop.get(i).getName().equals(shop_name)) {/*判断当前店铺名称在总列表里面是否存在 是不是通过搜索得到的*/
                    IsShop = true;
                    break;
                }
            }
        }
        dl.setIsSelsct(IsShop);
        dl.save();
        if (sl != null) {
            DataSupport.delete(ShopList.class, sl.getId());/*把原来的数据删掉*/
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        /**
         * 当点击搜索按钮执行 正好匹配
         */
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            hideKeyboard();/*隐藏软键盘*/
            shop_name = et_shopName.getText().toString().trim();/*拿到关键字*/
            if (shop_name.equals("")) {
                Toast.makeText(mContext, R.string.please_into_userinfo, Toast.LENGTH_SHORT).show();/*判断是否为空*/
            } else {/*开始发送请求给服务端*/
                progress.setVisibility(View.VISIBLE);/*显示 正在加载的图标*/
                if (list_shop == null) {/*初始化列表数据*/
                    list_shop = new ArrayList<SelectShop.ShopLists>();
                }
                list_shop.clear();
                //店面查询请求 用get方法传入URL地址 加关键字
                OkHttpManager.getInstance().getNet(Constant.ShopSelect + "?keyword=" + shop_name, new OkHttpManager.ResultCallback() {
                    @Override
                    public void onFailed(Request request, IOException e) {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(mContext, R.string.http_failed, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String response) {
                        progress.setVisibility(View.GONE);
                        //解析数据
                        Gson gson = new Gson();
                        if (!"".equals(response) && response != null) {/*通过gson访问的插件直接生成一个类 给它解析成一个对象*/
                            SelectShop ss = gson.fromJson(response, SelectShop.class);
                            if (ss != null) {
                                list_shop = ss.getShoplists();
                                if (list_shop == null) {
                                    list_shop = new ArrayList<SelectShop.ShopLists>();
                                }
                                if (list_shop.size() > 0) {/*拿到服务端返回的数据 并且有数据不为空 就给它创建适配器 把输入的关键字传进适配器里面*/
                                    SelectShopAdapter adapter = new SelectShopAdapter(mContext, list_shop, shop_name);
                                    search_lv.setAdapter(adapter);/*直接设置适配器*/
                                    rl_shopId.setVisibility(View.GONE);/*相关属性更改一下 最主要就是把店铺ID展示出来*/
                                    rl_shopWhere.setVisibility(View.GONE);/*把地址隐藏*/
                                    search_lv.setVisibility(View.VISIBLE);/*表单数据显示出来*/
                                    layout_hide.setVisibility(View.GONE);/*把图片采集开始到下面整个框都隐藏*/
                                } else {
                                    Toast.makeText(mContext, "未搜到相关店铺", Toast.LENGTH_LONG).show();
                                    shop_id = "";
                                    tv_shopId.setText("");
                                    rl_shopId.setVisibility(View.GONE);/*把店铺ID隐藏*/
                                }
                            }
                        }
                    }
                });
            }
        }
        return false;
    }

    /**
     * 调用手机拍照功能
     */
    protected void goTakePhoto() {/*通过Intent去调用系统原生的拍照 这里都是固定的 传一些intent参数*/
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);/*ACTION_IMAGE_CAPTURE：照相机*/
        filePath = CacheFileUtils.getUpLoadPhotosPath();/*通过工具类找到照片地址*/
        Uri uri = Uri.fromFile(new File(filePath));/*通过照片名称的绝对路径 生成照片文件*/
        //Uri uri = FileProvider.getUriForFile(mContext, "com.jph.takephoto.fileprovider", new File(filePath));//通过FileProvider创建一个content类型的Uri 特殊26

       // intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//特殊添加26版本 ；添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);/*将拍出来的照片存到这个文件里*/
        intent.putExtra(MediaStore.Images.ImageColumns.ORIENTATION, 0);
        startActivityForResult(intent, 1001);/*因为拍完照需要拿到这个数据 需要通过startActivityForResult去接收 这里面给它指定了一个请求码1001 这个方法调用完之后就会启动系统原生的拍照界面*/
    }
/*拍完照之后  在onActivityResult这里获取数据*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {//检验接收数据 RESULT_OK是系统定义的判断请求是否成功
            return;
        }
        switch (requestCode) {/*这里拿到requestCode 返回码*/
            case 1001://接收拍照返回数据 1001就是用来请求的返回码
                if (!TextUtils.isEmpty(filePath)) {
                    Bitmap bitmap = ImageTools.convertToBitmap(filePath, 640, 640);/*统一定义在ImageTools这个方法里*/
                    Bitmap bitmapComp = ImageTools.comp(bitmap);//图片压缩
                    ImageTools.saveBitmap(bitmapComp, filePath);/*通过saveBitmap把压缩完成的bitmapComp保存成文件 同时覆盖之前的图片地址*/
                    if (bitmap != null) {
                        filePaths.add(filePath);/*添加到图片filePaths集合里面去*/
                    }
                }
                break;
        }
    }

    public void hideKeyboard() {//隐藏软键盘
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * 百度定位初始化设置
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {//接收百度定位信息
        StringBuffer sb = new StringBuffer(256);
        if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
            sb.append(bdLocation.getAddrStr());
            LogUtils.i("bdLocation", "gps定位成功");
        } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
            sb.append(bdLocation.getAddrStr());
            LogUtils.i("bdLocation", "网络定位成功");
        } else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            sb.append(bdLocation.getAddrStr());
            LogUtils.i("bdLocation", "离线定位成功，离线定位结果也是有效的");
        } else if (bdLocation.getLocType() == BDLocation.TypeServerError) {
            LogUtils.i("bdLocation", "服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
            LogUtils.i("bdLocation", "网络不同导致定位失败，请检查网络是否通畅");
        } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {
            LogUtils.i("bdLocation", "无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        }
        LogUtils.i("bdLocation", sb.toString());
        tv_shopWhere.setText(sb.toString());
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
            mLocationClient = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清空静态变量
        filePaths = null;
        //关闭地图定位
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
            mLocationClient = null;
        }
    }

}
