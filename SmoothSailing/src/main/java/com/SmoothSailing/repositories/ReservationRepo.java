package com.SmoothSailing.repositories;

import com.SmoothSailing.dto.ReservationDatesDto;
import com.SmoothSailing.models.BoatModel;
import com.SmoothSailing.models.ReservationModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepo extends JpaRepository<ReservationModel, String> {

    @Query("SELECT r.boat_id.id FROM ReservationModel r")
    List<UUID> findAllBoatID();

    @Query("SELECT NEW com.SmoothSailing.dto.ReservationDatesDto(r.startDate, r.endDate) FROM ReservationModel r WHERE r.boat_id.id = :boatId  AND r.status <> 'Denied'")
    List<ReservationDatesDto> findAllDatesByBoatID(@Param("boatId") String boatId);

    @Query("SELECT NEW com.SmoothSailing.dto.ReservationDatesDto(r.startDate, r.endDate) FROM ReservationModel r")
    List<ReservationDatesDto> findAllDates();

    @Query("SELECT r FROM ReservationModel r Where r.boat_id.id = :id")
    List<ReservationModel> findAllByBoat(@Param("id") String id);

    @Query("SELECT r FROM ReservationModel r Where r.user_id.id = :id")
    List<ReservationModel> findAllByUserId(@Param("id") String id, Sort sort);

    @Query("SELECT r FROM ReservationModel r Where r.user_id.id = :id AND CONCAT(r.status) LIKE %:search%")
    List<ReservationModel> findAllByUserId(@Param("id") String id, @Param("search") String search, Sort sort);
}