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

        if ("string".equalsIgnoreCase(type)) {
            return "java.sql.Types.VARCHAR";
        }

        return "";
    }


    public static String buildGetterConstructorParameter(int index, String sqlType) {
        String indexAsString = "" + index;
        if ("".equals(sqlType) || sqlType == null) {
            return indexAsString;
        }
        else {
            return indexAsString + ", " + sqlType;
        }
    }
}
