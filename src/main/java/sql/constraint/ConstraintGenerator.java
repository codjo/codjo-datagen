/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package sql.constraint;
import java.io.File;
import javax.xml.transform.TransformerConfigurationException;
import kernel.DomUtil;
import kernel.OneFileByEntityGenerator;
import org.w3c.dom.Node;
/**
 * Generateur pour le feature sql-index.
 */
public class ConstraintGenerator extends OneFileByEntityGenerator {
    public ConstraintGenerator() throws TransformerConfigurationException {
        super("sql-constraint", "Constraint.xsl", GeneratorType.SQL);
    }


    @Override
    protected File determineOutputFile(Node entityNode, String rootDir) {
        String tableName = DomUtil.getAttributeValue(entityNode, "table");
        return new File(rootDir, tableName + ".sql");
    }
}
