package com.picnic.potluck.service.points;

import com.picnic.potluck.model.Fundraiser;
import com.picnic.potluck.model.PointsTransaction;
import com.picnic.potluck.model.Scan;
import com.picnic.potluck.model.User;
import com.picnic.potluck.repository.points.PointsTransactionRepository;
import com.picnic.potluck.repository.user.UserRepository;
import com.picnic.potluck.util.Reason;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointsService {
    public final PointsTransactionRepository pointsTxRepo;
    public final UserRepository userRepo;

    @Transactional
    public void awardScanPoints(User organizer, User participant, Fundraiser f, Scan scan, int pointsEach) {
        pointsTxRepo.saveAll(List.of(
                PointsTransaction.builder().user(organizer).fundraiser(f).scan(scan).delta(pointsEach).reason(Reason.SCAN_EARN).build(),
                PointsTransaction.builder().user(participant).fundraiser(f).scan(scan).delta(pointsEach).reason(Reason.SCAN_EARN).build()
        ));

        userRepo.incrementTotalPoints(organizer.getId(), pointsEach);
        userRepo.incrementTotalPoints(participant.getId(), pointsEach);
    }
}
