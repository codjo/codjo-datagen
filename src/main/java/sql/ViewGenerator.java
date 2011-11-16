/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package sql;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import kernel.DomUtil;
import kernel.Generator;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 */
public class ViewGenerator implements Generator {
    private static final Logger APP = Logger.getLogger(ViewGenerator.class);


    public ViewGenerator() {
    }


    public GeneratorType getType() {
        return GeneratorType.SQL;
    }


    public void generate(Document doc, String rootDir)
          throws IOException, TransformerException {
        NodeList entityList = DomUtil.selectNodeList(doc, "//data/entity[feature/view]");
        File root = new File(rootDir);
        kernel.Util.mkdirs(root);

        int length = entityList.getLength();
        for (int i = 0; i < length; i++) {
            Node entityNode = entityList.item(i);
            String entityName = DomUtil.getAttributeValue(entityNode, "name");
            generateView(doc, root, entityName);
        }
    }


    private void generateView(final Document doc, final File root, final String entityName)
          throws IOException, TransformerException {
        NodeList viewList =
              DomUtil.selectNodeList(doc, "//data/entity[@name ='" + entityName + "']/feature/view");
        Transformer transformer =
              DomUtil.toTransformer(ViewGenerator.class.getResourceAsStream("View.xsl"));
        int length = viewList.getLength();
        for (int j = 0; j < length; j++) {
            Node viewNode = viewList.item(j);
            String viewName = DomUtil.getAttributeValue(viewNode, "id");
            File dest = new File(root, viewName + ".sql");
            dest.delete();
            kernel.Util.mkdirs(dest.getParentFile());
            FileWriter writer = new FileWriter(dest);
            try {
                log("   Generation de la vue " + viewName + "pour " + entityName);
                log("                               dans " + dest);
                transformer.setParameter("entityName", entityName);
                transformer.setParameter("viewName", viewName);
                transformer.transform(new DOMSource(doc), new StreamResult(writer));
                log("                               Effectué ");
            }
            finally {
                writer.close();
            }
        }
    }


    private void log(String txt) {
        APP.debug(txt);
    }
}
