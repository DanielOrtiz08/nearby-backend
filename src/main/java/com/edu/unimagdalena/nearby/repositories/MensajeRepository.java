package com.edu.unimagdalena.nearby.repositories;

import com.edu.unimagdalena.nearby.entities.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, UUID> {
    List<Mensaje> findByReceptor_IdOrderByFechaDeEnvioAsc(UUID receptorId);
}
