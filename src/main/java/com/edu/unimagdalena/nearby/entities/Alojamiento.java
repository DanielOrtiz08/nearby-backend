package com.edu.unimagdalena.nearby.entities;

import com.edu.unimagdalena.nearby.enums.ServicioOfrecido;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "alojamiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alojamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propiedad_id", nullable = false)
    private Propiedad propiedad;

    @Column(name = "estado_alojamiento", nullable = false)
    private String estadoAlojamiento;

    @Column(name = "capacidad", nullable = false)
    private int capacidad;

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservacion> reservaciones;

    @Column(name = "precio_general", nullable = false)
    private BigDecimal precioGeneral;

    @ElementCollection(targetClass = ServicioOfrecido.class)
    @CollectionTable(name = "alojamiento_servicios", joinColumns = @JoinColumn(name = "alojamiento_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "servicio") // columna que almacena el valor del enum en la tabla de colección
    private Set<ServicioOfrecido> serviciosOfrecidos;
}
