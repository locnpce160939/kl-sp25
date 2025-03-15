package com.ftcs.bonusservice.repository;

import com.ftcs.bonusservice.model.DriverBonusProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DriverBonusProgressRepository extends JpaRepository<DriverBonusProgress, Long> {
    Page<DriverBonusProgress> findByAccountId(Integer accountId, Pageable pageable);

    Page<DriverBonusProgress> findByBonusConfigId(Long bonusConfigId, Pageable pageable);

    Optional<DriverBonusProgress> findByAccountIdAndBonusConfigId(Integer accountId, Long bonusConfigId);

    @Query("SELECT dbp FROM DriverBonusProgress dbp WHERE " +
            "dbp.bonusConfigId = ?1 AND dbp.isAchieved = true")
    List<DriverBonusProgress> findAchievedProgressByConfigId(Long bonusConfigId);

    @Query("SELECT COUNT(dbp) FROM DriverBonusProgress dbp WHERE " +
            "dbp.bonusConfigId = ?1 AND dbp.isAchieved = true")
    Long countAchievedProgressByConfigId(Long bonusConfigId);

    Optional<DriverBonusProgress> findByAccountIdAndBonusMonth(
            Integer accountId, Integer bonusMonth);



    List<DriverBonusProgress> findByIsAchievedTrueAndIsRewardedFalse();
}
