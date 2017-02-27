package com.github.hagward.mymustache;

import java.util.Map;

class Scope {

    private final String name;
    private final Scope parent;
    private final Map<String, Object> context;

    Scope(String name, Scope parent, Map<String, Object> context) {
        this.name = name;
        this.parent = parent;
        this.context = context;
    }

    String getName() {
        return name;
    }

    private Scope getParent() {
        return parent;
    }

    private Map<String, Object> getContext() {
        return context;
    }

    boolean isEnabled() {
        return context != null;
    }

    Object get(String key) {
        if (context == null) {
            return null;
        }

        String[] parts = key.split("\\.");

        Object value = context.get(parts[0]);
        Scope scope = parent;

        while (value == null && scope != null) {
            value = scope.getContext().get(parts[0]);
            scope = scope.getParent();
        }

        for (int i = 1; i < parts.length; i++) {
            if (!(value instanceof Map)) {
                return null;
            }
            value = ((Map) value).get(parts[i]);
        }

        return value;
    }

    Object getOrEmptyString(String key) {
        Object value = get(key);
        return value != null ? value : "";
    }
}
