/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package handler.select;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import kernel.DomUtil;
import kernel.Generator;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * Classe de génération des Handler Select
 */
public class SelectHandlerGenerator implements Generator {
    private static final Logger APP = Logger.getLogger(SelectHandlerGenerator.class);


    public GeneratorType getType() {
        return GeneratorType.JAVA;
    }


    public void generate(Document doc, String rootDir)
          throws IOException, TransformerException {
        NodeList entityList = DomUtil.selectNodeList(doc, "//data/entity[feature/handler-select]");

        StringWriter result = new StringWriter();
        Source source = new DOMSource(doc);
        Transformer preSelectTransformer =
              DomUtil.toTransformer(getClass().getResourceAsStream("PreSelectHandler.xsl"));
        preSelectTransformer.transform(source, new StreamResult(result));

        int length = entityList.getLength();
        for (int i = 0; i < length; i++) {
            Node entityNode = entityList.item(i);
            String fullClassName = DomUtil.getAttributeValue(entityNode, "name");
            File root = bean.Util.determineOutputDirectory(rootDir, fullClassName);
            kernel.Util.mkdirs(root);
            generateAbstract(source, root, fullClassName);
            generateSelect(doc, result.toString(), root, fullClassName);
        }
    }


    private void generateAbstract(Source source, final File root,
                                  final String fullClassName) throws IOException, TransformerException {
        String handlerClassName =
              kernel.Util.handlerClassName("AbstractSelect"
                                           + bean.Util.extractClassName(fullClassName));
        File dest = new File(root, handlerClassName + ".java");
        dest.delete();
        kernel.Util.createNewFile(dest);
        FileWriter writer = new FileWriter(dest);
        try {
            log("Generation de la definition du AbstractSelectHandler pour " + fullClassName);
            log("                               dans " + dest);
            Transformer abstractTransformer =
                  DomUtil.toTransformer(getClass().getResourceAsStream("AbstractSelectHandler.xsl"));
            abstractTransformer.setParameter("entityName", fullClassName);
            abstractTransformer.transform(source, new StreamResult(writer));
            log("                               Effectué ");
        }
        finally {
            writer.close();
        }
    }


    private void generateSelect(final Document doc, final String docSource,
                                final File root, final String fullClassName)
          throws IOException, TransformerException {
        NodeList selectList =
              DomUtil.selectNodeList(doc,
                                     "//data/entity[@name ='" + fullClassName + "']/feature/handler-select");
        Transformer selectTransformer =
              DomUtil.toTransformer(getClass().getResourceAsStream("SelectHandler.xsl"));
        int length = selectList.getLength();
        for (int j = 0; j < length; j++) {
            Node selectNode = selectList.item(j);
            String selectName = DomUtil.getAttributeValue(selectNode, "id");

            File dest = new File(root, bean.Util.capitalize(selectName) + "Handler.java");
            dest.delete();
            kernel.Util.createNewFile(dest);
            FileWriter writer = new FileWriter(dest);
            try {
                log("   Generation du SelectHandler " + selectName + "pour " + fullClassName);
                log("                               dans " + dest);
                selectTransformer.setParameter("entityName", fullClassName);
                selectTransformer.setParameter("selectHandlerId", selectName);
                selectTransformer.transform(new StreamSource(new StringReader(docSource)),
                                            new StreamResult(writer));
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
