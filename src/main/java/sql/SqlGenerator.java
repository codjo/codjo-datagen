/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package sql;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
public class SqlGenerator implements Generator {
    private static final Logger APP = Logger.getLogger(SqlGenerator.class);


    public GeneratorType getType() {
        return GeneratorType.SQL;
    }


    public void generate(Document doc, String rootDir)
          throws IOException, TransformerException {
        NodeList entityList = DomUtil.selectNodeList(doc, "//data/entity[feature/sql]");

        File root = new File(rootDir);
        kernel.Util.mkdirs(root);

        int length = entityList.getLength();
        for (int i = 0; i < length; i++) {
            Node entityNode = entityList.item(i);
            String entityName = DomUtil.getAttributeValue(entityNode, "name");
            generateSql(doc, root, entityName);
        }
    }


    private void generateSql(final Document doc, final File root, final String entityName)
          throws IOException, TransformerException {
        NodeList sqlNodeList =
              DomUtil.selectNodeList(doc,
                                     "//data/entity[@name='" + entityName + "']/feature/sql");

        int length = sqlNodeList.getLength();
        for (int i = 0; i < length; i++) {
            Node sqlNode = sqlNodeList.item(i);
            generateTableScript(doc, root, entityName, sqlNode);
            generateGapScript(doc, root, entityName, sqlNode);
            generateSequenceScript(doc, root, entityName, sqlNode);
            generateTriggerForSequenceScript(doc, root, entityName, sqlNode);
        }
    }


    private void generateTableScript(final Document doc, final File root,
                                     final String entityName, Node sqlNode)
          throws IOException, TransformerException {
        String tableName = DomUtil.getAttributeValue(sqlNode, "../..", "table");

        String pkGenerator = "yes";
        if (DomUtil.hasAttribute(sqlNode, "pk-generator")) {
            pkGenerator = DomUtil.getAttributeValue(sqlNode, "pk-generator");
        }
        File dest = new File(root, tableName + ".tab");
        dest.delete();
        kernel.Util.mkdirs(dest.getParentFile());
        kernel.Util.createNewFile(dest);
        FileWriter writer = new FileWriter(dest);
        try {
            log("Generation de la definition SQL " + tableName);
            log("                           dans " + dest);
            generateTable(new DOMSource(doc), writer, entityName, tableName, pkGenerator);
            log("                              Done ");
        }
        catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du script de table " + dest.getName(),
                                       e);
        }
        finally {
            writer.close();
        }
    }


    private void generateSequenceScript(final Document doc, final File root,
                                        final String entityName, Node sqlNode)
          throws IOException, TransformerException {
        String tableName = DomUtil.getAttributeValue(sqlNode, "../..", "table");

        NodeList pkGenerator = DomUtil.getNodeList(sqlNode, "../../properties/field/sql/@identity");
        if (pkGenerator.getLength() == 1) {
            File dest = new File(root, tableName + "-sequence.sql");
            dest.delete();
            kernel.Util.mkdirs(dest.getParentFile());
            kernel.Util.createNewFile(dest);
            FileWriter writer = new FileWriter(dest);
            try {
                log("Generation de la definition de la sequence SQL " + tableName);
                log("                           dans " + dest);
                generateSequence(new DOMSource(doc), writer, entityName, tableName);
                log("                              Done ");
            }
            catch (Exception e) {
                throw new RuntimeException("Erreur lors de la génération du script de la sequence " + dest.getName(),
                                           e);
            }
            finally {
                writer.close();
            }
        }
    }

    private void generateTriggerForSequenceScript(Document doc, File root, String entityName, Node sqlNode)
          throws TransformerException, IOException {
        String tableName = DomUtil.getAttributeValue(sqlNode, "../..", "table");

        String pkGenerator = "yes";
        if (DomUtil.hasAttribute(sqlNode, "pk-generator")) {
            pkGenerator = DomUtil.getAttributeValue(sqlNode, "pk-generator");
        }
        File dest = new File(root, "TR_" +tableName+"_SEQ_I.sql");
        dest.delete();
        kernel.Util.mkdirs(dest.getParentFile());
        kernel.Util.createNewFile(dest);
        FileWriter writer = new FileWriter(dest);
        try {
            log("Generation de la definition SQL " + tableName);
            log("                           dans " + dest);
            generateTriggerForSequence(new DOMSource(doc), writer, entityName, tableName, pkGenerator);
            log("                              Done ");
        }
        catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du script de table " + dest.getName(),
                                       e);
        }
        finally {
            writer.close();
        }
    }


    private void generateGapScript(final Document doc, final File root,
                                   final String entityName, Node sqlNode)
          throws IOException, TransformerException {
        if (!DomUtil.hasAttribute(sqlNode, "gap")) {
            return;
        }
        String tableName = DomUtil.getAttributeValue(sqlNode, "../..", "table");

        File dest = new File(root, tableName + "-gap.sql");
        dest.delete();
        kernel.Util.createNewFile(dest);
        FileWriter writer = new FileWriter(dest);
        try {
            log("Generation de la definition GAP " + tableName);
            log("                           dans " + dest);
            generateGap(new DOMSource(doc), writer, entityName, tableName);
            log("                              Done ");
        }
        catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du script de gap " + dest.getName(), e);
        }
        finally {
            writer.close();
        }
    }


    private void generateTriggerForSequence(DOMSource source, FileWriter writer, String entityName, String tableName,
                                            String pkGenerator) throws TransformerException {
        StreamResult result = new StreamResult(writer);
        Transformer sequenceXsl =
              DomUtil.toTransformer(SqlGenerator.class.getResourceAsStream("SQLTableTriggerForSequence.xsl"));
        sequenceXsl.setParameter("entityName", entityName);
        sequenceXsl.setParameter("tableName", tableName);
        sequenceXsl.transform(source, result);
    }


    private void generateSequence(DOMSource source, FileWriter writer, String entityName, String tableName
    ) throws TransformerException {
        StreamResult result = new StreamResult(writer);
        Transformer sequenceXsl =
              DomUtil.toTransformer(SqlGenerator.class.getResourceAsStream("SQLTableSequence.xsl"));
        sequenceXsl.setParameter("entityName", entityName);
        sequenceXsl.setParameter("tableName", tableName);
        sequenceXsl.transform(source, result);
    }


    private void generateGap(DOMSource source, Writer writer, String entityName,
                             String tableName) throws TransformerException {
        StreamResult result = new StreamResult(writer);
        Transformer gapXsl =
              DomUtil.toTransformer(SqlGenerator.class.getResourceAsStream("SQLTableGap.xsl"));
        gapXsl.setParameter("entityName", entityName);
        gapXsl.setParameter("tableName", tableName);
        gapXsl.transform(source, result);
    }


    private void generateTable(DOMSource source, Writer writer, String entityName,
                               String tableName, String pkGenerator) throws TransformerException {
        StreamResult result = new StreamResult(writer);
        Transformer sqlXsl =
              DomUtil.toTransformer(SqlGenerator.class.getResourceAsStream("SQLTable.xsl"));
        sqlXsl.setParameter("entityName", entityName);
        sqlXsl.setParameter("tableName", tableName);
        sqlXsl.setParameter("pk-generator", pkGenerator);
        sqlXsl.transform(source, result);
    }


    private void log(String txt) {
        APP.debug(txt);
    }
}
