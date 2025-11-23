package com.edu.unimagdalena.nearby.repositories;

import com.edu.unimagdalena.nearby.entities.Reservacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReservacionRepository extends JpaRepository<Reservacion, UUID> {
    List<Reservacion> findByHuespedId(UUID huespedId);

    @Query("select r from Reservacion r where r.alojamiento.id = :alojamientoId")
    List<Reservacion> findByAlojamientoId(@Param("alojamientoId") UUID alojamientoId);

    @Query("select r from Reservacion r where r.alojamiento.propiedad.dueño.usuario.id = :ownerUserId")
    List<Reservacion> findByOwnerUserId(@Param("ownerUserId") UUID ownerUserId);
}
