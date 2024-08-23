package ru.practicum;


import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    private static <T> ResponseEntity<T> prepareGatewayResponse(ResponseEntity<T> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }

    protected <T> ResponseEntity<T[]> get(String path, @Nullable Map<String, Object> parameters, Class<T[]> responseType) {
        return sendRequest((HttpMethod.GET), path, parameters, null, responseType);
    }

    protected <T> ResponseEntity<T> post(String path, T body, Class<T> responseType) {
        return sendRequest(HttpMethod.POST, path, null, body, responseType);
    }

    private <T, U> ResponseEntity<T> sendRequest(HttpMethod method, String path,
                                                 @Nullable Map<String, Object> parameters,
                                                 @Nullable U body,
                                                 Class<T> responseType) {
        HttpEntity<U> requestEntity = new HttpEntity<>(body, createDefaultHeaders());

        ResponseEntity<T> response;
        try {
            if (parameters != null) {
                response = rest.exchange(path, method, requestEntity, responseType, parameters);
            } else {
                response = rest.exchange(path, method, requestEntity, responseType);
            }
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException();
        }
        return prepareGatewayResponse(response);
    }

    private HttpHeaders createDefaultHeaders() {
        HttpHeaders defaultHeaders = new HttpHeaders();
        defaultHeaders.setContentType(MediaType.APPLICATION_JSON);
        defaultHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return defaultHeaders;
    }
}
