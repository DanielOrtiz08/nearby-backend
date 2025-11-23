package com.edu.unimagdalena.nearby.entities;

import com.edu.unimagdalena.nearby.enums.RangoAdministrador;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad Administrador.
 */
@Entity
@Table(name = "administrador")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Administrador {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_usuario_id", nullable = false)
    private CuentaUsuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "alcance", nullable = false)
    private RangoAdministrador alcance;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;
}
