<?xml version="1.0"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:java="http://xml.apache.org/xslt/java"
    exclude-result-prefixes="java">
<xsl:import href="/kernel/Kernel.xsl"/>
<xsl:output method="text" omit-xml-declaration="yes"/>
<xsl:param name="entityName">generated.Dividend</xsl:param>
<xsl:param name="selectHandlerId">selectDividendHandlerByPk</xsl:param>

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
  <xsl:variable name="handlerSelectNode" select="feature/handler-select[@id=$selectHandlerId]"/>
  <xsl:variable name="queryNode" select="$handlerSelectNode/query"/>
  <xsl:variable name="query" select="java:kernel.Util.flatten($queryNode)"/>
  <xsl:variable name="queryType" select="$queryNode/@type"/>
  <xsl:variable name="handlerClasseName" select="java:kernel.Util.handlerClassName($selectHandlerId)" />
package <xsl:value-of select="java:bean.Util.extractPackage(@name)"/>;
import net.codjo.database.api.Database;
import net.codjo.mad.server.handler.*;
import java.util.*;
<xsl:choose>
<xsl:when test="$queryNode and (not($queryType) or $queryType='SQL')">
import net.codjo.database.api.query.PreparedQuery;
import java.sql.*;
</xsl:when>
<xsl:otherwise>
import org.exolab.castor.jdo.*;
</xsl:otherwise>
</xsl:choose>
/**
 *  Classe Handler de selection pour <xsl:value-of select="@name"/>. &lt;p&gt;
 *
 *  Query : &lt;code&gt;<xsl:value-of select="$query"/>&lt;/code&gt;
 */
public class <xsl:value-of select="$handlerClasseName"/> extends AbstractSelect<xsl:value-of select="$entityClassName"/>Handler {

    public <xsl:value-of select="$handlerClasseName"/>(Database database) {
        <xsl:choose>
            <xsl:when test="$queryType='OQL'">
                super(OQL_QUERY, "<xsl:value-of select="$query"/>", database);
            </xsl:when>
            <xsl:otherwise>
                super(SQL_QUERY, "<xsl:value-of select="$query"/>", database);
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="methods/method"/>
    }

    public String getId() {
        return "<xsl:value-of select="$selectHandlerId"/>";
    }

<xsl:choose>
    <xsl:when test="$queryType='OQL'">
        @Override
        protected void fillOqlQuery(OQLQuery query, Map&lt;String, String> pks) {
            <xsl:apply-templates select="$handlerSelectNode/arg" mode="oql"/>
        }
    </xsl:when>
    <xsl:otherwise>
        @Override
        protected void fillSqlQuery(PreparedQuery query, Map&lt;String, String> pks) throws SQLException {
            <xsl:apply-templates select="$handlerSelectNode/arg" mode="sql"/>
        }
    </xsl:otherwise>
</xsl:choose>
}
</xsl:template>

<!-- ***************************************************************************
    Remplissage de la requete en mode SQL.
-->
<xsl:template match="arg" mode="sql">
    <xsl:param name="name"><xsl:value-of select="."/></xsl:param>
    <xsl:choose>
        <xsl:when test="@type">
             <xsl:call-template name="sqlSetParam">
                    <xsl:with-param name="name" select="$name"/>
                    <xsl:with-param name="type" select="@type"/>
             </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
            <xsl:call-template name="sqlSetParam">
                <xsl:with-param name="name" select="$name"/>
                <xsl:with-param name="type" select="../../../properties/field[@name=$name]/@type"/>
            </xsl:call-template>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<!-- ***************************************************************************
    Remplissage de la requete en mode OQL.
-->
<xsl:template match="arg" mode="oql">
    <xsl:param name="name"><xsl:value-of select="."/></xsl:param>
    <xsl:choose>
        <xsl:when test="@type">
             <xsl:call-template name="bind">
                    <xsl:with-param name="name" select="$name"/>
                    <xsl:with-param name="type" select="@type"/>
             </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
            <xsl:call-template name="bind">
                <xsl:with-param name="name" select="$name"/>
                <xsl:with-param name="type" select="../../../properties/field[@name=$name]/@type"/>
            </xsl:call-template>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>
<!-- ***************************************************************************
    Declaration des methodes du bean pour le constructeur
-->
<xsl:template match="method">
    <xsl:param name="className" select="../../@name"/>
    <xsl:param name="name" select="@name"/>
    <xsl:variable name="entityClassName" select="java:bean.Util.extractClassName($className)"/>
    addGetter("<xsl:value-of select="$name"/>",
        new Getter("<xsl:value-of select="$name"/>") {
            public String get(<xsl:value-of select="$entityClassName"/> dv) throws Exception {
                return XMLUtils.convertToStringValue(dv.<xsl:value-of select="$name"/>());
            }
        });
</xsl:template>

</xsl:stylesheet>

