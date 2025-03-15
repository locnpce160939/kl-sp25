package com.ftcs.transportation.trip_booking.repository;

import com.ftcs.transportation.trip_booking.model.TripBookings;
import com.ftcs.transportation.trip_booking.projection.BasePriceProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TripBookingsRepository extends JpaRepository<TripBookings, Long> {
    Optional<TripBookings> findTripBookingsByBookingId(Long bookingId);
    Optional<TripBookings> findTripBookingsByAccountId(Integer accountId);
//    List<TripBookings> findAllByBookingDateBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, String status);
//    List<TripBookings> findAllByStatus(String status);
    boolean existsByAccountId(Integer accountId);
    List<TripBookings> findAllByBookingDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    Page<TripBookings> findAllByAccountId(Integer accountId, Pageable pageable);

    @Query(value = "EXEC dbo.GetBasePrice @Km = :km, @Kg = :kg", nativeQuery = true)
    BasePriceProjection findBasePrice(@Param("km") BigDecimal km, @Param("kg") BigDecimal kg);
}
