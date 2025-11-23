package com.edu.unimagdalena.nearby.entities;

import com.edu.unimagdalena.nearby.enums.TipoReporte;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reporte")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reporte {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_de_reporte", nullable = false)
    private TipoReporte tipoDeReporte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_reportador_id", nullable = false)
    private CuentaUsuario usuarioReportador;

    @Column(name = "fecha_reporte", nullable = false)
    private LocalDateTime fechaReporte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensaje_objetivo_id", nullable = true)
    private Mensaje objetivo;

    @Column(name = "descripcion")
    private String descripcion;
}
