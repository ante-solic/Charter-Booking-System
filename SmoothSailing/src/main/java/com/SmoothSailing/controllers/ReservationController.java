package com.SmoothSailing.controllers;

import com.SmoothSailing.dto.BoatRegisterDto;
import com.SmoothSailing.models.BoatModel;
import com.SmoothSailing.models.ReservationModel;
import com.SmoothSailing.models.UserModel;
import com.SmoothSailing.repositories.BoatRepo;
import com.SmoothSailing.repositories.ReservationRepo;
import com.SmoothSailing.repositories.UserRepo;
import com.SmoothSailing.services.BoatService;
import com.SmoothSailing.services.ReservationService;
import com.SmoothSailing.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class ReservationController {
    @Autowired
    private final ReservationRepo reservationRepo;

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private BoatService boatService;
    @Autowired
    private BoatRepo boatRepo;
    @Autowired
    private UserService userService;
    @Autowired
    public UserRepo userRepo;

    @Autowired
    public ReservationController(ReservationRepo reservationRepo){
        this.reservationRepo=reservationRepo;
    }

    @GetMapping("/company/reservations")
    public String getCompanyReservations(@CookieValue(name = "company_id", required = false) String id, Model model){
        if(id == null || id.isEmpty()){
            return "company/login_company_page";
        }
        //preko company id-a koji smo dobili iz cookie-a dohvacamo brodeve za tu firmu
        List<BoatModel> boatModels = boatRepo.findAllByCompanyID(id);
        //za svaki brod spremamo rezervacije u listu
        List<ReservationModel> reservations = new ArrayList<>();
        for (BoatModel boatModel : boatModels) {
            reservations.addAll(reservationRepo.findAllByBoat(boatModel.getId()));
        }

        reservationService.checkReservationStatus(reservations);

        model.addAttribute("reservations", reservations);
        return "company/company_reservations";
    }

    @GetMapping("/user/reservations")
    public String getUserReservations(@CookieValue(name = "name", required = false) String role, @CookieValue(name = "id", required = false) String id, Model model,
                                      @RequestParam(value = "sort", defaultValue = "startDate,asc") String[] sort,
                                      @RequestParam(value = "search", required = false) String search){

        if(!role.equals("user")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Values are not equal");;
        if(id == null || id.isEmpty())
            return "user/login_page";

        String sortField = sort[0];
        String sortDirection = sort[1];

        List<ReservationModel> reservations = reservationService.findAllReservations(id, search, sort);
        System.out.println(reservations);
        model.addAttribute("reservations", reservations);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("search", search);

        return "user/user_reservations";
    }

    @PostMapping("/company/reservation/{status}")
    public String changeReservationStatus(@CookieValue(name = "company_id", required = false) String id,@RequestParam("id") String reservation_id, @PathVariable("status") String status){
        if(id == null || id.isEmpty()){
            return "company/login_company_page";
        }
        System.out.println(reservation_id + "This is res id");
        reservationService.editStatus(reservation_id, status.toUpperCase());

        return "redirect:/company/reservations";
    }

    @GetMapping("/user/reservation")
    public String getReservationPage(@CookieValue(name = "name", required = false) String role,
                                     @CookieValue(name = "id", required = false) String id, Model model){

        if(!Objects.equals(role, "user")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Values are not equal");;
        if(id == null || id.isEmpty()){
            return "user/login_page";
        }
        System.out.println("ID cookie value: " + id);

        Optional<UserModel> optionalUserModel = userRepo.findById(id);
        UserModel userModel = optionalUserModel.get();

        model.addAttribute("reservationRequest", new ReservationModel());
        model.addAttribute("licence", userModel.getLicense());

        return "user/reservation_page";
    }

    @RequestMapping("/user/available_boats")
    public String getAvailableBoatsPage( @CookieValue(name = "id", required = false) String id,
                                         @CookieValue(name = "name", required = false) String role,
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date newStartDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date newEndDate,
            @RequestParam("passengerCapacity") Integer passengerCapacity,
            @RequestParam("crew_choice") String crewChoice,
            @RequestParam(value = "sort", defaultValue = "name,asc") String[] sort,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            Model model) {
        if(!role.equals("user")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Values are not equal");
        }
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(2);

        String sortField = sort[0];
        String sortDirection = sort[1];

        Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Order order = new Order(direction, sortField);

        Page<BoatModel> boatPage = reservationService.findAvailableBoats(PageRequest.of(currentPage - 1, pageSize, Sort.by(order)), newStartDate,newEndDate, passengerCapacity, search);

        ReservationModel reservationRequest = new ReservationModel();
        reservationRequest.setStartDate(newStartDate);
        reservationRequest.setEndDate(newEndDate);

        Integer duration = reservationService.calculateDurationOfReservation(newStartDate,newEndDate);

        model.addAttribute("reservationRequest", reservationRequest);
        model.addAttribute("startDate", newStartDate);
        model.addAttribute("endDate", newEndDate);
        model.addAttribute("boatPage", boatPage);
        model.addAttribute("duration" , duration);
        model.addAttribute("passengerCapacity", passengerCapacity);
        model.addAttribute("crewChoice", crewChoice);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
        model.addAttribute("search", search);

        int totalPages = boatPage.getTotalPages();
        if(totalPages > 0) {
            List <Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        System.out.println("Start date is: " + newStartDate + " and End date is: " + newEndDate);

        return "user/available_boats";
    }

    @PostMapping("/user/make_reservation")
    public String makeReservation(@CookieValue(name = "id", required = false) String id,@RequestParam("boat_id") String boatId, ReservationModel reservationModel){
        if(id == null || id.isEmpty()){
            return "user/login_page";
        }
        //dphvacamo id usera i cookie i dohvacamo ga iz baze i spemamo u reservationModel
        Optional<UserModel> optionalUserModel = userRepo.findById(id);
        UserModel userModel = optionalUserModel.get();
        reservationModel.setUser_id(userModel);
        //dphvacamo id broda iz paramsa i dohvacamo ga iz baze i spemamo u reservationModel
        Optional<BoatModel> optionalBoatModel = boatRepo.findById(boatId);
        BoatModel boatModel = optionalBoatModel.get();
        reservationModel.setBoat_id(boatModel);

        System.out.println("Reservation request: " + reservationModel);
        reservationModel.setStatus("Pending");
        reservationModel.setReviewed("No");

        ReservationModel reservationAttempt = reservationService.saveReservation(reservationModel);
        return reservationAttempt == null ? "error_page" : "redirect:/";
    }

    @GetMapping("/")
    public String checkCookie(@CookieValue(name = "name", required = false) String name, Model model) {
        if (name == null || name.isEmpty()) {
            name = "logout";
        }
        model.addAttribute("authorize", name);
        return "index";
    }

    @GetMapping("/reservation/delete/{id}")
    public String delete(@CookieValue(name = "name", required = false) String role, @PathVariable("id") String id){

        if(!Objects.equals(role, "company") && !role.equals("admin"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Values are not equal");

        reservationService.delete(id);
        return "redirect:/user/reservations";
    }

    @GetMapping("/reservations/list")
    public String list(@CookieValue(name = "name", required = false) String role,
                       @RequestParam Map<String, String> allParams, Model model){

        if(!Objects.equals(role, "company") && !role.equals("admin"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Values are not equal");

        model.addAttribute("reservations", reservationService.getAll(Integer.parseInt(allParams.get("page"))));
        model.addAttribute("admin", true);
        model.addAttribute("prev", Integer.parseInt(allParams.get("page")) - 1);
        model.addAttribute("next", Integer.parseInt(allParams.get("page")) + 1);
        return "reservation_list";
    }

    @PostMapping("/user/make_review")
    public String makeReview(@RequestParam("reservationID") String reservationID, @RequestParam("review_score") double review_score){

        reservationService.calculateReview(reservationID, review_score);

        return "/user/user_reservations";
    }
}