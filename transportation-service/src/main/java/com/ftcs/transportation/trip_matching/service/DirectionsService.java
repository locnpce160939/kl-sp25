package com.ftcs.transportation.trip_matching.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftcs.common.component.GoongApiClient;
import com.ftcs.transportation.trip_matching.dto.DirectionsResponseDTO;
import com.ftcs.transportation.trip_matching.model.DirectionsData;
import com.ftcs.transportation.trip_matching.repository.DirectionsDataRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@AllArgsConstructor
public class DirectionsService {
    private final DirectionsDataRepository directionsDataRepository;
    private final GoongApiClient goongApiClient;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(DirectionsService.class);

    public String fetchDirectionsFromApi(String origin, String destination) {
        Map<String, String> params = new HashMap<>();
        params.put("origin", origin);
        params.put("destination", destination);
        params.put("vehicle", "car");

        return goongApiClient.get("/Direction", params);
    }

    public DirectionsResponseDTO getDirections(String origin, String destination) {
        String[] originCoords = origin.split(",");
        String[] destinationCoords = destination.split(",");
        String startLocationLat = originCoords[0];
        String startLocationLng = originCoords[1];
        String endLocationLat = destinationCoords[0];
        String endLocationLng = destinationCoords[1];

        DirectionsResponseDTO directionsResponse = getDirectionsFromDatabase(startLocationLat, startLocationLng, endLocationLat, endLocationLng);

        if (directionsResponse != null) {
            return directionsResponse;
        }
        String response = fetchDirectionsFromApi(origin, destination);
        if (response != null) {
            return processApiResponse(response, origin, destination);
        }
        return null;
    }

    private DirectionsResponseDTO getDirectionsFromDatabase(String startLocationLat, String startLocationLng, String endLocationLat, String endLocationLng) {
        DirectionsData directionsData = directionsDataRepository.findByStartLocationLatAndStartLocationLngAndEndLocationLatAndEndLocationLng(
                startLocationLat, startLocationLng, endLocationLat, endLocationLng
        );

        if (directionsData != null) {
            return parseDirectionsData(directionsData);
        }
        return null;
    }

    private DirectionsResponseDTO parseDirectionsData(DirectionsData directionsData) {
        try {
            return objectMapper.readValue(directionsData.getFullData(), DirectionsResponseDTO.class);
        } catch (JsonProcessingException e) {
            logger.error("Error parsing directions data from database: ", e);
            return null;
        }
    }

    private DirectionsResponseDTO processApiResponse(String response, String origin, String destination) {
        try {
            DirectionsResponseDTO directionsResponse = objectMapper.readValue(response, DirectionsResponseDTO.class);
            if (directionsResponse.getRoutes() != null && !directionsResponse.getRoutes().isEmpty()) {
                saveDirectionsToDatabase(directionsResponse, origin, destination);
            }
            return directionsResponse;
        } catch (JsonProcessingException e) {
            logger.error("Error processing API response: ", e);
            return null;
        }
    }

    private void saveDirectionsToDatabase(DirectionsResponseDTO directionsResponse, String origin, String destination) {
        try {
            DirectionsResponseDTO.RouteDto route = directionsResponse.getRoutes().get(0);
            DirectionsResponseDTO.LegDto leg = route.getLegs().get(0);

            String fullDataJson = objectMapper.writeValueAsString(directionsResponse);
            DirectionsData directionsData = DirectionsData.builder()
                    .distance(leg.getDistance() != null ? leg.getDistance().getValue() : null)
                    .duration(leg.getDuration() != null ? leg.getDuration().getValue() : null)
                    .startAddress(leg.getStartAddress())
                    .endAddress(leg.getEndAddress())
                    .endLocationLat(destination.split(",")[0])
                    .endLocationLng(destination.split(",")[1])
                    .startLocationLat(origin.split(",")[0])
                    .startLocationLng(origin.split(",")[1])
                    .fullData(fullDataJson)
                    .build();

            directionsDataRepository.save(directionsData);
        } catch (JsonProcessingException e) {
            logger.error("Error saving directions data to database: ", e);
        }
    }
}
