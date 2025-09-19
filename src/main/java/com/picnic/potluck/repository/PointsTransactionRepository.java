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

    @Query(value = """
    SELECT user_id, total, rnk FROM (
      SELECT t.user_id,
             COALESCE(SUM(t.delta),0) AS total,
             RANK() OVER (ORDER BY COALESCE(SUM(t.delta),0) DESC) AS rnk
      FROM points_transactions t
      GROUP BY t.user_id
    ) s
    WHERE s.user_id = :userId
    """, nativeQuery = true)
    RankRow rankForUser(@Param("userId") UUID userId);

    @Query(value = """
    SELECT 1 + COUNT(*) AS rnk
    FROM users u2
    WHERE u2.total_points > (SELECT u1.total_points FROM users u1 WHERE u1.id = :userId)
    """, nativeQuery = true)
    long rankFromUsersTotals(@Param("userId") UUID userId);

    interface LeaderboardRow {
        UUID getUserId();
        Long getTotal();
    }

    interface RankRow {
        UUID getUserId();
        long getTotal();
        long getRnk();
    }

    @Query("""
         select p.user.id as userId, sum(p.delta) as total
         from PointsTransaction p
         group by p.user.id
         order by total desc
         """)
    Page<LeaderboardRow> leaderboard(Pageable pageable);
}
