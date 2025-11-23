package com.edu.unimagdalena.nearby.entities;

import com.edu.unimagdalena.nearby.enums.TipoContacto;
import lombok.*;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "contacto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contacto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_id", nullable = false)
    private Perfil perfil;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_contacto", nullable = false)
    private TipoContacto tipoContacto;

    @Column(name = "valor", nullable = false)
    private String valor;
}
