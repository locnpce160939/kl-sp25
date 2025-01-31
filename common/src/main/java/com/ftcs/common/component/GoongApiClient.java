package com.ftcs.common.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class GoongApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    public GoongApiClient(@Value("${goong.api.base-url}") String baseUrl,
                          @Value("${goong.api.key}") String apiKey) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    /**
     * Sends an HTTP GET request to the Goong API.
     *
     * @param endpoint The API endpoint (e.g., "/geocode").
     * @param params   Query parameters for the request (if any).
     * @return The JSON response from the API as a String.
     */
    public String get(String endpoint, Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder(baseUrl).append(endpoint);
        urlBuilder.append("?api_key=").append(apiKey);

        if (params != null && !params.isEmpty()) {
            params.forEach((key, value) -> urlBuilder.append("&").append(key).append("=").append(value));
        }

        ResponseEntity<String> response = restTemplate.getForEntity(urlBuilder.toString(), String.class);
        return response.getBody();
    }
}
