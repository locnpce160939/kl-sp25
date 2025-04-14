package com.ftcs.rank_setting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RankSettingRepository extends JpaRepository<RankSetting, Integer> {
    @Query("SELECT r FROM RankSetting r WHERE r.minPoint <= :points ORDER BY r.minPoint DESC LIMIT 1")
    RankSetting findRankByPoints(Integer points);

    @Query("SELECT r FROM RankSetting r WHERE r.minPoint = :minPoint AND r.id != :id")
    Optional<RankSetting> findByMinPointAndNotId(Integer minPoint, Integer id);
}
