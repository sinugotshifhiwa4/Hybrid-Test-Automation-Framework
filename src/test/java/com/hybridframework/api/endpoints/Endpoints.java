package com.hybridframework.api.endpoints;

import com.hybridframework.api.core.HttpClient;
import com.hybridframework.config.properties.PropertiesFileAlias;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class Endpoints {

    private final HttpClient httpClient = new HttpClient();

    public Response generateAuthToken(Object payload, PropertiesFileAlias environment){ // replace obj with class
        return httpClient.sendPostRequest(
                Routes.getMethodOneUrl(environment),
                payload,
                null,
                null
        );
    }

    public Response create(Object payload, PropertiesFileAlias environment){
        return httpClient.sendPostRequest(
                Routes.getMethodOneUrl(environment),
                payload,
                null,
                null
        );
    }

    public Response getById(int id, PropertiesFileAlias environment){
        return httpClient.sendGetRequest(
                Routes.getMethodTwoUrl(id, environment),
                null,
                null
        );
    }

    public Response updateById(Object payload, int id, String token, PropertiesFileAlias environment){
        return httpClient.sendUpdateRequest(
                Routes.getMethodTwoUrl(id, environment),
                payload,
                null,
                createAuthHeader(token)
        );
    }

    private Map<String, String> createAuthHeader(String token) {
        if (token == null) return null;
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", "token=" + token);
        return headers;
    }

}
