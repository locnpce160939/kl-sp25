package com.ftcs.bonusservice.model;

import com.ftcs.bonusservice.constant.BonusTier;
import com.ftcs.bonusservice.constant.DriverGroup;
import com.ftcs.bonusservice.constant.RewardType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BonusConfiguration", schema = "dbo")
public class BonusConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BonusConfigurationId", nullable = false)
    private Long bonusConfigurationId;

    @Column(name = "BonusName", nullable = false, length = 100)
    private String bonusName;

    @Column(name = "Description", length = 500)
    private String description;

    @Column(name = "TargetTrips")
    private Integer targetTrips;

    @Column(name = "RevenueTarget")
    private Double revenueTarget;

    @Enumerated(EnumType.STRING)
    @Column(name = "RewardType", nullable = false)
    private RewardType rewardType;

    @Enumerated(EnumType.STRING)
    @Column(name = "DriverGroup", nullable = false)
    private DriverGroup driverGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "BonusTier", nullable = false)
    private BonusTier bonusTier;

    @Column(name = "BonusAmount", nullable = false)
    private Double bonusAmount;

    @Column(name = "StartDate", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "EndDate", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "IsActive", nullable = false)
    private Boolean isActive;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;
}
