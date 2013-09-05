/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package handler.sql;
import handler.HandlerTestCase;
import java.io.StringWriter;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import kernel.DomUtil;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 */
public class SqlHandlerTest extends HandlerTestCase {
    @Override
    protected String getXslFileName() {
        return "SqlHandler.xsl";
    }


    @Override
    protected String file(String name) {
        return "src/test/resources/handler/sql/" + name;
    }


    @Test
    public void test_generation() throws Exception {
        assertTransformation(transformer,
                             file("SqlHandlerTest.xml"),
                             file("SqlHandlerTestEtalon.txt"),
                             "!!mode transactionnel force :neoSelect");
    }


    @Test
    public void test_generation_noPk() throws Exception {
        assertTransformation(transformer,
                             file("SqlHandler_noPkTest.xml"),
                             file("SqlHandler_noPkTestEtalon.txt"));
    }


    @Test
    public void test_generation_class() throws Exception {
        assertTransformation(transformer,
                             file("SqlHandler_factoryTest.xml"),
                             file("SqlHandler_factoryTestEtalon.txt"));
    }
    

    @Test
    public void test_generation_statement_class() throws Exception {
        assertTransformation(transformer,
                             file("SqlHandler_factoryStatementTest.xml"),
                             file("SqlHandler_factoryStatementTestEtalon.txt"));
    }


    @Test
    public void test_factoryWithHandler() throws Exception {
        assertTransformation(transformer,
                             file("SqlHandler_factoryWithHandlerTest.xml"),
                             file("SqlHandler_factoryWithHandlerTestEtalon.txt"));
    }


    @Test
    public void test_generation_terminate() throws Exception {
        try {
            transformUsing(transformer, file("SqlHandlerTestTerminate.xml"));
            fail("la transaction a true avec le select aurait du être detecté");
        }
        catch (TransformerException cause) {
            assertThat(cause.getMessage(), equalTo("La feuille de style a provoqué l'arrêt"));
        }
    }


    @Test
    public void test_generation_warning_inTx() throws Exception {
        WarningCollector warningCollector = new WarningCollector();

        Document doc = DomUtil.toDocument(file("SqlHandlerTestTerminate.xml"));
        Node queryNode = getQueryNode(doc);
        String warnMsg = "Les requêtes de type select doivent se faire hors transaction !";

        queryNode.setNodeValue("select * from AP_TRUC");
        assertWarning("handler-sql[id='neoSelect'] - " + warnMsg, warningCollector, doc);

        // Supprimé car on ne test plus le mode transaction pour les proc stock
        //        queryNode.setNodeValue("... sp_Select ...");
        //        assertWarning("handler-sql[id='neoSelect'] - " + warnMsg,
        //            warningCollector, doc);
        queryNode.setNodeValue("update TOTO ...");
        assertWarning("", warningCollector, doc);
    }


    @Test
    public void test_generation_warning_inTxForModification() throws Exception {
        Document document = DomUtil.toDocument(file("SqlHandlerForModificationTest.xml"));
        WarningCollector warningCollector = new WarningCollector();

        assertWarning("", warningCollector, document, "DeleteHandler");
        assertWarning("", warningCollector, document, "UpdateHandler");
        assertWarning("", warningCollector, document, "InsertHandler");
        assertWarning("", warningCollector, document, "ExecHandler");

        assertWarning(
              "handler-sql[id='SelectHandler'] - Les requêtes de type select doivent se faire hors transaction !",
              warningCollector, document, "SelectHandler");

        assertWarning("", warningCollector, document, "DeleteWithSelectHandler");
        assertWarning("", warningCollector, document, "UpdateWithSelectHandler");
        assertWarning("", warningCollector, document, "InsertWithSelectHandler");
        assertWarning("", warningCollector, document, "ExecWithSelectHandler");
        assertWarning("", warningCollector, document, "ExecNoTransactionWithSelectHandler");
    }


    @Test
    public void test_generation_warning_notInTx() throws Exception {
        Document doc = DomUtil.toDocument(file("SqlHandlerTestTerminate.xml"));
        WarningCollector warningCollector = new WarningCollector();
        Node queryNode = getQueryNode(doc);
        getTxNode(doc).setNodeValue("false");
        String warnMsg = "Les requêtes de modification doivent se faire en transaction !";

        queryNode.setNodeValue("update Toto");
        assertWarning("handler-sql[id='neoSelect'] - " + warnMsg, warningCollector, doc);

        // Supprimé car on ne test plus le mode transaction pour les proc stock
        //        queryNode.setNodeValue("... sp_Duplicate ...");
        //        assertWarning("handler-sql[id='neoSelect'] - " + warnMsg,
        //            warningCollector, doc);
        queryNode.setNodeValue("select TOTO ...");
        assertWarning("", warningCollector, doc);
    }


    @Test
    public void test_generation_withStructure() throws Exception {
        assertTransformation(transformer,
                             file("SqlHandler_withStructure.xml"),
                             file("SqlHandler_withStructure_etalon.txt"));
    }


    @Test
    public void test_generation_quarantine() throws Exception {
        assertTransformation(transformer,
                             file("SqlHandler_quarantineTest.xml"),
                             file("SqlHandler_quarantineTest_etalon.txt"));
    }


    private Node getQueryNode(Document doc) throws TransformerException {
        return getQueryNode(doc, "neoSelect");
    }


    private Node getQueryNode(Document doc, String handlerName) throws TransformerException {
        Node aNode = DomUtil.getNode(doc,
                                     "data/entity[@name='generated.Dividend']"
                                     + "/feature/handler-sql[@id='" + handlerName + "']/query");

        return aNode.getFirstChild();
    }


    private Node getTxNode(Document doc) throws TransformerException {
        Node aNode = DomUtil.getNode(doc,
                                     "data/entity[@name='generated.Dividend']"
                                     + "/feature/handler-sql[@id='neoSelect']/@transaction");

        return aNode.getFirstChild();
    }


    private void assertWarning(String msg,
                               WarningCollector warningCollector,
                               Document document) throws TransformerException {
        assertWarning(msg, warningCollector, document, null);
    }


    private void assertWarning(String msg, WarningCollector warningCollector,
                               Document document, String handlerId) throws TransformerException {
        warningCollector.setWarning("");
        if (handlerId != null) {
            transformer.setParameter("handlerId", handlerId);
        }
        transformer.setErrorListener(warningCollector);
        transformer.transform(new DOMSource(document),
                              new StreamResult(new StringWriter()));
        assertThat(msg.trim(), is(warningCollector.getWarning().trim()));
    }
}
