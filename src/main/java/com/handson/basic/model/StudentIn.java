package com.handson.basic.model;


import com.fasterxml.jackson.annotation.JsonFormat;
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