package com.quiz.ai.correctionModule.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FlexibleStringListDeserializer extends JsonDeserializer<List<String>> {
    @Override
    public List<String> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonToken token = parser.currentToken();

        if (token == JsonToken.VALUE_NULL) {
            return Collections.emptyList();
        }

        if (token == JsonToken.VALUE_STRING) {
            String value = parser.getValueAsString();
            if (value == null || value.isBlank()) {
                return Collections.emptyList();
            }
            return List.of(value.trim());
        }

        if (token == JsonToken.START_ARRAY) {
            List<String> values = new ArrayList<>();
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                JsonNode node = parser.readValueAsTree();
                addNodeValue(values, node);
            }
            return values;
        }

        if (token == JsonToken.START_OBJECT) {
            JsonNode node = parser.readValueAsTree();
            List<String> values = new ArrayList<>();
            addNodeValue(values, node);
            return values;
        }

        return Collections.emptyList();
    }

    private void addNodeValue(List<String> values, JsonNode node) {
        if (node == null || node.isNull()) {
            return;
        }

        if (node.isTextual()) {
            String text = node.asText();
            if (!text.isBlank()) {
                values.add(text.trim());
            }
            return;
        }

        if (node.isObject()) {
            String text = firstText(node, "text", "content", "value", "correction");
            if (text != null && !text.isBlank()) {
                values.add(text.trim());
                return;
            }
        }

        String fallback = node.asText();
        if (fallback != null && !fallback.isBlank() && !"null".equalsIgnoreCase(fallback)) {
            values.add(fallback.trim());
            return;
        }

        String raw = node.toString();
        if (raw != null && !raw.isBlank()) {
            values.add(raw);
        }
    }

    private String firstText(JsonNode node, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode field = node.get(fieldName);
            if (field != null && !field.isNull() && field.isTextual()) {
                String value = field.asText();
                if (!value.isBlank()) {
                    return value;
                }
            }
        }
        return null;
    }
}