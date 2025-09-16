package com.picnic.potluck.repository;

import com.picnic.potluck.model.Fundraiser;
import com.picnic.potluck.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface FundraiserRepository extends JpaRepository<Fundraiser, UUID>, FundraiserRepositoryCustom {
    Page<Fundraiser> findByActiveTrue(Pageable pageable);
    Page<Fundraiser> findByActiveTrueAndTitleContainingIgnoreCase(String q, Pageable pageable);
    List<Fundraiser> findTop20ByOrganizerOrderByCreatedAtDesc(User organizer);
}