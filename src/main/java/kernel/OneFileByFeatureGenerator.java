/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package kernel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * Generation d'une classe Java par tag feature rencontré.
 *
 * <p> NB :
 *
 * <ul> <li> Le tag doit avoir un attribut id  : contient le futur nom de la classe </li> <li> La feuille XSL
 * de generation prend 2 arguments : entityName et handlerId </li> </ul> </p>
 */
public abstract class OneFileByFeatureGenerator implements Generator {
    private static final Logger LOG = Logger.getLogger(OneFileByFeatureGenerator.class);
    private String featureTag;
    private String filePrefix;
    private String xslFile;
    private GeneratorType type;


    protected OneFileByFeatureGenerator(String featureTag,
                                        String xslFile,
                                        String filePrefix,
                                        GeneratorType type) {
        this.featureTag = featureTag;
        this.filePrefix = filePrefix;
        this.xslFile = xslFile;
        this.type = type;
    }


    public GeneratorType getType() {
        return type;
    }


    public void generate(Document doc, String rootDir)
          throws IOException, TransformerException {
        NodeList entityList =
              DomUtil.selectNodeList(doc, "//data/entity[feature/" + featureTag + "]");

        int length = entityList.getLength();
        for (int i = 0; i < length; i++) {
            Node entityNode = entityList.item(i);
            String fullClassName = DomUtil.getAttributeValue(entityNode, "name");
            generateSelect(doc, rootDir, fullClassName);
        }
    }


    public String getFilePrefix() {
        return filePrefix;
    }


    private void generateSelect(Document doc, final String rootDir,
                                final String fullClassName) throws IOException, TransformerException {
        NodeList selectList =
              DomUtil.selectNodeList(doc,
                                     "//data/entity[@name ='" + fullClassName + "']/feature/" + featureTag);
        Transformer transformer =
              DomUtil.toTransformer(getClass().getResourceAsStream(xslFile));
        DOMSource source = new DOMSource(doc);
        int length = selectList.getLength();
        for (int j = 0; j < length; j++) {
            Node selectNode = selectList.item(j);
            String featureId = DomUtil.getAttributeValue(selectNode, "id");

            File dest =
                  determineOutputFile(rootDir, fullClassName, selectNode, featureId);
            dest.delete();
            kernel.Util.mkdirs(dest.getParentFile());
            kernel.Util.createNewFile(dest);
            FileWriter writer = new FileWriter(dest);
            try {
                LOG.debug("   Generation du " + featureTag + " " + featureId + " pour "
                          + fullClassName);
                LOG.debug("                               dans " + dest);
                transformer.setParameter("entityName", fullClassName);
                transformer.setParameter("handlerId", featureId);
                addParameterToXsl(transformer);
                transformer.transform(source, new StreamResult(writer));
                LOG.debug("                               Effectué ");
            }
            finally {
                writer.close();
            }
        }
    }


    protected void addParameterToXsl(Transformer transformer) {
    }


    protected File determineOutputFile(String rootDir, String fullEntityClassName,
                                       Node featureNode, String featureId) throws TransformerException {
        File root = bean.Util.determineOutputDirectory(rootDir, fullEntityClassName);
        return new File(root, bean.Util.capitalize(featureId) + filePrefix);
    }
}
