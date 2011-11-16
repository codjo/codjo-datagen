/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package sql;
import javax.xml.transform.TransformerConfigurationException;
import kernel.Generator;
import kernel.Generator.GeneratorType;
import kernel.GeneratorTestCase;
import org.junit.Test;
/**
 * Classe de test de {@link ViewGenerator}.
 */
public class ViewGeneratorTest extends GeneratorTestCase {

    @Test
    public void test_allowIdeaRun() throws Exception {
    }


    @Override
    protected Generator createGenerator() throws TransformerConfigurationException {
        return new ViewGenerator();
    }


    @Override
    protected GeneratorType getGeneratorType() {
        return GeneratorType.SQL;
    }


    @Override
    protected String getGeneratedFilePath() {
        return "VU_PTF_IN_DIVIDEND.sql";
    }


    @Override
    protected String getInputFilePath() {
        return "src/test/resources/sql/SQLTableTest.xml";
    }
}
