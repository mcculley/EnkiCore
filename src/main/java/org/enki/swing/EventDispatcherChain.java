package org.enki.swing;

import java.awt.*;
import java.util.List;

public class EventDispatcherChain extends EventQueue {

    private final List<ChainableEventQueueDispatcher> dispatchers;

    public EventDispatcherChain(final List<ChainableEventQueueDispatcher> dispatchers) {
        this.dispatchers = dispatchers;
    }

    @Override
    protected void dispatchEvent(final AWTEvent event) {
        dispatchers.forEach(d -> d.beforeDispatch(event));
        super.dispatchEvent(event);
        dispatchers.forEach(d -> d.afterDispatch(event));
    }

}
