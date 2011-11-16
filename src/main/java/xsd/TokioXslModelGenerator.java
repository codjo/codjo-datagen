package xsd;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import kernel.DomUtil;
import org.apache.log4j.Logger;

public class TokioXslModelGenerator extends XslGenerator {
    private static final String PRE_XSL = "TokioXsdModel-pre.xsl";
    private static final String POST_XSL = "TokioXsdModel-post.xsl";


    public TokioXslModelGenerator(String destinationFileName) {
        super(destinationFileName);
    }


    public TokioXslModelGenerator(String destinationFileName, Logger log)
          throws TransformerConfigurationException {
        super(destinationFileName);
        setLog(log);
    }


    protected void doGenerate(DOMSource source, Writer writer) throws TransformerException, IOException {
        Transformer preTransformer = DomUtil.toTransformer(getClass().getResourceAsStream(PRE_XSL));
        Transformer postTransformer = DomUtil.toTransformer(getClass().getResourceAsStream(POST_XSL));

        StringWriter tempWriter = new StringWriter();
        preTransformer.transform(source,
                                 new StreamResult(tempWriter));
        postTransformer.transform(new StreamSource(new StringReader(tempWriter.toString())),
                                  new StreamResult(writer));
    }
}
