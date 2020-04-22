package com.bdqn.visitshop.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.bean.TaskBody;

/**
 * 任务详情
 */
public class TaskDetailActivity extends BaseActivity {

    private TextView mTv_title;
    private TextView mTv_date;
    private TextView mTv_state;
    private TextView mTv_desc;
    private TaskBody mTaskBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        setTitleName("任务详情");
        //获取任务详情数据
        mTaskBody = (TaskBody) getIntent().getSerializableExtra("task");
        bindViews();
        if (null != mTaskBody) {
            initShow();
        }
    }

    /**
     * 加载任务详情数据
     */
    private void initShow() {
        mTv_title.setText(mTaskBody.getTitle());
        mTv_date.setText(mTaskBody.getExecutedate());
        mTv_state.setText(mTaskBody.getState() == 1 ? "已过期" : "进行中");
        mTv_desc.setText(mTaskBody.getDetail());
    }

    /**
     * 初始化控件
     */
    private void bindViews() {
        mTv_title = (TextView) findViewById(R.id.tv_title);
        mTv_date = (TextView) findViewById(R.id.tv_date);
        mTv_state = (TextView) findViewById(R.id.tv_state);
        mTv_desc = (TextView) findViewById(R.id.tv_desc);
    }
}
