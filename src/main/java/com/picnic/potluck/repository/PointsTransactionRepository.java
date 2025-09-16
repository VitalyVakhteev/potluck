package com.picnic.potluck.repository;

import com.picnic.potluck.model.PointsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PointsTransactionRepository extends JpaRepository<PointsTransaction, UUID> {
    @Query("select coalesce(sum(p.delta),0) from PointsTransaction p where p.user.id = :userId")
    int totalPointsForUser(@Param("userId") UUID userId);

    interface LeaderboardRow {
        UUID getUserId();
        Long getTotal();
    }

    @Query("""
         select p.user.id as userId, sum(p.delta) as total
         from PointsTransaction p
         group by p.user.id
         order by total desc
         """)
    Page<LeaderboardRow> leaderboard(Pageable pageable);
}
