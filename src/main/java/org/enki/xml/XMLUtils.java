package org.enki.xml;

import com.google.common.base.Preconditions;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class XMLUtils {

    private XMLUtils() {
    }

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

    public static String serialize(final Document doc) {
        return serialize(doc, true, "xhtml");
    }

    public static String serialize(final Document doc, final boolean prependBOM) {
        return serialize(doc, prependBOM, "xhtml");
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
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, method.equals("xml") ? "no" : "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(new DOMSource(doc), new StreamResult(baos));

            return new String(baos.toByteArray(), StandardCharsets.UTF_8);
        } catch (final Exception e) {
            throw new AssertionError(e);
        }
    }

    public static Document newDocument() {
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            return docBuilder.newDocument();
        } catch (final ParserConfigurationException e) {
            throw new AssertionError(e);
        }
    }

    public static Document parse(final String s) {
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            return docBuilder.parse(new InputSource(new StringReader(s)));
        } catch (final Exception e) {
            throw new AssertionError(e);
        }
    }

    public static URI toURIUnchecked(final URL u) {
        Preconditions.checkNotNull(u);
        try {
            return u.toURI();
        } catch (final URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Document parse(final URL u, final boolean validate) throws SAXException, IOException {
        return parse(toURIUnchecked(u), validate);
    }

    public static Document parseUnchecked(final URI i, final boolean validate) {
        try {
            return parse(i, validate);
        } catch (final SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Document parse(final URI i, final boolean validate) throws SAXException, IOException {
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

    public static Document parseUnchecked(final InputStream i, final boolean validate) {
        try {
            return parse(i, validate);
        } catch (final SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Document parse(final InputStream i, final boolean validate) throws SAXException, IOException {
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

    public static Transformer loadTransformer(final URL templateURL) {
        try {
            final TransformerFactory factory = TransformerFactory.newInstance();
            final Source xslt = new StreamSource(templateURL.openStream());
            return factory.newTransformer(xslt);
        } catch (final TransformerConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Document transformToHTML(final URL templateURL, final Document document) {
        return transformToHTML(loadTransformer(templateURL), document);
    }

    public static Document transformToHTML(final Transformer transformer, final Document document) {
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

    public static void writeDocumentToFile(final Document document, final File file) throws Exception {
        final TransformerFactory tFactory = TransformerFactory.newInstance();
        final Transformer transformer = tFactory.newTransformer();
        final DOMSource source = new DOMSource(document);
        final StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }

    public static <T> Iterable<T> iteratorToIterable(final Iterator<T> iterator) {
        return () -> iterator;
    }

    public static Iterable<Element> elementsByTagNameIterable(final Element e, final String name) {
        return iteratorToIterable(elementsByTagNameIterator(e, name));
    }

    private static Iterator<Element> elementsByTagNameIterator(final Element e, final String name) {
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

    public static Stream<Element> getElementsByTagName(final Element e, final String name) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(elementsByTagNameIterator(e, name), 0), false);
    }

    public static List<Attr> toList(final NamedNodeMap map) {
        if (map == null) {
            return Collections.emptyList();
        }

        final List<Attr> list = new ArrayList<>();
        for (int i = 0; i < map.getLength(); i++) {
            list.add((Attr) map.item(i));
        }

        return list;
    }

}
