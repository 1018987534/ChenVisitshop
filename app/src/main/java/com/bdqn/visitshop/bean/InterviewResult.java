package com.bdqn.visitshop.bean;

import java.util.List;

public class InterviewResult {



    private int code;
    private String msg;


    private List<InterviewResultBody> body;

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

    public List<InterviewResultBody> getBody() {
        return body;
    }

    public void setBody(List<InterviewResultBody> body) {
        this.body = body;
    }
}
