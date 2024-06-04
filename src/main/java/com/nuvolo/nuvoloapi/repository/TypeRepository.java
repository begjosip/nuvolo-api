package com.nuvolo.nuvoloapi.repository;

import com.nuvolo.nuvoloapi.model.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRepository extends JpaRepository<Type, Long> {
}
