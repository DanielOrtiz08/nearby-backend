package com.edu.unimagdalena.nearby.repositories;

import com.edu.unimagdalena.nearby.entities.Conversacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConversacionRepository extends JpaRepository<Conversacion, UUID> {
    List<Conversacion> findByMiembros_Id(UUID miembroId);
}
