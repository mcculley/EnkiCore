package org.enki;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.time.Duration;

public class UtilitiesTest {

    @Test
    public void sleepUninterruptiblyTest() {
        final Duration d = Duration.ofSeconds(1);
        final long start = System.currentTimeMillis();
        Utilities.sleepUninterruptibly(d);
        final long stop = System.currentTimeMillis();
        final long took = stop - start;
        assertTrue(took >= d.toMillis());
    }

}
