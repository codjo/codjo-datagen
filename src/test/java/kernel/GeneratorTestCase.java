/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package kernel;
import java.io.File;
import java.io.IOException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import kernel.Generator.GeneratorType;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.DatabaseHelper;
import net.codjo.database.common.api.ExecSqlScript;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
import static org.junit.Assert.assertTrue;
/**
 * TestCase de {@link Generator}.
 *
 * @version $Revision: 1.2 $
 */
public abstract class GeneratorTestCase {
    protected static final Logger LOGGER = Logger.getLogger(GeneratorTestCase.class);
    protected static final String ROOT = "target/generator-test-result";
    protected final DatabaseFactory databaseFactory = new DatabaseFactory();
    protected final DatabaseHelper databaseHelper = databaseFactory.createDatabaseHelper();
    private Generator generator;
    private Document source;
    private File generatedFile;


    @Before
    public void setUp() throws Exception {
        source = DomUtil.toDocument(getInputFilePath());
        generatedFile = toFile(getGeneratedFilePath());
        generator = createGenerator();
        generatedFile.delete();
    }


    @After
    public void tearDown() {
        generatedFile.delete();
    }


    @Test
    public void test_generate() throws Exception {
        generate();

        assertTrue(generatedFile + " n'existe pas", generatedFile.exists());
    }


    @Test
    public void test_generate_twice() throws Exception {
        generate();
        long first = generatedFile.lastModified();

        Thread.sleep(100);

        generate();
        long second = generatedFile.lastModified();
        assertTrue("Les fichiers sont regenere", first < second);
    }


    @Test
    public void test_type() throws Exception {
        assertThat(generator.getType(), is(getGeneratorType()));
    }


    public File getGeneratedFile() {
        return generatedFile;
    }


    protected abstract String getInputFilePath();


    protected abstract String getGeneratedFilePath();


    protected abstract Generator createGenerator() throws TransformerConfigurationException;


    protected abstract GeneratorType getGeneratorType();


    protected void generate() throws IOException, TransformerException, BuildException {
        generator.generate(source, ROOT);
    }


    protected static File toFile(String fileName) {
        return new File(ROOT, fileName);
    }


    protected ExecSqlScript executeScript(String scriptFileName) {
        File scripFile = toFile(scriptFileName);
        ExecSqlScript execSqlScript = databaseFactory.createExecSqlScript();
        execSqlScript.setConnectionMetadata(databaseHelper.createApplicationConnectionMetadata());
        execSqlScript.setLogger(new MyLogger());
        execSqlScript.execute(scripFile.getParentFile().getAbsolutePath(),
                              scripFile.getName());
        return execSqlScript;
    }


    private static class MyLogger implements ExecSqlScript.Logger {
        public void log(String log) {
            LOGGER.debug(log);
        }
    }
}
