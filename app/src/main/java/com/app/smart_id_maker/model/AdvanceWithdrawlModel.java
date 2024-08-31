package com.app.smart_id_maker.model;

public class AdvanceWithdrawlModel {

    private String amount, type, date;

    public AdvanceWithdrawlModel(String amount, String type, String date) {
        this.amount = amount;
        this.type = type;
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
