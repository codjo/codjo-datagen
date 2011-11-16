package globs;
import net.codjo.test.common.PathUtil;
import net.codjo.test.common.fixture.DirectoryFixture;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import junit.framework.TestCase;
import kernel.DomUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
/**
 *
 */
public class GlobGeneratorTest extends TestCase {
    private DirectoryFixture fixture =
          new DirectoryFixture(PathUtil.findTargetDirectory(GlobGeneratorTest.class) + "/globs");


    @Override
    protected void setUp() throws Exception {
        fixture.doSetUp();
    }


    @Override
    protected void tearDown() throws Exception {
        fixture.doTearDown();
    }


    public void test_simpleEntity() throws Exception {
        checkGeneration("src/test/resources/globs/SimpleEntity.xml",
                        new String[][]{
                              {computeGeneratedFilePath("net/codjo/test/globs/SimpleEntity.java"),
                               "src/test/resources/globs/SimpleEntity.txt"},
                        });
    }


    public void test_entityWithStructure() throws Exception {
        checkGeneration("src/test/resources/globs/EntityWithStructure.xml",
                        new String[][]{
                              {computeGeneratedFilePath("net/codjo/test/globs/EntityWithStructure.java"),
                               "src/test/resources/globs/EntityWithStructure.txt"},
                        });
    }


    public void test_entityWithSimpleLinks() throws Exception {
        checkGeneration("src/test/resources/globs/EntityWithSimpleLinks.xml",
                        new String[][]{
                              {computeGeneratedFilePath("net/codjo/test/globs/EntityWithSimpleLinks.java"),
                               "src/test/resources/globs/EntityWithSimpleLinks.txt"},
                        });
    }


    public void test_entityWithComplexLinks() throws Exception {
        checkGeneration("src/test/resources/globs/EntityWithComplexLinks.xml",
                        new String[][]{
                              {computeGeneratedFilePath("net/codjo/test/globs/EntityWithComplexLinks.java"),
                               "src/test/resources/globs/EntityWithComplexLinks.txt"},
                        });
    }


    public void test_twoEntities() throws Exception {
        checkGeneration("src/test/resources/globs/TwoEntities.xml",
                        new String[][]{
                              {computeGeneratedFilePath("net/codjo/test/globs/EntityOne.java"),
                               "src/test/resources/globs/EntityOne.txt"},
                              {computeGeneratedFilePath("net/codjo/test/globs/EntityTwo.java"),
                               "src/test/resources/globs/EntityTwo.txt"},
                        });
    }


    public void test_entityWithMissingLinkName() throws Exception {
        try {
            generate("src/test/resources/globs/EntityWithMissingLinkName.xml");
            fail();
        }
        catch (Exception e) {
            assertEquals(
                  "kernel.BuildException: Attribute 'name' must be provided for link 'FK_COUNTRY_DEPARTMENT'.",
                  e.getCause().getMessage());
        }
    }


    private void checkGeneration(String sourceFile, String[][] toBeCompared)
          throws Exception {
        generate(sourceFile);

        for (String[] aToBeCompared : toBeCompared) {
            String generatedFile = aToBeCompared[0];
            String etalon = aToBeCompared[1];
            kernel.Util.compare(flatten(loadEtalon(etalon)),
                                flatten(FileUtil.loadContent(new File(generatedFile))));
        }
    }


    private void generate(String sourceFile)
          throws IOException, ParserConfigurationException, SAXException, TransformerException {
        GlobGenerator generator = new GlobGenerator();
        Document source = DomUtil.toDocument(sourceFile);
        generator.generate(source, fixture.getAbsolutePath());
    }


    private String computeGeneratedFilePath(String filepath) {
        return fixture.getAbsolutePath() + File.separator + filepath;
    }


    private String loadEtalon(String fileName) throws Exception {
        return FileUtil.loadContent(new File(fileName));
    }


    private String flatten(String str) {
        return kernel.Util.flatten(str);
    }
}
