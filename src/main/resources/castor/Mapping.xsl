<?xml version="1.0"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:java="http://xml.apache.org/xslt/java"
    exclude-result-prefixes="java">
<xsl:output method="xml" indent="yes"/>

    <!-- ***************************************************************************
        Démarrage
    -->
    <xsl:template match="/">
        <mapping>
            <!-- ***************************************************************************
                Gestion des sequences ORACLE
            -->
            <xsl:if test="/data/entity/primary-key/@key-generator='SEQUENCE'">
                <key-generator name="SEQUENCE" alias="ORACLE_SEQUENCE">
                    <param name="returning" value="true"/>
                    <param name="trigger" value="true"/>
                </key-generator>
            </xsl:if>
            <xsl:apply-templates select="//data/*[feature/castor]"/>
        </mapping>
    </xsl:template>

<!-- ***************************************************************************
    Declaration du mapping d'une entity
-->
<xsl:template match="entity">
    <class name="{@name}"
         identity="{java:castor.Util.toList(primary-key/field)}">
        <xsl:if test="primary-key/@key-generator">
            <xsl:attribute name="key-generator"><xsl:value-of select="java:castor.Util.mapCastorKeyGenerator(primary-key/@key-generator)"/></xsl:attribute>
        </xsl:if>
        <cache-type type="none"/>
        <map-to table="{@table}" />

        <xsl:apply-templates select="properties/field" />
    </class>
</xsl:template>

<!-- ***************************************************************************
    Declaration des property directe du bean
-->
<xsl:template match="properties/field[@type]" >
    <xsl:param name="name"><xsl:value-of select="@name"/></xsl:param>
    <xsl:param name="propertyPrefix" />

    <field name="{$propertyPrefix}{@name}" type="{@type}" >
        <sql>
            <xsl:attribute name="name"> <xsl:value-of select="java:sql.Util.toSqlName(@name)" /> </xsl:attribute>
            <xsl:choose>
                <xsl:when test="@link"></xsl:when>
                <xsl:otherwise><xsl:attribute name="type"> <xsl:apply-templates select="sql/@type" mode="sqlType" /> </xsl:attribute></xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="../../primary-key/field[@name=$name]"></xsl:when>
                <xsl:otherwise><xsl:attribute name="dirty">ignore</xsl:attribute></xsl:otherwise>
            </xsl:choose>
        </sql>
    </field>
</xsl:template>
<!-- ***************************************************************************
    Declaration des property directe du bean
-->
<xsl:template match="sql/@type" mode="sqlType" >
    <xsl:choose>
        <xsl:when test=".='text'">varchar</xsl:when>
        <xsl:otherwise><xsl:value-of select="." /></xsl:otherwise>
    </xsl:choose>
</xsl:template>
<!-- ***************************************************************************
    Declaration des property indirecte du bean (au travers d'une structure)
-->
<xsl:template match="properties/field[@structure]" >
    <xsl:param name="name"><xsl:value-of select="@name"/></xsl:param>
    <xsl:param name="structure"><xsl:value-of select="@structure"/></xsl:param>

    <xsl:comment> Declaration de la structure <xsl:value-of select="$structure"/> </xsl:comment>
    <xsl:for-each select="//data/structure[@name=$structure]">
        <xsl:apply-templates select="properties/field">
            <xsl:with-param name="propertyPrefix"><xsl:value-of select="$name"/>.</xsl:with-param>
        </xsl:apply-templates>
    </xsl:for-each>
</xsl:template>
</xsl:stylesheet>

