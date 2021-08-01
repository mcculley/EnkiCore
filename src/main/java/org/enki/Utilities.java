package org.enki;

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

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.time.Duration;
import java.time.Instant;

/**
 * Utility functions that do not fit well elsewhere.
 */
public class Utilities {

    static {
        Assertions.assertAssertionsEnabled();
    }

    private Utilities() {
        throw new AssertionError("static utility class is not intended to be instantiated");
    }

    /**
     * Return a String that formats a count into a human readable number of bytes.
     *
     * See: https://stackoverflow.com/questions/3758606/how-can-i-convert-byte-size-into-a-human-readable-format-in-java
     *
     * @param bytes the number of bytes
     * @return the formatted String.
     */
    public static String humanReadableByteCountBinary(long bytes) {
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
     * Sleep until a specified time, ignoring {@link java.lang.InterruptedException InterruptedException}. The current
     * thread will sleep at least until the supplied {@link java.time.Instant Instant}.
     *
     * @param wakeupAt the {@link java.time.Instant Instant} to wake up at
     */
    public static void sleepUninterruptibly(final Instant wakeupAt) {
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
    public static void sleepUninterruptibly(final Duration duration) {
        sleepUninterruptibly(Instant.now().plus(duration));
    }

    /**
     * Time the execution of a {@link java.lang.Runnable Runnable}.
     *
     * @param r the {@link java.lang.Runnable Runnable} to execute
     * @return the {@link java.time.Duration Duration} taken
     */
    public static Duration timeRunnable(final Runnable r) {
        final long start = System.nanoTime();
        r.run();
        return Duration.ofNanos(System.nanoTime() - start);
    }

    /**
     * The CloudFlare caching layer obfuscates email addresses. This decodes the address.
     *
     * @param s the encoded address
     * @return the decoded address
     */
    public static String decodeCloudFlareEmail(final String s) {
        final StringBuilder email = new StringBuilder();
        final int r = Integer.parseInt(s.substring(0, 2), 16);
        for (int n = 2; s.length() - n > 0; n += 2) {
            final int i = Integer.parseInt(s.substring(n, n + 2), 16) ^ r;
            email.append((char) i);
        }

        return email.toString();
    }

}
