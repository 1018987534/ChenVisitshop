package com.bdqn.visitshop.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.activity.CreateVisitShopActivity;
import com.bdqn.visitshop.bean.ShopList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 未提交巡店列表适配器
 */
public class ShopListAdapter extends RecyclerView.Adapter<ShopListAdapter.ShopViewHolder> {
    private Context mContxt;
    public List<ShopList> list;

    public ShopListAdapter(Context mContxt, List<ShopList> list) {
        this.mContxt = mContxt;
        this.list = list;
        if(list==null){
            list = new ArrayList<ShopList>();
        }
        Collections.reverse(list);/*反向排序*/
    }

    @Override
    public ShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContxt,R.layout.fragment_shop_item,null);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShopViewHolder holder, final int position) {
        ShopList rd = list.get(position);
        holder.name.setText("店面名称："+rd.getName());
        holder.where.setText("巡店地址："+rd.getShoplocation());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContxt, CreateVisitShopActivity.class);
                intent.putExtra("info",list.get(position));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContxt.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ShopViewHolder extends RecyclerView.ViewHolder{
        TextView where,name;
        RelativeLayout root;

        public ShopViewHolder(View itemView) {
            super(itemView);
            where = (TextView)itemView.findViewById(R.id.activity_visit_shop_item_where);
            name = (TextView)itemView.findViewById(R.id.activity_visit_shop_item_name);
            root = (RelativeLayout) itemView.findViewById(R.id.item_root_rl);
        }
    }

    public List<ShopList> getList() {
        return list;
    }

    public void setList(List<ShopList> list) {
        this.list = list;
        if(list==null){
            list = new ArrayList<ShopList>();
        }
        Collections.reverse(list);
    }
}
