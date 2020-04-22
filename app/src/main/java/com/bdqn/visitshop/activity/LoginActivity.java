package com.bdqn.visitshop.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bdqn.visitshop.MainActivity;
import com.bdqn.visitshop.R;
import com.bdqn.visitshop.bean.LoginBeanResult;
import com.bdqn.visitshop.bean.User;
import com.bdqn.visitshop.net.OkHttpManager;
import com.bdqn.visitshop.utils.CommonUtils;
import com.bdqn.visitshop.utils.Constant;
import com.bdqn.visitshop.utils.SharePreUtil;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Request;

public class LoginActivity extends AppCompatActivity {

    private final int HANDLER_LOGIN = 99;
    private EditText mEtName;
    private EditText mEtPassword;
    private TextInputLayout mEtName_design;
    private TextInputLayout mEtPassword_design;
    private Button mBtnLogin;
    private String mUserName;
    private String mPassWord;
    private RelativeLayout mRelLoading;
    private Context mContext;
    private Button mBtnregist;
    private Button mBtngo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        if (checkLogin()) {/*判断用户是否已经登录*/
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            initStatusBarColor();/*初始化状态栏颜色透明，和背景色一致*/
            bindViews();/*初始化控件*/
            initListener();/*初始化监听器*/
        }
    }

    private boolean checkLogin() {
        //获取数据库用户
        List<User> list = DataSupport.findAll(User.class);
        if (null != list && list.size() > 0) {
            SharePreUtil.SetShareString(mContext, "userid", list.get(0).getUserId());
            return true;
        }
        return false;
    }

    /**
     * 初始化控件
     */
    private void bindViews() {
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mEtName_design = (TextInputLayout) findViewById(R.id.et_name_design);
        mEtPassword_design = (TextInputLayout) findViewById(R.id.et_password_design);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnregist = (Button) findViewById(R.id.btn_regist);
        mBtngo = (Button) findViewById(R.id.btn_go);
        mRelLoading = (RelativeLayout) findViewById(R.id.loading);

        mEtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mEtName_design.setErrorEnabled(false);
            }
        });
        mEtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mEtPassword_design.setErrorEnabled(false);
            }
        });
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        mBtnregist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistActivity.class));

            }
        });
        mBtngo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    /**
     * 登录
     */
    private void login() {
        if (checkData()) {
            mRelLoading.setVisibility(View.VISIBLE);/*弹出正在加载图标*/
            //发送登录请求
            OkHttpManager.getInstance().postNet(Constant.Login, new OkHttpManager.ResultCallback() {
                @Override
                public void onFailed(Request request, IOException e) {
                    mRelLoading.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "服务连接异常，登录失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String response) {
                     LoginBeanResult loginBeanResult = getDataFromJson(response);
                    mRelLoading.setVisibility(View.GONE);
                    if (loginBeanResult.getCode() == 0) {
                        //用户存在，密码正确，登录成功,先保存登录信息，然后跳转至主界面
                        SharePreUtil.SetShareString(mContext, "userid", loginBeanResult.getBody().getUserid());/*保存一个帐号*/
                        //先清除数据库
                        DataSupport.deleteAll(User.class);
                        User user = new User();
                        user.setUserId(loginBeanResult.getBody().getUserid());
                        user.setNickName(loginBeanResult.getBody().getNickname());
                        user.setSex(loginBeanResult.getBody().getSex());
                        user.setJob(loginBeanResult.getBody().getJob());
                        user.setArea(loginBeanResult.getBody().getArea());
                        user.setPhoneNum(loginBeanResult.getBody().getPhonenum());
                        user.setImg(loginBeanResult.getBody().getImg());
                        user.save();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "用户名或密码错误，登录失败", Toast.LENGTH_SHORT).show();
                    }
                }
            },new OkHttpManager.Param("userid", mUserName), new OkHttpManager.Param("password", mPassWord));
        }
    }

    /**
     * 检查登录数据是否合法
     */
    private boolean checkData() {
        mUserName = mEtName.getText().toString().trim();
        mPassWord = mEtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(mUserName.trim())) {
            mEtName_design.setError("用户名不能为空");
            return false;
        }
        if (mUserName.trim().length() <6 || mUserName.trim().length() >16) {
            mEtName_design.setError("请输入6~16位数的用户名");
            return false;
        }
        if (TextUtils.isEmpty(mPassWord)) {
            mEtPassword_design.setError("密码不能为空");
            return false;
        }
        return true;
    }

    /**
     * 初始化状态栏颜色透明，和背景色一致
     */
    private void initStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 返回数据组装
     *
     * @return
     */
    private LoginBeanResult getDataFromJson(String strResult) {
        Gson gson = new Gson();
        LoginBeanResult loginBeanResult = gson.fromJson(strResult, LoginBeanResult.class);
        return loginBeanResult;
    }


}
