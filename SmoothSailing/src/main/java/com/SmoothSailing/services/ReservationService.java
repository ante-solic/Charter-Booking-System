package com.SmoothSailing.services;

import com.SmoothSailing.dto.BoatRegisterDto;
import com.SmoothSailing.dto.ReservationDatesDto;
import com.SmoothSailing.models.BoatModel;
import com.SmoothSailing.models.ReservationModel;
import com.SmoothSailing.models.UserModel;
import com.SmoothSailing.repositories.BoatRepo;
import com.SmoothSailing.repositories.ReservationRepo;
import com.SmoothSailing.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ReservationService {
    public final ReservationRepo reservationRepo;

    @Autowired
    public BoatRepo boatRepo;

    @Autowired
    public ReservationService(ReservationRepo reservationRepo){
        this.reservationRepo=reservationRepo;
    }

    public List<UUID> getAllReservationBoatID() {
        return reservationRepo.findAllBoatID();
    }

    public List<ReservationDatesDto> getAllDatesByBoatID(String boat_id){
        return reservationRepo.findAllDatesByBoatID(boat_id);
    }

    public List<ReservationModel> findAllReservations(String id, String search, String[] sort){

        String sortField = sort[0];
        String sortDirection = sort[1];

        Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortField);

        if(search != null) {
            return reservationRepo.findAllByUserId(id, search, Sort.by(order));
        }
        return reservationRepo.findAllByUserId(id, Sort.by(order));
    }

    public ReservationModel saveReservation(ReservationModel reservationModel) {
        try {
            //gleda jeli start date postavljen posli end date ako je baca null
            if (reservationModel.getStartDate().after(reservationModel.getEndDate())) {
                throw new ExceptionService("Start date cannot be after end date!");
            }
            System.out.println(reservationModel.getStatus().toLowerCase());
            //gledamo sa funkcijom getAllReservationBoatID jeli sadrži brod u rezervacijama jer ako se nalazi onda moramo viditi jeli se preklapa nova
            //rezervacija sa starima
            if (getAllReservationBoatID().contains(UUID.fromString(reservationModel.getBoat_id().getId()))) {
                //Dohvacamo sve rezervacije za neki brod po ID-u pa u petlji gledamo jeli se trenutna rezervacija preklapa sa prijasnjim rezervacijama tog broda
                //ako je vracamo null ako nije nastavimo dalje s kodom i spremimo u bazu

                List<ReservationDatesDto> dateRows = getAllDatesByBoatID((reservationModel.getBoat_id().getId()));
                for (ReservationDatesDto dateRow : dateRows) {
                    if (checkDatesOverlap(reservationModel.getStartDate(), reservationModel.getEndDate(), dateRow.getStartDate(), dateRow.getEndDate())) {
                        throw new ExceptionService("There is already a reservation during this time!");
                    }
                }
            }
            return reservationRepo.save(reservationModel);
        } catch (ExceptionService e){
            e.printStackTrace();
            throw e;
        } catch (Exception e){
            e.printStackTrace();
            throw new ExceptionService("An error occurred while saving the reservation.");
        }
    }

    public boolean checkDatesOverlap(Date newStartDate, Date newEndDate, Date existingStartDate, Date existingEndDate) {
        //Sa ovom smo funkcijom gledali jeli se datumi preklapaju u funkciji saveReservation
        if(newStartDate.after(existingEndDate) || newEndDate.before(existingStartDate))
            return false;
        return true;
    }

    public List<ReservationModel> checkReservationStatus(List<ReservationModel> reservations){
        Date currentDate = new Date();

        for (ReservationModel reservation : reservations) {
            Date startDate = reservation.getStartDate();
            Date endDate = reservation.getEndDate();

            //ako je trenutni datum u rasponu rezervacije i ako je status "Confirmed" postavljamo status "In progress"
            if (currentDate.after(startDate) && currentDate.before(endDate) && reservation.getStatus().equals("Confirmed")) {
                reservation.setStatus("In progress");
                reservationRepo.save(reservation);
            }
            //ako je trenutni datum u rasponu rezervacije i ako je status "Pending" postavljamo status "Denied" jer je trenutni dazum prosa
            //pocetak kretanja a jos je u pendingu
            else if(currentDate.after(startDate) && currentDate.before(endDate) && reservation.getStatus().equals("Pending")) {
                reservation.setStatus("Denied");
                reservationRepo.save(reservation);
            } else if (currentDate.after(endDate) && (reservation.getStatus().equals("Confirmed") || reservation.getStatus().equals("In progress"))) {
                reservation.setStatus("Concluded");
                reservationRepo.save(reservation);
            } else if (currentDate.after(endDate) && reservation.getStatus().equals("Pending")) {
                reservation.setStatus("Denied");
                reservationRepo.save(reservation);
            }
        }

        return reservations;
    }

    public Page<BoatModel> findAvailableBoats(Pageable pageable, Date newStartDate, Date newEndDate, Integer passengerCapacity, String search){
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List <BoatModel> unavailableBoats = new ArrayList<>();
        List <BoatModel> availableBoats = new ArrayList<>();

        List <ReservationModel> reservations = reservationRepo.findAll();

        for (ReservationModel reservation : reservations) {
            if( checkDatesOverlap(newStartDate, newEndDate, reservation.getStartDate(), reservation.getEndDate())  && !Objects.equals(reservation.getStatus().toLowerCase(), "denied")){
                unavailableBoats.add(reservation.getBoat_id());
            }
        }

        List<BoatModel> allBoats;

        if (search != null){
            allBoats = boatRepo.findAllByPassengerCapacityAndSearch(passengerCapacity, search, pageable.getSort());
        } else {
            allBoats = boatRepo.findAllByPassengerCapacity(passengerCapacity, pageable.getSort());
        }

        for (BoatModel boat : allBoats) {
            if (!unavailableBoats.contains(boat)) {
                availableBoats.add(boat);
            }
        }

        List <BoatModel> list;

        if (availableBoats.size() < startItem){
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, availableBoats.size());
            list = availableBoats.subList(startItem, toIndex);
        }

        Sort sort = pageable.getSort();
        Page<BoatModel> boatPage = new PageImpl<BoatModel>(list, PageRequest.of(currentPage, pageSize, sort), availableBoats.size());

        return boatPage;
    }

    public Integer calculateDurationOfReservation(Date startDate, Date endDate) {
        long durationInMillis = Math.abs(endDate.getTime() - startDate.getTime());

        long durationInDays = TimeUnit.MILLISECONDS.toDays(durationInMillis);

        Integer numberOfDays = Math.toIntExact(durationInDays);

        return numberOfDays;
    }

    public void editStatus(String reservation_id, String status){
        reservationRepo.findById(reservation_id).map(reservationModel -> {
            reservationModel.setStatus(status);
            return reservationRepo.save(reservationModel);
        });
    }

    public void delete(String id){
        reservationRepo.deleteById(id);
    }

    public List<ReservationModel> getAll(int page){
        return reservationRepo.findAll(PageRequest.of(page, 5)).getContent();
    }

    public void calculateReview(String reservationID, double review_score){

        Optional<ReservationModel> optionalReservationModel = reservationRepo.findById(reservationID);

        ReservationModel reservationModel = optionalReservationModel.get();
        reservationModel.setReviewed("Yes");
        reservationRepo.save(reservationModel);

        BoatModel boatModel = reservationModel.getBoat_id();

        double numberOfReviews = boatModel.getNumberOfReviews();
        double reviewSum = boatModel.getReviewSum();

        numberOfReviews += 1;
        reviewSum += review_score;

        boatModel.setReviewSum(reviewSum);
        boatModel.setNumberOfReviews(numberOfReviews);
        boatModel.setReview(reviewSum/numberOfReviews);

        boatRepo.save(boatModel);
    }
}