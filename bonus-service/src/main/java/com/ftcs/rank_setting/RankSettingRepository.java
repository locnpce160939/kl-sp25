package com.ftcs.rank_setting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RankSettingRepository extends JpaRepository<RankSetting, Integer> {
    @Query("SELECT r FROM RankSetting r WHERE r.minPoint <= :points ORDER BY r.minPoint DESC LIMIT 1")
    RankSetting findRankByPoints(@Param("points") Integer points);

    @Query("SELECT r FROM RankSetting r WHERE r.minPoint = :minPoint AND r.id != :id")
    Optional<RankSetting> findByMinPointAndNotId(@Param("minPoint") Integer minPoint, @Param("id") Integer id);
}
