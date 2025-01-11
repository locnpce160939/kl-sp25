package com.ftcs.common.component;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class GoongApiClient {

    private static final String BASE_URL = "https://rsapi.goong.io";
    private static final String API_KEY = "t0vRyftUba3uIEnx5JlMJta2ff3B03BEUVg0xHWw";

    private final RestTemplate restTemplate;

    public GoongApiClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Sends an HTTP GET request to the Goong API.
     *
     * @param endpoint The API endpoint (e.g., "/geocode").
     * @param params   Query parameters for the request (if any).
     * @return The JSON response from the API as a String.
     */
    public String get(String endpoint, Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder(BASE_URL).append(endpoint);
        urlBuilder.append("?api_key=").append(API_KEY);

        // Append query parameters to the URL
        if (params != null && !params.isEmpty()) {
            params.forEach((key, value) -> urlBuilder.append("&").append(key).append("=").append(value));
        }

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.getForEntity(urlBuilder.toString(), String.class);
        System.out.println(response.getBody());
        return response.getBody();
    }
}
