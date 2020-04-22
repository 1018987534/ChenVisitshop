package com.bdqn.visitshop.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.bean.FeedBackResult;
import com.bdqn.visitshop.utils.Constant;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by 陈德瑶 on 20/4/20.
 */
public class MsgAdapter extends ArrayAdapter<FeedBackResult.BodyBean> {
    private int resourceId;
    private Context mContext;

    public MsgAdapter(Context context, int textViewResourceId, List<FeedBackResult.BodyBean> objects) {
        super(context, textViewResourceId, objects);
        this.mContext = context;
        resourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        FeedBackResult.BodyBean msg = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = (LinearLayout)view.findViewById(R.id.left_layout);
            viewHolder.rightLayout = (LinearLayout)view.findViewById(R.id.right_layout);
            viewHolder.leftMsg = (TextView)view.findViewById(R.id.left_msg);
            viewHolder.rightMsg = (TextView)view.findViewById(R.id.right_msg);
            viewHolder.rightTime = (TextView)view.findViewById(R.id.right_time);
            viewHolder.leftTime = (TextView)view.findViewById(R.id.left_time);
            viewHolder.head1 = (ImageView)view.findViewById(R.id.head_left);
            viewHolder.head2 = (ImageView)view.findViewById(R.id.head_right);
            if(!"".equals(msg.getImg())&&msg.getImg()!=null){
                Picasso.with(getContext()).load(Constant.BaseUrl + File.separator +msg.getImg()).into(viewHolder.head2);
            }
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if(msg.getFeedbackType() == Msg.TYPE_RECEIVED) {
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.head1.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.head2.setVisibility(View.GONE);
            viewHolder.leftMsg.setText(msg.getReplyContent());
            viewHolder.leftTime.setText(msg.getInsertDt());

        } else if(msg.getFeedbackType() == Msg.TYPE_SEND) {
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.head2.setVisibility(View.VISIBLE);
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.head1.setVisibility(View.GONE);
            viewHolder.rightMsg.setText(msg.getReplyContent());
            viewHolder.rightTime.setText(msg.getInsertDt());

        }
        return view;
    }

    class ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        TextView rightTime;
        TextView leftTime;
        ImageView head1;
        ImageView head2;
    }
}
