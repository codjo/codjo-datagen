package globs;
import junit.framework.TestCase;
/**
 *
 */
public class UtilTest extends TestCase {
    public void test_extractPackage() throws Exception {
        assertEquals("net.codjo.icomp.data.globs", Util.extractPackage("net.codjo.icomp.data.Employee"));
    }


    public void test_transformPackage() throws Exception {
        assertEquals("net.codjo.icomp.data.globs.Employee", Util.transformFullClassName("net.codjo.icomp.data.Employee"));
    }
}
