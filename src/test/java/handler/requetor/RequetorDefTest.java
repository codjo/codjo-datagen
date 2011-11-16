/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package handler.requetor;
import handler.HandlerTestCase;
import org.junit.Test;
/**
 */
public class RequetorDefTest extends HandlerTestCase {

    @Override
    protected String getXslFileName() {
        return "RequetorDef.xsl";
    }


    @Override
    protected String file(String name) {
        return "src/test/resources/handler/requetor/" + name;
    }


    @Test
    public void test_generation() throws Exception {
        assertTransformation(transformer,
                             file("RequetorHandlerTest.xml"),
                             file("RequetorDef.txt"));
    }
}
