package com.picnic.potluck.repository.user;

import com.picnic.potluck.model.UserFavoriteFundraiser;
import org.springframework.data.jpa.repository.*;

import java.util.UUID;

public interface UserFavoriteFundraiserRepository extends JpaRepository<UserFavoriteFundraiser, UUID> {
	@Modifying
	@Query(value = """
			insert into user_favorite_fundraisers (id, user_id, fundraiser_id, created_at, updated_at)
			values (gen_random_uuid(), :userId, :fundraiserId, now(), now())
			on conflict on constraint pk_user_fav do nothing
			""", nativeQuery = true)
	void upsert(UUID userId, UUID fundraiserId);

	void deleteByUserIdAndFundraiserId(UUID userId, UUID fundraiserId);

	long countByUserId(UUID userId);
}
