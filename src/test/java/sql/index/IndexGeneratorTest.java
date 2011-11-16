/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package sql.index;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.database.common.api.structure.SqlIndex;
import net.codjo.database.common.api.structure.SqlTable;
import javax.xml.transform.TransformerConfigurationException;
import kernel.Generator;
import kernel.Generator.GeneratorType;
import kernel.GeneratorTestCase;
import org.junit.Test;
/**
 * Classe de test de {@link IndexGenerator}.
 */
public class IndexGeneratorTest extends GeneratorTestCase {

    @Override
    protected Generator createGenerator() throws TransformerConfigurationException {
        return new IndexGenerator();
    }


    @Override
    protected GeneratorType getGeneratorType() {
        return GeneratorType.SQL;
    }


    @Override
    protected String getGeneratedFilePath() {
        return "AP_TOTO.sql";
    }


    @Override
    protected String getInputFilePath() {
        return "src/test/resources/sql/index/IndexTest.xml";
    }


    @Test
    public void test_scriptExecution() throws Exception {
        generate();

        JdbcFixture jdbcFixture = databaseFactory.createJdbcFixture();
        jdbcFixture.doSetUp();
        try {
            SqlTable tableToto = SqlTable.table("AP_TOTO");
            jdbcFixture.create(tableToto, "PORTFOLIO_CODE varchar(255),"
                                          + "AUTOMATIC_UPDATE varchar(255),"
                                          + "DIVIDEND_DATE varchar(255)");

            executeScript("AP_TOTO.sql");

            jdbcFixture.advanced().assertExists(SqlIndex.index("X1_AP_TOTO", tableToto));
            jdbcFixture.advanced().assertExists(SqlIndex.index("X2_AP_TOTO", tableToto));
            jdbcFixture.advanced().assertExists(SqlIndex.index("X3_AP_TOTO", tableToto));
        }
        finally {
            jdbcFixture.doTearDown();
        }
    }
}
