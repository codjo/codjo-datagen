<?xml version="1.0"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:java="http://xml.apache.org/xslt/java"
    xmlns:database="net.codjo.datagen.DatabaseScriptHelperXslAdapter"
    exclude-result-prefixes="java">
<xsl:output method="text" indent="yes" omit-xml-declaration="yes"/>
<xsl:param name="entityName">gen.Dividend</xsl:param>

<xsl:template match="/">
    <xsl:apply-templates select="data/entity[@name=$entityName and feature/sql-index]"/>
</xsl:template>

<xsl:template match="entity">
/*=============================*/
/*   Suppression des indexes   */
/*=============================*/

<xsl:apply-templates select="feature/sql-index/*" mode="delete_index"/>

/*==========================*/
/*   Creation des indexes   */
/*==========================*/

<xsl:apply-templates select="feature/sql-index/*" mode="create_index"/>

/*==============================*/
/*   Verification des indexes   */
/*==============================*/

<xsl:apply-templates select="feature/sql-index/*" mode="check_index"/>
</xsl:template>

<!-- ***************************************************************************
    Suppression des index
-->

<xsl:template match="idx" mode="delete_index">
<xsl:apply-templates select="database:buildDropIndexScript(.)"/>
<xsl:text>&#xa;</xsl:text>
</xsl:template>

<!-- ***************************************************************************
    Creation des index
-->

<xsl:template match="idx" mode="create_index">
<xsl:apply-templates select="database:buildCreateIndexScript(.)"/>
<xsl:text>&#xa;</xsl:text>
</xsl:template>

<!-- ***************************************************************************
    Verification des index
-->

<xsl:template match="idx" mode="check_index">
<xsl:apply-templates select="database:buildLogIndexCreationScript(.)"/>
<xsl:text>&#xa;</xsl:text>
</xsl:template>
</xsl:stylesheet>


