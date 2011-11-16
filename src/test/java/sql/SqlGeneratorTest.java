/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package sql;
import net.codjo.database.common.api.JdbcFixture;
import java.io.File;
import javax.xml.transform.TransformerConfigurationException;
import kernel.Generator;
import kernel.Generator.GeneratorType;
import kernel.GeneratorTestCase;
import org.junit.After;
import org.junit.Test;

import static net.codjo.database.common.api.structure.SqlTable.table;
import static org.junit.Assert.assertTrue;
/**
 * Classe de test de {@link SqlGenerator}.
 */
public class SqlGeneratorTest extends GeneratorTestCase {

    @After
    public void deleteGapTableAtEnd() {
        toFile("AP_DIVIDEND-gap.sql").delete();
    }


    @Override
    protected Generator createGenerator() throws TransformerConfigurationException {
        return new SqlGenerator();
    }


    @Override
    protected GeneratorType getGeneratorType() {
        return GeneratorType.SQL;
    }


    @Override
    protected String getGeneratedFilePath() {
        return "AP_DIVIDEND.tab";
    }


    @Override
    protected String getInputFilePath() {
        return "src/test/resources/sql/SQLTableTest.xml";
    }


    @Override
    @Test
    public void test_generate() throws Exception {
        File tableApDividend = toFile("AP_DIVIDEND.tab");
        File gapApDividend = toFile("AP_DIVIDEND-gap.sql");

        tableApDividend.delete();
        gapApDividend.delete();

        super.test_generate();

        assertTrue("Définition de la table Dividend existe", tableApDividend.exists());
        assertTrue("Définition du gap Dividend existe", gapApDividend.exists());
    }


    @Test
    public void test_scriptExecution() throws Exception {
        generate();

        JdbcFixture jdbcFixture = databaseFactory.createJdbcFixture();
        jdbcFixture.doSetUp();
        jdbcFixture.advanced().dropAllObjects();
        try {
            executeScript("AP_DIVIDEND.tab");
            executeScript("AP_DIVIDEND-gap.sql");
            jdbcFixture.advanced().assertExists(table("AP_DIVIDEND"));
        }
        finally {
            jdbcFixture.drop(table("AP_DIVIDEND"));
            jdbcFixture.doTearDown();
        }
    }
}
