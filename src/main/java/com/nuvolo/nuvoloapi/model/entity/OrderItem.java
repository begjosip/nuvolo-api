package com.nuvolo.nuvoloapi.model.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class OrderItem extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private OrderDetails orderDetails;

}
