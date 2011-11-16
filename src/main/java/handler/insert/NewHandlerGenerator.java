/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package handler.insert;
import bean.Util;
import java.io.File;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import kernel.OneFileByFeatureGenerator;
import org.w3c.dom.Node;
/**
 * Generateur de la balise 'handler-new'.
 */
public class NewHandlerGenerator extends OneFileByFeatureGenerator {
    public NewHandlerGenerator() throws TransformerConfigurationException {
        super("handler-new", "NewHandler.xsl", ".java", GeneratorType.JAVA);
    }


    @Override
    protected File determineOutputFile(String rootDir,
                                       String fullEntityClassName,
                                       Node featureNode,
                                       String featureId) throws TransformerException {
        File root = Util.determineOutputDirectory(rootDir, fullEntityClassName);
        return new File(root, kernel.Util.handlerClassName(featureId) + getFilePrefix());
    }
}
