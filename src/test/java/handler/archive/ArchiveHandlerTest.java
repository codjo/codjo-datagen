/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package handler.archive;
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
 * Classe de test de la feuille XSL.
 *
 * @version $Revision: 1.5 $
 */
public class ArchiveHandlerTest extends TestCase {
    public void test_generation() throws Exception {
        DOMSource source = toDataSource("src/test/resources/handler/archive/ArchiveHandlerTest.xml");
        Transformer transformer = toTransformer("ArchiveHandler.xsl");

        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);

        kernel.Util.compare(flatten(loadEtalon(
              "src/test/resources/handler/archive/ArchivePortfolioCodificationHandler.txt")),
                            flatten(writer.toString()));
    }


    public void test_generationWithHelper() throws Exception {
        DOMSource source = toDataSource("src/test/resources/handler/archive/ArchiveHandlerTest.xml");
        Transformer transformer = toTransformer("ArchiveHandler.xsl");

        transformer.setParameter("entityName", "generated.CommercialData");

        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);

        kernel.Util.compare(flatten(loadEtalon(
              "src/test/resources/handler/archive/ArchiveCommercialHandler.txt")),
                            flatten(writer.toString()));
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


    private Transformer toTransformer(final String name)
          throws TransformerConfigurationException {
        return kernel.DomUtil.toTransformer(ArchiveHandlerTest.class.getResourceAsStream(
              name));
    }
}
