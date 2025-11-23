package com.edu.unimagdalena.nearby.entities;

import com.edu.unimagdalena.nearby.enums.Rol;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cuenta_usuario", indexes = {@Index(columnList = "usuario", name = "idx_usuario" )})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "usuario", nullable = false, unique = true)
    private String usuario;

    @Column(name = "contrasena_hasheada")
    private String contrasenaHasheada;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    // No changes required: todos los atributos ya están anotados con @Column/@Id/@Enumerated según corresponda.
}
