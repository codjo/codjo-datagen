<?xml version="1.0"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:java="http://xml.apache.org/xslt/java"
    exclude-result-prefixes="java">
<xsl:import href="/kernel/Kernel.xsl"/>
<xsl:output method="text" omit-xml-declaration="yes"/>
<xsl:param name="entityName">generated.Dividend</xsl:param>
<xsl:param name="newHandlerId">newDividend</xsl:param>

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
  <xsl:variable name="handlerId" select="feature/handler-new/@id"/>
  <xsl:variable name="queryNode" select="feature/handler-new[@id=$handlerId]/query"/>
  <xsl:variable name="sqlQuery" select="java:kernel.Util.flatten($queryNode)"/>
  <xsl:variable name="handlerClasseName" select="java:kernel.Util.handlerClassName(feature/handler-new/@id)"/>
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
<xsl:if test="$queryNode">
import java.sql.*;
import net.codjo.mad.server.handler.util.QueryUtil;
</xsl:if>
/**
 * Handler de création pour <xsl:value-of select="@name"/>.
 */
public class <xsl:value-of select="$handlerClasseName"/> extends AbstractHandler {
    <xsl:if test="feature/controlable">
    private ControlManager controlManager;
    </xsl:if>
    Map setters = new HashMap();

    public <xsl:value-of select="$handlerClasseName"/>(<xsl:if test="feature/controlable">ControlManager controlManager</xsl:if>) {
    <xsl:if test="feature/controlable">
        this.controlManager = controlManager;
    </xsl:if>

        <xsl:apply-templates select="properties/field"/>
    }


    public String getId() {
        return "<xsl:value-of select="feature/handler-new/@id"/>";
    }


    public String proceed(Node node) throws HandlerException {
        try {
            <xsl:value-of select="$entityClassName"/> obj = new <xsl:value-of select="$entityClassName"/>();
            Map fields = XMLUtils.getRowFields(node);

            for (Iterator i = fields.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry item = (Map.Entry) i.next();
                String name = item.getKey().toString();
                String value = (String) item.getValue();

                Setter props = (Setter) setters.get(name);
                try {
                    props.set(obj, value);
                }
                catch (Exception ex) {
                    throw new HandlerException("Valeur '" +  value + "' incorrecte pour " + name, ex);
                }
            }

            <xsl:if test="feature/audit-trail">
            obj.getAudit().setCreationDatetime(new java.sql.Timestamp(System.currentTimeMillis()));
            obj.getAudit().setCreationBy(this.getContext().getUser());
            obj.getAudit().setUpdateBy(null);
            obj.getAudit().setUpdateDatetime(null);
            </xsl:if>
            <xsl:if test="feature/controlable">
            executeControlForAdd(obj);
            </xsl:if>

     <xsl:if test="feature/historic-audit-trail">
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
                    keyMap.put(fieldName, fields.get(fieldName));
                }
                int i = 0;

                Iterator iter = keyMap.keySet().iterator();
                while (iter.hasNext()) {
                    String fieldName = (String) iter.next();
                    functionalKeyStr += toSqlName(fieldName) + "='" + (String) keyMap.get(fieldName) + "',";
                }
            }

        <xsl:if test="@table!=''">
            dataAudit.setDbTableName(getDbTableName());
         </xsl:if>
            dataAudit.setFunctionalKey(functionalKeyStr.substring(0, functionalKeyStr.length() - 1));
            dataAudit.setStatus(getStatus());
            dataAudit.setUserName(this.getContext().getUser());
            dataAudit.setInsertionDate(new java.sql.Timestamp(System.currentTimeMillis()));
         <xsl:if test="@table!=''">
            createDataAudit(dataAudit);
         </xsl:if>
    </xsl:if>
            createBean(obj);

            String requestId = XMLUtils.getAttribute(node, "request_id");

            String expected = "&lt;result request_id=\"" + requestId + "\"&gt;&lt;primarykey&gt;"
                <xsl:apply-templates select="primary-key/field" mode="declare"/>
                      + "&lt;/primarykey&gt;&lt;row&gt;"
               <xsl:apply-templates select="primary-key/field" mode="value"/>
                     + "&lt;/row&gt;&lt;/result&gt;";

            return expected;
        }
        catch (HandlerException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new HandlerException(ex);
        }
    }

    <xsl:choose>
        <xsl:when test="not($queryNode)">
    protected void createBean(<xsl:value-of select="$entityClassName"/> obj) throws PersistenceException {
        Database db = getContext().getDatabase();
        try {
            db.create(obj);
        }
        finally {
            db.close();
        }
    }
        </xsl:when>
        <xsl:otherwise>
    protected void createBean(<xsl:value-of select="$entityClassName"/> obj) throws PersistenceException {
        PreparedStatement query = null;
        Connection connection = null;
        try {
            connection = getContext().getTxConnection();
            query = connection.prepareStatement(QueryUtil.replaceUser("<xsl:value-of select="$sqlQuery"/>", getContext().getUser()));

            <xsl:apply-templates select="properties/field" mode="object-to-sql-statement"/>

            ResultSet rs = query.executeQuery();

            if (rs.next()) {
                <xsl:apply-templates select="primary-key/field" mode="fillSql2"/>
            }

            rs.close();
        }
        catch (SQLException exception) {
            throw new PersistenceException(exception.getLocalizedMessage(), exception);
        }
        finally {
            try {
                query.close();
                connection.close();
            }
            catch (SQLException exception) {
                throw new PersistenceException(exception.getLocalizedMessage(), exception);
            }
        }
    }
        </xsl:otherwise>
    </xsl:choose>


 <xsl:if test="feature/historic-audit-trail">
     <xsl:if test="@table!=''">
    protected void createDataAudit(<xsl:value-of select="feature/historic-audit-trail/@auditClass"/> obj) throws PersistenceException {
        Database db = getContext().getDatabase();
        try {
            db.create(obj);
        }
        finally {
            db.close();
        }
    }
     </xsl:if>
  </xsl:if>

    <xsl:if test="feature/controlable">
    private void executeControlForAdd(<xsl:value-of select="$entityClassName"/> obj) throws ControlException {
        controlManager.controlNewEntity(obj);
    }
    </xsl:if>

    private static interface Setter {
        public void set(<xsl:value-of select="$entityClassName"/> dv, String xmlValue);
    }

    <xsl:if test="feature/historic-audit-trail">
        <xsl:if test="@table!=''">
   private String getDbTableName(){
        return "<xsl:value-of select="@table"/>";
   }
   private String getFunctionalKey(){
        return "<xsl:value-of select="feature/historic-audit-trail/@functionalKey"/>";
   }
        </xsl:if>
    </xsl:if>
   private String getStatus(){
        return "INSERT";
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
}


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

<xsl:template match="primary-key/field" mode="fillSql2">
    <xsl:param name="name">
        <xsl:value-of select="@name"/>
     </xsl:param>
        <xsl:call-template name="sql-statement-to-object">
            <xsl:with-param name="name" select="@name"/>
            <xsl:with-param name="obj">obj</xsl:with-param>
            <xsl:with-param name="type" select="../../properties/field[@name=$name]/@type"/>
        </xsl:call-template>
</xsl:template>
</xsl:stylesheet>