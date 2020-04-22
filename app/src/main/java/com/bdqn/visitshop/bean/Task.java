package com.bdqn.visitshop.bean;

import java.util.List;

public class Task {


    private int code;
    private String msg;

    private List<TaskBody> body;

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

    public List<TaskBody> getBody() {
        return body;
    }

    public void setBody(List<TaskBody> body) {
        this.body = body;
    }
}
