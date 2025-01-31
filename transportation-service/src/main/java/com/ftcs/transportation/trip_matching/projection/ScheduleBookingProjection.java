package com.ftcs.transportation.trip_matching.projection;

import java.time.LocalDateTime;

public interface ScheduleBookingProjection {
    Integer getScheduleId();
    Integer getAccountId();
    String getStartLocation();
    String getEndLocation();
    String getStartLocationAddress();
    String getEndLocationAddress();
    LocalDateTime getStartDate();
    LocalDateTime getEndDate();
    Integer getAvailableCapacity();
    String getScheduleStatus();
    String getScheduleNotes();
    LocalDateTime getScheduleCreateAt();
    LocalDateTime getScheduleUpdateAt();

    Integer getBookingId();
    Integer getBookingAccountId();
    String getBookingType();
    LocalDateTime getBookingDate();
    String getPickupLocation();
    String getDropoffLocation();
    String getBookingStartLocationAddress();
    String getBookingEndLocationAddress();
    Integer getBookingCapacity();
    String getBookingStatus();
    LocalDateTime getExpirationDate();
    Double getTotalPrice();
    String getBookingNotes();
    LocalDateTime getBookingCreateAt();
    LocalDateTime getBookingUpdateAt();
}
