package com.SmoothSailing.dto;

import com.SmoothSailing.models.CompanyModel;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CrewDto {
    private String name;
    private String surname;
    private String position;
    private String price;
    private String review = "0";
    private String company_id;
}
