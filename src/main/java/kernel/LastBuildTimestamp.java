package kernel;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class LastBuildTimestamp {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private String lastBuildTimestampFile;
    private Long lastBuildTimestamp;


    public LastBuildTimestamp(String lastBuildTimestampFile) {
        this.lastBuildTimestampFile = lastBuildTimestampFile;
    }


    public boolean isMoreRecent(File comparedFile) throws IOException {
        return getLastBuildTimestamp() > comparedFile.lastModified();
    }


    private long getLastBuildTimestamp() throws IOException {
        if (lastBuildTimestamp == null) {
            Reader reader = new InputStreamReader(getClass().getResourceAsStream(lastBuildTimestampFile));
            try {
                String buildDate = new BufferedReader(reader).readLine();
                lastBuildTimestamp = DATE_FORMAT.parse(buildDate).getTime();
            }
            catch (ParseException e) {
                throw new RuntimeException("Erreur lors de la récupération de la dernière date de build !!!",
                                           e);
            }
            finally {
                reader.close();
            }
        }
        return lastBuildTimestamp;
    }
}
