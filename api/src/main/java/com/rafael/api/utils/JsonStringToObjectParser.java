package com.rafael.api.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonStringToObjectParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Object> parseJson(String jsonString) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            return objectMapper.convertValue(jsonNode, Map.class);
        } catch (Exception e) {
            return Map.of("error", "Error parsing the JSON string: " + e.getMessage());
        }
    }

    public static Object parseJson(String jsonString, String... fieldNames) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            for (String fieldName : fieldNames) {
                jsonNode = jsonNode.path(fieldName);
                if (jsonNode.isArray()) {
                    jsonNode = jsonNode.isArray() && jsonNode.size() > 0 ? jsonNode.get(0) : null;
                }
                if (jsonNode == null || jsonNode.isMissingNode()) {
                    return "Error parsing field, unknown attribute";
                }
            }

            if (jsonNode.isTextual()) {
                return jsonNode.asText();
            }

            return jsonNode.toString();
        } catch (Exception e) {
            return "Error parsing field: " + e.getMessage();
        }
    }

    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }
}