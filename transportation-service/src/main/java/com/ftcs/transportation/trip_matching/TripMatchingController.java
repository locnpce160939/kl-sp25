package com.ftcs.transportation.trip_matching;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ftcs.common.dto.ApiResponse;
import com.ftcs.transportation.TransportationURL;
import com.ftcs.transportation.trip_booking.dto.TripBookingsRequestDTO;
import com.ftcs.transportation.trip_matching.model.TripMatchingCache;
import com.ftcs.transportation.trip_matching.service.TripAcceptanceService;
import com.ftcs.transportation.trip_matching.service.TripMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(TransportationURL.TRIP_MATCHING)
@RequiredArgsConstructor
public class TripMatchingController {

    private final TripMatchingService tripMatchingService;
    private final TripAcceptanceService tripAcceptanceService;

    @GetMapping("/wsTest")
    public void sendTripBookingUpdates(@RequestBody TripMatchingCache requestDTO) {
        tripMatchingService.sendTripBookingUpdates(requestDTO);
    }

    @GetMapping()
    public ApiResponse<?> getMatchedTrips(@RequestAttribute("accountId") Integer accountId) {
        return ApiResponse.success(tripMatchingService.getMatchedTrips(accountId));
    }

    @GetMapping("/find-matches")
    public ApiResponse<?> findBestMatches() {
        return ApiResponse.success(tripMatchingService.matchTripsForAll());
    }

    @GetMapping("/accept/{cacheId}")
    public ApiResponse<?> acceptTrip(@PathVariable("cacheId") Long cacheId,
                                     @RequestAttribute("accountId") Integer accountId) {
        tripAcceptanceService.acceptTripBooking(cacheId, accountId);
        return ApiResponse.success("Successfully accepted trip");
    }

}
