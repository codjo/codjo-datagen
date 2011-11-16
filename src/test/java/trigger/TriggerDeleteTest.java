package trigger;
import org.junit.Test;
import sql.XslTestCase;
public class TriggerDeleteTest extends XslTestCase {

    @Test
    public void test_allowIdeaRun() throws Exception {
    }


    @Override
    protected String getXmlSource() {
        return "src/test/resources/trigger/TriggerDeleteTest.xml";
    }


    @Override
    protected String getXslTransformer() {
        return "TriggerDelete.xsl";
    }


    @Override
    protected String getEtalon() {
        return "src/test/resources/trigger/TR_DATAGEN_FILES_D_etalon.sql";
    }
}
