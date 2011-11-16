/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package bean;
import java.io.File;
import java.util.StringTokenizer;
import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * Classe responsable de ..
 *
 * @version $Revision: 1.12 $
 */
public final class Util {
    private Util() {
    }


    public static String capitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }


    public static boolean containsIgnoreCase(String stringToLook, String stringToFind) {
        return stringToLook.toUpperCase().contains(stringToFind.toUpperCase());
    }


    public static boolean containsWordIgnoreCase(String wordToLook, String wordToFind) {
        int beginIndex = wordToLook.toUpperCase().indexOf(wordToFind.toUpperCase());
        int endIndex = beginIndex + wordToFind.length();
        if (beginIndex > -1) {
            if ((beginIndex == 0 || wordToLook.charAt(beginIndex - 1) == '\n'
                 || wordToLook.charAt(beginIndex - 1) == ' ')
                && (wordToLook.charAt(endIndex) == ' '
                    || wordToLook.substring(endIndex).startsWith("\n"))) {
                return true;
            }
        }
        return false;
    }


    public static int index(Node current) throws TransformerException {
        NodeList structureProps = XPathAPI.selectNodeList(current, "preceding-sibling::field[@structure]");
        int sum = 0;
        for (int i = 0; i < structureProps.getLength(); i++) {
            Node structureNode = structureProps.item(i);
            sum += XPathAPI.selectNodeList(current,
                                           "//data/structure" + "[@name='"
                                           + getAttributeValue(structureNode, "structure") + "']"
                                           + "/properties/field").getLength();
        }

        NodeList stdProps =
              XPathAPI.selectNodeList(current, "preceding-sibling::field[@type]");

        return stdProps.getLength() + sum;
    }


    public static File determineOutputDirectory(String parentPath, String fullClassName) {
        String path = computePath(parentPath, fullClassName);
        File directory = new File(path);
        kernel.Util.mkdirs(directory);
        return directory;
    }


    public static File determineOutputDirectory(File output, String fullClassName) {
        String path = computePath("", fullClassName);
        File directory = new File(output, path);
        kernel.Util.mkdirs(directory);
        return directory;
    }


    public static String extractPackage(String fullClassName) {
        int idx = fullClassName.lastIndexOf('.');
        if (idx == -1) {
            return "";
        }
        return fullClassName.substring(0, idx);
    }


    public static String extractClassName(String fullClassName) {
        int idx = fullClassName.lastIndexOf('.');
        if (idx == -1) {
            return fullClassName;
        }
        return fullClassName.substring(idx + 1);
    }


    private static String computePath(String parentPath, String fullClassName) {
        StringBuffer buffer = new StringBuffer(parentPath);
        String packageName = extractPackage(fullClassName);
        StringTokenizer tokenizer = new StringTokenizer(packageName, ".");
        while (tokenizer.hasMoreTokens()) {
            buffer.append("/").append(tokenizer.nextToken());
        }
        return buffer.toString();
    }


    private static String getAttributeValue(Node node, String attribute) {
        return node.getAttributes().getNamedItem(attribute).getNodeValue();
    }


    public static String removeNewLineAndTrim(String source) {
        return source.replace('\n', ' ').trim();
    }


    public static String capitalizeFirstLetter(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }


    public static String generateFormatString(String precision) {
        StringBuffer format = new StringBuffer("#,##0");
        if (precision != null && precision.matches("\\d+,\\d+")) {
            format.append(".");
            int digits = Integer.parseInt(precision.split(",")[1]);
            for (int i = 0; i < digits; i++) {
                format.append("0");
            }
        }
        return format.toString();
    }
}
