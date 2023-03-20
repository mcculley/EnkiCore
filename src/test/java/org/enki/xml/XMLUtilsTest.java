package org.enki.xml;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XMLUtilsTest {

    private static Document createTestDocument() {
        final Document d = XMLUtils.newDocument();
        final Element root = d.createElement("menu");
        d.appendChild(root);

        final Element waffles = d.createElement("item");
        waffles.setAttribute("name", "waffles");
        root.appendChild(waffles);
        final Element wafflesDescription = d.createElement("description");
        waffles.appendChild(wafflesDescription);
        wafflesDescription.appendChild(d.createTextNode("Two of our famous Belgian Waffles with plenty of real maple syrup"));
        waffles.setAttribute("calories", "500");
        waffles.setAttribute("price", "5.95");

        final Element skillet = d.createElement("item");
        waffles.setAttribute("name", "skillet");
        root.appendChild(skillet);
        final Element skilletDescription = d.createElement("description");
        skillet.appendChild(skilletDescription);
        skilletDescription.appendChild(d.createTextNode("A delicious skillet of eggs and vegetables"));
        skillet.setAttribute("calories", "600");
        skillet.setAttribute("price", "6.95");

        return d;
    }

    @Test
    public void testSimple() {
        final Document d = createTestDocument();
        final String serialized = XMLUtils.serialize(d, false, "xml");
        final Document parsed = XMLUtils.parse(serialized);
        final List<Element> nodes1 = XMLUtils.getElementsByTagName(d.getDocumentElement(), "item").collect(Collectors.toList());
        final List<Element> nodes2 =
                XMLUtils.getElementsByTagName(parsed.getDocumentElement(), "item").collect(Collectors.toList());
        assertEquals(nodes1.size(), nodes2.size());
    }

}
