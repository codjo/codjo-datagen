package trigger;
import org.junit.Test;
import sql.XslTestCase;
public class TriggerUpdateTest extends XslTestCase {

    @Test
    public void test_allowIdeaRun() throws Exception {
    }


    @Override
    protected String getXmlSource() {
        return "src/test/resources/trigger/TriggerUpdateTest.xml";
    }


    @Override
    protected String getXslTransformer() {
        return "TriggerUpdate.xsl";
    }


    @Override
    protected String getEtalon() {
        return "src/test/resources/trigger/TR_DATAGEN_FILES_U_etalon.sql";
    }
}
