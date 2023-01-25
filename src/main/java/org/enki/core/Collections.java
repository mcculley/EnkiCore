package org.enki.core;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Collections {

    private Collections() {
        throw new AssertionError("static utility class is not intended to be instantiated");
    }

    /**
     * Returns a {@code Collector} that accumulates elements into a {@code LinkedHashMap} whose keys and values are the
     * result of applying the provided mapping functions to the input elements.
     * <p>
     * This does not allow for duplicate keys.
     *
     * @param <T>         the type of the input elements
     * @param <K>         the output type of the key mapping function
     * @param <U>         the output type of the value mapping function
     * @param keyMapper   a mapping function to produce keys
     * @param valueMapper a mapping function to produce values
     * @return a {@code Collector} which collects elements into a {@code LinkedHashMap} whose keys are the result of
     * applying a key mapping function to the input elements, and whose values are the result of applying a value
     * mapping function to all input elements equal to the key and combining them using the merge function
     */
    public static <T, K, U> Collector<T, ?, Map<K, U>> toLinkedHashMap(final Function<? super T, ? extends K> keyMapper,
                                                                       final Function<? super T, ? extends U> valueMapper) {
        return Collectors.toMap(keyMapper, valueMapper, (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        }, LinkedHashMap::new);
    }

    /**
     * Returns a {@code Set} of {@code Map.Entry} objects sorted by the values.
     *
     * @param map the source {@code Map}
     * @param c the {@code Comparator} to use
     * @param <K> the type of the keys
     * @param <V> the type of the values
     * @return a {@code Set} of entries sorted by value
     */
    public static <K, V> Set<Map.Entry<K, V>> sortByValue(final Map<K, V> map, final Comparator<V> c) {
        // FIXME: Can I use Map.Entry.comparingByValue()?
        return java.util.Collections.unmodifiableSet(
                map.entrySet().stream().sorted((o1, o2) -> c.compare(o1.getValue(), o2.getValue()))
                        .collect(toLinkedHashMap(Map.Entry::getKey, Map.Entry::getValue)).entrySet());
    }

    /**
     * Returns a {@code Set} of {@code Map.Entry} objects sorted by the values using the natural order.
     *
     * @param map the source {@code Map}
     * @param <K> the type of the keys
     * @param <V> the type of the values
     * @return a {@code Set} of entries sorted by value
     */
    public static <K, V> Set<Map.Entry<K, V>> sortByValue(final Map<K, V> map) {
        final Comparator<V> c = (Comparator<V>) Comparator.naturalOrder();
        return sortByValue(map, c);
    }

}
