package com.nuvolo.nuvoloapi.model.entity;

import com.nuvolo.nuvoloapi.model.enums.ProductType;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private ProductType name;

    private String description;

}