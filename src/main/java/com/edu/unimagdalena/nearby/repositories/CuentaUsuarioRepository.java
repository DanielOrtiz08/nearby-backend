package com.edu.unimagdalena.nearby.repositories;

import com.edu.unimagdalena.nearby.entities.CuentaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CuentaUsuarioRepository extends JpaRepository<CuentaUsuario, UUID> {
    long countByActivoTrue();
    Optional<CuentaUsuario> findByUsuario(String usuario);
}
