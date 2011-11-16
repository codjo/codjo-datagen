/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package role;
import javax.xml.transform.TransformerConfigurationException;
import kernel.OneFileGenerator;
/**
 * Generateur d'un fichier xml qui contient les structures des tables.
 */
public class RoleGenerator extends OneFileGenerator {
    public RoleGenerator() throws TransformerConfigurationException {
        super("/data/roles", "Role.xsl", "role.xml", false, GeneratorType.CONFIGURATION);
    }
}
