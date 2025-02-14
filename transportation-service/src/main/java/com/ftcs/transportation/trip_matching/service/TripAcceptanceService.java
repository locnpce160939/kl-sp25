package com.ftcs.transportation.trip_matching.service;

import com.ftcs.common.exception.BadRequestException;
import com.ftcs.transportation.schedule.model.Schedule;
import com.ftcs.transportation.schedule.repository.ScheduleRepository;
import com.ftcs.transportation.trip_booking.constant.TripBookingStatus;
import com.ftcs.transportation.trip_booking.model.TripBookings;
import com.ftcs.transportation.trip_booking.repository.TripBookingsRepository;
import com.ftcs.transportation.trip_agreement.constant.AgreementStatusType;
import com.ftcs.transportation.trip_matching.constant.PaymentStatusType;
import com.ftcs.transportation.trip_agreement.model.TripAgreement;
import com.ftcs.transportation.trip_matching.model.TripMatchingCache;
import com.ftcs.transportation.trip_matching.model.TripMatchingFinal;
import com.ftcs.transportation.trip_agreement.repository.TripAgreementRepository;
import com.ftcs.transportation.trip_matching.repository.TripMatchingCacheRepository;
import com.ftcs.transportation.trip_matching.repository.TripMatchingFinalRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TripAcceptanceService  {
    private final TripMatchingCacheRepository tripMatchingCacheRepository;
    private final TripMatchingFinalRepository tripMatchingFinalRepository;
    private final ScheduleRepository scheduleRepository;
    private final TripBookingsRepository tripBookingsRepository;
    private final TripAgreementRepository tripAgreementRepository;

    @Transactional
    public void acceptTripBooking(Long cacheId, Integer accountId){
        copyTripMatchingCacheToFinal(cacheId);
    }

    public void copyTripMatchingCacheToFinal(Long cacheId) {
        TripMatchingCache cache = getTripMatchingCacheById(cacheId);

        TripMatchingFinal finalRecord = saveFinalTripMatching(cache);
        tripMatchingCacheRepository.deleteById(cacheId);

        TripAgreement tripAgreement = createAndSaveTripAgreement(cache, finalRecord);

        updateTripBookings(tripAgreement);

    }

    private TripMatchingFinal saveFinalTripMatching(TripMatchingCache cache) {
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

        return tripMatchingFinalRepository.save(finalRecord);
    }

    private TripAgreement createAndSaveTripAgreement(TripMatchingCache cache, TripMatchingFinal finalRecord) {
        Schedule schedule = getSchedule(cache.getScheduleId());
        TripBookings tripBooking = getTripBookings(cache.getBookingId());
        Integer distance = Optional.ofNullable(tripBooking.getTotalDistance())
                .map(d -> d.intValue())
                .orElse(0);
        TripAgreement tripAgreement = TripAgreement.builder()
                .tripMatchingId(finalRecord.getId())
                .scheduleId(finalRecord.getScheduleId())
                .bookingId(finalRecord.getBookingId())
                .tripMatchingId(finalRecord.getId())
                .driverId(schedule.getAccountId())
                .customerId(tripBooking.getAccountId())
                .totalPrice(0.0)
                .paymentStatus(PaymentStatusType.PAIR)
                .distance(distance)
                .estimatedDuration(100)
                .agreementStatus(AgreementStatusType.IN_TRANSIT)
                .build();

        return tripAgreementRepository.save(tripAgreement);
    }

    private void updateTripBookings(TripAgreement tripAgreement) {
        TripBookings tripBooking = getTripBookings(tripAgreement.getBookingId());
        tripBooking.setTripAgreementId(tripAgreement.getId());
        tripBooking.setStatus(TripBookingStatus.WAITING_FOR_DELIVERY);
        tripAgreementRepository.save(tripAgreement);
    }

    private TripMatchingCache getTripMatchingCacheById(Long cacheId) {
        return tripMatchingCacheRepository.findById(cacheId)
                .orElseThrow(() -> new BadRequestException("TripMatchingCache record not found for id: " + cacheId));
    }

    public Schedule getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BadRequestException("Schedule not found!"));
    }

    public TripBookings getTripBookings(Long tripBookingId) {
        return tripBookingsRepository.findById(tripBookingId)
                .orElseThrow(() -> new BadRequestException("TripBookings not found!"));
    }
}
