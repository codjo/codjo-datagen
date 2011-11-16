/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package sql.constraint;
import org.junit.Test;
import sql.XslTestCase;
public class ConstraintTest extends XslTestCase {

    @Test
    public void test_allowIdeaRun() throws Exception {
    }


    @Override
    protected String getXmlSource() {
        return "src/test/resources/sql/constraint/ConstraintTest.xml";
    }


    @Override
    protected String getXslTransformer() {
        return "Constraint.xsl";
    }


    @Override
    protected String getEtalon() {
        return "src/test/resources/sql/constraint/AP_TOTO_etalon.sql";
    }
}
