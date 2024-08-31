package com.app.smart_id_maker.model;

public class Referandearn {
    String id,name,refer_code,mobile,balance;

    public Referandearn(String id, String name, String refer_code, String mobile, String balance) {
        this.id = id;
        this.name = name;
        this.refer_code = refer_code;
        this.mobile = mobile;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRefer_code() {
        return refer_code;
    }

    public void setRefer_code(String refer_code) {
        this.refer_code = refer_code;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
