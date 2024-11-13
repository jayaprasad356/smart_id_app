package com.app.ai_di.model;

public class DemoCodeData {
    private String id;
    private String schoolName;
    private String studentName;
    private String rollNumber;
    private String dateOfBirth;

    // Constructor
    public DemoCodeData(String id, String schoolName, String studentName, String rollNumber, String dateOfBirth) {
        this.id = id;
        this.schoolName = schoolName;
        this.studentName = studentName;
        this.rollNumber = rollNumber;
        this.dateOfBirth = dateOfBirth;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}

