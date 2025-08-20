package ru.practicum;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path) {
        return get(path, null);
    }

    protected ResponseEntity<Object> get(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    protected ResponseEntity<Object> post(String path, Object body) {
        return post(path, null, body);
    }

    protected ResponseEntity<Object> post(String path, @Nullable Map<String, Object> parameters, Object body) {
        return makeAndSendRequest(HttpMethod.POST, path, parameters, body);
    }

    protected ResponseEntity<Object> patch(String path, Object body) {
        return patch(path, null, null, body);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, Object body) {
        return patch(path, userId, null, body);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, String str) {
        return patch(path, userId, null, str);
    }

    protected ResponseEntity<Object> patch(String path, @Nullable Map<String, Object> parameters, Object body) {
        return patch(path, null, parameters, body);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, @Nullable Map<String, Object> parameters, Object body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, parameters, body);
    }

    protected ResponseEntity<Object> put(String path, Object body) {
        return put(path, null, body);
    }

    protected ResponseEntity<Object> put(String path, @Nullable Map<String, Object> parameters, Object body) {
        return makeAndSendRequest(HttpMethod.PUT, path, parameters, body);
    }

    protected ResponseEntity<Object> delete(String path) {
        return delete(path, null);
    }

    protected ResponseEntity<Object> delete(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, parameters, null);
    }

    private ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, @Nullable Map<String, Object> parameters, @Nullable Object body) {
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> statsServerResponse;
        try {
            if (parameters != null && !parameters.isEmpty()) {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(statsServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}