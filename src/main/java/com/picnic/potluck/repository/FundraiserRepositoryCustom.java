package com.picnic.potluck.repository;

import com.picnic.potluck.model.Fundraiser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface FundraiserRepositoryCustom {
    Page<Fundraiser> searchActiveWithinRadius(double lat, double lon, double radiusKm, Pageable pageable);
}

@Repository
@RequiredArgsConstructor
class FundraiserRepositoryImpl implements FundraiserRepositoryCustom {
    @PersistenceContext private final EntityManager em;

    @Override
    public Page<Fundraiser> searchActiveWithinRadius(double lat, double lon, double radiusKm, Pageable pageable) {
        // 1 deg lat ~ 111.32 km; 1 deg lon shrinks by cos(lat)
        double dLat = radiusKm / 111.32;
        double dLon = radiusKm / (111.32 * Math.cos(Math.toRadians(lat)));

        // Using Haversine formula to calculate distance
        String select = """
        SELECT * FROM (
            SELECT f.*,
                 (6371 * acos(
                   cos(radians(:lat)) * cos(radians(f.lat)) *
                   cos(radians(f.lon) - radians(:lon)) +
                   sin(radians(:lat)) * sin(radians(f.lat))
                 )) AS distance_km
            FROM fundraisers f
            WHERE f.active = true
                AND f.lat BETWEEN :minLat AND :maxLat
                AND f.lon BETWEEN :minLon AND :maxLon
        ) s
        WHERE s.distance_km <= :radiusKm
        ORDER BY s.distance_km
        """;

        var query = em.createNativeQuery(select, Fundraiser.class)
                .setParameter("lat", lat)
                .setParameter("lon", lon)
                .setParameter("minLat", lat - dLat)
                .setParameter("maxLat", lat + dLat)
                .setParameter("minLon", lon - dLon)
                .setParameter("maxLon", lon + dLon)
                .setParameter("radiusKm", radiusKm);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        @SuppressWarnings("unchecked")
        List<Fundraiser> content = query.getResultList();

        String countSql = "SELECT count(*) FROM (" + select + ") t";
        var count = ((Number) em.createNativeQuery(countSql)
                .setParameter("lat", lat)
                .setParameter("lon", lon)
                .setParameter("minLat", lat - dLat)
                .setParameter("maxLat", lat + dLat)
                .setParameter("minLon", lon - dLon)
                .setParameter("maxLon", lon + dLon)
                .setParameter("radiusKm", radiusKm)
                .getSingleResult()).longValue();

        return new PageImpl<>(content, pageable, count);
    }
}