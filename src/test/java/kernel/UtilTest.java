/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package kernel;
import net.codjo.test.common.XmlUtil;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
/**
 *
 */
public class UtilTest extends TestCase {

    public void test_capitalize() throws Exception {
        assertEquals("Bobo", Util.capitalize("bobo"));
        assertEquals("Bobo", Util.capitalize("Bobo"));
    }


    public void test_handlerClass() throws Exception {
        assertEquals("SelectByPkHandler", Util.handlerClassName("selectByPk"));
    }


    public void test_compare() throws Exception {
        assertCompareFailure("je veux faire une comparaison", "je voudrais faire une comparaison",
                             "Comparaison\n" + "\texpected = ...je veux faire une comparaison...\n"
                             + "\tactual   = ...je voudrais faire une comparaison...");

        Util.compare("", "");

        assertCompareFailure("", "je voudrais faire une comparaison",
                             "Comparaison\n" + "\texpected = \n"
                             + "\tactual   = je voudrais faire une comparaison");

        assertCompareFailure("je veux faire une comparaison", "",
                             "Comparaison\n" + "\texpected = ...je veux faire une comparaison...\n"
                             + "\tactual   = ......");
    }


    public void test_replaceProperties() throws Exception {
        Map<String, String> properties;
        properties = new HashMap<String, String>();
        properties.put("previousPeriod", "200512");
        properties.put("nextPeriod", "200601");

        String datagenFile =
              "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
              + "<entities xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
              + "<data> my previous period = ${previousPeriod} </data>"
              + "<data> my next period = ${nextPeriod} </data>" + "</entities>";

        String result = Util.replaceProperties(datagenFile, properties);

        String expected =
              "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
              + "<entities xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
              + "<data> my previous period = 200512 </data>" + "<data> my next period = 200601 </data>"
              + "</entities>";

        assertEquals(expected, result);
    }


    public void test_replacePropertiesMultipleInstances() throws Exception {
        Map<String, String> properties;
        properties = new HashMap<String, String>();
        properties.put("previousPeriod", "200512");

        String datagenFile =
              "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
              + "<entities xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
              + "<data> my previous period = ${previousPeriod} </data>"
              + "<data> another previous period = ${previousPeriod} </data>" + "</entities>";

        String result = Util.replaceProperties(datagenFile, properties);

        String expected =
              "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
              + "<entities xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
              + "<data> my previous period = 200512 </data>"
              + "<data> another previous period = 200512 </data>" + "</entities>";

        assertEquals(expected, result);
    }


    public void test_replacePropertiesNotExist() throws Exception {
        Map<String, String> properties;
        properties = new HashMap<String, String>();
        properties.put("previousPeriod", "200512");

        String datagenFile =
              "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
              + "<entities xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
              + "<data> my previous period = ${previousPeriod} </data>"
              + "<data> my next period = ${nextPeriod} </data>" + "</entities>";

        String result = Util.replaceProperties(datagenFile, properties);

        String expected =
              "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
              + "<entities xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
              + "<data> my previous period = 200512 </data>" + "<data> my next period = ${nextPeriod} </data>"
              + "</entities>";

        assertEquals(expected, result);
    }


    private void assertCompareFailure(String firstElement, String secondElement, String expectedError) {
        try {
            Util.compare(firstElement, secondElement);
            fail();
        }
        catch (AssertionFailedError error) {
            assertEquals(expectedError, error.getMessage());
        }
    }


    public void test_timeMillisToString() throws Exception {
        assertEquals(Util.timeMillisToString(86400000 + 5000), "1j 5s");
        assertEquals(Util.timeMillisToString(86400000 + 999), "1j");
        assertEquals(Util.timeMillisToString(86400000 + 1000), "1j 1s");
        assertEquals(Util.timeMillisToString(86400000 + 900000), "1j 15mn");
        assertEquals(Util.timeMillisToString(86400000 + 900000 + 15000), "1j 15mn 15s");
        assertEquals(Util.timeMillisToString(86400000 + 18000000), "1j 5h");
        assertEquals(Util.timeMillisToString(666), "666ms");
    }


    public void test_replaceSQLNames() throws Exception {
        String depart = "test ${SQLName:replaceSqlName} test ${SQLName:test}";
        String expected = "test REPLACE_SQL_NAME test TEST";
        String actual = Util.replaceSQLNames(depart);
        assertEquals(expected, actual);
    }


    public void test_parseVariables() throws Exception {
        String depart =
              "<entity name='net.codjo.mint.ref.Country' table='REF_COUNTRY'>"
              + "    <feature>                                                 "
              + "        <handler-sql id='selectAll${className}WithClosedDate'>"
              + "            <attributes>                                      "
              + "                <name>refCode</name>                          "
              + "                <name>refLabel</name>                         "
              + "            </attributes>                                     "
              + "            <query>                                           "
              + "  select ${SQLAttributes} from ${table}                           "
              + "  where ${SQLName:closedDate} &gt; ? OR ${SQLName:closedDate} = '9999-12-31' "
              + "            </query>                                          "
              + "        </handler-sql>                                        "
              + "    </feature>                                                "
              + "</entity>                                                     ";

        String expected =
              "        <handler-sql id='selectAllCountryWithClosedDate'>"
              + "            <attributes>                                      "
              + "                <name>refCode</name>                          "
              + "                <name>refLabel</name>                         "
              + "            </attributes>                                     "
              + "            <query>                                           "
              + "  select REF_CODE, REF_LABEL from REF_COUNTRY                           "
              + "  where CLOSED_DATE &gt; ? OR CLOSED_DATE = '9999-12-31' "
              + "            </query>                                          "
              + "        </handler-sql>                                        ";

        assertParseVariable(expected, depart, "handler-sql");
    }


    public void test_parseVariablesWithNoQuery() throws Exception {
        String depart =
              "<entity name='net.codjo.mint.ref.Country' table='REF_COUNTRY'>"
              + "    <feature>                                                 "
              + "        <handler-select id='selectAll${className}' type='All'/> "
              + "    </feature>                                                "
              + "</entity>                                                     ";

        String expected = "<handler-select id='selectAllCountry' type='All'/> ";

        assertParseVariable(expected, depart, "handler-select");
    }


    public void test_parseVariables_notSql() throws Exception {
        String depart =
              "<entity name='net.codjo.mint.ref.Country' table='REF_COUNTRY'>"
              + "    <feature>                                                 "
              + "        <handler-sql id='selectAll${className}WithClosedDate'>"
              + "            <attributes>                                      "
              + "                <name>refCode</name>                          "
              + "                <name>refLabel</name>                         "
              + "                <name>uneChaineDeCaracteresVraimentTresLongue</name>  "
              + "            </attributes>                                     "
              + "            <query>select * from truc</query>                 "
              + "        </handler-sql>                                        "
              + "    </feature>                                                "
              + "</entity>                                                     ";

        String expected =
              "        <handler-sql id='selectAllCountryWithClosedDate'>"
              + "            <attributes>                                      "
              + "                <name>refCode</name>                          "
              + "                <name>refLabel</name>                         "
              + "                <name>uneChaineDeCaracteresVraimentTresLongue</name>  "
              + "            </attributes>                                     "
              + "            <query>select * from truc</query>                 "
              + "        </handler-sql>                                        ";

        assertParseVariable(expected, depart, "handler-sql");
    }


    public void test_parseVariables_useQueryFactory() throws Exception {
        String depart =
              "<entity name='net.codjo.mint.ref.Country' table='REF_COUNTRY'>    "
              + "    <feature>                                                 "
              + "        <handler-sql id='selectAll'>                          "
              + "            <attributes>                                      "
              + "                <name>uneChaineDeCaracteresVraimentTresLongue</name>  "
              + "            </attributes>                                     "
              + "            <query factory='net.codjo.truc.QueryBidule'/>       "
              + "        </handler-sql>                                        "
              + "    </feature>                                                "
              + "</entity>                                                     ";

        String expected =
              "        <handler-sql id='selectAll'>                            "
              + "            <attributes>                                      "
              + "                <name>uneChaineDeCaracteresVraimentTresLongue</name>  "
              + "            </attributes>                                     "
              + "            <query factory='net.codjo.truc.QueryBidule'/>       "
              + "        </handler-sql>                                        ";

        assertParseVariable(expected, depart, "handler-sql");
    }


    public void test_nullVariable() throws Exception {
        String result = Util.parseVariablesString("selectAll${className}WithClosedDate", null, null, null);
        String expected = "selectAll${className}WithClosedDate";
        assertEquals(expected, result);
    }


    public void test_specialProperties() throws Exception {
        assertTrue(Util.isSpecialProperties("className"));
        assertTrue(Util.isSpecialProperties("table"));
        assertTrue(Util.isSpecialProperties("SQLAttributes"));
        assertTrue(Util.isSpecialProperties("SQLName:xxxxYyyyZzzz"));
        assertFalse(Util.isSpecialProperties("xxxxx"));
    }


    private void assertParseVariable(String expected, String depart, String handlerType)
          throws ParserConfigurationException, IOException, SAXException, TransformerException {
        Document doc = DomUtil.toDocument(new StringReader(depart));
        Node entityNode = DomUtil.getNode(doc, "/entity");
        Node handlerNode = DomUtil.getNode(doc, "/entity/feature/" + handlerType);

        String actual = nodeToXML(Util.parseVariables(entityNode, handlerNode));

        XmlUtil.assertEquals(expected, actual);
    }


    private String nodeToXML(Node node) throws TransformerException {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        DOMSource dSource = new DOMSource(node);
        StringWriter sw = new StringWriter();
        StreamResult sr = new StreamResult(sw);
        transformer.transform(dSource, sr);
        StringWriter anotherSW = (StringWriter)sr.getWriter();
        StringBuffer sBuffer = anotherSW.getBuffer();
        return sBuffer.toString();
    }
}
