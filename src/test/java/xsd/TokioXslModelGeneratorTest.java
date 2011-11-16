package xsd;
import net.codjo.test.common.PathUtil;
import net.codjo.test.common.XmlUtil;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.io.FileReader;
import kernel.DomUtil;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class TokioXslModelGeneratorTest {
    private static final String TOKIO_XSD_MODEL_XML = "test-tokio-xsd-model.xml";
    private TokioXslModelGenerator generator;


    @Before
    public void setUp() throws Exception {
        generator = new TokioXslModelGenerator(TOKIO_XSD_MODEL_XML);
    }


    @Test
    public void test_generation() throws Exception {
        File targetDirectory = new File(PathUtil.findTargetDirectory(getClass()), "tokio");

        generator.generate(DomUtil.toDocument(new FileReader(PathUtil.find(getClass(), "final.xml"))),
                           targetDirectory.getPath());

        File generatedFile = new File(targetDirectory, TOKIO_XSD_MODEL_XML);
        assertTrue(generatedFile.exists());

        String expected = FileUtil.loadContent(new File(PathUtil.find(getClass(),
                                                                      "tokio-xsd-model_etalon.xml").getPath()));
        String actual = FileUtil.loadContent(generatedFile);

        XmlUtil.assertEquals(expected, actual);
    }
}
