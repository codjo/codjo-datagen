<?xml version="1.0"?>
<xsl:stylesheet
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      version="1.0"
      xmlns:java="http://xml.apache.org/xslt/java"
      xmlns:database="net.codjo.datagen.DatabaseScriptHelperXslAdapter"
      exclude-result-prefixes="java">
    <xsl:import href="/sql/SQLType.xsl"/>
    <xsl:output method="text" indent="yes" omit-xml-declaration="yes"/>
    <xsl:param name="entityName">gen.Dividend</xsl:param>
    <xsl:param name="tableName">AP_DIVIDEND</xsl:param>
    <xsl:param name="pk-generator">yes</xsl:param>
    <xsl:param name="databaseType">sybase</xsl:param>

<xsl:template match="/">
    <xsl:apply-templates select="data/entity[@name=$entityName and feature/sql]"/>
</xsl:template>

<xsl:template match="entity">
/* ========================================================================== */
/*   Generation Automatique : <xsl:value-of select="@name"/>   */
/* ========================================================================== */

<xsl:apply-templates select="database:buildDropTableScript(.)"/>

/* ========================================================================== */

<xsl:apply-templates select="database:buildCreateTableScript(., $pk-generator)"/>

/* ========================================================================== */

<xsl:apply-templates select="database:buildLogTableCreationScript(.)"/>
</xsl:template>
</xsl:stylesheet>



