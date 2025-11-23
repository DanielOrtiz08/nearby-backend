package com.edu.unimagdalena.nearby.repositories;

import com.edu.unimagdalena.nearby.entities.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, UUID> {
}
