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
    <xsl:apply-templates select="data/entity[@name=$entityName and feature/sql-constraint]"/>
</xsl:template>

<xsl:template match="entity">
/*=================================*/
/*   Suppression des contraintes   */
/*=================================*/

<xsl:apply-templates select="foreign-keys/*" mode="delete_constraint"/>

/*==============================*/
/*   Creation des contraintes   */
/*==============================*/

<xsl:apply-templates select="foreign-keys/*" mode="create_constraint"/>

/*==================================*/
/*   Verification des contraintes   */
/*==================================*/

<xsl:apply-templates select="foreign-keys/*" mode="check_constraint"/>
</xsl:template>

<!-- ***************************************************************************
    Suppression des contraintes
-->

<xsl:template match="link" mode="delete_constraint">
<xsl:apply-templates select="database:buildDropConstraintScript(.)"/>
<xsl:text>&#xa;</xsl:text>
</xsl:template>

<!-- ***************************************************************************
    Creation des contraintes
-->

<xsl:template match="link" mode="create_constraint">
<xsl:apply-templates select="database:buildCreateConstraintScript(.)"/>
<xsl:text>&#xa;</xsl:text>
</xsl:template>

<!-- ***************************************************************************
    Verification des contraintes
-->

<xsl:template match="link" mode="check_constraint">
<xsl:apply-templates select="database:buildLogConstraintCreationScript(.)"/>
<xsl:text>&#xa;</xsl:text>
</xsl:template>
</xsl:stylesheet>


