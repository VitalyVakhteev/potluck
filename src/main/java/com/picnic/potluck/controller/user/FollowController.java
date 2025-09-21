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
@RequestMapping("/api/users/follows")
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

    @GetMapping("/followers/{userId}")
    public Page<UserSummary> followers(@PathVariable UUID userId, Pageable p) {
        return followService.followers(userId, p);
    }

    @GetMapping("/following/{userId}")
    public Page<UserSummary> following(@PathVariable UUID userId, Pageable p) {
        return followService.following(userId, p);
    }

    @GetMapping("/followers/me")
    public Page<UserSummary> myFollowers(@AuthenticationPrincipal Jwt jwt, Pageable p) {
        return followService.followers(UUID.fromString(jwt.getSubject()), p);
    }

    @GetMapping("/following/me")
    public Page<UserSummary> myFollowing(@AuthenticationPrincipal Jwt jwt, Pageable p) {
        return followService.following(UUID.fromString(jwt.getSubject()), p);
    }
}