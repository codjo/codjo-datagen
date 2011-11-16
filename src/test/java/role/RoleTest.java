/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package role;
import net.codjo.test.common.XmlUtil;
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
/**
 */
public class RoleTest extends TestCase {
    public void test_generation() throws Exception {
        assertGeneration(loadEtalon("Etalon.txt"), toDataSource("RoleTest.xml"));
    }


    public void test_generate_usingIncludeHandler() throws Exception {
        assertGeneration(loadEtalon("EtalonUsingIncludeHandler.txt"),
                         toDataSource("RoleUsingIncludeHandlerTest.xml"));
    }


    private void assertGeneration(String expected, DOMSource source) throws Exception {
        Transformer transformer = toTransformer("Role.xsl");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);

        XmlUtil.assertEquals(expected, writer.toString());
    }


    private String loadEtalon(String expected) throws Exception {
        return FileUtil.loadContent(new File("src/test/resources/role/" + expected));
    }


    private DOMSource toDataSource(final String dataFileName)
          throws IOException, ParserConfigurationException, SAXException {
        return kernel.DomUtil.toDataSource("src/test/resources/role/" + dataFileName);
    }


    private Transformer toTransformer(final String name)
          throws TransformerConfigurationException {
        return kernel.DomUtil.toTransformer(RoleTest.class.getResourceAsStream(name));
    }
}
