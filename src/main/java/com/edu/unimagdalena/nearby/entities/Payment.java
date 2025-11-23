package com.edu.unimagdalena.nearby.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private CuentaUsuario usuario;

    @Column(name = "monto", nullable = false)
    private BigDecimal monto;

    @Column(name = "moneda", nullable = false)
    private String moneda;

    @Column(name = "status", nullable = false)
    private String status; // PENDING, CONFIRMED, REFUND_REQUESTED, REFUNDED, FAILED

    @Column(name = "intent_id", nullable = false, unique = true)
    private String intentId; // token generado para el intento

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "provider")
    private String provider; // ejemplo: stripe, mock

    @Column(name = "metadata")
    private String metadata;
}
