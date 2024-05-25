package com.SmoothSailing.services;

import com.SmoothSailing.dto.CompanyRegisterDto;
import com.SmoothSailing.models.BoatModel;
import com.SmoothSailing.models.CompanyModel;
import com.SmoothSailing.repositories.BoatRepo;
import com.SmoothSailing.repositories.CompanyRepo;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {
    public final CompanyRepo companyRepo;

    public final BoatRepo boatRepo;

    @Autowired
    public CompanyService(CompanyRepo companyRepo, BoatRepo boatRepo){
        this.companyRepo=companyRepo;
        this.boatRepo = boatRepo;
    }

    public CompanyModel registerCompany(CompanyRegisterDto companyRegisterDto) {
        try {
            if (companyRepo.findByEmail(companyRegisterDto.getEmail()).isPresent()) {
                System.out.println("Email already exists in the database!");
                throw new ExceptionService("Email already exists in the database!");
            }

            CompanyModel companyModel = new CompanyModel();
            companyModel.setName(companyRegisterDto.getName());
            companyModel.setLocation(companyRegisterDto.getLocation());
            companyModel.setEmail(companyRegisterDto.getEmail());
            companyModel.setPassword(BCrypt.hashpw(companyRegisterDto.getPassword(), BCrypt.gensalt(10)));

            return companyRepo.save(companyModel);

        } catch (ExceptionService e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionService("An error occurred while registering the company.");
        }
    }

    public CompanyModel authenticateCompany(String email, String password) {
        try {
            System.out.println("Authenticating company with email: " + email);
            System.out.println("Authenticating company with password: " + password);

            Optional<CompanyModel> companyOptional = companyRepo.findByEmail(email);

            if (companyOptional.isPresent()) {
                CompanyModel company = companyOptional.get();
                if (BCrypt.checkpw(password, company.getPassword())) {
                    return company;
                }
            }

            throw new ExceptionService("Authentication failed. Invalid email or password.");

        } catch (ExceptionService e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionService("An error occurred during authentication.");
        }
    }

    public List<CompanyModel> getAllCompanies(){
        return companyRepo.findAll();
    }

    public List<CompanyModel> getAll(int page){
        return companyRepo.findAll(PageRequest.of(page, 5)).getContent();
    }

    public List<CompanyModel> getCompanyById(String id) { return companyRepo.findAllById(Collections.singleton(id)); }

    public void changePass(String id, String password){
        companyRepo.findById(id).map(userModel -> {
            userModel.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));
            return companyRepo.save(userModel);
        });
    }

    public Optional<CompanyModel> getById(String id){
        return companyRepo.findById(id);
    }

    public void edit(String id, CompanyRegisterDto company){
        companyRepo.findById(id).map(companyModel -> {
            companyModel.setName(company.getName());
            companyModel.setLocation(company.getLocation());
            return companyRepo.save(companyModel);
        });
    }

    public void deleteById(String id){
        companyRepo.deleteById(id);
    }

    public List<BoatModel> getBoatsByCompanyId(String id, int page){
        return boatRepo.findAllByCompanyId(id, PageRequest.of(page, 5)).getContent();
    }

}