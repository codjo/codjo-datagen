/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package referential;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import kernel.DomUtil;
import kernel.Generator;
import org.w3c.dom.Document;
/**
 *
 */
public class ReferentialGenerator implements Generator {
    public GeneratorType getType() {
        return GeneratorType.CONFIGURATION;
    }


    public void generate(Document doc, String rootDir) throws IOException, TransformerException {
        File root = new File(rootDir);
        kernel.Util.mkdirs(root);

        DOMSource domSource = new DOMSource(doc);
        File preferenceFile = new File(rootDir, "referential-preference.xml");
        FileWriter writer = new FileWriter(preferenceFile);
        try {
            generatePreference(domSource, writer);
        }
        finally {
            writer.close();
        }
        File mappingFile = new File(rootDir, "referential-mapping.xml");
        writer = new FileWriter(mappingFile);
        try {
            generateMapping(domSource, writer);
        }
        finally {
            writer.close();
        }
    }


    void generatePreference(DOMSource source, Writer writer) throws TransformerException {
        generateFromXSL(writer, source, "ReferentialPreference.xsl");
    }


    public void generateMapping(DOMSource source, Writer writer) throws TransformerException {
        generateFromXSL(writer, source, "ReferentialMapping.xsl");
    }


    private void generateFromXSL(Writer writer, DOMSource source, String xslFileName)
          throws TransformerException {
        StreamResult result = new StreamResult(writer);
        Transformer sqlXsl = DomUtil.toTransformer(getClass().getResourceAsStream(xslFileName));
        sqlXsl.transform(source, result);
    }
}
