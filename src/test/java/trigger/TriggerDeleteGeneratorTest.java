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
public class TriggerDeleteGeneratorTest extends GeneratorTestCase {

    @Override
    protected Generator createGenerator() throws TransformerConfigurationException {
        return new TriggerDeleteGenerator();
    }


    @Override
    protected GeneratorType getGeneratorType() {
        return GeneratorType.SQL;
    }


    @Override
    protected String getGeneratedFilePath() {
        return "TR_DATAGEN_FILES_D.txt";
    }


    @Override
    protected String getInputFilePath() {
        return "src/test/resources/trigger/TriggerDeleteTest.xml";
    }


    @Test
    public void test_scriptExecution() throws Exception {
        generate();

        JdbcFixture jdbcFixture = databaseFactory.createJdbcFixture();
        jdbcFixture.doSetUp();
        try {
            SqlTable fatherTable = SqlTable.table("DATAGEN_FILES");
            jdbcFixture.create(fatherTable, "ID varchar(255)");

            SqlTable childTable = SqlTable.table("DATAGEN_FILE_CONTENTS");
            jdbcFixture.create(childTable, "FILE_ID varchar(255)");

            executeScript(getGeneratedFilePath());

            jdbcFixture.advanced().assertObjectExists("TR_DATAGEN_FILES_D", ObjectType.TRIGGER);
        }
        finally {
            jdbcFixture.doTearDown();
        }
    }
}
