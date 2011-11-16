<?xml version="1.0"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:util="bean.Util"
    exclude-result-prefixes="util">
<xsl:import href="/kernel/Kernel.xsl"/>
<xsl:output method="text" omit-xml-declaration="yes"/>
<xsl:param name="entityName">gen.Dividend</xsl:param>

<!-- ***************************************************************************
    Démarrage
-->
<xsl:template match="/">
    <xsl:apply-templates select="data/*[@name=$entityName]"/>
</xsl:template>

<!-- ***************************************************************************
    Declaration du corps du beans
-->
<xsl:template match="entity | structure">
package <xsl:value-of select="util:extractPackage(@name)"/>;
/**
* <xsl:value-of select="description"/>. &lt;p&gt;
*
* Cette classe est genere automatiquement.
*/
public class <xsl:value-of select="util:extractClassName(@name)"/>
    <xsl:if test="feature/bean[@implements]"> implements  <xsl:value-of select="feature/bean/@implements"/> </xsl:if>
    <xsl:if test="feature/bean[@extends]"> extends  <xsl:value-of select="feature/bean/@extends"/> </xsl:if>
        {

    /**
     * Constructeur.
     */
    public <xsl:value-of select="util:extractClassName(@name)"/>() {}

    <xsl:apply-templates select="methods/method"/>

    <xsl:apply-templates select="properties/field"/>
}
</xsl:template>

<!-- ***************************************************************************
    Déclaration ds méthodes
-->
<xsl:template match="methods/method">
    /**
     * <xsl:value-of select="description"/>
     * @return na
     */
    public  <xsl:apply-templates select="@type"/><xsl:text> </xsl:text><xsl:value-of select="@name"/>() <xsl:if test="throws">throws <xsl:value-of select="throws"/> </xsl:if>{
        <xsl:value-of select="body"/>
    }
</xsl:template>


<!-- ***************************************************************************
    Declaration des property du bean
-->
<xsl:template match="properties/field">
    <xsl:variable name="name" select="@name"/>
    <xsl:variable name="cname" select="util:capitalize(@name)"/>
    <xsl:variable name="type"> <xsl:apply-templates select="@type | @structure"/> </xsl:variable>
    private <xsl:value-of select="$type"/>
    <xsl:text> </xsl:text>
    <xsl:value-of select="$name"/><xsl:call-template name="initIfStructure"/>;

    <xsl:if test="description">/**
     * Positionne le <xsl:value-of select="description"/>.
     * @param <xsl:value-of select="$name"/> na
     */</xsl:if>
    public void set<xsl:value-of select="$cname"/>(<xsl:value-of select="$type"/> <xsl:text> </xsl:text><xsl:value-of select="@name"/>) {
      this.<xsl:value-of select="$name"/> = <xsl:value-of select="$name"/>;
    }

    <xsl:if test="description"> /**
     * Retourne le <xsl:value-of select="description"/>.
     * @return <xsl:value-of select="$name"/>
     */ </xsl:if>
    public <xsl:value-of select="$type"/> get<xsl:value-of select="$cname"/>() {
      return <xsl:value-of select="$name"/>;
    }
</xsl:template>

<!-- ***************************************************************************
    Initialisation de la property si c'est une structure.
-->
<xsl:template name="initIfStructure">
    <xsl:if test="@structure"><xsl:text>= new </xsl:text><xsl:value-of select="@structure"/>()</xsl:if>
</xsl:template>

</xsl:stylesheet>

