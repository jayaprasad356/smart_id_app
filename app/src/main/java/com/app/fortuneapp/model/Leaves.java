package com.app.fortuneapp.model;

public class Leaves {
    String id,date,type,user_id,reason,status;
    public Leaves(){

    }

    public Leaves(String id, String date, String type, String user_id, String reason, String status) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.user_id = user_id;
        this.reason = reason;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
