package com.app.ai_di.model;

public class LeaveListModel {
    String id, datetime, status, description;
    public LeaveListModel(){

    }

    public LeaveListModel(String id, String datetime, String status, String description) {
        this.id = id;
        this.status = status;
        this.datetime = datetime;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
