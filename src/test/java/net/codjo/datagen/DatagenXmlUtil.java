package net.codjo.datagen;
import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
public class DatagenXmlUtil {

    private DatagenXmlUtil() {
    }


    public static Node getTableNode(Document source, String tableName) throws TransformerException {
        return XPathAPI.selectSingleNode(source, "//data/entity[@table='" + tableName + "']");
    }


    public static Node getViewNode(Document source, String tableName, String viewName)
          throws TransformerException {
        return XPathAPI
              .selectSingleNode(getTableNode(source, tableName), "feature/view[@id='" + viewName + "']");
    }


    public static Node getIndexNode(Document source, String tableName, String indexPrefix)
          throws TransformerException {
        return XPathAPI.selectSingleNode(getTableNode(source, tableName),
                                         "feature/sql-index/idx[@name-prefix='" + indexPrefix + "']");
    }


    public static Node getConstraintNode(Document source, String tableName, String indexName)
          throws TransformerException {
        return XPathAPI.selectSingleNode(getTableNode(source, tableName),
                                         "foreign-keys/link[@id='" + indexName + "']");
    }


    public static Node getTriggerDeleteNode(Document source, String tableName) throws TransformerException {
        return XPathAPI.selectSingleNode(getTableNode(source, tableName), "feature/trigger-delete");
    }


    public static Node getTriggerInsertNode(Document source, String tableName) throws TransformerException {
        return XPathAPI.selectSingleNode(getTableNode(source, tableName), "feature/trigger-insert");
    }


    public static Node getTriggerUpdateNode(Document source, String tableName) throws TransformerException {
        return XPathAPI.selectSingleNode(getTableNode(source, tableName), "feature/trigger-update");
    }


    public static Node getTriggerInsertUpdateNode(Document source, String tableName)
          throws TransformerException {
        return XPathAPI.selectSingleNode(getTableNode(source, tableName), "feature/trigger-iu");
    }
}
