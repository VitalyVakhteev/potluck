package com.picnic.potluck.repository;

import com.picnic.potluck.model.Scan;
import com.picnic.potluck.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScanRepository extends JpaRepository<Scan, UUID> {
    Optional<Scan> findByIdempotencyKey(String idempotencyKey);

    @EntityGraph(attributePaths = {"fundraiser"})
    List<Scan> findTop50ByParticipantOrderByCreatedAtDesc(User participant);
}
