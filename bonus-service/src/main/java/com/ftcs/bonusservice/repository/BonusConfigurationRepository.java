package com.ftcs.bonusservice.repository;

import com.ftcs.bonusservice.constant.BonusTier;
import com.ftcs.bonusservice.constant.DriverGroup;
import com.ftcs.bonusservice.constant.RewardType;
import com.ftcs.bonusservice.model.BonusConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BonusConfigurationRepository extends JpaRepository<BonusConfiguration, Long> {
    List<BonusConfiguration> findByIsActiveTrue();

    List<BonusConfiguration> findByStartDateBeforeAndEndDateAfterAndIsActiveTrue(
            LocalDateTime currentDate, LocalDateTime currentDate2);

    @Query("SELECT bc FROM BonusConfiguration bc WHERE " +
            "bc.startDate <= ?1 AND bc.endDate >= ?1 AND bc.isActive = true " +
            "ORDER BY bc.createdAt DESC")
    Page<BonusConfiguration> findActiveConfigurations(LocalDateTime currentDate, Pageable pageable);

    // In BonusConfigurationRepository
    @Query("SELECT b FROM BonusConfiguration b WHERE b.driverGroup = :driverGroup " +
            "AND b.bonusTier = :bonusTier " +
            "AND b.isActive = true " +
            "AND b.startDate <= :currentDate " +
            "AND b.endDate >= :currentDate")
    Optional<BonusConfiguration> findActiveConfigurationForDriverGroupAndTier(
            @Param("driverGroup") DriverGroup driverGroup,
            @Param("bonusTier") BonusTier bonusTier,
            @Param("currentDate") LocalDateTime currentDate);

    // For filtering by driver group
    Page<BonusConfiguration> findByDriverGroup(DriverGroup driverGroup, Pageable pageable);

    // For filtering by reward type
    Page<BonusConfiguration> findByRewardType(RewardType rewardType,Pageable pageable);

    // For filtering by date range
    List<BonusConfiguration> findByStartDateBetweenAndEndDateBetween(
            LocalDateTime startFrom, LocalDateTime startTo,
            LocalDateTime endFrom, LocalDateTime endTo);

}
