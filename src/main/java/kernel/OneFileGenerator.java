/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package kernel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
/**
 * Generation d'un fichier pour tous les tags.
 */
public abstract class OneFileGenerator implements Generator {
    private static final Logger LOG = Logger.getLogger(OneFileGenerator.class);
    private String featureTag;
    private String destFileName;
    private boolean generateEmptyFile;
    private String xslFile;
    private GeneratorType type;


    protected OneFileGenerator(String featureTag, String xslFile, String destFileName, GeneratorType type) {
        this(featureTag, xslFile, destFileName, true, type);
    }


    protected OneFileGenerator(String featureTag,
                               String xslFile,
                               String destFileName,
                               boolean generateEmptyFile,
                               GeneratorType type) {
        this.featureTag = featureTag;
        this.destFileName = destFileName;
        this.generateEmptyFile = generateEmptyFile;
        this.xslFile = xslFile;
        this.type = type;
    }


    public GeneratorType getType() {
        return type;
    }


    public void generate(Document doc, String rootDir)
          throws IOException, TransformerException {
        if (!generateEmptyFile) {
            if (DomUtil.selectNodeList(doc, featureTag).getLength() == 0) {
                return;
            }
        }

        DOMSource source = new DOMSource(doc);

        Util.mkdirs(new File(rootDir));

        File dest = new File(rootDir, destFileName);
        dest.delete();
        kernel.Util.createNewFile(dest);
        FileWriter writer = new FileWriter(dest);
        try {
            LOG.debug("Generation de " + featureTag);
            LOG.debug("                               dans " + dest);
            generate(source, writer);
            LOG.debug("                               Effectué ");
        }
        finally {
            writer.close();
        }
    }


    private void generate(DOMSource source, Writer writer)
          throws TransformerException {
        StreamResult result = new StreamResult(writer);
        Transformer transformer =
              DomUtil.toTransformer(getClass().getResourceAsStream(xslFile));
        transformer.transform(source, result);
    }
}
