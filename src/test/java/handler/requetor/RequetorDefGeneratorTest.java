/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package handler.requetor;
import javax.xml.transform.TransformerConfigurationException;
import kernel.Generator;
import kernel.Generator.GeneratorType;
import kernel.GeneratorTestCase;
import org.junit.Test;
/**
 * Classe de test de {@link RequetorDefGenerator}.
 */
public class RequetorDefGeneratorTest extends GeneratorTestCase {

    @Test
    public void test_allowIdeaRun() throws Exception {
    }


    @Override
    protected Generator createGenerator() throws TransformerConfigurationException {
        return new RequetorDefGenerator();
    }


    @Override
    protected GeneratorType getGeneratorType() {
        return GeneratorType.CONFIGURATION;
    }


    @Override
    protected String getGeneratedFilePath() {
        return "RequetorDef.xml";
    }


    @Override
    protected String getInputFilePath() {
        return "src/test/resources/handler/requetor/RequetorHandlerTest.xml";
    }
}
