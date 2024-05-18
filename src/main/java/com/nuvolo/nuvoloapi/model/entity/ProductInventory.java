package com.nuvolo.nuvoloapi.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ProductInventory extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @OneToOne(mappedBy = "productInventory", fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private Product product;
}

