package com.edu.unimagdalena.nearby.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "path", nullable = false)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = true)
    private CuentaUsuario uploader;

    @Column(name = "type")
    private String type;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;
}
