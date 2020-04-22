package com.bdqn.visitshop.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bdqn.visitshop.R;
import com.bdqn.visitshop.utils.Constant;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/*实现一个单击事件 监听接口*/
public class ImageViewActivity extends BaseActivity implements View.OnClickListener{

    private ImageView left,right;
    private PhotoView img;
    private Button delete;
    private List<String> list;
    private String[] imgs;
    private int mposition;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        setTitleName("图片查看");
        initView();//初始化视图
        initData();//初始化数据
    }

    private void initData() {
        mposition = (Integer) getIntent().getIntExtra("position",-1);
        type = (Integer) getIntent().getIntExtra("type",-1);
        ShowImage(mposition,type);/*进行判断*/
    }

    private void initView() {
        img = (PhotoView) this.findViewById(R.id.image_view_img);
        right = (ImageView) this.findViewById(R.id.image_view_right);
        left = (ImageView) this.findViewById(R.id.image_view_left);
        delete = (Button) this.findViewById(R.id.image_view_detele);
        right.setOnClickListener(this);/*分别添加监听事件*/
        left.setOnClickListener(this);
        delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_view_left://浏览上一张图片
                if(mposition>0){/*根据下标来判断是不是第一张图片*/
                    mposition--;/*不是第一张就下标减1*/
                    ShowImage(mposition,type);/*调用显示图片*/
                }
                break;
            case R.id.image_view_right://浏览下一张图片
                if(list==null){
                    list = new ArrayList<String>();
                }
                if(mposition<list.size()-1&&mposition>-1){/*判断是不是达到集合的最大*/
                    mposition++;/*如果不是最后一张就下标加1*/
                    ShowImage(mposition,type);
                }
                break;
            case R.id.image_view_detele://删除图片
                if(list!=null){/*先判断集合是否为空*/
                    if(list.size()>0){/*判断是不是有数据*/
                        if(list.size()==1){
                            File file = new File(list.get(0));/*将图片集合申明为file类型*/
                            switch (type) {
                                case Constant.PhotoUp:/*培训拍照图片处理*/
                                    PhotoActivity.filePaths.clear();
                                    break;
                                case Constant.ShopImgUp:/*巡店拍照图片处理*/
                                    CreateVisitShopActivity.filePaths.clear();/*把相应的地址移除掉*/
                                    break;
                            }
                            if(file.exists()){
                                file.delete();/*把硬盘图片文件也删除掉*/
                            }
                            Toast.makeText(mContext,"删除成功!",Toast.LENGTH_SHORT).show();
                            finish();/*关闭界面*/
                        }else{
                            File file = new File(list.get(mposition));/*申明为file类型 通过下标拿到相应图片地址*/
                            switch (type) {
                                case Constant.PhotoUp:/*培训拍照图片处理*/
                                    PhotoActivity.filePaths.remove(mposition);
                                    break;
                                case Constant.ShopImgUp:/*巡店拍照图片处理*/
                                    CreateVisitShopActivity.filePaths.remove(mposition);/*把相应的下标对应的图片地址移除掉*/
                                    break;
                            }
                            if(file.exists()){
                                file.delete();/*把硬盘图片文件也删除掉 如果没删掉就有垃圾缓存了*/
                            }
                            Toast.makeText(mContext,"删除成功!",Toast.LENGTH_SHORT).show();
                            if(mposition==list.size()){/*如果删除的是最后一张图片*/
                                mposition--;/*下标减1*/
                                ShowImage(mposition,type);
                            }else{
                                ShowImage(mposition,type);
                            }
                        }
                    }
                }
                break;
        }
    }
/*显示图片*/
    public void ShowImage(int positon,int type){/*拿到类型和下标*/
        if(type>0&&positon>=0){/*首先判断类型大于0 并且 下标大于等于0*/
            switch (type){/*根据type来进行判断*/
                case Constant.ShopImgLook:/*巡店完成查看详情*/
                    delete.setVisibility(View.GONE);/*把删除图标隐藏掉*/
                    imgs = ShopDetailActivity.imgs;
                    String shopImgPath = getIntent().getStringExtra("path");
                    if(imgs!=null&&shopImgPath!=null&&!"".equals(shopImgPath)){
                        if(list==null){
                            list = new ArrayList<String>();
                            for(int i = 0;i<imgs.length;i++){
                                list.add(imgs[i]);
                            }
                        }
                        if(positon==list.size()-1){/*如果是从最后一张图片进去的*/
                            right.setSelected(true);
                        }else{
                            right.setSelected(false);
                        }
                        if(positon==0){
                            left.setSelected(true);
                        }else{
                            left.setSelected(false);
                        }
                        String pathStr = list.get(positon);
                        //为视图适配图片
                        Picasso.with(mContext).load(Constant.BaseUrl+shopImgPath+pathStr).into(img);
                    }
                    break;
                case Constant.PhotoLook://培训图片查看
                    delete.setVisibility(View.GONE);
                    imgs = TrainDetailActivity.imgs;
                    String path = getIntent().getStringExtra("path");
                    if(imgs!=null&&path!=null&&!"".equals(path)){
                        if(list==null){
                            list = new ArrayList<String>();
                            for(int i = 0;i<imgs.length;i++){
                                list.add(imgs[i]);
                            }
                        }
                        if(positon==list.size()-1){
                            right.setSelected(true);
                        }else{
                            right.setSelected(false);
                        }
                        if(positon==0){
                            left.setSelected(true);
                        }else{
                            left.setSelected(false);
                        }
                        String pathStr = list.get(positon);
                        //为视图适配图片
                        Picasso.with(mContext).load(Constant.BaseUrl+path+pathStr).into(img);
                    }
                    break;
                case Constant.PhotoUp://拍照上传查看图片
                    list = PhotoActivity.filePaths;
                    if(list==null){
                        list = new ArrayList<String>();
                    }
                    if(positon==list.size()-1){
                        right.setSelected(true);
                    }else{
                        right.setSelected(false);
                    }
                    if(positon==0){
                        left.setSelected(true);
                    }else{
                        left.setSelected(false);
                    }
                    String bm = list.get(positon);
                    File file = new File(bm);
                    if(file.exists()){
                        Bitmap bitmap = BitmapFactory.decodeFile(bm);
                        img.setImageBitmap(bitmap);
                    }else{
                        list.remove(positon);
                        Toast.makeText(mContext,R.string.please_into_userinfo,Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constant.ShopImgUp://巡店图片上传查看
                    list = CreateVisitShopActivity.filePaths;/*将创建好的图片列表给它赋值 这里用了一个静态变量filePaths*/
                    if(list==null){
                        list = new ArrayList<String>();
                    }
                    if(positon==list.size()-1){/*如果是最后一张图*/
                        right.setSelected(true);
                    }else{
                        right.setSelected(false);
                    }
                    if(positon==0){/*如果是第一张图*/
                        left.setSelected(true);
                    }else{
                        left.setSelected(false);
                    }
                    String shopImg = list.get(positon);/*然后通过list.get(positon)拿到当前图片下标对应的图片地址 代表要显示第几张图片*/
                    File shopFile = new File(shopImg);/*然后申明成一个File*/
                    if(shopFile.exists()){/*判断是否为空*/
                        Bitmap bitmap = BitmapFactory.decodeFile(shopImg);/*通过decodeFile去解析这个文件拿到bitmap*/
                        img.setImageBitmap(bitmap);/*最后设置给imageview*/
                    }else{
                        list.remove(positon);/*通过下标把这个图片地址移除掉*/
                        Toast.makeText(mContext,R.string.please_into_userinfo,Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }else{
            img.setImageResource(R.mipmap.default_img);/*如果图片有问题 就设置成默认图片*/
        }
    }

}
