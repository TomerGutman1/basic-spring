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

}
