package com.ftcs.common.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftcs.common.component.GoongApiClient;
import com.ftcs.common.feature.location.dto.ReverseGeocodeResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ReverseGeocodeService {

    private static final String ENDPOINT = "/geocode";

    private GoongApiClient goongApiClient;

    public List<ReverseGeocodeResponseDto.ResultDto> getAddressFromCoordinates(Double latitude, Double longitude) {
        Map<String, String> params = new HashMap<>();
        params.put("latlng", latitude + "," + longitude);

        String jsonResponse = goongApiClient.get(ENDPOINT, params);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return objectMapper.readValue(jsonResponse, ReverseGeocodeResponseDto.class).getResults();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
