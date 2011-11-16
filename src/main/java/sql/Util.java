/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package sql;
import java.util.StringTokenizer;
/**
 * Classe utilitaire pour la génération SQL
 */
public final class Util {
    @SuppressWarnings({"StaticNonFinalField", "PublicField"})
    public static boolean legacyMode = false;
    @SuppressWarnings({"PublicField"})
    public static String legacyPrefix = null;


    private Util() {
    }


    /**
     * Transforme le nom d'une propriétés en son nom SQL.
     *
     * <p> Exemple : <code>pimsCode</code> devient <code>PIMS_CODE</code>. </p>
     *
     * @param propertyName propriété
     *
     * @return nom SQL
     *
     * @throws IllegalArgumentException If the length of <code>propertyName</code> is greater than 28
     *                                  caracters.
     */
    public static String toSqlName(String propertyName) {
        if (legacyMode && legacyPrefix == null) {
            return propertyName;
        }
        String sqlName = toSqlUpper(propertyName);
        if (!legacyMode) {
            if (sqlName.length() > 28) {
                throw new IllegalArgumentException("La colonne pour le champ " + propertyName
                                                   + " dépasse 28 caractères ! C'est pas bien !! "
                                                   + " Et quand je dis que c'est pas bien, c'est que c'est vraiment pas bien !"
                                                   + " M'enfin, il faut relativiser quand meme, c'est pas le bout du monde "
                                                   + " je vais tout simplement bloquer la génération jusqu'a ce que vous "
                                                   + " corrigiez le probleme !");
            }
        }
        return sqlName;
    }


    public static String toSqlNameWithQuote(String propertyName) {
        return "\\\"" + toSqlName(propertyName) + "\\\"";
    }


    /**
     * Troncate the String to not exceed 30 caracts
     *
     * @return Troncated string
     */
    public static String troncate(String objectName) {
        if (objectName.length() > 30) {
            return objectName.substring(0, 30);
        }
        else {
            return objectName;
        }
    }


    public static String toSqlUpper(String propertyName) {
        StringBuilder buffer = new StringBuilder();

        if (isAlreadyInSQLSyntax(propertyName)) {
            return propertyName;
        }

        for (int i = 0; i < propertyName.length(); i++) {
            if (Character.isUpperCase(propertyName.charAt(i))) {
                buffer.append('_');
            }
            buffer.append(propertyName.charAt(i));
        }

        return buffer.toString().toUpperCase();
    }


    private static boolean isAlreadyInSQLSyntax(String propertyName) {
        for (int i = 0; i < propertyName.length(); i++) {
            char currentChar = propertyName.charAt(i);
            if (!Character.isUpperCase(currentChar) && currentChar != '_'
                && !Character.isDigit(currentChar)) {
                return false;
            }
        }
        return true;
    }


    public static String multipleKeytoSqlName(String multipleKey) {
        StringTokenizer token = new StringTokenizer(multipleKey, ",");
        StringBuilder functionalKeyStr = new StringBuilder();

        if (token.countTokens() == 1) {
            functionalKeyStr.append(toSqlName(multipleKey)).append(",");
        }
        else {
            while (token.hasMoreElements()) {
                String fieldName = token.nextElement().toString();
                functionalKeyStr.append(toSqlName(fieldName)).append(",");
            }
        }
        return functionalKeyStr.substring(0, functionalKeyStr.length() - 1);
    }
}
