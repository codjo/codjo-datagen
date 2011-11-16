/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package handler.delete;
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
 * Classe de test de DeleteHandler.xsl.
 *
 * @version $Revision: 1.7 $
 */
public class DeleteHandlerTest extends TestCase {
    public void test_generation() throws Exception {
        assertGeneration("src/test/resources/handler/delete/DeleteHandlerTest.xml",
                         "DeleteDividendHandler.txt");
    }


    public void test_generation_NoControl() throws Exception {
        assertGeneration("src/test/resources/handler/delete/DeleteHandlerTest_NoControl.xml",
                         "DeleteDividendHandler_NoControl.txt");
    }


    public void test_generation_use_bean() throws Exception {
        assertGeneration("src/test/resources/handler/delete/DeleteHandlerTest_use-bean.xml",
                         "DeleteDividendHandler_use-bean.txt");
    }


    public void test_generation_query() throws Exception {
        assertGeneration("src/test/resources/handler/delete/DeleteDividendHandler_Query.xml",
                         "DeleteDividendHandler_Query.txt");
    }


    private void assertGeneration(String input, String expected) throws Exception {
        DOMSource source = toDataSource(input);
        Transformer transformer = toTransformer("DeleteHandler.xsl");

        StringWriter writer = new StringWriter();
        transformer.transform(source, new StreamResult(writer));

        kernel.Util.compare(flatten(loadEtalon(expected)),
                            flatten(writer.toString()));
    }


    private String flatten(String str) {
        return kernel.Util.flatten(str, true);
    }


    private String loadEtalon(String etalonName) throws Exception {
        return FileUtil.loadContent(new File("src/test/resources/handler/delete/" + etalonName));
    }


    private DOMSource toDataSource(final String dataFileName)
          throws IOException, ParserConfigurationException, SAXException {
        return kernel.DomUtil.toDataSource(dataFileName);
    }


    private Transformer toTransformer(final String name)
          throws TransformerConfigurationException {
        return kernel.DomUtil.toTransformer(DeleteHandlerTest.class.getResourceAsStream(
              name));
    }
}
