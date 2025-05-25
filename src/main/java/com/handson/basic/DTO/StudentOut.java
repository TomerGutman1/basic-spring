// --- Update 1: DTO class ---
package com.handson.basic.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.handson.basic.model.StudentGrade;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.SqlResultSetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@SqlResultSetMapping(name = "StudentOut")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentOut {

    @Id
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdat;

    private String fullname;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    private Integer satscore;
    private Double graduationscore;
    private String phone;
    private String profilepicture;
    private Double avgscore; // changed type + kept consistent casing


    public Double getAvgscore() {
        return avgscore;
    }

    public void setAvgscore(Double avgscore) {
        this.avgscore = avgscore;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDateTime createdat) {
        this.createdat = createdat;
    }

    public String getFullname() {
        return fullname;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public Integer getSatscore() {
        return satscore;
    }
    public void setSatscore(Integer satscore) {
        this.satscore = satscore;
    }

    public Double getGraduationscore() {
        return graduationscore;
    }
    public void setGraduationscore(Double graduationscore) {
        this.graduationscore = graduationscore;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfilepicture() {
        return profilepicture;
    }
    public void setProfilepicture(String profilepicture) {
        this.profilepicture = profilepicture;
    }
}
