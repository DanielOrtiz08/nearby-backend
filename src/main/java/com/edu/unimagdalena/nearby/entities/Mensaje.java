package com.edu.unimagdalena.nearby.entities;

import com.edu.unimagdalena.nearby.enums.EstadoDeMensaje;
import com.edu.unimagdalena.nearby.enums.TipoDeMensaje;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "mensaje")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emisor_id", nullable = false)
    private CuentaUsuario emisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receptor_id", nullable = false)
    private Conversacion receptor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_de_mensaje")
    private TipoDeMensaje tipoDeMensaje;

    @Column(name = "fecha_de_envio")
    private LocalDateTime fechaDeEnvio;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_mensaje")
    private EstadoDeMensaje estado;

    @Column(name = "contenido")
    private String contenido; // texto o url

    @Column(name = "calificacion")
    private int calificacion;

    @OneToMany(mappedBy = "objetivo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reporte> reportes;
}
