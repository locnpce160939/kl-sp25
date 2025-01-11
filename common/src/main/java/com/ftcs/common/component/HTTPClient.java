package com.ftcs.common.component;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class HTTPClient {

    private final RestTemplate restTemplate;

    public HTTPClient() {
        this.restTemplate = new RestTemplate();
    }

    public <T> T get(String url, Map<String, String> headers, Class<T> responseType) {
        HttpEntity<String> entity = new HttpEntity<>(null, createHeaders(headers));
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
        return response.getBody();
    }

    public <T> T post(String url, Map<String, String> headers, Object body, Class<T> responseType) {
        HttpEntity<Object> entity = new HttpEntity<>(body, createHeaders(headers));
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
        return response.getBody();
    }

    public <T> T put(String url, Map<String, String> headers, Object body, Class<T> responseType) {
        HttpEntity<Object> entity = new HttpEntity<>(body, createHeaders(headers));
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.PUT, entity, responseType);
        return response.getBody();
    }

    public <T> T delete(String url, Map<String, String> headers, Class<T> responseType) {
        HttpEntity<String> entity = new HttpEntity<>(null, createHeaders(headers));
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, responseType);
        return response.getBody();
    }

    // Utility method to create HTTP headers
    private HttpHeaders createHeaders(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach(httpHeaders::set);
        }
        return httpHeaders;
    }
}
