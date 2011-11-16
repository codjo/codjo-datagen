/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package bean;
import java.io.File;
import javax.xml.transform.TransformerConfigurationException;
import kernel.Generator;
import kernel.Generator.GeneratorType;
import kernel.GeneratorTestCase;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
/**
 * Test de {@link BeanGenerator}.
 */
public class BeanGeneratorTest extends GeneratorTestCase {
    private File auditJava = toFile("generated/Audit.java");


    @Override
    @Test
    public void test_generate() throws Exception {
        auditJava.delete();
        super.test_generate();
        assertTrue(auditJava + " existe", auditJava.exists());
    }


    @Override
    protected Generator createGenerator() throws TransformerConfigurationException {
        return new BeanGenerator();
    }


    @Override
    protected GeneratorType getGeneratorType() {
        return GeneratorType.JAVA;
    }


    @Override
    protected String getGeneratedFilePath() {
        return "generated/Dividend.java";
    }


    @Override
    protected String getInputFilePath() {
        return "src/test/resources//bean/BeanGeneratorTest.xml";
    }
}
