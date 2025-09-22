package com.picnic.potluck.repository.fundraiser;

import com.picnic.potluck.model.Fundraiser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface FundraiserRepositoryCustom {
	@EntityGraph(attributePaths = "organizer")
	Page<Fundraiser> searchActiveWithinRadius(double lat, double lon, double radiusKm, Pageable pageable);

	@EntityGraph(attributePaths = "organizer")
	Page<Fundraiser> startingSoon(Pageable pageable);

	@EntityGraph(attributePaths = "organizer")
	Page<Fundraiser> endingSoon(Pageable pageable);
}

@Repository
@RequiredArgsConstructor
class FundraiserRepositoryImpl implements FundraiserRepositoryCustom {
	@PersistenceContext
	private final EntityManager em;

	@Override
	public Page<Fundraiser> searchActiveWithinRadius(double lat, double lon, double radiusKm, Pageable pageable) {
		// 1 deg lat ~ 111.32 km; 1 deg lon shrinks by cos(lat)
		double dLat = radiusKm / 111.32;
		double dLon = radiusKm / (111.32 * Math.cos(Math.toRadians(lat)));

		// Using Haversine formula to calculate distance
		String select = """
				select * from (
				    select f.*,
				         (6371 * acos(
				           cos(radians(:lat)) * cos(radians(f.lat)) *
				           cos(radians(f.lon) - radians(:lon)) +
				           sin(radians(:lat)) * sin(radians(f.lat))
				         )) as distance_km
				    from fundraisers f
				    where f.active = true
				        and f.lat between :minLat and :maxLat
				        and f.lon between :minLon and :maxLon
				) s
				where s.distance_km <= :radiusKm
				order by s.distance_km
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

		String countSql = "select count(*) from (" + select + ") t";
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

	@Override
	public Page<Fundraiser> startingSoon(Pageable pageable) {
		var query = em.createQuery("""
				select f from Fundraiser f
				where f.active = true and f.starts_at is not null and f.starts_at >= CURRENT_TIMESTAMP
				order by f.starts_at asc
				""", Fundraiser.class);

		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());
		var list = query.getResultList();

		var count = em.createQuery("""
				select count(f) from Fundraiser f
				where f.active = true and f.starts_at is not null and f.starts_at >= CURRENT_TIMESTAMP
				""", Long.class).getSingleResult();

		return new PageImpl<>(list, pageable, count);
	}

	@Override
	public Page<Fundraiser> endingSoon(Pageable pageable) {
		var query = em.createQuery("""
				select f from Fundraiser f
				where f.active = true and f.ends_at is not null and f.ends_at >= CURRENT_TIMESTAMP
				order by f.ends_at asc
				""", Fundraiser.class);

		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());
		var list = query.getResultList();

		var count = em.createQuery("""
				select count(f) from Fundraiser f
				where f.active = true and f.ends_at is not null and f.ends_at >= CURRENT_TIMESTAMP
				""", Long.class).getSingleResult();

		return new PageImpl<>(list, pageable, count);
	}
}