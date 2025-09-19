package com.picnic.potluck.repository;

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
    Page<User> findAllByOrderByTotalPointsDesc(Pageable pageable);
    boolean existsByEmailIgnoreCase(String email);

    @Modifying
    @Query("update User u set u.totalPoints = u.totalPoints + :delta where u.id = :userId")
    void incrementTotalPoints(@Param("userId") UUID userId, @Param("delta") int delta);
}
