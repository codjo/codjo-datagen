/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package castor;
import kernel.DomUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * Description of the Class
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.2 $
 */
public final class Util {
    private Util() {}

    public static String toList(NodeList list) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            buffer.append(DomUtil.getAttributeValue(node, "name"));
            if (i + 1 < list.getLength()) {
                buffer.append(" ");
            }
        }
        return buffer.toString();
    }
}
