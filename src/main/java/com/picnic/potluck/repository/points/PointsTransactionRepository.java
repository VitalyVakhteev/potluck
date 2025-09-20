package com.picnic.potluck.repository.points;

import com.picnic.potluck.model.PointsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PointsTransactionRepository extends JpaRepository<PointsTransaction, UUID> {
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
    Page<RankRow> leaderboard(Pageable pageable);
}
