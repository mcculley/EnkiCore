package org.enki.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectionsTest {

    @Test
    public void testConcat() {
        final List<Integer> l1 = List.of(1, 2, 3);
        final List<Integer> l2 = List.of(2, 3);
        assertEquals(l1, Collections.concat(1, l2));
    }

    @Test
    public void testSortByValue() {
        final Map<String, Integer> m = Map.of(
                "apple", 5,
                "pear", 3,
                "melon", 6,
                "squash", 1,
                "banana", 3
        );

        final List<Map.Entry<String, Integer>> s = Collections.sortByValue(m);
        assertEquals(m.size(), s.size());
        final List<Map.Entry<String, Integer>> l = new ArrayList<>(s);
        final List<Integer> values = l.stream().map(Map.Entry::getValue).collect(Collectors.toList());
        assertEquals(values, values.stream().sorted().collect(Collectors.toList()));
    }

}
