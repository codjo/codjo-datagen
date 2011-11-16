package kernel;
import net.codjo.test.common.fixture.DirectoryFixture;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Properties;
import junit.framework.TestCase;
import kernel.Generator.GeneratorType;
import static kernel.Main.ADD_FIELD_TRANSFORMER_FILENAME;
import static kernel.Main.ADD_HANDLER_TRANSFORMER_FILENAME;
import static kernel.Main.DB_TYPE_SYBASE;
import static kernel.Main.INCLUDE_TRANSFORMER_FILENAME;
import static kernel.Main.PRE_GENERATOR_TRANSFORMER_FILENAME;
import static kernel.Main.getResolvedDatagenPropertyReader;
import static kernel.Main.toGeneratorTypes;

public class MainTest extends TestCase {
    private DirectoryFixture directory = new DirectoryFixture("target/MainTest");


    @Override
    public void setUp() throws Exception {
        directory.doSetUp();
    }


    @Override
    public void tearDown() throws Exception {
        directory.doTearDown();
    }


    public void test_generation() throws Exception {
        Main main = new Main(DB_TYPE_SYBASE);
        main.init(DomUtil.toDocument("src/test/resources/kernel/ConfigurationFileTest.xml"));
        assertPaths(main);
    }


    public void test_generateWithFilenameInput() throws Exception {
        Main main = new Main(DB_TYPE_SYBASE);
        main.setExitOnError(false);
        main.generate("src/test/resources/kernel/ConfigurationFileTest.xml");
        assertTrue(new File("target/MainTest/Sql/table").exists());
        assertPaths(main);
    }


    public void test_generation_onlySql() throws Exception {
        Main main = new Main(DB_TYPE_SYBASE, Arrays.asList(GeneratorType.SQL));
        main.setExitOnError(false);
        main.generate("src/test/resources/kernel/ConfigurationFileTest.xml");
        assertTrue(new File("target/MainTest/Sql/table").exists());
        assertFalse(new File("target/MainTest/HandlerLib/src").exists());
    }


    public void test_toGeneratorType() throws Exception {
        assertEquals(toGeneratorTypes("SQL"), Arrays.asList(GeneratorType.SQL));
    }


    public void test_toGeneratorType_2types() throws Exception {
        assertEquals(toGeneratorTypes("SQL, JAVA"),
                     Arrays.asList(GeneratorType.SQL, GeneratorType.JAVA));
    }


    public void test_toGeneratorType_unknown() throws Exception {
        try {
            toGeneratorTypes("UNKNOWN_TYPE");
            fail();
        }
        catch (IllegalArgumentException ex) {
            ; // Ok
        }
    }


    public void test_toGeneratorType_empty() throws Exception {
        assertEquals(toGeneratorTypes("ALL"), Arrays.asList(GeneratorType.values()));
    }


    public void test_generation_without_Constraint() throws Exception {
        Main main = new Main(DB_TYPE_SYBASE);
        main.init(DomUtil.toDocument("src/test/resources/kernel/ConfigurationFileTest2.xml"));
        assertEquals("../../Sql/table", main.getSqlPath());
        assertEquals("../../Sql/index", main.getSqlIndexPath());
        assertNull(main.getSqlConstraintPath());
        assertEquals("../../Sql/trigger", main.getTriggerPath());
        assertEquals("../../DataLib/src", main.getBeanPath());
        assertEquals("../../HandlerLib/src", main.getHandlersPath());
        assertEquals("../../ServerLib/src/net/codjo/red/util", main.getCastorPath());
        assertEquals("../../HandlerLib/src", main.getCmdHandlerPath());
        assertEquals("../../ClientAPP/src", main.getStructurePath());
        assertEquals("../../ClientAPP/dest", main.getRequetorPath());
    }


    public void test_includeXml() throws Exception {
        Main main = new Main(DB_TYPE_SYBASE);
        Reader result =
              main.transform(new FileReader("src/test/resources/kernel/TestGenDefInclude.xml"),
                             DomUtil.toTransformer(
                                   Main.class.getResourceAsStream(INCLUDE_TRANSFORMER_FILENAME)));
        kernel.Util
              .compare(Util.flatten(FileUtil.loadContent(new File(
                    "src/test/resources/kernel/includeXmlEtalon.xml")), true),
                       Util.flatten(Util.toString(result), true));
    }


    public void test_externalEntityXml() throws Exception {
        Main main = new Main(DB_TYPE_SYBASE);
        Reader result =
              main.transform(new FileReader("src/test/resources/kernel/TestGenDefExternalEntity.xml"),
                             DomUtil.toTransformer(
                                   Main.class.getResourceAsStream(INCLUDE_TRANSFORMER_FILENAME)));
        kernel.Util
              .compare(Util.flatten(FileUtil.loadContent(new File(
                    "src/test/resources/kernel/includeXmlEtalon.xml")), true),
                       Util.flatten(Util.toString(result), true));
    }


    public void test_addFields() throws Exception {
        Main main = new Main(DB_TYPE_SYBASE);
        Reader result =
              main.transform(new FileReader("src/test/resources/kernel/includeXmlEtalon.xml"),
                             DomUtil.toTransformer(
                                   Main.class.getResourceAsStream(ADD_FIELD_TRANSFORMER_FILENAME)));
        kernel.Util
              .compare(Util.flatten(FileUtil.loadContent(new File(
                    "src/test/resources/kernel/addFieldEtalon.xml")), true),
                       Util.flatten(Util.toString(result), true));
    }


    public void test_addHandlers() throws Exception {
        Main main = new Main(DB_TYPE_SYBASE);
        Reader result = main.transform(new FileReader("src/test/resources/kernel/addHandlerTest.xml"),
                                       DomUtil.toTransformer(Main.class.getResourceAsStream(
                                             ADD_HANDLER_TRANSFORMER_FILENAME)));
        kernel.Util
              .compare(Util.flatten(FileUtil.loadContent(new File(
                    "src/test/resources/kernel/addHandlerEtalon.xml")), true),
                       Util.flatten(Util.toString(result), true));
    }


    public void test_appendTriggers() throws Exception {
        Main main = new Main(DB_TYPE_SYBASE);
        Reader result = main.transform(new FileReader("src/test/resources/kernel/appendToTriggerTest.xml"),
                                       DomUtil.toTransformer(Main.class.getResourceAsStream(
                                             PRE_GENERATOR_TRANSFORMER_FILENAME)));
        kernel.Util
              .compare(Util.flatten(FileUtil.loadContent(new File(
                    "src/test/resources/kernel/appendToTriggerEtalon.xml")),
                                    true),
                       Util.flatten(Util.toString(result), true));
    }


    public void test_manageEntities() throws Exception {
        Main main = new Main(DB_TYPE_SYBASE);
        Reader result = main.manageEntities("src/test/resources/kernel/addFieldEtalon.xml");
        kernel.Util.compare(
              Util.flatten(FileUtil.loadContent(new File("src/test/resources/kernel/generatedTestGenDef.xml")),
                           true),
              Util.flatten(Util.toString(result), true));
    }


    public void test_generate_noResetOnFileReader() throws Exception {
        Main main = new Main(DB_TYPE_SYBASE);
        main.generate(new FileReader("src/test/resources/kernel/ConfigurationFileTest.xml"));
    }


    public void test_getResolvedDatagenPropertyReader() throws Exception {
        String source =
              "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
              + "<entities xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
              + "<data> my previous period = ${previousPeriod} </data>"
              + "<data> my next period = ${nextPeriod} </data>" + "</entities>";
        String expected =
              "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
              + "<entities xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
              + "<data> my previous period = '200511' </data>" + "<data> my next period = '200512' </data>"
              + "</entities>";

        Properties properties = new Properties();
        properties.load(new FileInputStream("src/test/resources/" + File.separator + "kernel" + File.separator
                                            + "datagen.properties"));
        StringReader includeReader = new StringReader(source);
        Reader result = getResolvedDatagenPropertyReader(includeReader, properties);

        assertEquals(Util.flatten(expected), Util.toString(result).trim());
    }


    private void assertPaths(Main main) {
        assertEquals("target/MainTest/Sql/table", main.getSqlPath());
        assertEquals("target/MainTest/Sql/view", main.getSqlViewPath());
        assertEquals("target/MainTest/Sql/index", main.getSqlIndexPath());
        assertEquals("target/MainTest/Sql/constraint", main.getSqlConstraintPath());
        assertEquals("target/MainTest/Sql/trigger", main.getTriggerPath());
        assertEquals("target/MainTest/DataLib/src", main.getBeanPath());
        assertEquals("target/MainTest/HandlerLib/src", main.getHandlersPath());
        assertEquals("target/MainTest/ServerLib/src/net/codjo/red/util", main.getCastorPath());
        assertEquals("target/MainTest/HandlerLib/src", main.getCmdHandlerPath());
        assertEquals("target/MainTest/ClientAPP/src", main.getStructurePath());
        assertEquals("target/MainTest/ClientAPP/dest", main.getRequetorPath());
        assertEquals("target/MainTest/ClientAPP/dest/conf", main.getReferentialPath());
    }
}
