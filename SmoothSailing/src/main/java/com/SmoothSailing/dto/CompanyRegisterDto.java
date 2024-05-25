package com.SmoothSailing.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CompanyRegisterDto {
    private String name;
    private String location;
    private String password;
    private String confirmPassword;
    private String email;
    private String confirmEmail;
}
