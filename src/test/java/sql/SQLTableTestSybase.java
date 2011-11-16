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
 * DOCUMENT ME!
 *
 * @author $Author: duclosm $
 * @version $Revision: 1.8 $
 */
public class SQLTableTestSybase extends TestCase {
    private static final String EXPECTED_WITH_PK =
          "/* ========================================================================== */"
          + "/*   Generation Automatique : gen.Dividend   */"
          + "/* ========================================================================== */"
          + ""
          + "if exists (select 1 from  sysobjects where  id = object_id('AP_DIVIDEND') and type = 'U')"
          + "begin"
          + "   drop table AP_DIVIDEND"
          + "   print 'Table AP_DIVIDEND dropped'"
          + "end"
          + "go"
          + "/* ========================================================================== */"
          + "create table AP_DIVIDEND" + "("
          + "   PORTFOLIO_CODE     varchar(6)     not null,"
          + "    NET_DIVIDEND       numeric(17,2)  default 121.00    not null,"
          + "    NUM                numeric(18)    identity   not null,"
          + "    DIVIDEND_DATE      datetime      not null,"
          + "    AUTOMATIC_UPDATE   bit            default 0    not null,"
          + "    STATUS             varchar        not null"
          + "    constraint CKC_STATUS check (STATUS in ('A','B','C')),"
          + "    LENGTH             int            null,"
          + ""
          + "    COMMENT            varchar(6) null,"
          + "    CREATED_BY         varchar(6) null,"
          + "    LAST             int            null"
          + ")"
          + "go"
          + ""
          + "/* ========================================================================== */"
          + "if exists (select 1 from  sysobjects where  id = object_id('AP_DIVIDEND') and type = 'U')"
          + "   print 'Table AP_DIVIDEND created'"
          + "go";


    public void test_generation() throws Exception {
        DOMSource source = toDataSource("src/test/resources/sql/SQLTableTest.xml");
        Transformer transformer = toTransformer("SQLTable.xsl");

        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);

        kernel.Util.compare(flatten(EXPECTED_WITH_PK), flatten(writer.toString()));
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
