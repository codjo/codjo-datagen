/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package sql.index;
import java.io.File;
import javax.xml.transform.TransformerConfigurationException;
import kernel.DomUtil;
import kernel.OneFileByEntityGenerator;
import org.w3c.dom.Node;
/**
 * Generateur pour le feature sql-index.
 */
public class IndexGenerator extends OneFileByEntityGenerator {
    public IndexGenerator() throws TransformerConfigurationException {
        super("sql-index", "Index.xsl", GeneratorType.SQL);
    }


    @Override
    protected File determineOutputFile(Node entityNode, String rootDir) {
        String tableName = DomUtil.getAttributeValue(entityNode, "table");
        return new File(rootDir, tableName + ".sql");
    }
}
