/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.datagen;
import net.codjo.test.common.PathUtil;
import net.codjo.test.common.fixture.DirectoryFixture;
import net.codjo.test.common.fixture.Fixture;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import junit.framework.Assert;
import kernel.Main;
/**
 * Fixture pour l'utilisation de datagen.
 */
public class DatagenFixture implements Fixture {
    private static final String ENTITY_START_TAG = "<entity";
    private static final String ENTITIES_END_TAG = "</entities>";
    private static final String FILE_SEPARATOR = "/";
    private Class testCaseClass;
    private String entityFileRelativePath;
    private DirectoryFixture datagenDirectory;


    public DatagenFixture(Class testCaseClass) {
        this(testCaseClass, "src/datagen/datagen.xml");
    }


    public DatagenFixture(Class testCaseClass, String entityFileRelativePath) {
        this.testCaseClass = testCaseClass;
        this.entityFileRelativePath = entityFileRelativePath;
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

        File datagenFile = buildDatagenFile(PathUtil.findBaseDirectory(testCaseClass)
                                            + FILE_SEPARATOR + entityFileRelativePath);

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


    public File getTargetFile() {
        return new File(determineCurrentTargetPath());
    }


    public File getEntityFile() {
        return new File(determineCurrentTestPath(), entityFileRelativePath);
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
}
