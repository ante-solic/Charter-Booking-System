package com.SmoothSailing.services;

import com.SmoothSailing.dto.BoatRegisterDto;
import com.SmoothSailing.dto.UserRegisterDto;
import com.SmoothSailing.models.BoatModel;
import com.SmoothSailing.repositories.BoatRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BoatService {
    public final BoatRepo boatRepo;

    @Autowired
    public BoatService(BoatRepo boatRepo){
        this.boatRepo=boatRepo;
    }

    public List<BoatModel> getAllBoats(){
        return boatRepo.findAll();
    }

    public BoatModel registerBoat(BoatModel boatModel){
        return boatRepo.save(boatModel);
    }

    public Optional<BoatModel> getBoatById(String id){
        return boatRepo.findById(id);
    }
    public void editBoat(String id, BoatRegisterDto boat){
        boatRepo.findById(id).map(boatModel -> {
            boatModel.setName(boat.getName());
            boatModel.setAvailability(boat.getAvailability());
            boatModel.setPrice(boat.getPrice());
            boatModel.setCrewCapacity(boat.getCrewCapacity());
            boatModel.setPassengerCapacity(boat.getPassengerCapacity());
            boatModel.setType(boat.getType());
            return boatRepo.save(boatModel);
        });
    }

    public void delete(String id){
        boatRepo.deleteById(id);
    }

    public List<BoatModel> getAll(int page){
        return boatRepo.findAll(PageRequest.of(page, 5)).getContent();
    }

}