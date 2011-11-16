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
        <title>Syntaxe de datagen.</title>
        <style type="text/css">
            body {background-color: white}
            h3 {background-color: #dddddd}
            th {background-color: #2222aa; color: white}
            td.toc {background-color: #eeeeee}
            ul.toc {color: black}
            a {text-decoration: none}
            a:hover {background-color: #eeeeee}
            div {text-decoration: underline}
            code {color: #000099 }
            .source {
                background-color: #fff;
                color: #000000;
                border-right: 1px solid ;
                border-left: 1px solid ;
                border-top: 1px solid ;
                border-bottom: 1px solid ;
                margin-right: 7px;
                margin-left: 7px;
                margin-top: 1em;
            }
            .source pre {
                margin-right: 7px;
                margin-left: 7px;
            }
        </style>
    </head>

    <body>
        <table border="0" cellpadding="5">
            <tr>
                <th>TOC</th>
                <th>DOCUMENTATION</th>
            </tr>
            <tr><td class="toc" valign="top" >
                <a href="#data">&lt;data&gt;</a>
                <ul class="toc">
                    <xsl:apply-templates select="syntaxe/tag[@id='data']/sub-tag/tag" mode="toc"/>
                </ul>
            </td>
            <td class="documentation" valign="top" >
                <xsl:apply-templates select="syntaxe/tag"/>
            </td></tr>
        </table>
    </body>
    </html>

</xsl:template>

<!-- ***************************************************************************
    TOC
-->
<xsl:template match="tag" mode="toc" >
    <li><a href="#{@id}">&lt;<xsl:value-of select="@name"/>&gt;</a></li>
        <ul class="toc">
            <xsl:apply-templates select="sub-tag/tag" mode="toc"/>
        </ul>
</xsl:template>

<xsl:template match="tag" mode="toc.multiple" >
    <li><a href="#{@id}">&lt;<xsl:value-of select="@name"/>&gt;*</a></li>
        <ul class="toc">
            <xsl:apply-templates select="sub-tag/tag" mode="toc"/>
        </ul>
</xsl:template>

<xsl:template match="tag" mode="toc.optional" >
    <li><a href="#{@id}">[&lt;<xsl:value-of select="@name"/>&gt;]</a></li>
        <ul class="toc">
            <xsl:apply-templates select="sub-tag/tag" mode="toc"/>
        </ul>
</xsl:template>

<xsl:template match="sub-tag/tag" mode="toc">
    <xsl:variable name="refId" select="@refId"/>

    <xsl:choose>
        <xsl:when test="@type='multiple' and //syntaxe/tag[@id=$refId]">
            <xsl:apply-templates select="//syntaxe/tag[@id=$refId]" mode="toc.multiple"/>
        </xsl:when>
        <xsl:when test="@type='optional' and //syntaxe/tag[@id=$refId]">
            <xsl:apply-templates select="//syntaxe/tag[@id=$refId]" mode="toc.optional"/>
        </xsl:when>
        <xsl:when test="//syntaxe/tag[@id=$refId]">
            <xsl:apply-templates select="//syntaxe/tag[@id=$refId]" mode="toc"/>
        </xsl:when>
        <xsl:otherwise>
            <li>&lt;<xsl:value-of select="$refId"/>&gt;</li>
        </xsl:otherwise>
    </xsl:choose>

</xsl:template>

<!-- ***************************************************************************
    TAG
-->
<xsl:template match="tag" >
    <h3>&lt;<a name="{@id}"><xsl:value-of select="@name"/></a>&gt;</h3>

    <p><xsl:value-of select="description"/></p>

    <xsl:if test="count(attributes-tag/attribute)!=0">
        <div>Attribut</div>
    </xsl:if>
    <ul>
        <xsl:apply-templates select="attributes-tag/attribute"/>
    </ul>

    <xsl:if test="count(sub-tag/tag)!=0">
        <div>Sous-balise</div>
        <xsl:apply-templates select="sub-tag/tag"/>
    </xsl:if>

    <xsl:if test="count(samples/sample)!=0">
        <div>Exemples:</div>
        <xsl:apply-templates select="samples/sample"/>
    </xsl:if>
</xsl:template>

<xsl:template match="sub-tag/tag" >
    <xsl:variable name="refId" select="@refId"/>

    <xsl:choose>
        <xsl:when test="//syntaxe/tag[@id=$refId]">
            <a href="#{$refId}">&lt;<xsl:value-of select="//syntaxe/tag[@id=$refId]/@name"/>&gt;</a>
        </xsl:when>
        <xsl:otherwise>
            &lt;<xsl:value-of select="$refId"/>&gt;
        </xsl:otherwise>
    </xsl:choose>

    <xsl:text> </xsl:text>

</xsl:template>

<xsl:template match="samples/sample" >
    <p>
        <xsl:value-of select="description"/>
    </p>
    <pre class="source">
        <xsl:value-of select="code"/>
    </pre>
</xsl:template>


<!-- ***************************************************************************
    ATTRIBUTE
-->
<xsl:template match="attribute" >
    <li>
        <code><xsl:value-of select="@name"/></code>
            <xsl:if test="@type='optionnal'"> <small>[optionnel]</small> </xsl:if>
            : <xsl:value-of select="description"/>
        <xsl:if test="enumeration">
            <ul>
                <xsl:apply-templates select="enumeration/value"/>
            </ul>
        </xsl:if>
    </li>
</xsl:template>

<xsl:template match="enumeration/value" >
    <xsl:variable name="refId" select="@refId"/>
    <li>
        <i><xsl:value-of select="@val"/></i> : <xsl:value-of select="@description"/>
    </li>
</xsl:template>

</xsl:stylesheet>

