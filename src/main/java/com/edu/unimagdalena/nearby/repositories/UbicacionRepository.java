package com.edu.unimagdalena.nearby.repositories;

import com.edu.unimagdalena.nearby.entities.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, UUID> {
    List<Ubicacion> findByValorContainingIgnoreCase(String valor);
}
