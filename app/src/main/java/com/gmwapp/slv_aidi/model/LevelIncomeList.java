package com.gmwapp.slv_aidi.model;

public class LevelIncomeList {
    private String id, name, mobile, joined_date, total_earnings, team_income;

    public LevelIncomeList() {
    }

    public LevelIncomeList(String id, String name, String mobile, String joined_date, String total_earnings, String team_income) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.joined_date = joined_date;
        this.total_earnings = total_earnings;
        this.team_income = team_income;
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

    public String getMobileNumber() {
        return mobile;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobile = mobile;
    }

    public String getJoinedDate() {
        return joined_date;
    }

    public void setJoinedDate(String joined_date) {
        this.joined_date = joined_date;
    }

    public String getTotalEarnings() {
        return total_earnings;
    }

    public void setTotalEarnings(String total_earnings) {
        this.total_earnings = total_earnings;
    }

    public String getTeamIncome() {
        return team_income;
    }

    public void setTeamIncome(String team_income) {
        this.team_income = team_income;
    }
}