package com.bdqn.visitshop.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.bean.SubmitResult;
import com.bdqn.visitshop.bean.User;
import com.bdqn.visitshop.net.OkHttpManager;
import com.bdqn.visitshop.utils.Constant;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Request;

/**
 * APP意见反馈
 */
public class AppFeedbackActivity extends BaseActivity {

    private Button mBtnSubmit;
    private EditText mEtFeedback;
    private SubmitResult mSubmitResult;
    private User mUser;
    private RelativeLayout mRelLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_feedback);
        setTitleName("意见反馈");

        mBtnSubmit = (Button) findViewById(R.id.btn_submit);
        mEtFeedback = (EditText) findViewById(R.id.et_feedback);
        mRelLoading = (RelativeLayout) findViewById(R.id.loading);
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnSubmit.setEnabled(false);
                String feedback = mEtFeedback.getText().toString();
                if (TextUtils.isEmpty(feedback)) {
                    Toast.makeText(getApplicationContext(), "您未输入反馈意见", Toast.LENGTH_LONG).show();
                    return;
                }
                if (feedback.length() > 200) {
                    Toast.makeText(getApplicationContext(), "反馈意见不能超过200字", Toast.LENGTH_LONG).show();
                    return;
                }
                //获取数据库用户
                List<User> list = DataSupport.findAll(User.class);
                if (null != list && list.size() > 0) {
                    mUser = list.get(0);
                }
                //组装提交参数
                OkHttpManager.Param[] params;
                if (null == mUser) {
                    params = new OkHttpManager.Param[]{
                            new OkHttpManager.Param("userid", ""),
                            new OkHttpManager.Param("feedback", feedback)};
                } else {
                    params = new OkHttpManager.Param[]{
                            new OkHttpManager.Param("userid", mUser.getUserId()),
                            new OkHttpManager.Param("feedback", feedback)};
                }
                mRelLoading.setVisibility(View.VISIBLE);/*显示加载图标*/
                OkHttpManager.getInstance().postNet(Constant.FeedBack, new OkHttpManager.ResultCallback() {
                    @Override
                    public void onFailed(Request request, IOException e) {
                        mRelLoading.setVisibility(View.GONE);
                        mBtnSubmit.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "服务连接异常,稍后再试", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(String response) {
                        mSubmitResult = getDataFromJson(response);/*拿到服务端提交返回数据*/
                        mBtnSubmit.setEnabled(true);/*按钮可以点击*/
                        mRelLoading.setVisibility(View.GONE);/*干掉加载图标*/
                        if (mSubmitResult.getCode() == 0) {
                            Toast.makeText(getApplicationContext(), "提交成功", Toast.LENGTH_LONG).show();
                            finish();/*返回*/
                        } else {
                            Toast.makeText(getApplicationContext(), "提交异常，请稍后再试", Toast.LENGTH_LONG).show();
                        }
                    }
                },params);/*传入数组*/
            }
        });
    }

    /**
     * 返回数据组装
     *
     * @param msg
     * @return
     */
    private SubmitResult getDataFromJson(String msg) {
        Gson gson = new Gson();
        SubmitResult info = gson.fromJson(msg, SubmitResult.class);
        return info;
    }
}
