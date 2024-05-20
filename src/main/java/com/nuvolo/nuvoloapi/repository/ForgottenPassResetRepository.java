package com.nuvolo.nuvoloapi.repository;

import com.nuvolo.nuvoloapi.model.entity.ForgottenPassReset;
import com.nuvolo.nuvoloapi.model.entity.NuvoloUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ForgottenPassResetRepository extends JpaRepository<ForgottenPassReset, Long> {

    Optional<ForgottenPassReset> findFirstByUserAndTokenAndUtilisedAndCreatedAtAfter(NuvoloUser user,
                                                                                     String token,
                                                                                     Boolean utilised,
                                                                                     LocalDateTime createdAtAfter);

}
