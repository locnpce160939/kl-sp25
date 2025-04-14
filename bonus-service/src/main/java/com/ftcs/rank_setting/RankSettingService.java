package com.ftcs.rank_setting;

import com.ftcs.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RankSettingService {

    private final RankSettingRepository rankSettingRepository;

    @Transactional
    public RankSetting updateMinPoint(RankSettingUpdateDto dto) {
        RankSetting rankSetting = rankSettingRepository.findById(dto.getId())
                .orElseThrow(() -> new BadRequestException("RankSetting with ID " + dto.getId() + " not found"));

        if (dto.getMinPoint() == null || dto.getMinPoint() < 0) {
            throw new BadRequestException("MinPoint must be non-negative and not null");
        }

        Optional<RankSetting> conflictingRank = rankSettingRepository.findByMinPointAndNotId(dto.getMinPoint(), dto.getId());
        if (conflictingRank.isPresent()) {
            throw new BadRequestException("MinPoint " + dto.getMinPoint() + " is already used by another rank");
        }

        rankSetting.setMinPoint(dto.getMinPoint());
        rankSetting.setUpdateDate(LocalDateTime.now());

        return rankSettingRepository.save(rankSetting);
    }
}