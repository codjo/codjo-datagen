package handler;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
public class UtilTest {

    @Test
    public void test_convertTypeJavaSqlTypesConstant() throws Exception {
        assertEquals("", Util.convertTypeJavaSqlTypesConstant("java.sql.NotAType"));
        assertEquals("java.sql.Types.DATE",
                     Util.convertTypeJavaSqlTypesConstant("java.sql.Date"));
        assertEquals("java.sql.Types.TIMESTAMP",
                     Util.convertTypeJavaSqlTypesConstant("java.sql.Timestamp"));
    }
}
