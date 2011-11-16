package sql;
import net.codjo.database.common.api.confidential.DatabaseTranscoder;
import net.codjo.database.common.api.structure.SqlFieldDefinition;
import net.codjo.database.common.api.structure.SqlTableDefinition;
import net.codjo.database.sybase.impl.SybaseDatabaseTranscoder;
import net.codjo.database.sybase.impl.helper.SybaseDatabaseScriptHelper;
import net.codjo.database.sybase.impl.query.SybaseDatabaseQueryHelper;
import static sql.Util.legacyPrefix;
/**
 *
 */
public class LegacyDatabaseScriptHelper extends SybaseDatabaseScriptHelper {
    private boolean legacyTable;


    public LegacyDatabaseScriptHelper() {
        super(new SybaseDatabaseQueryHelper(), new SybaseDatabaseTranscoder());
    }


    @Override
    protected String buildFieldDefinition(SqlTableDefinition tableDefinition,
                                          SqlFieldDefinition fieldDefinition) {
        legacyTable = legacyPrefix != null && tableDefinition.getName().startsWith(legacyPrefix);
        return super.buildFieldDefinition(tableDefinition, fieldDefinition)
              .replace("bit default null", "bit");
    }


    @Override
    protected DatabaseTranscoder getDatabaseTranscoder() {
        if (legacyTable) {
            return new LegacyDatabaseTranscoder();
        }
        else {
            return new SybaseDatabaseTranscoder();
        }
    }
}
