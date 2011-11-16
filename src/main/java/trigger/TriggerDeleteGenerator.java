/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package trigger;
import java.io.File;
import javax.xml.transform.TransformerConfigurationException;
import kernel.DomUtil;
import kernel.OneFileByEntityGenerator;
import org.w3c.dom.Node;
/**
 * Generateur pour le feature trigger-delete.
 */
public class TriggerDeleteGenerator extends OneFileByEntityGenerator {
    public TriggerDeleteGenerator() throws TransformerConfigurationException {
        super("trigger-delete", "TriggerDelete.xsl", GeneratorType.SQL);
    }


    @Override
    protected File determineOutputFile(Node entityNode, String rootDir) {
        String tableName = DomUtil.getAttributeValue(entityNode, "table");
        return new File(rootDir, "TR_" + tableName + "_D.txt");
    }
}
