/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package handler.delete;
import javax.xml.transform.TransformerConfigurationException;
import kernel.Generator;
import kernel.Generator.GeneratorType;
import kernel.GeneratorTestCase;
import org.junit.Test;
/**
 * Classe de test {@link DeleteHandlerGenerator}.
 *
 * @version $Revision: 1.3 $
 */
public class DeleteHandlerGeneratorTest extends GeneratorTestCase {

    @Test
    public void test_allowIdeaRun() throws Exception {
    }


    @Override
    protected Generator createGenerator() throws TransformerConfigurationException {
        return new DeleteHandlerGenerator();
    }


    @Override
    protected GeneratorType getGeneratorType() {
        return GeneratorType.JAVA;
    }


    @Override
    protected String getGeneratedFilePath() {
        return "/generated/DeleteDividendHandler.java";
    }


    @Override
    protected String getInputFilePath() {
        return "src/test/resources/handler/HandlerTest.xml";
    }
}
