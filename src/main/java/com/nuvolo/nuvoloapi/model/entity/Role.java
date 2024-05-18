package com.nuvolo.nuvoloapi.model.entity;

import com.nuvolo.nuvoloapi.model.enums.RoleName;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleName name;
}
