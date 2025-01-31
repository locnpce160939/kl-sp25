package com.ftcs.transportation.trip_matching;

import com.ftcs.common.dto.ApiResponse;
import com.ftcs.transportation.TransportationURL;
import com.ftcs.transportation.trip_matching.service.TripAcceptanceService;
import com.ftcs.transportation.trip_matching.service.TripMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(TransportationURL.TRIP_MATCHING)
@RequiredArgsConstructor
public class TripMatchingController {

    private final TripMatchingService tripMatchingService;
    private final TripAcceptanceService tripAcceptanceService;

    @GetMapping("/{scheduleId}")
    public ApiResponse<?> getMatchedTrips(@PathVariable("scheduleId") Integer scheduleId) {
        return ApiResponse.success(tripMatchingService.getMatchedTrips(scheduleId));
    }

    @GetMapping("/find-matches")
    public ApiResponse<?> findBestMatches() {
        return ApiResponse.success(tripMatchingService.matchTripsForAll());
    }

    @GetMapping("/accept/{cacheId}")
    public ApiResponse<?> acceptTrip(@PathVariable("cacheId") Integer cacheId,
                                     @RequestAttribute("accountId") Integer accountId) {
        tripAcceptanceService.acceptTripBooking(cacheId, accountId);
        return ApiResponse.success("oke");
    }

}
