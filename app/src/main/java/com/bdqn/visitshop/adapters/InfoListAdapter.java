package com.bdqn.visitshop.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.activity.InfoDetailActivity;
import com.bdqn.visitshop.bean.InfoResult;
import com.squareup.picasso.Picasso;

/**
 * 资讯列表适配器
 */
public class InfoListAdapter extends RecyclerView.Adapter<InfoListAdapter.InfoViewHolder> {

    private Context mContext;
    private InfoResult mInfo;

    public InfoListAdapter(Context mContext, InfoResult mInfo) {
        this.mContext = mContext;
        this.mInfo = mInfo;
        if(mInfo==null){
            mInfo = new InfoResult();
        }
    }

    public void setmInfo(InfoResult mInfo) {
        this.mInfo = mInfo;
        if(mInfo==null){
            mInfo = new InfoResult();
        }
    }

    @Override
    public InfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext,R.layout.item_info,null);
        return new InfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InfoViewHolder holder, final int position) {
        holder.tvTitle.setText(mInfo.getBody().get(position).getTitle());/*因为现在mInfo还不是表单类型需要使用getBody()方法来转换一下 才能显示到布局里面去*/
        holder.tvDesc.setText(mInfo.getBody().get(position).getSummary());
            String imgUrl = mInfo.getBody().get(position).getImgurl();
            if(imgUrl!=null&&!"".equals(imgUrl)){
                Picasso.with(mContext).load(imgUrl).into(holder.iv);
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, InfoDetailActivity.class);
                intent.putExtra("url", mInfo.getBody().get(position).getDetail());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mInfo.getBody()==null){
            return 0;
        }
        return mInfo.getBody().size();
    }

    class InfoViewHolder extends RecyclerView.ViewHolder{
        ImageView iv;
        TextView tvTitle;
        TextView tvDesc;
        LinearLayout root;

        public InfoViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv_info);
            tvDesc = (TextView) itemView.findViewById(R.id.tv_desc);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            root = (LinearLayout) itemView.findViewById(R.id.item_root_layout);
        }
    }
}
