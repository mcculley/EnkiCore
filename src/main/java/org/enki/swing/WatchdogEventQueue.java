package org.enki.swing;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

public class WatchdogEventQueue extends ChainableEventQueueDispatcher {

    private final AtomicReference<Watched> watched = new AtomicReference<>();

    private static class Watched {

        public final Thread thread;
        public final Object context;
        public final Instant startTime;

        private Watched(final Thread thread, final Object context, final Instant startTime) {
            this.thread = thread;
            this.context = context;
            this.startTime = startTime;
        }

    }

    public WatchdogEventQueue() {
        final long limit = 500;
        final Thread watchdogThread = new Thread(() -> {
            while (true) {
                try {
                    final Watched w = watched.get();
                    if (w != null) {
                        final Duration totalTaken = Duration.between(w.startTime, Instant.now());
                        if (totalTaken.toMillis() > limit) {
                            System.err.println("\nwatchdog time exceeded for " + w.context);
                            final StackTraceElement[] stackTrace = w.thread.getStackTrace();
                            for (final StackTraceElement e : stackTrace) {
                                System.err.println(e);
                            }

                            //System.exit(-1);
                        }
                    }

                    Thread.sleep(50);
                } catch (final InterruptedException e) {
                    // Being interrupted is okay. Just start over.
                }
            }
        });

        watchdogThread.start();
    }

    @Override
    public void beforeDispatch(final AWTEvent event) {
        watched.set(new Watched(Thread.currentThread(), event, Instant.now()));
    }

    @Override
    public void afterDispatch(final AWTEvent event) {
        watched.set(null);
    }

}
