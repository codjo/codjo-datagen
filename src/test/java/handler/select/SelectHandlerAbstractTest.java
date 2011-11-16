/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package handler.select;
import handler.HandlerTestCase;
import org.junit.Test;
/**
 */
public class SelectHandlerAbstractTest extends HandlerTestCase {
    @Test
    public void test_generation() throws Exception {
        assertTransformation(transformer,
                             file("../HandlerTest.xml"),
                             file("AbstractSelectDividendHandler.txt"));
    }


    /**
     * Regarde la generation lorsqu'il y a incoherence entre le type java et le type SQL. Exemple SQL =
     * timestamp mais java = java.sql.Date.
     */
    @Test
    public void test_generation_withTranslate() throws Exception {
        assertTransformation(transformer,
                             file("SelectHandlerAbstractTest.xml"),
                             file("AbstractSelectDividendHandlerTranslate.txt"));
    }


    @Override
    protected String getXslFileName() {
        return "AbstractSelectHandler.xsl";
    }


    @Override
    protected String file(String name) {
        return "src/test/resources/handler/select/" + name;
    }
}
