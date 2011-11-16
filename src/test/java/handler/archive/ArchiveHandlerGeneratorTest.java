/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package handler.archive;
import javax.xml.transform.TransformerConfigurationException;
import kernel.Generator;
import kernel.Generator.GeneratorType;
import kernel.GeneratorTestCase;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
/**
 * Classe de test de {@link ArchiveHandlerGenerator}.
 *
 * @version $Revision: 1.5 $
 */
public class ArchiveHandlerGeneratorTest extends GeneratorTestCase {

    @Override
    @Test
    public void test_generate() throws Exception {
        super.test_generate();
        assertTrue(toFile("generated/ArchiveCommercialHandler.java").exists());
    }


    @Override
    protected Generator createGenerator() throws TransformerConfigurationException {
        return new ArchiveHandlerGenerator();
    }


    @Override
    protected GeneratorType getGeneratorType() {
        return GeneratorType.JAVA;
    }


    @Override
    protected String getGeneratedFilePath() {
        return "generated/ArchivePortfolioCodificationHandler.java";
    }


    @Override
    protected String getInputFilePath() {
        return "src/test/resources/handler/archive/ArchiveHandlerTest.xml";
    }
}
