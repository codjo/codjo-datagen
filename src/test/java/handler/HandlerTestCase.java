package handler;
import java.io.File;
import java.io.StringWriter;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import kernel.DomUtil;
import kernel.Util;
import net.codjo.util.file.FileUtil;
import org.junit.Before;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public abstract class HandlerTestCase {
    protected Transformer transformer;


    @Before
    public void initializeTransformer() {
        transformer = transformer(getXslFileName());
    }


    protected void assertTransformation(Transformer xsl, String xmlSource, String etalon)
          throws Exception {

        assertThat(assertXslTransformAndCollectWaning(xsl, xmlSource, etalon).warning,
                   describedAs("Aucun warning émis lors de la transformation", equalTo("")));
    }


    private WarningCollector assertXslTransformAndCollectWaning(Transformer xsl, String xmlSource, String etalon)
          throws Exception {
        WarningCollector warningCollector = new WarningCollector();
        xsl.setErrorListener(warningCollector);

        Util.compare(Util.flatten(FileUtil.loadContent(new File(etalon)), true),
                     Util.flatten(transformUsing(xsl, xmlSource), true));
        return warningCollector;
    }


    protected void assertTransformation(Transformer xsl,
                                        String xmlSource,
                                        String etalon,
                                        String warning) throws Exception {

        assertThat(assertXslTransformAndCollectWaning(xsl, xmlSource, etalon).warning,
                   describedAs("Warning émis lors de la transformation", equalTo(warning)));
    }


    protected String transformUsing(Transformer xsl, String xmlSource) throws Exception {
        DOMSource source = toDataSource(xmlSource);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        xsl.transform(source, result);
        return writer.toString();
    }


    protected DOMSource toDataSource(String xmlSource) throws Exception {
        return DomUtil.toDataSource(xmlSource);
    }


    protected Transformer transformer(String xslTransformer) {
        try {
            return DomUtil.toTransformer(getClass().getResourceAsStream(xslTransformer));
        }
        catch (TransformerConfigurationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    protected abstract String getXslFileName();


    protected abstract String file(String name);


    public static class WarningCollector implements ErrorListener {
        private String warning = "";


        public void warning(TransformerException ex)
              throws TransformerException {
            if (warning.length() > 0) {
                warning += ", ";
            }
            warning += ex.getLocalizedMessage();
        }


        public void error(TransformerException ex) {
        }


        public void fatalError(TransformerException ex) {
        }


        public String getWarning() {
            return warning;
        }


        public void setWarning(String warning) {
            this.warning = warning;
        }
    }
}
