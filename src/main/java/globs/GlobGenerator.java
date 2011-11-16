package globs;
import bean.Util;
import java.io.File;
import javax.xml.transform.TransformerConfigurationException;
import kernel.DomUtil;
import kernel.OneFileByEntityGenerator;
import org.w3c.dom.Node;
/**
 *
 */
public class GlobGenerator extends OneFileByEntityGenerator {
    public GlobGenerator() throws TransformerConfigurationException {
        super("glob", "Glob.xsl", GeneratorType.JAVA);
    }


    @Override
    protected File determineOutputFile(Node entityNode, String rootDir) {
        String fullClassName = globs.Util
              .transformFullClassName(DomUtil.getAttributeValue(entityNode, "name"));
        File root = Util.determineOutputDirectory(rootDir, fullClassName);
        return new File(root, Util.extractClassName(fullClassName) + ".java");
    }
}