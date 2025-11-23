package com.edu.unimagdalena.nearby.entities;

import com.edu.unimagdalena.nearby.enums.VisibilidadConversacion;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "publicacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publicacion { // feed
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_perfil_id", nullable = false)
    private Perfil autor;

    @Column(name = "contenido", nullable = false)
    private String contenido;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @ManyToMany
    @JoinTable(name = "publicacion_reacciones",
            joinColumns = @JoinColumn(name = "publicacion_id"),
            inverseJoinColumns = @JoinColumn(name = "cuenta_usuario_id"))
    private Set<CuentaUsuario> reacciones;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "conversacion_comentarios_id", nullable = true)
    private Conversacion comentarios;

    @ManyToMany
    @JoinTable(name = "publicacion_compartidos",
            joinColumns = @JoinColumn(name = "publicacion_id"),
            inverseJoinColumns = @JoinColumn(name = "cuenta_usuario_id"))
    private Set<CuentaUsuario> compartidos;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibilidad")
    private VisibilidadConversacion visibilidad;
}
