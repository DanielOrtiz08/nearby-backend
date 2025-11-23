package com.edu.unimagdalena.nearby.entities;

import lombok.*;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "carrera")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carrera {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "facultad")
    private String facultad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @Column(name = "usuarios_activos", nullable = false)
    private int usuariosActivos;
}
