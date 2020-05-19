package org.enki;

/*-
 * #%L
 * EnkiCore
 * %%
 * Copyright (C) 2020 Gene McCulley
 * %%
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
 * #L%
 */

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
     * Sleep until a specified time, ignoring <code>InterruptedException</code>. The current thread will sleep at least
     * until the supplied <code>Instant</code>.
     *
     * @param wakeupAt the <code>Instant</code> to wake up at
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
     * Sleep for the specified duration, ignoring <code>InterruptedException</code>.
     *
     * @param duration the <code>Duration</code> to sleep
     */
    public static void sleepUninterruptibly(final Duration duration) {
        sleepUninterruptibly(Instant.now().plus(duration));
    }

    /**
     * Time the execution of a <code>Runnable</code>.
     *
     * @param r the <code>Runnable</code> to execute
     * @return the <code>Duration</code> taken
     */
    public static Duration timeRunnable(final Runnable r) {
        final long start = System.nanoTime();
        r.run();
        return Duration.ofNanos(System.nanoTime() - start);
    }

}
