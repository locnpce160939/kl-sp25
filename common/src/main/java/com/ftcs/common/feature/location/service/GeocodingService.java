package com.ftcs.common.feature.location.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftcs.common.component.GoongApiClient;
import com.ftcs.common.feature.location.dto.GeocodingResponseDTO;
import com.ftcs.common.feature.location.model.GeocodingData;
import com.ftcs.common.feature.location.repository.GeocodingDataRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GeocodingService {
    private final GoongApiClient goongApiClient;
    private final ObjectMapper objectMapper;
    private final GeocodingDataRepository geocodingDataRepository;
    private static final Logger logger = LoggerFactory.getLogger(GeocodingService.class);

    private String callGeocodingApi(String address) {
        Map<String, String> params = new HashMap<>();
        params.put("address", address);
        return goongApiClient.get("/geocode", params);
    }

    @Transactional
    public GeocodingResponseDTO getGeocode(String address) {
        Optional<GeocodingData> existingData = geocodingDataRepository.findByAddress(address);
        if (existingData.isPresent()) {
            logger.info("Address found in DB: {}", address);
            return parseGeocodeResponse(existingData.get().getFullData());
        }

        String response = callGeocodingApi(address);
        if (response != null) {
            GeocodingResponseDTO geocodeResponse = parseGeocodeResponse(response);
            if (geocodeResponse != null) {
                saveGeocodingData(address, response);
            }
            return geocodeResponse;
        }
        return null;
    }

    private void saveGeocodingData(String address, String response) {
        GeocodingData newData = GeocodingData.builder()
                .address(address)
                .fullData(response)
                .build();
        geocodingDataRepository.save(newData);
        logger.info("Saved new geocoding data for address: {}", address);
    }

    private GeocodingResponseDTO parseGeocodeResponse(String response) {
        try {
            return objectMapper.readValue(response, GeocodingResponseDTO.class);
        } catch (JsonProcessingException e) {
            logger.error("Error parsing geocode response: ", e);
            return null;
        }
    }
}
