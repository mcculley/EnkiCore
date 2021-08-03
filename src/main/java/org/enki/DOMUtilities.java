package org.enki;

import com.google.common.collect.AbstractIterator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Utilities for use with XML DOM objects.
 *
 * @author mcculley
 */
public class DOMUtilities {

    private DOMUtilities() {
        throw new AssertionError("static utility class is not intended to be instantiated");
    }

    /**
     * Get a view of a NodeList as an Iterator.
     *
     * @param nodeList the NodeList to iterate over
     * @return an Iterator that iterates over nodeList
     */
    public static Iterator<Node> iterator(final NodeList nodeList) {
        return new AbstractIterator<>() {

            private int index;

            @Override
            protected Node computeNext() {
                if (index == nodeList.getLength()) {
                    return endOfData();
                }

                return nodeList.item(index++);
            }

        };
    }

    /**
     * Get a view of a NodeList as an Iterable.
     *
     * @param nodeList the NodeList to iterate over
     * @return an Iterable that iterates over nodeList
     */
    public static Iterable<Node> iterable(final NodeList nodeList) {
        return () -> iterator(nodeList);
    }

    /**
     * Create a Stream<Node> from a NodeList.
     *
     * @param l The NodeList
     * @return The stream
     */
    public static Stream<Node> stream(final NodeList l) {
        final int length = l.getLength();
        final AtomicInteger index = new AtomicInteger();
        return Stream.generate(() -> l.item(index.getAndIncrement())).limit(length);
    }

}