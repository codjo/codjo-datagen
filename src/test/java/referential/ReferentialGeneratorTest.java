package referential;
import net.codjo.test.common.XmlUtil;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.io.StringWriter;
import javax.xml.transform.dom.DOMSource;
import junit.framework.TestCase;
import kernel.DomUtil;
import org.w3c.dom.Document;
/**
 *
 */
public class ReferentialGeneratorTest extends TestCase {
    private ReferentialGenerator generator;


    public void test_generatePreference() throws Exception {
        DOMSource doc = DomUtil.toDataSource("src/test/resources/referential/referentialTest.xml");
        StringWriter writer = new StringWriter();
        generator.generatePreference(doc, writer);
        String expected
              = FileUtil.loadContent(new File("src/test/resources/referential/preferenceEtalon.xml"));
        String actual = writer.toString();
        XmlUtil.assertEquals(expected, actual);
    }


    public void test_generateReferentialMapping() throws Exception {
        DOMSource doc = DomUtil.toDataSource("src/test/resources/referential/referentialTest.xml");
        StringWriter writer = new StringWriter();
        generator.generateMapping(doc, writer);
        String expected = FileUtil.loadContent(new File("src/test/resources/referential/mappingEtalon.xml"));
        String actual = writer.toString();
        XmlUtil.assertEquals(expected, actual);
    }


    public void test_generateFiles() throws Exception {
        Document doc = DomUtil.toDocument("src/test/resources/referential/referentialTest.xml");
        String destDir = "target/test-dir";
        generator.generate(doc, destDir);
        File prefFile = new File(destDir, "referential-preference.xml");
        File mappFile = new File(destDir, "referential-mapping.xml");

        assertTrue(prefFile.exists());
        assertTrue(mappFile.exists());

        String prefExpected = FileUtil.loadContent(new File(
              "src/test/resources/referential/preferenceEtalon.xml"));
        String mappExpected
              = FileUtil.loadContent(new File("src/test/resources/referential/mappingEtalon.xml"));

        XmlUtil.assertEquals(prefExpected, FileUtil.loadContent(prefFile));
        XmlUtil.assertEquals(mappExpected, FileUtil.loadContent(mappFile));
    }


    @Override
    protected void setUp() throws Exception {
        generator = new ReferentialGenerator();
    }
}
