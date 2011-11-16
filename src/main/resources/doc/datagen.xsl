<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      xmlns:xs="http://www.w3.org/2001/XMLSchema"
      version="1.0">

    <xsl:template match="/">
        <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
                   elementFormDefault="qualified"
                   attributeFormDefault="unqualified">

            <xs:annotation>
                <xs:appinfo>Schema XML des fichiers datagen</xs:appinfo>
                <xs:documentation xml:lang="fr">
                    Ce schéma définit le format des fichiers XML permettant de générer les
                    handlers et les scripts sql.
                </xs:documentation>
            </xs:annotation>
            <xsl:apply-templates select="/syntaxe/tag"/>
        </xs:schema>
    </xsl:template>

    <xsl:template match="/syntaxe/tag">
        <xs:element name="{@id}">
            <xs:annotation>
                <xs:documentation xml:lang="fr">
                    <xsl:value-of select="description"/>
                </xs:documentation>
            </xs:annotation>
            <xs:complexType>
                <xsl:if test="sub-tag/tag">
                    <xs:choice>
                        <xsl:apply-templates select="sub-tag/tag"/>
                    </xs:choice>
                </xsl:if>
                <xsl:apply-templates select="attributes-tag/attribute"/>
            </xs:complexType>
        </xs:element>
    </xsl:template>

    <xsl:template match="sub-tag/tag">
        <xs:element>
            <xsl:if test="@refId">
                <!--<xsl:attribute name="ref">-->
                    <!--<xsl:variable name="refId">-->
                        <!--<xsl:value-of select="@refId"/>-->
                    <!--</xsl:variable>-->
                    <!--<xsl:value-of select="//tag[@id=$refId]/@name"/>-->
                <!--</xsl:attribute>-->
                <xsl:attribute name="name">
                    <xsl:variable name="refId">
                        <xsl:value-of select="@refId"/>
                    </xsl:variable>
                    <xsl:value-of select="//tag[@id=$refId]/@name"/>
                </xsl:attribute>
                <xsl:attribute name="ref">
                    <xsl:value-of select="@refId"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@minOccurs">
                <xsl:attribute name="minOccurs">
                    <xsl:value-of select="@minOccurs"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@maxOccurs">
                <xsl:attribute name="maxOccurs">
                    <xsl:value-of select="@maxOccurs"/>
                </xsl:attribute>
            </xsl:if>
        </xs:element>
    </xsl:template>

    <xsl:template match="attributes-tag/attribute">
        <xs:attribute>
            <xsl:if test="@name">
                <xsl:attribute name="name">
                    <xsl:value-of select="@name"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@use='required'">
                <xsl:attribute name="use">
                    <xsl:value-of select="@use"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@type">
                <xsl:attribute name="type">
                    <xsl:value-of select="@type"/>
                </xsl:attribute>
            </xsl:if>
            <xs:annotation>
                <xs:documentation xml:lang="fr">
                    <xsl:value-of select="description"/>
                </xs:documentation>
            </xs:annotation>
        </xs:attribute>
    </xsl:template>
</xsl:stylesheet>