package castor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
/**
 *
 */
public class UtilTest {
    @Test
    public void test_mapCastorKeyGenerator() throws Exception {
        assertEquals("IDENTITY", Util.mapCastorKeyGenerator("IDENTITY"));
        assertEquals("ORACLE_SEQUENCE", Util.mapCastorKeyGenerator("SEQUENCE"));

        assertEquals("EVERYTHING_ELSE", Util.mapCastorKeyGenerator("EVERYTHING_ELSE"));
    }
}
