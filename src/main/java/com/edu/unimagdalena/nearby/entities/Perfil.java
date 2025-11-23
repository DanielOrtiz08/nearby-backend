package com.edu.unimagdalena.nearby.entities;

import lombok.*;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "perfil")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Perfil {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_usuario_id", unique = true, nullable = false)
    private CuentaUsuario cuentaUsuario;

    @Column(name = "nombres")
    private String nombres;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "descripcion")
    private String descripcion;

    @ElementCollection
    @CollectionTable(name = "perfil_enlaces", joinColumns = @JoinColumn(name = "perfil_id"))
    @Column(name = "enlace")
    private List<String> enlacesPersonales;

    @OneToMany(mappedBy = "perfil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contacto> contactos; // telefono, correo, etc.

    @Column(name = "foto_perfil_url")
    private String fotoPerfilUrl;

    @OneToMany(mappedBy = "perfilPropietario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ListaPersonalizada> listasPersonalizadas;
}
