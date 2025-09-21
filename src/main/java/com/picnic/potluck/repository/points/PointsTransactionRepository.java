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
    select user_id, total, rnk from (
      select t.user_id,
             COALESCE(SUM(t.delta),0) as total,
             RANK() over (order by COALESCE(SUM(t.delta),0) desc ) as rnk
      from points_transactions t
      group by t.user_id
    ) s
    where s.user_id = :userId
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
