package com.SmoothSailing.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Data
@Entity
@Table(name="reservation")
public class ReservationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date startDate;
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date endDate;
    @Column
    private String downPayment;
    @Column
    private String status;
    @Column
    private String reviewed;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user_id;
    @ManyToOne
    @JoinColumn(name = "boat_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private BoatModel boat_id;
}
