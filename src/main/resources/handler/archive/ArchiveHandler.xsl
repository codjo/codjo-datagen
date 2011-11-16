<?xml version="1.0"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:java="http://xml.apache.org/xslt/java"
    exclude-result-prefixes="java">
<xsl:import href="/kernel/Kernel.xsl"/>
<xsl:output method="text" omit-xml-declaration="yes"/>
<xsl:param name="entityName">generated.PortfolioCodification</xsl:param>

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
  <xsl:variable name="archiveClasseName" select="java:kernel.Util.handlerClassName(feature/handler-archive/@id)"/>
  <xsl:variable name="beginDateVarName" select="feature/handler-archive/@beginDateVarName"/>
/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package <xsl:value-of select="java:bean.Util.extractPackage(@name)"/>;
<xsl:if test="feature/controlable">
import net.codjo.control.common.ControlException;
import net.codjo.control.common.manager.ControlManager;
</xsl:if>
import net.codjo.mad.server.handler.*;
import net.codjo.mad.server.util.*;
import java.util.*;
import org.exolab.castor.jdo.*;
import org.w3c.dom.*;
import org.xml.sax.*;
/**
 *  Handler d'Historisation pour <xsl:value-of select="@name"/>.
 */
public class <xsl:value-of select="$archiveClasseName"/> extends AbstractHandler {
    <xsl:if test="feature/controlable">
     private ControlManager controlManager;
    </xsl:if>
    private final static String oql = "SELECT p FROM <xsl:value-of select="@name"/> p WHERE <xsl:apply-templates select="primary-key/field" mode="oql"/>";
    Map setters = new HashMap();

    public <xsl:value-of select="$archiveClasseName"/>(<xsl:if test="feature/controlable">ControlManager controlManager</xsl:if>) {
        <xsl:if test="feature/controlable">
            this.controlManager = controlManager;
        </xsl:if>

            <xsl:apply-templates select="properties/field"/>
    }

    public String getId() {
        return "<xsl:value-of select="feature/handler-archive/@id"/>";
    }

    public String proceed(Node node) throws HandlerException {
        try {
            Map pks = XMLUtils.getPrimaryKeys(node);

            Database db = getContext().getDatabase();
            try {
                OQLQuery query = db.getOQLQuery(oql);
                <xsl:apply-templates select="primary-key/field" mode="fillOql"/>

                QueryResults results = query.execute();
                if (results.hasMore() == false) {
                    throw new IllegalArgumentException("ligne non trouvée");
                }

                <xsl:value-of select="$entityClassName"/> containingObj = (<xsl:value-of select="$entityClassName"/>) results.next();
                <xsl:value-of select="$entityClassName"/> obj = new <xsl:value-of select="$entityClassName"/>();

                update(containingObj, obj);
                update(XMLUtils.getRowFields(node), obj);

                <xsl:if test="feature/audit-trail">
                obj.getAudit().setCreationDatetime(new java.sql.Timestamp(System.currentTimeMillis()));
                obj.getAudit().setCreationBy(this.getContext().getUser());
                obj.getAudit().setUpdateBy(null);
                obj.getAudit().setUpdateDatetime(null);
                containingObj.getAudit().setUpdateBy(obj.getAudit().getCreationBy());
                containingObj.getAudit().setUpdateDatetime(obj.getAudit().getCreationDatetime());
                <xsl:choose>
                    <xsl:when test="$beginDateVarName='beginDate'">
                containingObj.setEndDate(obj.getBeginDate());
                    </xsl:when>
                    <xsl:otherwise>
                containingObj.setDateEnd(obj.getDateBegin());
                   </xsl:otherwise>
                </xsl:choose>
                </xsl:if>

                <xsl:if test="feature/controlable">
                executeControlForAdd(obj);
                </xsl:if>
                db.create(obj);
                 <xsl:if test="feature/handler-archive[@helper]">
               <xsl:value-of select="feature/handler-archive/@helper"/>.postArchive(db, containingObj, obj);
                </xsl:if>
                String requestId = XMLUtils.getAttribute(node, "request_id");
                String expected = "&lt;result request_id=\"" + requestId + "\"&gt;&lt;primarykey&gt;"
                    <xsl:apply-templates select="primary-key/field" mode="declare"/>
                          + "&lt;/primarykey&gt;&lt;row&gt;"
                   <xsl:apply-templates select="primary-key/field" mode="value"/>
                         + "&lt;/row&gt;&lt;/result&gt;";

                results.close();

                return expected;
            }
            finally {
                db.close();
            }
        }
        catch (HandlerException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new HandlerException(ex);
        }
    }

    private void update(<xsl:value-of select="$entityClassName"/> prev, <xsl:value-of select="$entityClassName"/> obj) {
        <xsl:apply-templates select="properties/field" mode="updateFromPrev"/>
    }

    private void update(Map fields, <xsl:value-of select="$entityClassName"/> obj) throws SAXException, HandlerException {
        for (Iterator i = fields.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry item = (Map.Entry) i.next();
            String name = item.getKey().toString();
            String value = (String) item.getValue();

            Setter props = (Setter) setters.get(name);
            try {
                props.set(obj, value);
            }
            catch (Exception ex) {
                if (props == null) {
                    throw new HandlerException("Attribut incorrecte " + name + " valeur " + value, ex);
                }
                else {
                    throw new HandlerException("Valeur '" + value + "' incorrecte pour " + name, ex);
                }
            }
        }
    }

    <xsl:if test="feature/controlable">
    private void executeControlForAdd(<xsl:value-of select="$entityClassName"/> obj) throws ControlException {
        controlManager.controlNewEntity(obj);
    }
    </xsl:if>

    private static interface Setter {
        public void set(<xsl:value-of select="$entityClassName"/> dv, String xmlValue);
    }
}

</xsl:template>
<!-- ***************************************************************************
    MAJ de l'obj à partir de l'objet historisé
-->
<xsl:template match="properties/field[@structure]" mode="updateFromPrev">
    <xsl:param name="name"><xsl:value-of select="@name"/></xsl:param>
    <xsl:param name="structure"><xsl:value-of select="@structure"/></xsl:param>
    <xsl:variable name="cname" select="java:bean.Util.capitalize(@name)"/>
    <xsl:apply-templates select="//data/structure[@name=$structure]/properties/field" mode="updateFromPrev">
            <xsl:with-param name="obj">obj.get<xsl:value-of select="$cname"/>()</xsl:with-param>
            <xsl:with-param name="prev">prev.get<xsl:value-of select="$cname"/>()</xsl:with-param>
            <xsl:with-param name="className"><xsl:value-of select="../../@name"/></xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<xsl:template match="properties/field[@type]" mode="updateFromPrev">
    <xsl:param name="obj">obj</xsl:param>
    <xsl:param name="prev">prev</xsl:param>
    <xsl:param name="className" select="../../@name"/>
    <xsl:param name="name" select="@name"/>
    <xsl:variable name="cname" select="java:bean.Util.capitalize(@name)"/>

    <xsl:value-of select="$obj"/>.set<xsl:value-of select="$cname"/>(<xsl:value-of select="$prev"/>.get<xsl:value-of select="$cname"/>());
    <xsl:text> </xsl:text>
</xsl:template>

<!-- ***************************************************************************
    Remplissage de la requete.
-->
<xsl:template match="primary-key/field" mode="fillOql">
    <xsl:param name="name"><xsl:value-of select="@name"/></xsl:param>
    <xsl:call-template name="bind">
        <xsl:with-param name="name" select="$name"/>
        <xsl:with-param name="type" select="../../properties/field[@name=$name]/@type"/>
    </xsl:call-template>
    <xsl:text> </xsl:text>
</xsl:template>

<!-- ***************************************************************************
    Declaration des pk dans la requete oql
-->
<xsl:template match="primary-key/field" mode="oql">
<xsl:if test="position()>1"> and </xsl:if>
<xsl:value-of select="@name"/> = $<xsl:value-of select="position()"/>
</xsl:template>

<!-- ***************************************************************************
    Declaration des property de type structure du bean pour le constructeur
-->
<xsl:template match="properties/field[@structure]">
    <xsl:param name="name"><xsl:value-of select="@name"/></xsl:param>
    <xsl:param name="structure"><xsl:value-of select="@structure"/></xsl:param>
    <xsl:variable name="cname" select="java:bean.Util.capitalize(@name)"/>
    <xsl:apply-templates select="//data/structure[@name=$structure]/properties/field">
            <xsl:with-param name="obj">dv.get<xsl:value-of select="$cname"/>()</xsl:with-param>
            <xsl:with-param name="className"><xsl:value-of select="../../@name"/></xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- ***************************************************************************
    Declaration des property du bean pour le constructeur
-->
<xsl:template match="properties/field[@type]">
    <xsl:param name="obj">dv</xsl:param>
    <xsl:param name="className" select="../../@name"/>
    <xsl:param name="name" select="@name"/>
    <xsl:variable name="cname" select="java:bean.Util.capitalize(@name)"/>
    <xsl:variable name="type"> <xsl:apply-templates select="@type"/> </xsl:variable>

    <xsl:choose>
        <xsl:when test="$type='boolean'">
            setters.put("<xsl:value-of select="$name"/>",
            new Setter() {
                public void set(<xsl:value-of select="java:bean.Util.extractClassName($className)"/> dv, String xmlValue) {
                    <xsl:value-of select="$obj"/>.set<xsl:value-of select="$cname"/>((XMLUtils.convertFromStringValue(Boolean.class, xmlValue)).booleanValue());
                }
            });
        </xsl:when>
        <xsl:when test="$type='int'">
            setters.put("<xsl:value-of select="$name"/>",
            new Setter() {
                public void set(<xsl:value-of select="java:bean.Util.extractClassName($className)"/> dv, String xmlValue) {
                    <xsl:value-of select="$obj"/>.set<xsl:value-of select="$cname"/>((XMLUtils.convertFromStringValue(Integer.class, xmlValue)).intValue());
                }
            });
        </xsl:when>
        <xsl:when test="$type='double'">
            setters.put("<xsl:value-of select="$name"/>",
            new Setter() {
                public void set(<xsl:value-of select="java:bean.Util.extractClassName($className)"/> dv, String xmlValue) {
                    <xsl:value-of select="$obj"/>.set<xsl:value-of select="$cname"/>((XMLUtils.convertFromStringValue(Double.class, xmlValue)).doubleValue());
                }
            });
        </xsl:when>
        <xsl:otherwise>
            setters.put("<xsl:value-of select="$name"/>",
            new Setter() {
                public void set(<xsl:value-of select="java:bean.Util.extractClassName($className)"/> dv, String xmlValue) {
                    <xsl:value-of select="$obj"/>.set<xsl:value-of select="$cname"/>(XMLUtils.convertFromStringValue(<xsl:value-of select="$type"/>.class, xmlValue));
                }
            });
        </xsl:otherwise>
    </xsl:choose>

</xsl:template>

</xsl:stylesheet>

