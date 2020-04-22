package com.bdqn.visitshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bdqn.visitshop.R;

/**
 * 意见反馈
 */

public class FeedBackActivity extends BaseActivity {

    private ImageView save;
    private EditText et;
    private RatingBar ratingBar;
    private String feedback_str;
    private float feedback_star;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        setTitleName("培训反馈");
        initView();//初始化视图
        initData();//初始化数据
    }

    private void initData() {
        //得到上个activity传来的数据，并展示
        feedback_str = getIntent().getStringExtra("feedback_str");
        feedback_star = getIntent().getFloatExtra("feedback_star",0);
        if(feedback_star>0){
            ratingBar.setRating(feedback_star);
        }
        if(!"".equals(feedback_str)){
            et.setText(feedback_str);
        }
    }

    private void initView() {
        ratingBar = (RatingBar) this.findViewById(R.id.activity_train_edit_score);
        et = (EditText) this.findViewById(R.id.activity_train_edit_et);
        save = (ImageView) this.findViewById(R.id.title_bar_save);
        save.setVisibility(View.VISIBLE);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float star = ratingBar.getRating();
                if(star<1){
                    Toast.makeText(mContext,"请为本次培训打分",Toast.LENGTH_SHORT).show();
                }else{
                    String str = et.getText().toString().trim();
                    if(!"".equals(str)&&!"请输入你对培训的意见和建议，不超过1000字".equals(str)){
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putFloat("star", star);
                        bundle.putString("feedback", str);
                        intent.putExtras(bundle);
                        mActivity.setResult(RESULT_OK, intent);
                        finish();
                    }else{
                        Toast.makeText(mContext,"请为本次培训提出您的宝贵意见",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}
