package com.app.abcdapp.model;

public class Redeem {
    String id,user_id,amount,datetime,status;
    public Redeem(){

    }

    public Redeem(String id, String user_id, String amount, String datetime, String status) {
        this.id = id;
        this.user_id = user_id;
        this.amount = amount;
        this.datetime = datetime;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
