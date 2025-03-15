package com.ftcs.bonusservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverBonusProgressDTO {
    private Long driverBonusProgressId;
    private Integer accountId;
    private Long bonusConfigId;
    private Integer completedTrips;
    private Double currentRevenue;
    private Double progressPercentage;
    private Boolean isAchieved;
    private LocalDateTime achievedDate;
    private Boolean isRewarded;
    private LocalDateTime rewardedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Additional status fields
    private Boolean tripRequirementMet;
    private Boolean revenueRequirementMet;
    private String bonusStatus;

    // Related entities
    private BonusConfigurationDTO bonusConfiguration;
    private AccountDTO driverAccount;
}
