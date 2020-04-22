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
import com.bdqn.visitshop.activity.InfoDetailActivity;
import com.bdqn.visitshop.bean.InfoResultBody;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * HomeFragment资讯列表适配器
 */
/*这边继承RecyclerView.Adapter 同时这边有有个泛型<InfoListBaseAdapter.InfoViewHolder> 这里要实现一个ViewHolder 其实就是一个java类 实现好了后传给泛型里面去  然后会让你强制实现三个方法*/
public class InfoListBaseAdapter extends RecyclerView.Adapter<InfoListBaseAdapter.InfoViewHolder> {
    private Context mContext;
    private List<InfoResultBody> list;
    int number;

    public InfoListBaseAdapter(Context mContxt, List<InfoResultBody> list,int number) {
        this.mContext = mContxt;
        this.list = list;
        this.number = number;
        if(list==null){
            list = new ArrayList<InfoResultBody>();
        }
    }
    /*创建ViewHolder对象  通过view.inflate()方法来加载每个条目布局 拿到view */
    @Override
    public InfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext,R.layout.fragment_home_info_item,null);
        return new InfoViewHolder(view);
    }
/*创建onBindViewHolder 有绑定的意思 这里就是做具体的数据处理 get(position)拿到每个条目的对象*/
    @Override
    public void onBindViewHolder(InfoViewHolder holder, final int position) {
        InfoResultBody rd = list.get(position);
        /*给文本框设置标题 getTitle()*/
        holder.title.setText(rd.getTitle());
        /*这个是简介*/
        holder.context.setText(rd.getSummary());
        /*先判断图片是不是为空 不为空就使用Picasso把他加载一下*/
        if(!"".equals(rd.getImgurl().trim())&&rd.getImgurl()!=null){
            Picasso.with(mContext).load(rd.getImgurl()).into(holder.img);
        }
        /*这里root就是给根布局添加了一个单击事件*/
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到资讯详情
                Intent intent = new Intent(mContext, InfoDetailActivity.class);
                intent.putExtra("url",list.get(position).getDetail());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }
/*getItemCount()就是得到想要展示的列表数目 这个number就是通过构造方法传回来的*/
    @Override
    public int getItemCount() {
        if(list.size()>0){
            return number;
        }else{
            return 0;
        }
    }
/*继承 RecyclerView.ViewHolder 会让你强制实现一个构造方法 InfoViewHolder*/
    class InfoViewHolder extends RecyclerView.ViewHolder{
        /*先声明你要操作的控件*/
        ImageView img,arrow;
        TextView title,context;
        RelativeLayout root;
/*然后里面会传入一个view对象itemView 这个就是每一个条目的view对象 一个图片 两行文字 这个布局生成的view对象他就默认帮你返回好了*/
        public InfoViewHolder(View itemView) {
            super(itemView);
            /*然后在这里去找到控件的同时使用这个itemView对象 .findViewById就可以找到控件对象了*/
            arrow = (ImageView)itemView.findViewById(R.id.fragment_home_info_item_arrow);
            img = (ImageView)itemView.findViewById(R.id.fragment_home_info_item_img);
            context = (TextView)itemView.findViewById(R.id.fragment_home_info_item_context);
            title = (TextView)itemView.findViewById(R.id.fragment_home_info_item_title);
            root = (RelativeLayout) itemView.findViewById(R.id.item_root_home);
        }
    }

}
