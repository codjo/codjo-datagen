/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package sql.constraint;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.database.common.api.structure.SqlConstraint;
import net.codjo.database.common.api.structure.SqlField;
import net.codjo.database.common.api.structure.SqlIndex;
import net.codjo.database.common.api.structure.SqlTable;
import javax.xml.transform.TransformerConfigurationException;
import kernel.Generator;
import kernel.Generator.GeneratorType;
import kernel.GeneratorTestCase;
import org.junit.Test;
/**
 * Classe de test de {@link ConstraintGenerator}.
 *
 * @version $Revision: 1.2 $
 */
public class ConstraintGeneratorTest extends GeneratorTestCase {

    @Override
    protected Generator createGenerator() throws TransformerConfigurationException {
        return new ConstraintGenerator();
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
        return "src/test/resources/sql/constraint/ConstraintTest.xml";
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
                                          + "TOTO_DATE varchar(255)");
            jdbcFixture.advanced().create(SqlIndex.uniqueIndex("X1_AP_TOTO", tableToto,
                                                               SqlField.fieldName("PORTFOLIO_CODE"),
                                                               SqlField.fieldName("AUTOMATIC_UPDATE")));
            jdbcFixture.advanced().create(SqlIndex.uniqueIndex("X2_AP_TOTO", tableToto,
                                                               SqlField.fieldName("PORTFOLIO_CODE"),
                                                               SqlField.fieldName("TOTO_DATE")));

            SqlTable tableMereToto = SqlTable.table("AP_MERETOTO");
            jdbcFixture.create(tableMereToto, "ISIN_CODE varchar(255),"
                                              + "AUTOMATIC_UPDATE varchar(255)");
            jdbcFixture.advanced().create(SqlIndex.uniqueIndex("X1_AP_MERETOTO", tableMereToto,
                                                               SqlField.fieldName("ISIN_CODE"),
                                                               SqlField.fieldName("AUTOMATIC_UPDATE")));

            SqlTable tableMereToto2 = SqlTable.table("AP_MERETOTO2");
            jdbcFixture.create(tableMereToto2, "ISIN_CODE varchar(255),"
                                               + "AUTOMATIC_UPDATE varchar(255)");
            jdbcFixture.advanced().create(SqlIndex.uniqueIndex("X1_AP_MERETOTO2", tableMereToto2,
                                                               SqlField.fieldName("ISIN_CODE"),
                                                               SqlField.fieldName("AUTOMATIC_UPDATE")));

            executeScript("AP_TOTO.sql");

            jdbcFixture.advanced().assertExists(SqlConstraint.foreignKey("FK_MERETOTO_TOTO", tableToto));
            jdbcFixture.advanced().assertExists(SqlConstraint.foreignKey("FK_MERETOTO2_TOTO", tableToto));
        }
        finally {
            jdbcFixture.doTearDown();
        }
    }
}
