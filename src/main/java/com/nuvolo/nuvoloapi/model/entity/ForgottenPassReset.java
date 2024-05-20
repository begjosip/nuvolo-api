package com.nuvolo.nuvoloapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uc_forgotten_pass_reset__token", columnNames = "token"))
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ForgottenPassReset extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Boolean utilised;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private NuvoloUser user;

}
