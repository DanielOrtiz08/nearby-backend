package com.edu.unimagdalena.nearby.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "search_alert")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private CuentaUsuario usuario;

    @Column(name = "criteria", nullable = false)
    private String criteria;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
