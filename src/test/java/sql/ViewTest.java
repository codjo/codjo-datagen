/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package sql;
import org.junit.Test;
public class ViewTest extends XslTestCase {
    
    @Test
    public void test_allowIdeaRun() throws Exception {
    }


    @Override
    protected String getXmlSource() {
        return "src/test/resources/sql/SQLTableTest.xml";
    }


    @Override
    protected String getXslTransformer() {
        return "View.xsl";
    }


    @Override
    protected String getEtalon() {
        return "src/test/resources/sql/view_etalon.sql";
    }
}
