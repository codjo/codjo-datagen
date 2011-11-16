package sql;
import java.io.IOException;
import java.io.StringWriter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import junit.framework.TestCase;
import org.xml.sax.SAXException;
public class ViewTestSybase extends TestCase {
    private static final String EXPECTED =
          "/* ========================================================================== */"
          + "/*   Generation Automatique : VU_PTF_IN_DIVIDEND   */"
          + "/* ========================================================================== */"
          + ""
          + "if exists (select 1 from sysobjects where type = 'V' and name = 'VU_PTF_IN_DIVIDEND')"
          + "begin" + "   drop view VU_PTF_IN_DIVIDEND"
          + "   print 'View VU_PTF_IN_DIVIDEND dropped'"
          + "end"
          + "go"
          + ""
          + "create view VU_PTF_IN_DIVIDEND "
          + "as"
          + "   select portfolioCode"
          + "    from AP_DIVIDEND"
          + "    group by portfolioCode "
          + "go"
          + ""
          + "if exists (select 1 from sysobjects where type = 'V' and name = 'VU_PTF_IN_DIVIDEND')"
          + "   print 'View VU_PTF_IN_DIVIDEND created'"
          + "go";


    public void test_generation() throws Exception {
        DOMSource source = toDataSource("src/test/resources/sql/SQLTableTest.xml");
        Transformer transformer = toTransformer("View.xsl");

        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);

        assertEquals(flatten(EXPECTED), flatten(writer.toString()));
    }


    private String flatten(String str) {
        return kernel.Util.flatten(str);
    }


    private DOMSource toDataSource(final String dataFileName)
          throws IOException, ParserConfigurationException, SAXException {
        return kernel.DomUtil.toDataSource(dataFileName);
    }


    private Transformer toTransformer(final String name)
          throws TransformerConfigurationException {
        return kernel.DomUtil.toTransformer(SQLTableTestSybase.class.getResourceAsStream(name));
    }
}
