/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package kernel;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import junit.framework.AssertionFailedError;
import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * Classe utilitaire pour la génération.
 *
 * @author $Author: duclosm $
 * @version $Revision: 1.12 $
 */
public final class Util {
    private static final Logger LOG = Logger.getLogger(Util.class);
    private static final int DISPLAY_LENGTH = 40;


    private Util() {
    }


    public static String capitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }


    public static String handlerClassName(String handlerId) {
        return capitalize(handlerId) + "Handler";
    }


    public static void compare(String expected, String actual) {
        if (expected.equals(actual)) {
            return;
        }
        for (int i = 0; i < expected.length(); i++) {
            if (!actual.startsWith(expected.substring(0, i))) {
                int min = Math.max(0, i - DISPLAY_LENGTH);
                String expectedInfo =
                      "..." + expected.substring(min, Math.min(i + DISPLAY_LENGTH, expected.length()))
                      + "...";
                String actualInfo = "..."
                                    + actual.substring(min, Math.min(i + DISPLAY_LENGTH, actual.length()))
                                    + "...";
                throw new AssertionFailedError("Comparaison\n\texpected = " + expectedInfo + "\n"
                                               + "\tactual   = " + actualInfo);
            }
        }
        throw new AssertionFailedError("Comparaison\n\texpected = " + expected + "\n" + "\tactual   = "
                                       + actual);
    }


    /**
     * Mise à plat de la chaîne de charactère (sans saut de ligne).
     *
     * @return la chaîne applatit
     */
    public static String flatten(String str) {
        return flatten(str, false);
    }


    public static String flatten(String str, boolean skipWhitespace) {
        StringBuilder buffer = new StringBuilder();
        boolean previousWhite = true;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '\r' || ch == '\n') {
                ; // On fait rien
            }
            else if (Character.isWhitespace(ch) || Character.isSpaceChar(ch)) {
                if (!skipWhitespace) {
                    if (!previousWhite) {
                        buffer.append(" ");
                    }
                    previousWhite = true;
                }
            }
            else {
                buffer.append(ch);
                previousWhite = false;
            }
        }

        return buffer.toString();
    }


    public static String toString(Reader reader) throws IOException {
        return toString(reader, true);
    }


    public static String toString(Reader reader, boolean resetReader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        String line = bufferedReader.readLine();
        while (line != null) {
            writer.println(line);
            line = bufferedReader.readLine();
        }
        if (resetReader) {
            reader.reset();
        }
        return stringWriter.toString();
    }


    public static Node document(String fileName)
          throws IOException, ParserConfigurationException, SAXException {
        Document initialDoc = DomUtil.toDocument(fileName);
        Element root = initialDoc.getDocumentElement();
        removeAttributes(root);
        return root;
    }


    private static void removeAttributes(Element root) {
        NamedNodeMap attributes = root.getAttributes();
        if (attributes != null) {
            for (int j = 0; j < attributes.getLength(); j++) {
                String nodeName = attributes.item(j).getNodeName();
                attributes.removeNamedItem(nodeName);
            }
        }
    }


    public static String replaceProperties(String value, Map keys) throws BuildException {
        if (value == null || keys == null) {
            return null;
        }

        List<String> fragments = new ArrayList<String>();
        List<String> propertyRefs = new ArrayList<String>();

        parsePropertyString(value, fragments, propertyRefs);

        StringBuilder result = new StringBuilder();
        Iterator<String> propertyRefsIterator = propertyRefs.iterator();

        for (String fragment : fragments) {
            if (fragment == null) {
                String propertyName = propertyRefsIterator.next();

                Object replacement = keys.get(propertyName);

                if (replacement == null) {
                    if (!isSpecialProperties(propertyName)) {
                        LOG.error("Property ${" + propertyName + "} has not been set");
                    }
                }
                fragment = (replacement != null) ? replacement.toString() : "${" + propertyName + "}";
            }
            result.append(fragment);
        }

        return result.toString();
    }


    static boolean isSpecialProperties(String propertyName) {
        List<String> properties = Arrays.asList("className", "table", "SQLAttributes");
        return properties.contains(propertyName) || propertyName.startsWith("SQLName:");
    }


    public static Node evaluatePath(Node node, String path) throws TransformerException {
        return XPathAPI.selectSingleNode(node, path);
    }


    public static Node parseVariables(Node entityNode, Node handlerNode) throws TransformerException {
        if (handlerNode == null) {
            return null;
        }
        Node parsedNode = handlerNode.cloneNode(true);
        String nodeName = parsedNode.getNodeName();
        if (nodeName != null && nodeName.length() >= 7 && "handler".equals(nodeName.substring(0, 7))) {
            String className =
                  bean.Util.extractClassName(entityNode.getAttributes().getNamedItem("name").getNodeValue());
            String tableName = getAttribute(entityNode, "table");

            Node attributesNode = DomUtil.getNode(parsedNode, "./attributes");
            Node queryNode = DomUtil.getNode(parsedNode, "./query");
            StringBuilder attributesList = new StringBuilder();
            if ((attributesNode != null) &&
                (queryNode != null) &&
                queryNode.getTextContent().contains("${SQLAttributes}")) {
                NodeList attributeNodes = DomUtil.getNodeList(attributesNode, "name");
                for (int i = 0; i < attributeNodes.getLength(); i++) {
                    if (i != 0) {
                        attributesList.append(", ");
                    }
                    String sqlName = sql.Util.toSqlName(attributeNodes.item(i).getTextContent());
                    attributesList.append(sqlName);
                }
            }

            parseVariablesNode(parsedNode, className, tableName, attributesList.toString());
        }
        return parsedNode;
    }


    private static void parseVariablesNode(Node handlerNode,
                                           String className,
                                           String tableName,
                                           String attributesList) throws TransformerException {
        NamedNodeMap nodeAttributes = handlerNode.getAttributes();
        if (nodeAttributes != null) {
            for (int i = 0; i < nodeAttributes.getLength(); i++) {
                Node attribute = nodeAttributes.item(i);
                String value = attribute.getNodeValue();
                value = parseVariablesString(value, className, tableName, attributesList);
                attribute.setNodeValue(value);
            }
        }
        NodeList children = handlerNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String value = child.getNodeValue();
            value = parseVariablesString(value, className, tableName, attributesList);
            child.setNodeValue(value);
            parseVariablesNode(child, className, tableName, attributesList);
        }
    }


    static String parseVariablesString(String value,
                                       String className,
                                       String tableName,
                                       String attributesList) {
        if (value != null) {
            if (className != null) {
                value = value.replaceAll("\\$\\{className}", className);
            }
            if (tableName != null) {
                value = value.replaceAll("\\$\\{table}", tableName);
            }
            if (attributesList != null) {
                value = value.replaceAll("\\$\\{SQLAttributes}", attributesList);
            }
            value = replaceSQLNames(value);
        }
        return value;
    }


    static String replaceSQLNames(String string) {
        Pattern pattern = Pattern.compile("\\$\\{SQLName:([^}]*)}");
        Matcher matcher = pattern.matcher(string);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, sql.Util.toSqlName(matcher.group(1)));
        }
        matcher.appendTail(sb);
        string = sb.toString();
        return string;
    }


    private static void parsePropertyString(String value, List<String> fragments, List<String> propertyRefs)
          throws BuildException {
        int prev = 0;
        int currentPos;

        currentPos = value.indexOf("$", prev);
        while (currentPos >= 0) {
            int nextPos = currentPos + 1;
            if (currentPos > 0) {
                fragments.add(value.substring(prev, currentPos));
            }
            if (currentPos == (value.length() - 1)) {
                fragments.add("$");
                prev = nextPos;
            }
            else if (value.charAt(nextPos) != '{') {
                if (value.charAt(nextPos) == '$') {
                    fragments.add("$");
                    prev = nextPos + 1;
                }
                else {
                    fragments.add(value.substring(currentPos, nextPos + 1));
                    prev = nextPos + 1;
                }
            }
            else {
                int endName = value.indexOf('}', currentPos);
                if (endName < 0) {
                    throw new BuildException("Syntax error in property: " + value);
                }
                String propertyName = value.substring(nextPos + 1, endName);
                fragments.add(null);
                propertyRefs.add(propertyName);
                prev = endName + 1;
            }
            currentPos = value.indexOf("$", prev);
        }
        if (prev < value.length()) {
            fragments.add(value.substring(prev));
        }
    }


    public static String timeMillisToString(long timeMillis) {
        StringBuilder strTime = new StringBuilder();
        long jour = (int)(timeMillis / 86400000);
        long heure = (int)((timeMillis - jour * 86400000) / 3600000);
        long minute = (int)((timeMillis - jour * 86400000 - heure * 3600000) / 60000);
        long seconde = (int)((timeMillis - jour * 86400000 - heure * 3600000 - minute * 60000) / 1000);
        long milliseconde =
              (int)(timeMillis - jour * 86400000 - heure * 3600000 - minute * 60000 - seconde * 1000);

        if (jour > 0) {
            strTime.append(jour).append("j ");
        }
        if (heure > 0) {
            strTime.append(heure).append("h ");
        }
        if (minute > 0) {
            strTime.append(minute).append("mn ");
        }
        if (seconde > 0) {
            strTime.append(seconde).append("s ");
        }
        if (jour + heure + minute + seconde == 0) {
            strTime.append(milliseconde).append("ms");
        }
        return strTime.toString().trim();
    }


    public static String getAttribute(Node node, String attributeName) {
        String value = null;

        Node attNode = node.getAttributes().getNamedItem(attributeName);
        if (attNode != null) {
            value = attNode.getNodeValue();
        }

        return value;
    }


    public static void mkdirs(File file) {
        while (!file.mkdirs()) {
            if (file.exists()) {
                return;
            }
            LOG.info("Re-tentative de creation du repertoire : " + file);
            try {
                Thread.sleep(10);
            }
            catch (InterruptedException e) {
                ;
            }
        }
    }
}
