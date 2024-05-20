package com.nuvolo.nuvoloapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uc_verification__token", columnNames = "token"))
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Verification extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private NuvoloUser user;
}
