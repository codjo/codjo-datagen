package handler;
public class Util {

    private Util() {
    }


    public static String convertTypeJavaSqlTypesConstant(String type) {
        if ("java.sql.Date".equals(type)) {
            return "java.sql.Types.DATE";
        }

        if ("java.sql.Timestamp".equals(type)) {
            return "java.sql.Types.TIMESTAMP";
        }

        if ("String".equals(type)) {
            return "java.sql.Types.VARCHAR";
        }

        return "";
    }
}
