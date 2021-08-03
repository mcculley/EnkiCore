package org.enki;

import com.google.common.collect.AbstractIterator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
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
     * Create a Stream of Node objects from a NodeList.
     *
     * @param l The NodeList
     * @return The stream
     */
    public static Stream<Node> stream(final NodeList l) {
        final int length = l.getLength();
        final AtomicInteger index = new AtomicInteger();
        return Stream.generate(() -> l.item(index.getAndIncrement())).limit(length);
    }

    /**
     * Do the simplest possible parsing of an XML document from a resource.
     *
     * @param i the resource identifier
     * @return the parsed document
     * @throws SAXException if the document did not parse
     * @throws IOException if the resource could not be read
     */
    public static Document parseSimple(final URI i) throws SAXException, IOException {
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            return docBuilder.parse(i.toString());
        } catch (final ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static String serialize(final Document doc, final boolean prependBOM, final String method) {
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();

            if (prependBOM) {
                // FIXME: I have no idea why I need to prepend the UTF-8 BOM.
                final int[] BYTE_ORDER_MARK = {239, 187, 191};
                for (int i : BYTE_ORDER_MARK) {
                    baos.write((byte) i);
                }
            }

            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, method);
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(doc), new StreamResult(baos));

            return new String(baos.toByteArray(), StandardCharsets.UTF_8);
        } catch (final Exception e) {
            throw new AssertionError(e);
        }
    }

}