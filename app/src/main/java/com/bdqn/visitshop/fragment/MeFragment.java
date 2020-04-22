package com.bdqn.visitshop.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bdqn.settingitemlibrary.SetItemView;
import com.bdqn.visitshop.MainActivity;
import com.bdqn.visitshop.R;
import com.bdqn.visitshop.activity.AboutActivity;
import com.bdqn.visitshop.activity.AppFeedbackActivity;
import com.bdqn.visitshop.activity.LoginActivity;
import com.bdqn.visitshop.activity.MainActivity1;
import com.bdqn.visitshop.activity.MeDataActivity;
import com.bdqn.visitshop.bean.AppInfoResult;
import com.bdqn.visitshop.bean.SubmitResult;
import com.bdqn.visitshop.bean.User;
import com.bdqn.visitshop.dialog.ExitScoreDialog;
import com.bdqn.visitshop.dialog.PopDialog;
import com.bdqn.visitshop.net.OkHttpManager;
import com.bdqn.visitshop.utils.Constant;
import com.bdqn.visitshop.utils.ImageTools;
import com.bdqn.visitshop.utils.LogUtils;
import com.bdqn.visitshop.utils.SDCardUtils;
import com.bdqn.visitshop.utils.SharePreUtil;
import com.google.gson.Gson;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Request;

/**
 * 个人中心-主界面
 */
public class MeFragment extends BaseFragment {
    private ImageView mIvHeader;
    private TextView mTvName;
    private TextView mTvJob;
    private SetItemView mRlMe;
    private SetItemView mRlClear;
    private SetItemView mRlFeedback;
    private SetItemView mRlAbout;
    private SetItemView mRlVersion;
    private SetItemView mRlExit;
    private final int GETVERSION = 100;//版本检测Handler码 成功
    private final int UPDATEHEAD = 99;// 提交照片
    public static final int HttpFail = 7001;//失败
    private final int RESULT_LOAD_IMAGE = 101;//选择照片码
    private String mCurrentVersionCode;//当前客户端版本号
    private View mView;
    private User mUser;
    private RelativeLayout mRelLoading;
    private TextView mLoading;
    private CompressTask compressTask;
    private String mHeadPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //加载布局
        mView = inflater.inflate(R.layout.fragment_me, container, false);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindViews();/*初始化控件*/
        //获取数据库用户
        List<User> list = (List<User>) DataSupport.findAll(User.class);/*拿到本地数据库数据*/
        Log.i("MeFragment","user--"+list.toString());
        if (null != list && list.size() > 0) {/*判断是否登录*/
            mUser = list.get(0);/*获取帐号*/
            mTvName.setText(mUser.getNickName());/*获取姓名*/
            mTvJob.setText(mUser.getJob());/*获取职位*/
            updateHeaderImg();/*更新用户头像方法*/
        } else {/*如果没有登录*/
            mTvName.setText("");/*将设置点击事件*/
            mTvJob.setText("登录");/*将字设置为登录*/
            mTvJob.setTextSize(20);
        }

        mTvName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == mUser) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                }
            }
        });
        mTvJob.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == mUser) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                }
            }
        });
        //获取当前客户端版本号 直接通过getPackageInfo 通过当前APP的包名
        mCurrentVersionCode = getPackageInfo(mContext).versionName;
    }

    /**
     * 更新用户头像
     */
    private void updateHeaderImg() {
        Uri uri = Uri.parse(Constant.BaseUrl + File.separator + mUser.getImg());/*通过接口拿到图片地址*/
        LogUtils.i("photo", "更新头像"+uri);
        //清除缓存
        Picasso.with(mContext).invalidate(uri);/*显示新的头像*/
        Picasso.with(mContext)
                .load(uri)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .placeholder(R.mipmap.defaulthead)
                .error(R.mipmap.defaulthead)
                .into(mIvHeader);
    }

    /**
     * 初始化界面控件
     */
    private void bindViews() {
        mIvHeader = (ImageView) mView.findViewById(R.id.iv_header);
        mTvName = (TextView) mView.findViewById(R.id.tv_name);
        mTvJob = (TextView) mView.findViewById(R.id.tv_job);
        mRlMe = (SetItemView) mView.findViewById(R.id.rl_me);
        mRlClear = (SetItemView) mView.findViewById(R.id.rl_clear);
        mRlFeedback = (SetItemView) mView.findViewById(R.id.rl_feedback);
        mRlAbout = (SetItemView) mView.findViewById(R.id.rl_about);
        mRlVersion = (SetItemView) mView.findViewById(R.id.rl_version);
        mRlExit = (SetItemView) mView.findViewById(R.id.rl_exit);
        mRelLoading = (RelativeLayout) mView.findViewById(R.id.loading);
        mLoading = (TextView) mView.findViewById(R.id.activity_login_progressbar_text);
        initListener();
    }

    private void initListener() {
        //点击头像
        mIvHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i("photo", "选取图片");
                Intent i = new Intent(Intent.ACTION_PICK, null);
                i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*");
                startActivityForResult(i, RESULT_LOAD_IMAGE);/*传一个选择照片码*/
            }
        });
        //点击个人资料
        mRlMe.setmOnSetItemClick(new SetItemView.OnSetItemClick() {
            @Override
            public void click() {
                if (null != mUser) {
                    //之前已经登录过
                    Intent intent = new Intent(mActivity, MeDataActivity.class);
                    startActivity(intent);
                } else {
                    //没有登录，跳转到登录界面
                    startActivity(new Intent(mActivity, LoginActivity.class));
                    mActivity.finish();
                }
            }
        });
        mRlClear.setmOnSetItemClick(new SetItemView.OnSetItemClick() {
            @Override
            public void click() {
                //清除缓存
                clearChache();
            }
        });
        mRlFeedback.setmOnSetItemClick(new SetItemView.OnSetItemClick() {
            @Override
            public void click() {
                //开启反馈Activity
               // startActivity(new Intent(mActivity, AppFeedbackActivity.class));
                startActivity(new Intent(mActivity, MainActivity1.class));
            }
        });
        mRlAbout.setmOnSetItemClick(new SetItemView.OnSetItemClick() {
            @Override
            public void click() {
                //开启关于Activity
                startActivity(new Intent(mActivity, AboutActivity.class));
            }
        });
        mRlVersion.setmOnSetItemClick(new SetItemView.OnSetItemClick() {
            @Override
            public void click() {
                //检测版本
                checkVersion();
            }
        });
        mRlExit.setmOnSetItemClick(new SetItemView.OnSetItemClick() {
            @Override
            public void click() {
                //退出登录
                startActivity(new Intent(mActivity, LoginActivity.class));
                DataSupport.deleteAll(User.class);
                SharePreUtil.SetShareString(mContext,"userid","");
                mActivity.finish();
                //ExitScoreDialog dialog = new ExitScoreDialog(mContext, 0, mActivity);
                //dialog.setTitle("是否退出应用");
                //dialog.show();
            }
        });
    }
    /**
     * 清除缓存
     */
    private void clearChache() {
        String cacheSize = null;
        try {
            //计算缓存大小，格式化显示数据
            cacheSize = SDCardUtils.getTotalCacheSize(mContext);/*Fragment界面*/
            //清除对应缓存
            SDCardUtils.clearAllCache(mContext);/*它需要context类型的*/
            Toast.makeText(mContext, "已清除" + cacheSize + "缓存", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 新版本检测
     */
    private void checkVersion() {
        OkHttpManager.getInstance().getNet(Constant.AppUpdate, new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                meHandler.sendEmptyMessage(HttpFail);
            }
            @Override
            public void onSuccess(String response) {
                Message m = new Message();
                m.obj = response;
                m.what = GETVERSION;
                meHandler.sendMessage(m);
            }
        });
    }

    /**
     * 返回数据组装
     *
     * @param msg
     * @return
     */
    private AppInfoResult getDataFromJson(Message msg) {
        Gson gson = new Gson();
        AppInfoResult appInfo = gson.fromJson(msg.obj.toString(), AppInfoResult.class);
        return appInfo;
    }

    /**
     * 获取包信息
     *
     * @param context
     * @return
     */
    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            //获取包管理器
            PackageManager pm = context.getPackageManager();/*拿到包管理器对象 版本号在gradle里面*/
            //根据报名获取应用包信息
            pi = pm.getPackageInfo(context.getPackageName(),/*通过报名拿到版本号*/
                    PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == mActivity.RESULT_OK && null != data) {/*接收从手机文件中选择的图片*/
            if (data != null) {
                String picturePath = null;
                Uri selectedImage = data.getData();/*拿到图片url 包含图片绝对路径 修改时间 大小 文件名*/
                if (!TextUtils.isEmpty(selectedImage.getAuthority())) {/*判断是否为空*/
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};/*创建一个存储图片绝对路径的数组 获取图片的绝对路径*/
                    Cursor cursor = mActivity.getContentResolver().query(selectedImage,/*用cursor把Uri指向的图片信息查找（query）出来。*/
                            filePathColumn, null, null, null);
                    if (null == cursor) {
                        Toast.makeText(mContext,"图片路径获取为空",Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        cursor.moveToFirst();/*默认查询出来的cursor的初始位置是指向第一条记录的前一个位置的 moveToFirst指向查询结果的第一个位置*/
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);/*取到图片绝对路径*/
                        picturePath = cursor.getString(column_index);/*用String类型的picturePath来接受图片绝对路径*/
                    }
                    cursor.close();
                } else {
                    picturePath = selectedImage.getPath();
                }
                LogUtils.i("photo", "path : " + picturePath);
                //压缩图片
                compressTask = new CompressTask();
                compressTask.execute(picturePath);
            }else{
                Toast.makeText(mContext,"获取设置图片失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Handler meHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HttpFail:/*失败*/
                    Toast.makeText(mContext, "访问服务器失败，请稍后再试", Toast.LENGTH_LONG).show();
                    mRelLoading.setVisibility(View.GONE);
                    break;
                case GETVERSION:/*成功*/
                    AppInfoResult appInfo = getDataFromJson(msg);
                    if (mCurrentVersionCode.compareTo(appInfo.getBody().getVersion())<0) {/*拿到版本号和你当前机器的版本号来进行比对*/
                        //当前版本号小于服务端版本号，有新版本,提示下载更新 然后把新的更新界面抽取成PopDialog
                        PopDialog.showPopwindow(mContext, mActivity, appInfo.getBody().getUpdateinfo(), appInfo.getBody().getAddress());
                    } else {
                        Toast.makeText(mContext, "当前已是最新版本", Toast.LENGTH_LONG).show();
                    }
                    break;
                case UPDATEHEAD:/*提交照片*/
                    Gson gson = new Gson();
                    SubmitResult submitResult = gson.fromJson(msg.obj.toString(), SubmitResult.class);
                    if (submitResult.getCode() == 0) {
                        Toast.makeText(mContext, "头像设置成功", Toast.LENGTH_LONG).show();
                        //头像更新成功后更新本地缓存
                        LogUtils.i("ssssssssss", "wwww111"+submitResult.getMsg());
                        mUser.setImg(submitResult.getMsg());/*set*/
                        ContentValues values = new ContentValues();/*储存数据*/
                        values.put("img",submitResult.getMsg());
                        DataSupport.update(User.class,values,(long) mUser.getId());/*更新img数据*/
                        LogUtils.i("photo", "更新头像1111"+mUser.getImg());
                        List<User> list = (List<User>) DataSupport.findAll(User.class);/*拿到本地数据库数据*/
                        Log.i("MeFragment","更新头像2222--"+list.toString());
                        //更新显示图像
                        mIvHeader.setImageBitmap(BitmapFactory.decodeFile(mHeadPath));
                    } else {
                        Toast.makeText(mContext, "头像设置失败", Toast.LENGTH_LONG).show();
                        mHeadPath = "";
                    }
                    mRelLoading.setVisibility(View.GONE);/*隐藏加载图标*/
                    break;
            }
            return false;
        }
    });

    public void uploadHeadImg(String headPath) {
        //上传图像至服务器
        mHeadPath = headPath;
        OkHttpManager.getInstance().upFileNet(Constant.UpdateHead, new OkHttpManager.ResultCallback() {
            @Override
            public void onFailed(Request request, IOException e) {
                meHandler.sendEmptyMessage(HttpFail);
            }

            @Override
            public void onSuccess(String response) {
                Message m = new Message();
                m.obj = response;
                m.what = UPDATEHEAD;
                meHandler.sendMessage(m);
            }
        },new File[]{new File(headPath)}, "file",
                new OkHttpManager.Param("userid", mUser.getUserId()));/*因为只有一张图片 所以直接把图片传进File数组就可以了 */
    }

    private class CompressTask extends AsyncTask<String, Void, String> {/*压缩图片*/

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoading.setText("图片上传中");
            mRelLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            //压缩图片
            String headPath = "";
            if (SDCardUtils.isSDCardEnable()) {
                headPath = SDCardUtils.getSDCardPath() + File.separator + "visitshop" + File.separator;/*指定一个获取SD卡路径下的visitshop文件夹*/
            } else {
                headPath = SDCardUtils.getRootDirectoryPath() + File.separator + "visitshop" + File.separator;/*获取系统存储路径*/
            }
            File file = new File(headPath);//创建一个file对象 获取该SD卡路径
            if (!file.exists()) {
                file.mkdirs();
            }
            //先将图片压缩保存
            Bitmap bitmap = BitmapFactory.decodeFile(params[0]);
            bitmap = ImageTools.comp(bitmap);/*图片压缩*/
            ImageTools.saveBitmap(bitmap, headPath, File.separator + mUser.getUserId() + ".png");/*通过saveBitmap把压缩完成的图片保存成文件 同时覆盖之前的图片地址*/
            return headPath + File.separator + mUser.getUserId() + ".png";/*返回图片名称的绝对路径*/
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            uploadHeadImg(s);/*上传图像至服务器*/
        }
    }
}
