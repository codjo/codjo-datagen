<?xml version="1.0"?>
<xsl:stylesheet
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      version="1.0"
      xmlns:java="http://xml.apache.org/xslt/java"
      exclude-result-prefixes="java">
    <xsl:import href="/kernel/Kernel.xsl"/>
    <xsl:output method="text" omit-xml-declaration="yes"/>
    <xsl:param name="entityName">generated.Dividend</xsl:param>
    <xsl:param name="handlerId">neoSelect</xsl:param>

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
        <xsl:variable name="selectClassName" select="java:bean.Util.capitalize($handlerId)"/>
        <xsl:variable name="sqlQuery" select="java:kernel.Util.flatten(feature/handler-sql[@id=$handlerId]/query)"/>
        <xsl:variable name="hasQueryFactory" select="feature/handler-sql[@id=$handlerId]/query[@factory]"/>
        <xsl:variable name="hasParameter" select="feature/handler-sql[@id=$handlerId]/query[@with-parameter='true']"/>
        <xsl:variable name="inTransaction" select="feature/handler-sql[@id=$handlerId and @transaction='true']"/>
        <xsl:variable name="inTransactionForce"
                      select="feature/handler-sql[@id=$handlerId and @force-transaction-mode='true']"/>
        <xsl:variable name="factory" select="$hasQueryFactory/@factory"/>
        package <xsl:value-of select="java:bean.Util.extractPackage(@name)"/>;
        import net.codjo.database.api.Database;
        import net.codjo.database.api.query.PreparedQuery;
        import net.codjo.mad.server.handler.XMLUtils;
        import net.codjo.mad.server.handler.sql.SqlHandler;
        import net.codjo.mad.server.handler.sql.Getter;
        <xsl:if test="$hasQueryFactory">
        import net.codjo.mad.server.handler.sql.QueryBuilder;
        <xsl:if test="$hasParameter">
        import net.codjo.mad.server.handler.sql.QueryParameterFiller;
        </xsl:if>
        import net.codjo.mad.server.handler.HandlerException;
        </xsl:if>
        import java.sql.*;
        import java.util.Map;
        /**
        * Classe Handler de selection pour <xsl:value-of select="@name"/>. &lt;p&gt;
        *
        <xsl:choose>
            <xsl:when test="$hasQueryFactory">
                * Query générée par factory.
            </xsl:when>
            <xsl:otherwise>
                * Query : &lt;code&gt;<xsl:value-of select="$sqlQuery"/>&lt;/code&gt;
            </xsl:otherwise>
        </xsl:choose>
        */
        public class <xsl:value-of select="$selectClassName"/>Handler extends SqlHandler {

        private static final String[] PK =
        {
        <xsl:if test="not(feature/handler-sql[@id=$handlerId and @return-pk='false'])">
            <xsl:apply-templates select="primary-key/field" mode="declaration"/>
        </xsl:if>
        };

        <xsl:if test="$hasQueryFactory">
            private QueryBuilder queryBuilder;
        </xsl:if>

        public <xsl:value-of select="$selectClassName"/>Handler(Database database)
        <xsl:if test="$hasQueryFactory">throws Exception</xsl:if>
        {
        super(PK, "<xsl:value-of select="$sqlQuery"/>"<xsl:if test="$inTransaction">, true</xsl:if>, database);
        <xsl:apply-templates select="feature/handler-sql[@id=$handlerId]/attributes/name"/>

        <xsl:if test="$inTransactionForce">
            <xsl:message terminate="no">
                <xsl:text>!!mode transactionnel force :</xsl:text><xsl:value-of select="$handlerId"/>
            </xsl:message>
        </xsl:if>
        <xsl:if test="not($inTransactionForce)">
            <xsl:if
                  test="$inTransaction and java:bean.Util.containsWordIgnoreCase($sqlQuery,'select') and not($hasQueryFactory)">
                <xsl:if
                      test="not(java:bean.Util.containsWordIgnoreCase($sqlQuery,'delete') or java:bean.Util.containsWordIgnoreCase($sqlQuery,'insert') or java:bean.Util.containsWordIgnoreCase($sqlQuery,'update') or java:bean.Util.containsWordIgnoreCase($sqlQuery,'exec'))">
                    <xsl:message terminate="yes">handler-sql[id='<xsl:value-of
                          select="$handlerId"/>'] - Les requêtes de type select doivent se faire hors transaction !
                    </xsl:message>
                </xsl:if>
            </xsl:if>
            <xsl:if
                  test="not($inTransaction) and  (java:bean.Util.containsWordIgnoreCase($sqlQuery,'delete') or java:bean.Util.containsWordIgnoreCase($sqlQuery,'insert') or java:bean.Util.containsWordIgnoreCase($sqlQuery,'update')) and not($hasQueryFactory)">
                <xsl:message terminate="yes">handler-sql[id='<xsl:value-of
                      select="$handlerId"/>'] - Les requêtes de modification doivent se faire en transaction !
                </xsl:message>
            </xsl:if>
        </xsl:if>
        <xsl:if test="$hasQueryFactory">
            queryBuilder = (QueryBuilder)Class.forName("<xsl:value-of select="$factory"/>").newInstance();
        </xsl:if>
        }

        public String getId() {
        return "<xsl:value-of select="$handlerId"/>";
        }

        @Override
        protected void fillQuery(PreparedQuery query, Map&lt;String, String> pks) throws SQLException {
        <xsl:if test="$hasQueryFactory">
            if (pks == null) {
            return;
            }
        <xsl:if test="$hasParameter">
            ((QueryParameterFiller)queryBuilder).fillQuery(query, pks);
        </xsl:if>

            int idx = 1;
        </xsl:if>
        <xsl:apply-templates select="feature/handler-sql[@id=$handlerId]/arg" mode="sql"/>
        }
        <xsl:if test="$hasQueryFactory">
            @Override
            protected String buildQuery(Map&lt;String, String> arguments) throws HandlerException {
            return queryBuilder.buildQuery(arguments, this);
            }
        </xsl:if>
        }

    </xsl:template>

    <!-- ***************************************************************************
        Remplissage de la requete en mode SQL.
    -->
    <xsl:template match="feature/handler-sql[@id=$handlerId]/arg" mode="sql">
        <xsl:param name="name">
            <xsl:value-of select="."/>
        </xsl:param>
        <xsl:variable name="hasQueryFactory" select="../query/@factory"/>
        <xsl:variable name="idx">
            <xsl:choose>
                <xsl:when test="$hasQueryFactory">
                    <xsl:value-of select="'idx++'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="position()"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:if test="$hasQueryFactory">
            if (pks.containsKey("<xsl:value-of select="$name"/>")) {
        </xsl:if>
        <xsl:choose>
            <xsl:when test="@type">
                <xsl:call-template name="sqlSetParam">
                    <xsl:with-param name="name" select="$name"/>
                    <xsl:with-param name="type" select="@type"/>
                    <xsl:with-param name="idx" select="$idx"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="sqlSetParam">
                    <xsl:with-param name="name" select="$name"/>
                    <xsl:with-param name="type" select="../../../properties/field[@name=$name]/@type"/>
                    <xsl:with-param name="idx" select="$idx"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="$hasQueryFactory">
            }
        </xsl:if>
    </xsl:template>

    <!-- ***************************************************************************
        Declaration des pk.
    -->
    <xsl:template match="primary-key/field" mode="declaration">
        <xsl:if test="position()>1">,</xsl:if>
        "<xsl:value-of select="@name"/>"
    </xsl:template>

    <!-- ***************************************************************************
        Declaration des attributs accessible apar la requete
    <xsl:value-of select="../../../../properties/field[@name=$name and @type='string']"/>
    -->
    <xsl:template match="attributes/name">
        <xsl:variable name="fieldName" select="."/>
        <xsl:variable name="type">
            <xsl:choose>
                <xsl:when test="@type">
                    <xsl:value-of select="@type"/>
                </xsl:when>
                <xsl:when test="../../../../properties/field[@name=$fieldName and @type]">
                    <xsl:value-of select="../../../../properties/field[@name=$fieldName]/@type"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:for-each select="../../../../properties/field[@structure]">
                        <xsl:variable name="structureName" select="@structure"/>
                        <xsl:apply-templates
                              select="../../../structure[@name=$structureName]/properties/field[@name=$fieldName]"
                              mode="select_field_type"/>
                    </xsl:for-each>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="javaSqlTypesConstant" select="java:handler.Util.convertTypeJavaSqlTypesConstant($type)"/>
        <xsl:variable name="fieldIndex">
            <xsl:value-of select="position()"/>
        </xsl:variable>
        <xsl:variable name="constructorParameter"
                      select="java:handler.Util.buildGetterConstructorParameter($fieldIndex,$javaSqlTypesConstant)"/>

        addGetter("<xsl:value-of select="."/>", new Getter(<xsl:value-of select="$constructorParameter"/>));
    </xsl:template>

    <xsl:template match="field" mode="select_field_type">
        <xsl:value-of select="@type"/>
    </xsl:template>
</xsl:stylesheet>

