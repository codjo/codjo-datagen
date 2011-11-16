/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package handler.archive;
import bean.Util;
import java.io.File;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import kernel.DomUtil;
import kernel.OneFileByEntityGenerator;
import org.w3c.dom.Node;
/**
 * Generateur d'handler 'historisation'.
 */
public class ArchiveHandlerGenerator extends OneFileByEntityGenerator {
    public ArchiveHandlerGenerator() throws TransformerConfigurationException {
        super("handler-archive", "ArchiveHandler.xsl", GeneratorType.JAVA);
    }


    @Override
    protected File determineOutputFile(Node entityNode, String rootDir) throws TransformerException {
        String fullClassName = DomUtil.getAttributeValue(entityNode, "name");
        File root = Util.determineOutputDirectory(rootDir, fullClassName);
        String handlerId = DomUtil.getAttributeValue(entityNode, "feature/handler-archive", "id");

        return new File(root, kernel.Util.handlerClassName(handlerId) + ".java");
    }
}
