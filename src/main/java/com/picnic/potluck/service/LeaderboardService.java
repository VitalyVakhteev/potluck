package com.picnic.potluck.service;

import com.picnic.potluck.model.User;
import com.picnic.potluck.repository.PointsTransactionRepository;
import com.picnic.potluck.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final PointsTransactionRepository pointsRepo;
    private final UserRepository userRepo;

    public record LeaderboardEntry(UUID userId, String username, long totalPoints) {}

    public Page<LeaderboardEntry> getLeaderboard(Pageable pageable) {
        return pointsRepo.leaderboard(pageable)
                .map(row -> {
                    User user = userRepo.findById(row.getUserId())
                            .orElseThrow();
                    return new LeaderboardEntry(
                            row.getUserId(),
                            user.getUsername(),
                            row.getTotal()
                    );
                });
    }

    public LeaderboardEntry getUserEntry(UUID userId) {
        long total = pointsRepo.totalPointsForUser(userId);
        long rank = pointsRepo.countUsersWithMorePoints(total) + 1;

        return new LeaderboardEntry(userId, total, rank);
    }
}
