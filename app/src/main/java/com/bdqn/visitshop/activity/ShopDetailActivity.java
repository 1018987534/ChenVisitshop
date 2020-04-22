package com.bdqn.visitshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.bean.DateList;
import com.bdqn.visitshop.fragment.ShopFragment;
import com.bdqn.visitshop.utils.Constant;
import com.bdqn.visitshop.utils.LogUtils;
import com.squareup.picasso.Picasso;

/**
 * 巡店详情查看
 */

public class ShopDetailActivity extends BaseActivity {

    private RatingBar rb_shop,rb_person,rb_product;
    private TextView tv_shopId,tv_shopWhere,ed_suggest;
    private EditText et_shopName;
    private LinearLayout gallery;
    public static String[] imgs;
    private DateList rd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        setTitleName("巡店详情");
        initView();//初始化视图
        initData();//初始化数据
    }

    private void initData() {
        int position = (int) getIntent().getIntExtra("info",-1);
            if(position>=0){
            rd = ShopFragment.info_list.get(position);
        }
        if(rd!=null){
            et_shopName.setText(rd.getName());//设置店面名称
            tv_shopId.setText(rd.getShopid());//设置店面id
            tv_shopWhere.setText(rd.getShoplocation());//设置店面地址
            try {//抛异常
                String[] str = rd.getShoplevel().split(";");//拆分成数组
                rb_shop.setRating(Float.parseFloat(str[0]));
                rb_person.setRating(Float.parseFloat(str[1]));
                rb_product.setRating(Float.parseFloat(str[2]));
                ed_suggest.setText(rd.getFeedback());
                imgs = rd.getImgname().split(";");//拆分成数组
                if(imgs!=null){
                    gallery.removeAllViews();/*先把图片清空*/
                    gallery.setVisibility(View.VISIBLE);/*显示出来*/
                    for (int i=0;i<imgs.length;i++){/*根据保存的地址去动态创建图片*/
                        //通过动态的方式去创建imageView对象
                        ImageView imageView = new ImageView(mContext);
                        //设置ImageView的缩放类型 这里是伸缩 填充XY轴
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(180,220);/*设置宽和高*/
                        layout.leftMargin = 10;/*设置图片的左间距*/
                        layout.rightMargin = 10;/*设置图片的右间距*/
                        imageView.setLayoutParams(layout);/*把这些属性设置给imageView*/
                        //为视图适配图片 设置它的图片源
                        Picasso.with(mContext).load(Constant.BaseUrl+rd.getImgpath()+imgs[i]).into(imageView);
                        gallery.addView(imageView,i);/*gallery这个是LinearLayout  然后addView动态添加 添加的时候要注意顺序 就是新拍的要放到后面去 所以引入了i*/
                        imgClickListener(imageView,i);/*点击图片有一个事件 也是抽象出一个方法 当前图片下标i也传进去*/
                    }
                }
            }catch (Exception e){
            }
        }
    }

    private void initView() {
        rb_shop = (RatingBar) this.findViewById(R.id.shopdetail_activity_score_shop);
        rb_person = (RatingBar) this.findViewById(R.id.shopdetail_activity_score_person);
        rb_product = (RatingBar) this.findViewById(R.id.shopdetail_activity_score_product);
        tv_shopId = (TextView) this.findViewById(R.id.shopdetail_activity_shopid);
        tv_shopWhere = (TextView) this.findViewById(R.id.shopdetail_activity_shopwhere);
        ed_suggest = (TextView) this.findViewById(R.id.shopdetail_activity_feedback);
        et_shopName = (EditText) this.findViewById(R.id.shopdetail_activity_shopname);
        gallery = (LinearLayout) this.findViewById(R.id.shopdetail_activity_gallery);

    }

    /*
     * 图片点击查看事件
     */
    private void imgClickListener(View imagView, final int position){
        imagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imgs!=null){
                    Intent intent = new Intent(mContext,ImageViewActivity.class);
                    intent.putExtra("type", Constant.ShopImgLook);
                    intent.putExtra("path", rd.getImgpath());
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清空静态变量
        imgs = null;
    }

}
