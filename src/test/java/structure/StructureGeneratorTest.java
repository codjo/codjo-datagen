/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package structure;
import javax.xml.transform.TransformerConfigurationException;
import kernel.Generator;
import kernel.Generator.GeneratorType;
import kernel.GeneratorTestCase;
import org.junit.Test;
/**
 * Classe de test de {@link StructureGenerator}.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.3 $
 */
public class StructureGeneratorTest extends GeneratorTestCase {

    @Test
    public void test_allowIdeaRun() throws Exception {
    }


    @Override
    protected String getInputFilePath() {
        return "src/test/resources/structure/StructureTest.xml";
    }


    @Override
    protected String getGeneratedFilePath() {
        return "structure.xml";
    }


    @Override
    protected Generator createGenerator() throws TransformerConfigurationException {
        return new StructureGenerator();
    }


    @Override
    protected GeneratorType getGeneratorType() {
        return GeneratorType.CONFIGURATION;
    }
}
