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
			select s.user_id as userId, s.total as total, s.rnk as rnk from (
			  select t.user_id,
			         coalesce(sum(t.delta),0) as total,
			         rank() over (order by coalesce(sum(t.delta),0) desc ) as rnk
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

	@Query(value = """
			with totals as (
			    select t.user_id, coalesce(sum(t.delta), 0) as total
			    from points_transactions t
			    group by t.user_id
			),
			ranked as (
			    select user_id,
			        total,
			        rank() over (order by total desc, user_id asc) as rnk
			    from totals
			)
			select user_id as userId, total as total, rnk as rnk
			from ranked
			order by total desc, user_id asc
			""",
			countQuery = """
					select count(*) from (
					  select 1
					  from points_transactions t
					  group by t.user_id
					) x
					""", nativeQuery = true)
	Page<RankRow> leaderboard(Pageable pageable);
}
