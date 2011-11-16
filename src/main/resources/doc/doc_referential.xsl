<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet
           xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
           version="1.0"
           >
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
                <th>REF -> DATA</th>
                <th>DATA -> REF</th>
            </tr>
            <tr><td class="toc" valign="top" >
                <ul class="toc">
                    <li> Referentiel </li>
                    <xsl:call-template name="TOC">
                        <xsl:with-param name="ref">net.codjo.pims.ref.Referential</xsl:with-param>
                    </xsl:call-template>
                    <li> Person </li>
                    <xsl:call-template name="TOC">
                        <xsl:with-param name="ref">net.codjo.pims.ref.Referential</xsl:with-param>
                    </xsl:call-template>
                </ul>
            </td>
            <td class="documentation" valign="top" >
                <xsl:for-each select="//data/entity[properties/field/referential]">
                    <xsl:sort select="@name"/>
                    <xsl:call-template name="ENTITY"/>
                </xsl:for-each>
            </td></tr>
        </table>
    </body>
    </html>

</xsl:template>

<!-- ***************************************************************************
    Par Referentiel
-->
<xsl:template name="TOC">
    <xsl:param name="ref" />
    <br/>
    <ul>
        <xsl:for-each select="//data/entity/properties/field/referential[@name=$ref]">
            <xsl:sort select="@family"/>
            <li>
                <xsl:value-of select="@family"/> :
                    <small> <a href="#{../../../@name}.{../@name}"><xsl:value-of select="../@name"/></a></small>
            </li>
        </xsl:for-each>
    </ul>
<!--
    <br><xsl:value-of select="@family"/></br>
                    <xsl:apply-templates select="data/entity/properties/field/referential[@name='net.codjo.pims.ref.Referential']" mode="toc"/>
-->
</xsl:template>

<!-- ***************************************************************************
    Par Entity
-->
<xsl:template name="ENTITY" >
    <h3><xsl:value-of select="@name"/> (<xsl:value-of select="@table"/>)</h3>

    <p><xsl:value-of select="description"/></p>

    <div><xsl:value-of select="count(properties/field[referential])"/> Attribut(s) referentiel</div>
    <ul>
        <xsl:for-each select="properties/field[referential]">
            <xsl:sort select="@name"/>
            <xsl:call-template name="FIELD"/>
        </xsl:for-each>
    </ul>
</xsl:template>
<!--
            <xsl:apply-templates select="properties/field[referential]"/>
-->
<xsl:template name="FIELD" >
    <li>
        <code><a name="{../../@name}.{@name}"><xsl:value-of select="@name"/></a></code> : <xsl:value-of select="description"/>
            <br>
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
            </br>
    </li>
</xsl:template>


</xsl:stylesheet>

