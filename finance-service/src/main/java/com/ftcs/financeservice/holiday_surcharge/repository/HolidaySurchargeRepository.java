package com.ftcs.financeservice.holiday_surcharge.repository;

import com.ftcs.financeservice.holiday_surcharge.model.HolidaySurcharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface HolidaySurchargeRepository extends JpaRepository<HolidaySurcharge, Integer> {

    Optional<HolidaySurcharge> findByHolidaySurchargeId(Integer holidaySurchargeId);

    @Query("SELECT COUNT(h) > 0 FROM HolidaySurcharge h WHERE (h.startDate <= :endDate AND h.endDate >= :startDate)")
    boolean existsByStartDateAndEndDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
