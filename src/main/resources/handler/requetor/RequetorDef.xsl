<?xml version="1.0"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:java="http://xml.apache.org/xslt/java"
    exclude-result-prefixes="java">
    <xsl:import href="/kernel/Kernel.xsl"/>
    <xsl:output method="xml" indent="yes" encoding="ISO-8859-1"/>

    <!-- ***************************************************************************
        Démarrage
    -->
    <xsl:template match="/">
        <link-def>
            <xsl:apply-templates select="data/entity/feature/handler-requetor"/>
        </link-def>
    </xsl:template>

    <!-- ***************************************************************************
        Declaration d'une links
    -->
    <xsl:template match="handler-requetor">
        <links root="{../../@table}" id="{@id}">
            <xsl:apply-templates select="link"/>
        </links>
    </xsl:template>

    <xsl:template match="link">
        <link type="inner join" to="{@to}" from="{../../../@table}">
            <xsl:apply-templates select="key"/>
        </link>
    </xsl:template>

    <xsl:template match="key">
        <xsl:choose>
            <xsl:when test="@operator">
                <key to="{@to}" operator="{@operator}" from="{@from}"/>
            </xsl:when>
            <xsl:otherwise>
                <key to="{@to}" operator="=" from="{@from}"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>

