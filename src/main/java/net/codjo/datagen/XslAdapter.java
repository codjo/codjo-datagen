package net.codjo.datagen;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
/**
 *
 */
public class XslAdapter {
    protected Node createTextNode(String script) throws ParserConfigurationException {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        return document.createTextNode(script);
    }
}
