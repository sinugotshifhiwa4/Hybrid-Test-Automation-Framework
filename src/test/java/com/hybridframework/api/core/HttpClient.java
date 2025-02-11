package com.hybridframework.api.core;

import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import io.restassured.response.Response;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.restassured.RestAssured.given;

public class HttpClient {

    private static final Logger logger = LoggerUtils.getLogger(HttpClient.class);
    private final Map<String, String> headers;

    public HttpClient() {
        this.headers = new HashMap<>();
        this.headers.put("Content-Type", "application/json");
        this.headers.put("Accept", "application/json");
    }

    private Map<String, String> createHeaders(String authorizationHeader, Map<String, String> additionalHeaders) {
        Map<String, String> headers = new HashMap<>(this.headers);

        Optional.ofNullable(authorizationHeader)
                .filter(auth -> !auth.isEmpty())
                .ifPresent(auth -> headers.put("Authorization", "Bearer " + auth));

        Optional.ofNullable(additionalHeaders).ifPresent(headers::putAll);

        return headers;
    }

    private Response sendRequest(HttpMethod method, String endpoint, Object payload,
                                 String authorizationHeader, Map<String, String> additionalHeaders) {
        try {
            Map<String, String> headers = createHeaders(authorizationHeader, additionalHeaders);

            return switch (method) {
                case POST -> given().headers(headers).body(payload).when().post(endpoint);
                case PATCH -> given().headers(headers).body(payload).when().patch(endpoint);
                case GET -> given().headers(headers).when().get(endpoint);
                case DELETE -> given().headers(headers).when().delete(endpoint);
                case PUT -> given().headers(headers).body(payload).when().put(endpoint);
            };
        } catch (Exception error) {
            ErrorHandler.logError(error, "sendRequest", "Failed to send http request");
            throw error;
        }
    }

    public Response sendPostRequest(
            String endpoint,
            Object payload,
            String authorizationHeader,
            Map<String, String> additionalHeaders
    ) {
        return sendRequest(
                HttpMethod.POST,
                endpoint,
                payload,
                authorizationHeader,
                Optional.ofNullable(additionalHeaders).orElse(new HashMap<>()
                ));
    }

    public Response sendGetRequest(
            String endpoint,
            String authorizationHeader,
            Map<String, String> additionalHeaders) {
        return sendRequest(
                HttpMethod.GET,
                endpoint,
                null,
                authorizationHeader,
                Optional.ofNullable(additionalHeaders).orElse(new HashMap<>()
                ));
    }

    public Response sendUpdateRequest(
            String endpoint,
            Object payload,
            String authorizationHeader,
            Map<String, String> additionalHeaders
    ) {
        return sendRequest(
                HttpMethod.PATCH,
                endpoint,
                payload,
                authorizationHeader,
                Optional.ofNullable(additionalHeaders).orElse(new HashMap<>()
                ));
    }

    public Response sendPartialUpdateRequest(
            String endpoint,
            Object payload,
            String authorizationHeader,
            Map<String, String> additionalHeaders
    ) {
        return sendRequest(
                HttpMethod.PATCH,
                endpoint,
                payload,
                authorizationHeader,
                Optional.ofNullable(additionalHeaders).orElse(new HashMap<>()
                ));
    }

    public Response sendDeleteRequest(
            String endpoint,
            String authorizationHeader,
            Map<String, String> additionalHeaders
    ) {
        return sendRequest(
                HttpMethod.DELETE,
                endpoint,
                null,
                authorizationHeader,
                Optional.ofNullable(additionalHeaders).orElse(new HashMap<>()
                ));
    }

    public enum HttpMethod {
        POST, GET, PUT, PATCH, DELETE
    }
}
