package com.bdqn.visitshop.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.activity.CreateVisitShopActivity;
import com.bdqn.visitshop.bean.SelectShop;

import java.util.ArrayList;
import java.util.List;

/**
 * 店铺选择列表适配器
 */
public class SelectShopAdapter extends RecyclerView.Adapter<SelectShopAdapter.SelectViewHolder> {
    private Context mContxt;/*上下文环境*/
    private List<SelectShop.ShopLists> list; /*数据源*/
    private String mSearchText;

    public SelectShopAdapter(Context mContxt, List<SelectShop.ShopLists> list, String mSearchText) {
        this.mContxt = mContxt;
        this.list = list;
        this.mSearchText = mSearchText;
        if(list == null){
           list = new ArrayList<SelectShop.ShopLists>();
        }
    }
/*通过onCreateViewHolder加载条目布局*/
    @Override
    public SelectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContxt,R.layout.select_shop_item,null);
        return new SelectViewHolder(view);
    }
/*设置数据*/
    @Override
    public void onBindViewHolder(SelectViewHolder holder, final int position) {
        SelectShop.ShopLists sl = list.get(position);
        //设置搜索关键字变红色
        int chageTextColor;
        ForegroundColorSpan redSpan = new ForegroundColorSpan(mContxt.getResources().getColor(R.color.red));/*设置前景的颜色 直接通过new的方式指定颜色*/
        SpannableStringBuilder builder = new SpannableStringBuilder(sl.getName());/*通过谷歌提供的SpannableStringBuilder这个接口 通过构建的这个对象 把整体的这行字给它传进来*/
        chageTextColor = sl.getName().indexOf(mSearchText);/*首先通过字符串的indexOf找到你当前想要改变的关键字 在你这个整体字符串的位置*/
        if (chageTextColor != -1) {/*加了一个判断 代表当前你能够找到你这个关键字*/
            builder.setSpan(redSpan, chageTextColor, chageTextColor/*把前景色 下标位置 下标长度 传进来 */
                            + mSearchText.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);/*这个就是是否包括最后一位 这里是不包括*/
            holder.name.setText(builder);/*最后通过setText把这个builder传进来*/
        } else{
            holder.name.setText(sl.getName());
        }holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectShop.ShopLists sl = list.get(position);
                ((CreateVisitShopActivity)mContxt).selectShop(sl);/*调用方法selectShop() 把数据传进去 */
            }
        });

        holder.id.setText("店面编号："+sl.getId());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SelectViewHolder extends RecyclerView.ViewHolder{
        TextView name,id;
        RelativeLayout root;

        public SelectViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.select_shop_item_name);
            id = (TextView)itemView.findViewById(R.id.select_shop_item_id);
            root = (RelativeLayout) itemView.findViewById(R.id.item_root_select);
        }
    }

}
