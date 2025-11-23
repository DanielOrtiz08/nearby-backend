package com.edu.unimagdalena.nearby.entities;

import com.edu.unimagdalena.nearby.enums.TipoDeConversacion;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "conversacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversacion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nombre")
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_de_conversacion")
    private TipoDeConversacion tipoDeConversacion;

    @ManyToMany
    @JoinTable(name = "conversacion_miembros",
            joinColumns = @JoinColumn(name = "conversacion_id"),
            inverseJoinColumns = @JoinColumn(name = "cuenta_usuario_id"))
    private Set<CuentaUsuario> miembros;

    @ManyToMany
    @JoinTable(name = "conversacion_administradores",
            joinColumns = @JoinColumn(name = "conversacion_id"),
            inverseJoinColumns = @JoinColumn(name = "cuenta_usuario_id"))
    private Set<CuentaUsuario> administradores;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "receptor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mensaje> chat;
}
