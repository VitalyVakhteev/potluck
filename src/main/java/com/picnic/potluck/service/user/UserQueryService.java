package com.picnic.potluck.service.user;

import com.picnic.potluck.dto.user.UserDetail;
import com.picnic.potluck.dto.user.UserSummary;
import com.picnic.potluck.model.User;
import com.picnic.potluck.repository.user.UserFavoriteFundraiserRepository;
import com.picnic.potluck.repository.user.UserFollowRepository;
import com.picnic.potluck.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserQueryService {
	private final UserRepository userRepository;
	private final UserFollowRepository userFollowRepository;
	private final UserFavoriteFundraiserRepository userFavoriteFundraiserRepository;

	@Transactional(readOnly = true)
	public UserDetail getUserForViewer(UUID targetUserId, @Nullable UUID viewerUserId) {
		var user = userRepository.findById(targetUserId).orElseThrow();
		return buildDetail(user, viewerUserId);
	}

	@Transactional(readOnly = true)
	public UserDetail getUserForViewer(String username, @Nullable UUID viewerUserId) {
		var user = userRepository.findByUsernameIgnoreCase(username).orElseThrow();
		return buildDetail(user, viewerUserId);
	}

	@Transactional(readOnly = true)
	public UserSummary getSummary(UUID userId) {
		var u = userRepository.findById(userId).orElseThrow();
		return new UserSummary(u.getId(), u.getUsername(), u.getTotalPoints(), u.getTotalFundraisers());
	}

	@Transactional(readOnly = true)
	public UserSummary getSummary(String username) {
		var u = userRepository.findByUsernameIgnoreCase(username).orElseThrow();
		return new UserSummary(u.getId(), u.getUsername(), u.getTotalPoints(), u.getTotalFundraisers());
	}

	@Transactional(readOnly = true)
	public Page<UserSummary> search(String q, Pageable pageable) {
		String term = q == null ? "" : q.trim();
		if (term.isEmpty()) return Page.empty(pageable);
		return userRepository.searchActiveByQuery(term, pageable).map(this::toSummary);
	}

	public UserSummary toSummary(User user) {
		return new UserSummary(
				user.getId(),
				user.getUsername(),
				user.getTotalPoints(),
				user.getTotalFundraisers()
		);
	}

	private UserDetail buildDetail(User user, @Nullable UUID viewerUserId) {

		long followers = userFollowRepository.countByUserId(user.getId());
		long following = userFollowRepository.countByFollowerId(user.getId());
		long favorites = userFavoriteFundraiserRepository.countByUserId(user.getId());

		boolean displayName = user.isDisplayName();
		boolean displayEmail = user.isDisplayEmail();
		boolean displayPhone = user.isDisplayPhone();

		String firstNameOut = (displayName ? user.getFirstName() : "...");
		String lastNameOut = (displayName ? user.getLastName() : "...");
		String emailOut = (displayEmail ? user.getEmail() : "...");
		String phoneOut = (displayPhone ? user.getPhoneNumber() : "...");

		return new UserDetail(
				user.getId(),
				user.getUsername(),
				user.getBio(),
				user.getLocation(),
				user.getBannerColor(),
				user.getTotalPoints(),
				user.getTotalFundraisers(),
				followers, following, favorites,
				firstNameOut, lastNameOut, emailOut, phoneOut,
				displayName, displayEmail, displayPhone
		);
	}
}
