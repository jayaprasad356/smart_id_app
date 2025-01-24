package com.gmwapp.slv_aidi.model;

public class OutsourcePlanModel {

    private String id;
    private String user_id;
    private String plan_id;
    private String income;
    private String claim;
    private String name;
    private String joined_date;
    private String inactive;
    private String image;
    private String demo_video;
    private String monthly_codes;
    private String monthly_earnings;
    private String per_code_cost;
    private String price;
    private String sync_cost;

    // Constructor, getters, and setters
    public OutsourcePlanModel(
            String id,
            String user_id,
            String plan_id,
            String income,
            String joined_date,
            String claim,
            String inactive,
            String name,
            String image,
            String demo_video,
            String monthly_codes,
            String monthly_earnings,
            String per_code_cost,
            String price,
            String sync_cost
    ) {
        this.id = id;
        this.user_id = user_id;
        this.plan_id = plan_id;
        this.income = income;
        this.joined_date = joined_date;
        this.claim = claim;
        this.inactive = inactive;
        this.name = name;
        this.image = image;
        this.demo_video = demo_video;
        this.monthly_codes = monthly_codes;
        this.monthly_earnings = monthly_earnings;
        this.per_code_cost = per_code_cost;
        this.price = price;
        this.sync_cost = sync_cost;
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

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public String getUserId() {
        return user_id;
    }

    public void setPlanId(String plan_id) {
        this.user_id = user_id;
    }

    public String getPlanId() {
        return plan_id;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getIncome() {
        return income;
    }

    public void setJoinedDate(String joined_date) {
        this.joined_date = joined_date;
    }

    public String getJoinedDate() {
        return joined_date;
    }

    public void setClaim(String claim) {
        this.claim = claim;
    }

    public String getClaim() {
        return claim;
    }

    public void setInactive(String inactive) {
        this.inactive = inactive;
    }

    public String getInactive() {
        return inactive;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDemo_video() {
        return demo_video;
    }

    public void setDemo_video(String demo_video) {
        this.demo_video = demo_video;
    }

    public String getMonthly_codes() {
        return monthly_codes;
    }

    public void setMonthly_codes(String monthly_codes) {
        this.monthly_codes = monthly_codes;
    }

    public String getMonthly_earnings() {
        return monthly_earnings;
    }

    public void setMonthly_earnings(String monthly_earnings) {
        this.monthly_earnings = monthly_earnings;
    }

    public String getPer_code_cost() {
        return per_code_cost;
    }

    public void setPer_code_cost(String per_code_cost) {
        this.per_code_cost = per_code_cost;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSyncCost() {
        return sync_cost;
    }

    public void setSyncCost(String sync_cost) {
        this.sync_cost = sync_cost;
    }
}

