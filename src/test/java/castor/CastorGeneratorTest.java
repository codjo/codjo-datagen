/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package castor;
import java.io.File;
import junit.framework.TestCase;
import kernel.DomUtil;
import org.w3c.dom.Document;
/**
 * DOCUMENT ME!
 *
 * @version $Revision: 1.3 $
 */
public class CastorGeneratorTest extends TestCase {
    private static final String ROOT = "target/generated/";
    private CastorGenerator generator;
    private File mappingFile;
    private Document source;

    public void test_generate() throws Exception {
        mappingFile.delete();

        generator.generate(source, ROOT);

        assertTrue(mappingFile + " existe", mappingFile.exists());
    }


    public void test_generate_twice() throws Exception {
        generator.generate(source, ROOT);
        long first = mappingFile.lastModified();

        Thread.sleep(500);

        generator.generate(source, ROOT);
        long second = mappingFile.lastModified();
        assertTrue("Les fichiers sont regenere", first < second);
    }


    protected void setUp() throws Exception {
        source = DomUtil.toDocument("src/test/resources/castor/CastorTest.xml");
        mappingFile = new File(ROOT, "Mapping.xml");
        generator = new CastorGenerator();
    }


    protected void tearDown() {
        mappingFile.delete();
    }
}
