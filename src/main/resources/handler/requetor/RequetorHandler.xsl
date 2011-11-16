<?xml version="1.0"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:java="http://xml.apache.org/xslt/java"
    exclude-result-prefixes="java">
<xsl:import href="/kernel/Kernel.xsl"/>
<xsl:output method="text" omit-xml-declaration="yes"/>
<xsl:param name="entityName">generated.Dividend</xsl:param>
<xsl:param name="handlerId">dividendRequetor</xsl:param>

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
  <xsl:variable name="handlerClasseName" select="java:kernel.Util.handlerClassName($handlerId)"/>
package <xsl:value-of select="java:bean.Util.extractPackage(@name)"/>;
import net.codjo.database.api.Database;
import net.codjo.mad.server.handler.requetor.AbstractRequetorHandler;
/**
 *  Classe Handler de requete pour <xsl:value-of select="@name"/>. &lt;p&gt;
 */
public class <xsl:value-of select="$handlerClasseName"/> extends AbstractRequetorHandler {

    private static final String[] PK =
        {<xsl:apply-templates select="primary-key/field" mode="declare-as-list"/>};

    public <xsl:value-of select="$handlerClasseName"/>(Database database) {
        super("<xsl:value-of select="@table"/>", PK, database);
        <xsl:apply-templates select="properties/field"/>
    }

    public String getId() {
        return "<xsl:value-of select="$handlerId"/>";
    }
}
</xsl:template>

<!-- ***************************************************************************
    Declaration des property de type structure du bean pour le constructeur
-->
<xsl:template match="properties/field[@structure]">
    <xsl:param name="structure"><xsl:value-of select="@structure"/></xsl:param>
    <xsl:apply-templates select="//data/structure[@name=$structure]/properties/field" />
</xsl:template>

<!-- ***************************************************************************
    Declaration des property du bean pour le constructeur
-->
<xsl:template match="properties/field[@type]">
    <xsl:variable name="type"> <xsl:apply-templates select="@type"/> </xsl:variable>

    <xsl:choose>
        <xsl:when test="$type='java.sql.Date'">
            wrappers.put("<xsl:value-of select="@name"/>", new SqlWrapper("<xsl:value-of select="java:sql.Util.toSqlName(@name)"/>", java.sql.Types.DATE));
        </xsl:when>
        <xsl:otherwise>
            wrappers.put("<xsl:value-of select="@name"/>", new SqlWrapper("<xsl:value-of select="java:sql.Util.toSqlName(@name)"/>"));
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

</xsl:stylesheet>

