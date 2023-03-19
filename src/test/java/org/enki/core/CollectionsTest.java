package org.enki.core;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectionsTest {

    @Test
    public void testConcat() {
        final List<Integer> l1 = List.of(1, 2, 3);
        final List<Integer> l2 = List.of(2, 3);
        assertEquals(l1, Collections.concat(1, l2));
    }

}
