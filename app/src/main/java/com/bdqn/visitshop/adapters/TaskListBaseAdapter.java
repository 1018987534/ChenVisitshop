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
import com.bdqn.visitshop.bean.TaskBody;

import java.util.ArrayList;
import java.util.List;

/**
 * HomeFragment任务列表适配器
 */
public class TaskListBaseAdapter extends RecyclerView.Adapter<TaskListBaseAdapter.TaskViewHolder> {
    private Context mContxt;
    private List<TaskBody> list;
    private int count;

    public TaskListBaseAdapter(Context mContxt, List<TaskBody> list) {
        this.mContxt = mContxt;
        this.list = list;
        if(list==null){
            list = new ArrayList<TaskBody>();
        }
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContxt,R.layout.fragment_home_task_item,null);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, final int position) {/*根据当前position*/
        TaskBody rd = list.get(position);
        holder.title.setText(rd.getTitle());
        if(position==count-1){
            holder.view.setVisibility(View.GONE);
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到任务详情
                Intent intent = new Intent(mContxt, TaskDetailActivity.class);
                intent.putExtra("task", list.get(position));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContxt.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list.size()>0){
            count = 3;
            return 3;
        }else{
            count = 0;
            return 0;
        }
    }

    class TaskViewHolder extends RecyclerView.ViewHolder{
        ImageView arrow;
        TextView title;
        RelativeLayout root;
        View view;

        public TaskViewHolder(View itemView) {
            super(itemView);
            arrow = (ImageView)itemView.findViewById(R.id.fragment_home_task_item_arrow);
            title = (TextView)itemView.findViewById(R.id.fragment_home_task_item_title);
            root = (RelativeLayout) itemView.findViewById(R.id.item_root_home);
            view = itemView.findViewById(R.id.item_home_task);
        }
    }

}
