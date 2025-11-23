package com.edu.unimagdalena.nearby.repositories;

import com.edu.unimagdalena.nearby.entities.Propiedad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PropiedadRepository extends JpaRepository<Propiedad, UUID> {
    List<Propiedad> findByEstaVerificadaFalse();
    long countByEstaVerificadaFalse();

    // Nuevos métodos
    List<Propiedad> findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String nombre, String descripcion);
    List<Propiedad> findByDueño_Id(UUID dueñoId);
    List<Propiedad> findByTipoDePropiedadAndIdNot(String tipoDePropiedad, UUID id);
    List<Propiedad> findByUbicacion_Id(UUID ubicacionId);
}
