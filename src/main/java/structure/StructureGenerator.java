/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package structure;
import javax.xml.transform.TransformerConfigurationException;
import kernel.OneFileGenerator;
/**
 * Generateur d'un fichier xml qui contient les structures des tables.
 */
public class StructureGenerator extends OneFileGenerator {
    public StructureGenerator() throws TransformerConfigurationException {
        super("doc-structure", "Structure.xsl", "structure.xml", GeneratorType.CONFIGURATION);
    }
}
