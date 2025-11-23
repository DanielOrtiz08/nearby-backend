package com.edu.unimagdalena.nearby.entities;

import lombok.*;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "arrendador")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Arrendador {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_usuario_id", nullable = false)
    private CuentaUsuario usuario;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_id", nullable = false)
    private Perfil perfil;

    @Column(name = "cedula", nullable = false)
    private int cedula;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversacion_reseñas_id", nullable = true)
    private Conversacion reseñas;

    @OneToMany(mappedBy = "dueño", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Propiedad> propiedades;
}
