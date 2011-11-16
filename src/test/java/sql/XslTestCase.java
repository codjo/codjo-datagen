package sql;
import net.codjo.util.file.FileUtil;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import kernel.DomUtil;
import kernel.Util;
import org.junit.Test;
import org.w3c.dom.Document;
public abstract class XslTestCase {

    @Test
    public void test_generation() throws Exception {
        Transformer transformer = toTransformer(getXslTransformer());

        assertTransformation(transformer, getXmlSource(), getEtalon());
    }


    protected void assertTransformation(Transformer transformer, String xmlSource, String etalon)
          throws Exception {
        DOMSource source = DomUtil.toDataSource(xmlSource);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);

        Util.compare(Util.flatten(FileUtil.loadContent(new File(etalon))), Util.flatten(writer.toString()));
    }


    protected Transformer toTransformer(String xslTransformer) throws Exception {
        return DomUtil.toTransformer(mockXslFile(xslTransformer));
    }


    protected abstract String getEtalon();


    protected abstract String getXslTransformer();


    protected abstract String getXmlSource();


    private FileInputStream mockXslFile(String xslFileName) throws Exception {
        File tempFile = File.createTempFile("transformer", "xsl");

        Document document = kernel.DomUtil
              .toDocument(new InputStreamReader(getClass().getResourceAsStream(xslFileName)));
        document.getDocumentElement()
              .setAttribute("xmlns:database", "net.codjo.datagen.DatabaseScriptHelperXslAdapterMock");

        XMLSerializer xmlSerializer = new XMLSerializer(new FileOutputStream(tempFile),
                                                        new OutputFormat(document));
        xmlSerializer.serialize(document.getDocumentElement());
        return new FileInputStream(tempFile);
    }
}
