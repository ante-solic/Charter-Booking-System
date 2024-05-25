package com.SmoothSailing.services;

import com.SmoothSailing.dto.ChangeUserPassDto;
import com.SmoothSailing.dto.UserRegisterDto;
import com.SmoothSailing.models.BoatModel;
import com.SmoothSailing.models.UserModel;
import com.SmoothSailing.repositories.UserRepo;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    @Autowired
    public UserRepo userRepo;

    public UserModel registerUser(UserRegisterDto userModel){
        try {
            if (userRepo.findByEmail(userModel.getEmail()).isPresent()) {
                throw new ExceptionService("Email already exists in the database!");
            }
            UserModel newUserModel = new UserModel();
            newUserModel.setName(userModel.getName());
            newUserModel.setSurname(userModel.getSurname());
            newUserModel.setEmail(userModel.getEmail());
            newUserModel.setPassword(BCrypt.hashpw(userModel.getPassword(), BCrypt.gensalt(10)));
            newUserModel.setGender(userModel.getGender());
            newUserModel.setLicense(userModel.getLicense());
            newUserModel.setBirthday(userModel.getBirthday());
            newUserModel.setSuperuser(false);
            return userRepo.save(newUserModel);
        } catch (ExceptionService e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e){
            e.printStackTrace();
            throw new ExceptionService("An error occurred while registering the user.");
        }
    }

    public List<UserModel> getAll(int page){
        return userRepo.findAll(PageRequest.of(page, 5)).getContent();
    }

    public UserModel authenticate(String email, String password) {
        try {
            Optional<UserModel> userOptional = userRepo.findByEmail(email);

            if (userOptional.isPresent()) {
                UserModel user = userOptional.get();
                if (BCrypt.checkpw(password, user.getPassword())) {
                    return user;
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

    public Optional<UserModel> getUserById(String id){
        return userRepo.findById(id);
    }

    public void editUser(String id, UserRegisterDto user){
        userRepo.findById(id).map(userModel -> {
            userModel.setName(user.getName());
            userModel.setSurname(user.getSurname());
            userModel.setBirthday(user.getBirthday());
            userModel.setGender(user.getGender());
            userModel.setLicense(user.getLicense());
            return userRepo.save(userModel);
        });
    }

    public void changePass(String id, String password){
        userRepo.findById(id).map(userModel -> {
            userModel.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));
            return userRepo.save(userModel);
        });
    }

    public void deleteUserById(String id){
        userRepo.deleteById(id);
    }
}
