package sql;
import net.codjo.database.sybase.impl.SybaseDatabaseTranscoder;
/**
 *
 */
public class LegacyDatabaseTranscoder extends SybaseDatabaseTranscoder{
    public LegacyDatabaseTranscoder() {
        addSqlFieldType(TIMESTAMP, "timestamp");
    }
}
