<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      version="1.0"
      xmlns:java="http://xml.apache.org/xslt/java"
      exclude-result-prefixes="java">
    <xsl:output method="xml" encoding="iso-8859-1" indent="yes"/>

    <!-- ***************************************************************************
        Generation handler generaux.
    -->
    <xsl:template match="feature/generate-all" priority="1">
        <xsl:param name="className" select="java:bean.Util.extractClassName(../../@name)"/>
        <sql name="{../../@table}">
            <xsl:if test="@sql-gap">
                <xsl:attribute name="gap">
                    <xsl:value-of select="@sql-gap"/>
                </xsl:attribute>
            </xsl:if>
        </sql>
        <bean/>
        <castor/>
        <handler-new id="new{$className}"/>
        <handler-delete id="delete{$className}"/>
        <handler-update id="update{$className}"/>
        <handler-select id="select{$className}ById" type="By_Primary-Key"/>
        <handler-select id="selectAll{$className}" type="All"/>
    </xsl:template>

    <!-- ***************************************************************************
        Generation handler generaux sans delete
    -->
    <xsl:template match="feature/generate-all-withoutDelete" priority="1">
        <xsl:param name="className" select="java:bean.Util.extractClassName(../../@name)"/>
        <sql name="{../../@table}">
            <xsl:if test="@sql-gap">
                <xsl:attribute name="gap">
                    <xsl:value-of select="@sql-gap"/>
                </xsl:attribute>
            </xsl:if>
        </sql>
        <bean/>
        <castor/>
        <handler-new id="new{$className}"/>
        <handler-update id="update{$className}"/>
        <handler-select id="select{$className}ById" type="By_Primary-Key"/>
        <handler-select id="selectAll{$className}" type="All"/>
    </xsl:template>

    <!-- ***************************************************************************
        Generation multi select
    -->
    <xsl:template match="feature/multi-select " priority="1">
        <xsl:param name="selectionTable" select="@selection-table"/>
        <handler-sql id="{@handler}_TempCreate" transaction="false" return-pk="false">
            <query>
                create table
                <xsl:value-of select="$selectionTable"/>
                (
                <xsl:variable name="space" select="' '"/>
                <xsl:for-each select="parameters/arg">
                    <xsl:value-of select="@sql-name"/>
                    <xsl:value-of select="$space"/>
                    <xsl:value-of select="@sql-type"/>
                    (
                    <xsl:value-of select="@precision"/>
                    ) not null,
                </xsl:for-each>
                )
            </query>
        </handler-sql>
        <handler-sql id="{@handler}_TempDrop" transaction="false" return-pk="false">
            <query>
                drop table
                <xsl:value-of select="$selectionTable"/>
            </query>
        </handler-sql>
        <handler-sql id="{@handler}_TempFill" transaction="false" return-pk="false"
                     force-transaction-mode="true">
            <query>
                insert into
                <xsl:value-of select="$selectionTable"/>
                values (
                <xsl:for-each select="parameters/arg">
                    <xsl:choose>
                        <xsl:when test="position()>1">,</xsl:when>
                    </xsl:choose>
                    ?
                </xsl:for-each>
                )
            </query>
            <xsl:for-each select="parameters/arg">
                <arg type="{@type}">
                    <xsl:value-of select="."/>
                </arg>
            </xsl:for-each>
        </handler-sql>
    </xsl:template>

    <!-- ***************************************************************************
        Generation pour les tables de caractéristique
    -->
    <xsl:template match="feature/features-for-characteristic" priority="1">
        <xsl:param name="className" select="java:bean.Util.extractClassName(../../@name)"/>
        <xsl:param name="tableName" select="../../@table"/>
        <sql name="{../../@table}">
            <xsl:if test="@sql-gap">
                <xsl:attribute name="gap">
                    <xsl:value-of select="@sql-gap"/>
                </xsl:attribute>
            </xsl:if>
        </sql>
        <castor/>
        <audit-trail/>
        <doc-structure/>
        <bean extends="net.codjo.pims.data.AbstractCharacteristic"/>

        <xsl:choose>

            <xsl:when test="@controlable = 'false'">
            </xsl:when>
            <xsl:otherwise>
                <controlable jndi="java:comp/env/ejb/ControlManagerEJB"/>
            </xsl:otherwise>
        </xsl:choose>

        <handler-archive id="archive{$className}Handler"/>
        <handler-new id="new{$className}"/>
        <handler-update id="update{$className}"/>
        <handler-select id="select{$className}ById" type="By_Primary-Key"/>

        <handler-select id="selectValid{$className}">
            <query type="SQL">
                select caract.*
                from
                <xsl:value-of select="$tableName"/>
                caract
                where caract.DATE_BEGIN &lt;= ?
                and caract.DATE_END &gt; ?
                and caract.PORTFOLIO_CODE = ?
            </query>
            <arg type="java.sql.Date">date</arg>
            <arg type="java.sql.Date">date</arg>
            <arg type="string">portfolioCode</arg>
        </handler-select>

        <handler-select id="selectAll{$className}ByPtfCode">
            <query type="SQL">
                select caract.*
                from
                <xsl:value-of select="$tableName"/>
                caract
                where caract.PORTFOLIO_CODE = ?
                order by caract.DATE_END desc
            </query>
            <arg type="string">portfolioCode</arg>
        </handler-select>
    </xsl:template>

    <!-- ***************************************************************************
        Generation des Quarantaine utilisateur.
    -->
    <xsl:template match="feature/user-quarantine" priority="1">
    </xsl:template>

    <xsl:template match="entity" mode="userQ">
        <xsl:param name="className" select="java:bean.Util.extractClassName(feature/user-quarantine/@name)"/>
        <xsl:param name="requetorName" select="java:bean.Util.lowerize(java:bean.Util.extractClassName(feature/user-quarantine/@name))"/>
        <xsl:param name="orderClause" select="@order-clause"/>
        
        <entity name="{feature/user-quarantine/@name}" table="{feature/user-quarantine/@table}">
            <xsl:if test="@order-clause">
                <xsl:attribute name="order-clause">
                    <xsl:value-of select="@order-clause"/>
                </xsl:attribute>
            </xsl:if>
            <feature>
                <doc-structure/>
                <sql name="{feature/user-quarantine/@table}" pk-generator="no"/>
                <sql-index>
                    <idx type="primary-key" name-prefix="X1_"/>
                </sql-index>
                <bean/>
                <castor/>
                <xsl:if test="not(feature/user-quarantine/@delete='false')">
                    <handler-delete id="delete{$className}"/>
                </xsl:if>
                <xsl:if test="not(feature/user-quarantine/@update='false')">
                    <handler-update id="update{$className}"/>
                </xsl:if>
                <xsl:if test="(feature/user-quarantine/@requetor='true')">
                    <handler-requetor id="{$requetorName}RequetorHandler"/>
                </xsl:if>
                <handler-select id="select{$className}ById" type="By_Primary-Key"/>
                <handler-select id="selectAll{$className}" type="All"/>
                <xsl:copy-of select="feature/historic-audit-trail"/>
                <xsl:call-template name="selectAllWithParameters">
                    <xsl:with-param name="className">
                        <xsl:value-of select="$className"/>
                    </xsl:with-param>
                    <xsl:with-param name="orderClause">
                        <xsl:value-of select="$orderClause"/>
                    </xsl:with-param>
                </xsl:call-template>
            </feature>
            <xsl:copy-of select="primary-key"/>
            <xsl:copy-of select="methods"/>
            <xsl:copy-of select="properties"/>
        </entity>
    </xsl:template>

    <xsl:template name="selectAllWithParameters">
        <xsl:param name="className"/>
        <xsl:param name="orderClause"/>
        <handler-sql id="selectAll{$className}WithParameters">
            <attributes>
                <xsl:for-each select="properties/field">
                    <xsl:choose>
                        <xsl:when test="@type">
                            <name><xsl:value-of select="@name"/></name>
                        </xsl:when>
                        <xsl:when test="@structure">
                            <xsl:variable name="structureName" select="@structure"/>
                            <xsl:for-each select="/data/structure[@name=$structureName]/properties/field">
                                <name><xsl:value-of select="@name"/></name>
                            </xsl:for-each>
                        </xsl:when>
                    </xsl:choose>
                </xsl:for-each>
            </attributes>
            <query>
            <xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
             select
             <xsl:for-each select="properties/field">
                 <xsl:choose>
                    <xsl:when test="@type">
                        <xsl:if test="position() &gt; 1">,</xsl:if>
                        <xsl:value-of select="java:sql.Util.toSqlName(@name)"/>
                    </xsl:when>
                 <xsl:when test="@structure">
                 <xsl:variable name="structureName" select="@structure"/>
                        <xsl:for-each select="/data/structure[@name=$structureName]/properties/field">
                            ,<xsl:value-of select="java:sql.Util.toSqlName(@name)"/>
                        </xsl:for-each>
                    </xsl:when>
                </xsl:choose>
             </xsl:for-each>
             from <xsl:value-of select="feature/user-quarantine/@table"/>
             where
             <xsl:for-each select="properties/field[not(@name='quarantineId')]">
                 <xsl:if test="not(sql/@type='text')">
                     <xsl:choose>
                     <xsl:when test="@type">
                        <xsl:if test="position() &gt; 1">
                            and
                        </xsl:if>
                        <xsl:call-template name="resolveWhereClauseForSelectAllWithParameters">
                            <xsl:with-param name="field" select="."/>
                        </xsl:call-template>
                        </xsl:when>
                     <xsl:when test="@structure">
                     <xsl:variable name="structureName" select="@structure"/>
                     <xsl:for-each select="/data/structure[@name=$structureName]/properties/field">
                            and
                            <xsl:call-template name="resolveWhereClauseForSelectAllWithParameters">
                                <xsl:with-param name="field" select="."/>
                            </xsl:call-template>
                        </xsl:for-each>
                    </xsl:when>
                    </xsl:choose>
                 </xsl:if>
             </xsl:for-each>

             <xsl:if test="@order-clause">
                 order by
                 <xsl:value-of select="java:sql.Util.toSqlName(@order-clause)"/>
             </xsl:if>
             <xsl:text disable-output-escaping="yes">
                 ]]&gt;</xsl:text>

            </query>
            <xsl:for-each select="properties/field[not(@name='quarantineId')]">
                <xsl:if test="not(sql/@type='text')">
                        <xsl:choose>
                               <xsl:when test="@type">
                                       <arg converter="net.codjo.control.server.handler.FilterValueConverter" type="{@type}"><xsl:value-of select="@name"/></arg>
                                       <arg converter="net.codjo.control.server.handler.FilterValueConverter" type="{@type}"><xsl:value-of select="@name"/></arg>
                                </xsl:when>
                                <xsl:when test="@structure">
                                    <xsl:variable name="structureName" select="@structure"/>
                                    <xsl:for-each select="/data/structure[@name=$structureName]/properties/field">
                                        <arg type="{@type}" converter="net.codjo.control.server.handler.FilterValueConverter"><xsl:value-of select="@name"/></arg>
                                        <arg type="{@type}" converter="net.codjo.control.server.handler.FilterValueConverter"><xsl:value-of select="@name"/></arg>
                                    </xsl:for-each>
                                </xsl:when>
                        </xsl:choose>
                </xsl:if>
            </xsl:for-each>
        </handler-sql>
    </xsl:template>

    <xsl:template name="resolveWhereClauseForSelectAllWithParameters">
        <xsl:param name="field"/>
            <xsl:variable name="columnName" select="java:sql.Util.toSqlName($field/@name)"/>
            (<xsl:value-of select="$columnName"/> = ? or ? =
            <xsl:choose>
                <xsl:when test="($field/sql/@type='integer') or $field/sql/@type='numeric'">-1</xsl:when>
                <xsl:when test="($field/sql/@type='datetime') or ($field/sql/@type='timestamp')">'9999-12-31'</xsl:when>
                <xsl:otherwise>'Tout'</xsl:otherwise>
            </xsl:choose>)
    </xsl:template>

    <!-- ***************************************************************************
        Generation copy-entity.
    -->
    <xsl:template match="feature/copy-entity" priority="1">
    </xsl:template>

    <xsl:template match="copy-entity" mode="copyEntity">
        <xsl:param name="className" select="java:bean.Util.extractClassName(@name)"/>

        <entity name="{@name}" table="{@table}">
            <xsl:if test="../../@order-clause">
                <xsl:attribute name="order-clause">
                    <xsl:value-of select="../../@order-clause"/>
                </xsl:attribute>
            </xsl:if>
            <feature>
                <sql name="{@table}" pk-generator="no"/>
                <bean/>
                <castor/>
                <xsl:if test="not(@delete='false')">
                    <handler-delete id="delete{$className}"/>
                </xsl:if>
                <xsl:if test="not(@update='false')">
                    <handler-update id="update{$className}"/>
                </xsl:if>
                <handler-select id="select{$className}ById" type="By_Primary-Key"/>
                <handler-select id="selectAll{$className}" type="All"/>
            </feature>
            <xsl:copy-of select="../../primary-key"/>
            <xsl:copy-of select="../../methods"/>
            <xsl:copy-of select="../../properties"/>
        </entity>
    </xsl:template>

    <!-- ***************************************************************************
        Complete la balise SQL.
    -->
    <xsl:template match="feature/sql[not(@name)]" priority="1">
        <sql name="{../../@table}">
            <xsl:for-each select="@*">
                <xsl:attribute name="{local-name()}">
                    <xsl:value-of select="."/>
                </xsl:attribute>
            </xsl:for-each>
        </sql>
    </xsl:template>

    <!-- ***************************************************************************
        Copy de la structure XML
    -->
    <xsl:template match="/">
        <data>
            <xsl:apply-templates select="//data/configuration"/>
            <xsl:apply-templates select="//data/roles"/>
            <xsl:apply-templates select="//data/structure"/>
            <xsl:apply-templates select="//data/entity[not(@extends)]"/>
            <xsl:apply-templates select="//data/entity[feature/user-quarantine]" mode="userQ"/>
            <xsl:apply-templates select="//data/entity/feature/copy-entity" mode="copyEntity"/>
        </data>
    </xsl:template>

    <xsl:template match="feature/*">
        <xsl:choose>
            <xsl:when test="substring(name(), 1, 7)='handler'">
                <xsl:copy-of select="java:kernel.Util.parseVariables(./../.., .)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="feature/trigger-delete" priority="1">
        <trigger-delete>
            <xsl:for-each select="@*">
                <xsl:attribute name="{local-name()}">
                    <xsl:value-of select="."/>
                </xsl:attribute>
            </xsl:for-each>
            <xsl:for-each select="./*">
                <xsl:copy-of select="."/>
            </xsl:for-each>
            <xsl:variable name="parentName" select="../../@name"/>
            <xsl:for-each select="//data/append-to-trigger-delete[@name=$parentName]/*">
                <xsl:copy-of select="."/>
            </xsl:for-each>
        </trigger-delete>
    </xsl:template>
    <xsl:template match="feature/trigger-insert" priority="1">
        <trigger-insert>
            <xsl:for-each select="@*">
                <xsl:attribute name="{local-name()}">
                    <xsl:value-of select="."/>
                </xsl:attribute>
            </xsl:for-each>
            <xsl:for-each select="./*">
                <xsl:copy-of select="."/>
            </xsl:for-each>
            <xsl:variable name="parentName" select="../../@name"/>
            <xsl:for-each select="//data/append-to-trigger-insert[@name=$parentName]/*">
                <xsl:copy-of select="."/>
            </xsl:for-each>
        </trigger-insert>
    </xsl:template>
    <xsl:template match="feature/trigger-update" priority="1">
        <trigger-update>
            <xsl:for-each select="@*">
                <xsl:attribute name="{local-name()}">
                    <xsl:value-of select="."/>
                </xsl:attribute>
            </xsl:for-each>
            <xsl:for-each select="./*">
                <xsl:copy-of select="."/>
            </xsl:for-each>
            <xsl:variable name="parentName" select="../../@name"/>
            <xsl:for-each select="//data/append-to-trigger-update[@name=$parentName]/*">
                <xsl:copy-of select="."/>
            </xsl:for-each>
        </trigger-update>
    </xsl:template>

    <xsl:template match="feature/referential" priority="1">
        <xsl:param name="parentEntity"/>
        <xsl:param name="child"/>
        <xsl:variable name="className" select="java:bean.Util.extractClassName(../../@name)"/>
        <sql name="{../../@table}"/>



        <referential>
            <xsl:if test="@generateHandler">
                <xsl:attribute name="generateHandler">
                    <xsl:value-of select="@generateHandler"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:for-each select="fill">
                <xsl:copy-of select="."/>
            </xsl:for-each>
            <xsl:if test="$parentEntity">
                <xsl:for-each select="$parentEntity/feature/referential/fill">
                    <xsl:variable name="parentFillField" select="@field"/>
                    <xsl:if test="not($child/feature/referential/fill[@field=$parentFillField])">
                        <xsl:copy-of select="."/>
                    </xsl:if>
                </xsl:for-each>
            </xsl:if>
        </referential>
        <bean/>
        <castor/>
        <doc-structure/>
        <sql-index>
            <idx clustered="true" name-prefix="X1_" type="primary-key"/>
        </sql-index>
        <xsl:if test="not(@generateHandler) or @generateHandler='true'">
            <handler-delete id="delete{$className}"/>
            <handler-update id="update{$className}"/>
            <handler-select id="select{$className}ById" type="By_Primary-Key"/>
            <handler-select id="selectAll{$className}" type="All"/>
            <handler-new id="new{$className}"/>
            <handler-requetor id="all{$className}"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="entity[not(@extends)]">
        <xsl:variable name="name" select="@name"/>
        <entity>
            <xsl:attribute name="name">
                <xsl:value-of select="@name"/>
            </xsl:attribute>
            <xsl:attribute name="table">
                <xsl:value-of select="@table"/>
            </xsl:attribute>

            <xsl:if test="@type">
                <xsl:attribute name="type">
                    <xsl:value-of select="@type"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@order-clause">
                <xsl:attribute name="order-clause">
                    <xsl:value-of select="@order-clause"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:text></xsl:text>
            <xsl:copy-of select="description"/>
            <xsl:text></xsl:text>
            <feature>
                <!--<xsl:copy-of select="feature/referential"/>-->
                <xsl:apply-templates select="feature"/>
            </feature>

            <xsl:copy-of select="primary-key"/>
            <xsl:copy-of select="functional-key"/>
            <xsl:copy-of select="foreign-keys"/>
            <xsl:copy-of select="methods"/>
            <xsl:copy-of select="properties"/>
        </entity>
        <xsl:apply-templates select="//data/entity[@extends=$name]">
            <xsl:with-param name="parentEntity" select="."/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="entity[@extends]">
        <xsl:param name="parentEntity"/>

        <entity name="{@name}" table="{@table}">
            <xsl:if test="$parentEntity/@order-clause">
                <xsl:attribute name="order-clause">
                    <xsl:value-of select="$parentEntity/@order-clause"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:copy-of select="description"/>
            <xsl:variable name="child" select="."/>
            <feature>
                <xsl:choose>
                    <xsl:when test="feature/referential">
                        <!--<xsl:copy-of select="feature/referential"/>-->
                        <xsl:apply-templates select="feature/referential">
                            <xsl:with-param name="parentEntity" select="$parentEntity"/>
                            <xsl:with-param name="child" select="$child"/>
                        </xsl:apply-templates>
                    </xsl:when>
                    <xsl:when test="$parentEntity/feature/referential">
                        <!--<xsl:copy-of select="$parentEntity/feature/referential"/>-->
                        <xsl:apply-templates select="$parentEntity/feature/referential"/>
                    </xsl:when>
                </xsl:choose>

                <!-- Parcours des features et surcharge des handler -->
                <!-- Parcours des features du fils -->
                <xsl:for-each select="feature/*">
                    <xsl:choose>
                        <!-- Quand on a un handler dans le fils, on le copie toujours -->
                        <xsl:when test="substring(name(), 1, 7)='handler'">
                            <xsl:copy-of select="java:kernel.Util.parseVariables($child, .)"/>
                        </xsl:when>
                        <!-- On ne recopie pas le referential qui est géré séparément -->
                        <xsl:when test="name()!='referential'">
                            <xsl:copy-of select="."/>
                        </xsl:when>
                    </xsl:choose>
                </xsl:for-each>

                <!-- Parcours des features du parent -->
                <xsl:for-each select="$parentEntity/feature/*">
                    <xsl:if test="not(name()='referential')">
                        <xsl:choose>
                            <!-- cas des handler -->
                            <xsl:when test="substring(name(), 1, 7)='handler'">
                                <!-- Si le handler est dans le parent et pas dans l'enfant, on le copie -->
                                <xsl:variable name="path"
                                              select="concat( 'feature/', name(), '[@id=&quot;', @id, '&quot;]' )"/>
                                <xsl:if test="not(java:kernel.Util.evaluatePath($child, $path))">
                                    <xsl:copy-of select="java:kernel.Util.parseVariables($child, .)"/>
                                </xsl:if>
                            </xsl:when>
                            <!-- cas des non-handler et non-referential -->
                            <xsl:otherwise>
                                <xsl:variable name="path" select="concat( 'feature/', name() )"/>
                                <xsl:if test="not(java:kernel.Util.evaluatePath($child, $path))">
                                    <xsl:copy-of select="."/>
                                </xsl:if>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:if>
                </xsl:for-each>
            </feature>

            <xsl:choose>
                <xsl:when test="primary-key">
                    <xsl:copy-of select="primary-key"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="$parentEntity/primary-key"/>
                </xsl:otherwise>
            </xsl:choose>

            <xsl:choose>
                <xsl:when test="functional-key">
                    <xsl:copy-of select="functional-key"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="$parentEntity/functional-key"/>
                </xsl:otherwise>
            </xsl:choose>

            <xsl:choose>
                <xsl:when test="foreign-keys">
                    <xsl:copy-of select="foreign-keys"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="$parentEntity/foreign-keys"/>
                </xsl:otherwise>
            </xsl:choose>

            <xsl:choose>
                <xsl:when test="methods">
                    <xsl:copy-of select="methods"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="$parentEntity/methods"/>
                </xsl:otherwise>
            </xsl:choose>

            <properties>
                <!-- Gestion de la surcharge des fields -->
                <xsl:for-each select="$parentEntity/properties/field">
                    <xsl:variable name="path" select="concat( 'properties/field[@name=&quot;', @name, '&quot;]' )"/>
                    <xsl:choose>
                        <!-- Le field n'est que dans le parent, on le copie directement -->
                        <xsl:when test="not(java:kernel.Util.evaluatePath($child, $path))">
                            <xsl:copy-of select="."/>
                        </xsl:when>
                        <!--  Sinon, il est à la fois dans le parent et l'enfant, on doit gérer la surcharge -->
                        <xsl:otherwise>
                            <field name="{@name}" type="{@type}">
                                <xsl:variable name="childField" select="java:kernel.Util.evaluatePath($child, $path)"/>
                                <!-- balise <description/> -->
                                <xsl:choose>
                                    <xsl:when test="$childField/description">
                                        <xsl:copy-of select="$childField/description"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:copy-of select="./description"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                                <!-- balise <sql/> -->
                                <xsl:choose>
                                    <xsl:when test="$childField/sql">
                                        <xsl:copy-of select="$childField/sql"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:copy-of select="./sql"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </field>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>
                <xsl:for-each select="properties/field">
                    <xsl:variable name="path" select="concat( 'properties/field[@name=&quot;', @name, '&quot;]' )"/>
                        <!-- Le field n'est que dans le fils, on le copie directement -->
                        <xsl:if test="not(java:kernel.Util.evaluatePath($parentEntity, $path))">
                            <xsl:copy-of select="."/>
                        </xsl:if>
                </xsl:for-each>
            </properties>

        </entity>
    </xsl:template>

    <xsl:template match="columns/*" mode="columns">
        <name>{@name}</name>
    </xsl:template>

    <xsl:template match="columns/*" mode="properties">
        <field name="{@name}" type="string">
            <sql type="varchar" precision="{@length}" required="true"/>
        </field>
    </xsl:template>

    <xsl:template match="structure">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="configuration">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="roles">
        <xsl:copy-of select="."/>
    </xsl:template>

</xsl:stylesheet>

