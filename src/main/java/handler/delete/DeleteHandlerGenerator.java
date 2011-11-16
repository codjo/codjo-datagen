/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package handler.delete;
import bean.Util;
import java.io.File;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import kernel.OneFileByFeatureGenerator;
import org.w3c.dom.Node;
/**
 * Generateur de 'handler-delete'.
 */
public class DeleteHandlerGenerator extends OneFileByFeatureGenerator {
    public DeleteHandlerGenerator() throws TransformerConfigurationException {
        super("handler-delete", "DeleteHandler.xsl", ".java", GeneratorType.JAVA);
    }


    @Override
    protected File determineOutputFile(String rootDir, String fullEntityClassName,
                                       Node featureNode, String featureId) throws TransformerException {
        File root = Util.determineOutputDirectory(rootDir, fullEntityClassName);
        return new File(root, kernel.Util.handlerClassName(featureId) + getFilePrefix());
    }
}
