/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package handler.select;
import javax.xml.transform.TransformerConfigurationException;
import kernel.Generator;
import kernel.Generator.GeneratorType;
import kernel.GeneratorTestCase;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
/**
 * Classe de test de {@link SelectHandlerGenerator}
 */
public class SelectHandlerGeneratorTest extends GeneratorTestCase {

    @Override
    protected Generator createGenerator() throws TransformerConfigurationException {
        return new SelectHandlerGenerator();
    }


    @Override
    protected String getGeneratedFilePath() {
        return "generated/SelectDividendHandlerByPkHandler.java";
    }


    @Override
    protected String getInputFilePath() {
        return "src/test/resources/handler/HandlerTest.xml";
    }


    @Override
    protected GeneratorType getGeneratorType() {
        return GeneratorType.JAVA;
    }


    @Override
    @Test
    public void test_generate() throws Exception {
        super.test_generate();
        assertTrue("generated/AbstractSelectDividendHandler.java existe",
                   toFile("generated/AbstractSelectDividendHandler.java").exists());
    }
}
