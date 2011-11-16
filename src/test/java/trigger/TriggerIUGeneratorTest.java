/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package trigger;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.database.common.api.ObjectType;
import net.codjo.database.common.api.structure.SqlTable;
import javax.xml.transform.TransformerConfigurationException;
import kernel.Generator;
import kernel.Generator.GeneratorType;
import kernel.GeneratorTestCase;
import org.junit.Test;
public class TriggerIUGeneratorTest extends GeneratorTestCase {

    @Override
    protected Generator createGenerator() throws TransformerConfigurationException {
        return new TriggerIUGenerator();
    }


    @Override
    protected GeneratorType getGeneratorType() {
        return GeneratorType.SQL;
    }


    @Override
    protected String getGeneratedFilePath() {
        return "TR_DATAGEN_COLUMNS_IU.txt";
    }


    @Override
    protected String getInputFilePath() {
        return "src/test/resources/trigger/TriggerIUTest.xml";
    }


    @Test
    public void test_scriptExecution() throws Exception {
        generate();

        JdbcFixture jdbcFixture = databaseFactory.createJdbcFixture();
        jdbcFixture.doSetUp();
        try {
            SqlTable fatherTable = SqlTable.table("DATAGEN_SECTION");
            jdbcFixture.create(fatherTable, "ID varchar(255)");

            SqlTable childTable = SqlTable.table("DATAGEN_COLUMNS");
            jdbcFixture.create(childTable, "SECTION_ID varchar(255)");

            executeScript(getGeneratedFilePath());

            jdbcFixture.advanced().assertObjectExists("TR_DATAGEN_COLUMNS_IU", ObjectType.TRIGGER);
        }
        finally {
            jdbcFixture.doTearDown();
        }
    }
}
