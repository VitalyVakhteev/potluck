package com.picnic.potluck.controller.user;

import com.picnic.potluck.dto.user.FollowResponse;
import com.picnic.potluck.dto.user.UserSummary;
import com.picnic.potluck.service.user.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/me/follows")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @PostMapping("/{targetId}")
    public FollowResponse follow(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID targetId) {
        return followService.follow(UUID.fromString(jwt.getSubject()), targetId);
    }

    @DeleteMapping("/{targetId}")
    public FollowResponse unfollow(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID targetId) {
        return followService.unfollow(UUID.fromString(jwt.getSubject()), targetId);
    }
    @GetMapping("/followers")
    public Page<UserSummary> followers(@AuthenticationPrincipal Jwt jwt, Pageable p) {
        return followService.followers(UUID.fromString(jwt.getSubject()), p);
    }
    @GetMapping("/following")
    public Page<UserSummary> following(@AuthenticationPrincipal Jwt jwt, Pageable p) {
        return followService.following(UUID.fromString(jwt.getSubject()), p);
    }
}