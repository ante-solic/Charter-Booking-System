package com.SmoothSailing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BoatRegisterDto {


    private int price;

    private String availability;

    private String review;

    private String name;

    private String type;

    private int crewCapacity;

    private int passengerCapacity;

}
