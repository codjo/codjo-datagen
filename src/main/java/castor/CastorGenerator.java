/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package castor;
import javax.xml.transform.TransformerConfigurationException;
import kernel.OneFileGenerator;
/**
 */
public class CastorGenerator extends OneFileGenerator {
    public CastorGenerator() throws TransformerConfigurationException {
        super("castor", "Mapping.xsl", "Mapping.xml", GeneratorType.CONFIGURATION);
    }
}
