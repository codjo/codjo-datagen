package xsd;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import kernel.DomUtil;
import kernel.LastBuildTimestamp;
import kernel.Main;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class GenerateTokioXsdsMain {
    private static final Logger LOG = Logger.getLogger(Main.class);
    private static final String COMMAND_LINE_USAGE
          = "Main <projectArtifactId> <datagenFinalFile> <outputDirectory>";

    private static final String GENERATE_CASES_XSL = "generateCases.xsl";
    private static final String GENERATE_SCENARII_XSL = "generateScenarii.xsl";
    private static final String GENERATE_STORY_XSL = "generateStory.xsl";
    private static final String GENERATE_ENTITIES_XSL = "generateEntities.xsl";
    private static final String CASES_XSD = "-cases.xsd";
    private static final String SCENARII_XSD = "-scenarii.xsd";
    private static final String STORY_XSD = "-story.xsd";
    private static final String ENTITIES_XSD = "-entities.xsd";
    private static final Map<String, String> xslToXsd = new HashMap<String, String>();


    static {
        xslToXsd.put(GENERATE_CASES_XSL, CASES_XSD);
        xslToXsd.put(GENERATE_ENTITIES_XSL, ENTITIES_XSD);
        xslToXsd.put(GENERATE_SCENARII_XSL, SCENARII_XSD);
        xslToXsd.put(GENERATE_STORY_XSL, STORY_XSD);
    }


    private final LastBuildTimestamp lastBuildTimestamp = new LastBuildTimestamp(
          "/lib-datagen-lastBuild.timestamp");

    private final String projectArtifactId;
    private final File datagenFinalFile;
    private final File outputDirectory;
    private String projectPrefix;


    public GenerateTokioXsdsMain(String projectArtifactId, File datagenFinalFile, File outputDirectory) {
        this.projectArtifactId = projectArtifactId;
        this.datagenFinalFile = datagenFinalFile;
        this.outputDirectory = outputDirectory;
    }


    public static void main(String... args) {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);

        if (args.length <= 2 && args.length >= 4) {
            LOG.error("command line : " + Arrays.asList(args));
            LOG.error("usage : ");
            LOG.error("\t" + COMMAND_LINE_USAGE);
            throw new IllegalArgumentException("usage : " + COMMAND_LINE_USAGE);
        }

        String legacyModeProperty = System.getProperty("net.codjo.datagen.legacyMode");
        if ((legacyModeProperty != null) && ("true".equalsIgnoreCase(legacyModeProperty))) {
            sql.Util.legacyMode = true;
            LOG.warn("Activation du mode base legacy !");
        }

        String legacyPrefixProperty = System.getProperty("net.codjo.datagen.legacyPrefix");
        if (legacyPrefixProperty != null) {
            sql.Util.legacyPrefix = legacyPrefixProperty;
        }

        new GenerateTokioXsdsMain(args[0], new File(args[1]), new File(args[2])).execute();
    }


    public void execute() {
        try {
            if (hasToBeGenerated()) {
                LOG.info("Generation des xsd pour les fichiers tokio et entities");
                generate();
            }
            else {
                LOG.info("Generation non necessaire car pas de modifications.");
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Generation en erreur : " + e.getMessage(), e);
        }
    }


    private String getProjectPrefix() {
        if (projectPrefix == null) {
            if (projectArtifactId.endsWith("-datagen")) {
                projectPrefix = projectArtifactId.substring(0, projectArtifactId.length() - 8);
            }
            else {
                projectPrefix = projectArtifactId;
            }
        }
        return projectPrefix;
    }


    private boolean hasToBeGenerated() throws IOException, ParseException {
        for (Entry<String, String> entry : xslToXsd.entrySet()) {
            File file = new File(outputDirectory, getProjectPrefix() + entry.getValue());
            if (!file.exists()
                || datagenFinalFile.lastModified() > file.lastModified()
                || lastBuildTimestamp.isMoreRecent(file)) {
                return true;
            }
        }
        return false;
    }


    private void generate() throws IOException,
                                   SAXException,
                                   ParserConfigurationException,
                                   TransformerException {
        String tokioXsdModelFilename = getProjectPrefix() + "-tokio-xsd-model.xml";
        generateTokioXsdModel(tokioXsdModelFilename);
        generateXsds(tokioXsdModelFilename);
    }


    private void generateTokioXsdModel(String tokioXsdModelFileName) throws IOException,
                                                                            SAXException,
                                                                            ParserConfigurationException,
                                                                            TransformerException {
        new TokioXslModelGenerator(tokioXsdModelFileName, LOG)
              .generate(DomUtil.toDocument(new FileReader(datagenFinalFile)),
                        outputDirectory.getAbsolutePath());
    }


    private void generateXsds(String tokioXsdModelFileName) throws IOException,
                                                                   SAXException,
                                                                   ParserConfigurationException,
                                                                   TransformerException {
        Document tokioXsdModelDoc = DomUtil.toDocument(new FileReader(new File(outputDirectory,
                                                                               tokioXsdModelFileName)));

        for (Entry<String, String> entry : xslToXsd.entrySet()) {
            new SimpleXslGenerator(getProjectPrefix() + entry.getValue(), entry.getKey(), LOG)
                  .generate(tokioXsdModelDoc,
                            outputDirectory.getAbsolutePath());
        }
    }
}
