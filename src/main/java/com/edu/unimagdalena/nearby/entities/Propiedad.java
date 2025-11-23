package com.edu.unimagdalena.nearby.entities;

import lombok.*;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "propiedad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Propiedad {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrendador_id", nullable = false)
    private Arrendador dueño;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "tipo_de_propiedad")
    private String tipoDePropiedad;

    @OneToMany(mappedBy = "propiedad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Alojamiento> cuposDisponibles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_id", nullable = false)
    private Ubicacion ubicacion;

    @Column(name = "esta_verificada", nullable = false)
    private boolean estaVerificada;

    @Column(name = "es_ambientalista", nullable = false)
    private boolean esAmbientalista;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversacion_reseñas_id", nullable = true)
    private Conversacion reseñas;

    @ElementCollection
    @CollectionTable(name = "propiedad_imagenes", joinColumns = @JoinColumn(name = "propiedad_id"))
    @Column(name = "imagen_url")
    private List<String> imagenesUrls;
}
