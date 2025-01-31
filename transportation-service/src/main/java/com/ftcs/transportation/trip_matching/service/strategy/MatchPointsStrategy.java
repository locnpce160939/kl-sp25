package com.ftcs.transportation.trip_matching.service.strategy;

import com.ftcs.transportation.trip_matching.dto.DirectionsResponseDTO;
import com.ftcs.transportation.trip_matching.dto.MatchResult;
import com.ftcs.transportation.trip_matching.service.DirectionsService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;

import static com.ftcs.common.component.PolylineDecoder.decodePolyline;
import static com.ftcs.common.utils.LocationUtils.formatLocation;
import static com.ftcs.common.utils.LocationUtils.parseLocation;

@AllArgsConstructor
public class MatchPointsStrategy implements MatchingStrategy<MatchResult> {

    private final DirectionsService directionsService;

    @Override
    public MatchResult evaluateMatch(MatchingContext context) {
        double[] customerStartLocation = parseLocation(context.getBooking().getPickupLocation());
        double[] customerEndLocation = parseLocation(context.getBooking().getDropoffLocation());

        double[] driverStartLocation = parseLocation(context.getSchedule().getStartLocation());
        double[] driverEndLocation = parseLocation(context.getSchedule().getEndLocation());

        DirectionsResponseDTO customerRoute = directionsService.getDirections(
                formatLocation(customerStartLocation),
                formatLocation(customerEndLocation)
        );
        String customerPolyline = customerRoute.getRoutes().get(0).getOverviewPolyline().getPoints();
        List<double[]> customerRoutePoints = decodePolyline(customerPolyline);

        DirectionsResponseDTO driverRoute = directionsService.getDirections(
                formatLocation(driverStartLocation),
                formatLocation(driverEndLocation)
        );
        String driverPolyline = driverRoute.getRoutes().get(0).getOverviewPolyline().getPoints();
        List<double[]> driverRoutePoints = decodePolyline(driverPolyline);

        int commonPoints = calculateCommonPoints(driverRoutePoints, customerRoutePoints);
        int totalCustomerPoints = customerRoutePoints.size();

        return new MatchResult(commonPoints, totalCustomerPoints);
    }

    private int calculateCommonPoints(List<double[]> route1, List<double[]> route2) {
        Set<String> route1Points = new HashSet<>();
        for (double[] point : route1) {
            route1Points.add(formatPoint(point));
        }

        int commonPoints = 0;
        for (double[] point : route2) {
            if (route1Points.contains(formatPoint(point))) {
                commonPoints++;
            }
        }
        return commonPoints;
    }

    private String formatPoint(double[] point) {
        return String.format("%.5f,%.5f", point[0], point[1]);
    }
}
