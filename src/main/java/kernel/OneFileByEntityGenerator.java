/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package kernel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * Generation d'un fichier par entité.
 *
 * <p> NB : La feuille XSL de generation prend 1 arguments : entityName </p>
 */
public abstract class OneFileByEntityGenerator implements Generator {
    private static final Logger APP = Logger.getLogger(OneFileByEntityGenerator.class);
    private Transformer transformer;
    private String featureTag;
    private GeneratorType type;


    protected OneFileByEntityGenerator(String featureTag, String xslFile, GeneratorType type)
          throws TransformerConfigurationException {
        this.featureTag = featureTag;
        transformer = DomUtil.toTransformer(getClass().getResourceAsStream(xslFile));
        this.type = type;
    }


    public GeneratorType getType() {
        return type;
    }


    public void generate(Document doc, String rootDir)
          throws IOException, TransformerException {
        NodeList entityList =
              DomUtil.selectNodeList(doc, "//data/*[feature/" + featureTag + "]");

        DOMSource source = new DOMSource(doc);

        for (int i = 0; i < entityList.getLength(); i++) {
            Node entityNode = entityList.item(i);

            File dest = determineOutputFile(entityNode, rootDir);
            dest.delete();
            kernel.Util.mkdirs(dest.getParentFile());
            kernel.Util.createNewFile(dest);
            FileWriter writer = new FileWriter(dest);
            try {
                String entityName = DomUtil.getAttributeValue(entityNode, "name");
                log("Generation de " + featureTag + " pour " + entityName);
                log("                               dans " + dest);
                generate(source, writer, entityName);
                log("                               Done ");
            }
            catch (Exception e) {
                throw new RuntimeException("Erreur lors de la génération du script " + dest.getName(), e);
            }
            finally {
                writer.close();
            }
        }
    }


    protected abstract File determineOutputFile(Node entityNode, String rootDir)
          throws TransformerException;


    private void generate(DOMSource source, Writer writer, String entityName)
          throws TransformerException {
        StreamResult result = new StreamResult(writer);
        transformer.setParameter("entityName", entityName);
        transformer.transform(source, result);
    }


    private void log(String txt) {
        APP.debug(txt);
    }
}
