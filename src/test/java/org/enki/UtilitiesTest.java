package org.enki;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.time.Duration;

public class UtilitiesTest {

    @Test
    public void sleepUninterruptiblyTest() {
        final Duration d = Duration.ofSeconds(1);
        final Duration took = Utilities.timeRunnable(() -> Utilities.sleepUninterruptibly(d));
        assertTrue(took.compareTo(d) >= 0);
    }

}
