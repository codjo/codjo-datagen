package handler.command;
import javax.xml.transform.TransformerConfigurationException;
import kernel.BuildException;
import kernel.DomUtil;
import kernel.Generator;
import kernel.Generator.GeneratorType;
import kernel.GeneratorTestCase;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import org.junit.Test;
public class CommandHandlerGeneratorTest extends GeneratorTestCase {

    @Test
    public void test_generate_noMessage() throws Exception {
        getGeneratedFile().delete();

        createGenerator().generate(
              DomUtil.toDocument("src/test/resources/handler/command/NoHandlerCommandTest.xml"), ROOT);

        assertFalse(getGeneratedFile() + " existe", getGeneratedFile().exists());
    }


    @Override
    @Test
    public void test_generate() throws Exception {
        try {
            super.test_generate();
            fail("Les handler commandes ne sont plus générés.");
        }
        catch (BuildException exception) {
            assertEquals(CommandHandlerGenerator.ERROR_MESSAGE, exception.getLocalizedMessage());
        }
    }


    @Override
    @Test
    public void test_generate_twice() throws Exception {
        try {
            super.test_generate();
            fail("Les handler commandes ne sont plus générés.");
        }
        catch (BuildException exception) {
            assertEquals(CommandHandlerGenerator.ERROR_MESSAGE, exception.getLocalizedMessage());
        }
    }


    @Override
    protected Generator createGenerator() throws TransformerConfigurationException {
        return new CommandHandlerGenerator();
    }


    @Override
    protected GeneratorType getGeneratorType() {
        return GeneratorType.JAVA;
    }


    @Override
    protected String getGeneratedFilePath() {
        return "/generated/myCommand.java";
    }


    @Override
    protected String getInputFilePath() {
        return "src/test/resources/handler/HandlerTest.xml";
    }
}
