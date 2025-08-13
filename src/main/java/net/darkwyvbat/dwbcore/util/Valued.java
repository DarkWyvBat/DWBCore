package net.darkwyvbat.dwbcore.util;

public interface Valued<T extends Number & Comparable<T>> {

    T getValue();

    default boolean is(Valued<T> value) {
        return this.getValue().compareTo(value.getValue()) == 0;
    }

    default boolean isLess(Valued<T> value) {
        return this.getValue().compareTo(value.getValue()) < 0;
    }

    default boolean isGreater(Valued<T> value) {
        return this.getValue().compareTo(value.getValue()) > 0;
    }

    default boolean isNotGreater(Valued<T> value) {
        return this.getValue().compareTo(value.getValue()) <= 0;
    }

    default boolean isNotLess(Valued<T> value) {
        return this.getValue().compareTo(value.getValue()) >= 0;
    }
}