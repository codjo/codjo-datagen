/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package handler.requetor;
import javax.xml.transform.TransformerConfigurationException;
import kernel.OneFileGenerator;
/**
 * Generateur d'un fichier xml qui contient les structures des tables.
 */
public class RequetorDefGenerator extends OneFileGenerator {
    public RequetorDefGenerator() throws TransformerConfigurationException {
        super("requetor def", "RequetorDef.xsl", "RequetorDef.xml", GeneratorType.CONFIGURATION);
    }
}
