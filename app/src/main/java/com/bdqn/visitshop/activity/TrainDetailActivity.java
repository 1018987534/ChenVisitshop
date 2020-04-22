package com.bdqn.visitshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.bean.TrainDetailResult;
import com.bdqn.visitshop.bean.TrainListBody;
import com.bdqn.visitshop.net.OkHttpManager;
import com.bdqn.visitshop.utils.Constant;
import com.bdqn.visitshop.utils.SharePreUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Request;

/**
 * 培训详情
 */
public class TrainDetailActivity extends BaseActivity {

    private TextView name,id,time,where,teacher;
    private TextView editText;
    private RatingBar ratingBar;
    private LinearLayout gallery;
    private RelativeLayout progress,rl_http_failed;
    private TrainDetailResult.Body trd;
    public static String[] imgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_detil);
        setTitleName("培训详情");
        initView();//初始化视图
        initData();//初始化数据
    }

    private void initData() {
        String userid = SharePreUtil.GetShareString(mContext,"userid");
        TrainListBody rd = (TrainListBody)getIntent().getSerializableExtra("info");
        if(rd!=null){
            name.setText(rd.getTitle());
            id.setText(rd.getTrainid());
            time.setText(rd.getDate());
            where.setText(rd.getLocation());
            teacher.setText(rd.getTrainer());
            //请求其他详情信息
            OkHttpManager.getInstance().getNet(Constant.TrainDetail
                    +"?userid="+userid+"&trainid="+rd.getTrainid(), new OkHttpManager.ResultCallback() {
                @Override
                public void onFailed(Request request, IOException e) {
                    progress.setVisibility(View.GONE);
                    rl_http_failed.setVisibility(View.VISIBLE);
                    Toast.makeText(mContext,R.string.http_failed,Toast.LENGTH_LONG).show();
                }
                @Override
                public void onSuccess(String response) {
                    progress.setVisibility(View.GONE);
                    if(response!=null&&!"".equals(response)){
                        Gson gson = new Gson();
                        TrainDetailResult infos = gson.fromJson(response,TrainDetailResult.class);
                        if(infos!=null){//抛异常
                            if(infos.getBody()!=null){
                                trd = infos.getBody();
                                if(trd!=null){
                                    ratingBar.setRating(Float.parseFloat(trd.getScore()+""));
                                    editText.setText(trd.getFeedback());
                                    try {
                                        imgs = trd.getImgName().split(";");
                                        if(imgs!=null){
                                            gallery.removeAllViews();
                                            gallery.setVisibility(View.VISIBLE);
                                            for (int i=0;i<imgs.length;i++){
                                                //创建一个ImageView
                                                ImageView imageView = new ImageView(mContext);
                                                //设置ImageView的缩放类型
                                                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                                LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(180,220);
                                                layout.leftMargin = 10;
                                                layout.rightMargin = 10;
                                                imageView.setLayoutParams(layout);
                                                //为视图适配图片
                                                Picasso.with(mContext).load(Constant.BaseUrl+trd.getImgPath()+imgs[i]).into(imageView);
                                                gallery.addView(imageView,i);
                                                imgClickListener(imageView,i);
                                            }
                                        }
                                    }catch (Exception e){
                                    }
                                }
                            }
                        }
                    }
                }
            });
            progress.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        progress = (RelativeLayout) this.findViewById(R.id.rl_http_progressbar);
        rl_http_failed = (RelativeLayout) this.findViewById(R.id.rl_http_filed);
        editText = (TextView) this.findViewById(R.id.activity_train_feedback);
        ratingBar = (RatingBar) this.findViewById(R.id.activity_train_score);
        name = (TextView) this.findViewById(R.id.activity_train_name);
        id = (TextView) this.findViewById(R.id.activity_train_id);
        time = (TextView) this.findViewById(R.id.activity_train_time);
        where = (TextView) this.findViewById(R.id.activity_train_where);
        teacher = (TextView) this.findViewById(R.id.activity_train_teacher);
        gallery = (LinearLayout) this.findViewById(R.id.activity_train_gallery);
        rl_http_failed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_http_failed.setVisibility(View.GONE);
                initData();
            }
        });
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
                    intent.putExtra("type", Constant.PhotoLook);
                    intent.putExtra("path", trd.getImgPath());
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imgs = null;
    }

}
