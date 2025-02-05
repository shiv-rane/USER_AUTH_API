package com.example.auth.repository;


import com.example.auth.model.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpEntity, Integer> {
    Optional<OtpEntity> findByEmail(String email);
}
