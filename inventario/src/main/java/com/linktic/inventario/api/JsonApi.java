package com.linktic.inventario.api;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility para respuestas JSON:API simples.
 * data: { type, id, attributes: { ... } }
 */
public class JsonApi {

    public static Map<String, Object> single(String type, Object id, Map<String, Object> attributes) {
        return Map.of("data", Map.of(
                "type", type,
                "id", id == null ? null : id.toString(),
                "attributes", attributes
        ));
    }

    public static Map<String, Object> collection(String type, List<Map<String,Object>> items) {
        List<Map<String,Object>> data = items.stream()
                .map(attrs -> Map.of("type", type, "id", attrs.get("id")==null?null:attrs.get("id").toString(), "attributes", attrs))
                .collect(Collectors.toList());
        return Map.of("data", data);
    }

    public static Map<String,Object> error(String title, String detail) {
        return Map.of("errors", List.of(Map.of("title", title, "detail", detail)));
    }
}
