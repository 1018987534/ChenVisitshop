package com.bdqn.visitshop.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.activity.TaskDetailActivity;
import com.bdqn.visitshop.bean.Task;
import com.bdqn.visitshop.utils.Constant;
import com.bdqn.visitshop.utils.LogUtils;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {

    private Context mContext;
    private Task mTask;

    public TaskListAdapter(Context mContext, Task mTask) {
        this.mContext = mContext;
        this.mTask = mTask;
    }

    public void setmTask(Task task) {
        if(task==null){
            task = new Task();
        }
        this.mTask = task;
    }

    @Override
    public TaskListAdapter.TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.item_task, null);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, final int position) {
        holder.tv_title.setText("任务名称:" + mTask.getBody().get(position).getTitle());
        holder.tv_date.setText("发布时间:" + mTask.getBody().get(position).getPublishdate());
        LogUtils.i("LEON", "done1:" + mTask.getBody().get(position).getState());

        if (mTask.getBody().get(position).getState() == Constant.TASK_DONE) {
            holder.iv_state.setImageResource(R.mipmap.task_pass);
        }else {
            holder.iv_state.setImageResource(R.mipmap.task_ing);
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到任务详情
                Intent intent = new Intent(mContext, TaskDetailActivity.class);
                intent.putExtra("task", mTask.getBody().get(position));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mTask.getBody()==null){
            return 0;
        }
        return mTask.getBody().size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_date;
        ImageView iv_state;
        ImageView iv_arraw;
        RelativeLayout root;

        public TaskViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            iv_state = (ImageView) itemView.findViewById(R.id.iv_state);
            iv_arraw = (ImageView) itemView.findViewById(R.id.iv_arraw);
            root = (RelativeLayout) itemView.findViewById(R.id.task_root_layout);
        }
    }
}
