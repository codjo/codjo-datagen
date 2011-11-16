package kernel;
import net.codjo.test.common.fixture.DirectoryFixture;
import java.io.File;
import org.junit.After;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class LastBuildTimestampTest {
    private LastBuildTimestamp lastBuildTimestamp;
    private DirectoryFixture directoryFixture = DirectoryFixture.newTemporaryDirectoryFixture();
    private File generatedFile;


    @Before
    public void setUp() throws Exception {
        directoryFixture.doSetUp();

        generatedFile = new File(directoryFixture, "generated.file");
        assertTrue(generatedFile.createNewFile());
    }


    @After
    public void tearDown() throws Exception {
        directoryFixture.doTearDown();
    }


    @Test
    public void test_isMoreRecent_mustReturnTrue() throws Exception {
        lastBuildTimestamp = new LastBuildTimestamp("/lastBuild_future.timestamp");

        assertTrue(lastBuildTimestamp.isMoreRecent(generatedFile));
    }


    @Test
    public void test_isMoreRecent_mustReturnFalse() throws Exception {
        lastBuildTimestamp = new LastBuildTimestamp("/lastBuild_past.timestamp");

        assertFalse(lastBuildTimestamp.isMoreRecent(generatedFile));
    }
}
