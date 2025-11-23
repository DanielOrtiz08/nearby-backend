package com.edu.unimagdalena.nearby.entities;

import lombok.*;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "universidad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Universidad {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_id", nullable = false)
    private Ubicacion ubicacion;
}
