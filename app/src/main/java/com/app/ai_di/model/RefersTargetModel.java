package com.app.ai_di.model;

public class RefersTargetModel {
    private String id;
    private String title;
    private String refer_count;
    private String bonus;

    public RefersTargetModel(
            String id,
            String title,
            String refer_count,
            String bonus
    ) {
        this.id = id;
        this.title = title;
        this.refer_count = refer_count;
        this.bonus = bonus;
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

    public String getReferCount() {
        return refer_count;
    }

    public void setReferCount(String refer_count) {
        this.refer_count = refer_count;
    }

    public String getBonus() {
        return bonus;
    }

    public void setBonus(String bonus) {
        this.bonus = bonus;
    }
}