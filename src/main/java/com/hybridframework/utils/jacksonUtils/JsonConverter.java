package com.hybridframework.utils.jacksonUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Objects;

public class JsonConverter {

    // Initialize ObjectMapper
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonConverter() {}

    /**
     * Initialize the ObjectMapper with some commonly used features.
     * This method:
     * <ul>
     *     <li>Registers the {@link JavaTimeModule} to support Java 8 Date/Time types</li>
     *     <li>Configures the mapper to ignore unknown properties when deserializing JSON</li>
     *     <li>Configures the mapper to not write dates as timestamps</li>
     *     <li>Sets the serialization inclusion to {@link JsonInclude.Include#NON_NULL} to exclude null fields</li>
     * </ul>
     */
    public static void initJsonMapper() {
        Objects.requireNonNull(objectMapper).registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * Gets the shared ObjectMapper instance.
     *
     * @return the configured ObjectMapper
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Serializes the given object to a JSON string.
     *
     * @param value The object to serialize
     * @return The JSON string representation of the object
     * @throws Exception If the serialization fails
     */
    public static String serialize(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    /**
     * Deserializes the given JSON string to the given class type.
     * <p>
     * This method will throw a {@link Exception} if the deserialization fails.
     * <p>
     * @param json The JSON string to deserialize
     * @param clazz The class type to deserialize to
     * @return The deserialized object of the given class type
     * @throws Exception If the deserialization fails
     */
    public static <T> T deserialize(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }
}
