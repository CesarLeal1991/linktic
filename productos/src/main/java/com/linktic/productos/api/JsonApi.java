package com.linktic.productos.api;

import java.util.Map;

public class JsonApi {

    public static Map<String, Object> error(String title, String detail) {
        return Map.of(
                "error", Map.of(
                        "title", title,
                        "detail", detail
                )
        );
    }

    public static Map<String, Object> single(String type, Object id, Map<String, Object> attributes) {
        return Map.of(
                "data", Map.of(
                        "type", type,
                        "id", id,
                        "attributes", attributes
                )
        );
    }

    public static Map<String, Object> collection(String type, Iterable<Map<String, Object>> items) {
        return Map.of(
                "data", items
        );
    }
}
