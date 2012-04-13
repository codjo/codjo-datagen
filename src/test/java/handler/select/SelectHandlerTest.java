/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package handler.select;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import junit.framework.TestCase;
import kernel.Util;
import org.xml.sax.SAXException;
/**
 */
public class SelectHandlerTest extends TestCase {

    public void test_generation() throws Exception {
        transformAndCompare("src/test/resources/handler/HandlerTest.xml", null,
                            "src/test/resources/handler/select/SelectDividendHandlerByPk.txt");
    }


    /**
     * Test que la generation d'un handler de type Select Automatise marche .
     */
    public void test_generation_SelectByPK() throws Exception {
        transformAndCompare("src/test/resources/handler/select/SelectByPkHandlerTest.xml", null,
                            "src/test/resources/handler/select/SelectDividendHandlerByPk.txt");
    }


    /**
     * Test que la generation d'un handler de type Select en mode SQL marche.
     */
    public void test_generation_SQLHandler() throws Exception {
        transformAndCompare("src/test/resources/handler/select/SQLSelectHandlerTest.xml",
                            "sqlSelectDividend",
                            "src/test/resources/handler/select/SqlSelectDividendHandler.txt");
    }


    public void test_generation_sqlAllHandler() throws Exception {
        transformAndCompare("src/test/resources/handler/select/SQLSelectHandlerTest.xml",
                            "selectAllDividend",
                            "src/test/resources/handler/select/SqlSelectAllDividendHandler.txt");
    }

   public void test_generation_sqlAllHandlerWithoutOrderClause() throws Exception {
        transformAndCompare("src/test/resources/handler/select/SQLSelectHandlerWithoutOrderClauseTest.xml",
                            "selectAllDividend",
                            "src/test/resources/handler/select/SqlSelectAllDividendHandlerWithoutOrderClause.txt");
    }


    /**
     * Test que la generation d'un handler de type Select Automatise marche qd on surcharge le type.
     */
    public void test_generation_SelectByPK_overrideType() throws Exception {
        String depart = "src/test/resources/handler/select/SelectHandlerTest.xml";
        String expected = "src/test/resources/handler/select/SelectDividendHandlerByPk_overrideType.txt";
        String handlerId = "SelectDividendHandlerByPk_overrideType";
        transformAndCompare(depart, handlerId, expected);
    }


    public void test_generation_method() throws Exception {
        transformAndCompare("src/test/resources/handler/select/SelectHandlerMethodTest.xml",
                            "SelectDividendHandlerByPk",
                            "src/test/resources/handler/select/SelectDividendHandlerByPk_method.txt");
    }


    private void transformAndCompare(String depart, String handlerId, String expected) throws Exception {
        DOMSource source = toDataSource(depart);
        Transformer preTransformer = toTransformer("PreSelectHandler.xsl");
        Transformer transformer = toTransformer("SelectHandler.xsl");

        StringWriter result = new StringWriter();
        preTransformer.transform(source, new StreamResult(result));
        Source intermediate = new StreamSource(new StringReader(result.toString()));
        result = new StringWriter();
        if (handlerId != null) {
            transformer.setParameter("selectHandlerId", handlerId);
        }
        transformer.transform(intermediate, new StreamResult(result));

        Util.compare(flatten(loadEtalon(expected)), flatten(result.toString()));
    }


    private String flatten(String str) {
        return kernel.Util.flatten(str);
    }


    private String loadEtalon(String fileName) throws Exception {
        return FileUtil.loadContent(new File(fileName));
    }


    private DOMSource toDataSource(final String dataFileName)
          throws IOException, ParserConfigurationException, SAXException {
        return kernel.DomUtil.toDataSource(dataFileName);
    }


    private Transformer toTransformer(final String name) throws TransformerConfigurationException {
        return kernel.DomUtil.toTransformer(SelectHandlerAbstractTest.class.getResourceAsStream(name));
    }
}
