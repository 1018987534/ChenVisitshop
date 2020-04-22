package com.bdqn.visitshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.bean.SubmitResult;
import com.bdqn.visitshop.bean.TrainListBody;
import com.bdqn.visitshop.fragment.TrainFragment;
import com.bdqn.visitshop.net.OkHttpManager;
import com.bdqn.visitshop.utils.Constant;
import com.bdqn.visitshop.utils.SharePreUtil;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Request;

/**
 * 未提交培训信息详情
 */

public class TrainManageActivity extends BaseActivity implements View.OnClickListener{

    private TextView name,id,time,where,teacher;
    private LinearLayout photo,feedback,submit;
    private String feedback_str;
    private float feedback_star;
    private ArrayList<String> filePaths;
    private TrainListBody rd;
    private RelativeLayout progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_manage);
        setTitleName("培训管理");
        initView();//初始化视图
        initData();//初始化数据
    }

    private void initData() {
        rd = (TrainListBody)getIntent().getSerializableExtra("info");
        if(rd!=null){
            name.setText(rd.getTitle());
            id.setText(rd.getTrainid());
            time.setText(rd.getDate());
            where.setText(rd.getLocation());
            teacher.setText(rd.getTrainer());
        }
    }

    private void initView() {
        if(filePaths==null){
            filePaths = new ArrayList<String>();
        }
        if(feedback_str==null){
            feedback_str = "";
        }
        progress = (RelativeLayout) this.findViewById(R.id.train_manage_activity_progress);
        name = (TextView) this.findViewById(R.id.activity_train_edit_name);
        id = (TextView) this.findViewById(R.id.activity_train_edit_id);
        time = (TextView) this.findViewById(R.id.activity_train_edit_time);
        where = (TextView) this.findViewById(R.id.activity_train_edit_where);
        teacher = (TextView) this.findViewById(R.id.activity_train_edit_teacher);
        photo = (LinearLayout) this.findViewById(R.id.activity_train_edit_photo);
        feedback = (LinearLayout) this.findViewById(R.id.activity_train_edit_feedback);
        submit = (LinearLayout) this.findViewById(R.id.activity_train_edit_submit);
        photo.setOnClickListener(this);
        feedback.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_train_edit_photo://执行拍照功能
                Intent intentPhoto = new Intent(mActivity,PhotoActivity.class);
                intentPhoto.putStringArrayListExtra("filePaths",filePaths);
                startActivityForResult(intentPhoto, Constant.ForResultImageBack);
                break;
            case R.id.activity_train_edit_feedback://执行意见反馈
                Intent intentFeed = new Intent(mActivity,FeedBackActivity.class);
                intentFeed.putExtra("feedback_str",feedback_str);
                intentFeed.putExtra("feedback_star",feedback_star);
                startActivityForResult(intentFeed, Constant.ForResultFeedBack);
                break;
            case R.id.activity_train_edit_submit://执行数据提交
                    if (filePaths.size()>1){
                        //店面数据提交请求封装
                        String userid = SharePreUtil.GetShareString(mContext,"userid");
                        OkHttpManager.Param[] params = new OkHttpManager.Param[]{
                                new OkHttpManager.Param("userId",userid),
                                new OkHttpManager.Param("score",feedback_star+""),
                                new OkHttpManager.Param("feedback",feedback_str),
                                new OkHttpManager.Param("trainId",rd.getTrainid())};
                        File[] files = new File[filePaths.size()];
                        for(int i=0;i<filePaths.size();i++){
                            String sName = filePaths.get(i);
                            File imgFile = new File(sName);
                            files[i] = imgFile;
                        }
                        OkHttpManager.getInstance().upFileNet(Constant.TrainSubmit, new OkHttpManager.ResultCallback() {
                            @Override
                            public void onFailed(Request request, IOException e) {
                                submit.setClickable(true);
                                feedback.setClickable(true);
                                photo.setClickable(true);
                                progress.setVisibility(View.GONE);
                                Toast.makeText(mContext,"提交培训信息失败",Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onSuccess(String response) {
                                submit.setClickable(true);
                                feedback.setClickable(true);
                                photo.setClickable(true);
                                progress.setVisibility(View.GONE);
                                Gson gson = new Gson();
                                SubmitResult sr = gson.fromJson(response, SubmitResult.class);
                                if(sr.getCode()==0){
                                    TrainFragment.isFirst = true;
                                    Toast.makeText(mContext,"提交培训信息成功",Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    Toast.makeText(mContext,R.string.please_resubmit,Toast.LENGTH_SHORT).show();
                                }
                            }
                        },files,"file",params);
                        submit.setClickable(false);
                        feedback.setClickable(false);
                        photo.setClickable(false);
                        progress.setVisibility(View.VISIBLE);
                    }else{
                        Toast.makeText(mContext,"请为本次培训拍照",Toast.LENGTH_SHORT).show();
                    }
                    if(feedback_star<1){
                    Toast.makeText(mContext,"请为本次培训提出您的宝贵意见",Toast.LENGTH_SHORT).show();
                    }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {//判断接收消息
            return;
        }
        switch (requestCode){
            case Constant.ForResultFeedBack://得到意见反馈返回信息
                if(data!=null){
                    Bundle bundle = data.getExtras();
                    feedback_str = bundle.getString("feedback");
                    feedback_star = bundle.getFloat("star");
                }
                break;
            case Constant.ForResultImageBack://得到现场拍照返回信息
                if(data!=null){
                    Bundle bundle = data.getExtras();
                    filePaths = bundle.getStringArrayList("filepaths");
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清空静态变量
        filePaths = null;
        feedback_str = null;
        feedback_star = 0;
    }
}
