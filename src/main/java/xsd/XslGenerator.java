package xsd;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import kernel.Util;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

public abstract class XslGenerator {
    protected final String destinationFileName;
    protected Logger log;


    protected XslGenerator(String destinationFileName) {
        this.destinationFileName = destinationFileName;
    }


    public String getDestinationFileName() {
        return destinationFileName;
    }


    public Logger getLog() {
        return log;
    }


    public void setLog(Logger log) {
        this.log = log;
    }


    public void generate(Document doc, String rootDir) throws IOException, TransformerException {
        DOMSource source = new DOMSource(doc);

        Util.mkdirs(new File(rootDir));

        File destinationFile = new File(rootDir, getDestinationFileName());
        destinationFile.delete();
        FileWriter writer = new FileWriter(destinationFile);
        try {
            debug("Generation de " + getDestinationFileName());
            debug("                                dans " + destinationFile);
            doGenerate(source, writer);
            debug("                                Effectue");
        }
        finally {
            writer.close();
        }
    }


    protected abstract void doGenerate(DOMSource source, Writer writer)
          throws TransformerException, IOException;


    private void debug(String message) {
        if (log != null) {
            log.debug(message);
        }
    }
}
