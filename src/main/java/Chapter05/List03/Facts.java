package Chapter05.List03;

import java.util.HashMap;
import java.util.Map;

public class Facts<K, V> {
    private final Map<K, V> facts = new HashMap<>();

    public V getFact(final K name) {
        return this.facts.get(name);
    }

    public void addFact(final K name, final V value) {
        this.facts.put(name, value);
    }
}
