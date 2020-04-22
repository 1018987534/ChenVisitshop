package com.bdqn.visitshop.dialog;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.service.UpdateService;
import com.bdqn.visitshop.utils.Constant;
import com.bdqn.visitshop.utils.SDCardUtils;

import java.io.File;

public class PopDialog {


    /**
     * 显示提示升级信息界面 调取Popwindow 方法 Popwindow是可以自定义布局的
     */
    public static void showPopwindow(final Context context, final Activity activity, String updateInfo, final String downloadUrl) {
        // 利用layoutInflater获得View
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View popView = inflater.inflate(R.layout.pop_updateapp, null);/*通过inflate拿到布局*/
        final PopupWindow window = new PopupWindow(popView,/*定义一个PopupWindow对象 给它设置相应的宽高属性*/
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        // 这里设置一些常规的属性 比如说设置popWindow弹出窗体可点击
        window.setFocusable(true);/*获取按钮焦点*/
        window.setOutsideTouchable(true);/*点击窗体外面可以将窗体关闭*/
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);/*实现背景半透明状态*/
        window.setBackgroundDrawable(dw);/*半透明设置到window里面*/
        //设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.mypopwindow_anim_style);
        //更新显示信息
        TextView tvUpdateInfo = (TextView) popView.findViewById(R.id.tv_updateinfo);/*拿到版本更新备注布局*/
        tvUpdateInfo.setText(updateInfo.replaceAll(";", "\n"));/*吧；号换成换行符*/
        Button btnCancel = (Button) popView.findViewById(R.id.btn_cancel);/*取消按钮*/
        Button btnUpdate = (Button) popView.findViewById(R.id.btn_update);/*更新按钮*/
        btnCancel.setOnClickListener(new View.OnClickListener() {/*点击取消按钮发生事件*/
            @Override
            public void onClick(View v) {
                changeLightclose(activity);/*恢复背景方法*/
                window.dismiss();/*直接把当前window关闭*/
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {/*点击立即更新按钮*/
            @Override
            public void onClick(View v) {
                String savePath;
                //检测SD卡是否可用，如果可用，在SD卡创建文件保存路径，如果不能用，则保存在应用目录下
                if (SDCardUtils.isSDCardEnable()) {/*如果可以用 就保存到web存储下面*/
                    savePath = SDCardUtils.getSDCardPath() + File.separator + "visitshop" + File.separator;
                } else {/*如果不可用 就保存到根目录下面*/
                    savePath = SDCardUtils.getRootDirectoryPath() + File.separator + "visitshop" + File.separator;
                }
                File file = new File(savePath);/*拿到目录*/
                if (!file.exists()) {
                    //文件目录不存在，则创建
                    file.mkdirs();
                }
                window.dismiss();
                //背景恢复
                changeLightclose(activity);

                //下载APK文件 通过Intent启动Service让它在后台运行
                Intent intent = new Intent(context, UpdateService.class);
                intent.putExtra("url", Constant.BaseUrl+downloadUrl);/*把服务端的apk包传进来*/
                intent.putExtra("path", savePath + "visitshop.apk");/*传进本地保存的地址*/
                activity.startService(intent);
            }
        });

        // 在底部显示
        window.showAtLocation(activity.findViewById(R.id.root_layout),
                Gravity.BOTTOM, 0, 0);
        //改变背景亮度
        changeLightShow(activity);

        //popWindow消失监听方法
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                changeLightclose(activity);
                System.out.println("popWindow消失");
            }
        });
    }

    /**
     * 背景变暗,并增加渐变动画
     */
    public static void changeLightShow(Activity activity) {
        final ValueAnimator animation = ValueAnimator.ofFloat(1.0f, 0.6f);
        animation.setDuration(200);
        animation.start();
        final Window window = activity.getWindow();
        final WindowManager.LayoutParams lp = window.getAttributes();
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lp.alpha = (float) valueAnimator.getAnimatedValue();
                window.setAttributes(lp);
            }
        });
    }

    /**
     * 恢复背景,并增加渐变动画
     */
    public static void changeLightclose(Activity activity) {
        final ValueAnimator animation = ValueAnimator.ofFloat(0.6f, 1.0f);
        animation.setDuration(200);
        animation.start();
        final Window window = activity.getWindow();
        final WindowManager.LayoutParams lp = window.getAttributes();/*通过LayoutParams参数可以设置它的透明度alpha*/
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lp.alpha = (float) valueAnimator.getAnimatedValue();
                window.setAttributes(lp);
            }
        });
    }

}
