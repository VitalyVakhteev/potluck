package com.picnic.potluck.repository.user;

import com.picnic.potluck.model.UserFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.UUID;

public interface UserFollowRepository extends JpaRepository<UserFollow, UUID> {
    @Modifying
    @Query(value = """
      INSERT INTO user_followers (id, user_id, follower_user_id, created_at, updated_at)
      VALUES (gen_random_uuid(), :userId, :followerId, now(), now())
      ON CONFLICT ON CONSTRAINT pk_user_follow DO NOTHING
      """, nativeQuery = true)
    void upsert(UUID userId, UUID followerId);

    void deleteByUserIdAndFollowerId(UUID userId, UUID followerId);
    long countByUserId(UUID userId);
    long countByFollowerId(UUID followerUserId);
    Page<UserFollow> findByUserId(UUID userId, Pageable p);
    Page<UserFollow> findByFollowerId(UUID followerId, Pageable p);
}
