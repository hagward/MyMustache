import java.util.HashMap;
import java.util.Map;

public class Scope {

    private final Scope parent;
    private final Map<String, Object> context;

    public Scope(Scope parent, Map<String, Object> context) {
        this.parent = parent;
        this.context = context;
    }

    public Scope getParent() {
        return parent;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public Object get(String key) {
        Object value = context.get(key);
        Scope scope = parent;

        while (value == null && scope != null) {
            value = scope.getContext().get(key);
            scope = scope.getParent();
        }

        return value;
    }

    public Object getOrEmptyString(String key) {
        Object value = get(key);
        return value != null ? value : "";
    }
}
