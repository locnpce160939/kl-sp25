package com.ftcs.transportation.trip_matching.service;

import com.ftcs.common.exception.BadRequestException;
import com.ftcs.transportation.schelude.repository.ScheduleRepository;
import com.ftcs.transportation.trip_booking.repository.TripBookingsRepository;
import com.ftcs.transportation.trip_matching.model.TripMatchingCache;
import com.ftcs.transportation.trip_matching.model.TripMatchingFinal;
import com.ftcs.transportation.trip_matching.repository.TripMatchingCacheRepository;
import com.ftcs.transportation.trip_matching.repository.TripMatchingFinalRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TripAcceptanceService  {
    private final TripMatchingCacheRepository tripMatchingCacheRepository;
    private final TripMatchingFinalRepository tripMatchingFinalRepository;

    @Transactional
    public void acceptTripBooking(Integer cacheId, Integer accountId){
        copyTripMatchingCacheToFinal(cacheId);
    }

    public void copyTripMatchingCacheToFinal(Integer cacheId) {
        Optional<TripMatchingCache> cacheRecord = tripMatchingCacheRepository.findById(cacheId);
        if (cacheRecord.isEmpty()) {
            throw new BadRequestException("TripMatchingCache record not found for id: " + cacheId);
        }

        TripMatchingCache cache = cacheRecord.get();
        TripMatchingFinal finalRecord = TripMatchingFinal.builder()
                .scheduleId(cache.getScheduleId())
                .bookingId(cache.getBookingId())
                .driverStartLocation(cache.getDriverStartLocation())
                .driverEndLocation(cache.getDriverEndLocation())
                .customerStartLocation(cache.getCustomerStartLocation())
                .customerEndLocation(cache.getCustomerEndLocation())
                .driverStartLocationAddress(cache.getDriverStartLocationAddress())
                .driverEndLocationAddress(cache.getDriverEndLocationAddress())
                .customerStartLocationAddress(cache.getCustomerStartLocationAddress())
                .customerEndLocationAddress(cache.getCustomerEndLocationAddress())
                .startTime(cache.getStartTime())
                .endTime(cache.getEndTime())
                .commonPoints(cache.getCommonPoints())
                .totalCustomerPoints(cache.getTotalCustomerPoints())
                .sameDirection(cache.getSameDirection())
                .status(cache.getStatus())
                .build();

        tripMatchingFinalRepository.save(finalRecord);

        tripMatchingCacheRepository.deleteById(cacheId);
    }
}
