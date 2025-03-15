package com.ftcs.bonusservice.dto;

import com.ftcs.bonusservice.constant.DriverGroup;
import com.ftcs.bonusservice.constant.RewardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponseDTO {
    private Long bonusConfigId;
    private String bonusName;
    private RewardType rewardType;
    private DriverGroup driverGroup;
    private Long totalDrivers;
    private Long achievedDrivers;
    private Double achievementRate;
    private Double totalRewardAmount;
    private Double averageCompletionTime;
}
