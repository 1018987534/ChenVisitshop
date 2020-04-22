package com.bdqn.visitshop.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 培训列表适配器
 */
public class TrainListBody extends DataSupport implements Serializable{
    private int id;
    private int state;
    private String date;
    private String title;
    private String location;
    private String trainer;
    private String trainid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTrainer() {
        return trainer;
    }

    public void setTrainer(String trainer) {
        this.trainer = trainer;
    }

    public String getTrainid() {
        return trainid;
    }

    public void setTrainid(String trainid) {
        this.trainid = trainid;
    }

    @Override
    public String toString() {
        return "Body{" +
                "id=" + id +
                ", state=" + state +
                ", date='" + date + '\'' +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", trainer='" + trainer + '\'' +
                ", trainid='" + trainid + '\'' +
                '}';
    }
}
