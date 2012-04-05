<?xml version="1.0"?>
<xsl:stylesheet
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      version="1.0"
      xmlns:java="http://xml.apache.org/xslt/java"
      exclude-result-prefixes="java">
    <xsl:import href="/kernel/Kernel.xsl"/>
    <xsl:output method="text" omit-xml-declaration="yes"/>
    <xsl:param name="entityName">generated.Dividend</xsl:param>
    <xsl:param name="databaseType">sybase</xsl:param>

    <!-- ***************************************************************************
        Démarrage
    -->
    <xsl:template match="/">
        <xsl:apply-templates select="data/entity[@name=$entityName]"/>
    </xsl:template>

    <!-- ***************************************************************************
        Declaration du corps du handler
    -->
    <xsl:template match="entity">
        <xsl:variable name="entityClassName" select="java:bean.Util.extractClassName(@name)"/>
        <xsl:variable name="handlerClasseName"
                      select="java:kernel.Util.handlerClassName(concat('AbstractSelect',$entityClassName))"/>
        package <xsl:value-of select="java:bean.Util.extractPackage(@name)"/>;
        import net.codjo.database.api.Database;
        import net.codjo.mad.server.handler.XMLUtils;
        import net.codjo.mad.server.handler.select.AbstractSelectHandler;
        import net.codjo.mad.server.handler.select.Getter;
        import java.sql.ResultSet;
        import java.sql.SQLException;
        /**
        * Classe Handler de selection abstraite pour <xsl:value-of select="@name"/>.
        */
        public abstract class
        <xsl:value-of select="$handlerClasseName"/>
        extends AbstractSelectHandler&lt;<xsl:value-of select="$entityClassName"/>> {

        protected <xsl:value-of select="$handlerClasseName"/>(int queryType, String selectQuery, Database database) {
        super(queryType,
        selectQuery,
        new String[]{<xsl:apply-templates select="primary-key/field" mode="declare-as-list"/>},
        database);
        <xsl:apply-templates select="properties/field"/>
        }
        }
    </xsl:template>

    <!-- ***************************************************************************
        Declaration des property de type structure du bean pour le constructeur
    -->
    <xsl:template match="properties/field[@structure]">
        <xsl:param name="structure">
            <xsl:value-of select="@structure"/>
        </xsl:param>
        <xsl:variable name="cname" select="java:bean.Util.capitalize(@name)"/>
        <xsl:apply-templates select="//data/structure[@name=$structure]/properties/field">
            <xsl:with-param name="obj">dv.get<xsl:value-of select="$cname"/>()
            </xsl:with-param>
            <xsl:with-param name="className">
                <xsl:value-of select="../../@name"/>
            </xsl:with-param>
            <xsl:with-param name="relativePos">
                <xsl:value-of select="java:bean.Util.index(.)"/>
            </xsl:with-param>
        </xsl:apply-templates>
    </xsl:template>

    <!-- ***************************************************************************
        Declaration des property du bean pour le constructeur
    -->
    <xsl:template match="properties/field[@type]">
        <xsl:param name="obj">dv</xsl:param>
        <xsl:param name="className" select="../../@name"/>
        <xsl:param name="relativePos">0</xsl:param>
        <xsl:param name="name" select="@name"/>
        <xsl:variable name="cname" select="java:bean.Util.capitalize(@name)"/>
        <xsl:variable name="entityClassName" select="java:bean.Util.extractClassName($className)"/>
        <xsl:variable name="type">
            <xsl:apply-templates select="@type"/>
        </xsl:variable>
        <xsl:variable name="javaSqlTypesConstant" select="java:handler.Util.convertTypeJavaSqlTypesConstant($type)"/>
        <xsl:variable name="fieldIndex">
            <xsl:value-of select="java:bean.Util.index(.) + $relativePos + 1"/>
        </xsl:variable>
        <xsl:variable name="constructorParameter"
                      select="java:handler.Util.buildGetterConstructorParameter($fieldIndex,$javaSqlTypesConstant)"/>
        addGetter("<xsl:value-of select="$name"/>",
        new Getter&lt;<xsl:value-of select="$entityClassName"/>&gt;(<xsl:value-of select="$constructorParameter"/>) {
        @Override
        public String get(<xsl:value-of select="$entityClassName"/> dv) {
        return XMLUtils.convertToStringValue(<xsl:value-of select="$obj"/>.get<xsl:value-of select="$cname"/>());
        }
        });
    </xsl:template>

</xsl:stylesheet>

