package com.nuvolo.nuvoloapi.model.entity;

import com.nuvolo.nuvoloapi.model.enums.ProductType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uc_type__name", columnNames = "name"))
public class Type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private ProductType name;

    private String description;

}
