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

        Object value = context.get(key);
        Scope scope = parent;

        while (value == null && scope != null) {
            value = scope.getContext().get(key);
            scope = scope.getParent();
        }

        return value;
    }

    Object getOrEmptyString(String key) {
        Object value = get(key);
        return value != null ? value : "";
    }
}
