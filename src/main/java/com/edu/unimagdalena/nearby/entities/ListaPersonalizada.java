package com.edu.unimagdalena.nearby.entities;

import lombok.*;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "lista_personalizada")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListaPersonalizada { // grafo (amigos, grupos, alcancePublicaciones, etc)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_id", nullable = false)
    private Perfil perfilPropietario; // vertice inicial

    @Column(name = "nombre_lista", nullable = false)
    private String nombreLista; // amigos, mejores amigos, compartidos, historias, ver perfil, etc

    @OneToMany(mappedBy = "listaPersonalizada", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContenidoLista> elementos; // lista de succesores
}
