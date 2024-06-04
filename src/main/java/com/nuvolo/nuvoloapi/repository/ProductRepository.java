package com.nuvolo.nuvoloapi.repository;

import com.nuvolo.nuvoloapi.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
