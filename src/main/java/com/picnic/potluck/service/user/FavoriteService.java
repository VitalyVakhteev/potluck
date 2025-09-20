package com.picnic.potluck.service.user;

import com.picnic.potluck.dto.user.FavoriteResponse;
import com.picnic.potluck.model.Fundraiser;
import com.picnic.potluck.repository.fundraiser.FundraiserRepository;
import com.picnic.potluck.repository.user.UserFavoriteFundraiserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

// My favorite service :) - Vitaly
@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final UserFavoriteFundraiserRepository favoriteRepository;
    private final FundraiserRepository fundraisers;

    @Transactional
    public FavoriteResponse add(UUID userId, UUID fundraiserId) {
        Fundraiser f = fundraisers.findById(fundraiserId).orElseThrow();
        favoriteRepository.upsert(userId, f.getId());
        return new FavoriteResponse(userId, f.getId(), true);
    }

    @Transactional
    public FavoriteResponse remove(UUID userId, UUID fundraiserId) {
        favoriteRepository.deleteByUserIdAndFundraiserId(userId, fundraiserId);
        return new FavoriteResponse(userId, fundraiserId, false);
    }
}
