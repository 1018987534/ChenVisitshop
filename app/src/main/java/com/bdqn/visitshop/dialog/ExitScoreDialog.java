package com.bdqn.visitshop.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.bean.User;
import com.bdqn.visitshop.utils.SharePreUtil;

import org.litepal.crud.DataSupport;

/**
 * 退出Dialog
 */
public class ExitScoreDialog extends BaseDialog implements View.OnClickListener {
    private Button btn_exit, btn_cancel;
    private View view;
    private static final String DBNAME = "visitshop";
    private Activity mActivity;

    public ExitScoreDialog(Context context, int theme, Activity activity) {
        super(context, android.R.style.Theme_Light_NoTitleBar, Gravity.CENTER, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, 1);
        setCancelableOnTouchMenuOutside(false);
        this.mActivity = activity;
    }

    /**
     * 重写这个方法添加自定义view
     *
     * @return view
     */
    @Override
    protected View customPanel() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.dialog_exit, null);
        btn_exit = (Button) linearLayout.findViewById(R.id.btn_exit);
        btn_cancel = (Button) linearLayout.findViewById(R.id.btn_cancel);
        btn_exit.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        return linearLayout;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        view = v;
        dismiss();
    }

    /**
     * dismiss回调
     */
    @Override
    protected void onDismissed() {
        super.onDismissed();
        if (view != null) {
            if (R.id.btn_cancel == view.getId()) {

            } else if (R.id.btn_exit == view.getId()) {
                //注销账户
                DataSupport.deleteAll(User.class);
                SharePreUtil.SetShareString(mContext,"userid","");
                mActivity.finish();
            }
        }
        view = null;
    }

}
