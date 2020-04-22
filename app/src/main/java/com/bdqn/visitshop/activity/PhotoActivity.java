package com.bdqn.visitshop.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.utils.CacheFileUtils;
import com.bdqn.visitshop.utils.Constant;
import com.bdqn.visitshop.utils.ImageTools;

import java.io.File;
import java.util.ArrayList;

/**
 * 培训拍照
 */

public class PhotoActivity extends BaseActivity implements View.OnClickListener{

    private LinearLayout gallery;
    private Button btn;
    public static ArrayList<String> filePaths;
    private String fileName;
    private ImageView save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        setTitleName("现场拍照");
        initView();//初始化视图
        initData();//初始化数据
    }

    private void initData() {
        filePaths = getIntent().getStringArrayListExtra("filePaths");
    }
    private void initView() {
        filePaths = new ArrayList<String>();
        gallery = (LinearLayout) this.findViewById(R.id.photo_activity_gallery);
        btn = (Button) this.findViewById(R.id.photo_activity_btn);
        btn.setOnClickListener(this);
        save = (ImageView) this.findViewById(R.id.title_bar_save);
        save.setVisibility(View.VISIBLE);
        save.setOnClickListener(this);
    }
/*最终显示图片*/
    @Override
    public void onResume() {
        super.onResume();
        if(filePaths!=null){
            gallery.removeAllViews();/*布局里的图片清空*/
            gallery.setVisibility(View.VISIBLE);
            for (int i=0;i<filePaths.size();i++){
                //创建一个ImageView
                ImageView imageView = new ImageView(mContext);
                //设置ImageView的缩放类型
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(180,220);
                layout.leftMargin = 10;
                layout.rightMargin = 10;
                imageView.setLayoutParams(layout);
                //为视图适配图片
               // if(i!=filePaths.size()){
                    File file = new File(filePaths.get(i));
                    if(file.exists()){
                        Bitmap bitmap = BitmapFactory.decodeFile(filePaths.get(i));
                        imageView.setImageBitmap(bitmap);
                    }else{
                        imageView.setImageResource(R.mipmap.default_img);
                    }
             //   }else{
            //        imageView.setImageResource(R.mipmap.default_img);
            //    }
                gallery.addView(imageView,i);
                imgClickListener(imageView,i);
            }
        }
    }

    /*
     * 图片点击查看事件
     */
    private void imgClickListener(View imagView, final int position){
        imagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filePaths==null){
                    filePaths = new ArrayList<String>();
                }
              //  if(position<filePaths.size()){
                    Intent intent = new Intent(mContext,ImageViewActivity.class);
                    intent.putExtra("type", Constant.PhotoUp);
                    intent.putExtra("position", position);
                    startActivity(intent);
              //  }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_bar_save:
                if(filePaths.size()>=2){
                    if(filePaths.size()<=4){
                        //得到第一张和最后一张图片的创建时间
                        int last = filePaths.size()-1;
                        String start = filePaths.get(0).split("_bdqn")[1];
                        long stime = Long.parseLong(start.split(".j")[0]);
                        String end = filePaths.get(last).split("_bdqn")[1];
                        long etime = Long.parseLong(end.split(".j")[0]);
                        //求取图片创建时间差是否大于1分钟
                        long between = etime-stime;
                        long betweenThree = 1000;
                        if(between>betweenThree){
                            Intent intentBack = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("filepaths", filePaths);
                            intentBack.putExtras(bundle);
                            mActivity.setResult(RESULT_OK, intentBack);/*将数据传回到上一个界面*/
                            finish();
                        }else{
                            Toast.makeText(mContext,"第一张和最后一张时间间隔不得少于1分钟",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(mContext,"拍照不得多于4张",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(mContext,"拍照不得少于2张，为培训开始一张和培训结束一张",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.photo_activity_btn://执行拍照功能
                if(filePaths==null){
                    filePaths = new ArrayList<String>();
                }
                if(filePaths.size()<4){
                    goTakePhoto();
                }else{
                    Toast.makeText(mContext, "最多拍摄4张照片", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 调用手机拍照功能
     */
    protected void goTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileName = CacheFileUtils.getUpLoadPhotosPath();
        Uri uri = Uri.fromFile(new File(fileName));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.Images.ImageColumns.ORIENTATION, 0);
        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {//判断接收数据
            return;
        }
        switch (requestCode) {
            case 1001://接收拍照返回数据
                if(!TextUtils.isEmpty(fileName)){
                    Bitmap bitmap = ImageTools.convertToBitmap(fileName, 640, 640);
                    Bitmap bitmapComp = ImageTools.comp(bitmap);
                    ImageTools.saveBitmap(bitmapComp, fileName);
                    if(bitmap!=null) {
                        filePaths.add(fileName);
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清空静态变量
        filePaths = null;
    }
}
