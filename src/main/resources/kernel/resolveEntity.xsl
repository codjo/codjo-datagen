<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      version="1.0"
      xmlns:java="http://xml.apache.org/xslt/java"
      exclude-result-prefixes="java">
    <xsl:output method="xml" encoding="iso-8859-1" indent="yes"/>

    <xsl:template match="/">
        <data>
            <xsl:apply-templates/>
        </data>
    </xsl:template>

    <!-- récupération du contenu des xml part -->
    <xsl:template name="include">
        <xsl:param name="path"/>
        <xsl:copy-of
              select="java:kernel.Util.document(@name)/*"/>
    </xsl:template>

    <!-- récupération du contenu des xml part -->
    <xsl:template match="data" priority="0">
        <xsl:apply-templates mode="h"/>
    </xsl:template>

    <xsl:template match="*" mode="h">
        <xsl:choose>
            <xsl:when test="name()='include' and name(./..)='data'">
                <xsl:call-template name="include">
                    <xsl:with-param name="path" select="@name"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="name()!='entities'">
                        <xsl:element name="{name()}">
                        <xsl:copy-of select="attribute::*"/>
                        <xsl:apply-templates mode="h"/>
                    </xsl:element>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates mode="h"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>


