/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.datagen;
import net.codjo.test.common.PathUtil;
import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;
/**
 * Classe de test de {@link DatagenFixture}.
 */
public class DatagenFixtureTest extends TestCase {
    private static final String ENTITY_FILE_RELATIVE_PATH
          = "src/test/resources/net/codjo/datagen/DatagenFixtureTest.xml";
    private DatagenFixture datagenFixture;


    public void test_getTargetFile() throws Exception {
        File expectedTargetPath = PathUtil.findTargetDirectory(DatagenFixtureTest.class);
        assertEquals(expectedTargetPath, datagenFixture.getTargetFile());
    }


    public void test_getEntityFile() throws Exception {
        File expectedEntityPath = determineCurrentEntityPath();
        assertEquals(expectedEntityPath, datagenFixture.getEntityFile().getCanonicalFile());
    }


    public void test_getEntityFile_defaultPath() throws Exception {
        File expectedEntityPath =
              new File(PathUtil.findBaseDirectory(getClass()) + "/src/datagen/datagen.xml");

        assertEquals(expectedEntityPath, new DatagenFixture(getClass()).getEntityFile().getCanonicalFile());
    }


    public void test_getSqlPath() throws Exception {
        File expectedSqlDirectory =
              new File(PathUtil.findTargetDirectory(DatagenFixtureTest.class), "datagen/Sql");
        assertEquals(expectedSqlDirectory, new File(datagenFixture.getSqlPath()));
    }


    public void test_generate() throws Exception {
        datagenFixture.doSetUp();

        datagenFixture.generate();

        File expectedSqlDirectory =
              new File(PathUtil.findTargetDirectory(DatagenFixtureTest.class), "datagen/Sql");
        assertTrue(expectedSqlDirectory.exists());
        assertTrue(new File(expectedSqlDirectory, "PM_TEST_TABLE_DEF.tab").exists());
    }


    @Override
    protected void setUp() throws Exception {
        datagenFixture = new DatagenFixture(DatagenFixtureTest.class, ENTITY_FILE_RELATIVE_PATH);
    }


    @Override
    protected void tearDown() throws Exception {
        datagenFixture.doTearDown();
    }


    private File determineCurrentEntityPath() throws IOException {
        return new File(PathUtil.findTestDirectory(DatagenFixtureTest.class),
                        "../../../" + ENTITY_FILE_RELATIVE_PATH).getCanonicalFile();
    }
}
