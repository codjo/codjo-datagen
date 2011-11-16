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
    Declaration du corps du handler
-->
<xsl:template match="entity">
  <xsl:variable name="entityClassName" select="java:bean.Util.extractClassName(@name)"/>
  <xsl:variable name="handlerId" select="feature/handler-update/@id"/>
  <xsl:variable name="handlerClasseName" select="java:kernel.Util.handlerClassName($handlerId)"/>
  <xsl:variable name="queryNode" select="feature/handler-update[@id=$handlerId]/query"/>
  <xsl:variable name="sqlQuery" select="java:kernel.Util.flatten($queryNode)"/>
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
import java.util.*;
import net.codjo.mad.server.handler.*;
import net.codjo.mad.server.util.*;
<xsl:if test="not($queryNode) or feature/historic-audit-trail">
import org.exolab.castor.jdo.*;
</xsl:if>
import org.w3c.dom.*;
import org.xml.sax.*;
<xsl:if test="feature/historic-audit-trail">import java.lang.reflect.Method;</xsl:if>
<xsl:if test="$queryNode">import java.sql.*; import net.codjo.mad.server.handler.util.QueryUtil;</xsl:if>

/**
 *  Handler de MAJ pour <xsl:value-of select="@name"/>.
 */
public class <xsl:value-of select="$handlerClasseName"/> extends AbstractHandler {
    <xsl:if test="feature/controlable">
    private ControlManager controlManager;
    </xsl:if>
    <xsl:if test="not($queryNode)">
    private final static String oql = "SELECT p FROM <xsl:value-of select="@name"/> p WHERE <xsl:apply-templates select="primary-key/field" mode="oql-where-pk"/>";
    </xsl:if>
    <xsl:if test="$queryNode">
    private final static String sql = "SELECT <xsl:apply-templates select="properties/field" mode="sql-select-field"/> FROM <xsl:value-of select="@table"/> WHERE <xsl:apply-templates select="primary-key/field" mode="sql-where-pk"/>";
    </xsl:if>
    Map setters = new HashMap();

    public <xsl:value-of select="$handlerClasseName"/>(<xsl:if test="feature/controlable">ControlManager controlManager</xsl:if>) {
    <xsl:if test="feature/controlable">
        this.controlManager = controlManager;
    </xsl:if>

        <xsl:apply-templates select="properties/field"/>
    }

    public String getId() {
        return "<xsl:value-of select="$handlerId"/>";
    }

    public String proceed(Node node) throws HandlerException {
        try {
            Map pks = XMLUtils.getPrimaryKeys(node);

            <xsl:if test="$queryNode">
            Connection connection = getContext().getTxConnection();
            </xsl:if>
            <xsl:if test="not($queryNode)">
            Database db = getContext().getDatabase();
            </xsl:if>
            try {
                <xsl:if test="$queryNode">
                <xsl:value-of select="$entityClassName"/> obj = selectObject(connection, pks);
                </xsl:if>
                <xsl:if test="not($queryNode)">
                OQLQuery query = db.getOQLQuery(oql);
                <xsl:apply-templates select="primary-key/field" mode="fillOql"/>

                QueryResults results = query.execute();
                if (results.hasMore() == false) {
                    throw new IllegalArgumentException("ligne non trouvée");
                }
                <xsl:value-of select="$entityClassName"/> obj = (<xsl:value-of select="$entityClassName"/>) results.next();
                </xsl:if>

                <xsl:if test="feature/audit-trail">
                java.sql.Timestamp creationDate = obj.getAudit().getCreationDatetime();
                String creator = obj.getAudit().getCreationBy();

                </xsl:if>

                update(node, obj);
                <xsl:if test="feature/audit-trail">
                obj.getAudit().setCreationDatetime(creationDate);
                obj.getAudit().setCreationBy(creator);
                obj.getAudit().setUpdateBy(this.getContext().getUser());
                obj.getAudit().setUpdateDatetime(new java.sql.Timestamp(System.currentTimeMillis()));
                </xsl:if>

                <xsl:if test="feature/controlable">
                executeControl(obj);
                </xsl:if>

                updateBean(obj);

                <xsl:if test="$queryNode">
                executeDatabaseUpdate(connection, "<xsl:value-of select="$sqlQuery"/>", obj);
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
                <xsl:if test="$queryNode">connection.close();</xsl:if>
                <xsl:if test="not($queryNode)">db.close();</xsl:if>
            }
        }
        catch (HandlerException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new HandlerException(ex);
        }
    }

    protected void updateBean(<xsl:value-of select="$entityClassName"/> obj) {
    }

    private void removePk(Map fields) {
        <xsl:apply-templates select="primary-key/field" mode="removePk"/>
    }

    private void update(Node node, <xsl:value-of select="$entityClassName"/> obj) throws SAXException, HandlerException{
        Map fields = XMLUtils.getRowFields(node);
<xsl:if test="feature/historic-audit-trail">
        Map fieldsForHistoricAudit = new HashMap(fields);
        Map pks = XMLUtils.getPrimaryKeys(node);
</xsl:if>
        removePk(fields);

        for (Iterator i = fields.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry item = (Map.Entry) i.next();
            String name = item.getKey().toString();
            String value = (String) item.getValue();
<xsl:if test="feature/historic-audit-trail">
        try {
                String getterName = "get"+  name.substring(0,1).toUpperCase() + name.substring(1,name.length()) ;
                Method met = obj.getClass().getMethod(getterName);
                Object oldValue = met.invoke(obj);
                if (oldValue==null){
                    oldValue="null";
                }
                if (value==null){
                    value="null";
                }
                if (!(oldValue.equals(value))){
                    proceedDataAudit(fieldsForHistoricAudit, pks, oldValue.toString(), value, name);
                }
        } catch (NoSuchMethodException e) {            }
            catch(Exception ex){
            ex.printStackTrace();
        }
</xsl:if>
            Setter props = (Setter) setters.get(name);
            try {
                props.set(obj, value);
            }
            catch (Exception ex) {
                throw new HandlerException("Valeur '" +  value + "' incorrecte pour " + name, ex);
            }
        }
    }

    <xsl:if test="feature/controlable">
    private void executeControl(<xsl:value-of select="$entityClassName"/> obj) throws ControlException {
        controlManager.controlUpdatedEntity(obj);
    }
    </xsl:if>

    private static interface Setter {
        public void set(<xsl:value-of select="$entityClassName"/> dv, String xmlValue);
    }
<xsl:if test="feature/historic-audit-trail">
    private void proceedDataAudit(Map fields, Map pks, String oldValue, String newValue, String dbFieldName) {
        <xsl:value-of select="feature/historic-audit-trail/@auditClass"/> dataAudit = new  <xsl:value-of select="feature/historic-audit-trail/@auditClass"/>();

        HashMap keyMap = new HashMap();
        StringTokenizer t = new StringTokenizer(getFunctionalKey(), ",");
        String functionalKeyStr = "";

        if (t.countTokens() == 1) {
            functionalKeyStr = toSqlName(getFunctionalKey()) + "= '" + fields.get(getFunctionalKey().trim())
                    + "',";
        } else {

            while (t.hasMoreElements()) {
                String fieldName = t.nextElement().toString().trim();
                Object fieldValue = fields.get(fieldName);
                if ( fieldValue == null || "null".equals(fieldValue) ) {
                    fieldValue = pks.get(fieldName);
                }
                keyMap.put(fieldName, fieldValue);
            }

            Iterator iter = keyMap.keySet().iterator();
            while (iter.hasNext()) {
                String fieldName = (String) iter.next();
                functionalKeyStr += toSqlName(fieldName) + "='" + (String) keyMap.get(fieldName) + "',";
            }
        }


        dataAudit.setDbTableName(getDbTableName());
        dataAudit.setFunctionalKey(functionalKeyStr.substring(0, functionalKeyStr.length() - 1));
        dataAudit.setStatus(getStatus());
        dataAudit.setUserName(this.getContext().getUser());
        dataAudit.setInsertionDate(new java.sql.Timestamp(System.currentTimeMillis()));
        dataAudit.setNewValue(newValue);
        dataAudit.setOldValue(oldValue);
        dataAudit.setDbFieldName(toSqlName(dbFieldName));
        try {
            createDataAudit(dataAudit);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }
    protected void createDataAudit(<xsl:value-of select="feature/historic-audit-trail/@auditClass"/> obj) throws PersistenceException {
        Database db = getContext().getDatabase();
        try {
            db.create(obj);
        }
        finally {
            db.close();
        }
    }
    private String getDbTableName(){
    return "<xsl:value-of select="@table"/>";
    }
   private String getFunctionalKey(){
        return "<xsl:value-of select="feature/historic-audit-trail/@functionalKey"/>";
    }
     private String getStatus(){
    return "UPDATE";
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
    <xsl:if test="$queryNode">
    private <xsl:value-of select="$entityClassName"/> selectObject(Connection connection, Map pks) throws SQLException {
        PreparedStatement query = connection.prepareStatement(QueryUtil.replaceUser(sql, getContext().getUser()));
        try {
            <xsl:apply-templates select="primary-key/field" mode="fillSql"/>
            ResultSet rs = query.executeQuery();

            if (!rs.next()) {
                throw new IllegalArgumentException("ligne non trouvée");
            }
            <xsl:value-of select="$entityClassName"/> obj = new <xsl:value-of select="$entityClassName"/>();
            <xsl:apply-templates select="properties/field" mode="sql-statement-to-object"/>
            rs.close();
            return obj;
        }
        finally {
            query.close();
        }
    }

    private void executeDatabaseUpdate(Connection connection, String querySql, <xsl:value-of select="$entityClassName"/> obj) throws SQLException {
        CallableStatement query = connection.prepareCall(querySql);

        try {
            <xsl:apply-templates select="properties/field" mode="object-to-sql-statement"/>
            query.execute();
        } finally {
          query.close();
        }
    }
    </xsl:if>
}


</xsl:template>
<!-- ***************************************************************************
    Enleve les champs pk des fields d'update
-->
<xsl:template match="primary-key/field" mode="removePk">
        fields.remove("<xsl:value-of select="@name"/>");
</xsl:template>

<!-- ***************************************************************************
    Remplissage de la requete.
-->
<xsl:template match="primary-key/field" mode="fillOql">
    <xsl:param name="name"><xsl:value-of select="@name"/></xsl:param>
    <xsl:call-template name="bind">
        <xsl:with-param name="name" select="$name"/>
        <xsl:with-param name="type" select="../../properties/field[@name=$name]/@type"/>
    </xsl:call-template>
</xsl:template>
<xsl:template match="primary-key/field" mode="fillSql">
    <xsl:param name="name"><xsl:value-of select="@name"/></xsl:param>
    <xsl:call-template name="sqlSetParam">
        <xsl:with-param name="name" select="$name"/>
        <xsl:with-param name="type" select="../../properties/field[@name=$name]/@type"/>
    </xsl:call-template>
</xsl:template>

<!-- ***************************************************************************
    Declaration des property de type structure du bean pour le constructeur
-->
<xsl:template match="properties/field[@structure]">
    <xsl:param name="name"><xsl:value-of select="@name"/></xsl:param>
    <xsl:param name="structure"><xsl:value-of select="@structure"/></xsl:param>
    <xsl:variable name="cname" select="java:bean.Util.capitalize(@name)"/>
    <xsl:apply-templates select="//data/structure[@name=$structure]/properties/field">
            <xsl:with-param name="obj">dv.get<xsl:value-of select="$cname"/>()</xsl:with-param>
            <xsl:with-param name="className"><xsl:value-of select="../../@name"/></xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- ***************************************************************************
    Declaration des property du bean pour le constructeur
-->
<xsl:template match="properties/field[@type]">
    <xsl:param name="obj">dv</xsl:param>
    <xsl:param name="className" select="../../@name"/>
    <xsl:param name="name" select="@name"/>
    <xsl:variable name="cname" select="java:bean.Util.capitalize(@name)"/>
    <xsl:variable name="type"> <xsl:apply-templates select="@type"/> </xsl:variable>

    <xsl:choose>
        <xsl:when test="$type='boolean'">
            setters.put("<xsl:value-of select="$name"/>",
            new Setter() {
                public void set(<xsl:value-of select="java:bean.Util.extractClassName($className)"/> dv, String xmlValue) {
                    <xsl:value-of select="$obj"/>.set<xsl:value-of select="$cname"/>((XMLUtils.convertFromStringValue(Boolean.class, xmlValue)).booleanValue());
                }
            });
        </xsl:when>
        <xsl:when test="$type='int'">
            setters.put("<xsl:value-of select="$name"/>",
            new Setter() {
                public void set(<xsl:value-of select="java:bean.Util.extractClassName($className)"/> dv, String xmlValue) {
                    <xsl:value-of select="$obj"/>.set<xsl:value-of select="$cname"/>((XMLUtils.convertFromStringValue(Integer.class, xmlValue)).intValue());
                }
            });
        </xsl:when>
        <xsl:when test="$type='double'">
            setters.put("<xsl:value-of select="$name"/>",
            new Setter() {
                public void set(<xsl:value-of select="java:bean.Util.extractClassName($className)"/> dv, String xmlValue) {
                    <xsl:value-of select="$obj"/>.set<xsl:value-of select="$cname"/>((XMLUtils.convertFromStringValue(Double.class, xmlValue)).doubleValue());
                }
            });
        </xsl:when>
        <xsl:otherwise>
            setters.put("<xsl:value-of select="$name"/>",
            new Setter() {
                public void set(<xsl:value-of select="java:bean.Util.extractClassName($className)"/> dv, String xmlValue) {
                    <xsl:value-of select="$obj"/>.set<xsl:value-of select="$cname"/>(XMLUtils.convertFromStringValue(<xsl:value-of select="$type"/>.class, xmlValue));
                }
            });
        </xsl:otherwise>
    </xsl:choose>

</xsl:template>

</xsl:stylesheet>

