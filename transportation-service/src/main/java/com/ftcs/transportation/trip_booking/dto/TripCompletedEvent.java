package com.ftcs.transportation.trip_booking.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TripCompletedEvent {
    private final Long bookingId;
    private final Integer accountId;
    private final Double amount;
}