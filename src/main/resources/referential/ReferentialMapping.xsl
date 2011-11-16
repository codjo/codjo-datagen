<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      version="1.0"
      xmlns:java="http://xml.apache.org/xslt/java"
      exclude-result-prefixes="java">
    <xsl:output method="xml" encoding="iso-8859-1" indent="yes"/>

    <xsl:template match="/">
        <referentialList>
            <xsl:apply-templates select="//data/entity[feature/referential]"/>
        </referentialList>
    </xsl:template>

    <xsl:template match="entity">
        <xsl:variable name="className" select="java:bean.Util.extractClassName(@name)"/>

        <linkReferential name="{$className}" preferenceId="{$className}List" label="{description}">
            <xsl:for-each select="properties/field">
                <field name="{@name}" label="{description}">
                    <xsl:copy-of select="@type"/>
                    <xsl:copy-of select="@structure"/>
                    <xsl:copy-of select="sql/@precision"/>
                    <xsl:copy-of select="sql/@required"/>
                    <xsl:copy-of select="sql/@default"/>
                    <xsl:variable name="fieldName" select="@name"/>
                    <xsl:choose>
                        <xsl:when test="../../primary-key/field[@name=$fieldName]">
                            <xsl:attribute name="primary">true</xsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="primary">false</xsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:attribute name="isGenerated">
                        <xsl:choose>
                            <xsl:when test="../../primary-key[@key-generator]/field[@name=$fieldName]">true</xsl:when>
                            <xsl:otherwise>false</xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                    <xsl:for-each select="../../feature/referential/fill">
                            <xsl:if test="@field=$fieldName">
                                <refHandler id="{@handlerId}"/>
                            </xsl:if>
                    </xsl:for-each>
                </field>
            </xsl:for-each>
        </linkReferential>
    </xsl:template>

</xsl:stylesheet>

