package com.ftcs.bonusservice.dto;

import com.ftcs.bonusservice.constant.BonusTier;
import com.ftcs.bonusservice.constant.DriverGroup;
import com.ftcs.bonusservice.constant.RewardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BonusConfigurationDTO {
    private Long bonusConfigurationId;
    private String bonusName;
    private String description;
    private Integer targetTrips;
    private Double revenueTarget;
    private RewardType rewardType; // Added field matching the model
    private DriverGroup driverGroup; // Added field matching the model
    private BonusTier bonusTier;
    private Double bonusAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
