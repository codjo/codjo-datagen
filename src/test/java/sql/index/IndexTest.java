/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package sql.index;
import org.junit.Test;
import sql.XslTestCase;
public class IndexTest extends XslTestCase {

    @Test
    public void test_allowIdeaRun() throws Exception {
    }


    @Override
    protected String getXmlSource() {
        return "src/test/resources/sql/index/IndexTest.xml";
    }


    @Override
    protected String getXslTransformer() {
        return "Index.xsl";
    }


    @Override
    protected String getEtalon() {
        return "src/test/resources/sql/index/AP_TOTO_etalon.sql";
    }
}
