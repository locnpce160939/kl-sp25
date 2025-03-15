package com.ftcs.bonusservice.dto;

import com.ftcs.bonusservice.constant.BonusTier;
import com.ftcs.bonusservice.constant.DriverGroup;
import com.ftcs.bonusservice.constant.RewardType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BonusConfigurationCreateRequest {
    private String bonusName;
    private String description;
    private Integer targetTrips;
    private Double revenueTarget;
    private RewardType rewardType;
    private DriverGroup driverGroup;
    private BonusTier bonusTier;
    private Double bonusAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
