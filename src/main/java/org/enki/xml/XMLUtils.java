package org.enki.xml;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import org.enki.core.ExcludeFromJacocoGeneratedReport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Some utilities for dealing with XML.
 */
public class XMLUtils {

    @ExcludeFromJacocoGeneratedReport
    private XMLUtils() {
        throw new AssertionError("static utility class is not intended to be instantiated");
    }

    /**
     * A very strict <code>ErrorHandler</code>. It immediately throws <code>AssertionError</code> for any problem encountered.
     */
    public static final ErrorHandler strictErrorHandler = new ErrorHandler() {

        @Override
        public void warning(final SAXParseException e) {
            throw new AssertionError(e);
        }

        @Override
        public void error(final SAXParseException e) {
            throw new AssertionError(e);
        }

        @Override
        public void fatalError(final SAXParseException e) {
            throw new AssertionError(e);
        }

    };

    /**
     * Serialize a <code>Document</code> to a <code>String</code> as XHTML and prepend the Unicode BOM.
     *
     * @param doc the <code>Document</code> to serialize
     * @return the serialized <code>Document</code>
     */
    public static @NotNull String serialize(final @NotNull Document doc) {
        return serialize(doc, true, "xhtml");
    }

    /**
     * Serialize a <code>Document</code> to a <code>String</code> as XHTML.
     *
     * @param doc        the <code>Document</code> to serialize
     * @param prependBOM <code>true</code> if the Unicode BOM should be prepended
     * @return the serialized <code>Document</code>
     */
    public static @NotNull String serialize(final @NotNull Document doc, final boolean prependBOM) {
        return serialize(doc, prependBOM, "xhtml");
    }

    /**
     * Serialize a <code>Document</code> to a <code>String</code>.
     *
     * @param doc        the <code>Document</code> to serialize
     * @param prependBOM <code>true</code> if the Unicode BOM should be prepended
     * @param method     the method of serialization to use (e.g., "xhtml", "xml", "html", "text")
     * @return the serialized <code>Document</code>
     */
    public static @NotNull String serialize(final @NotNull Document doc, final boolean prependBOM, final String method) {
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();

            if (prependBOM) {
                final int[] BYTE_ORDER_MARK = {239, 187, 191};
                for (int i : BYTE_ORDER_MARK) {
                    baos.write((byte) i);
                }
            }

            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, method);
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, method.equals("xml") ? "no" : "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(new DOMSource(doc), new StreamResult(baos));

            return baos.toString(StandardCharsets.UTF_8);
        } catch (final Exception e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Create a new empty <code>Document</code>.
     *
     * @return the new <code>Document</code>
     */
    public static @NotNull Document newDocument() {
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            return docBuilder.newDocument();
        } catch (final ParserConfigurationException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Parse a <code>String</code> containing an XML document.
     *
     * @param s the <code>String</code> containing the XML document
     * @return the parsed <code>Document</code>
     */
    public static @NotNull Document parse(final @NotNull String s) {
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            return docBuilder.parse(new InputSource(new StringReader(s)));
        } catch (final Exception e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Convert a URL to a URI, rethrowing any <code>URISyntaxException</code> as an <code>IllegalArgumentException</code>.
     *
     * @param u the URL to convert
     * @return the <code>URI</code>
     */
    public static @NotNull URI toURIUnchecked(final @NotNull URL u) {
        Preconditions.checkNotNull(u);
        try {
            return u.toURI();
        } catch (final URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Parse an XML document at a URL.
     *
     * @param u        the URL of the document to parse
     * @param validate <code>true</code> if the document should be validated
     * @return the parsed <code>Document</code>
     * @throws SAXException if there are any problems parsing the XML
     * @throws IOException if any IO errors occur
     */
    public static @NotNull Document parse(final @NotNull URL u, final boolean validate) throws SAXException, IOException {
        return parse(toURIUnchecked(u), validate);
    }

    /**
     * Parse an XML document at a URI, rethrowing exceptions as runtime exceptions.
     *
     * @param i        the <code>URI</code> of the document
     * @param validate <code>true</code> if the document should be validated
     * @return the parsed <code>Document</code>
     */
    public static @NotNull Document parseUnchecked(final @NotNull URI i, final boolean validate) {
        try {
            return parse(i, validate);
        } catch (final SAXException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Do the simplest possible parsing of an XML document from a resource.
     *
     * @param i the resource identifier
     * @return the parsed document
     * @throws SAXException if the document did not parse
     * @throws IOException if the resource could not be read
     */
    public static Document parse(final URI i) throws SAXException, IOException {
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            return docBuilder.parse(i.toString());
        } catch (final ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parse an XML document at a URI.
     *
     * @param i        the <code>URI</code> of the document
     * @param validate <code>true</code> if the document should be validated
     * @return the parsed <code>Document</code>
     * @throws SAXException if there are any problems parsing the XML
     * @throws IOException if any IO errors occur
     */
    public static @NotNull Document parse(final @NotNull URI i, final boolean validate) throws SAXException, IOException {
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        if (validate) {
            docFactory.setValidating(true);
        }

        try {
            final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            docBuilder.setErrorHandler(strictErrorHandler);
            return docBuilder.parse(i.toString());
        } catch (final ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parse an XML document at from an <code>InputStream</code>, rethrowing exceptions as runtime exceptions.
     *
     * @param i        the <code>URI</code> of the document
     * @param validate <code>true</code> if the document should be validated
     * @return the parsed <code>Document</code>
     */
    public static @NotNull Document parseUnchecked(final @NotNull InputStream i, final boolean validate) {
        try {
            return parse(i, validate);
        } catch (final SAXException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Parse an XML document at from an <code>InputStream</code>.
     *
     * @param i        the <code>URI</code> of the document
     * @param validate <code>true</code> if the document should be validated
     * @return the parsed <code>Document</code>
     * @throws SAXException if there are any problems parsing the XML
     * @throws IOException if any IO errors occur
     */
    public static @NotNull Document parse(final @NotNull InputStream i, final boolean validate) throws SAXException, IOException {
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        if (validate) {
            docFactory.setValidating(true);
        }

        try {
            final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            docBuilder.setErrorHandler(strictErrorHandler);
            return docBuilder.parse(i);
        } catch (final ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load a <code>Transformer</code> from a supplied URL.
     *
     * @param templateURL the URL of the transform
     * @return the parsed <code>Transformer</code>
     */
    public static @NotNull Transformer loadTransformer(final @NotNull URL templateURL) {
        try {
            final TransformerFactory factory = TransformerFactory.newInstance();
            final Source xslt = new StreamSource(templateURL.openStream());
            return factory.newTransformer(xslt);
        } catch (final TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Transform an XML document to HTML, using a supplied <code>Transformer</code>.
     *
     * @param templateURL the URL of the transform
     * @param document    the XML <code>Document</code> to transform
     * @return the transformed <code>Document</code>
     */
    public static @NotNull Document transformToHTML(final @NotNull URL templateURL, final @NotNull Document document) {
        return transformToHTML(loadTransformer(templateURL), document);
    }

    /**
     * Transform an XML document to HTML, using a supplied <code>Transformer</code>.
     *
     * @param transformer the <code>Transformer</code>
     * @param document    the XML <code>Document</code> to transform
     * @return the transformed <code>Document</code>
     */
    public static @NotNull Document transformToHTML(final @NotNull Transformer transformer, final @NotNull Document document) {
        try {
            final DOMResult outputResult = new DOMResult();
            transformer.setOutputProperty(OutputKeys.METHOD, "xhtml");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xalan}omit-meta-tag", "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(new DOMSource(document), outputResult);
            return (Document) outputResult.getNode();
        } catch (final TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serialized a <code>Document</code> to a <code>File</code>.
     *
     * @param document the <code>Document</code> to serialize
     * @param file     the <code>File</code> to write the serialized document to
     * @throws TransformerException if there was an exception during serialization
     */
    public static void serialize(final @NotNull Document document, final @NotNull File file) throws TransformerException {
        final TransformerFactory tFactory = TransformerFactory.newInstance();
        final Transformer transformer = tFactory.newTransformer();
        final DOMSource source = new DOMSource(document);
        final StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }

    /**
     * Get an <code>Iterable</code> for an <code>Iterator</code>.
     *
     * @param iterator the <code>Iterator</code>
     * @param <T>      the element type
     * @return an <code>Iterable</code> for for the <code>Iterator</code>
     */
    public static @NotNull <T> Iterable<T> iteratorToIterable(final @NotNull Iterator<T> iterator) {
        return () -> iterator;
    }

    /**
     * Get an <code>Iterable</code> of child elements of an XML element of a given tag name.
     *
     * @param e    the <code>Element</code>
     * @param name the tag name of the children
     * @return an <code>Iterable</code> of the child elements
     */
    public static @NotNull Iterable<Element> elementsByTagNameIterable(final @NotNull Element e, final @NotNull String name) {
        return iteratorToIterable(elementsByTagNameIterator(e, name));
    }

    /**
     * Get an <code>Iterator</code> of child elements of an XML element of a given tag name.
     *
     * @param e    the <code>Element</code>
     * @param name the tag name of the children
     * @return an <code>Iterator</code> of the child elements
     */
    private static @NotNull Iterator<Element> elementsByTagNameIterator(final @NotNull Element e, final @NotNull String name) {
        final NodeList nodes = e.getElementsByTagName(name);
        final int numNodes = nodes.getLength();
        return new Iterator<>() {

            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < numNodes;
            }

            @Override
            public Element next() {
                return (Element) nodes.item(i++);
            }

        };
    }

    /**
     * Get a <code>Stream</code> of child elements of an XML element of a given tag name.
     *
     * @param e    the <code>Element</code>
     * @param name the tag name of the children
     * @return an <code>Stream</code> of the child elements
     */
    public static @NotNull Stream<Element> getElementsByTagName(final @NotNull Element e, final @NotNull String name) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(elementsByTagNameIterator(e, name), 0), false);
    }

    /**
     * Turn a <code>NamedNodeMap</code> into a <code>List</code>. This is a convenience function for dealing with attributes. It
     * returns an empty <code>List</code> if given <code>null</code>.
     *
     * @param map the <code>NamedNodeMap</code> of attributes
     * @return a <code>List</code> of <code>Attr</code> objects
     */
    public static @NotNull List<Attr> toList(final @Nullable NamedNodeMap map) {
        if (map == null) {
            return Collections.emptyList();
        }

        final List<Attr> list = new ArrayList<>();
        for (int i = 0; i < map.getLength(); i++) {
            list.add((Attr) map.item(i));
        }

        return list;
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

}
