package com.ftcs.transportation.trip_matching.service.strategy;

import lombok.AllArgsConstructor;

import static com.ftcs.common.utils.LocationUtils.parseLocation;

@AllArgsConstructor
public class DirectionStrategy implements MatchingStrategy<Boolean> {

    @Override
    public Boolean evaluateMatch(MatchingContext context) {
        double[] driverStart = parseLocation(context.getSchedule().getStartLocation());
        double[] driverEnd = parseLocation(context.getSchedule().getEndLocation());
        double[] customerStart = parseLocation(context.getBooking().getPickupLocation());
        double[] customerEnd = parseLocation(context.getBooking().getDropoffLocation());

        double[] vector1 = {driverEnd[0] - driverStart[0], driverEnd[1] - driverStart[1]};
        double[] vector2 = {customerEnd[0] - customerStart[0], customerEnd[1] - customerStart[1]};

        double magnitude1 = Math.sqrt(vector1[0] * vector1[0] + vector1[1] * vector1[1]);
        double magnitude2 = Math.sqrt(vector2[0] * vector2[0] + vector2[1] * vector2[1]);
        vector1[0] /= magnitude1;
        vector1[1] /= magnitude1;
        vector2[0] /= magnitude2;
        vector2[1] /= magnitude2;

        double dotProduct = vector1[0] * vector2[0] + vector1[1] * vector2[1];

        return dotProduct > 0.7;
    }
}
