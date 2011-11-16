package trigger;
import java.io.File;
import javax.xml.transform.TransformerConfigurationException;
import kernel.DomUtil;
import kernel.OneFileByEntityGenerator;
import org.w3c.dom.Node;
/**
 * Generateur pour le feature trigger-delete.
 */
public class TriggerUpdateGenerator extends OneFileByEntityGenerator {
    public TriggerUpdateGenerator() throws TransformerConfigurationException {
        super("trigger-update", "TriggerUpdate.xsl", GeneratorType.SQL);
    }


    @Override
    protected File determineOutputFile(Node entityNode, String rootDir) {
        String tableName = DomUtil.getAttributeValue(entityNode, "table");
        return new File(rootDir, "TR_" + tableName + "_U.txt");
    }
}
