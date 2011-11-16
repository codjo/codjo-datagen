package xsd;
import java.io.IOException;
import java.io.Writer;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import kernel.DomUtil;
import org.apache.log4j.Logger;

public class SimpleXslGenerator extends XslGenerator {
    private String xslFilename;


    public SimpleXslGenerator(String destinationFileName, String xslFilename, Logger log) {
        super(destinationFileName);
        this.xslFilename = xslFilename;
        setLog(log);
    }


    protected void doGenerate(DOMSource source, Writer writer) throws TransformerException, IOException {
        StreamResult result = new StreamResult(writer);
        Transformer transformer =
              DomUtil.toTransformer(getClass().getResourceAsStream(xslFilename));
        transformer.transform(source, result);
    }
}
