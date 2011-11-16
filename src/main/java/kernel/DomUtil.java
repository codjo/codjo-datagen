/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package kernel;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.xpath.XPathAPI;
import org.apache.xml.utils.DefaultErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
/**
 * Classe Utilitaire pour les trucs DOM.
 *
 * @author $Author: torrent $
 * @version $Revision: 1.9 $
 */
public final class DomUtil {
    private DomUtil() {
    }


    public static String getAttributeValue(Node node, String attribute) {
        return node.getAttributes().getNamedItem(attribute).getNodeValue();
    }


    public static String getAttributeValue(Node node, String xpathNode, String attribute)
          throws TransformerException {
        return getAttributeValue(getNode(node, xpathNode), attribute);
    }


    public static Node getNode(Node doc, String xpath) throws TransformerException {
        return XPathAPI.selectSingleNode(doc, xpath);
    }


    public static NodeList getNodeList(Node doc, String xpath) throws TransformerException {
        return XPathAPI.selectNodeList(doc, xpath);
    }


    public static String getNodeValue(Document doc, String xpath) throws TransformerException {
        Node node = XPathAPI.selectSingleNode(doc, xpath);
        Node firstChild;
        try {
            firstChild = node.getFirstChild();
        }
        catch (NullPointerException ex) {
            return null;
        }
        return firstChild.getNodeValue();
    }


    public static boolean hasAttribute(Node node, String attribute) {
        return node.getAttributes().getNamedItem(attribute) != null;
    }


    public static NodeList selectNodeList(Document doc, String xpath) throws TransformerException {
        return XPathAPI.selectNodeList(doc, xpath);
    }


    public static DOMSource toDataSource(final String dataFileName)
          throws IOException, ParserConfigurationException, SAXException {
        return new DOMSource(toDocument(dataFileName));
    }


    public static Document toDocument(final String dataFileName)
          throws IOException, ParserConfigurationException, SAXException {
        File datafile = new java.io.File(dataFileName);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(datafile);
    }


    public static Document toDocument(final Reader reader)
          throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(reader));
    }


    public static Transformer toTransformer(final InputStream stream)
          throws TransformerConfigurationException {
        StreamSource stylesource = new StreamSource(stream);
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setURIResolver(new MyResolver());

        Transformer transformer = factory.newTransformer(stylesource);
        transformer.setErrorListener(new DefaultErrorHandler(true));
        return transformer;
    }


    private static class MyResolver implements URIResolver {
        public Source resolve(String systemId, String ttts)
              throws TransformerException {
            return new StreamSource(DomUtil.class.getResourceAsStream(systemId));
        }
    }
}
