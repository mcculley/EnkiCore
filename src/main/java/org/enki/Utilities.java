package org.enki;

import java.time.Duration;
import java.time.Instant;

public class Utilities {

    static {
        Assertions.assertAssertionsEnabled();
    }

    private Utilities() {
        throw new AssertionError("static utility class is not intended to be instantiated");
    }

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

    public static void sleepUninterruptibly(final Duration duration) {
        sleepUninterruptibly(Instant.now().plus(duration));
    }

}