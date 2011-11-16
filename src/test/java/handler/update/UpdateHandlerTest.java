/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package handler.update;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.io.StringWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import junit.framework.TestCase;
import static kernel.Util.compare;
import static kernel.Util.flatten;
/**
 */
public class UpdateHandlerTest extends TestCase {
    public void test_updateWithCastor() throws Exception {
        assertGeneration("UpdateDividendHandler.txt", "src/test/resources/handler/HandlerTest.xml");
    }


    public void test_updateWithSql() throws Exception {
        assertGeneration("UpdateUsingSql.txt", "src/test/resources/handler/update/UpdateUsingSql.xml");
    }


    public void test_updateUsingSqlWizzHistoricAudit() throws Exception {
        assertGeneration("UpdateUsingSqlWithHistoricAudit.txt",
                         "src/test/resources/handler/update/UpdateUsingSqlWithHistoricAudit.xml");
    }


    private void assertGeneration(String expected, String input) throws Exception {
        DOMSource source = toDataSource(input);
        Transformer transformer = toTransformer("UpdateHandler.xsl");

        StringWriter writer = new StringWriter();
        transformer.transform(source, new StreamResult(writer));

        compare(flatten(loadEtalon(expected), true),
                flatten(writer.toString(), true));
    }


    private String loadEtalon(String etalon) throws Exception {
        return FileUtil.loadContent(new File("src/test/resources/handler/update/" + etalon));
    }


    private DOMSource toDataSource(final String dataFileName) throws Exception {
        return kernel.DomUtil.toDataSource(dataFileName);
    }


    private Transformer toTransformer(final String name) throws TransformerConfigurationException {
        return kernel.DomUtil.toTransformer(UpdateHandlerTest.class.getResourceAsStream(name));
    }
}
