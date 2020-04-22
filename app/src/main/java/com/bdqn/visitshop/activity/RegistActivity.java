package com.bdqn.visitshop.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import com.bdqn.visitshop.R;
import com.bdqn.visitshop.bean.InterviewResultBody;
import com.bdqn.visitshop.bean.SubmitResult;
import com.bdqn.visitshop.bean.User;
import com.bdqn.visitshop.net.OkHttpManager;
import com.bdqn.visitshop.utils.Constant;
import com.bdqn.visitshop.utils.Utils;
import com.google.gson.Gson;
import org.litepal.crud.DataSupport;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.Request;

public class RegistActivity extends BaseActivity implements View.OnTouchListener {
    private static final String[] mSexs =
            {"     男      ", "     女      "};
    private static final String[] mAreas =
            {"     华北地区      ", "     华中地区      ","     华东地区      ","     华南地区      "};
    private static final String[] mJobs=
            {"     董事长      ","     总经理      ","     总经理秘书      ","     副总经理      ", "     销售经理      ","     采购主管      ","     品质主管      "};
        private EditText mEtUserid;
        private User mInterview;
        private User mUser;
        private Spinner mSpSex;
        private EditText mEtPassword;
        private EditText mEtName;
        private EditText mEtPhone;
        private Spinner mEtJob;
        private Spinner mEtArea;
        private Button mBtnSubmit;
        private int mSex;
        private String mArea;
    private String mJob;
    private TextInputLayout mEtName_design;
    private TextInputLayout mEtPassword_design;
    private String userid;
    private String password;
    private String name;
    private String phone;
    private String job;
    private String area;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_regist);/*拜访查看和添加拜访公用一个界面*/
            setTitleName("注册");


            bindViews();/*拿到EditView输入的数据*/
        initSpinner();/*初始化性别选择器*/
            mBtnSubmit.setOnClickListener(new Utils() {

                    @Override
                    public void forbidClick(View view) {
                        if (checkData()) {/*数据校验*/

                            submitData();/*提交数据*/
                        }
                    }
                });

            }


        /**
         * 向服务器提交数据
         */
        private void submitData() {

            userid = mEtUserid.getText().toString().trim();
            password = mEtPassword.getText().toString().trim();
            name = mEtName.getText().toString().trim();
            phone = mEtPhone.getText().toString().trim();
            mArea = mEtArea.getSelectedItem().toString().trim();
            mJob = mEtJob.getSelectedItem().toString().trim();
            mSex = mSpSex.getSelectedItemPosition();
            User user = new User();
            user.setUserId(userid);
            user.setPassWord(password);
            user.setJob(mJob);
            user.setPhoneNum(phone);
            user.setSex(mSex);
            user.setNickName(name);
            user.setArea(mArea);
            String s = Integer.toString(mSex);

            OkHttpManager.getInstance().postNet(Constant.regist,new OkHttpManager.ResultCallback() {
                @Override
                public void onFailed(Request request, IOException e) {
                    Toast.makeText(getApplicationContext(), "注册失败，请稍后再试", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onSuccess(String response) {
                    SubmitResult sr = getDataFromJson(response);
                    if (sr.getCode() == 0) {
                        //注册成功，关闭当前界面
                        Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "此帐号已经被注册，请更改帐号", Toast.LENGTH_SHORT).show();
                    }
                }
            },new OkHttpManager.Param("userId",userid),new OkHttpManager.Param("passWord",password),new OkHttpManager.Param("job",mJob),new OkHttpManager.Param("phoneNum",phone),new OkHttpManager.Param("nickName",name),new OkHttpManager.Param("area",mArea),new OkHttpManager.Param("sex",s));
        }

        /**
         * 新增拜访数据校验
         * @return
         */
        private boolean checkData() {

             userid = mEtUserid.getText().toString().trim();
             password = mEtPassword.getText().toString().trim();
             name = mEtName.getText().toString().trim();
             phone = mEtPhone.getText().toString().trim();
            if (TextUtils.isEmpty(userid.trim())) {
                mEtUserid.setError("用户名不能为空");
                return false;
            }
            Pattern o = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]{6,16}$");
            Matcher k = o.matcher(userid);

            if (!k.matches()) {
                mEtUserid.setError("帐号请以字母开头");
                return false;
            }

            if (userid.trim().length() <6 || userid.trim().length() >16) {
                mEtUserid.setError("请输入6~16位数的用户名");
                return false;
            }
            if (TextUtils.isEmpty(password.trim())) {
                mEtPassword.setError("密码不能为空");
                return false;
            }
            if (password.trim().length() <8 || password.trim().length() >16) {
                mEtPassword.setError("请输入8~16位数的密码");
                return false;
            }
            if (TextUtils.isEmpty(name.trim())) {
                mEtName.setError("昵称不能为空");
                return false;
            }
            Pattern p = Pattern.compile("^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$");/*手机号的正则表达式 用这个来判断手机号*/
            Matcher m = p.matcher(phone);

            if (!m.matches()) {
                mEtPhone.setError("请输入正确的手机号");
                return false;
            }



            mSex = mSpSex.getSelectedItemPosition();
            mArea = mEtArea.getSelectedItem().toString().trim();
            mJob = mEtJob.getSelectedItem().toString().trim();
            return true;
        }


    /**
     * 初始化性别选择器
     */
    private void initSpinner() {
        ArrayAdapter adapterSex = new ArrayAdapter(this, R.layout.spinner_checked_text, mSexs);
        adapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpSex.setAdapter(adapterSex);
        ArrayAdapter adapterArea = new ArrayAdapter(this, R.layout.spinner_checked_text, mAreas);
        adapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEtArea.setAdapter(adapterArea);
        ArrayAdapter adapterJob = new ArrayAdapter(this, R.layout.spinner_checked_text, mJobs);
        adapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEtJob.setAdapter(adapterJob);
    }
        /**
         * 初始化界面控件
         */
        private void bindViews() {
            mEtUserid = (EditText) findViewById(R.id.et_userid);
            mEtPassword = (EditText) findViewById(R.id.et_password);
            mEtName = (EditText) findViewById(R.id.et_nickname);
            mEtPhone = (EditText) findViewById(R.id.et_phone);
            mEtJob = (Spinner) findViewById(R.id.sp_job);
            mEtArea = (Spinner) findViewById(R.id.rg_area);
            mBtnSubmit = (Button) findViewById(R.id.btn_regist);
            mSpSex = (Spinner) findViewById(R.id.rg_sex);
        }

        /**
         * 返回数据组装
         *
         * @param msg
         * @return
         */
        private SubmitResult getDataFromJson(String msg) {
            Gson gson = new Gson();
            SubmitResult sr = gson.fromJson(msg, SubmitResult.class);
            return sr;
        }


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                case R.id.sp_job:
                case R.id.rg_area:
                    // 解决scrollView中嵌套EditText导致不能上下滑动的问题
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
            }
            return false;
        }

    private class Object {
    }
}


