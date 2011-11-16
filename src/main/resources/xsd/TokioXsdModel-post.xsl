<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      version="1.0"
      xmlns:java="http://xml.apache.org/xslt/java"
      exclude-result-prefixes="java">
    <xsl:import href="/kernel/Kernel.xsl"/>
    <xsl:output method="xml" encoding="iso-8859-1" indent="yes"/>

    <xsl:template match="/">
        <xsl:element name="tokio">
            <xsl:apply-templates select="//table"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="table">
        <xsl:copy>
            <xsl:attribute name="name">
                <xsl:value-of select="@name"/>
            </xsl:attribute>
            <xsl:apply-templates select="field"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="field">
        <xsl:variable name="name" select="@name"/>
        <xsl:if test="@entity = ../@entity or count(../field[@name = $name]) = 1">
            <xsl:copy>
                <xsl:attribute name="name">
                    <xsl:value-of select="@name"/>
                </xsl:attribute>
            </xsl:copy>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>

