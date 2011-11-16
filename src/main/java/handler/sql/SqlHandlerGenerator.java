/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package handler.sql;
import javax.xml.transform.TransformerConfigurationException;
import kernel.OneFileByFeatureGenerator;
/**
 * Generateur de handler sql.
 */
public class SqlHandlerGenerator extends OneFileByFeatureGenerator {
    public SqlHandlerGenerator() throws TransformerConfigurationException {
        super("handler-sql", "SqlHandler.xsl", "Handler.java", GeneratorType.JAVA);
    }
}
