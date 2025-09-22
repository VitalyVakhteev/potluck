package com.picnic.potluck.service.user;

import com.picnic.potluck.dto.user.ProfileRequest;
import com.picnic.potluck.dto.user.ProfileResponse;
import com.picnic.potluck.model.User;
import com.picnic.potluck.repository.user.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
	private final UserRepository users;

	@Transactional
	public ProfileResponse modifyProfile(UUID userId, ProfileRequest req) {
		var u = users.findById(userId).orElseThrow();

		if (req.firstName() != null) {
			u.setFirstName(trimToNull(req.firstName()));
		}
		if (req.lastName() != null) {
			u.setLastName(trimToNull(req.lastName()));
		}
		if (req.bio() != null) {
			u.setBio(trimToNull(req.bio()));
		}
		if (req.location() != null) {
			u.setLocation(trimToNull(req.location()));
		}
		if (req.bannerColor() != null) {
			u.setBannerColor(req.bannerColor());
		}
		if (req.displayName() != null) {
			u.setDisplayName(req.displayName());
		}
		if (req.displayEmail() != null) {
			u.setDisplayEmail(req.displayEmail());
		}
		if (req.displayPhone() != null) {
			u.setDisplayPhone(req.displayPhone());
		}
		// JPA dirty checking will persist changes at tx commit, so we convert to a DTO
		return toDto(u);
	}

	private static String trimToNull(String s) {
		if (s == null) return null;
		var t = s.trim();
		return t.isEmpty() ? null : t;
	}

	private static ProfileResponse toDto(User u) {
		return new ProfileResponse(
				u.getId(),
				u.getUsername(),
				u.getFirstName(),
				u.getLastName(),
				u.getBio(),
				u.getLocation(),
				u.getBannerColor(),
				u.isDisplayName(),
				u.isDisplayEmail(),
				u.isDisplayPhone(),
				u.getTotalPoints()
		);
	}
}
