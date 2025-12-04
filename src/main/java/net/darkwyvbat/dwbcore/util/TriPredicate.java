package net.darkwyvbat.dwbcore.util;

@FunctionalInterface
public interface TriPredicate<T, U, V> {
    boolean test(T t, U u, V v);
}