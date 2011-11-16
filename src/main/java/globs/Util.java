package globs;
import kernel.BuildException;
/**
 *
 */
public class Util {
    private Util() {
    }


    public static String extractPackage(String fullClassName) {
        return bean.Util.extractPackage(fullClassName) + ".globs";
    }


    public static String transformFullClassName(String fullClassName) {
        return extractPackage(fullClassName) + "." + bean.Util.extractClassName(fullClassName);
    }

    public static String toLinkName(String linkId, String linkName) throws BuildException {
        if ((linkName == null) || "".equals(linkName)) {
            throw new BuildException("Attribute 'name' must be provided for link '" + linkId + "'.");
        }
        return sql.Util.toSqlName(linkName);
    }
}
