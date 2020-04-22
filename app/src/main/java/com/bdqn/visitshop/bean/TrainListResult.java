package com.bdqn.visitshop.bean;

import java.io.Serializable;
import java.util.List;

public class TrainListResult implements Serializable{
    private int code;//请求返回码，0为成功
    private String msg;//请求放回信息
    private List<TrainListBody> body;//请求数据数组

    @Override
    public String toString() {
        return "TrainListResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", body=" + body +
                '}';
    }

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

    public List<TrainListBody> getBody() {
        return body;
    }

    public void setBody(List<TrainListBody> body) {
        this.body = body;
    }
}
