package trigger;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.database.common.api.ObjectType;
import net.codjo.database.common.api.structure.SqlTable;
import javax.xml.transform.TransformerConfigurationException;
import kernel.Generator;
import kernel.Generator.GeneratorType;
import kernel.GeneratorTestCase;
import org.junit.Test;
public class TriggerInsertGeneratorTest extends GeneratorTestCase {

    @Override
    protected Generator createGenerator() throws TransformerConfigurationException {
        return new TriggerInsertGenerator();
    }


    @Override
    protected GeneratorType getGeneratorType() {
        return GeneratorType.SQL;
    }


    @Override
    protected String getGeneratedFilePath() {
        return "TR_DATAGEN_FILES_I.txt";
    }


    @Override
    protected String getInputFilePath() {
        return "src/test/resources/trigger/TriggerInsertTest.xml";
    }


    @Test
    public void test_scriptExecution() throws Exception {
        generate();

        JdbcFixture jdbcFixture = databaseFactory.createJdbcFixture();
        jdbcFixture.doSetUp();
        try {
            SqlTable table = SqlTable.table("DATAGEN_FILES");
            jdbcFixture.create(table, "ID varchar(255)");

            executeScript(getGeneratedFilePath());

            jdbcFixture.advanced().assertObjectExists("TR_DATAGEN_FILES_I", ObjectType.TRIGGER);
        }
        finally {
            jdbcFixture.doTearDown();
        }
    }
}
