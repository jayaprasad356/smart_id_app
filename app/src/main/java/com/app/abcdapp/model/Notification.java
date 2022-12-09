package com.app.abcdapp.model;

import com.app.abcdapp.Adapter.NotificationAdapter;

import java.util.ArrayList;

public class Notification {
    String id,title,description,datetime;
    public Notification(){

    }

    public Notification(String id, String title, String description, String datetime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.datetime = datetime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}



