package com.SmoothSailing.repositories;

import com.SmoothSailing.models.BoatModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.*;
import java.util.List;
import java.util.UUID;

@Repository
public interface BoatRepo extends JpaRepository<BoatModel, String> {
    @Query("SELECT b FROM BoatModel b WHERE b.company_id.id = :companyId")
    Page<BoatModel> findAllByCompanyId(@Param("companyId") String companyId, Pageable pageable);

    @Query("SELECT b FROM BoatModel b WHERE b.company_id.id = :id")
    List<BoatModel> findAllByCompanyID(@Param("id") String id);

    @Query("SELECT b FROM BoatModel b WHERE b.passengerCapacity = :passengerCapacity")
    List <BoatModel> findAllByPassengerCapacity(@Param("passengerCapacity") Integer passengerCapacity, Sort sort);

    @Query("SELECT b FROM BoatModel b WHERE b.passengerCapacity = :passengerCapacity AND CONCAT(b.name, b.review, b.type) LIKE %:search%")
    List<BoatModel> findAllByPassengerCapacityAndSearch(
            @Param("passengerCapacity") Integer passengerCapacity,
            @Param("search") String search,
            Sort sort
    );

}