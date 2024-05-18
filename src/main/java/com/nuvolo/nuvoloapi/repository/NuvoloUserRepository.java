package com.nuvolo.nuvoloapi.repository;


import com.nuvolo.nuvoloapi.model.entity.NuvoloUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NuvoloUserRepository extends JpaRepository<NuvoloUser, Long> {

    Optional<NuvoloUser> findByEmail(String email);
}
