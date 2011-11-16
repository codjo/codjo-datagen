/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package handler.requetor;
import java.io.File;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import kernel.OneFileByFeatureGenerator;
import org.w3c.dom.Node;
/**
 * Generateur de 'handler-requetor'.
 */
public class RequetorHandlerGenerator extends OneFileByFeatureGenerator {

    public RequetorHandlerGenerator() throws TransformerConfigurationException {
        super("handler-requetor", "RequetorHandler.xsl", ".java", GeneratorType.JAVA);
    }


    @Override
    protected File determineOutputFile(String rootDir,
                                       String fullEntityClassName,
                                       Node featureNode,
                                       String featureId) throws TransformerException {
        File root = bean.Util.determineOutputDirectory(rootDir, fullEntityClassName);
        return new File(root, kernel.Util.handlerClassName(featureId) + getFilePrefix());
    }
}
