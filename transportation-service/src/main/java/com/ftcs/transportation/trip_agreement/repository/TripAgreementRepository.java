package com.ftcs.transportation.trip_agreement.repository;

import com.ftcs.transportation.trip_agreement.model.TripAgreement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripAgreementRepository extends JpaRepository<TripAgreement, Long> {
    List<TripAgreement> findAllByCustomerId(Integer customerId);
    List<TripAgreement> findAllByDriverId(Integer driverId);
    List<TripAgreement> findAllByScheduleId(Long driverId);
    Page<TripAgreement> findTripAgreementByScheduleId(Long scheduleId, Pageable pageable);
    TripAgreement findByBookingId(Long bookingId);
}
