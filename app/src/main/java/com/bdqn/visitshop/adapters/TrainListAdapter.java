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
import com.bdqn.visitshop.activity.TrainDetailActivity;
import com.bdqn.visitshop.activity.TrainManageActivity;
import com.bdqn.visitshop.bean.TrainListBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 培训列表适配器
 */
public class TrainListAdapter extends RecyclerView.Adapter<TrainListAdapter.TrainViewHolder> {

    private Context mContext;
    private List<TrainListBody> info_list;

    public TrainListAdapter(Context mContext, List<TrainListBody> info_list) {
        this.mContext = mContext;
        this.info_list = info_list;
        if(info_list==null){
            info_list = new ArrayList<TrainListBody>();
        }
    }

    public void setInfo_list(List<TrainListBody> info_list) {
        this.info_list = info_list;
        if(info_list==null){
            info_list = new ArrayList<TrainListBody>();
        }
    }

    @Override
    public TrainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.fragment_train_item, null);
        return new TrainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrainViewHolder holder, final int position) {
        TrainListBody rd = info_list.get(position);
        holder.name.setText("培训名称："+rd.getTitle());
        holder.id.setText("培训编号："+rd.getTrainid());
        if(rd.getState()==0){//数据临时保存的
            holder.img.setSelected(false);
        }else if(rd.getState()==1){//数据已提交的
            holder.img.setSelected(true);
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrainListBody rd = info_list.get(position);
                int bstate = rd.getState();
                if(bstate==0){
                    Intent intent1 = new Intent(mContext, TrainManageActivity.class);
                    intent1.putExtra("info",rd);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent1);
                }else if(bstate==1){
                    Intent intent2 = new Intent(mContext, TrainDetailActivity.class);
                    intent2.putExtra("info",rd);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent2);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return info_list.size();
    }

    class TrainViewHolder extends RecyclerView.ViewHolder{//适配器变量封装
        TextView name,id;
        ImageView img;
        RelativeLayout root;

        public TrainViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.fragment_visit_item_name);
            id = (TextView)itemView.findViewById(R.id.frament_visit_item_id);
            img = (ImageView) itemView.findViewById(R.id.fragment_visit_item_img);
            root = (RelativeLayout) itemView.findViewById(R.id.item_root_rl);
        }
    }
}
