package com.SmoothSailing.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Data
@Entity
@Table(name="boats")
public class BoatModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column
    private String img;
    @Column
    private int price;
    @Column
    private String availability;
    @Column
    private double review;
    @Column(name="number_of_reviews")
    private double numberOfReviews;
    @Column(name="review_sum")
    private double reviewSum;
    @Column
    private String name;
    @Column
    private String type;
    @Column
    private int crewCapacity;
    @Column
    private int passengerCapacity;
    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    @Convert(converter = CompanyModelConverter.class)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CompanyModel company_id;
}