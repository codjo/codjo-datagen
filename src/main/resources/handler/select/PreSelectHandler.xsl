<?xml version="1.0"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:java="http://xml.apache.org/xslt/java"
    exclude-result-prefixes="java">
<xsl:output method="xml" indent="yes"/>

<!-- ***************************************************************************
    Requete de recherche sans contrainte - type = 'All'
-->
<xsl:template match="feature/handler-select[@type='All']" priority="1">
    <handler-select id="{@id}">
             <query type='SQL' >SELECT * FROM <xsl:value-of select="../../@table"/> p</query>
    </handler-select>
</xsl:template>

<!-- ***************************************************************************
    Requete de recherche par clef primaire - type = 'By_Primary-Key'
-->
<xsl:template match="feature/handler-select[@type='By_Primary-Key']" priority="1">
    <handler-select id="{@id}">
            <query type='OQL'>SELECT p FROM <xsl:value-of select="../../@name"/> p WHERE <xsl:apply-templates select="../../primary-key/field" mode="By_Primary-Key_oql"/></query>
            <xsl:apply-templates select="../../primary-key/field" mode="By_Primary-Key_declareArg"/>
    </handler-select>
</xsl:template>

<xsl:template match="primary-key/field" mode="By_Primary-Key_declareArg">
    <arg><xsl:value-of select="@name"/></arg>
</xsl:template>

<xsl:template match="primary-key/field" mode="By_Primary-Key_oql">
    <xsl:if test="position()>1"> and </xsl:if> <xsl:value-of select="@name"/> = $<xsl:value-of select="position()"/>
</xsl:template>

<!-- ***************************************************************************
    Copy de la structure XML
-->
<xsl:template match="/">
    <data>
   <xsl:apply-templates/>
   </data>
</xsl:template>

<xsl:template match="feature/*" >
    <xsl:copy-of select="."/>
</xsl:template>

<xsl:template match="entity">
    <entity>
        <xsl:attribute name="name"> <xsl:value-of select="@name" /> </xsl:attribute>
        <xsl:attribute name="table"> <xsl:value-of select="@table" /> </xsl:attribute>
        <xsl:copy-of select="description"/>

        <feature> <xsl:apply-templates select="feature"/> </feature>

        <xsl:copy-of select="primary-key"/>
        <xsl:copy-of select="methods"/>
        <xsl:copy-of select="properties"/>
    </entity>
</xsl:template>

<xsl:template match="structure">
    <xsl:copy-of select="."/>
</xsl:template>

</xsl:stylesheet>

