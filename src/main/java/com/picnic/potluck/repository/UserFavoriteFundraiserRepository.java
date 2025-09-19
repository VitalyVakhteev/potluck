package com.picnic.potluck.repository;

import com.picnic.potluck.model.UserFavoriteFundraiser;
import org.springframework.data.jpa.repository.*;

import java.util.UUID;

public interface UserFavoriteFundraiserRepository extends JpaRepository<UserFavoriteFundraiser, UUID> {
    @Modifying
    @Query(value = """
      INSERT INTO user_favorite_fundraisers (id, user_id, fundraiser_id, created_at, updated_at)
      VALUES (gen_random_uuid(), :userId, :fundraiserId, now(), now())
      ON CONFLICT ON CONSTRAINT pk_user_fav DO NOTHING
      """, nativeQuery = true)
    void upsert(UUID userId, UUID fundraiserId);

    void deleteByUserIdAndFundraiserId(UUID userId, UUID fundraiserId);
    boolean existsByUserIdAndFundraiserId(UUID userId, UUID fundraiserId);
}
