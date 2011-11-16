<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:java="http://xml.apache.org/xslt/java"
    exclude-result-prefixes="java">
    <xsl:output method="html" encoding="ISO-8859-1"
        />
<!-- ***************************************************************************
    <xsl:output method="html" encoding="ISO-8859-1"
        doctype-system="def.dtd"
        doctype-public="-//W3C//DTD HTML 4.01 Transitional//FR"/>
    START
-->
<xsl:template match="/" >
    <html>

    <head>
        <meta http-equiv="content-type" content="text/html;charset=iso-8859-1" />
        <title>Referentiel.</title>
        <style type="text/css">
            body {background-color: white}
            h3 {background-color: #dddddd}
            th {background-color: #2222aa; color: white}
            td.toc {background-color: #eeeeee}
            ul.toc {color: black}
            a {text-decoration: none}
            a:hover {text-decoration: underline}
            div {text-decoration: underline}
            code {color: #000099 }
        </style>
    </head>

    <body>
        <table border="0" cellpadding="5">
            <tr>
                <th>TABLE</th>
                <th>STRUCTURE</th>
            </tr>
            <tr><td class="toc" valign="top" >
                <b> Entity </b>
                <ul>
                    <xsl:for-each select="//data/entity">
                        <xsl:sort select="description"/>
                        <xsl:call-template name="TOC_NAME"/>
                    </xsl:for-each>
                </ul>
                <b> BD </b>
                <ul>
                    <xsl:for-each select="//data/entity">
                        <xsl:sort select="@table"/>
                        <xsl:call-template name="TOC_BD"/>
                    </xsl:for-each>
                </ul>
            </td>
            <td class="documentation" valign="top" >
                <xsl:for-each select="//data/entity">
                    <xsl:sort select="@description"/>
                    <xsl:call-template name="ENTITY"/>
                </xsl:for-each>
            </td></tr>
        </table>
    </body>
    </html>

</xsl:template>

<!-- ***************************************************************************
    TOC
-->
<xsl:template name="TOC_NAME">
    <li>
         <small> <a href="#{@table}"><xsl:value-of select="description"/> </a> </small>
            <!-- <br/> <small> <xsl:value-of select="@table"/> </small> -->
    </li>
</xsl:template>
<xsl:template name="TOC_BD">
    <li>
         <small> <a href="#{@table}"><xsl:value-of select="@table"/> </a> </small>
            <!-- <br/> <small> <xsl:value-of select="@table"/> </small> -->
    </li>
</xsl:template>

<!-- ***************************************************************************
    Par Entity
-->
<xsl:template name="ENTITY" >
    <a name="{@table}" />
    <h3><xsl:value-of select="description"/> (<xsl:value-of select="@table"/>)</h3>

    <div>Clef primaire</div>
    <ul>
        <xsl:for-each select="primary-key/field">
            <xsl:sort select="@name"/>
            <xsl:call-template name="FIELD"/>
        </xsl:for-each>
    </ul>

    <div><xsl:value-of select="count(properties/field)"/> Attribut(s)</div>
    <ul>
        <xsl:for-each select="properties/field">
            <xsl:sort select="@description"/>
            <xsl:call-template name="FIELD"/>
        </xsl:for-each>
    </ul>
</xsl:template>
<!--
            <xsl:apply-templates select="properties/field[referential]"/>
-->
<xsl:template name="FIELD" >
    <li>
        <code><xsl:value-of select="description"/></code> : <xsl:value-of select="@name"/>
            ( <xsl:value-of select="java:sql.Util.toSqlName(@name)"/> )
            -
                <xsl:value-of select="sql/@type"/>
                <xsl:if test="sql/@precision">(<xsl:value-of select="sql/@precision"/>)</xsl:if>
                <xsl:if test="sql/@required='true'"> <i> obligatoire </i></xsl:if>
<!--
            -
            <xsl:choose>
                    <xsl:when test="referential/@name='net.codjo.pims.ref.Referential'">
                        Referentiel
                    </xsl:when>
                    <xsl:when test="referential/@name='net.codjo.pims.ref.Person'">
                        Person
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="referential/@name"/>
                    </xsl:otherwise>
                </xsl:choose>

                [ <xsl:value-of select="referential/@family"/> ]
-->
    </li>
</xsl:template>


</xsl:stylesheet>

