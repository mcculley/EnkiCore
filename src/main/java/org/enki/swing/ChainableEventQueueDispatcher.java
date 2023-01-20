package org.enki.swing;

import java.awt.*;

public class ChainableEventQueueDispatcher extends EventQueue {

    public void beforeDispatch(AWTEvent event) {
    }

    public void afterDispatch(AWTEvent event) {
    }

    @Override
    protected void dispatchEvent(AWTEvent event) {
        beforeDispatch(event);
        super.dispatchEvent(event);
        afterDispatch(event);
    }

}
