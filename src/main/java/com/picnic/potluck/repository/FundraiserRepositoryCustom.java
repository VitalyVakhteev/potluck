package com.picnic.potluck.repository;

import com.picnic.potluck.model.Fundraiser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

public interface FundraiserRepositoryCustom {
    Page<Fundraiser> searchActiveWithinRadius(double lat, double lon, double radiusKm, Pageable pageable);
}

@Repository
class FundraiserRepositoryImpl implements FundraiserRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Fundraiser> searchActiveWithinRadius(double lat, double lon, double radiusKm, Pageable pageable) {
        return Page.empty(pageable); // TODO: Implement this method using a spatial query; maybe PostGIS will be helpful
    }
}
