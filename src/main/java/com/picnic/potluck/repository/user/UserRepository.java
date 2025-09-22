package com.picnic.potluck.repository.user;

import com.picnic.potluck.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
	Optional<User> findByUsernameIgnoreCase(String username);

	Optional<User> findByEmailIgnoreCase(String email);

	Optional<User> findByPhoneNumberIgnoreCase(String phone_number);

	@Modifying
	@Query("update User u set u.totalPoints = u.totalPoints + :delta where u.id = :userId")
	void incrementTotalPoints(@Param("userId") UUID userId, @Param("delta") int delta);

	@Modifying
	@Query("update User u set u.totalFundraisers = u.totalFundraisers + :delta where u.id = :userId")
	void incrementTotalFundraisers(@Param("userId") UUID userId, @Param("delta") int delta);

	@Query("""
			select u from User u
			where u.active = true
			  and (
			    lower(u.username) like lower(concat('%', :q, '%')) or
			    lower(u.email) like lower(concat('%', :q, '%')) or
			    lower(u.phoneNumber) like lower(concat('%', :q, '%'))
			  )
			""")
	Page<User> searchActiveByQuery(String q, Pageable pageable);
}
