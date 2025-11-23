package com.edu.unimagdalena.nearby.entities;

import lombok.*;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservacion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "huesped_id", nullable = false)
    private CuentaUsuario huesped;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alojamiento_id", nullable = false)
    private Alojamiento alojamiento;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(name = "numero_de_personas", nullable = false)
    private int numeroDePersonas;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "costo_final")
    private BigDecimal costoFinal;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "tipo_contrato")
    private String tipoContrato;
}
