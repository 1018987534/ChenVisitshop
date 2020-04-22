package com.bdqn.visitshop.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.activity.ShopDetailActivity;
import com.bdqn.visitshop.bean.DateList;

import java.util.ArrayList;
import java.util.List;

/**
 * 巡店列表适配器
 */
/*这边继承RecyclerView.Adapter 同时这边有有个泛型<InfoListBaseAdapter.InfoViewHolder> 这里要实现一个java类 实现好了后传给泛型里面去  然后会让你强制实现三个方法*/
public class VisitShopListAdapter extends RecyclerView.Adapter<VisitShopListAdapter.ShopViewHolder> {
    private Context mContxt;
    private List<DateList> list;/*创建一个数据集*/
/*创建一个构造方法 传入两个参数*/
    public VisitShopListAdapter(Context mContxt, List<DateList> list) {
        this.mContxt = mContxt;
        this.list = list;
    }
/*ShopViewHolder拿到具体每个条目的字段
 * 创建ViewHolder 通过view.inflate()方法来加载每个条目布局 拿到view 同时需要创建一个ShopViewHolder对象 */
    @Override
    public ShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContxt,R.layout.fragment_shop_item,null);
        return new ShopViewHolder(view);
    }
/*onBindViewHolder里面是具体给每个条目设置他的值*/
    @Override
    public void onBindViewHolder(ShopViewHolder holder, final int position) {
        /*这块呢有一个DateList 然后list.get(position)就是从服务端传过来的一个数据集*/
        DateList rd = list.get(position);
        /*这里面设置他相应的展示内容*/
        holder.name.setText("店面名称："+rd.getName());
        holder.where.setText("巡店地址："+rd.getShoplocation());
        /*给每一个条目添加了一个单击事件*/
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContxt,ShopDetailActivity.class);
                intent.putExtra("info",position);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContxt.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
/*自己定义一个ViewHolder来继承RecyclerView.ViewHolder*/
    class ShopViewHolder extends RecyclerView.ViewHolder{
        TextView where,name;
        private RelativeLayout root;
/*拿到这些字段 店铺名称 店铺地址 图标*/
        public ShopViewHolder(View itemView) {
            super(itemView);
            where = (TextView)itemView.findViewById(R.id.activity_visit_shop_item_where);
            name = (TextView)itemView.findViewById(R.id.activity_visit_shop_item_name);
            root = (RelativeLayout) itemView.findViewById(R.id.item_root_rl);
        }
    }

    public List<DateList> getList() {
        return list;
    }

    public void setList(List<DateList> list) {
        this.list = list;
        if(list==null){
            list = new ArrayList<DateList>();
        }
    }
}
