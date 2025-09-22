package com.picnic.potluck.repository.user;

import com.picnic.potluck.model.UserFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.UUID;

public interface UserFollowRepository extends JpaRepository<UserFollow, UUID> {
	@Modifying
	@Query(value = """
			insert into user_followers (id, user_id, follower_user_id, created_at, updated_at)
			values (gen_random_uuid(), :userId, :followerId, now(), now())
			on conflict on constraint pk_user_follow do nothing
			""", nativeQuery = true)
	void upsert(UUID userId, UUID followerId);

	void deleteByUserIdAndFollowerId(UUID userId, UUID followerId);

	long countByUserId(UUID userId);

	long countByFollowerId(UUID followerUserId);

	Page<UserFollow> findByUserId(UUID userId, Pageable p);

	Page<UserFollow> findByFollowerId(UUID followerId, Pageable p);
}
