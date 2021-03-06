package net.codjo.datagen;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import kernel.DomUtil;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.DatabaseScriptHelper;
import org.w3c.dom.Node;
import sql.Util;
public class DatabaseScriptHelperXslAdapter extends XslAdapter {
    private final DatabaseScriptHelper databaseScriptHelper;
    private final NodeConverter nodeConverter = new NodeConverter();


    public DatabaseScriptHelperXslAdapter() {
        databaseScriptHelper = new DatabaseFactory().createDatabaseScriptHelper();

        databaseScriptHelper.setLegacyMode(sql.Util.legacyMode);
        databaseScriptHelper.setLegacyPrefix(sql.Util.legacyPrefix);
    }


    public DatabaseScriptHelperXslAdapter(DatabaseScriptHelper databaseScriptHelper) {
        this.databaseScriptHelper = databaseScriptHelper;
    }


    public Node getQueryDelimiter() throws ParserConfigurationException {
        return createTextNode(databaseScriptHelper.getQueryDelimiter());
    }


    public Node buildDropTableScript(Node node)
          throws TransformerException, ParserConfigurationException {
        return createTextNode(
              databaseScriptHelper.buildDropTableScript(
                    nodeConverter.convertNodeToSqlTable(node)));
    }


    public Node buildCreateTableScript(Node node, String pkGenerator)
          throws TransformerException, ParserConfigurationException {
        return createTextNode(
              databaseScriptHelper.buildCreateTableScript(
                    nodeConverter.convertNodeToSqlTableDefinition(node, pkGenerator)));
    }


    public Node buildLogTableCreationScript(Node node)
          throws ParserConfigurationException, TransformerException {
        return createTextNode(
              databaseScriptHelper.buildLogTableCreationScript(
                    nodeConverter.convertNodeToSqlTable(node)));
    }


    public Node buildCreateViewScript(Node node)
          throws TransformerException, ParserConfigurationException {
        return createTextNode(
              databaseScriptHelper.buildCreateViewScript(
                    nodeConverter.convertNodeToSqlView(node)));
    }


    public Node buildDropIndexScript(Node node)
          throws ParserConfigurationException, TransformerException {
        return createTextNode(
              databaseScriptHelper.buildDropIndexScript(
                    nodeConverter.convertNodeToSqlIndex(node)));
    }


    public Node buildCreateIndexScript(Node node)
          throws ParserConfigurationException, TransformerException {
        return createTextNode(
              databaseScriptHelper.buildCreateIndexScript(
                    nodeConverter.convertNodeToSqlIndex(node)));
    }


    public Node buildLogIndexCreationScript(Node node)
          throws ParserConfigurationException, TransformerException {
        return createTextNode(
              databaseScriptHelper.buildLogIndexCreationScript(
                    nodeConverter.convertNodeToSqlIndex(node)));
    }


    public Node buildDropConstraintScript(Node node)
          throws ParserConfigurationException, TransformerException {
        return createTextNode(
              databaseScriptHelper.buildDropConstraintScript(
                    nodeConverter.convertNodeToSqlConstraint(node)));
    }


    public Node buildCreateConstraintScript(Node node)
          throws ParserConfigurationException, TransformerException {
        return createTextNode(
              databaseScriptHelper.buildCreateConstraintScript(
                    nodeConverter.convertNodeToSqlConstraint(node)));
    }


    public Node buildLogConstraintCreationScript(Node node)
          throws ParserConfigurationException, TransformerException {
        return createTextNode(
              databaseScriptHelper.buildLogConstraintCreationScript(
                    nodeConverter.convertNodeToSqlConstraint(node)));
    }


    public Node buildCustomScript(String customScriptName, Node node)
          throws ParserConfigurationException, TransformerException {
        String script;
        if ("createGap".equals(customScriptName) || "logGapCreation".equals(customScriptName)) {
            String gap = DomUtil.getAttributeValue(node, "feature/sql[@gap]", "gap");
            script = databaseScriptHelper
                  .buildCustomScript(customScriptName,
                                     nodeConverter.convertNodeToSqlTable(node),
                                     gap);
        }
        else if ("createSequence".equals(customScriptName)) {
            script = databaseScriptHelper.buildCustomScript(customScriptName,
                                                            nodeConverter.convertNodeToSqlTable(node));
        }
        else if ("createTriggerForSequence".equals(customScriptName)) {
            String key = DomUtil.getAttributeValue(node, "properties/field[sql/@identity]", "name");
            script = databaseScriptHelper.buildCustomScript(customScriptName,
                                                            nodeConverter.convertNodeToSqlTable(node), Util.toSqlName(key));
        }
        else {
            script = databaseScriptHelper.buildCustomScript(customScriptName);
        }
        return createTextNode(script);
    }


    public Node buildCreateTriggerScript(Node node)
          throws ParserConfigurationException, TransformerException {
        return createTextNode(
              databaseScriptHelper.buildCreateTriggerScript(nodeConverter.convertNodeToSqlTrigger(node)));
    }


    protected NodeConverter getNodeConverter() {
        return nodeConverter;
    }
}
