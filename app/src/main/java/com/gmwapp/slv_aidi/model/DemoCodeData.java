package com.gmwapp.slv_aidi.model;

public class DemoCodeData {
    private Integer id;
    private String college;
    private String name;
    private String batchNumber;
    private String pass_key;
    private String date;

    // Constructor
    public DemoCodeData(Integer id, String college, String name, String batchNumber, String pass_key, String date) {
        this.id = id;
        this.college = college;
        this.name = name;
        this.batchNumber = batchNumber;
        this.pass_key = pass_key;
        this.date = date;
    }

    public DemoCodeData() {

    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getPass_key() {
        return pass_key;
    }

    public void setPass_key(String pass_key) {
        this.pass_key = pass_key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

