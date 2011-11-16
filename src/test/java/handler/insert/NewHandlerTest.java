/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package handler.insert;
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
 * Classe de test 'handler-new'.
 */
public class NewHandlerTest extends TestCase {
    public void test_generation() throws Exception {
        assertTransform("src/test/resources/handler/HandlerTest.xml", "newDividendHandler.txt");
    }


    public void test_generationWithControl() throws Exception {
        assertTransform("src/test/resources/handler/insert/NewHandlerWithControlTest.xml",
                        "NewPortfolioWithControlHandler.txt");
    }


    public void test_generationWithStructureAndSqlQuery() throws Exception {
        assertTransform("src/test/resources/handler/insert/NewHandlerWithStructureAndSqlQuery.xml",
                        "newDividendWithAudit.txt");
    }


    private void assertTransform(String input, String etalon) throws Exception {
        DOMSource source = toDataSource(input);
        Transformer transformer = toTransformer("NewHandler.xsl");

        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);

        kernel.Util.compare(flatten(loadEtalon(etalon)), flatten(writer.toString()));
    }


    private String flatten(String str) {
        return kernel.Util.flatten(str, true);
    }


    private String loadEtalon(String etalon) throws Exception {
        return FileUtil.loadContent(new File("src/test/resources/handler/insert/" + etalon));
    }


    private DOMSource toDataSource(final String dataFileName)
          throws IOException, ParserConfigurationException, SAXException {
        return kernel.DomUtil.toDataSource(dataFileName);
    }


    private Transformer toTransformer(final String name)
          throws TransformerConfigurationException {
        return kernel.DomUtil.toTransformer(NewHandlerTest.class.getResourceAsStream(name));
    }
}
