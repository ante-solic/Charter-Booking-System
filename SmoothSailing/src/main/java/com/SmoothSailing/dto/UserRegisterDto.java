package com.SmoothSailing.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@Setter
@Getter
public class UserRegisterDto {
    private String name;
    private String surname;
    private String email;
    private String confirmEmail;
    private String password;
    private String confirmPassword;
    private String license;
    private String gender;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
}
