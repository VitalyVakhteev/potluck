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
	public LeaderboardEntry getUserEntry(String username) {
		var user = userRepo.findByUsernameIgnoreCase(username).orElseThrow();
		var rankUser = pointsRepo.rankForUser(user.getId());

		return new LeaderboardEntry(rankUser.getUserId(), user.getUsername(), rankUser.getTotal(), rankUser.getRnk());
	}
}
