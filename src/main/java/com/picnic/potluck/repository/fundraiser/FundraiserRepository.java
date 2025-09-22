package com.picnic.potluck.repository.fundraiser;

import com.picnic.potluck.model.Fundraiser;
import com.picnic.potluck.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FundraiserRepository extends JpaRepository<Fundraiser, UUID>, FundraiserRepositoryCustom {
	Optional<Fundraiser> findById(UUID id);

	@EntityGraph(attributePaths = "organizer")
	Page<Fundraiser> findByActiveTrue(Pageable pageable);

	@EntityGraph(attributePaths = "organizer")
	Page<Fundraiser> findByOrganizerOrderByCreatedAtDesc(User organizer, Pageable pageable);

	@Query("""
			select f from Fundraiser f
			where f.active = true
			  and (
			    lower(f.title) like lower(concat('%', :q, '%')) or
			    lower(f.description) like lower(concat('%', :q, '%'))
			  )
			""")
	Page<Fundraiser> searchActiveByQuery(String q, Pageable pageable);

	@Query("""
			select f from Fundraiser f
			where exists (
			    select 1 from UserFavoriteFundraiser uf
			    where uf.user.id = :userId and uf.fundraiser.id = f.id
			)
			""")
	Page<Fundraiser> findFavorites(UUID userId, Pageable pageable);

	@Query("""
			select f from Fundraiser f
			where f.active = true
			  and exists (
			    select 1 from UserFollow uf
			    where uf.follower.id = :followerId
			      and uf.user.id = f.organizer.id
			  )
			order by f.createdAt desc
			""")
	Page<Fundraiser> feedForFollower(UUID followerId, Pageable pageable);
}