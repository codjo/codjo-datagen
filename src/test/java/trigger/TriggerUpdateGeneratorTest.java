package trigger;
import javax.xml.transform.TransformerConfigurationException;
import kernel.Generator;
import kernel.Generator.GeneratorType;
import kernel.GeneratorTestCase;
import org.junit.Test;
public class TriggerUpdateGeneratorTest extends GeneratorTestCase {

    @Override
    protected Generator createGenerator() throws TransformerConfigurationException {
        return new TriggerUpdateGenerator();
    }


    @Override
    protected GeneratorType getGeneratorType() {
        return GeneratorType.SQL;
    }


    @Override
    protected String getGeneratedFilePath() {
        return "TR_DATAGEN_FILES_U.txt";
    }


    @Override
    protected String getInputFilePath() {
        return "src/test/resources/trigger/TriggerUpdateTest.xml";
    }


    @Test
    public void test_scriptExecution() throws Exception {

    }
}
