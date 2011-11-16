package net.codjo.datagen;
import net.codjo.database.common.api.structure.SqlConstraint;
import net.codjo.database.common.api.structure.SqlIndex;
import net.codjo.database.common.api.structure.SqlTrigger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;
public class DatabaseScriptHelperXslAdapterMock extends DatabaseScriptHelperXslAdapter {

    @Override
    public Node getQueryDelimiter() throws ParserConfigurationException {
        return createTextNode("${queryDelimiter}");
    }


    @Override
    public Node buildDropTableScript(Node node) throws TransformerException, ParserConfigurationException {
        return createTextNode("${dropTableScript(" + tableToString(node) + ")}");
    }


    @Override
    public Node buildCreateTableScript(Node node, String pkGenerator)
          throws TransformerException, ParserConfigurationException {
        return createTextNode("${createTableScript(" + tableToString(node) + ")}");
    }


    @Override
    public Node buildLogTableCreationScript(Node node)
          throws TransformerException, ParserConfigurationException {
        return createTextNode("${logTableCreationScript(" + tableToString(node) + ")}");
    }


    @Override
    public Node buildCreateViewScript(Node node) throws TransformerException, ParserConfigurationException {
        return createTextNode("${createViewScript(" + viewToString(node) + ")}");
    }


    @Override
    public Node buildDropIndexScript(Node node) throws ParserConfigurationException, TransformerException {
        return createTextNode("${dropIndexScript(" + indexToString(node) + ")}");
    }


    @Override
    public Node buildCreateIndexScript(Node node) throws ParserConfigurationException, TransformerException {
        return createTextNode("${createIndexScript(" + indexToString(node) + ")}");
    }


    @Override
    public Node buildLogIndexCreationScript(Node node)
          throws ParserConfigurationException, TransformerException {
        return createTextNode("${logIndexCreationScript(" + indexToString(node) + ")}");
    }


    @Override
    public Node buildDropConstraintScript(Node node)
          throws ParserConfigurationException, TransformerException {
        return createTextNode("${dropConstraintScript(" + constraintToString(node) + ")}");
    }


    @Override
    public Node buildCreateConstraintScript(Node node)
          throws ParserConfigurationException, TransformerException {
        return createTextNode("${createConstraintScript(" + constraintToString(node) + ")}");
    }


    @Override
    public Node buildLogConstraintCreationScript(Node node)
          throws ParserConfigurationException, TransformerException {
        return createTextNode("${logConstraintCreationScript(" + constraintToString(node) + ")}");
    }


    @Override
    public Node buildCustomScript(String customScriptName, Node node)
          throws ParserConfigurationException, TransformerException {
        return createTextNode("${customScript(" + customScriptName + ")}");
    }


    @Override
    public Node buildCreateTriggerScript(Node node)
          throws ParserConfigurationException, TransformerException {
        return createTextNode("${createTriggerScript(" + triggerToString(node) + ")}");
    }


    private String tableToString(Node node) throws TransformerException {
        return getNodeConverter().convertNodeToSqlTable(node).getName();
    }


    private String viewToString(Node node) throws TransformerException {
        return getNodeConverter().convertNodeToSqlView(node).getName();
    }


    private String indexToString(Node node) throws TransformerException {
        SqlIndex index = getNodeConverter().convertNodeToSqlIndex(node);
        return index.getTable().getName() + "." + index.getName();
    }


    private String constraintToString(Node node) throws TransformerException {
        SqlConstraint constraint = getNodeConverter().convertNodeToSqlConstraint(node);
        return constraint.getAlteredTable().getName() + "." + constraint.getName();
    }


    private String triggerToString(Node node) throws TransformerException {
        SqlTrigger trigger = getNodeConverter().convertNodeToSqlTrigger(node);
        return trigger.getTable().getName() + "." + trigger.getName();
    }
}
