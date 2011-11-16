<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      version="1.0">
    <xsl:output method="xml" encoding="iso-8859-1" indent="yes"/>

    <xsl:template match="/">
        <data>
            <xsl:apply-templates/>
        </data>
    </xsl:template>

    <xsl:template match="feature" priority="3">
        <xsl:variable name="name">
            <xsl:value-of select="../@name"/>
        </xsl:variable>
        <feature>
            <xsl:copy-of select="*"/>
            <xsl:copy-of select="/data/add-handler-sql/handler-sql[../@to=$name]"/>
        </feature>
    </xsl:template>

    <xsl:template match="add-handler-sql" priority="1">
        
    </xsl:template>
    <xsl:template match="entity" priority="1">
        <entity>
            <xsl:for-each select="@*">
                <xsl:attribute name="{local-name()}">
                    <xsl:value-of select="."/>
                </xsl:attribute>
            </xsl:for-each>
            <xsl:apply-templates/>
        </entity>
    </xsl:template>

    <!-- récupération du contenu des xml part -->
    <xsl:template match="data/*" priority="0">
        <xsl:copy-of select="."/>
    </xsl:template>

    <!-- récupération du contenu des xml part -->
    <xsl:template match="data/entity/*" priority="2">
        <xsl:copy-of select="."/>
    </xsl:template>
</xsl:stylesheet>