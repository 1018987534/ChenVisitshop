package com.bdqn.visitshop.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.bdqn.visitshop.R;

/**
 * Fragment基类
 */

public class BaseFragment extends Fragment {

    protected Context mContext;/*创建Context对象 环境*/
    protected Activity mActivity;/*Activity环境*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mActivity = getActivity();
    }
/*跳转activity给它添加一个动画效果*/
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.activity_in, 0);
    }
    
}
