package com.edu.unimagdalena.nearby.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// sin persistena en BDR
public class Publicidad {
    private UUID id;
    private Ubicacion ubicacion;
    private String tipoPublicidad;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String estado;
    private BigDecimal costo;
    private int prioridad;
}
