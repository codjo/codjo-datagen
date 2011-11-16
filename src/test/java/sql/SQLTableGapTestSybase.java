/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
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
/**
 * Classe responsable de ..
 *
 * @version $Revision: 1.6 $
 */
public class SQLTableGapTestSybase extends TestCase {
    private static final String EXPECTED_WITH_PK =
          "/* ========================================================================== */"
          + "/*   Generation Automatique du GAP pour : AP_DIVIDEND   */"
          + "/* ========================================================================== */"
          + "sp_chgattribute 'AP_DIVIDEND', 'identity_gap', 1000"
          + "go"
          + "/* ========================================================================== */"
          + "if exists (select 1 from  sysindexes where  id = object_id('AP_DIVIDEND') and identitygap = 1000)"
          + "    print 'Identity Gap = 1000 created'"
          + "go";


    public void test_generation() throws Exception {
        DOMSource source = toDataSource("src/test/resources/sql/SQLTableTest.xml");
        Transformer transformer = toTransformer("SQLTableGap.xsl");

        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);

        kernel.Util.compare(flatten(EXPECTED_WITH_PK), flatten(writer.toString()));
    }


    private String flatten(String str) {
        return kernel.Util.flatten(str, true);
    }


    private DOMSource toDataSource(final String dataFileName)
          throws IOException, ParserConfigurationException, SAXException {
        return kernel.DomUtil.toDataSource(dataFileName);
    }


    private Transformer toTransformer(final String name)
          throws TransformerConfigurationException {
        return kernel.DomUtil.toTransformer(SQLTableGapTestSybase.class.getResourceAsStream(
              name));
    }
}
