package com.bdqn.visitshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.bean.SubmitResult;
import com.bdqn.visitshop.bean.User;
import com.bdqn.visitshop.net.OkHttpManager;
import com.bdqn.visitshop.utils.Constant;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.io.IOException;

import okhttp3.Request;

public class MeDataActivity extends BaseActivity {
    private static final String[] mSexs =
            {"     男      ", "     女      "};
    private TextView mTvName;
    private EditText mEtNickname;
    private EditText mEtPhone;
    private TextView mTvRole;
    private TextView mTvArea;
    private Spinner mSpSex;
    private Button mBtnSubmit;
    private int mSex;
    private String mNickName;
    private String mPhone;
    private User mUser;
    private RelativeLayout mRelLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_data);
        setTitleName("个人资料");
        bindViews();/*初始化布局控件*/
        initSpinner();/*初始化性别选择器*/
        initData();/*初始化数据*/
    }

    /**
     * 初始化数据，显示用户相关信息
     */
    private void initData() {
        mUser = DataSupport.findAll(User.class).get(0);/*从本地数据库打印数据*/
        mTvName.setText(mUser.getUserId());
        mEtNickname.setText(mUser.getNickName());
        mEtPhone.setText(mUser.getPhoneNum());
        mTvRole.setText(mUser.getJob());
        mTvArea.setText(mUser.getArea());
        mSpSex.setSelection(mUser.getSex());
    }

    /**
     * 初始化布局控件
     */
    private void bindViews() {
        mTvName = (TextView) findViewById(R.id.et_name);
        mEtNickname = (EditText) findViewById(R.id.et_nickname);
        mEtPhone = (EditText) findViewById(R.id.et_phone);
        mSpSex = (Spinner) findViewById(R.id.sp_sex);
        mTvRole = (TextView) findViewById(R.id.tv_role);
        mTvArea = (TextView) findViewById(R.id.tv_area);
        mRelLoading = (RelativeLayout) findViewById(R.id.loading);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();/*提交用户资料*/
            }
        });
    }

    /**
     * 初始化性别选择器
     */
    private void initSpinner() {
        ArrayAdapter adapterSex = new ArrayAdapter(this, R.layout.spinner_checked_text, mSexs);
        adapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpSex.setAdapter(adapterSex);
    }

    /**
     * 提交用户资料
     */
    public void submitData() {
        mNickName = mEtNickname.getText().toString().trim();
        mPhone = mEtPhone.getText().toString().trim();
        mSex = mSpSex.getSelectedItemPosition();

        if (checkData()) {
            mRelLoading.setVisibility(View.VISIBLE);
            OkHttpManager.getInstance().postNet(Constant.UpdateUser , new OkHttpManager.ResultCallback() {
                @Override
                public void onFailed(Request request, IOException e) {
                    mRelLoading.setVisibility(View.GONE);
                    Toast.makeText(mContext, "更新失败，服务连接异常，请稍后再试:", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String response) {
                    Gson gson = new Gson();
                    SubmitResult sr = gson.fromJson(response, SubmitResult.class);
                    if (sr.getCode() == 0) {
                        Toast.makeText(mContext, "更新成功", Toast.LENGTH_SHORT).show();

                        mUser.setNickName(mNickName);/*修改本地数据库数据*/
                        mUser.setPhoneNum(mPhone);
                        mUser.setSex(mSex);
                        //更新本地缓存
                        mUser.save();/*添加上去*/
                        mRelLoading.setVisibility(View.GONE);
                      //  startActivity(new Intent(MeDataActivity.this, LoginActivity.class));
                    } else {
                        mRelLoading.setVisibility(View.GONE);
                        Toast.makeText(mContext, "更新失败，请稍后再试:" + sr.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
            },new OkHttpManager.Param("nickName",mNickName),new OkHttpManager.Param("phoneNum",mPhone),new OkHttpManager.Param("sex",Integer.toString(mSex)),new OkHttpManager.Param("userId",mUser.getUserId()));

        }

    }

    /**
     * 数据更新提交校验
     */
    private boolean checkData() {
        mNickName = mEtNickname.getText().toString().trim();
        if (TextUtils.isEmpty(mNickName)) {
            Toast.makeText(getApplicationContext(), "昵称不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(mPhone)) {
            Toast.makeText(getApplicationContext(), "手机号不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        String telRegex = "^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (!mPhone.matches(telRegex)) {
            Toast.makeText(getApplicationContext(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
