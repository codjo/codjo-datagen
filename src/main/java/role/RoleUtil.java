package role;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import javax.xml.transform.TransformerException;
import kernel.DomUtil;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 *
 */
public class RoleUtil {

    private RoleUtil() {
    }


    public static String toInclude(Node root, String xpath, String patternStr) throws TransformerException {
        Pattern pattern = new Pattern(patternStr);

        NodeList entityList = XPathAPI.selectNodeList(root, xpath + "/feature/*[starts-with(name(.), 'handler')]");

        List<String> includes = new ArrayList<String>();
        for (int i = 0; i < entityList.getLength(); i++) {
            Node handlerNode = entityList.item(i);

            String id = DomUtil.getAttributeValue(handlerNode, "id").trim();

            if (pattern.match(id)) {
                includes.add(id);
            }
        }

        return toXmlFragment(includes);
    }


    private static String toXmlFragment(List<String> includes) {
        StringBuilder result = new StringBuilder();
        for (String include : includes) {
            result.append("<include>").append(include).append("</include>");
        }
        return result.toString();
    }


    private static class Pattern implements Serializable {
        private java.util.regex.Pattern pattern;


        Pattern(String pattern) {
            try {

                this.pattern = java.util.regex.Pattern.compile(encode(pattern));
            }
            catch (Throwable ex) {
                throw new IllegalArgumentException("pattern invalide " + pattern, ex);
            }
        }


        public boolean match(String function) {
            Matcher matcher = pattern.matcher(function);
            return matcher.matches();
        }


        private String encode(String function) {
            StringBuffer sb = new StringBuffer("^");
            for (int i = 0; i < function.length(); i++) {
                char ch = function.charAt(i);
                if (ch == '*') {
                    sb.append('.');
                }
                sb.append(ch);
            }
            sb.append('$');
            return sb.toString();
        }
    }
}
