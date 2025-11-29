package com.social.SocialMicroservice.repositories;

import com.social.SocialMicroservice.entities.EventStatus;
import com.social.SocialMicroservice.entities.OutboxEvent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    // Buscar eventos PENDING en orden FIFO
    @Query("select e from OutboxEvent e where e.status = 'PENDING' order by e.createdAt")
    List<OutboxEvent> findPendingEvents(Pageable pageable);

    // Buscar todos los eventos pendientes
    List<OutboxEvent> findByStatus(EventStatus status);
}

