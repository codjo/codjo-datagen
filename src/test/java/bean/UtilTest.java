/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package bean;
import java.io.File;
import java.text.DecimalFormat;
import junit.framework.TestCase;
/**
 * Classe de test de {@link Util}.
 */
public class UtilTest extends TestCase {
    public void test_determineOutputDirectory() throws Exception {
        String rootDir = System.getProperty("user.dir") + "/target";

        File expected = new File(rootDir + "/generated/");

        assertEquals(expected,
                     Util.determineOutputDirectory(rootDir, "generated.MaClass"));
        assertEquals(expected,
                     Util.determineOutputDirectory(new File(rootDir), "generated.MaClass"));
    }


    public void test_extractPackage() throws Exception {
        assertEquals("", Util.extractPackage("MaClass"));
        assertEquals("bobo", Util.extractPackage("bobo.Class"));
        assertEquals("bo.bo", Util.extractPackage("bo.bo.Class"));
    }


    public void test_extractClassName() throws Exception {
        assertEquals("MaClass", Util.extractClassName("MaClass"));
        assertEquals("MaClass", Util.extractClassName("bobo.MaClass"));
        assertEquals("MaClass", Util.extractClassName("bo.bo.MaClass"));
    }


    public void test_containsWordIgnoreCase() throws Exception {
        assertFalse(Util.containsWordIgnoreCase("updateMe", "update"));
        assertTrue(Util.containsWordIgnoreCase("update\nMe", "update"));
        assertFalse(Util.containsWordIgnoreCase("\nupdateMe", "update"));
        assertTrue(Util.containsWordIgnoreCase("\nupdate\nMe", "update"));
        assertTrue(Util.containsWordIgnoreCase(" update\nMe", "update"));
        assertTrue(Util.containsWordIgnoreCase(" update Me", "update"));
        assertTrue(Util.containsWordIgnoreCase("\nupdate Me", "update"));
        assertFalse(Util.containsWordIgnoreCase("\ninsert Me", "update"));
        assertTrue(Util.containsWordIgnoreCase("\nUpdate Me", "update"));
        assertTrue(Util.containsWordIgnoreCase("\nupdate Me", "UPDATE"));
    }


    public void test_removeNewLineAndTrim() throws Exception {
        final String expected = "dev sans test = pas bien !";

        assertEquals(expected, Util.removeNewLineAndTrim("dev\nsans\ntest = pas bien !"));
        assertEquals(expected, Util.removeNewLineAndTrim(expected));
        assertEquals(expected, Util.removeNewLineAndTrim("dev sans test = pas bien !\n       "));
        assertEquals(expected, Util.removeNewLineAndTrim("   dev sans test = pas bien !"));

    }


    public void test_generateFormatString() {
        String result = Util.generateFormatString("10");
        assertEquals("#,##0", result);
        assertEquals("12\u00A0345\u00A0678", new DecimalFormat(result).format(12345678));

        result = Util.generateFormatString("15,3");
        assertEquals("#,##0.000", result);
        assertEquals("1\u00A0234,568", new DecimalFormat(result).format(1234.5678));

        result = Util.generateFormatString(null);
        assertEquals("#,##0", result);

        result = Util.generateFormatString(",");
        assertEquals("#,##0", result);
    }
}
