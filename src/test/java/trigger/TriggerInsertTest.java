package trigger;
import org.junit.Test;
import sql.XslTestCase;
public class TriggerInsertTest extends XslTestCase {

    @Test
    public void test_allowIdeaRun() throws Exception {
    }


    @Override
    protected String getXmlSource() {
        return "src/test/resources/trigger/TriggerInsertTest.xml";
    }


    @Override
    protected String getXslTransformer() {
        return "TriggerInsert.xsl";
    }


    @Override
    protected String getEtalon() {
        return "src/test/resources/trigger/TR_DATAGEN_FILES_I_etalon.sql";
    }
}
