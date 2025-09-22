package com.picnic.potluck.service.fundraiser;

import com.picnic.potluck.dto.fundraiser.FundraiserDetail;
import com.picnic.potluck.dto.fundraiser.FundraiserSummary;
import com.picnic.potluck.dto.fundraiser.NearRequest;
import com.picnic.potluck.model.Fundraiser;
import com.picnic.potluck.model.User;
import com.picnic.potluck.repository.user.UserRepository;
import com.picnic.potluck.repository.fundraiser.FundraiserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FundraiserQueryService {
	private final FundraiserRepository fundraisers;
	private final UserRepository users;

	@Transactional(readOnly = true)
	public FundraiserDetail getOne(UUID id) {
		var f = fundraisers.findById(id).orElseThrow();
		return toDetail(f);
	}

	@Transactional(readOnly = true)
	public Page<FundraiserSummary> listActive(Pageable pageable) {
		return fundraisers.findByActiveTrue(pageable).map(this::toSummary);
	}

	@Transactional(readOnly = true)
	public Page<FundraiserSummary> search(String q, Pageable pageable) {
		String term = q == null ? "" : q.trim();
		if (term.isEmpty()) return Page.empty(pageable);
		return fundraisers.searchActiveByQuery(term, pageable).map(this::toSummary);
	}

	@Transactional(readOnly = true)
	public Page<FundraiserSummary> feed(UUID followerId, Pageable pageable) {
		return fundraisers.feedForFollower(followerId, pageable).map(this::toSummary);
	}

	@Transactional(readOnly = true)
	public Page<FundraiserSummary> listByOrganizer(UUID organizerId, Pageable pageable) {
		User organizer = users.findById(organizerId).orElseThrow();
		return fundraisers.findByOrganizerOrderByCreatedAtDesc(organizer, pageable).map(this::toSummary);
	}

	@Transactional(readOnly = true)
	public Page<FundraiserSummary> near(NearRequest req, Pageable pageable) {
		return fundraisers.searchActiveWithinRadius(req.lat(), req.lon(), req.radiusKm(), pageable).map(this::toSummary);
	}

	@Transactional(readOnly = true)
	public Page<FundraiserSummary> startingSoon(Pageable pageable) {
		return fundraisers.startingSoon(pageable).map(this::toSummary);
	}

	@Transactional(readOnly = true)
	public Page<FundraiserSummary> endingSoon(Pageable pageable) {
		return fundraisers.endingSoon(pageable).map(this::toSummary);
	}

	@Transactional(readOnly = true)
	public Page<FundraiserSummary> list(UUID userId, Pageable pageable) {
		return fundraisers.findFavorites(userId, pageable).map(this::toSummary);
	}

	private FundraiserSummary toSummary(Fundraiser f) {
		return new FundraiserSummary(
				f.getId(), f.getTitle(), f.isActive(),
				f.getLat(), f.getLon(),
				f.getStarts_at(), f.getEnds_at());
	}

	private FundraiserDetail toDetail(Fundraiser f) {
		return new FundraiserDetail(
				f.getId(), f.getTitle(), f.getDescription(), f.getEmail(), f.getPhone(),
				f.isActive(), f.getLat(), f.getLon(), f.getStarts_at(), f.getEnds_at(),
				f.getOrganizer().getId(), f.getOrganizer().getUsername()
		);
	}
}
