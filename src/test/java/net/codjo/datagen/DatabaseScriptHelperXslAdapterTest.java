package net.codjo.datagen;
import kernel.DomUtil;
import net.codjo.database.common.api.DatabaseScriptHelper;
import net.codjo.database.common.api.structure.SqlConstraint;
import net.codjo.database.common.api.structure.SqlIndex;
import net.codjo.database.common.api.structure.SqlTable;
import net.codjo.database.common.api.structure.SqlTableDefinition;
import net.codjo.database.common.api.structure.SqlTrigger;
import net.codjo.database.common.api.structure.SqlView;
import net.codjo.test.common.LogString;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import static net.codjo.datagen.DatagenXmlUtil.getConstraintNode;
import static net.codjo.datagen.DatagenXmlUtil.getIndexNode;
import static net.codjo.datagen.DatagenXmlUtil.getTableNode;
import static net.codjo.datagen.DatagenXmlUtil.getTriggerDeleteNode;
import static net.codjo.datagen.DatagenXmlUtil.getTriggerInsertNode;
import static net.codjo.datagen.DatagenXmlUtil.getTriggerInsertUpdateNode;
import static net.codjo.datagen.DatagenXmlUtil.getTriggerUpdateNode;
import static net.codjo.datagen.DatagenXmlUtil.getViewNode;
public class DatabaseScriptHelperXslAdapterTest {
    private DatabaseScriptHelperXslAdapter databaseScriptHelperXslAdapter;
    private Document source;
    private LogString logString;


    @Before
    public void setUp() throws Exception {
        logString = new LogString();
        databaseScriptHelperXslAdapter = new DatabaseScriptHelperXslAdapter(
              new DatabaseScriptHelperMock(logString));
        source = DomUtil.toDocument("src/test/resources/net/codjo/datagen/DatagenTest.xml");
    }


    @Test
    public void test_getQueryDelimiter() throws Exception {
        databaseScriptHelperXslAdapter.getQueryDelimiter();
        logString.assertContent("getQueryDelimiter()");
    }


    @Test
    public void test_buildDropTableScript() throws Exception {
        databaseScriptHelperXslAdapter.buildDropTableScript(getTableNode(source, "AP_TOTO"));
        logString.assertContent("buildDropTableScript(AP_TOTO)");
    }


    @Test
    public void test_buildCreateTableScript() throws Exception {
        databaseScriptHelperXslAdapter.buildCreateTableScript(getTableNode(source, "AP_TOTO"), "no");
        logString.assertContent("buildCreateTableScript(AP_TOTO)");
    }


    @Test
    public void test_buildLogTableCreationScript() throws Exception {
        databaseScriptHelperXslAdapter.buildLogTableCreationScript(getTableNode(source, "AP_TOTO"));
        logString.assertContent("buildLogTableCreationScript(AP_TOTO)");
    }


    @Test
    public void test_buildCreateViewScript() throws Exception {
        databaseScriptHelperXslAdapter
              .buildCreateViewScript(getViewNode(source, "AP_TOTO", "VU_PTF_IN_DIVIDEND"));
        logString.assertContent("buildCreateViewScript(VU_PTF_IN_DIVIDEND)");
    }


    @Test
    public void test_buildDropIndexScript() throws Exception {
        databaseScriptHelperXslAdapter.buildDropIndexScript(getIndexNode(source, "AP_TOTO", "X1_"));
        logString.assertContent("buildDropIndexScript(X1_AP_TOTO)");
    }


    @Test
    public void test_buildCreateIndexScript() throws Exception {
        databaseScriptHelperXslAdapter.buildCreateIndexScript(getIndexNode(source, "AP_TOTO", "X1_"));
        logString.assertContent("buildCreateIndexScript(X1_AP_TOTO)");
    }


    @Test
    public void test_buildLogIndexCreationScript() throws Exception {
        databaseScriptHelperXslAdapter.buildLogIndexCreationScript(getIndexNode(source, "AP_TOTO", "X1_"));
        logString.assertContent("buildLogIndexCreationScript(X1_AP_TOTO)");
    }


    @Test
    public void test_buildDropConstraintScript() throws Exception {
        databaseScriptHelperXslAdapter
              .buildDropConstraintScript(getConstraintNode(source, "AP_TOTO", "FK_MERETOTO_TOTO"));
        logString.assertContent("buildDropConstraintScript(FK_MERETOTO_TOTO)");
    }


    @Test
    public void test_buildCreateConstraintScript() throws Exception {
        databaseScriptHelperXslAdapter
              .buildCreateConstraintScript(getConstraintNode(source, "AP_TOTO", "FK_MERETOTO_TOTO"));
        logString.assertContent("buildCreateConstraintScript(FK_MERETOTO_TOTO)");
    }


    @Test
    public void test_buildLogConstraintCreationScript() throws Exception {
        databaseScriptHelperXslAdapter
              .buildLogConstraintCreationScript(getConstraintNode(source, "AP_TOTO", "FK_MERETOTO_TOTO"));
        logString.assertContent("buildLogConstraintCreationScript(FK_MERETOTO_TOTO)");
    }


    @Test
    public void test_buildCustomScript() throws Exception {
        databaseScriptHelperXslAdapter.buildCustomScript("myCustomScript", source);
        logString.assertContent("buildCustomScript(myCustomScript, [])");
    }


    @Test
    public void test_buildCustomScript_createGap() throws Exception {
        databaseScriptHelperXslAdapter.buildCustomScript("createGap", getTableNode(source, "AP_TOTO"));
        logString.assertContent("buildCustomScript(createGap, [AP_TOTO, 10000])");
    }


    @Test
    public void test_buildCustomScript_logGapCreation() throws Exception {
        databaseScriptHelperXslAdapter.buildCustomScript("logGapCreation", getTableNode(source, "AP_TOTO"));
        logString.assertContent("buildCustomScript(logGapCreation, [AP_TOTO, 10000])");
    }


    @Test
    public void test_buildCreateTriggerScript_delete() throws Exception {
        databaseScriptHelperXslAdapter.buildCreateTriggerScript(getTriggerDeleteNode(source, "AP_TOTO"));
        logString.assertContent("buildCreateTriggerScript(TR_AP_TOTO_D)");
    }


    @Test
    public void test_buildCreateTriggerScript_insert() throws Exception {
        databaseScriptHelperXslAdapter.buildCreateTriggerScript(getTriggerInsertNode(source, "AP_TOTO"));
        logString.assertContent("buildCreateTriggerScript(TR_AP_TOTO_I)");
    }


    @Test
    public void test_buildCreateTriggerScript_update() throws Exception {
        databaseScriptHelperXslAdapter.buildCreateTriggerScript(getTriggerUpdateNode(source, "AP_TOTO"));
        logString.assertContent("buildCreateTriggerScript(TR_AP_TOTO_U)");
    }


    @Test
    public void test_buildCreateTriggerScript_insertUpdate() throws Exception {
        databaseScriptHelperXslAdapter
              .buildCreateTriggerScript(getTriggerInsertUpdateNode(source, "AP_TOTO"));
        logString.assertContent("buildCreateTriggerScript(TR_AP_TOTO_IU)");
    }


    private static class DatabaseScriptHelperMock implements DatabaseScriptHelper {
        private LogString logString;


        private DatabaseScriptHelperMock(LogString logString) {
            this.logString = logString;
        }


        public void setLegacyMode(boolean legacyMode) {
            logString.call("setLegacyMode", legacyMode);
        }


        public void setLegacyPrefix(String legacyPrefix) {
            logString.call("setLegacyPrefix", legacyPrefix);
        }


        public String getQueryDelimiter() {
            logString.call("getQueryDelimiter");
            return null;
        }


        public String buildDropTableScript(SqlTable sqlTable) {
            logString.call("buildDropTableScript", sqlTable.getName());
            return null;
        }


        public String buildCreateTableScript(SqlTableDefinition sqlTableDefintion) {
            logString.call("buildCreateTableScript", sqlTableDefintion.getName());
            return null;
        }


        public String buildLogTableCreationScript(SqlTable sqlTable) {
            logString.call("buildLogTableCreationScript", sqlTable.getName());
            return null;
        }


        public String buildCreateViewScript(SqlView sqlView) {
            logString.call("buildCreateViewScript", sqlView.getName());
            return null;
        }


        public String buildDropIndexScript(SqlIndex sqlIndex) {
            logString.call("buildDropIndexScript", sqlIndex.getName());
            return null;
        }


        public String buildCreateIndexScript(SqlIndex sqlIndex) {
            logString.call("buildCreateIndexScript", sqlIndex.getName());
            return null;
        }


        public String buildLogIndexCreationScript(SqlIndex sqlIndex) {
            logString.call("buildLogIndexCreationScript", sqlIndex.getName());
            return null;
        }


        public String buildDropConstraintScript(SqlConstraint sqlConstraint) {
            logString.call("buildDropConstraintScript", sqlConstraint.getName());
            return null;
        }


        public String buildCreateConstraintScript(SqlConstraint constraint) {
            logString.call("buildCreateConstraintScript", constraint.getName());
            return null;
        }


        public String buildLogConstraintCreationScript(SqlConstraint sqlConstraint) {
            logString.call("buildLogConstraintCreationScript", sqlConstraint.getName());
            return null;
        }


        public String buildCustomScript(String customScriptName, Object... parameters) {
            logString.call("buildCustomScript", customScriptName, toString(parameters));
            return null;
        }


        public String buildCreateTriggerScript(SqlTrigger trigger) {
            logString.call("buildCreateTriggerScript", trigger.getName());
            return null;
        }


        private String toString(Object... parameters) {
            StringBuilder string = new StringBuilder()
                  .append("[");
            for (Object parameter : parameters) {
                if (parameter instanceof SqlTable) {
                    string.append(((SqlTable)parameter).getName());
                }
                else if (parameter instanceof SqlIndex) {
                    string.append(((SqlIndex)parameter).getName());
                }
                else {
                    string.append(parameter);
                }
                string.append(", ");
            }
            if (string.length() > 1) {
                string.delete(string.length() - 2, string.length());
            }
            return string.append("]").toString();
        }
    }
}
