package org.enki.core;

/*
 * EnkiCore
 *
 * Copyright © 2020 Gene McCulley
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import com.google.common.base.Converter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.NotNull;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

/**
 * Utility functions that do not fit well elsewhere.
 */
public class Utilities {

    static {
        Assertions.assertAssertionsEnabled();
    }

    @ExcludeFromJacocoGeneratedReport
    private Utilities() {
        throw new AssertionError("static utility class is not intended to be instantiated");
    }

    /**
     * Return a String that formats a count into a human readable number of bytes.
     * <p>
     * See: https://stackoverflow.com/questions/3758606/how-can-i-convert-byte-size-into-a-human-readable-format-in-java
     *
     * @param bytes the number of bytes
     * @return the formatted String.
     */
    public static @NotNull String humanReadableByteCountBinary(long bytes) {
        final long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }

        long value = absB;
        final CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }

        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }

    /**
     * Sleep until a specified time, ignoring {@link java.lang.InterruptedException InterruptedException}. The current thread will
     * sleep at least until the supplied {@link java.time.Instant Instant}.
     *
     * @param wakeupAt the {@link java.time.Instant Instant} to wake up at
     */
    public static void sleepUninterruptibly(final @NotNull Instant wakeupAt) {
        Instant now = Instant.now();
        while (now.compareTo(wakeupAt) <= 0) {
            try {
                Thread.sleep(Duration.between(now, wakeupAt).toMillis());
            } catch (final InterruptedException e) {
                // Ignore.
            }

            now = Instant.now();
        }
    }

    /**
     * Sleep for the specified duration, ignoring {@link java.lang.InterruptedException InterruptedException}.
     *
     * @param duration the {@link java.time.Duration Duration} to sleep
     */
    public static void sleepUninterruptibly(final @NotNull Duration duration) {
        sleepUninterruptibly(Instant.now().plus(duration));
    }

    /**
     * Time the execution of a {@link java.lang.Runnable Runnable}.
     *
     * @param r the {@link java.lang.Runnable Runnable} to execute
     * @return the {@link java.time.Duration Duration} taken
     */
    public static @NotNull Duration timeRunnable(final @NotNull Runnable r) {
        final long start = System.nanoTime();
        r.run();
        return Duration.ofNanos(System.nanoTime() - start);
    }

    public static @NotNull String to2DigitHexString(final byte x) {
        final String s = Integer.toHexString(x & 0xFF);
        return s.length() == 1 ? "0" + s : s;
    }

    /**
     * The CloudFlare caching layer obfuscates email addresses. This is an encoder/decoder for that scheme.
     *
     * FIXME: This should probably be in some "miscellaneous" package.
     */
    public static final Converter<String, String> cloudFlareObfuscation = new Converter<>() {

        private final Random rng = new Random();

        @Override
        @SuppressFBWarnings(
                value = "HE_INHERITS_EQUALS_USE_HASHCODE",
                justification = "The Converter class only overrides equals() for documentation purposes, deferring it to super."
        )
        protected String doForward(final String s) {
            final byte r = (byte) rng.nextInt();
            final StringBuilder b = new StringBuilder();
            b.append(to2DigitHexString(r));
            final int length = s.length();
            for (int i = 0; i < length; i++) {
                final char c = s.charAt(i);
                final byte encoded = (byte) (c ^ r);
                b.append(to2DigitHexString(encoded));
            }

            return b.toString();
        }

        @Override
        protected String doBackward(final String s) {
            final StringBuilder email = new StringBuilder();
            final int r = Integer.parseInt(s.substring(0, 2), 16);
            for (int n = 2; s.length() - n > 0; n += 2) {
                final int i = Integer.parseInt(s.substring(n, n + 2), 16) ^ r;
                email.append((char) i);
            }

            return email.toString();
        }

    };

    /**
     * Given a floating point number, render it into a String with no trailing zeros. (e.g. 25.0 renders as "25" and 25.050 renders
     * as "25.05").
     *
     * @param x the number to render
     * @return a String with no trailing zeros or decimal point if there are no fractional digits
     */
    public static @NotNull String formatWithoutTrailingZeros(final double x) {
        return Double.toString(x).replaceAll("\\.?0*$", "");
    }

}
