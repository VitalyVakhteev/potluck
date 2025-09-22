package com.picnic.potluck.service.user;

import com.picnic.potluck.dto.user.FollowResponse;
import com.picnic.potluck.dto.user.UserSummary;
import com.picnic.potluck.repository.user.UserFollowRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FollowService {
	private final UserFollowRepository followRepository;

	@Transactional
	public FollowResponse follow(UUID me, UUID target) {
		if (me.equals(target)) throw new IllegalArgumentException("cannot follow self");
		followRepository.upsert(target, me);
		return new FollowResponse(me, target, true);
	}

	@Transactional
	public FollowResponse unfollow(UUID me, UUID target) {
		followRepository.deleteByUserIdAndFollowerId(target, me);
		return new FollowResponse(me, target, false);
	}

	@Transactional
	public Page<UserSummary> followers(UUID user, Pageable p) {
		return followRepository.findByUserId(user, p)
				.map(f -> new UserSummary(f.getFollower().getId(), f.getFollower().getUsername(),
						f.getFollower().getTotalPoints(), f.getFollower().getTotalFundraisers()));
	}

	@Transactional
	public Page<UserSummary> following(UUID user, Pageable p) {
		return followRepository.findByFollowerId(user, p)
				.map(f -> new UserSummary(f.getUser().getId(), f.getUser().getUsername(),
						f.getUser().getTotalPoints(), f.getUser().getTotalFundraisers()));
	}
}
