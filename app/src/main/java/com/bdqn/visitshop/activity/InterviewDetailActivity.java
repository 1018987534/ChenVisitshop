package com.bdqn.visitshop.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.bean.InterviewResultBody;
import com.bdqn.visitshop.bean.SubmitResult;
import com.bdqn.visitshop.bean.User;
import com.bdqn.visitshop.net.OkHttpManager;
import com.bdqn.visitshop.utils.Constant;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Request;

/**
 * 拜访详情activity
 */
public class InterviewDetailActivity extends BaseActivity implements View.OnTouchListener {

    private EditText mEtTitle;
    private EditText mEtName;
    private EditText mEtPhone;
    private EditText mEtCompany;
    private EditText mEtJob;
    private EditText mEtAim;
    private EditText mEtSummary;
    private Button mBtnSubmit;
    private InterviewResultBody mInterview;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_detail);/*拜访查看和添加拜访公用一个界面*/
        setTitleName("拜访");

        //获取拜访实体对象数据
        Object object = getIntent().getSerializableExtra("body");
        bindViews();/*拿到EditView输入的数据*/
        //获取数据库用户
        List<User> list = (List<User>) DataSupport.findAll(User.class);
        if (null != list && list.size() > 0) {
            mUser = list.get(0);
        }
        if (null == object) {/*如果拿到的数据为空  代表是新增拜访*/
            //新增拜访
            mBtnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkData()) {/*数据校验*/
                        submitData();/*提交数据*/
                    }
                }
            });

        } else {
            //拜访查看
            InterviewResultBody bodyBean = (InterviewResultBody) object;/*用封装好的bean 把数据封装进去*/
            //查看详情，数据不可编辑
            disableView();
            //返显填充数据
            initData(bodyBean);
        }
    }

    /**
     * 向服务器提交数据
     */
    private void submitData() {
        if (null == mUser) {
            Toast.makeText(mActivity.getApplicationContext(), "您尚未登录,无法提交", Toast.LENGTH_SHORT).show();
            return;
        }
        //店面数据提交请求封装 这里把数据提交也封装到OkHttpManager这个接口里面 首先声明一个数组Param[][ˌpærəˈm]  然后添加接口文档里的相应的数据
        OkHttpManager.Param[] params = new OkHttpManager.Param[]{
                new OkHttpManager.Param("title", mInterview.getTitle()),
                new OkHttpManager.Param("person", mInterview.getPerson()),
                new OkHttpManager.Param("phone", mInterview.getPhone()),
                new OkHttpManager.Param("company", mInterview.getCompany()),
                new OkHttpManager.Param("job", mInterview.getJob()),
                new OkHttpManager.Param("aim", mInterview.getAim()),
                new OkHttpManager.Param("summary", mInterview.getSummary()),
                new OkHttpManager.Param("userId", mUser.getUserId())};
        OkHttpManager.getInstance().postNet(Constant.InterviewSubmit, new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                Toast.makeText(getApplicationContext(), "提交失败，请稍后再试", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(String response) {
                SubmitResult sr = getDataFromJson(response);
                if (sr.getCode() == 0) {
                    //提交成功，关闭当前界面
                    Toast.makeText(getApplicationContext(), "提交成功", Toast.LENGTH_SHORT).show();
                    mActivity.setResult(RESULT_OK);/*传一个返回码 如果是从首页点进去的 就回到首页调用onActivityResult()方法来跳转到巡店界面 如果是巡店界面点加号进去的 就直接返回就可以了*/
                    finish();//调用了finish()，接下来使用进行onActivityResult方法 回调 返回至历史拜访界面
                } else {
                    Toast.makeText(getApplicationContext(), "提交失败，请稍后再试", Toast.LENGTH_SHORT).show();
                }
            }
        },params);
    }

    /**
     * 新增拜访数据校验
     * @return
     */
    private boolean checkData() {
        mInterview = new InterviewResultBody();/*创建一个新的 封装好的bean 集合*/
        String title = mEtTitle.getText().toString();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(getApplicationContext(), "拜访标题不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (title.length() > 32) {
            Toast.makeText(getApplicationContext(), "拜访标题不能超过20个字", Toast.LENGTH_SHORT).show();
            return false;
        }
        String name = mEtName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "拜访人姓名不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (name.length() > 16) {
            Toast.makeText(getApplicationContext(), "拜访人姓名不能超过16个字", Toast.LENGTH_SHORT).show();
            return false;
        }
        String phone = mEtPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(getApplicationContext(), "联系方式不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        Pattern p = Pattern.compile("^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$");/*手机号的正则表达式 用这个来判断手机号*/
        Matcher m = p.matcher(phone);

        if (!m.matches()) {
            mEtPhone.setError("请输入正确的手机号");
            return false;
        }
        String company = mEtCompany.getText().toString();
        if (TextUtils.isEmpty(company)) {
            Toast.makeText(getApplicationContext(), "公司不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (company.length() > 32) {
            Toast.makeText(getApplicationContext(), "公司名不能超过16个字", Toast.LENGTH_SHORT).show();
            return false;
        }
        String job = mEtJob.getText().toString();
        if (TextUtils.isEmpty(job)) {
            Toast.makeText(getApplicationContext(), "拜访人职务不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (job.length() > 16) {
            Toast.makeText(getApplicationContext(), "拜访人职务不能超过16个字", Toast.LENGTH_SHORT).show();
            return false;
        }
        String aim = mEtAim.getText().toString();
        if (TextUtils.isEmpty(aim)) {
            Toast.makeText(getApplicationContext(), "拜访目的不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (aim.length() > 200) {
            Toast.makeText(getApplicationContext(), "拜访目的不能超过200字", Toast.LENGTH_SHORT).show();
            return false;
        }
        String summary = mEtSummary.getText().toString();
        if (TextUtils.isEmpty(summary)) {
            Toast.makeText(getApplicationContext(), "拜访总结不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (summary.length() > 500) {
            Toast.makeText(getApplicationContext(), "拜访总结不能超过500字", Toast.LENGTH_SHORT).show();
            return false;
        }
        //填充待提交数据
        mInterview.setTitle(title);
        mInterview.setPerson(name);
        mInterview.setPhone(phone);
        mInterview.setCompany(company);
        mInterview.setJob(job);
        mInterview.setAim(aim);
        mInterview.setSummary(summary);
        return true;
    }

    /**
     * 控制控件不可输入
     */
    private void disableView() {
        mBtnSubmit.setEnabled(false);
        mEtTitle.setEnabled(false);
        mEtName.setEnabled(false);
        mEtPhone.setEnabled(false);
        mEtCompany.setEnabled(false);
        mEtJob.setEnabled(false);
        mEtAim.setEnabled(false);
        mEtSummary.setEnabled(false);
        mBtnSubmit.setVisibility(View.GONE);
    }

    /**
     * 显示之前拜访数据
     *
     * @param bodyBean
     */
    private void initData(InterviewResultBody bodyBean) {
        mEtTitle.setText(bodyBean.getTitle());
        mEtName.setText(bodyBean.getPerson());
        mEtPhone.setText(bodyBean.getPhone());
        mEtCompany.setText(bodyBean.getCompany());
        mEtJob.setText(bodyBean.getJob());
        mEtAim.setText(bodyBean.getAim());
        mEtSummary.setText(bodyBean.getSummary());
    }

    /**
     * 初始化界面控件
     */
    private void bindViews() {
        mEtTitle = (EditText) findViewById(R.id.et_title);
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtPhone = (EditText) findViewById(R.id.et_phone);
        mEtCompany = (EditText) findViewById(R.id.et_company);
        mEtJob = (EditText) findViewById(R.id.et_job);
        mEtAim = (EditText) findViewById(R.id.et_aim);
        mEtSummary = (EditText) findViewById(R.id.et_summary);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit);
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
            case R.id.et_aim:
            case R.id.et_summary:
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
}
