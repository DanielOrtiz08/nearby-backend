package com.edu.unimagdalena.nearby.entities;

import com.edu.unimagdalena.nearby.enums.TipoUbicacion;
import lombok.*;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "ubicacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ubicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ubicacion", nullable = false)
    private TipoUbicacion tipoUbicacion;

    @Column(name = "valor", nullable = false)
    private String valor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referencia_padre_id", nullable = true)
    private Ubicacion referenciaUbacionPadre;

    @Column(name = "coordenadas")
    private String coordenadas; // Estructura "lat, lng"
}
