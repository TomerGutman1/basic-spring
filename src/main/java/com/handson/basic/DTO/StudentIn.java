package com.handson.basic.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.handson.basic.model.Student;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDate;

import static com.handson.basic.model.Student.StudentBuilder.aStudent;

public class StudentIn implements Serializable {

    @Size(max = 60)
    private String fullname;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Min(100)
    @Max(800)
    private Integer satScore;

    @Min(30)
    @Max(110)
    private Double graduationScore;

    @Size(max = 20)
    private String phone;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getSatScore() {
        return satScore;
    }

    public void setSatScore(Integer satScore) {
        this.satScore = satScore;
    }

    public Double getGraduationScore() {
        return graduationScore;
    }

    public void setGraduationScore(Double graduationScore) {
        this.graduationScore = graduationScore;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Student toStudent() {
        return aStudent()
                .fullname(fullname)
                .birthDate(birthDate)           // now LocalDate
                .satScore(satScore)
                .graduationScore(graduationScore)
                .phone(phone)
                .build();
    }

    public void updateStudent(Student target) {
        target.setFullname(fullname);
        target.setBirthDate(birthDate);
        target.setSatScore(satScore);
        target.setGraduationScore(graduationScore);
        target.setPhone(phone);
    }
}

