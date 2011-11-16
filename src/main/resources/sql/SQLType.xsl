<?xml version="1.0"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:java="http://xml.apache.org/xslt/java"
    exclude-result-prefixes="java">

<!-- ***************************************************************************
    Transcodage du type sql d'une property
-->
<xsl:template match="sql/@type">
    <xsl:variable name="type" select="."/>
    <xsl:choose>
        <xsl:when test="$type='timestamp'">datetime</xsl:when>
        <xsl:when test="$type='integer'">int</xsl:when>
        <xsl:when test="$type='longvarchar'">text</xsl:when>
        <xsl:otherwise><xsl:value-of select="$type"/></xsl:otherwise>
    </xsl:choose>
</xsl:template>

</xsl:stylesheet>

