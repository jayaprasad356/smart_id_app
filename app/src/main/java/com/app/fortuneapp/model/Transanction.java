package com.app.fortuneapp.model;

public class Transanction {
    String id,user_id,codes,amount,datetime,type;
    public Transanction(){

    }

    public Transanction(String id, String user_id, String codes, String amount, String datetime, String type) {
        this.id = id;
        this.user_id = user_id;
        this.codes = codes;
        this.amount = amount;
        this.datetime = datetime;
        this.type = type;
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

    public String getCodes() {
        return codes;
    }

    public void setCodes(String codes) {
        this.codes = codes;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
