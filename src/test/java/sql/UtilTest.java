/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package sql;
import junit.framework.TestCase;
/**
 * DOCUMENT ME!
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.7 $
 */
public class UtilTest extends TestCase {
    public void test_toSqlName() throws Exception {
        assertEquals("PIMS", Util.toSqlName("pims"));
        assertEquals("PIMS_CODE", Util.toSqlName("pimsCode"));
        assertEquals("EXT_VAL_VL", Util.toSqlName("extValVl"));
    }


    public void test_troncate() {
        assertEquals("PIMS", Util.troncate("PIMS"));
        assertEquals("MY_VERRY_LONG_TABLE_NAME_TO_B",
                     Util.troncate("MY_VERRY_LONG_TABLE_NAME_TO_B")); // 29
        assertEquals("MY_VERRY_LONG_TABLE_NAME_TO_BE",
                     Util.troncate("MY_VERRY_LONG_TABLE_NAME_TO_BE")); // 30
        assertEquals("MY_VERRY_LONG_TABLE_NAME_TO_BE",
                     Util.troncate("MY_VERRY_LONG_TABLE_NAME_TO_BE_")); // more than 30
    }


    public void test_toSqlName_tooLong() throws Exception {
        assertEquals("PIMS_CODE0123456789012345678", Util.toSqlName("pimsCode0123456789012345678"));

        try {
            Util.toSqlName("pimsCode01234567890123456780");
            fail("Le nom est trop long ! Il faut au maximum 28 caractères");
        }
        catch (Exception ex) {
        }
    }


    public void test_toSqlName_allreadyInSQLSynntax() throws Exception {
        assertEquals("ID_CHARACTERISTIC", Util.toSqlName("ID_CHARACTERISTIC"));
        assertEquals("ID_1_CHARACTERISTIC", Util.toSqlName("ID_1_CHARACTERISTIC"));
    }


    public void test_tomultipleKeySqlName() throws Exception {
        assertEquals("PIMS,RED", Util.toSqlName("pims,red"));
        assertEquals("PIMS_CODE", Util.toSqlName("pimsCode"));
        assertEquals("EXT_VAL_VL,EXT_VAL_ERIE", Util.toSqlName("extValVl,extValErie"));
    }


    public void test_multipleKeytoSqlName() throws Exception {
        assertEquals("PIMS_CODE,PIMS_CODE,EXT_VAL_VL,EXT_VAL_ERIE",
                     Util.multipleKeytoSqlName("pimsCode,pimsCode,extValVl,extValErie"));
    }
}
