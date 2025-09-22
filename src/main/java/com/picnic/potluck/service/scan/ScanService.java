package com.picnic.potluck.service.scan;

import com.picnic.potluck.dto.scan.ClaimRequest;
import com.picnic.potluck.dto.scan.ClaimResponse;
import com.picnic.potluck.model.Scan;
import com.picnic.potluck.repository.fundraiser.FundraiserRepository;
import com.picnic.potluck.repository.scan.ScanRepository;
import com.picnic.potluck.repository.user.UserRepository;
import com.picnic.potluck.service.points.PointsService;
import com.picnic.potluck.util.Source;
import com.picnic.potluck.util.Status;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScanService {
	private final QrService qrService;
	private final ScanRepository scanRepo;
	private final UserRepository users;
	private final FundraiserRepository funds;
	private final PointsService points;
	@Value("${app.base-points.per-scan}")
	private int perScan;

	@Transactional
	public ClaimResponse claim(UUID participantId, ClaimRequest req) {
		UUID fundraiserId = req.fundraiserId();
		UUID organizerId = req.organizerId();
		long exp = req.time();
		String sig = req.signature();

		qrService.verify(fundraiserId, organizerId, exp, sig);

		var participant = users.findById(participantId).orElseThrow();
		var organizer = users.findById(organizerId).orElseThrow();
		var fundraiser = funds.findById(fundraiserId).orElseThrow();

		if (!fundraiser.isReward()) {
			return new ClaimResponse(false, false, perScan, "no-reward");
		}

		var idemp = participantId + "." + fundraiserId;

		var existing = scanRepo.findByIdempotencyKey(idemp);
		if (existing.isPresent()) {
			return new ClaimResponse(true, false, perScan, "already-claimed");
		}

		var scan = Scan.builder()
				.fundraiser(fundraiser)
				.participant(participant)
				.organizer(organizer)
				.source(Source.QR)
				.status(Status.ACCEPTED)
				.idempotencyKey(idemp)
				.build();
		scanRepo.save(scan);

		points.awardScanPoints(organizer, participant, fundraiser, scan, perScan);

		return new ClaimResponse(false, true, perScan, "ok");
	}
}
