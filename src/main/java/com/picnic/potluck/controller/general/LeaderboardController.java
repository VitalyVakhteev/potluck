package com.picnic.potluck.controller.general;

import com.picnic.potluck.dto.user.LeaderboardEntry;
import com.picnic.potluck.service.general.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    public Page<LeaderboardEntry> getLeaderboard(Pageable pageable) {
        return leaderboardService.getLeaderboard(pageable);
    }

    @GetMapping("/{userId}")
    public LeaderboardEntry getUserEntry(@PathVariable UUID userId) {
        return leaderboardService.getUserEntry(userId);
    }
}
