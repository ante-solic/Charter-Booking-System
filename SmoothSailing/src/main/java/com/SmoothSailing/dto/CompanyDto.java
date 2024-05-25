package com.SmoothSailing.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CompanyDto {
    private String name;
    private String email;
    private String password;
    private String location;
}
