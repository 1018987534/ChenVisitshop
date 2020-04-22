package com.bdqn.visitshop.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.activity.InterviewDetailActivity;
import com.bdqn.visitshop.bean.InterviewResult;

/**
 * 拜访列表适配器
 */
public class InterviewListAdapter extends RecyclerView.Adapter<InterviewListAdapter.InterViewViewHolder> {

    private Context mContext;
    private InterviewResult mInterview;

    public InterviewListAdapter(Context mContext, InterviewResult mInterview) {
        this.mContext = mContext;
        this.mInterview = mInterview;
        if(mInterview==null){
            mInterview = new InterviewResult();
        }
    }

    public void setmInterview(InterviewResult mInterview) {
        this.mInterview = mInterview;
        if(mInterview==null){
            mInterview = new InterviewResult();
        }
    }

    @Override
    public InterViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.item_interview, null);
        return new InterViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InterViewViewHolder holder, final int position) {
        holder.tvTitle.setText(mInterview.getBody().get(position).getTitle());
        holder.tvDate.setText(mInterview.getBody().get(position).getVisitdate());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到拜访详情界面
                Intent intent = new Intent(mContext, InterviewDetailActivity.class);
                intent.putExtra("body", mInterview.getBody().get(position));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mInterview.getBody()==null){
            return 0;
        }
        return mInterview.getBody().size();
    }

    class InterViewViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle;
        TextView tvDate;
        LinearLayout root;

        public InterViewViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            root = (LinearLayout) itemView.findViewById(R.id.item_root_layout);
        }
    }
}
