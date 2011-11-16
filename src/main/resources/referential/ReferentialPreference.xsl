<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      version="1.0"
      xmlns:java="http://xml.apache.org/xslt/java"
      exclude-result-prefixes="java">
    <xsl:output method="xml" encoding="iso-8859-1" indent="yes"/>

    <xsl:template match="/">
        <preferenceList>
            <xsl:apply-templates select="//data/entity[feature/referential]"/>
        </preferenceList>
    </xsl:template>

    <xsl:template match="entity">
        <xsl:variable name="className" select="java:bean.Util.extractClassName(@name)"/>
        <preference id="{$className}List" detailWindowClassName="net.codjo.referential.gui.ReferentialDetailLogic">
            <entity><xsl:value-of select="$className"/></entity>
            <selectAll>selectAll<xsl:value-of select="$className"/></selectAll>
            <selectByPk>select<xsl:value-of select="$className"/>ById</selectByPk>
            <update>update<xsl:value-of select="$className"/></update>
            <delete>delete<xsl:value-of select="$className"/></delete>
            <insert>new<xsl:value-of select="$className"/></insert>
            <requetor>all<xsl:value-of select="$className"/></requetor>

            <xsl:for-each select="properties/field">
                <xsl:if test="sql">
                    <xsl:variable name="numberFormat" select="java:bean.Util.generateFormatString(sql/@precision)"/>
                    <xsl:choose>
                        <xsl:when test="@type='java.sql.Date'">
                            <column fieldName="{@name}" label="{description}"
                                    renderer="net.codjo.mad.gui.request.util.renderers.HideInfiniteDateRenderer"
                                    format="date(dd/MM/yyyy)" preferredSize="30"/>
                        </xsl:when>
                        <xsl:when test="@type='boolean'">
                            <column fieldName="{@name}" label="{description}"
                                    sorter="Boolean" preferredSize="20"/>
                        </xsl:when>
                        <xsl:when test="@type='integer'">
                            <column fieldName="{@name}" label="{description}"
                                    sorter="Numeric" preferredSize="50"/>
                        </xsl:when>
                        <xsl:when test="@type='big-decimal'">
                            <column fieldName="{@name}" label="{description}"
                                    format="Numeric({$numberFormat})" sorter="Numeric" preferredSize="50"/>
                        </xsl:when>
                        <xsl:when test="@type='double'">
                            <column fieldName="{@name}" label="{description}"
                                    format="Numeric({$numberFormat})" sorter="Numeric" preferredSize="50"/>
                        </xsl:when>
                        <xsl:when test="@type='long'">
                            <column fieldName="{@name}" label="{description}"
                                    format="Numeric({$numberFormat})" sorter="Numeric" preferredSize="50"/>
                        </xsl:when>
                        <xsl:when test="@type='java.math.BigDecimal'">
                            <column fieldName="{@name}" label="{description}"
                                    format="Numeric({$numberFormat})" sorter="Numeric" preferredSize="50"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <column fieldName="{@name}" label="{description}"
                                    preferredSize="{sql/@precision}"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:if>
            </xsl:for-each>

        </preference>
    </xsl:template>

</xsl:stylesheet>

