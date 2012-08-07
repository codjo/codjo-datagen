<?xml version="1.0"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:java="http://xml.apache.org/xslt/java"
    exclude-result-prefixes="java">
    <xsl:import href="/kernel/Kernel.xsl"/>
    <xsl:output method="text" omit-xml-declaration="yes"/>
    <xsl:param name="entityName">generated.Dividend</xsl:param>

    <!-- ***************************************************************************
        Démarrage
    -->
    <xsl:template match="/">
        <xsl:apply-templates select="data/entity[@name=$entityName]"/>
    </xsl:template>


    <!-- ***************************************************************************
        Declaration du corps du handler NON CONTROLABLE
    -->
    <xsl:template match="entity[not(feature/controlable) and not(feature/handler-delete/@use-bean='true')]">
        <xsl:variable name="entityClassName" select="java:bean.Util.extractClassName(@name)"/>
        <xsl:variable name="handlerId" select="feature/handler-delete/@id"/>
        <xsl:variable name="handlerClasseName" select="java:kernel.Util.handlerClassName($handlerId)"/>
        <xsl:variable name="queryNode" select="feature/handler-delete[@id=$handlerId]/query"/>
        <xsl:variable name="sqlQuery" select="java:kernel.Util.flatten($queryNode)"/>
/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package <xsl:value-of select="java:bean.Util.extractPackage(@name)"/>;
import net.codjo.mad.server.handler.AbstractHandler;
import net.codjo.mad.server.handler.HandlerException;
import net.codjo.mad.server.handler.XMLUtils;
import org.w3c.dom.Node;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Handler de destruction pour generated.Dividend.
 */
public class <xsl:value-of select="$handlerClasseName"/> extends AbstractHandler {
    <xsl:if test="$queryNode">
        private static final String SQL = "<xsl:value-of select="$sqlQuery"/>";
    </xsl:if>
    <xsl:if test="not($queryNode)">
        private static final String SQL = "delete from <xsl:value-of select="@table"/> where <xsl:apply-templates select="primary-key/field" mode="sql"/>";
    </xsl:if>
    <xsl:if test="feature/historic-audit-trail">private static final String SELECT_SQL = "select * from <xsl:value-of select="@table"/> where <xsl:apply-templates select="primary-key/field" mode="sql"/>";
    private static final String AUDIT_SQL = "insert into AP_DATA_AUDIT (DB_TABLE_NAME,FUNCTIONAL_KEY,STATUS,USER_NAME,INSERTION_DATE)" +
            "values (?,?,'DELETE',?,?)";

        </xsl:if>


    public String getId() {
        return "<xsl:value-of select="$handlerId"/>";
    }

        <xsl:if test="feature/historic-audit-trail">

   private String getDbTableName() {
        return "<xsl:value-of select="@table"/>";
    }

    private String getFunctionalKey() {
        return "<xsl:value-of select="java:sql.Util.multipleKeytoSqlName(feature/historic-audit-trail/@functionalKey)"/>";
    }
        </xsl:if>

    public String proceed(Node node) throws HandlerException {
        try {
            Map pks = XMLUtils.getPrimaryKeys(node);
            Connection con = getContext().getTxConnection();
            PreparedStatement stmt = null;
            try {
                stmt = con.prepareStatement(SQL);
                fillSqlQuery(stmt, pks);

        <xsl:if test="feature/historic-audit-trail">
                PreparedStatement auditStmt = con.prepareStatement(AUDIT_SQL);

                PreparedStatement selectStmt = con.prepareStatement(SELECT_SQL);
                fillSqlQuery(selectStmt, pks);
                ResultSet rs = selectStmt.executeQuery();

                if (!rs.next()) {
                    throw new IllegalArgumentException("Erreur audit handler delete : pas de ligne à détruire!");
                }

                auditStmt.setString(1, getDbTableName());
                auditStmt.setString(2, buildFunctionKey(rs, getFunctionalKey()));
                auditStmt.setString(3, getContext().getUser());
                auditStmt.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
                 try {
                    auditStmt.executeUpdate();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    throw new HandlerException("Erreur lors de l'audit de la suppression de la table "+getDbTableName());
                }
        </xsl:if>

                stmt.executeUpdate();

                String requestId = XMLUtils.getAttribute(node, "request_id");

                String expected = "&lt;result request_id=\"" + requestId + "\"&gt;&lt;primarykey&gt;"
        <xsl:apply-templates select="primary-key/field" mode="declare"/>
                          + "&lt;/primarykey&gt;&lt;row&gt;"
        <xsl:apply-templates select="primary-key/field" mode="valueSql"/>
                         + "&lt;/row&gt;&lt;/result&gt;";

                return expected;
            }
            finally {
                if (stmt != null) {
                    stmt.close();
                }
                con.close();
            }
        }
        catch (Exception ex) {
            throw new HandlerException(ex);
        }
    }

    private void fillSqlQuery(final PreparedStatement query, final Map pks)
        throws SQLException {
        <xsl:apply-templates select="primary-key/field" mode="fillSql"/>
        }

        <xsl:if test="feature/historic-audit-trail">
    private String buildFunctionKey(ResultSet rs, String functionalKey) throws SQLException {
        HashMap keyMap = new HashMap();
        StringTokenizer t = new StringTokenizer(functionalKey, ",");
        String functionalKeyStr = "";

        if (t.countTokens() == 1) {
            functionalKeyStr = functionalKey + "= '" + safeGetValueFromResultSet(rs,functionalKey)+"'";

        } else {

            while (t.hasMoreElements()) {
                String fieldName = t.nextElement().toString().trim();
                Object fieldValue = safeGetValueFromResultSet(rs,fieldName);
                keyMap.put(fieldName, fieldValue.toString());
            }
            int i = 0;

            Iterator iter = keyMap.keySet().iterator();
            while (iter.hasNext()) {
                String fieldName = (String) iter.next();
                functionalKeyStr += fieldName + "='" + (String) keyMap.get(fieldName) + "',";
            }
        }

        return functionalKeyStr.substring(0, functionalKeyStr.length() - 1);
    }

    private String safeGetValueFromResultSet(ResultSet rs, String fieldName) {
        try {
            return rs.getObject(fieldName).toString();
        } catch (Exception ee) {
            return "null";
        }
    }
        </xsl:if>

}
    </xsl:template>

    <!-- ***************************************************************************
        Declaration du corps du handler DANS LE CAS CONTROLABLE
    -->
    <xsl:template match="entity[feature/controlable or feature/handler-delete/@use-bean='true']">
        <xsl:variable name="entityClassName" select="java:bean.Util.extractClassName(@name)"/>
        <xsl:variable name="handlerId" select="feature/handler-delete/@id"/>
        <xsl:variable name="handlerClasseName" select="java:kernel.Util.handlerClassName($handlerId)"/>
/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package <xsl:value-of select="java:bean.Util.extractPackage(@name)"/>;
<xsl:if test="feature/controlable">
import net.codjo.control.common.ControlException;
import net.codjo.control.common.manager.ControlManager;
</xsl:if>
import net.codjo.mad.server.handler.*;
import net.codjo.mad.server.util.*;
import java.util.*;
import org.exolab.castor.jdo.*;
import org.w3c.dom.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
        

/**
 * Handler de destruction pour
        <xsl:value-of select="@name"/>.
 *
 */
public class <xsl:value-of select="$handlerClasseName"/> extends AbstractHandler {
        <xsl:if test="feature/controlable">
    private ControlManager controlManager;
        </xsl:if>
    private final static String oql = "SELECT p FROM "+
        " <xsl:value-of select="@name"/> p WHERE <xsl:apply-templates select="primary-key/field" mode="oql"/>";

        <xsl:if test="feature/historic-audit-trail">
            private static final String SELECT_SQL = "select * from <xsl:value-of select="@table"/> where <xsl:apply-templates select="primary-key/field" mode="sql"/>";
            private static final String AUDIT_SQL = "insert into AP_DATA_AUDIT (DB_TABLE_NAME,FUNCTIONAL_KEY,STATUS,USER_NAME,INSERTION_DATE)" +
                                                    "values (?,?,'DELETE',?,getdate())";

        </xsl:if>


        <xsl:if test="feature/controlable">
    public <xsl:value-of select="$handlerClasseName"/>(ControlManager controlManager) {
        this.controlManager = controlManager;
    }
        </xsl:if>

    public String getId() {
        return "<xsl:value-of select="$handlerId"/>";
    }


    public String proceed(Node node) throws HandlerException {
        try {
            Map pks = XMLUtils.getPrimaryKeys(node);

            Connection con = getContext().getTxConnection();
            Database db = getContext().getDatabase();

        try {
                OQLQuery query = db.getOQLQuery(oql);
        <xsl:apply-templates select="primary-key/field" mode="fillOql"/>

                QueryResults results = query.execute();
                if (results.hasMore() == false) {
                    throw new IllegalArgumentException("ligne non trouvée");
                }
        <xsl:value-of select="$entityClassName"/> obj = (<xsl:value-of select="$entityClassName"/>) results.next();

        <xsl:if test="feature/historic-audit-trail">
               PreparedStatement auditStmt = con.prepareStatement(AUDIT_SQL);

                PreparedStatement selectStmt = con.prepareStatement(SELECT_SQL);
                fillSqlQuery(selectStmt, pks);
                ResultSet rs = selectStmt.executeQuery();

                if (!rs.next()) {
                    throw new IllegalArgumentException("Erreur audit handler delete : pas de ligne à détruire!");
                }

                auditStmt.setString(1, getDbTableName());
                auditStmt.setString(2, buildFunctionKey(rs, getFunctionalKey()));
                auditStmt.setString(3, getContext().getUser());
                 try {
                    auditStmt.executeUpdate();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    throw new HandlerException("Erreur lors de l'audit de la suppression de la table "+getDbTableName());
                }
        </xsl:if>

                removeBean(db, obj);

        <xsl:if test="feature/controlable">
                executeControlForDelete(obj);
        </xsl:if>
                String requestId = XMLUtils.getAttribute(node, "request_id");

                String expected = "&lt;result request_id=\"" + requestId + "\"&gt;&lt;primarykey&gt;"
        <xsl:apply-templates select="primary-key/field" mode="declare"/>
                          + "&lt;/primarykey&gt;&lt;row&gt;"
        <xsl:apply-templates select="primary-key/field" mode="value"/>
                         + "&lt;/row&gt;&lt;/result&gt;";

                return expected;
            }
            finally {
                con.close();
                db.close();
            }
        }
        catch (Exception ex) {
            throw new HandlerException(ex);
        }
    }

    private void fillSqlQuery(final PreparedStatement query, final Map pks)
        throws SQLException {
        <xsl:apply-templates select="primary-key/field" mode="fillSql"/>
        }

        <xsl:if test="feature/historic-audit-trail">
    private String buildFunctionKey(ResultSet rs, String functionalKey) throws SQLException {
        HashMap keyMap = new HashMap();
        StringTokenizer t = new StringTokenizer(functionalKey, ",");
        String functionalKeyStr = "";

        if (t.countTokens() == 1) {
            functionalKeyStr = functionalKey + "= '" + safeGetValueFromResultSet(rs,functionalKey)+"'";

        } else {

            while (t.hasMoreElements()) {
                String fieldName = t.nextElement().toString().trim();
                Object fieldValue = safeGetValueFromResultSet(rs,fieldName);
                keyMap.put(fieldName, fieldValue.toString());
            }
            int i = 0;

            Iterator iter = keyMap.keySet().iterator();
            while (iter.hasNext()) {
                String fieldName = (String) iter.next();
                functionalKeyStr += fieldName + "='" + (String) keyMap.get(fieldName) + "',";
            }
        }

        return functionalKeyStr.substring(0, functionalKeyStr.length() - 1);
    }

    private String safeGetValueFromResultSet(ResultSet rs, String fieldName) {
        try {
            return rs.getObject(fieldName).toString();
        } catch (Exception ee) {
            return "null";
        }
    }
        </xsl:if>

    protected void removeBean(Database db,
        <xsl:value-of select="$entityClassName"/> obj) throws PersistenceException {
        db.remove(obj);
    }
        <xsl:if test="feature/historic-audit-trail">
    protected void removeDataAudit(Database db,
            <xsl:value-of select="feature/historic-audit-trail/@auditClass"/> obj) throws PersistenceException {
        db.remove(obj);
    }
        </xsl:if>
        <xsl:if test="feature/controlable">
    private void executeControlForDelete(<xsl:value-of select="$entityClassName"/> obj) throws ControlException {
        controlManager.controlDeletedEntity(obj);
    }
        </xsl:if>
        <xsl:if test="feature/historic-audit-trail">
    private String getDbTableName(){
    return "<xsl:value-of select="@table"/>";
    }
   private String getFunctionalKey() {
        return "<xsl:value-of select="feature/historic-audit-trail/@functionalKey"/>";
    }
     private String getStatus(){
    return "DELETE";
    }

         public static String toSqlName(String propertyName) {
        String sqlName = toSqlUpper(propertyName);
        if (sqlName.length() > 28) {
            throw new IllegalArgumentException("La colonne pour le field " + propertyName
                    + " dépasse 28 caractères ! C'est pas bien !! "
                    + " Et quand je dis que c'est pas bien, c'est que c'est vraiment pas bien !"
                    + " m'enfin, il faut relativiser quand meme, c'est pas le bout du monde "
                    + " je vais tout simplement bloquer la génération jusqu'a ce que vous "
                    + " corrigiez le probleme !");
        }
        return sqlName;
    }

            <![CDATA[
     public static String toSqlUpper(String propertyName) {
         StringBuffer buffer = new StringBuffer();
         for (int i = 0; i < propertyName.length(); i++) {
             if (Character.isUpperCase(propertyName.charAt(i))) {
                 buffer.append('_');
             }
             buffer.append(propertyName.charAt(i));
         }

         String sqlName = buffer.toString().toUpperCase();
         return sqlName;
     }
        ]]>
        </xsl:if>
}
    </xsl:template>
    <!-- ***************************************************************************
        Remplissage de la requete.
    -->
    <xsl:template match="primary-key/field" mode="fillOql">
        <xsl:param name="name">
            <xsl:value-of select="@name"/>
        </xsl:param>
        <xsl:call-template name="bind">
            <xsl:with-param name="name" select="@name"/>
            <xsl:with-param name="type" select="../../properties/field[@name=$name]/@type"/>
        </xsl:call-template>
    </xsl:template>
    <!-- ***************************************************************************
        Remplissage de la requete en mode sql.
    -->
    <xsl:template match="primary-key/field" mode="fillSql">
        <xsl:param name="name">
            <xsl:value-of select="@name"/>
        </xsl:param>
        <xsl:call-template name="sqlSetParam">
            <xsl:with-param name="name" select="@name"/>
            <xsl:with-param name="type" select="../../properties/field[@name=$name]/@type"/>
        </xsl:call-template>
    </xsl:template>

    <!-- ***************************************************************************
        Declaration des pk dans la requete oql
    -->
    <xsl:template match="primary-key/field" mode="oql">
        <xsl:if test="position()>1"> and </xsl:if>
        <xsl:value-of select="@name"/> = $<xsl:value-of select="position()"/>
    </xsl:template>

    <!-- ***************************************************************************
        Declaration des pk dans la requete sql
    -->
    <xsl:template match="primary-key/field" mode="sql">
        <xsl:if test="position()>1"> and </xsl:if>
        <xsl:value-of select="java:sql.Util.toSqlName(@name)"/> = ?</xsl:template>

    <!-- ***************************************************************************
        Declaration des pk dans le resultat et de leur valeur
    -->
    <xsl:template match="primary-key/field" mode="valueSql">
        <xsl:variable name="cname" select="(@name)"/>
     + "&lt;field name=\"<xsl:value-of select="@name"/>\">" + XMLUtils.convertToStringValue(pks.get("<xsl:value-of select="$cname"/>")) + "&lt;/field>"
    </xsl:template>


</xsl:stylesheet>

