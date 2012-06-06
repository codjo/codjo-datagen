/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package castor;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import junit.framework.TestCase;
import kernel.DomUtil;
import net.codjo.test.common.XmlUtil;
import net.codjo.util.file.FileUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
/**
 * DOCUMENT ME!
 *
 * @version $Revision: 1.3 $
 */
public class CastorGeneratorTest extends TestCase {
    private static final String ROOT = "target/generated/";
    private CastorGenerator generator;
    private File mappingFile;
    private Document source;


    public void test_generate() throws Exception {
        assertXmlOutput("src/test/resources/castor/CastorTest.xml", "/castor/MappingEtalon.xml");
    }


    public void test_generateWithSequence() throws Exception {
        assertXmlOutput("src/test/resources/castor/CastorTestWithSequence.xml",
                        "/castor/MappingEtalonWithSequence.xml");
    }


    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    private void assertXmlOutput(String srcFile, String etalon)
          throws IOException, ParserConfigurationException, SAXException, TransformerException {
        mappingFile.delete();

        source = DomUtil.toDocument(srcFile);
        generator.generate(source, ROOT);

        assertTrue(mappingFile + " existe", mappingFile.exists());

        String actual = FileUtil.loadContent(mappingFile);
        String expected = FileUtil.loadContent(new File(getClass().getResource(etalon).getFile()));

        XmlUtil.assertEquivalent(expected, actual);
    }


    public void test_generate_twice() throws Exception {
        generator.generate(source, ROOT);
        long first = mappingFile.lastModified();

        Thread.sleep(500);

        generator.generate(source, ROOT);
        long second = mappingFile.lastModified();
        assertTrue("Les fichiers sont regenere", first < second);
    }


    protected void setUp() throws Exception {
        mappingFile = new File(ROOT, "Mapping.xml");
        generator = new CastorGenerator();
    }


    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    protected void tearDown() {
        mappingFile.delete();
    }
}
