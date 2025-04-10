package com.ftcs.transportation.trip_matching.projection;

import com.ftcs.transportation.schedule.constant.ScheduleStatus;
import com.ftcs.transportation.trip_booking.constant.TripBookingStatus;

import java.time.LocalDateTime;

public interface ScheduleBookingProjection {
    Long getScheduleId();
    Integer getAccountId();
    String getStartLocation();
    String getEndLocation();
    String getStartLocationAddress();
    String getEndLocationAddress();
    LocalDateTime getStartDate();
    LocalDateTime getEndDate();
    Integer getAvailableCapacity();
    ScheduleStatus getScheduleStatus();
    String getScheduleNotes();
    LocalDateTime getScheduleCreateAt();
    LocalDateTime getScheduleUpdateAt();

    Long getBookingId();
    Integer getBookingAccountId();
    Long getBookingType();
    LocalDateTime getBookingDate();
    String getPickupLocation();
    String getDropoffLocation();
    String getBookingStartLocationAddress();
    String getBookingEndLocationAddress();
    Integer getBookingCapacity();
    TripBookingStatus getBookingStatus();
    LocalDateTime getExpirationDate();
    Double getTotalPrice();
    String getBookingNotes();
    LocalDateTime getBookingCreateAt();
    LocalDateTime getBookingUpdateAt();
}
