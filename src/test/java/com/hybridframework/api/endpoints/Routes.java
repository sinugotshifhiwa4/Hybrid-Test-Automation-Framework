package com.hybridframework.api.endpoints;

import com.hybridframework.config.properties.PropertiesConfigManager;
import com.hybridframework.config.properties.PropertiesFileAlias;
import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;

public class Routes {

    private static final Logger logger = LoggerUtils.getLogger(Routes.class);
    private static final String BASE_URL = "API_BASE_URL";

    // Endpoint paths
    private static final String ENDPOINT = "/";

    private Routes() {
        throw new UnsupportedOperationException("Routes is a utility class and cannot be instantiated.");
    }

    private static String getBaseUrl(String environmentAlias) {
        try {
            return PropertiesConfigManager.getPropertyKeyFromCache(environmentAlias, BASE_URL);
        } catch (Exception error) {
            ErrorHandler.logError(error, "getBaseUrl", "Failed to retrieve API Base URL for " + environmentAlias);
            throw error;
        }
    }

    public static String getBaseUrl(PropertiesFileAlias environment) {
        return getBaseUrl(environment.getConfigurationAlias());
    }

    public static String getMethodOneUrl(PropertiesFileAlias environment) {
        return buildUri(ENDPOINT, environment).toString();
    }

    public static String getMethodTwoUrl(int id, PropertiesFileAlias environment) {
        return buildUri(String.format("%s/%s", ENDPOINT, id), environment).toString();
    }

    /**
     * Constructs a URI by appending the given path to the base URL.
     *
     * @param path       The endpoint path
     * @param environment The target environment (e.g., UAT, DEV)
     * @return URI representing the full URL
     * @throws IllegalStateException If URI construction fails
     */
    private static URI buildUri(String path, PropertiesFileAlias environment) {
        try {
            String baseUrl = getBaseUrl(environment);
            String finalUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
            finalUrl += path.startsWith("/") ? path : "/" + path;
            return new URI(finalUrl);
        } catch (URISyntaxException error) {
            ErrorHandler.logError(error, "buildUri", "Failed to build url");
            throw new IllegalStateException("Failed to construct valid URL", error);
        }
    }

    /**
     * Checks if a given string is a valid booking ID.
     *
     * @param id Booking identifier to check
     * @return true if id is valid, false otherwise
     */
    public static boolean isValidId(String id) {
        return id != null && id.matches("^\\d+$");
    }
}
