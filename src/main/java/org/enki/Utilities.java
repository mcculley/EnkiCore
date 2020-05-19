package org.enki;

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