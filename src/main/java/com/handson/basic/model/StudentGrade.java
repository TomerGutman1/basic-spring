package com.handson.basic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_grade")
public class StudentGrade implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Expose formatted version of createdAt
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createdAt")
    public LocalDateTime getCreatedAtFormatted() {
        return createdAt;
    }

    @JsonIgnore
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "studentId")
    private Student student;

    @NotEmpty
    @Length(max = 60)
    private String courseName;

    @Min(10)
    @Max(100)
    private Integer courseScore;

    // Constructors
    public StudentGrade() {
        this.createdAt = LocalDateTime.now(); // set default on new instances
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getCourseScore() {
        return courseScore;
    }

    public void setCourseScore(Integer courseScore) {
        this.courseScore = courseScore;
    }

    // Builder
    public static final class StudentGradeBuilder {
        private Long id;
        private LocalDateTime createdAt = LocalDateTime.now();
        private Student student;
        private String courseName;
        private Integer courseScore;

        private StudentGradeBuilder() {}

        public static StudentGradeBuilder aStudentGrade() {
            return new StudentGradeBuilder();
        }

        public StudentGradeBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public StudentGradeBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public StudentGradeBuilder student(Student student) {
            this.student = student;
            return this;
        }

        public StudentGradeBuilder courseName(String courseName) {
            this.courseName = courseName;
            return this;
        }

        public StudentGradeBuilder courseScore(Integer courseScore) {
            this.courseScore = courseScore;
            return this;
        }

        public StudentGrade build() {
            StudentGrade studentGrade = new StudentGrade();
            studentGrade.id = this.id;
            studentGrade.createdAt = this.createdAt;
            studentGrade.student = this.student;
            studentGrade.courseName = this.courseName;
            studentGrade.courseScore = this.courseScore;
            return studentGrade;
        }
    }
}
