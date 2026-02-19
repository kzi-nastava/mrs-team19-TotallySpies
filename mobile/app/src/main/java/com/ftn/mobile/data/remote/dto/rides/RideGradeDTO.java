package com.ftn.mobile.data.remote.dto.rides;

public class RideGradeDTO {
    private String email;
    private int grade;
    private String gradeType;

    public RideGradeDTO(String email, int grade, String gradeType) {
        this.email = email;
        this.grade = grade;
        this.gradeType = gradeType;
    }

    public RideGradeDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getGradeType() {
        return gradeType;
    }

    public void setGradeType(String gradeType) {
        this.gradeType = gradeType;
    }
}
