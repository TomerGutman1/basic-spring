package com.handson.basic.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

    @Entity
    @Table(name="student")
    public class Student implements Serializable {
        private static final long serialVersionUID = 1L;

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, updatable = false)
        private Instant createdAt;

        public Instant getCreatedAt() {
            return createdAt;
        }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    @NotEmpty
    @Size(max = 60)
    private String fullname;

    private LocalDate birthDate;

    @Min(100)
    @Max(800)
    private Integer satScore;

    @DecimalMin("30")
    @DecimalMax("110")
    private Double graduationScore;

    @Size(max = 20)
    private String phone;

    @Size(max = 500)
    private String profilePicture;

    @PrePersist
    private void onCreate() {
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

        public static final class StudentBuilder {
            private Long id;
            private Instant createdAt;
            private @NotEmpty
            @Size(max = 60) String fullname;
            private LocalDate birthDate;
            private @Min(100)
            @Max(800) Integer satScore;
            private @DecimalMin("30")
            @DecimalMax("110") Double graduationScore;
            private @Size(max = 20) String phone;
            private @Size(max = 500) String profilePicture;

            private StudentBuilder() {
            }

            public static StudentBuilder aStudent() {
                return new StudentBuilder();
            }

            public StudentBuilder id(Long id) {
                this.id = id;
                return this;
            }

            public StudentBuilder createdAt(Instant createdAt) {
                this.createdAt = createdAt;
                return this;
            }

            public StudentBuilder fullname(String fullname) {
                this.fullname = fullname;
                return this;
            }

            public StudentBuilder birthDate(LocalDate birthDate) {
                this.birthDate = birthDate;
                return this;
            }

            public StudentBuilder satScore(Integer satScore) {
                this.satScore = satScore;
                return this;
            }

            public StudentBuilder graduationScore(Double graduationScore) {
                this.graduationScore = graduationScore;
                return this;
            }

            public StudentBuilder phone(String phone) {
                this.phone = phone;
                return this;
            }

            public StudentBuilder profilePicture(String profilePicture) {
                this.profilePicture = profilePicture;
                return this;
            }

            public Student build() {
                Student student = new Student();
                student.setId(id);
                student.setCreatedAt(createdAt);
                student.setFullname(fullname);
                student.setBirthDate(birthDate);
                student.setSatScore(satScore);
                student.setGraduationScore(graduationScore);
                student.setPhone(phone);
                student.setProfilePicture(profilePicture);
                return student;
            }
        }
    }
