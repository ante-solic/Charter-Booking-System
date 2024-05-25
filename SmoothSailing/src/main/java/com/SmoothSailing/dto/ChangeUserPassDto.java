package com.SmoothSailing.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeUserPassDto {
    private String repeatPassword;
    private String password;
}
