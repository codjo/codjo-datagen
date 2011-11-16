<?xml version="1.0"?>
<xsl:stylesheet
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      version="1.0"
      xmlns:java="http://xml.apache.org/xslt/java"
      exclude-result-prefixes="java">

    <xsl:import href="/kernel/Kernel.xsl"/>
    <xsl:output method="text" omit-xml-declaration="yes"/>
    <xsl:param name="entityName"></xsl:param>

    <xsl:template match="/">
        <xsl:apply-templates select="data/*[@name=$entityName][feature/glob]"/>
    </xsl:template>

    <xsl:template match="entity">
      <xsl:variable name="className" select="java:bean.Util.extractClassName(@name)"/>

package <xsl:value-of select="java:globs.Util.extractPackage(@name)"/>;

import org.crossbowlabs.globs.metamodel.*;
import org.crossbowlabs.globs.metamodel.utils.GlobTypeLoader;
import org.crossbowlabs.globs.metamodel.fields.*;
import org.crossbowlabs.globs.metamodel.annotations.*;

@SuppressWarnings({"ALL"})
public class <xsl:value-of select="$className"/> {

    public static GlobType TYPE;

    <xsl:apply-templates select="properties/field"/>
    <xsl:apply-templates select="foreign-keys/link" mode="declareLink"/>

    static {
        GlobTypeLoader loader = GlobTypeLoader.init(<xsl:value-of select="$className"/>.class);
        <xsl:apply-templates select="foreign-keys/link" mode="defineLink"/>
    }
}
</xsl:template>
    <xsl:template match="properties/field">
        <xsl:variable name="fieldName" select="@name"/>
        <xsl:variable name="type" select="@type"/>
        <xsl:variable name="sqlName" select="java:sql.Util.toSqlName(@name)"/>
        <xsl:choose>
            <xsl:when test="../../primary-key/field[@name=$fieldName]">
    @Key
            </xsl:when>
            <xsl:otherwise>
                <xsl:if test="sql/@required='true'">
    @Required
                </xsl:if>
                <xsl:if test="@namingField='true'">
    @NamingField
                </xsl:if>
                <xsl:if test="@encrypted='true'">
    @Encrypted
                </xsl:if>
                <xsl:if test="@multiLineText='true'">
    @MultiLineText
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="$type='string'">
    @MaxSize(<xsl:choose>
            <xsl:when test="sql/@precision"><xsl:value-of select="sql/@precision"/></xsl:when>
            <xsl:when test="sql/@type='text'">2147483647</xsl:when>
            <xsl:otherwise>1</xsl:otherwise>
            </xsl:choose>)
        </xsl:if>
        <xsl:choose>
            <xsl:when test="@type">
    public static <xsl:apply-templates select="@type"/><xsl:text> </xsl:text> <xsl:value-of select="$sqlName"/>;
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="@structure"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="foreign-keys/link" mode="declareLink">
        <xsl:variable name="linkName" select="java:globs.Util.toLinkName(@id, @name)"/>
        <xsl:if test="@required='true'">
    @Required
       </xsl:if>
    public static Link <xsl:value-of select="$linkName"/>;
    </xsl:template>
    <xsl:template match="foreign-keys/link" mode="defineLink">
        <xsl:variable name="linkName" select="java:globs.Util.toLinkName(@id, @name)"/>
        <xsl:variable name="toClass" select="java:globs.Util.transformFullClassName(@table)"/>
        loader.defineLink(<xsl:value-of select="$linkName"/>)
        <xsl:for-each select="field">
            <xsl:variable name="fromField" select="java:sql.Util.toSqlName(@from)"/>
            <xsl:variable name="toField" select="java:sql.Util.toSqlName(@to)"/>
            <xsl:choose>
                <xsl:when test="position()!=last()">
              .add(<xsl:value-of select="$fromField"/>, <xsl:value-of select="$toClass"/>.<xsl:value-of select="$toField"/>)
                </xsl:when>
                <xsl:otherwise>
              .add(<xsl:value-of select="$fromField"/>, <xsl:value-of select="$toClass"/>.<xsl:value-of select="$toField"/>);
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>
    <xsl:template match="@type">
        <xsl:variable name="type" select="."/>
        <xsl:choose>
            <xsl:when test="$type='boolean'">BooleanField</xsl:when>
            <xsl:when test="$type='integer'">IntegerField</xsl:when>
            <xsl:when test="$type='java.lang.Integer'">IntegerField</xsl:when>
            <xsl:when test="$type='big-decimal'">IntegerField</xsl:when>
            <xsl:when test="$type='double'">DoubleField</xsl:when>
            <xsl:when test="$type='string'">StringField</xsl:when>
            <xsl:when test="$type='java.sql.Date'">DateField</xsl:when>
            <xsl:when test="$type='java.sql.Timestamp'">DateField</xsl:when>
            <xsl:otherwise><xsl:value-of select="$type"/></xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="@structure">
        <xsl:variable name="structure" select="."/>
        <xsl:for-each select="//data/structure">
            <xsl:if test="@name=$structure">
                <xsl:apply-templates select="properties/field"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
