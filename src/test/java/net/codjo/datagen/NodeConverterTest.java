package net.codjo.datagen;
import net.codjo.database.common.api.structure.SqlConstraint;
import net.codjo.database.common.api.structure.SqlField;
import net.codjo.database.common.api.structure.SqlFieldDefinition;
import net.codjo.database.common.api.structure.SqlIndex;
import net.codjo.database.common.api.structure.SqlIndex.Type;
import net.codjo.database.common.api.structure.SqlTable;
import net.codjo.database.common.api.structure.SqlTableDefinition;
import net.codjo.database.common.api.structure.SqlTrigger;
import net.codjo.database.common.api.structure.SqlTrigger.TableLink;
import static net.codjo.database.common.api.structure.SqlTrigger.Type.CHECK_RECORD;
import static net.codjo.database.common.api.structure.SqlTrigger.Type.INSERT;
import static net.codjo.database.common.api.structure.SqlTrigger.Type.UPDATE;
import net.codjo.database.common.api.structure.SqlView;
import static net.codjo.datagen.DatagenXmlUtil.getConstraintNode;
import static net.codjo.datagen.DatagenXmlUtil.getIndexNode;
import static net.codjo.datagen.DatagenXmlUtil.getTableNode;
import static net.codjo.datagen.DatagenXmlUtil.getTriggerDeleteNode;
import static net.codjo.datagen.DatagenXmlUtil.getTriggerInsertNode;
import static net.codjo.datagen.DatagenXmlUtil.getTriggerInsertUpdateNode;
import static net.codjo.datagen.DatagenXmlUtil.getTriggerUpdateNode;
import static net.codjo.datagen.DatagenXmlUtil.getViewNode;
import java.util.List;
import kernel.DomUtil;
import kernel.Util;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
public class NodeConverterTest {
    private NodeConverter nodeConverter;
    private Document source;


    @Before
    public void setUp() throws Exception {
        nodeConverter = new NodeConverter();
        source = DomUtil.toDocument("src/test/resources/net/codjo/datagen/DatagenTest.xml");
    }


    @Test
    public void test_convertNodeToSqlTable() throws Exception {
        SqlTable table = nodeConverter.convertNodeToSqlTable(getTableNode(source, "AP_TOTO"));

        assertEquals("AP_TOTO", table.getName());
        assertFalse(table.isTemporary());
    }


    @Test
    public void test_convertNodeToSqlTableDefinition() throws Exception {
        SqlTableDefinition table = nodeConverter
              .convertNodeToSqlTableDefinition(getTableNode(source, "AP_TOTO"), "yes");

        assertEquals("AP_TOTO", table.getName());

        assertTrue(table.isPkGenerator());
        assertEquals("PORTFOLIO_CODE", table.getPrimaryKeys().get(0));
        assertEquals("DIVIDEND_DATE", table.getPrimaryKeys().get(1));

        List<SqlFieldDefinition> fieldDefinitions = table.getSqlFieldDefinitions();
        assertEquals(5, fieldDefinitions.size());
        SqlFieldDefinition fieldDefinition = fieldDefinitions.get(0);
        assertFieldDefinition(fieldDefinition, "PORTFOLIO_CODE", "6", true, "varchar");
        fieldDefinition = fieldDefinitions.get(1);
        assertFieldDefinition(fieldDefinition, "DIVIDEND_DATE", null, true, "timestamp");
        fieldDefinition = fieldDefinitions.get(2);
        assertFieldDefinition(fieldDefinition, "COMMENT", "6", false, "varchar");
        fieldDefinition = fieldDefinitions.get(3);
        assertFieldDefinition(fieldDefinition, "CREATED_BY", "6", false, "varchar");
        fieldDefinition = fieldDefinitions.get(4);
        assertFieldDefinition(fieldDefinition, "AUTOMATIC_UPDATE", null, true, "bit");
    }


    @Test
    public void test_convertNodeToSqlView() throws Exception {
        SqlView view = nodeConverter
              .convertNodeToSqlView(getViewNode(source, "AP_TOTO", "VU_PTF_IN_DIVIDEND"));

        assertEquals("VU_PTF_IN_DIVIDEND", view.getName());
        assertEquals(Util.flatten("select portfolioCode from AP_DIVIDEND group by portfolioCode"),
                     Util.flatten(view.getBody().trim()));
    }


    @Test
    public void test_convertNodeToSqlIndex_primaryKey() throws Exception {
        SqlIndex index = nodeConverter.convertNodeToSqlIndex(getIndexNode(source, "AP_TOTO", "X1_"));

        assertEquals("X1_AP_TOTO", index.getName());
        assertEquals(Type.PRIMARY_KEY, index.getType());
        assertEquals("AP_TOTO", index.getTable().getName());
        assertEquals(2, index.getFields().size());
        assertEquals("PORTFOLIO_CODE", index.getFields().get(0).getName());
        assertEquals("DIVIDEND_DATE", index.getFields().get(1).getName());
    }


    @Test
    public void test_convertNodeToSqlIndex_primaryKeyClustered() throws Exception {
        SqlIndex index = nodeConverter.convertNodeToSqlIndex(getIndexNode(source, "AP_TUTU", "X1_"));

        assertEquals("X1_AP_TUTU", index.getName());
        assertEquals(Type.PRIMARY_KEY_CLUSTERED, index.getType());
        assertEquals("AP_TUTU", index.getTable().getName());
        assertEquals(1, index.getFields().size());
        assertEquals("DIVIDEND_DATE", index.getFields().get(0).getName());
    }


    @Test
    public void test_convertNodeToSqlIndex() throws Exception {
        SqlIndex index = nodeConverter.convertNodeToSqlIndex(getIndexNode(source, "AP_TOTO", "X2_"));

        assertEquals("X2_AP_TOTO", index.getName());
        assertEquals(Type.NORMAL, index.getType());
        assertEquals("AP_TOTO", index.getTable().getName());
        assertEquals(1, index.getFields().size());
        assertEquals("PORTFOLIO_CODE", index.getFields().get(0).getName());
    }


    @Test
    public void test_convertNodeToSqlIndex_unique() throws Exception {
        SqlIndex index = nodeConverter.convertNodeToSqlIndex(getIndexNode(source, "AP_TOTO", "X3_"));

        assertEquals("X3_AP_TOTO", index.getName());
        assertEquals(Type.UNIQUE, index.getType());
        assertEquals("AP_TOTO", index.getTable().getName());
        assertEquals(1, index.getFields().size());
        assertEquals("DIVIDEND_DATE", index.getFields().get(0).getName());
    }


    @Test
    public void test_convertNodeToSqlIndex_clustered() throws Exception {
        SqlIndex index = nodeConverter.convertNodeToSqlIndex(getIndexNode(source, "AP_TOTO", "X4_"));

        assertEquals("X4_AP_TOTO", index.getName());
        assertEquals(Type.CLUSTERED, index.getType());
        assertEquals("AP_TOTO", index.getTable().getName());
        assertEquals(1, index.getFields().size());
        assertEquals("AUTOMATIC_UPDATE", index.getFields().get(0).getName());
    }


    @Test
    public void test_convertNodeToSqlIndex_uniqueClustered() throws Exception {
        SqlIndex index = nodeConverter.convertNodeToSqlIndex(getIndexNode(source, "AP_TOTO", "X5_"));

        assertEquals("X5_AP_TOTO", index.getName());
        assertEquals(Type.UNIQUE_CLUSTERED, index.getType());
        assertEquals("AP_TOTO", index.getTable().getName());
        assertEquals(2, index.getFields().size());
        assertEquals("PORTFOLIO_CODE", index.getFields().get(0).getName());
        assertEquals("DIVIDEND_DATE", index.getFields().get(1).getName());
    }


    @Test
    public void test_convertNodeToSqlConstraint() throws Exception {
        SqlConstraint constraint = nodeConverter
              .convertNodeToSqlConstraint(getConstraintNode(source, "AP_TOTO", "FK_MERETOTO_TOTO"));

        assertEquals("FK_MERETOTO_TOTO", constraint.getName());
        assertEquals("AP_TOTO", constraint.getAlteredTable().getName());
        assertEquals("AP_MERETOTO", constraint.getReferencedTable().getName());
        assertEquals("PORTFOLIO_CODE", constraint.getLinkedFields().get(0)[0].getName());
        assertEquals("ISIN_CODE", constraint.getLinkedFields().get(0)[1].getName());
        assertEquals("AUTOMATIC_UPDATE", constraint.getLinkedFields().get(1)[0].getName());
        assertEquals("AUTOMATIC_UPDATE", constraint.getLinkedFields().get(1)[1].getName());
    }


    @Test
    public void test_convertNodeToSqlTrigger_delete() throws Exception {
        SqlTrigger trigger = nodeConverter.convertNodeToSqlTrigger(getTriggerDeleteNode(source, "AP_TOTO"));

        assertEquals("TR_AP_TOTO_D", trigger.getName());
        assertEquals("AP_TOTO", trigger.getTable().getName());
        assertEquals("blablabla", trigger.getSqlContent());
        assertEquals(SqlTrigger.Type.DELETE, trigger.getType());
        List<TableLink> links = trigger.getLinks();
        assertEquals(1, links.size());
        TableLink link = links.get(0);
        assertEquals("AP_MERETOTO2", link.getOtherTable().getName());
        assertEquals("PORTFOLIO_CODE", link.getLinkedFields().get(0)[0].getName());
        assertEquals("ISIN_CODE2", link.getLinkedFields().get(0)[1].getName());
    }


    @Test
    public void test_convertNodeToSqlTrigger_insert() throws Exception {
        SqlTrigger trigger = nodeConverter.convertNodeToSqlTrigger(getTriggerInsertNode(source, "AP_TOTO"));

        assertEquals("TR_AP_TOTO_I", trigger.getName());
        assertEquals("AP_TOTO", trigger.getTable().getName());
        assertEquals("-- Additional insert sql code", trigger.getSqlContent());
        assertEquals(INSERT, trigger.getType());
    }


    @Test
    public void test_convertNodeToSqlTrigger_update() throws Exception {
        SqlTrigger trigger = nodeConverter.convertNodeToSqlTrigger(getTriggerUpdateNode(source, "AP_TOTO"));

        assertEquals("TR_AP_TOTO_U", trigger.getName());
        assertEquals("AP_TOTO", trigger.getTable().getName());
        assertEquals("-- Additional update sql code", trigger.getSqlContent());
        assertEquals(UPDATE, trigger.getType());
    }


    @Test
    public void test_convertNodeToSqlTrigger_insertUpdate() throws Exception {
        SqlTrigger trigger = nodeConverter
              .convertNodeToSqlTrigger(getTriggerInsertUpdateNode(source, "AP_TOTO"));

        assertEquals("TR_AP_TOTO_IU", trigger.getName());
        assertEquals("AP_TOTO", trigger.getTable().getName());
        assertEquals(CHECK_RECORD, trigger.getType());
        List<TableLink> links = trigger.getLinks();
        assertEquals(2, links.size());

        TableLink tableLink1 = links.get(0);
        List<SqlField[]> linkedFields = tableLink1.getLinkedFields();
        assertEquals(1, linkedFields.size());
        assertEquals("DATAGEN_SECTION", tableLink1.getOtherTable().getName());
        assertEquals(2, linkedFields.get(0).length);
        assertEquals("SECTION_ID", linkedFields.get(0)[0].getName());
        assertEquals("ID", linkedFields.get(0)[1].getName());

        tableLink1 = links.get(1);
        linkedFields = tableLink1.getLinkedFields();
        assertEquals(1, linkedFields.size());
        assertEquals("AP_MERETOTO", tableLink1.getOtherTable().getName());
        assertEquals(2, linkedFields.get(0).length);
        assertEquals("PORTFOLIO_CODE", linkedFields.get(0)[0].getName());
        assertEquals("ISIN_CODE2", linkedFields.get(0)[1].getName());
    }


    @Test
    public void test_convertNodeToSqlTrigger_tableNameTruncated() throws Exception {
        SqlTrigger trigger = nodeConverter
              .convertNodeToSqlTrigger(getTriggerDeleteNode(source, "AP_MERETOTOTUTUTATATITITETE"));

        assertEquals("TR_AP_MERETOTOTUTUTATATI_D", trigger.getName());
    }


    private void assertFieldDefinition(SqlFieldDefinition fieldDefinition,
                                       String fieldName,
                                       String precision, boolean required, String sqlType) {
        assertEquals(fieldName, fieldDefinition.getName());
        assertEquals(precision, fieldDefinition.getPrecision());
        assertEquals(required, fieldDefinition.isRequired());
        assertEquals(sqlType, fieldDefinition.getType());
        assertEquals(null, fieldDefinition.getCheck());
        assertEquals(null, fieldDefinition.getDefault());
        assertEquals(false, fieldDefinition.isIdentity());
    }
}
