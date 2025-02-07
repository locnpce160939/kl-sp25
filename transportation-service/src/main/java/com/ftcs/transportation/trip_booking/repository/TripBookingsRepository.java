package com.ftcs.transportation.trip_booking.repository;

import com.ftcs.transportation.schelude.model.Schedule;
import com.ftcs.transportation.trip_booking.model.TripBookings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TripBookingsRepository extends JpaRepository<TripBookings, Long> {
    Optional<TripBookings> findTripBookingsByBookingId(Long bookingId);
    Optional<TripBookings> findTripBookingsByAccountId(Integer accountId);
//    List<TripBookings> findAllByBookingDateBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, String status);
//    List<TripBookings> findAllByStatus(String status);
    List<TripBookings> findAllByBookingDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<TripBookings> findAllByAccountId(Integer accountId);
}
