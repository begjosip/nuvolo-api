package com.nuvolo.nuvoloapi.repository;

import com.nuvolo.nuvoloapi.model.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
}
