<?xml version="1.0"?>
<xsl:stylesheet
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      version="1.0"
      xmlns:java="http://xml.apache.org/xslt/java"
      exclude-result-prefixes="java">

    <xsl:output method="xml" encoding="iso-8859-1" indent="yes"/>

    <!-- ***************************************************************************
        Copy de la structure XML
    -->
    <xsl:template match="/">
        <xsl:apply-templates select="//data/roles"/>
    </xsl:template>

    <xsl:template match="roles">
        <roles>
            <xsl:apply-templates select="./*"/>
        </roles>
    </xsl:template>
    <xsl:template match="role">
        <role id="{@id}">
            <xsl:copy-of select="./include"/>
            <xsl:copy-of select="./exclude"/>
            <xsl:apply-templates select="include-handler"/>
        </role>
    </xsl:template>
    <xsl:template match="include-handler">
        <xsl:value-of select="java:role.RoleUtil.toInclude(//data ,@entity, @pattern)" disable-output-escaping="yes"/>
    </xsl:template>

</xsl:stylesheet>

