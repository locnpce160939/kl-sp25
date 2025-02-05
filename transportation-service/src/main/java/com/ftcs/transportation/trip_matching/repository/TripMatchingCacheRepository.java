package com.ftcs.transportation.trip_matching.repository;

import com.ftcs.transportation.trip_matching.model.TripMatchingCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface TripMatchingCacheRepository extends JpaRepository<TripMatchingCache, Long> {
    @Transactional
    void deleteByScheduleIdAndBookingId(Long scheduleId, Long bookingId);

    List<TripMatchingCache> findByScheduleIdInOrderBySameDirectionDescCommonPointsDesc(Collection<Long> scheduleId);

}
