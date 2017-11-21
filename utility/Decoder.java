package utility;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Decoder<K extends Serializable, V extends Encodable<K>> {

    private final Map<K, V> map;

    private Decoder(V[] values) throws IllegalArgumentException {
        map = new HashMap<>(values.length);
        for (V value : values) {
            V old = map.put(value.getCode(), value);
            if (old != null) {
                throw new IllegalArgumentException("duplicated code: " + value);
            }
        }
    }

    public V decode(K code) {
        return map.get(code);
    }

    public static <L extends Serializable, W extends Encodable<L>> Decoder<L, W> create(W[] values) throws IllegalArgumentException {
        return new Decoder<>(values);
    }
}