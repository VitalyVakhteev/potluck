package com.picnic.potluck.repository.scan;

import com.picnic.potluck.model.Scan;
import com.picnic.potluck.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScanRepository extends JpaRepository<Scan, UUID> {
    @EntityGraph(attributePaths = {"fundraiser", "participant", "organizer"})
    Optional<Scan> findByIdempotencyKey(String idempotencyKey);
}
