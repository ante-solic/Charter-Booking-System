package com.SmoothSailing.repositories;

import com.SmoothSailing.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<UserModel, String> {

    Optional<UserModel> findByEmailAndPassword(String email, String password);
    Optional<UserModel> findByEmail(String email);

    Optional<UserModel> findById(String id);
}
