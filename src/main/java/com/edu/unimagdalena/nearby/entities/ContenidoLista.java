package com.edu.unimagdalena.nearby.entities;

import com.edu.unimagdalena.nearby.enums.TipoContenido;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contenido_lista")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContenidoLista { // esta seria la arista
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lista_personalizada_id", nullable = false)
    private ListaPersonalizada listaPersonalizada;

    @Column(name = "tipo_lista")
    private String tipoLista; // amigos, compartir

    @Column(name = "fecha_agregado")
    private LocalDateTime fechaAgregado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_contenido")
    private TipoContenido tipoContenido;

    @Column(name = "agregado_id")
    private UUID agregado; // vertice final (amigo, grupo, etc)

    @Column(name = "peso_vinculo", nullable = false)
    private int pesoVinculo; // que tan valioso es para el usuario (1-10)  // info para el SR
}
