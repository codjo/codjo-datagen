/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.datagen;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import junit.framework.Assert;
import kernel.Main;
import net.codjo.test.common.PathUtil;
import net.codjo.test.common.fixture.DirectoryFixture;
import net.codjo.test.common.fixture.Fixture;
import net.codjo.util.file.FileUtil;
/**
 * Fixture pour l'utilisation de datagen.
 */
public class DatagenFixture implements Fixture {
    private static final String ENTITY_START_TAG = "<entity";
    private static final String ENTITIES_END_TAG = "</entities>";
    private static final String FILE_SEPARATOR = "/";
    private Class testCaseClass;
    private DatagenInput datagenInput;
    private DirectoryFixture datagenDirectory;


    public DatagenFixture(Class testCaseClass) {
        this(testCaseClass, DatagenInput.file("src/datagen/datagen.xml"));
    }


    public DatagenFixture(Class testCaseClass, String entityFileRelativePath) {
        this(testCaseClass, DatagenInput.file(entityFileRelativePath));
    }


    public DatagenFixture(Class testCaseClass, DatagenInput datagenInput) {
        this.testCaseClass = testCaseClass;
        this.datagenInput = datagenInput;
        datagenDirectory = new DirectoryFixture(PathUtil.findTargetDirectory(testCaseClass) + "/datagen");
    }


    public void doSetUp() throws Exception {
        datagenDirectory.doSetUp();
    }


    public void doTearDown() throws Exception {
        datagenDirectory.doTearDown();
    }


    public void generate() throws Exception {
        Main main = new Main(Main.DB_TYPE_SYBASE);

        File datagenFile = buildDatagenFile(datagenInput.entityFileAbsolutePath(testCaseClass));

        main.generate(datagenFile.getAbsolutePath());
    }


    private File buildDatagenFile(String entityFileAbsolutePath) throws IOException {
        File datagenFile = new File(getDatagenDirectory(), "datagen.xml");

        FileWriter writer = new FileWriter(datagenFile);
        try {
            writer.write(getDatagenFileContent(entityFileAbsolutePath));
        }
        finally {
            writer.close();
        }

        return datagenFile;
    }


    private File getDatagenDirectory() {
        return datagenDirectory;
    }


    public String getSqlPath() {
        return getDatagenDirectory() + "/sql";
    }


    public String getHandlerPath() {
        return getDatagenDirectory() + "/handler";
    }


    public File getHandlerFile(String relativePath) {
        return new File(getHandlerPath(), relativePath);
    }


    public File getTargetFile() {
        return new File(determineCurrentTargetPath());
    }


    public File getEntityFile() {
        return new File(datagenInput.entityFileAbsolutePath(testCaseClass));
    }


    private String determineCurrentTestPath() {
        return determineCurrentTargetPath() + "/..";
    }


    private String determineCurrentTargetPath() {
        String absolutePath =
              testCaseClass.getResource(FILE_SEPARATOR + testCaseClass.getName().replace('.', '/') + ".class")
                    .getFile();
        Assert.assertNotNull("Determination du chemin target", absolutePath);
        Assert.assertEquals("Le chemin commence par '/'", FILE_SEPARATOR, absolutePath.substring(0, 1));
        absolutePath = absolutePath.substring(1, absolutePath.lastIndexOf("target") + "target".length());
        return absolutePath;
    }


    private String getDatagenFileContent(String entityFileAbsolutePath) throws IOException {
        final String sqlDirectory = getDatagenDirectory() + "/Sql";
        return "<?xml version='1.0' encoding='ISO-8859-1'?>              "
               + "<data>                                                        "
               + "    <configuration>                                           "
               + "        <project><name>TestDataGen</name></project>           "
               + "        <path>                                                "
               + buildNode("sql", sqlDirectory)
               + buildNode("sql-view", sqlDirectory)
               + buildNode("sql-index", sqlDirectory) + buildNode("sql-constraint", sqlDirectory)
               + buildNode("trigger", sqlDirectory) + buildNode("bean", getDatagenDirectory() + "/data")
               + buildNode("handlers", getDatagenDirectory() + "/handler")
               + buildNode("cmdHandler", getDatagenDirectory() + "/handler")
               + buildNode("castor", getDatagenDirectory() + "/data")
               + buildNode("structure", getDatagenDirectory() + FILE_SEPARATOR)
               + buildNode("requetor", getDatagenDirectory() + FILE_SEPARATOR)
               + buildNode("referential", getDatagenDirectory() + FILE_SEPARATOR)
               + buildNode("globs", getDatagenDirectory() + FILE_SEPARATOR)
               + "        </path>                                               "
               + "    </configuration>                                          "
               + getEntityList(entityFileAbsolutePath)
               + "                                                       "
               + "</data>                                                       ";
    }


    private String getEntityList(String entityFileAbsolutePath) throws IOException {
        String content = FileUtil.loadContent(new File(entityFileAbsolutePath));
        return content.substring(content.indexOf(ENTITY_START_TAG), content.indexOf(ENTITIES_END_TAG));
    }


    private String buildNode(String nodeName, String nodeValue) throws IOException {
        return "<" + nodeName + ">" + new File(nodeValue).getCanonicalPath() + "</" + nodeName + ">";
    }


    enum LocalisationStrategy {
        LOCATE_AS_A_RESOURCE,
        LOCATE_FROM_BASE_DIR
    }
    public static class DatagenInput {
        private LocalisationStrategy localisationStrategy;
        private String path;


        private DatagenInput(LocalisationStrategy localisationStrategy, String path) {
            this.localisationStrategy = localisationStrategy;
            this.path = path;
        }


        public static DatagenInput resource(String path) {
            return new DatagenInput(LocalisationStrategy.LOCATE_AS_A_RESOURCE, path);
        }


        public static DatagenInput file(String path) {
            return new DatagenInput(LocalisationStrategy.LOCATE_FROM_BASE_DIR, path);
        }


        public String entityFileAbsolutePath(Class testCaseClass) {
            switch (localisationStrategy) {
                case LOCATE_AS_A_RESOURCE:
                    return new File(testCaseClass.getResource(path).getFile()).getAbsolutePath();
                case LOCATE_FROM_BASE_DIR:
                    return PathUtil.findBaseDirectory(testCaseClass) + FILE_SEPARATOR + path;
            }
            throw new InternalError("impossible");
        }
    }
}
