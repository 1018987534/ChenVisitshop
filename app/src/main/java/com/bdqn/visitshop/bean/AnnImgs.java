package com.bdqn.visitshop.bean;

import org.litepal.crud.DataSupport;

public class AnnImgs extends DataSupport{
    private int id;
    private String imgUrl;
    private String detailUrl;


    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

}
