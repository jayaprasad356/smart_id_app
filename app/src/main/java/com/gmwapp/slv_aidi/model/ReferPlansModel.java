package com.gmwapp.slv_aidi.model;


public class ReferPlansModel {
    private String basicPlan, standardPlan, advancedPlan, premiumPlan, freeTrail, mobile, joinedDate;

    public ReferPlansModel() {}

    public ReferPlansModel(String basicPlan, String standardPlan, String advancedPlan, String premiumPlan, String freeTrail, String mobile, String joinedDate) {
        this.basicPlan = basicPlan;
        this.standardPlan = standardPlan;
        this.advancedPlan = advancedPlan;
        this.premiumPlan = premiumPlan;
        this.freeTrail = freeTrail;
        this.mobile = mobile;
        this.joinedDate = joinedDate;
    }

    public String getBasicPlan() {
        return basicPlan;
    }

    public void setBasicPlan(String basicPlan) {
        this.basicPlan = basicPlan;
    }

    public String getStandardPlan() {
        return standardPlan;
    }

    public void setStandardPlan(String standardPlan) {
        this.standardPlan = standardPlan;
    }

    public String getAdvancedPlan() {
        return advancedPlan;
    }

    public void setAdvancedPlan(String advancedPlan) {
        this.advancedPlan = advancedPlan;
    }

    public String getPremiumPlan() {
        return premiumPlan;
    }

    public void setPremiumPlan(String premiumPlan) {
        this.premiumPlan = premiumPlan;
    }

    public String getFreeTrail() {
        return freeTrail;
    }

    public void setFreeTrail(String freeTrail) {
        this.freeTrail = freeTrail;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }
}
