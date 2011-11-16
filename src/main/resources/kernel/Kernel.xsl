<?xml version="1.0"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:java="http://xml.apache.org/xslt/java"
    exclude-result-prefixes="java">

<!-- ***************************************************************************
    Declaration des pk dans le resultat
-->
<xsl:template match="primary-key/field" mode="declare">
     + "&lt;field name=\"<xsl:value-of select="@name"/>\"/&gt;"
</xsl:template>
<!-- ***************************************************************************
        Declaration des pk dans un tableau : "pk1", "pk2"
-->
<xsl:template match="primary-key/field" mode="declare-as-list">
<xsl:if test="position()>1"> , </xsl:if>
"<xsl:value-of select="@name"/>"
</xsl:template>

<!-- ***************************************************************************
    Declaration des pk dans le resultat et de leur valeur
-->
<xsl:template match="primary-key/field" mode="value">
    <xsl:variable name="cname" select="java:bean.Util.capitalize(@name)"/>
     + "&lt;field name=\"<xsl:value-of select="@name"/>\">" + XMLUtils.convertToStringValue(obj.get<xsl:value-of select="$cname"/>()) + "&lt;/field>"
</xsl:template>

<!-- ***************************************************************************
    Transcodage du type d'une property
-->
<xsl:template match="@type">
    <xsl:variable name="type" select="."/>
    <xsl:choose>
        <xsl:when test="$type='boolean'">boolean</xsl:when>
        <xsl:when test="$type='integer'">int</xsl:when>
        <xsl:when test="$type='double'">double</xsl:when>
        <xsl:when test="$type='string'">String</xsl:when>
        <xsl:when test="$type='big-decimal'">java.math.BigDecimal</xsl:when>
        <xsl:when test="$type='sql-date'">java.sql.Date</xsl:when>
        <xsl:otherwise><xsl:value-of select="$type"/></xsl:otherwise>
    </xsl:choose>
</xsl:template>

<!-- ***************************************************************************
    Transcodage pour le bind d'une requete OQL
-->
<xsl:template name="bind">
    <xsl:param name="name" />
    <xsl:param name="type" />
    <xsl:choose>
        <xsl:when test="$type='boolean'">query.bind((XMLUtils.convertFromStringValue(Boolean.class, (String) pks.get("<xsl:value-of select="$name"/>"))).booleanValue());</xsl:when>
        <xsl:when test="$type='double'">query.bind(XMLUtils.convertFromStringValue(double.class, (String) pks.get("<xsl:value-of select="$name"/>")));</xsl:when>
        <xsl:when test="$type='integer'">query.bind(XMLUtils.convertFromStringValue(Integer.class, (String) pks.get("<xsl:value-of select="$name"/>")));</xsl:when>
        <xsl:when test="$type='string'">query.bind(XMLUtils.convertFromStringValue(String.class, (String) pks.get("<xsl:value-of select="$name"/>")));</xsl:when>
        <xsl:when test="$type='big-decimal'">query.bind(XMLUtils.convertFromStringValue(java.math.BigDecimal.class, (String) pks.get("<xsl:value-of select="$name"/>")));</xsl:when>
        <xsl:otherwise>
            query.bind(XMLUtils.convertFromStringValue(<xsl:value-of select="$type"/>.class, (String) pks.get("<xsl:value-of select="$name"/>")));
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<!-- ***************************************************************************
    Transcodage pour le setXX d'une requete SQL
-->
<xsl:template name="sqlSetParam">
    <xsl:param name="name" />
    <xsl:param name="type" />
    <xsl:param name="idx"><xsl:value-of select="position()"/></xsl:param>
    <xsl:choose>
        <xsl:when test="$type='boolean'">
            query.setBoolean(<xsl:value-of select="$idx"/>, (XMLUtils.convertFromStringValue(Boolean.class, (String) pks.get("<xsl:value-of select="$name"/>"))).booleanValue());
        </xsl:when>
        <xsl:when test="$type='string'">
            query.setString(<xsl:value-of select="$idx"/>, XMLUtils.convertFromStringValue(String.class, (String) pks.get("<xsl:value-of select="$name"/>")));
        </xsl:when>
        <xsl:when test="$type='big-decimal' or $type='java.math.BigDecimal'">
            query.setBigDecimal(<xsl:value-of select="$idx"/>, XMLUtils.convertFromStringValue(java.math.BigDecimal.class, (String) pks.get("<xsl:value-of select="$name"/>")));
        </xsl:when>
        <xsl:when test="$type='java.sql.Timestamp'">
            query.setTimestamp(<xsl:value-of select="$idx"/>, XMLUtils.convertFromStringValue(java.sql.Timestamp.class, (String) pks.get("<xsl:value-of select="$name"/>")));
        </xsl:when>
        <xsl:when test="$type='java.sql.Date'">
            query.setDate(<xsl:value-of select="$idx"/>, XMLUtils.convertFromStringValue(java.sql.Date.class, (String) pks.get("<xsl:value-of select="$name"/>")));
        </xsl:when>
        <xsl:when test="$type='double'">
            query.setDouble(<xsl:value-of select="$idx"/>, (XMLUtils.convertFromStringValue(Double.class, (String) pks.get("<xsl:value-of select="$name"/>"))).doubleValue());
        </xsl:when>
        <xsl:when test="$type='integer' or $type='int' or $type='java.lang.Integer' ">
            query.setObject(<xsl:value-of select="$idx"/>, XMLUtils.convertFromStringValue(Integer.class, (String) pks.get("<xsl:value-of select="$name"/>")), java.sql.Types.INTEGER);
        </xsl:when>
        <xsl:otherwise>
            A faire :) Type inconnu <xsl:value-of select="$type"/>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>


<!-- ***************************************************************************
    Declaration des fields dans une requete (e.g. select TOTO, TITI from...)
-->
<xsl:template match="properties/field[@type]" mode="sql-select-field">
    <xsl:param name="shift" select="0"/>
    <xsl:if test="position()+$shift>1"> , </xsl:if>
    <xsl:value-of select="java:sql.Util.toSqlNameWithQuote(@name)"/>
    </xsl:template>
<xsl:template match="properties/field[@structure]" mode="sql-select-field">
    <xsl:param name="name"><xsl:value-of select="@name"/></xsl:param>
    <xsl:param name="structure"><xsl:value-of select="@structure"/></xsl:param>
    <xsl:apply-templates select="//data/structure[@name=$structure]/properties/field" mode="sql-select-field">
        <xsl:with-param name="shift" select="position()-1"/>
    </xsl:apply-templates>
</xsl:template>

<!-- ***************************************************************************
    Declaration des pk dans une requete (e.g. select ... from ... where TOTO = ? and TITI = ? ...)
-->
<xsl:template match="primary-key/field" mode="oql-where-pk"> <xsl:if test="position()>1"> and </xsl:if><xsl:value-of select="@name"/> = $<xsl:value-of select="position()"/> </xsl:template>
<xsl:template match="primary-key/field" mode="sql-where-pk"> <xsl:if test="position()>1"> and </xsl:if><xsl:value-of select="java:sql.Util.toSqlName(@name)"/> = ? </xsl:template>

<!-- ***************************************************************************
    Statement vers Object (e.g. obj.setName(query.getString(1)); )
-->
<xsl:template match="properties/field[@type]" mode="sql-statement-to-object">
    <xsl:param name="shift" select="0"/>
    <xsl:param name="obj">obj</xsl:param>
    <xsl:call-template name="sql-statement-to-object">
        <xsl:with-param name="obj" select="$obj"/>
        <xsl:with-param name="name" select="@name"/>
        <xsl:with-param name="type" select="@type"/>
        <xsl:with-param name="idx" select="position()+$shift"/>
    </xsl:call-template>
</xsl:template>
<xsl:template match="properties/field[@structure]" mode="sql-statement-to-object">
    <xsl:param name="obj">obj</xsl:param>
    <xsl:param name="name"><xsl:value-of select="@name"/></xsl:param>
    <xsl:param name="structure"><xsl:value-of select="@structure"/></xsl:param>
    <xsl:variable name="cname" select="java:bean.Util.capitalize(@name)"/>
    <xsl:apply-templates select="//data/structure[@name=$structure]/properties/field" mode="sql-statement-to-object">
        <xsl:with-param name="obj"><xsl:value-of select="$obj"/>.get<xsl:value-of select="$cname"/>()</xsl:with-param>
        <xsl:with-param name="shift" select="position()-1"/>
    </xsl:apply-templates>
</xsl:template>

<xsl:template name="sql-statement-to-object">
    <xsl:param name="obj" />
    <xsl:param name="name" />
    <xsl:param name="type" />
    <xsl:param name="idx"><xsl:value-of select="position()"/></xsl:param>
    <xsl:variable name="cname" select="java:bean.Util.capitalize(@name)"/>
    <xsl:choose>
        <xsl:when test="$type='boolean'">
            <xsl:value-of select="$obj"/>.set<xsl:value-of select="$cname"/>(rs.getBoolean(<xsl:value-of select="$idx"/>));
        </xsl:when>
        <xsl:when test="$type='string'">
            <xsl:value-of select="$obj"/>.set<xsl:value-of select="$cname"/>(rs.getString(<xsl:value-of select="$idx"/>));
        </xsl:when>
        <xsl:when test="$type='big-decimal'">
            <xsl:value-of select="$obj"/>.set<xsl:value-of select="$cname"/>(rs.getBigDecimal(<xsl:value-of select="$idx"/>));
        </xsl:when>
        <xsl:when test="$type='java.sql.Timestamp'">
            <xsl:value-of select="$obj"/>.set<xsl:value-of select="$cname"/>(rs.getTimestamp(<xsl:value-of select="$idx"/>));
        </xsl:when>
        <xsl:when test="$type='java.sql.Date'">
            <xsl:value-of select="$obj"/>.set<xsl:value-of select="$cname"/>(rs.getDate(<xsl:value-of select="$idx"/>));
        </xsl:when>
        <xsl:when test="$type='double'">
            <xsl:value-of select="$obj"/>.set<xsl:value-of select="$cname"/>(rs.getDouble(<xsl:value-of select="$idx"/>));
        </xsl:when>
        <xsl:when test="$type='integer' or $type='int' or $type='java.lang.Integer' ">
            <xsl:value-of select="$obj"/>.set<xsl:value-of select="$cname"/>(rs.getInt(<xsl:value-of select="$idx"/>));
        </xsl:when>
        <xsl:when test="$type='java.math.BigDecimal'">
            <xsl:value-of select="$obj"/>.set<xsl:value-of select="$cname"/>(rs.getBigDecimal(<xsl:value-of select="$idx"/>));
        </xsl:when>
        <xsl:otherwise>
            A faire :) Type inconnu <xsl:value-of select="$type"/>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<!-- ***************************************************************************
    Obect vers Statement (e.g. query.setString(1, obj.getName()); )
-->
<xsl:template match="properties/field[@type]" mode="object-to-sql-statement">
    <xsl:param name="shift" select="0"/>
    <xsl:param name="obj">obj</xsl:param>
    <xsl:call-template name="object-to-sql-statement">
        <xsl:with-param name="obj" select="$obj"/>
        <xsl:with-param name="name" select="@name"/>
        <xsl:with-param name="type" select="@type"/>
        <xsl:with-param name="idx" select="java:bean.Util.index(.)+1+$shift"/>
    </xsl:call-template>
</xsl:template>
<xsl:template match="properties/field[@structure]" mode="object-to-sql-statement">
    <xsl:param name="obj">obj</xsl:param>
    <xsl:param name="name"><xsl:value-of select="@name"/></xsl:param>
    <xsl:param name="structure"><xsl:value-of select="@structure"/></xsl:param>
    <xsl:variable name="cname" select="java:bean.Util.capitalize(@name)"/>
    <xsl:apply-templates select="//data/structure[@name=$structure]/properties/field" mode="object-to-sql-statement">
        <xsl:with-param name="obj"><xsl:value-of select="$obj"/>.get<xsl:value-of select="$cname"/>()</xsl:with-param>
        <xsl:with-param name="shift" select="java:bean.Util.index(.)"/>
    </xsl:apply-templates>
</xsl:template>
<xsl:template name="object-to-sql-statement">
    <xsl:param name="obj" />
    <xsl:param name="name" />
    <xsl:param name="type" />
    <xsl:param name="idx"><xsl:value-of select="position()"/></xsl:param>
    <xsl:variable name="cname" select="java:bean.Util.capitalize(@name)"/>
    <xsl:choose>
        <xsl:when test="$type='boolean'">query.setBoolean</xsl:when>
        <xsl:when test="$type='string'">query.setString</xsl:when>
        <xsl:when test="$type='big-decimal' or $type='java.math.BigDecimal'">query.setBigDecimal</xsl:when>
        <xsl:when test="$type='java.sql.Timestamp'">query.setTimestamp</xsl:when>
        <xsl:when test="$type='java.sql.Date'">query.setDate</xsl:when>
        <xsl:when test="$type='double'">query.setDouble</xsl:when>
        <xsl:when test="$type='integer' or $type='int' or $type='java.lang.Integer' ">query.setObject</xsl:when>
        <xsl:otherwise>
            A faire :) Type inconnu <xsl:value-of select="$type"/>
        </xsl:otherwise>
    </xsl:choose>
    (<xsl:value-of select="$idx"/>, <xsl:value-of select="$obj"/>.get<xsl:value-of select="$cname"/>());
</xsl:template>


</xsl:stylesheet>

