package net.darkwyvbat.dwbcore.util.collection;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

public final class MutableMapBuilder<K, V> {

    private final Map<K, V> map;

    private MutableMapBuilder(Supplier<Map<K, V>> mapFactory) {
        this.map = mapFactory.get();
    }

    public static <K, V> MutableMapBuilder<K, V> builder(Supplier<Map<K, V>> mapFactory) {
        return new MutableMapBuilder<>(mapFactory);
    }

    public static <K, V> MutableMapBuilder<K, V> newHashMap() {
        return new MutableMapBuilder<>(HashMap::new);
    }

    public static <K, V> MutableMapBuilder<K, V> newLinkedHashMap() {
        return new MutableMapBuilder<>(LinkedHashMap::new);
    }

    public static <K, V> MutableMapBuilder<K, V> newTreeMap() {
        return new MutableMapBuilder<>(TreeMap::new);
    }

    public MutableMapBuilder<K, V> put(K key, V value) {
        this.map.put(key, value);
        return this;
    }

    public MutableMapBuilder<K, V> putAll(Map<? extends K, ? extends V> sourceMap) {
        if (sourceMap != null)
            this.map.putAll(sourceMap);
        return this;
    }

    public MutableMapBuilder<K, V> putIf(boolean condition, K key, V value) {
        if (condition)
            this.map.put(key, value);
        return this;
    }

    public Map<K, V> build() {
        return this.map;
    }
}