package handler;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
public class UtilTest {

    @Test
    public void test_convertTypeJavaSqlTypesConstant() throws Exception {
        assertEquals("", Util.convertTypeJavaSqlTypesConstant("java.sql.NotAType"));
        assertEquals("java.sql.Types.DATE",
                     Util.convertTypeJavaSqlTypesConstant("java.sql.Date"));
        assertEquals("java.sql.Types.TIMESTAMP",
                     Util.convertTypeJavaSqlTypesConstant("java.sql.Timestamp"));
    }


    @Test
    public void test_buildGetterConstructorParameter() throws Exception {
        assertEquals("1", Util.buildGetterConstructorParameter(1, null));
        assertEquals("2", Util.buildGetterConstructorParameter(2, ""));
        assertEquals("3, java.sql.Types.String", Util.buildGetterConstructorParameter(3, "java.sql.Types.String"));
    }
}
