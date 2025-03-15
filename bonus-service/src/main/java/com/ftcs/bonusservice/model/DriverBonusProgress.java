package com.ftcs.bonusservice.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DriverBonusProgress", schema = "dbo")
public class DriverBonusProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DriverBonusProgressId", nullable = false)
    private Long driverBonusProgressId;

    @Column(name = "AccountId", nullable = false)
    private Integer accountId;

    @Column(name = "BonusConfigId", nullable = false)
    private Long bonusConfigId;

    @Column(name = "CompletedTrips", nullable = false)
    private Integer completedTrips;

    @Column(name = "CurrentRevenue", nullable = false)
    private Double currentRevenue;

    @Column(name = "ProgressPercentage", nullable = false)
    private Double progressPercentage;

    @Column(name = "BonusMonth", nullable = false)
    private Integer bonusMonth; // Month number (1-12)

    @Column(name = "IsAchieved", nullable = false)
    private Boolean isAchieved;

    @Column(name = "AchievedDate")
    private LocalDateTime achievedDate;

    @Column(name = "IsRewarded", nullable = false)
    private Boolean isRewarded;

    @Column(name = "RewardedDate")
    private LocalDateTime rewardedDate;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;
}
