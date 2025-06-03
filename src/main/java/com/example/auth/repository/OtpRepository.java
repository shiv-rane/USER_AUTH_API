package com.example.auth.repository;


import com.example.auth.model.OtpEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, Integer> {
    Optional<OtpEntity> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM OtpEntity o WHERE o.expiration_time < :now")
    void deleteExpiredOtps(@Param("now") LocalDateTime now);

}
