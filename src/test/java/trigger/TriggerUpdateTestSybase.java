package trigger;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import junit.framework.TestCase;
import org.xml.sax.SAXException;
public class TriggerUpdateTestSybase extends TestCase {
    public void test_generation() throws Exception {
        DOMSource source = toDataSource("src/test/resources/trigger/TriggerUpdateTest.xml");
        Transformer transformer = toTransformer("TriggerUpdate.xsl");

        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);

        kernel.Util.compare(flatten(loadEtalon()), flatten(writer.toString()));
    }


    private String flatten(String str) {
        return kernel.Util.flatten(str);
    }


    private String loadEtalon() throws Exception {
        return FileUtil.loadContent(new File("src/test/resources/trigger/TR_DATAGEN_FILES_U_sybase.txt"));
    }


    private DOMSource toDataSource(final String dataFileName)
          throws IOException, ParserConfigurationException, SAXException {
        return kernel.DomUtil.toDataSource(dataFileName);
    }


    private Transformer toTransformer(final String name)
          throws TransformerConfigurationException {
        return kernel.DomUtil.toTransformer(TriggerUpdateTestSybase.class.getResourceAsStream(
              name));
    }
}
