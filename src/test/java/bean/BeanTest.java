/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package bean;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import junit.framework.TestCase;
import org.xml.sax.SAXException;
/**
 * DOCUMENT ME!
 *
 * @version $Revision: 1.6 $
 */
public class BeanTest extends TestCase {
    private static final String EXPECTED =
          "package generated;" + "/**" + "* Piste d'audit. <p>" + "*"
          + "* Cette classe est genere automatiquement." + "*/" + "public class Audit {"
          + "" + "    /**" + "     * Constructeur." + "     */" + "    public Audit() {}"
          + "    " + "    private String comment;" + "" + "    " + "    /**"
          + "     * Positionne le Commentaire de fiche." + "     * @param comment na"
          + "     */" + "    " + "    public void setComment(String comment) {"
          + "      this.comment = comment;" + "    }" + "" + "    " + "    /**"
          + "     * Retourne le Commentaire de fiche." + "     * @return comment"
          + "     */" + "    " + "    public String getComment() {"
          + "      return comment;" + "    }" + "" + "    private String createdBy;" + ""
          + "    " + "    public void setCreatedBy(String createdBy) {"
          + "      this.createdBy = createdBy;" + "    }" + "" + "    "
          + "    public String getCreatedBy() {" + "      return createdBy;" + "    }" + ""
          + "}";


    public void test_generation() throws Exception {
        DOMSource source = toDataSource("src/test/resources/bean/BeanTest.xml");
        Transformer transformer = toTransformer("Bean.xsl");

        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.setParameter("entityName", "generated.Audit");
        transformer.transform(source, result);

        kernel.Util.compare(flatten(EXPECTED), flatten(writer.toString()));
    }


    public void test_generation_method() throws Exception {
        DOMSource source = toDataSource("src/test/resources/bean/BeanMethodTest.xml");
        Transformer transformer = toTransformer("Bean.xsl");

        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.setParameter("entityName", "generated.Dividend");
        transformer.transform(source, result);

        kernel.Util.compare(flatten(loadEtalon("src/test/resources/bean/dividendEtalon.txt")),
                            flatten(writer.toString()));
    }


    public void test_generation_method_withExtends()
          throws Exception {
        DOMSource source = toDataSource("src/test/resources/bean/BeanMethodTest_WithExtends.xml");
        Transformer transformer = toTransformer("Bean.xsl");

        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.setParameter("entityName", "generated.Dividend");
        transformer.transform(source, result);

        kernel.Util.compare(flatten(loadEtalon("src/test/resources/bean/dividendEtalon_WithExtends.txt")),
                            flatten(writer.toString()));
    }


    private String loadEtalon(String fileName) throws Exception {
        return FileUtil.loadContent(new File(fileName));
    }


    private String flatten(String str) {
        return kernel.Util.flatten(str);
    }


    private DOMSource toDataSource(final String dataFileName)
          throws IOException, ParserConfigurationException, SAXException {
        return kernel.DomUtil.toDataSource(dataFileName);
    }


    private Transformer toTransformer(final String name)
          throws TransformerConfigurationException {
        return kernel.DomUtil.toTransformer(BeanTest.class.getResourceAsStream(name));
    }
}
