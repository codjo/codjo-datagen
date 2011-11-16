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
    <xsl:apply-templates select="data/entity[@name=$entityName and feature/trigger-iu]"/>
</xsl:template>

<xsl:template match="entity">
<xsl:apply-templates select="database:buildCreateTriggerScript(./feature/trigger-iu)"/>
</xsl:template>

</xsl:stylesheet>
