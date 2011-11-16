package xsd;
import net.codjo.test.common.PathUtil;
import net.codjo.test.common.XmlUtil;
import net.codjo.test.common.fixture.DirectoryFixture;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.io.IOException;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class GenerateTokioXsdsMainTest {
    private DirectoryFixture directoryFixture = DirectoryFixture.newTemporaryDirectoryFixture("xsds");
    private String projectArtifactId = "test-project";
    private String datagenFinalFile =
          PathUtil.find(getClass(), "GenerateTokioXsdsMainTest_final.xml").getPath();
    private String outputDirectory = directoryFixture.getPath();


    @Before
    public void setUp() throws Exception {
        directoryFixture.doSetUp();
    }


    @After
    public void tearDown() throws Exception {
        sql.Util.legacyMode = false;
        directoryFixture.doTearDown();
    }


    @Test
    public void test_nominal() throws Exception {
        GenerateTokioXsdsMain.main(projectArtifactId, datagenFinalFile, outputDirectory);

        assertAllXsds(directoryFixture);
    }


    @Test
    public void test_nominal_legacyMode() throws Exception {

        String customizedFinalFile = replaceColumnName("id", "id_SubGroup");

        GenerateTokioXsdsMain.main(projectArtifactId, customizedFinalFile, outputDirectory, "y");

        String etalon = FileUtil.loadContent(PathUtil.find(getClass(), "cases_etalon.xsd"));
        etalon = changeExpected(etalon, "ID", "ID__SUB_GROUP");
        etalon = changeExpected(etalon, "REF", "REF");
        etalon = changeExpected(etalon, "DATE", "DATE");
        etalon = changeExpected(etalon, "DOUBLE", "DOUBLE");
        etalon = changeExpected(etalon, "BOOLEAN", "BOOLEAN");

        XmlUtil.assertEquals(etalon, loadResult("test-project-cases.xsd"));
    }


    @Test
    public void test_removeDatagenPrefix() throws Exception {
        GenerateTokioXsdsMain.main("test-project-datagen", datagenFinalFile, outputDirectory);

        assertAllXsds(directoryFixture);
    }


    @Test
    public void test_createOutputDirIfNeeded() throws Exception {
        GenerateTokioXsdsMain.main(projectArtifactId,
                                   datagenFinalFile,
                                   new File(directoryFixture, "newDirectory").getPath());

        assertAllXsds(new File(directoryFixture, "newDirectory"));
    }


    @Test
    public void test_generateOnlyIfNeeded() throws Exception {
        GenerateTokioXsdsMain.main(projectArtifactId, datagenFinalFile, outputDirectory);

        File generatedFile = new File(directoryFixture, "test-project-cases.xsd");
        long lastModified = generatedFile.lastModified();

        GenerateTokioXsdsMain.main(projectArtifactId, datagenFinalFile, outputDirectory);

        assertEquals(lastModified, generatedFile.lastModified());
    }


    private void assertAllXsds(File outputDir) throws IOException {
        assertXsd("cases_etalon.xsd", "test-project-cases.xsd", outputDir);
        assertXsd("scenarii_etalon.xsd", "test-project-scenarii.xsd", outputDir);
        assertXsd("story_etalon.xsd", "test-project-story.xsd", outputDir);
        assertXsd("entities_etalon.xsd", "test-project-entities.xsd", outputDir);
    }


    private void assertXsd(String expectedXsdFile, String generatedXsdFile, File outputDir)
          throws IOException {
        File generatedFile = new File(outputDir.getPath(), generatedXsdFile);
        assertTrue(generatedFile.exists());
        XmlUtil.assertEquals(
              FileUtil.loadContent(PathUtil.find(getClass(), expectedXsdFile)),
              FileUtil.loadContent(generatedFile));
    }


    private String loadResult(String child) throws IOException {
        return FileUtil.loadContent(new File(directoryFixture.getPath(), child));
    }


    private String replaceColumnName(String oldColumnName, String newColumnName) throws IOException {
        File customizedFinalFile = new File(directoryFixture, "my-legacy-final.xml");

        String content = FileUtil.loadContent(new File(datagenFinalFile));

        assertTrue("La colonne " + oldColumnName + " doit exister", content.contains(oldColumnName));
        content = content.replaceAll(oldColumnName, newColumnName);

        FileUtil.saveContent(customizedFinalFile, content);
        return customizedFinalFile.getAbsolutePath();
    }


    private String changeExpected(String etalon, String defaultColumn, String legacyColumn) {
        return etalon
              .replaceAll("<xs:element name=\""
                          + defaultColumn
                          + "\" minOccurs=\"0\" type=\"field_attributes\"/>",
                          "<xs:element name=\""
                          + legacyColumn
                          + "\" minOccurs=\"0\" type=\"field_attributes\"/>");
    }
}
