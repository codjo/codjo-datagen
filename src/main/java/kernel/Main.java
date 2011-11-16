/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package kernel;
import bean.BeanGenerator;
import castor.CastorGenerator;
import net.codjo.taskpool.Task;
import net.codjo.taskpool.TaskManager;
import net.codjo.util.file.FileUtil;
import globs.GlobGenerator;
import handler.archive.ArchiveHandlerGenerator;
import handler.command.CommandHandlerGenerator;
import handler.delete.DeleteHandlerGenerator;
import handler.insert.NewHandlerGenerator;
import handler.requetor.RequetorDefGenerator;
import handler.requetor.RequetorHandlerGenerator;
import handler.select.SelectHandlerGenerator;
import handler.sql.SqlHandlerGenerator;
import handler.update.UpdateHandlerGenerator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import kernel.Generator.GeneratorType;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import referential.ReferentialGenerator;
import role.RoleGenerator;
import sql.SqlGenerator;
import sql.ViewGenerator;
import sql.constraint.ConstraintGenerator;
import sql.index.IndexGenerator;
import structure.StructureGenerator;
import trigger.TriggerDeleteGenerator;
import trigger.TriggerIUGenerator;
import trigger.TriggerInsertGenerator;
import trigger.TriggerUpdateGenerator;
/**
 * Classe de génération totale des données.
 */
@SuppressWarnings({"OverlyCoupledClass"})
public class Main {
    static final boolean DEBUG = false;
    public static final String ALL_GENERATOR = "ALL";
    private static final Logger LOG = Logger.getLogger(Main.class);
    public static final String DB_TYPE_PARAMETER = "databaseType";
    public static final String DB_TYPE_SYBASE = "sybase";
    public static final String DB_TYPE_ORACLE = "oracle";
    public static final Object DB_TYPE_MYSQL = "mysql";
    public static final String INCLUDE_TRANSFORMER_FILENAME = "resolveEntity.xsl";
    public static final String ADD_FIELD_TRANSFORMER_FILENAME = "addField.xsl";
    public static final String ADD_HANDLER_TRANSFORMER_FILENAME = "addHandler.xsl";
    public static final String PRE_GENERATOR_TRANSFORMER_FILENAME = "PreGenerator.xsl";
    private static final LastBuildTimestamp lastBuildTimestamp = new LastBuildTimestamp(
          "/lib-datagen-lastBuild.timestamp");
    private ArchiveHandlerGenerator archiveHandlerGenerator;
    private BeanGenerator beanGenerator;
    private String beanPath;
    private CastorGenerator castorGenerator;
    private String castorPath;
    private CommandHandlerGenerator cmdHandlerGenerator;
    private String cmdHandlerPath;
    private DeleteHandlerGenerator deleteHandlerGenerator;
    private String handlersPath;
    private NewHandlerGenerator newHandlerGenerator;
    private SelectHandlerGenerator selectHandlerGenerator;
    private SqlGenerator sqlGenerator;
    private String sqlPath;
    private String sqlViewPath;
    private StructureGenerator structureGenerator;
    private String structurePath;
    private UpdateHandlerGenerator updateHandlerGenerator;
    private ViewGenerator viewGenerator;
    private SqlHandlerGenerator sqlSelectGenerator;
    private RequetorHandlerGenerator requetorHandlerGenerator;
    private RequetorDefGenerator requetorDefGenerator;
    private IndexGenerator sqlIndexGenerator;
    private String sqlIndexPath;
    private ConstraintGenerator sqlConstraintGenerator;
    private String sqlConstraintPath;
    private String triggerPath;
    private TriggerDeleteGenerator triggerDeleteGenerator;
    private TriggerInsertGenerator triggerInsertGenerator;
    private TriggerUpdateGenerator triggerUpdateGenerator;
    private TriggerIUGenerator triggerIUGenerator;
    private String requetorPath;
    private RoleGenerator roleGenerator;
    private ReferentialGenerator referentialGenerator;
    private String referentialPath;
    private GlobGenerator globGenerator;
    private String globPath;
    private String documentAsString;
    private TaskManager taskManager;
    private String databaseType;
    private boolean exitOnError = true;
    private List<GeneratorType> activatedTypes;


    public Main(String databaseType) throws Exception {
        this(databaseType, toGeneratorTypes(ALL_GENERATOR));
    }


    @SuppressWarnings({"OverlyCoupledMethod"})
    public Main(String databaseType, List<GeneratorType> activatedTypes) throws Exception {
        this.databaseType = databaseType;
        this.activatedTypes = activatedTypes;
        sqlGenerator = new SqlGenerator();
        viewGenerator = new ViewGenerator();
        beanGenerator = new BeanGenerator();
        structureGenerator = new StructureGenerator();
        newHandlerGenerator = new NewHandlerGenerator();
        deleteHandlerGenerator = new DeleteHandlerGenerator();
        updateHandlerGenerator = new UpdateHandlerGenerator();
        selectHandlerGenerator = new SelectHandlerGenerator();
        cmdHandlerGenerator = new CommandHandlerGenerator();
        archiveHandlerGenerator = new ArchiveHandlerGenerator();
        sqlSelectGenerator = new SqlHandlerGenerator();
        castorGenerator = new CastorGenerator();
        requetorHandlerGenerator = new RequetorHandlerGenerator();
        requetorDefGenerator = new RequetorDefGenerator();
        sqlIndexGenerator = new IndexGenerator();
        sqlConstraintGenerator = new ConstraintGenerator();
        triggerDeleteGenerator = new TriggerDeleteGenerator();
        triggerInsertGenerator = new TriggerInsertGenerator();
        triggerUpdateGenerator = new TriggerUpdateGenerator();
        triggerIUGenerator = new TriggerIUGenerator();
        roleGenerator = new RoleGenerator();
        referentialGenerator = new ReferentialGenerator();
        globGenerator = new GlobGenerator();
    }


    @SuppressWarnings({"MethodNamesDifferingOnlyByCase"})
    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);

        if ((args.length <= 3) || (args.length >= 6)) {
            LOG.error("command line : " + Arrays.asList(args));
            LOG.error("usage : ");
            LOG.error(
                  "\tMain <source> <destination> <properties> <databaseType> [<generatorType>]");
            throw new IllegalArgumentException(
                  "usage : Main <source> <destination> <properties> <databaseType> [<generatorType>]");
        }
        String generatorTypes = args.length == 5 ? args[4] : ALL_GENERATOR;

        String legacyModeProperty = System.getProperty("net.codjo.datagen.legacyMode");
        if ((legacyModeProperty != null) && ("true".equalsIgnoreCase(legacyModeProperty))) {
            sql.Util.legacyMode = true;
            LOG.warn("Activation du mode base legacy !");
        }

        String legacyPrefixProperty = System.getProperty("net.codjo.datagen.legacyPrefix");
        if (legacyPrefixProperty != null) {
            sql.Util.legacyPrefix = legacyPrefixProperty;
        }

        Main main = new Main(args[3], toGeneratorTypes(generatorTypes));
        FileReader sourceReader = new FileReader(args[0]);

        Transformer includeTransformer =
              DomUtil.toTransformer(Main.class.getResourceAsStream(INCLUDE_TRANSFORMER_FILENAME));
        Reader includeReader = main.transform(sourceReader, includeTransformer);
        sourceReader.close();

        StringReader propertyParsedReader = null;

        // resolve datagen properties
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(new File(args[2])));
            LOG.info("\n Utilisation du datagen.properties : " + new File(args[2]));
            propertyParsedReader = getResolvedDatagenPropertyReader(includeReader, prop);
        }
        catch (Exception ex) {
            LOG.info("\n No datagen.properties defini.");
        }

        Reader finalXml;
        Transformer addFieldTransformer =
              DomUtil.toTransformer(Main.class.getResourceAsStream(ADD_FIELD_TRANSFORMER_FILENAME));
        if (propertyParsedReader == null) {
            finalXml = main.transform(includeReader, addFieldTransformer);
        }
        else {
            finalXml = main.transform(propertyParsedReader, addFieldTransformer);
            propertyParsedReader.close();
        }

        Transformer addHandlerTransformer =
              DomUtil.toTransformer(Main.class.getResourceAsStream(ADD_HANDLER_TRANSFORMER_FILENAME));
        finalXml = main.transform(finalXml, addHandlerTransformer);

        Transformer preGeneratorTransformer =
              DomUtil.toTransformer(Main.class.getResourceAsStream(PRE_GENERATOR_TRANSFORMER_FILENAME));
        finalXml = main.transform(finalXml, preGeneratorTransformer);

        includeReader.close();

        String filename = args[1];
        if (hasToBeGenerated(filename, finalXml)) {
            main.generate(new FileReader(filename));
        }
        else {
            LOG.info("\nGénération non nécessaire car pas de modification.");
        }
    }


    private static boolean hasToBeGenerated(String filename, Reader finalXml) throws IOException {
        String oldContent = null;
        File oldFinalFile = new File(filename);
        if (oldFinalFile.exists()) {
            oldContent = Util.toString(new FileReader(oldFinalFile), false);
        }

        String newContent = Util.toString(finalXml);
        if (oldContent == null ||
            !oldContent.equals(newContent) ||
            lastBuildTimestamp.isMoreRecent(oldFinalFile)) {
            writeFinalXmlFile(filename, newContent);
            return true;
        }

        return false;
    }


    private static void writeFinalXmlFile(String filename, String newContent) throws IOException {
        FileWriter writer = new FileWriter(new File(filename));
        try {
            writer.write(newContent);
        }
        finally {
            writer.close();
        }
    }


    public static StringReader getResolvedDatagenPropertyReader(Reader includeReader, Properties properties)
          throws IOException, BuildException {
        Map<Object, Object> propertiesMap = new HashMap<Object, Object>();
        propertiesMap.putAll(properties);

        return new StringReader(Util.replaceProperties(Util.toString(includeReader), propertiesMap));
    }


    public String getBeanPath() {
        return beanPath;
    }


    public String getCastorPath() {
        return castorPath;
    }


    public String getCmdHandlerPath() {
        return cmdHandlerPath;
    }


    public String getHandlersPath() {
        return handlersPath;
    }


    public String getSqlPath() {
        return sqlPath;
    }


    public String getSqlViewPath() {
        return sqlViewPath;
    }


    public String getStructurePath() {
        return structurePath;
    }


    public String getReferentialPath() {
        return referentialPath;
    }


    public String getGlobPath() {
        return globPath;
    }


    public void setExitOnError(boolean exitOnError) {
        this.exitOnError = exitOnError;
    }


    public void generate(String filename) throws Exception {
        Reader reader = this.manageEntities(filename);
        doGenerate(Util.toString(reader));
    }


    public void generate(Reader reader) throws Exception {
        doGenerate(Util.toString(reader, false));
        writeConfigFile();
    }


    private void doGenerate(String document) throws Exception {
        LOG.info("Debut de generation....");
        long startTime = System.currentTimeMillis();

        this.documentAsString = document;
        init(parse(document));

        taskManager = new TaskManager(10);

        addTask("Generation table", sqlGenerator, sqlPath);
        addTask("Generation Index", sqlIndexGenerator, sqlIndexPath);

        if (sqlConstraintPath != null) {
            addTask("Generation Constraint", sqlConstraintGenerator, sqlConstraintPath);
        }

        if (!Main.DB_TYPE_MYSQL.equals(databaseType)) {
            addTask("Generation Trigger Delete", triggerDeleteGenerator, triggerPath);
            addTask("Generation Trigger Insert", triggerInsertGenerator, triggerPath);
            addTask("Generation Trigger Update", triggerUpdateGenerator, triggerPath);
            addTask("Generation Trigger IU", triggerIUGenerator, triggerPath);
        }
        addTask("Generation VIEW", viewGenerator, sqlViewPath);
        addTask("Generation JAVA", beanGenerator, beanPath);
        addTask("Generation CASTOR", castorGenerator, castorPath);
        addTask("Structures", structureGenerator, structurePath);
        addTask("Roles", roleGenerator, structurePath);
        addTask("Generation des definitions de lien", requetorDefGenerator, requetorPath);
        addTask("Handler New", newHandlerGenerator, handlersPath);
        addTask("Handler Sql", sqlSelectGenerator, handlersPath);
        addTask("Handler Requetor", requetorHandlerGenerator, handlersPath);
        addTask("Handler Select", selectHandlerGenerator, handlersPath);
        addTask("Handler Update", updateHandlerGenerator, handlersPath);
        addTask("Handler Delete", deleteHandlerGenerator, handlersPath);
        addTask("Handler Archive", archiveHandlerGenerator, handlersPath);
        addTask("Handler Commande", cmdHandlerGenerator, cmdHandlerPath);
        addTask("Generation referentiels", referentialGenerator, referentialPath);
        addTask("Generation globs", globGenerator, globPath);

        taskManager.start();
        taskManager.waitCurrentFinished();
        taskManager.shutdown();

        long endTime = System.currentTimeMillis();
        LOG.info("...Fin de generation en " + Util.timeMillisToString(endTime - startTime));
    }


    private void addTask(String label, Generator generator, String path) {
        if (activatedTypes.contains(generator.getType())) {
            taskManager.pushToDoTask(new GenerateTask(label, generator, path));
        }
    }


    void init(Document doc) throws Exception {
        sqlPath = getPath(doc, "//data/configuration/path/sql");
        sqlViewPath = getPath(doc, "//data/configuration/path/sql-view");
        sqlIndexPath = getPath(doc, "//data/configuration/path/sql-index");
        sqlConstraintPath = getPath(doc, "//data/configuration/path/sql-constraint");
        triggerPath = getPath(doc, "//data/configuration/path/trigger");
        beanPath = getPath(doc, "//data/configuration/path/bean");
        handlersPath = getPath(doc, "//data/configuration/path/handlers");
        castorPath = getPath(doc, "//data/configuration/path/castor");
        cmdHandlerPath = getPath(doc, "//data/configuration/path/cmdHandler");
        structurePath = getPath(doc, "//data/configuration/path/structure");
        requetorPath = getPath(doc, "//data/configuration/path/requetor");
        referentialPath = getPath(doc, "//data/configuration/path/referential");
        globPath = getPath(doc, "//data/configuration/path/globs");
    }


    private String getPath(Document doc, String xpath) throws TransformerException {
        return DomUtil.getNodeValue(doc, xpath);
    }


    public String getSqlIndexPath() {
        return sqlIndexPath;
    }


    public String getSqlConstraintPath() {
        return sqlConstraintPath;
    }


    public String getTriggerPath() {
        return triggerPath;
    }


    public String getRequetorPath() {
        return requetorPath;
    }


    public void setRequetorPath(String requetorPath) {
        this.requetorPath = requetorPath;
    }


    Reader transform(Reader reader, Transformer transformer)
          throws IOException, ParserConfigurationException, SAXException, TransformerException {
        Document initialDoc = DomUtil.toDocument(reader);
        StringWriter result = new StringWriter();
        transformer.transform(new DOMSource(initialDoc), new StreamResult(result));
        return new StringReader(result.toString());
    }


    Reader manageEntities(String fileName)
          throws IOException, ParserConfigurationException, SAXException, TransformerException {
        Document initialDoc = DomUtil.toDocument(fileName);
        StringWriter result = new StringWriter();

        Transformer entitiesTransformer =
              DomUtil.toTransformer(Main.class.getResourceAsStream(PRE_GENERATOR_TRANSFORMER_FILENAME));
        entitiesTransformer.transform(new DOMSource(initialDoc), new StreamResult(result));
        return new StringReader(result.toString());
    }


    private Document parse(String requests) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(requests)));
    }


    private void writeConfigFile() throws IOException {
        if (!activatedTypes.contains(GeneratorType.CONFIGURATION)) {
            return;
        }
        String content;
        if (Main.DB_TYPE_ORACLE.equals(databaseType)) {
            content = "<?xml version='1.0' encoding='ISO-8859-1'?>"
                      + "<database name='DirectUseDB' engine='oracle'>"
                      + "    <driver class-name='oracle.jdbc.driver.OracleDriver' url='jdbc:oracle:thin:notUSED'/>"
                      + "    <mapping href='Mapping.xml'/>"
                      + "</database>";
        }
        else if (Main.DB_TYPE_MYSQL.equals(databaseType)) {
            content = "<?xml version='1.0' encoding='ISO-8859-1'?>"
                      + "<database name='DirectUseDB' engine='mysql'>"
                      + "    <driver class-name='com.mysql.jdbc.Driver' url='jdbc:mysql://notUSED'/>"
                      + "    <mapping href='Mapping.xml'/>"
                      + "</database>";
        }
        else {
            content = "<?xml version='1.0' encoding='ISO-8859-1'?>"
                      + "<database name='DirectUseDB' engine='sybase'>"
                      + "    <driver class-name='com.sybase.jdbc2.jdbc.SybDataSource' url='jdbc:sybase:Tds:notUSED'/>"
                      + "    <mapping href='Mapping.xml'/>"
                      + "</database>";
        }
        File castorDirectory = new File(getCastorPath());
        castorDirectory.mkdirs();
        FileUtil.saveContent(new File(castorDirectory, "castor-config.xml"), content);
    }


    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }


    public static List<GeneratorType> toGeneratorTypes(String typeInString) {
        if (ALL_GENERATOR.equals(typeInString)) {
            return Arrays.asList(GeneratorType.values());
        }
        List<GeneratorType> generatorTypes = new ArrayList<GeneratorType>();
        for (String value : typeInString.split(",")) {
            generatorTypes.add(GeneratorType.valueOf(value.trim()));
        }
        return generatorTypes;
    }


    public class GenerateTask implements Task {
        private String label;
        private Generator generator;
        private String path;


        public GenerateTask(String label, Generator generator, String path) {
            this.label = label;
            this.generator = generator;
            this.path = path;
        }


        public int getPriority() {
            return 0;
        }


        public void run() {
            try {
                long startTime = System.currentTimeMillis();
                generator.generate(parse(documentAsString), path);
                long endTime = System.currentTimeMillis();
                LOG.info(label + " en " + Util.timeMillisToString(endTime - startTime));
            }
            catch (Throwable throwable) {
                LOG.error("Erreur lors de la '" + label + "' ", throwable);
                if (exitOnError) {
                    System.exit(1);
                }
            }
        }
    }
}
