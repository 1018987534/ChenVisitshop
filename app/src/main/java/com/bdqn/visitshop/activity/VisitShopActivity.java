package com.bdqn.visitshop.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.adapters.ShopListAdapter;
import com.bdqn.visitshop.bean.ShopList;
import com.bdqn.visitshop.utils.SharePreUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 巡店临时保存数据展示类
 */

public class VisitShopActivity extends BaseActivity{

    private RecyclerView recyclerView;
    private ShopListAdapter adapter;
    private List<ShopList> db_list;
    private TextView none;
    private String userid;//用户id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_shop);
        setTitleName("未提交巡店");
        initView();//初始化视图
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();//初始化数据
    }

    private void initView() {
        userid = SharePreUtil.GetShareString(mContext,"userid");
        none = (TextView) findViewById(R.id.fragment_visitshop_none);
        recyclerView = (RecyclerView) findViewById(R.id.fragment_visitshop_list);
        //设置展示风格
        recyclerView.setLayoutManager(
                new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    private void initData() {
        //获取数据库信息
        List<ShopList> db_lists = DataSupport.findAll(ShopList.class);
        if(db_list==null){
            db_list = new ArrayList<ShopList>();
        }
        if(db_lists==null){
            db_lists = new ArrayList<ShopList>();
        }else{
            db_list.clear();
            //筛选出此用户的本地保存数据
            for(int i=0;i<db_lists.size();i++){
                ShopList db_sl = db_lists.get(i);
                if(userid.equals(db_sl.getUserid())){
                    db_list.add(db_sl);
                }
            }
        }
        if(db_list.size()>0){
            none.setVisibility(View.GONE);
            adapter = new ShopListAdapter(mContext,db_list);
            recyclerView.setAdapter(adapter);
        }else if(db_list.size()==0){
            none.setVisibility(View.VISIBLE);
        }
    }

}
