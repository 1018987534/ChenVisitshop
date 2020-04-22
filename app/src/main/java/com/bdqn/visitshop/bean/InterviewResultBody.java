package com.bdqn.visitshop.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class InterviewResultBody extends DataSupport implements Serializable{
    private int id;
    private String title;
    private String visitdate;
    private String person;
    private String phone;
    private String company;
    private String job;
    private String aim;
    private String summary;

    public InterviewResultBody() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVisitdate() {
        return visitdate;
    }

    public void setVisitdate(String visitdate) {
        this.visitdate = visitdate;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getAim() {
        return aim;
    }

    public void setAim(String aim) {
        this.aim = aim;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
