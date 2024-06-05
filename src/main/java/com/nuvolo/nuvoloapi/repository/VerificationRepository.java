package com.nuvolo.nuvoloapi.repository;

import com.nuvolo.nuvoloapi.model.entity.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface VerificationRepository extends JpaRepository<Verification, Long> {

    Optional<Verification> findByTokenAndIsVerified(String token, Boolean isVerified);
}
