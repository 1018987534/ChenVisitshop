package com.bdqn.visitshop.bean;

import java.util.List;

public class InfoResult {



    private int code;
    private String msg;


    private List<InfoResultBody> body;

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

    public List<InfoResultBody> getBody() {
        return body;
    }

    public void setBody(List<InfoResultBody> body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "InfoResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", body=" + body +
                '}';
    }
}
