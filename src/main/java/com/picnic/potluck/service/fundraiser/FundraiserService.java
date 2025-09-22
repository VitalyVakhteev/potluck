package com.picnic.potluck.service.fundraiser;

import com.picnic.potluck.dto.fundraiser.*;
import com.picnic.potluck.model.Fundraiser;
import com.picnic.potluck.model.User;
import com.picnic.potluck.repository.fundraiser.FundraiserRepository;
import com.picnic.potluck.repository.user.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FundraiserService {

    private final FundraiserRepository fundraiserRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateFundraiserResponse add(UUID organizerId, CreateFundraiserRequest req) {
        if (req.startsAt() != null && req.endsAt() != null && !req.endsAt().isAfter(req.startsAt())) {
            throw new IllegalArgumentException("endsAt must be after startsAt");
        }

        User organizer = userRepository.findById(organizerId).orElseThrow();

        var fundraiser = Fundraiser.builder()
                .organizer(organizer)
                .active(true)
                .title(req.title())
                .description(req.description())
                .email(req.email())
                .phone(req.phoneNumber())
                .lat(req.lat())
                .lon(req.lon())
                .starts_at(req.startsAt())
                .ends_at(req.endsAt())
                .reward(req.reward())
                .build();

        fundraiserRepository.save(fundraiser);
        userRepository.incrementTotalFundraisers(organizerId, +1);

        return new CreateFundraiserResponse(
                fundraiser.getId(),
                fundraiser.getTitle(),
                fundraiser.getStarts_at(),
                fundraiser.getEnds_at()
        );
    }

    @Transactional
    public PatchFundraiserResponse edit(UUID organizerId, PatchFundraiserRequest req) {
        if (req.startsAt() != null && req.endsAt() != null && !req.endsAt().isAfter(req.startsAt())) {
            throw new IllegalArgumentException("endsAt must be after startsAt");
        }

        var fundraiser = fundraiserRepository.findById(req.id()).orElseThrow();
        if (!fundraiser.getOrganizer().getId().equals(organizerId))
            throw new AccessDeniedException("Not your fundraiser");

        if (req.title() != null) {
            fundraiser.setTitle(req.title());
        }
        if (req.description() != null) {
            fundraiser.setDescription(req.description());
        }
        if (req.email() != null) {
            fundraiser.setEmail(req.email());
        }
        if (req.phoneNumber() != null) {
            fundraiser.setPhone(req.phoneNumber());
        }
        if (req.lat() != null) {
            fundraiser.setLat(req.lat());
        }
        if (req.lon() != null) {
            fundraiser.setLon(req.lon());
        }
        if (req.startsAt() != null) {
            fundraiser.setStarts_at(req.startsAt());
        }
        if (req.endsAt() != null) {
            fundraiser.setEnds_at(req.endsAt());
        }
        if (req.reward() != null) {
            fundraiser.setReward(req.reward());
        }

        fundraiserRepository.save(fundraiser);

        return new PatchFundraiserResponse(
                fundraiser.getId(),
                fundraiser.getTitle(),
                fundraiser.getStarts_at(),
                fundraiser.getEnds_at()
        );
    }

    @Transactional
    public DeleteFundraiserResponse delete(UUID organizerId, UUID fundraiserId) {
        var f = fundraiserRepository.findById(fundraiserId).orElseThrow();
        if (!f.getOrganizer().getId().equals(organizerId)) throw new AccessDeniedException("Not your fundraiser");
        fundraiserRepository.delete(f);
        if (f.isActive()) userRepository.incrementTotalFundraisers(organizerId, -1);
        return new DeleteFundraiserResponse(
                fundraiserId
        );
    }
}