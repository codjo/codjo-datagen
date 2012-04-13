/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package kernel;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import junit.framework.TestCase;
import net.codjo.test.common.XmlUtil;
import net.codjo.util.file.FileUtil;
import org.xml.sax.SAXException;
/**
 * Classe de test de la phase de pre-generation.
 */
public class PreGeneratorTest extends TestCase {

    public void test_generationWithOrderClause() throws Exception {
        assertTransform("src/test/resources/kernel/EtalonWithOrderClause.xml",
                        "src/test/resources/kernel/ConfigurationWithOrderClauseFileTest.xml");
    }


    public void test_generation() throws Exception {
        assertTransform("src/test/resources/kernel/Etalon.xml",
                        "src/test/resources/kernel/ConfigurationFileTest.xml");
    }


    public void test_generation_noReferentialHandler() throws Exception {
        assertTransform("src/test/resources/kernel/Etalon_NoHandlerForReferential.xml",
                        "src/test/resources/kernel/ConfigurationFileTest_NoHandlerForReferential.xml");
    }


    public void test_generationExtended() throws Exception {
        assertTransform("src/test/resources/kernel/extendsEtalon.xml",
                        "src/test/resources/kernel/extendsTest.xml");
    }


    public void test_appendToTrigger() throws Exception {
        assertTransform("src/test/resources/kernel/appendToTriggerEtalon.xml",
                        "src/test/resources/kernel/appendToTriggerTest.xml");
    }


    private void assertTransform(String expectedFile, String sourceFile)
          throws IOException, ParserConfigurationException, SAXException,
                 TransformerException {
        DOMSource source = toDataSource(sourceFile);
        Transformer transformer = toTransformer("PreGenerator.xsl");

        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        XmlUtil.assertEquals(loadEtalon(expectedFile).replaceAll("\\s+", " "),
                             writer.toString().replaceAll("\\s+", " "));
    }


    private String loadEtalon(String expectedResult) throws IOException {
        return FileUtil.loadContent(new File(expectedResult));
    }


    private DOMSource toDataSource(final String dataFileName)
          throws IOException, ParserConfigurationException, SAXException {
        return kernel.DomUtil.toDataSource(dataFileName);
    }


    private Transformer toTransformer(final String name) throws TransformerConfigurationException {
        return kernel.DomUtil.toTransformer(PreGeneratorTest.class.getResourceAsStream(name));
    }
}
