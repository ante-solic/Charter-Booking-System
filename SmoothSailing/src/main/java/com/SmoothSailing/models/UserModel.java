package com.SmoothSailing.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Data
@Entity
@Table(name="users")
@Getter
@Setter
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column
    private String name;
    @Column
    private String surname;
    @Column
    private String license;
    @Column
    private String email;
    @Column
    private String password;
    @Column
    private String gender;
    @Column(name="Date_Of_Birth")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    @Column
    private Boolean superuser;
}
