package org.enki.core;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConstrainedStringTest {

    private static class CallSign extends ConstrainedString {

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
        assertEquals("WDC4444", new CallSign("WDC4444").toString());
        assertEquals(new CallSign("WDC4444"), new CallSign("WDC4444"));
        assertNotEquals(new CallSign("K4444"), new CallSign("WDC4444"));
        assertTrue(new CallSign("K4444").compareTo(new CallSign("WDC4444")) < 0);
        assertThrows(IllegalArgumentException.class, () -> new CallSign(null));
        assertThrows(IllegalArgumentException.class, () -> new CallSign("@"));
        assertThrows(IllegalArgumentException.class, () -> new CallSign("a-3"));
        assertThrows(IllegalArgumentException.class, () -> new CallSign("a25491182"));
        assertThrows(IllegalArgumentException.class, () -> new CallSign(""));
    }

}
