/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package bean;
import java.io.File;
import javax.xml.transform.TransformerConfigurationException;
import kernel.DomUtil;
import kernel.OneFileByEntityGenerator;
import org.w3c.dom.Node;
/**
 * Generateur pour le feature bean.
 */
public class BeanGenerator extends OneFileByEntityGenerator {
    public BeanGenerator() throws TransformerConfigurationException {
        super("bean", "Bean.xsl", GeneratorType.JAVA);
    }


    @Override
    protected File determineOutputFile(Node entityNode, String rootDir) {
        String fullClassName = DomUtil.getAttributeValue(entityNode, "name");
        File root = Util.determineOutputDirectory(rootDir, fullClassName);
        return new File(root, Util.extractClassName(fullClassName) + ".java");
    }
}
