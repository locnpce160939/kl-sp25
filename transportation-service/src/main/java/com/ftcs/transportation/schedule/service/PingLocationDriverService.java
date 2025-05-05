package com.ftcs.transportation.schedule.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.realtimeservice.socket.contants.Message;
import com.ftcs.realtimeservice.socket.contants.MessageType;
import com.ftcs.realtimeservice.socket.service.SocketService;
import com.ftcs.transportation.schedule.dto.LocationDriverDTO;
import com.ftcs.transportation.schedule.model.Schedule;
import com.ftcs.transportation.schedule.repository.ScheduleRepository;
import com.ftcs.transportation.trip_agreement.model.TripAgreement;
import com.ftcs.transportation.trip_agreement.repository.TripAgreementRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ftcs.common.utils.JsonUtils.toJson;

@Service
@AllArgsConstructor
@Slf4j
public class PingLocationDriverService {
    private final ObjectMapper objectMapper;
    private final SocketIOServer server;
    private final SocketService socketService;

    private final TripAgreementRepository tripAgreementRepository;

    private final Map<String, LocationDriverDTO> driverSessionMap = new ConcurrentHashMap<>();
    private final ScheduleRepository scheduleRepository;

    @PostConstruct
    public void init() {
        this.server.addDisconnectListener(onDisconnected());
        this.server.addEventListener(MessageType.LOCATION_SEND.toString(), Message.class, onPingLocation());
    }

    private DataListener<Message> onPingLocation() {
        return (client, data, ackSender) -> {
            //if (isAuthenticated(client)) {
            LocationDriverDTO location = objectMapper.readValue(data.getContent(), LocationDriverDTO.class);
            driverSessionMap.put(client.getSessionId().toString(), new LocationDriverDTO(location.getId(), location.getLocationDriver()));
            log.info("Event: LOCATION_SEND, Data: Account Driver Id id {} in Location is {}", location.getId(), location.getLocationDriver());
            sendLocationAddressToClient(location);
            //}
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            String sessionId = client.getSessionId().toString();
            if (driverSessionMap.containsKey(sessionId)) {
                LocationDriverDTO lastLocation = driverSessionMap.get(sessionId);
                log.info("Location of schedule Id {} disconnected", lastLocation.getId());
                if (lastLocation.getLocationDriver() != null) {
                    saveLastLocationToDB(lastLocation);
                    log.info("Saved last location for schedule {}: {}", lastLocation.getId(), lastLocation.getLocationDriver());
                }
                driverSessionMap.remove(sessionId);
            } else {
                log.info("Client {} disconnected (Not a driver)", sessionId);
            }
        };
    }

    public void sendLocationAddressToClient(LocationDriverDTO dto) {
        List<TripAgreement> tripAgreements = tripAgreementRepository.findAllByDriverId(dto.getId().intValue());
        for (TripAgreement tripAgreement : tripAgreements) {
            Message message = Message.builder()
                    .messageType(MessageType.LOCATION)
                    .room(tripAgreement.getCustomerId().toString())
                    .content(toJson(dto))
                    .username("system")
                    .build();
            log.info("Event: LOCATION, Data: Customer id {} in Location is {}", tripAgreement.getCustomerId(), dto.getLocationDriver());
            socketService.sendSocketMessage(message);
        }
    }

    private void saveLastLocationToDB(LocationDriverDTO dto) {
        Schedule schedule = scheduleRepository.findScheduleByScheduleId(dto.getId())
                .orElseThrow(() -> new BadRequestException("Schedule does not exist"));
        schedule.setLocationDriver(dto.getLocationDriver());
        scheduleRepository.save(schedule);
    }

}