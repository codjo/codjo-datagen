package net.codjo.datagen;
import net.codjo.database.common.api.structure.SqlConstraint;
import net.codjo.database.common.api.structure.SqlField;
import static net.codjo.database.common.api.structure.SqlField.fields;
import net.codjo.database.common.api.structure.SqlFieldDefinition;
import net.codjo.database.common.api.structure.SqlIndex;
import net.codjo.database.common.api.structure.SqlIndex.Type;
import net.codjo.database.common.api.structure.SqlTable;
import static net.codjo.database.common.api.structure.SqlTable.table;
import net.codjo.database.common.api.structure.SqlTableDefinition;
import net.codjo.database.common.api.structure.SqlTrigger;
import net.codjo.database.common.api.structure.SqlView;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.TransformerException;
import kernel.DomUtil;
import static kernel.DomUtil.getAttributeValue;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sql.Util;
public class NodeConverter {

    public SqlTable convertNodeToSqlTable(Node node) throws TransformerException {
        return table(getAttributeValue(node, "table"));
    }


    public SqlTableDefinition convertNodeToSqlTableDefinition(Node node, final String pkGenerator)
          throws TransformerException {
        SqlTableDefinition tableDefinition = new SqlTableDefinition(getAttributeValue(node, "table"));

        if ("yes".equals(pkGenerator)) {
            tableDefinition.setPkGenerator(true);
        }
        else {
            tableDefinition.setPkGenerator(false);
        }

        NodeList primaryKeys = DomUtil.getNodeList(node, "primary-key/field");
        List<String> pkList = new ArrayList<String>();
        for (int i = 0; i < primaryKeys.getLength(); i++) {
            Node primaryKeyNode = primaryKeys.item(i);
            String fieldName = Util.toSqlName(getAttributeValue(primaryKeyNode, "name"));
            pkList.add(fieldName);
        }

        tableDefinition.setPrimaryKeys(pkList);

        NodeList fields = DomUtil.getNodeList(node, "properties/field");
        List<SqlFieldDefinition> fieldDefinitions = new ArrayList<SqlFieldDefinition>();
        for (int i = 0; i < fields.getLength(); i++) {
            Node fieldNode = fields.item(i);
            if (DomUtil.hasAttribute(fieldNode, "type")) {
                String fieldName = Util.toSqlName(getAttributeValue(fieldNode, "name"));
                SqlFieldDefinition sqlFieldDefinition = new SqlFieldDefinition(Util.toSqlName(fieldName));

                Node sqlNode = DomUtil.getNode(fieldNode, "sql");
                sqlFieldDefinition.setType(getStringValueFor(sqlNode, "type"));
                sqlFieldDefinition.setPrecision(getStringValueFor(sqlNode, "precision"));
                sqlFieldDefinition.setRequired(getBooleanValueFor(sqlNode, "required"));
                sqlFieldDefinition.setDefault(getStringValueFor(sqlNode, "default"));
                sqlFieldDefinition.setIdentity(getBooleanValueFor(sqlNode, "identity"));
                sqlFieldDefinition.setCheck(getStringValueFor(sqlNode, "in"));
                fieldDefinitions.add(sqlFieldDefinition);
            }
            else if (DomUtil.hasAttribute(fieldNode, "structure")) {
                getStructureDefinition(node, getAttributeValue(fieldNode, "structure"), fieldDefinitions);
            }
        }
        tableDefinition.setSqlFieldDefinitions(fieldDefinitions);

        return tableDefinition;
    }


    private void getStructureDefinition(Node node,
                                        String structureName,
                                        List<SqlFieldDefinition> fieldDefinitions)
          throws TransformerException {
        NodeList structureFields = DomUtil
              .getNodeList(node, "//data/structure[@name='" + structureName + "']/properties/field");
        for (int i = 0; i < structureFields.getLength(); i++) {
            Node fieldNode = structureFields.item(i);
            String fieldName = Util.toSqlName(getAttributeValue(fieldNode, "name"));
            SqlFieldDefinition sqlFieldDefinition = new SqlFieldDefinition(Util.toSqlName(fieldName));

            Node sqlNode = DomUtil.getNode(fieldNode, "sql");
            sqlFieldDefinition.setType(getStringValueFor(sqlNode, "type"));
            sqlFieldDefinition.setPrecision(getStringValueFor(sqlNode, "precision"));
            sqlFieldDefinition.setRequired(getBooleanValueFor(sqlNode, "required"));
            sqlFieldDefinition.setDefault(getStringValueFor(sqlNode, "default"));
            sqlFieldDefinition.setIdentity(getBooleanValueFor(sqlNode, "identity"));
            sqlFieldDefinition.setCheck(getStringValueFor(sqlNode, "in"));
            fieldDefinitions.add(sqlFieldDefinition);
        }
    }


    private String getStringValueFor(Node node, String attribute) {
        if (!DomUtil.hasAttribute(node, attribute)) {
            return null;
        }
        return getAttributeValue(node, attribute);
    }


    private boolean getBooleanValueFor(Node node, String attribute) {
        return DomUtil.hasAttribute(node, attribute) && Boolean
              .valueOf(getAttributeValue(node, attribute));
    }


    public SqlView convertNodeToSqlView(Node node) throws TransformerException {
        return SqlView.view(getAttributeValue(node, "id"), node.getTextContent());
    }


    public SqlIndex convertNodeToSqlIndex(Node node) throws TransformerException {
        String tableName = getAttributeValue(node, "../../..", "table");
        String indexName = getAttributeValue(node, "name-prefix");
        SqlIndex index = SqlIndex.index(indexName + tableName, table(tableName));

        String type = getAttributeValue(node, "type");
        String unique = "false";
        if (DomUtil.hasAttribute(node, "unique")) {
            unique = getAttributeValue(node, "unique");
        }
        String clustered = "false";
        if (DomUtil.hasAttribute(node, "clustered")) {
            clustered = getAttributeValue(node, "clustered");
        }
        if ("primary-key".equals(type)) {
            if ("true".equals(clustered)) {
                index.setType(Type.PRIMARY_KEY_CLUSTERED);
            }
            else {
                index.setType(Type.PRIMARY_KEY);
            }
            fillSqlFields(index, DomUtil.getNode(node, "../../../primary-key"));
        }
        else if ("index".equals(type)) {
            if ("true".equals(unique) && "true".equals(clustered)) {
                index.setType(Type.UNIQUE_CLUSTERED);
            }
            else if ("true".equals(unique)) {
                index.setType(Type.UNIQUE);
            }
            else if ("true".equals(clustered)) {
                index.setType(Type.CLUSTERED);
            }
            else {
                index.setType(Type.NORMAL);
            }
            fillSqlFields(index, node);
        }
        index.setTable(SqlTable.table(tableName));
        return index;
    }


    public SqlConstraint convertNodeToSqlConstraint(Node node) throws TransformerException {
        String referencedJavaName = getAttributeValue(node, "table");
        String referencedSqlName =
              getAttributeValue(node, "//data/entity[@name='" + referencedJavaName + "']", "table");
        SqlConstraint constraint = SqlConstraint.foreignKey(getAttributeValue(node, "id"), null);
        constraint.setAlteredTable(table(getAttributeValue(node, "../..", "table")));
        constraint.setReferencedTable(table(referencedSqlName));
        fillSqlFields(constraint, node);
        return constraint;
    }


    public SqlTrigger convertNodeToSqlTrigger(Node node) throws TransformerException {
        String tableName = getAttributeValue(node, "../..", "table");
        if ("trigger-delete".equals(node.getNodeName())) {
            return buildDeleteTrigger(tableName, node);
        }
        else if ("trigger-insert".equals(node.getNodeName())) {
            return buildInsertTrigger(tableName, node);
        }
        else if ("trigger-update".equals(node.getNodeName())) {
            return buildUpdateTrigger(tableName, node);
        }
        else if ("trigger-iu".equals(node.getNodeName())) {
            return buildInsertUpdateTrigger(tableName, node);
        }
        else {
            throw new RuntimeException("Invalid node");
        }
    }


    private SqlTrigger buildDeleteTrigger(String tableName, Node node) throws TransformerException {
        SqlTrigger trigger = SqlTrigger.deleteTrigger(buildTriggerName(tableName, "_D"),
                                                      table(tableName),
                                                      buildSqlContent(node));
        addTableLink(node, trigger, "cascade");
        return trigger;
    }


    private SqlTrigger buildInsertTrigger(String tableName, Node node) throws TransformerException {
        return SqlTrigger.insertTrigger(buildTriggerName(tableName, "_I"),
                                        table(tableName),
                                        buildSqlContent(node));
    }


    private SqlTrigger buildUpdateTrigger(String tableName, Node node) throws TransformerException {
        return SqlTrigger.updateTrigger(buildTriggerName(tableName, "_U"),
                                        table(tableName),
                                        buildSqlContent(node));
    }


    private SqlTrigger buildInsertUpdateTrigger(String tableName, Node node) throws TransformerException {
        SqlTrigger trigger = SqlTrigger
              .checkRecordTrigger(buildTriggerName(tableName, "_IU"), table(tableName));

        addTableLink(node, trigger, "check-record-exist");
        return trigger;
    }


    private String buildTriggerName(String tableName, String suffix) {
        return "TR_"
               + tableName.substring(0, Math.min(tableName.length(), 21))
               + suffix;
    }


    private String buildSqlContent(Node node) throws TransformerException {
        StringBuilder additionnalSql = new StringBuilder();
        NodeList sqlList = DomUtil.getNodeList(node, "sql");
        for (int i = 0; i < sqlList.getLength(); i++) {
            Node sqlNode = sqlList.item(i);
            additionnalSql.append(sqlNode.getTextContent());
        }
        return additionnalSql.toString();
    }


    private void fillSqlFields(SqlIndex index, Node node) throws TransformerException {
        NodeList primaryKeys = DomUtil.getNodeList(node, "field");
        for (int i = 0; i < primaryKeys.getLength(); i++) {
            Node primaryKeyNode = primaryKeys.item(i);
            String fieldName = getAttributeValue(primaryKeyNode, "name");
            index.addField(SqlField.fieldName(Util.toSqlName(fieldName)));
        }
    }


    private void fillSqlFields(SqlConstraint constraint, Node node) throws TransformerException {
        NodeList fields = DomUtil.getNodeList(node, "field");
        for (int i = 0; i < fields.getLength(); i++) {
            Node field = fields.item(i);
            String fromField = Util.toSqlName(getAttributeValue(field, "from"));
            String toField = Util.toSqlName(getAttributeValue(field, "to"));
            constraint.addLinkedFields(SqlField.fieldName(fromField), SqlField.fieldName(toField));
        }
    }


    private void addTableLink(Node node, SqlTrigger trigger, String attribute) throws TransformerException {
        NodeList nodes = DomUtil.getNodeList(node, attribute);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node linkNode = nodes.item(i);
            String referencedJavaName = getAttributeValue(linkNode, "entity");
            String referencedSqlName = getAttributeValue(linkNode,
                                                         "//data/entity[@name='" + referencedJavaName + "']",
                                                         "table");

            Node key = DomUtil.getNode(linkNode, "key");
            String fromField = Util.toSqlName(getAttributeValue(key, "from"));
            String toField = Util.toSqlName(getAttributeValue(key, "to"));

            trigger.addTableLink(table(referencedSqlName), fields(fromField, toField));
        }
    }
}
