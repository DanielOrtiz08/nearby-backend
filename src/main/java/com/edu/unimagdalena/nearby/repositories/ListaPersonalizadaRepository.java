package com.edu.unimagdalena.nearby.repositories;

import com.edu.unimagdalena.nearby.entities.ListaPersonalizada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ListaPersonalizadaRepository extends JpaRepository<ListaPersonalizada, UUID> {
}
