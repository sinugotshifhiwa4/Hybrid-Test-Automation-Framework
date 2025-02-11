package com.hybridframework.utils.jacksonUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class JsonDataReader {

    private static final Logger logger = LoggerUtils.getLogger(JsonDataReader.class);
    private final JsonNode jsonData;

    /**
     * Constructs a JsonDataReader with the specified file path.
     *
     * @param filePath the path to the JSON file
     * @throws JsonDataReaderException if the JSON file cannot be loaded
     */
    public JsonDataReader(String filePath) {
        this.jsonData = loadJson(filePath);
    }

    private static JsonNode loadJson(String filePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(new File(filePath));
        } catch (IOException error) {
            ErrorHandler.logError(error, "loadJson", "Failed to load json file");
            throw new JsonDataReaderException("Failed to load JSON file: " + filePath, error);
        }
    }

    private  <ConversionType> Optional<ConversionType> getData(String category, String key, Class<ConversionType> type) {
        try {
            JsonNode node = jsonData.path(category).path(key);
            if (node.isMissingNode() || node.isNull()) {
                return Optional.empty();
            }

            if (type == Integer.class && node.isInt()) {
                return Optional.of(type.cast(node.asInt()));
            } else if (type == Double.class && node.isDouble()) {
                return Optional.of(type.cast(node.asDouble()));
            } else if (type == Boolean.class && node.isBoolean()) {
                return Optional.of(type.cast(node.asBoolean()));
            } else if (type == String.class) {
                return Optional.of(type.cast(node.asText()));
            } else {
                return Optional.empty();
            }
        } catch (Exception error) {
            ErrorHandler.logError(error, "getData", "Failed to retrieve data for category: " + category + ", key: " + key);
            return Optional.empty();
        }
    }

    public String getString(String category, String key) {
        return getData(category, key, String.class)
                .orElseThrow(() -> new IllegalArgumentException("Missing String value for key: " + key));
    }

    public int getInt(String category, String key) {
        return getData(category, key, Integer.class)
                .orElseThrow(() -> new IllegalArgumentException("Missing int value for key: " + key));
    }

    public boolean getBoolean(String category, String key) {
        return getData(category, key, Boolean.class)
                .orElseThrow(() -> new IllegalArgumentException("Missing boolean value for key: " + key));
    }



    /**
     * Custom exception for JSON data reading errors.
     */
    public static class JsonDataReaderException extends RuntimeException {
        public JsonDataReaderException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}