package org.enki.core;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utilities for Collections.
 */
public class Collections {

    @ExcludeFromJacocoGeneratedReport
    private Collections() {
        throw new AssertionError("static utility class is not intended to be instantiated");
    }

    /**
     * Returns a {@code List} of {@code Map.Entry} objects sorted by the values.
     *
     * @param map the source {@code Map}
     * @param c   the {@code Comparator} to use
     * @param <K> the type of the keys
     * @param <V> the type of the values
     * @return a {@code Set} of entries sorted by value
     */
    @NotNull
    public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> sortByValue(final @NotNull Map<K, V> map,
                                                                                         @NotNull final Comparator<V> c) {
        return map.entrySet().stream().sorted(Map.Entry.comparingByValue(c)).collect(Collectors.toList());
    }

    /**
     * Returns a {@code List} of {@code Map.Entry} objects sorted by the values using the natural order.
     *
     * @param map the source {@code Map}
     * @param <K> the type of the keys
     * @param <V> the type of the values
     * @return a {@code Set} of entries sorted by value
     */
    @NotNull
    public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> sortByValue(final @NotNull Map<K, V> map) {
        return sortByValue(map, Comparator.naturalOrder());
    }

    /**
     * Given an object and a <code>List</code> of objects, concatenate them into a <code>List</code>.
     *
     * @param a   the first object
     * @param b   the <code>List</code> of objects
     * @param <T> the type of object
     * @return a <code>List</code> containing all objects
     */
    public static <T> List<T> concat(final T a, final List<T> b) {
        final List<T> l = new ArrayList<>();
        l.add(a);
        l.addAll(b);
        return l;
    }

}
