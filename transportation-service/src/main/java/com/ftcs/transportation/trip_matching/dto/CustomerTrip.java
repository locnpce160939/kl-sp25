package com.ftcs.transportation.trip_matching.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerTrip {
    private double[] startLocation;
    private double[] endLocation;
    private long startTime;
    private long endTime;
}
