package com.edu.unimagdalena.nearby.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "eco_certification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EcoCertification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private CuentaUsuario solicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propiedad_id", nullable = true)
    private Propiedad propiedad;

    @Column(name = "estado", nullable = false)
    private String estado; // PENDING, VERIFIED, REJECTED

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "verificado", nullable = false)
    private boolean verificado;

    @Column(name = "notas")
    private String notas;
}
