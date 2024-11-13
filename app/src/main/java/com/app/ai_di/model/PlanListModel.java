package com.app.ai_di.model;

public class PlanListModel {
    private String id;
    private String name;
    private String description;
    private String image;
    private String demo_video;
    private String monthly_codes;
    private String monthly_earnings;
    private String per_code_cost;
    private String price;
    private String type;
    private String min_refers;
    private Integer status;

    // Constructor, getters, and setters
    public PlanListModel(
            String id,
            String name,
            String description,
            String image,
            String demo_video,
            String monthly_codes,
            String monthly_earnings,
            String per_code_cost,
            String price,
            String type,
            String min_refers,
            Integer status
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.demo_video = demo_video;
        this.monthly_codes = monthly_codes;
        this.monthly_earnings = monthly_earnings;
        this.per_code_cost = per_code_cost;
        this.price = price;
        this.type = type;
        this.min_refers = min_refers;
        this.status = status;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMin_refers() {
        return min_refers;
    }

    public void setMin_refers(String min_refers) {
        this.min_refers = min_refers;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

