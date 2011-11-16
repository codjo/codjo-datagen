<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      version="1.0"
      xmlns:xs="http://www.w3.org/2001/XMLSchema">
    <xsl:import href="/xsd/common.xsl"/>
    <xsl:output method="xml" encoding="iso-8859-1" indent="yes"/>

    <xsl:template match="/">
        <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
            <xsl:apply-templates select="tokio/table"/>

            <xs:element name="input">
                <xs:complexType>
                    <xs:sequence minOccurs="0">
                        <xs:choice maxOccurs="unbounded">
                            <xs:group ref="groupContent"/>
                        </xs:choice>
                    </xs:sequence>
                </xs:complexType>
            </xs:element>

            <xs:element name="etalon">
                <xs:complexType>
                    <xs:sequence minOccurs="0">
                        <xs:element ref="comparators" minOccurs="0"/>
                        <xs:sequence>
                            <xs:choice maxOccurs="unbounded">
                                <xs:group ref="groupContent"/>
                            </xs:choice>
                        </xs:sequence>
                    </xs:sequence>
                </xs:complexType>
            </xs:element>

            <xs:group name="groupContent">
                <xs:choice>
                    <xs:element ref="table"/>
                    <xsl:for-each select="tokio/table">
                        <xs:element ref="{@name}"/>
                    </xsl:for-each>
                </xs:choice>
            </xs:group>

            <xs:element name="Scenarii">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element ref="Scenario" maxOccurs="unbounded"/>
                    </xs:sequence>
                    <xs:attribute name="name" type="xs:string" use="optional"/>
                </xs:complexType>
            </xs:element>

            <xs:element name="Scenario">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element ref="comment" minOccurs="0"/>
                        <xs:element ref="properties" minOccurs="0"/>
                        <xs:element ref="property" minOccurs="0" maxOccurs="unbounded"/>
                        <xs:element ref="input" minOccurs="0"/>
                        <xs:element ref="etalon" minOccurs="0"/>
                    </xs:sequence>
                    <xs:attribute name="id" type="xs:ID" use="required"/>
                </xs:complexType>
            </xs:element>

            <xs:element name="comment">
                <xs:complexType mixed="true"/>
            </xs:element>

            <xs:element name="comparator">
                <xs:complexType>
                    <xs:attribute name="precision" type="xs:string" use="optional"/>
                    <xs:attribute name="assert" type="xs:string" use="optional"/>
                    <xs:attribute name="field" type="xs:string" use="required"/>
                    <xs:attribute name="param" type="xs:string" use="optional"/>
                </xs:complexType>
            </xs:element>

            <xs:element name="comparators">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element ref="comparator" minOccurs="0" maxOccurs="unbounded"/>
                    </xs:sequence>
                </xs:complexType>
            </xs:element>

            <xsl:call-template name="include-field"/>

            <xs:element name="properties">
                <xs:complexType>
                    <xs:attribute name="filename" type="xs:string" use="optional"/>
                    <xs:attribute name="overwrite" type="xs:string" use="optional"/>
                </xs:complexType>
            </xs:element>

            <xs:element name="property">
                <xs:complexType>
                    <xs:attribute name="name" type="xs:string" use="required"/>
                    <xs:attribute name="value" type="xs:string" use="required"/>
                </xs:complexType>
            </xs:element>

            <xs:element name="row">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element ref="field" minOccurs="0" maxOccurs="unbounded"/>
                    </xs:sequence>
                    <xs:attribute name="comment" type="xs:string" use="optional"/>
                    <xs:attribute name="inheritId" type="xs:IDREF" use="optional"/>
                    <xs:attribute name="id" type="xs:ID" use="optional"/>
                    <xs:attribute name="autoComplete" type="xs:boolean" use="optional"/>
                </xs:complexType>
            </xs:element>

            <xsl:call-template name="include-table-attributeGroup"/>

            <xs:element name="table">
                <xs:complexType>
                    <xs:sequence minOccurs="0">
                        <xs:element ref="row" maxOccurs="unbounded"/>
                    </xs:sequence>
                    <xs:attribute name="name" type="xs:string" use="required"/>
                    <xs:attribute name="temporary" type="xs:string" use="optional"/>
                    <xs:attributeGroup ref="table"/>
                </xs:complexType>
            </xs:element>
        </xs:schema>
    </xsl:template>

    <xsl:template match="table">
        <xs:element name="{@name}">
            <xs:complexType>
                <xs:sequence minOccurs="0">
                    <xs:choice maxOccurs="unbounded">
                        <xs:element name="row">
                            <xs:complexType>
                                <xs:all>
                                    <xs:element ref="field" minOccurs="0" maxOccurs="1"/>
                                    <xsl:apply-templates select="field"/>
                                </xs:all>
                                <xs:attribute name="id" type="xs:ID"/>
                                <xs:attribute name="comment" type="xs:string" use="optional"/>
                                <xs:attribute name="inheritId" type="xs:IDREF" use="optional"/>
                                <xs:attribute name="autoComplete" type="xs:boolean" use="optional"/>
                            </xs:complexType>
                        </xs:element>
                    </xs:choice>
                </xs:sequence>
                <xs:attributeGroup ref="table"/>
            </xs:complexType>
        </xs:element>
    </xsl:template>

    <xsl:template match="field">
        <xs:element name="{@name}" minOccurs="0" maxOccurs="1">
            <xs:complexType>
                <xs:group ref="generators" minOccurs="0" maxOccurs="1"/>
                <xs:attribute name="value"/>
            </xs:complexType>
        </xs:element>
    </xsl:template>
</xsl:stylesheet>

