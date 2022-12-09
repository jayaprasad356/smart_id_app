package com.app.abcdapp.model;

public class GenerateCodes {
    String id,student_name,id_number,ecity,pin_code;

    public GenerateCodes() {

    }

    public GenerateCodes(String id, String student_name, String id_number, String ecity, String pin_code) {
        this.id = id;
        this.student_name = student_name;
        this.id_number = id_number;
        this.ecity = ecity;
        this.pin_code = pin_code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public String getEcity() {
        return ecity;
    }

    public void setEcity(String ecity) {
        this.ecity = ecity;
    }

    public String getPin_code() {
        return pin_code;
    }

    public void setPin_code(String pin_code) {
        this.pin_code = pin_code;
    }
}
