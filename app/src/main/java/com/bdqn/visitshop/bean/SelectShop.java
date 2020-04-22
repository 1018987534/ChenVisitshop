package com.bdqn.visitshop.bean;

import java.util.List;

public class SelectShop {
    private int code;//请求返回码，0为成功
    private String msg;//请求放回信息
    private List<ShopLists> shoplists;//请求数据数组

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<ShopLists> getShoplists() {
        return shoplists;
    }

    public void setShoplists(List<ShopLists> shoplists) {
        this.shoplists = shoplists;
    }

    @Override
    public String toString() {
        return "SelectShop{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", shoplists=" + shoplists +
                '}';
    }

    public class ShopLists{
        private String id;//店面id
        private String name;//店面名字

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "ShopLists{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
