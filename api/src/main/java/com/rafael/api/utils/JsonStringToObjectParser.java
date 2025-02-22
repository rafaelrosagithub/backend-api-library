package com.rafael.api.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Iterator;
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

    public static Object parseJson(String jsonString, String fieldName) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            return findField(jsonNode, fieldName);
        } catch (Exception e) {
            return "Error parsing field: " + e.getMessage();
        }
    }

    private static Object findField(JsonNode node, String fieldName) {
        if (node.isObject()) {
            if (node.has(fieldName)) {
                return node.get(fieldName).asText();
            }

            for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                Object result = findField(entry.getValue(), fieldName);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}





