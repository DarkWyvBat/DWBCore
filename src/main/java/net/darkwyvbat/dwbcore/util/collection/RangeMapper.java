package net.darkwyvbat.dwbcore.util.collection;

import java.util.Map;
import java.util.TreeMap;

public record RangeMapper<T extends Number & Comparable<T>, V>(TreeMap<T, V> thresholds, V defaultValue) {
    public RangeMapper(TreeMap<T, V> thresholds, V defaultValue) {
        this.thresholds = new TreeMap<>(thresholds);
        this.defaultValue = defaultValue;
    }

    public V get(T value) {
        Map.Entry<T, V> entry = thresholds.floorEntry(value);
        return entry != null ? entry.getValue() : defaultValue;
    }

    public static <T extends Comparable<T>> boolean isBetween(T v, T min, T max) {
        return v.compareTo(min) >= 0 && v.compareTo(max) <= 0;
    }

    public static <T extends Comparable<T>> boolean isBelow(T v, T min) {
        return v.compareTo(min) < 0;
    }

    public static <T extends Comparable<T>> boolean isAbove(T v, T max) {
        return v.compareTo(max) > 0;
    }

    public boolean isUnder(T v) {
        return !thresholds.isEmpty() && v.compareTo(thresholds.firstKey()) < 0;
    }

    public boolean isOver(T v) {
        return !thresholds.isEmpty() && v.compareTo(thresholds.lastKey()) >= 0;
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