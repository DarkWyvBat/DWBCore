package net.darkwyvbat.dwbcore.util.collection;

import java.util.Map;
import java.util.TreeMap;

public final class RangeMapper<T extends Number & Comparable<T>, V> {
    private final TreeMap<T, V> thresholds;
    private final V defaultValue;

    private RangeMapper(TreeMap<T, V> thresholds, V defaultValue) {
        this.thresholds = new TreeMap<>(thresholds);
        this.defaultValue = defaultValue;
    }

    public V get(T value) {
        Map.Entry<T, V> entry = thresholds.floorEntry(value);
        return entry != null ? entry.getValue() : defaultValue;
    }

    public static <T extends Number & Comparable<T>, V> Builder<T, V> builder() {
        return new Builder<>();
    }

    public static class Builder<T extends Number & Comparable<T>, V> {
        private final TreeMap<T, V> thresholds = new TreeMap<>();
        private V defaultValue;

        public Builder<T, V> add(T threshold, V value) {
            thresholds.put(threshold, value);
            return this;
        }

        public Builder<T, V> setDefault(V value) {
            this.defaultValue = value;
            return this;
        }

        public RangeMapper<T, V> build() {
            return new RangeMapper<>(thresholds, defaultValue);
        }
    }
}
