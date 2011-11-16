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
            <xsl:apply-templates select="//data/entity"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="entity[feature/sql]">
        <xsl:element name="table">
            <xsl:attribute name="name">
                <xsl:choose>
                    <xsl:when test="feature/sql/@name">
                        <xsl:value-of select="feature/sql/@name"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="@table"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:attribute name="entity">
                <xsl:value-of select="@name"/>
            </xsl:attribute>

            <xsl:apply-templates select="properties/field"/>

            <xsl:if test="@extends">
                <xsl:variable name="extends" select="@extends"/>
                <xsl:apply-templates select="//entity[@name = $extends]/properties/field"/>
            </xsl:if>
        </xsl:element>

        <xsl:apply-templates select="feature/user-quarantine"/>
    </xsl:template>

    <xsl:template match="feature/user-quarantine">
        <xsl:if test="@table">
            <xsl:element name="table">
                <xsl:attribute name="name">
                    <xsl:value-of select="@table"/>
                </xsl:attribute>
                <xsl:attribute name="entity">
                    <xsl:value-of select="@name"/>
                </xsl:attribute>

                <xsl:apply-templates select="ancestor::entity/properties/field"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template match="field[@structure]">
        <xsl:variable name="structure" select="@structure"/>

        <xsl:apply-templates select="//structure[@name = $structure]/properties/field"/>
    </xsl:template>

    <xsl:template match="field">
        <xsl:element name="field">
            <xsl:attribute name="name">
                <xsl:value-of select="java:sql.Util.toSqlName(@name)"/>
            </xsl:attribute>
            <xsl:attribute name="entity">
                <xsl:value-of select="ancestor::entity/@name"/>
            </xsl:attribute>
            <xsl:attribute name="structure">
                <xsl:value-of select="ancestor::structure/@name"/>
            </xsl:attribute>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>

