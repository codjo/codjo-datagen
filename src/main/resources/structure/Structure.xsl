<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      version="1.0"
      xmlns:java="http://xml.apache.org/xslt/java"
      exclude-result-prefixes="java">
    <xsl:import href="/kernel/Kernel.xsl"/>
    <xsl:output method="xml" indent="yes" encoding="ASCII"/>

    <!-- ***************************************************************************
        Démarrage
    -->
    <xsl:template match="/">
        <structure>
            <xsl:apply-templates select="data/entity[feature/doc-structure]"/>
        </structure>
    </xsl:template>

    <!-- ***************************************************************************
        Declaration d'une table
    -->
    <xsl:template match="entity">
        <xsl:variable name="tableName">
            <xsl:value-of select="@table"/>
        </xsl:variable>

        <xsl:variable name="tableLabel">
            <xsl:choose>
                <xsl:when test="description">
                    <xsl:value-of select="java:bean.Util.removeNewLineAndTrim(description)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$tableName"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <table sql="{$tableName}" name="{java:bean.Util.extractClassName(@name)}" label="{$tableLabel}"
               type="{@type}">
            <xsl:apply-templates select="feature/referential"/>
            <xsl:apply-templates select="properties/field"/>
        </table>
    </xsl:template>

    <!-- ***************************************************************************
        Traitement des structures.
    -->
    <xsl:template match="properties/field[@structure]">
        <xsl:variable name="structureName">
            <xsl:value-of select="@structure"/>
        </xsl:variable>

        <xsl:for-each select="//data/structure[@name=$structureName]/properties/field">

            <xsl:variable name="dbFieldName">
                <xsl:value-of select="java:sql.Util.toSqlName(@name)"/>
            </xsl:variable>
            <xsl:variable name="label">
                <xsl:choose>
                    <xsl:when test="description">
                        <xsl:value-of select="java:bean.Util.removeNewLineAndTrim(description)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$dbFieldName"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <field sql="{$dbFieldName}" name="{@name}" label="{$label}">
                <xsl:if test="sql/@type">
                    <xsl:attribute name="sql-type">
                        <xsl:choose>
                            <xsl:when test="sql/@type='text'">LONGVARCHAR</xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="sql/@type"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="sql/@precision">
                    <xsl:attribute name="sql-precision">
                        <xsl:value-of select="sql/@precision"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="sql/@required">
                    <xsl:attribute name="sql-required">
                        <xsl:value-of select="sql/@required"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="referential">
                    <xsl:attribute name="referential">
                        <xsl:choose>
                            <xsl:when test="referential/@name='net.codjo.pims.ref.Referential'">Referential</xsl:when>
                            <xsl:when test="referential/@name='net.codjo.pims.ref.Person'">Person</xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="referential/@name"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                </xsl:if>
            </field>
        </xsl:for-each>

    </xsl:template>
    <!-- ***************************************************************************
        Déclaration des champs d'une table.
    -->
    <xsl:template match="properties/field[@type]">
        <xsl:variable name="dbFieldName">
            <xsl:value-of select="java:sql.Util.toSqlName(@name)"/>
        </xsl:variable>
        <xsl:variable name="fieldName">
            <xsl:value-of select="@name"/>
        </xsl:variable>
        <xsl:variable name="label">
            <xsl:choose>
                <xsl:when test="description">
                    <xsl:value-of select="java:bean.Util.removeNewLineAndTrim(description)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$dbFieldName"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <field sql="{$dbFieldName}" name="{@name}" label="{$label}">
            <xsl:if test="sql/@type">
                <xsl:attribute name="sql-type">
                    <xsl:choose>
                        <xsl:when test="sql/@type='text'">LONGVARCHAR</xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="sql/@type"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="sql/@precision">
                <xsl:attribute name="sql-precision">
                    <xsl:value-of select="sql/@precision"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="sql/@required">
                <xsl:attribute name="sql-required">
                    <xsl:value-of select="sql/@required"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="referential">
                <xsl:attribute name="referential">
                    <xsl:choose>
                        <xsl:when test="referential/@name='net.codjo.pims.ref.Referential'">Referential</xsl:when>
                        <xsl:when test="referential/@name='net.codjo.pims.ref.Person'">Person</xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="referential/@name"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="../../primary-key/field[@name=$fieldName]">
                <xsl:attribute name="sql-primary-key">true</xsl:attribute>
            </xsl:if>
            <xsl:if test="../../functional-key/field[@name=$fieldName]">
                <xsl:attribute name="functional-key">true</xsl:attribute>
            </xsl:if>
        </field>

    </xsl:template>

</xsl:stylesheet>

