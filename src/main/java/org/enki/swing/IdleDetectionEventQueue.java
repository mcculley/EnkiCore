package org.enki.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;

public class IdleDetectionEventQueue extends ChainableEventQueueDispatcher {

    private Instant lastInteractiveEventTimestamp = Instant.now();
    private boolean idle = false;
    private final Duration idleThreshold;

    public IdleDetectionEventQueue(final Duration idleCheckInterval, final Duration idleThreshold) {
        this.idleThreshold = idleThreshold;
        new Timer((int) idleCheckInterval.toMillis(), e -> checkIdle()).start();
    }

    public IdleDetectionEventQueue() {
        this(Duration.ofSeconds(1), Duration.ofSeconds(60));
    }

    private void checkIdle() {
        final Duration durationSinceLastInteractiveEvent =
                Duration.between(lastInteractiveEventTimestamp, Instant.now());
        if (!idle && durationSinceLastInteractiveEvent.compareTo(idleThreshold) > 0) {
            idle = true;
            fireListeners();
        }
    }

    @Override
    public void afterDispatch(final AWTEvent event) {
        if (event instanceof InputEvent) {
            lastInteractiveEventTimestamp = Instant.now();
            if (idle) {
                idle = false;
                fireListeners();
            }
        }
    }

    public static class IdleEvent extends EventObject {

        public final boolean idle;

        public IdleEvent(final IdleDetectionEventQueue source, final boolean idle) {
            super(source);
            this.idle = idle;
        }

        public boolean isIdle() {
            return idle;
        }

    }

    public interface IdleListener extends EventListener {

        void wentIdle(IdleEvent e);

        void wentActive(IdleEvent e);

    }

    private final java.util.List<IdleListener> listeners = new ArrayList<>();

    public void addIdleListener(final IdleListener l) {
        listeners.add(l);
    }

    public void removeIdleListener(final IdleListener l) {
        listeners.remove(l);
    }

    public void fireListeners() {
        final IdleEvent e = new IdleEvent(this, idle);
        for (final IdleListener l : listeners) {
            if (idle) {
                l.wentIdle(e);
            } else {
                l.wentActive(e);
            }
        }

    }

    public boolean isIdle() {
        return idle;
    }

}
