package com.ftcs.transportation.trip_matching.service;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.ftcs.realtimeservice.socket.contants.Message;
import com.ftcs.realtimeservice.socket.contants.MessageType;
import com.ftcs.realtimeservice.socket.service.SocketService;
import com.ftcs.transportation.schelude.model.Schedule;
import com.ftcs.transportation.trip_booking.model.TripBookings;
import com.ftcs.transportation.trip_matching.dto.MatchResult;
import com.ftcs.transportation.trip_matching.projection.ScheduleBookingProjection;
import com.ftcs.transportation.trip_matching.dto.ScheduleBookingDTO;
import com.ftcs.transportation.trip_matching.mapper.ScheduleBookingMapper;
import com.ftcs.transportation.trip_matching.model.TripMatchingCache;
import com.ftcs.transportation.trip_matching.repository.DirectionsDataRepository;
import com.ftcs.transportation.trip_matching.repository.TripMatchingCacheRepository;
import com.ftcs.transportation.trip_matching.service.strategy.MatchingContext;
import com.ftcs.transportation.trip_matching.service.strategy.MatchingStrategy;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.ftcs.common.utils.JsonUtils.toJson;

@Service
@AllArgsConstructor
public class TripMatchingService {
    @Autowired
    private final SocketIOServer server;


    private final TripMatchingCacheRepository tripMatchingRepository;
    private final DirectionsDataRepository tripDirectionsDataRepository;

    private final MatchingStrategy<MatchResult> matchPointsStrategy;
    private final MatchingStrategy<Boolean> directionStrategy;

    private final SocketService socketService;

    public void sendTripBookingUpdates(TripMatchingCache matchedTrips) {
        Message message = Message.builder()
                .messageType(MessageType.NOTIFICATION)
                .room("7")
                .content(toJson(matchedTrips))
                .username("admin")
                .build();

        socketService.sendSocketMessage(message);
    }

    public List<TripMatchingCache> getMatchedTrips(Long scheduleId) {
        return tripMatchingRepository.findByScheduleIdOrderBySameDirectionDescCommonPointsDesc(scheduleId);
    }

    @Async
    public CompletableFuture<List<TripMatchingCache>> matchTripsForAll() {
        List<ScheduleBookingProjection> projections = tripDirectionsDataRepository.findUnmatchedSchedulesAndBookings();

        List<ScheduleBookingDTO> scheduleBookingDTOs = projections.stream()
                .map(ScheduleBookingMapper::mapToDTO)
                .toList();

        return CompletableFuture.completedFuture(matchTrips(scheduleBookingDTOs));
    }

    public List<TripMatchingCache> matchTrips(List<ScheduleBookingDTO> scheduleBookingDTOs) {
        List<TripMatchingCache> matchedTrips = new ArrayList<>();

        for (ScheduleBookingDTO scheduleBookingDTO : scheduleBookingDTOs) {
                MatchingContext context = new MatchingContext(scheduleBookingDTO.getSchedule(), scheduleBookingDTO.getTripBooking());

                MatchResult matchPointsResult = matchPointsStrategy.evaluateMatch(context);
                Boolean sameDirection = directionStrategy.evaluateMatch(context);

                TripMatchingCache tripMatching = createTripMatching(scheduleBookingDTO.getSchedule(), scheduleBookingDTO.getTripBooking(), matchPointsResult, sameDirection);
                matchedTrips.add(tripMatching);

                sendTripBookingUpdates(tripMatching);
        }

        return matchedTrips;
    }

    private TripMatchingCache createTripMatching(Schedule schedule, TripBookings booking,
                                                 MatchResult matchPointsResult, boolean sameDirection) {
        TripMatchingCache tripMatching = new TripMatchingCache();
        tripMatching.setScheduleId(schedule.getScheduleId());
        tripMatching.setBookingId(booking.getBookingId());
        tripMatching.setDriverStartLocation(schedule.getStartLocation());
        tripMatching.setDriverEndLocation(schedule.getEndLocation());
        tripMatching.setCustomerStartLocation(booking.getPickupLocation());
        tripMatching.setCustomerEndLocation(booking.getDropoffLocation());
        tripMatching.setDriverStartLocationAddress(schedule.getStartLocationAddress());
        tripMatching.setDriverEndLocationAddress(schedule.getEndLocationAddress());
        tripMatching.setCustomerStartLocationAddress(booking.getStartLocationAddress());
        tripMatching.setCustomerEndLocationAddress(booking.getEndLocationAddress());
        tripMatching.setCommonPoints(matchPointsResult.getCommonPoints());
        tripMatching.setTotalCustomerPoints(matchPointsResult.getTotalCustomerPoints());
        tripMatching.setSameDirection(sameDirection);
        tripMatching.setStatus(booking.getStatus());
        tripMatchingRepository.save(tripMatching);
        return tripMatching;
    }
}
