package org.enki.core;

import org.junit.jupiter.api.Test;

import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PredicatesTest {

    @Test
    public void testAnd() {
        final Predicate<String> isEmpty = String::isEmpty;
        final Predicate<String> isLongerThan4 = (s) -> s.length() > 4;
        assertTrue(isEmpty.test(""));
        assertTrue(isLongerThan4.test("12345"));
        assertFalse(isLongerThan4.test("1234"));
        final Predicate<String> combined = Predicates.and(Stream.of(isEmpty.negate(), isLongerThan4));
        assertTrue(combined.test("12345"));
        assertFalse(combined.test("1234"));
        assertTrue(Predicates.alwaysTrue().test("foo"));
    }

}
