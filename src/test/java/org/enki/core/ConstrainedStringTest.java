package org.enki.core;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConstrainedStringTest {

    private static class CallSign extends RegularExpressionConstrainedString<CallSign> {

        @NotNull
        private final String sign;

        public CallSign(@NotNull final String sign) {
            super(sign);
            this.sign = sign;
        }

        private static final Pattern pattern = Pattern.compile("[\\p{Digit}\\p{Upper}]+");

        @Override
        @NotNull
        protected Pattern pattern() {
            return pattern;
        }

        @Override
        @NotNull
        public String toString() {
            return sign;
        }

    }

    @Test
    public void testCallSign() {
        final CallSign cWDC4444 = new CallSign("WDC4444");
        assertEquals("WDC4444", cWDC4444.toString());
        assertEquals(7, cWDC4444.length());
        assertEquals("DC4", cWDC4444.subSequence(1, 4));
        assertEquals('C', cWDC4444.charAt(2));
        assertEquals(cWDC4444, new CallSign("WDC4444"));
        assertEquals(cWDC4444, cWDC4444);
        assertNotEquals(cWDC4444, "not a callsign");
        assertNotEquals(cWDC4444, null);
        assertNotEquals(new CallSign("K4444"), cWDC4444);
        assertTrue(new CallSign("K4444").compareTo(cWDC4444) < 0);
        assertThrows(NullPointerException.class, () -> new CallSign(null));
        assertThrows(IllegalArgumentException.class, () -> new CallSign("@"));
        assertThrows(IllegalArgumentException.class, () -> new CallSign("a-3"));
        assertThrows(IllegalArgumentException.class, () -> new CallSign("a25491182"));
        assertThrows(IllegalArgumentException.class, () -> new CallSign(""));
        assertEquals(8, Set.of(cWDC4444, "2", "3", "4", "5", "6", "7", "8").size());
    }

}
