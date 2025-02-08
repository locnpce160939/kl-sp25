package com.ftcs.transportation.trip_matching.service.strategy;

import com.ftcs.transportation.schedule.model.Schedule;
import com.ftcs.transportation.trip_booking.model.TripBookings;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MatchingContext {
    private Schedule schedule;
    private TripBookings booking;
}
