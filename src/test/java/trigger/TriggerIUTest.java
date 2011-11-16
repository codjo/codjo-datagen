package trigger;
import org.junit.Test;
import sql.XslTestCase;
public class TriggerIUTest extends XslTestCase {

    @Test
    public void test_allowIdeaRun() throws Exception {
    }


    @Override
    protected String getXmlSource() {
        return "src/test/resources/trigger/TriggerIUTest.xml";
    }


    @Override
    protected String getXslTransformer() {
        return "TriggerIU.xsl";
    }


    @Override
    protected String getEtalon() {
        return "src/test/resources/trigger/TR_DATAGEN_COLUMNS_IU_etalon.sql";
    }
}
