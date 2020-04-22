package com.bdqn.visitshop.bean;

import org.litepal.crud.DataSupport;

import java.util.Date;

public class Feedback extends DataSupport {
    private int id;
    private String userId;
    private Date date;
    private String feedBack;
    private String feedbackType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(String feedBack) {
        this.feedBack = feedBack;
    }

    public String getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", date=" + date +
                ", feedBack='" + feedBack + '\'' +
                ", feedbackType='" + feedbackType + '\'' +
                '}';
    }
}
