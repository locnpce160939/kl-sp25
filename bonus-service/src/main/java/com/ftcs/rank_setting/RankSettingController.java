package com.ftcs.rank_setting;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.ftcs.bonusservice.BonusURL.RANK_SETTING;

@RestController
@RequestMapping(RANK_SETTING)
@RequiredArgsConstructor
public class RankSettingController {

    private final RankSettingService rankSettingService;

    @PutMapping()
    public ResponseEntity<RankSetting> updateMinPoint(@RequestBody RankSettingUpdateDto dto) {
        RankSetting updatedRank = rankSettingService.updateMinPoint(dto);
        return ResponseEntity.ok(updatedRank);
    }
}