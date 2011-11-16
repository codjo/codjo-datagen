package trigger;
import java.io.File;
import javax.xml.transform.TransformerConfigurationException;
import kernel.DomUtil;
import kernel.OneFileByEntityGenerator;
import org.w3c.dom.Node;
/**
 * Generateur pour le feature trigger-delete.
 */
public class TriggerInsertGenerator extends OneFileByEntityGenerator {
    public TriggerInsertGenerator() throws TransformerConfigurationException {
        super("trigger-insert", "TriggerInsert.xsl", GeneratorType.SQL);
    }


    @Override
    protected File determineOutputFile(Node entityNode, String rootDir) {
        String tableName = DomUtil.getAttributeValue(entityNode, "table");
        return new File(rootDir, "TR_" + tableName + "_I.txt");
    }
}
