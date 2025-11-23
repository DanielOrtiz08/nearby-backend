package com.edu.unimagdalena.nearby.repositories;

import com.edu.unimagdalena.nearby.entities.Alojamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AlojamientoRepository extends JpaRepository<Alojamiento, UUID> {
}
