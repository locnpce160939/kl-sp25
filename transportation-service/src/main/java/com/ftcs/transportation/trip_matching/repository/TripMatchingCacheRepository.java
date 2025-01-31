package com.ftcs.transportation.trip_matching.repository;

import com.ftcs.transportation.trip_matching.model.TripMatchingCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TripMatchingCacheRepository extends JpaRepository<TripMatchingCache, Integer> {
    @Transactional
    void deleteByScheduleIdAndBookingId(Integer scheduleId, Integer bookingId);

    List<TripMatchingCache> findByScheduleIdOrderBySameDirectionDescCommonPointsDesc(Integer scheduleId);

}
