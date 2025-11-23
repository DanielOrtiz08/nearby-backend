package com.edu.unimagdalena.nearby.repositories;

import com.edu.unimagdalena.nearby.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByPropiedad_IdOrderByFechaCreacionDesc(UUID propiedadId);
    List<Review> findByAutor_IdOrderByFechaCreacionDesc(UUID autorId);
}
