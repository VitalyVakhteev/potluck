package com.picnic.potluck.service.general;

import com.picnic.potluck.dto.user.LeaderboardEntry;
import com.picnic.potluck.model.User;
import com.picnic.potluck.repository.points.PointsTransactionRepository;
import com.picnic.potluck.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

	private final PointsTransactionRepository pointsRepo;
	private final UserRepository userRepo;

	@Transactional(readOnly = true)
	public Page<LeaderboardEntry> getLeaderboard(Pageable pageable) {
		return pointsRepo.leaderboard(pageable)
				.map(row -> {
					User user = userRepo.findById(row.getUserId())
							.orElseThrow();
					return new LeaderboardEntry(
							row.getUserId(),
							user.getUsername(),
							row.getTotal(),
							row.getRnk()
					);
				});
	}

	@Transactional(readOnly = true)
	public LeaderboardEntry getUserEntry(UUID userId) {
		var rankUser = pointsRepo.rankForUser(userId);
		var username = userRepo.findById(userId).orElseThrow().getUsername();

		return new LeaderboardEntry(rankUser.getUserId(), username, rankUser.getTotal(), rankUser.getRnk());
	}
}
