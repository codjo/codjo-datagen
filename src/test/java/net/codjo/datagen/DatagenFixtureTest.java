/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.datagen;
import java.io.File;
import java.io.IOException;
import net.codjo.test.common.PathUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static net.codjo.datagen.DatagenFixture.DatagenInput.resource;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
/**
 * Classe de test de {@link DatagenFixture}.
 */
public class DatagenFixtureTest {
    private static final String ENTITY_FILE_RELATIVE_PATH
          = "src/test/resources/net/codjo/datagen/DatagenFixtureTest.xml";
    private DatagenFixture datagenFixture;


    @Test
    public void test_constructor() throws Exception {
        DatagenFixture viaResource = new DatagenFixture(DatagenFixtureTest.class, resource("DatagenFixtureTest.xml"));

        assertThat(viaResource.getEntityFile(),
                   is(new File((getClass().getResource("DatagenFixtureTest.xml").toURI())).getAbsoluteFile()));
    }


    @Test
    public void test_getTargetFile() throws Exception {
        File expectedTargetPath = PathUtil.findTargetDirectory(DatagenFixtureTest.class);
        assertEquals(expectedTargetPath, datagenFixture.getTargetFile());
    }


    @Test
    public void test_getEntityFile() throws Exception {
        File expectedEntityPath = determineCurrentEntityPath();
        assertEquals(expectedEntityPath, datagenFixture.getEntityFile().getCanonicalFile());
    }


    @Test
    public void test_getEntityFile_defaultPath() throws Exception {
        File expectedEntityPath = new File(PathUtil.findBaseDirectory(getClass()) + "/src/datagen/datagen.xml");

        assertEquals(expectedEntityPath, new DatagenFixture(getClass()).getEntityFile().getCanonicalFile());
    }


    @Test
    public void test_getSqlPath() throws Exception {
        File expectedSqlDirectory = new File(PathUtil.findTargetDirectory(DatagenFixtureTest.class), "datagen/Sql");
        assertEquals(expectedSqlDirectory, new File(datagenFixture.getSqlPath()));
    }


    @Test
    public void test_getHandlerPath() throws Exception {
        File expectedDirectory = new File(PathUtil.findTargetDirectory(DatagenFixtureTest.class), "datagen/handler");
        assertEquals(expectedDirectory, new File(datagenFixture.getHandlerPath()));
    }


    @Test
    public void test_generate() throws Exception {
        datagenFixture.doSetUp();

        datagenFixture.generate();

        File sqlDirectory = new File(PathUtil.findTargetDirectory(DatagenFixtureTest.class), "datagen/Sql");
        assertTrue(sqlDirectory.exists());
        assertTrue(new File(sqlDirectory, "PM_TEST_TABLE_DEF.tab").exists());

        assertTrue(new File(datagenFixture.getHandlerPath()).exists());
        File oneHandler = datagenFixture.getHandlerFile("com/mycompany/NewTestTableDefHandler.java");
        assertTrue(oneHandler.exists());
    }


    @Before
    public void setUp() throws Exception {
        datagenFixture = new DatagenFixture(DatagenFixtureTest.class, ENTITY_FILE_RELATIVE_PATH);
    }


    @After
    public void tearDown() throws Exception {
        datagenFixture.doTearDown();
    }


    private File determineCurrentEntityPath() throws IOException {
        return new File(PathUtil.findTestDirectory(DatagenFixtureTest.class),
                        "../../../" + ENTITY_FILE_RELATIVE_PATH).getCanonicalFile();
    }
}
