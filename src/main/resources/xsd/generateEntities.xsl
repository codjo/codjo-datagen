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

            <xsl:call-template name="include-group"/>

            <xs:group name="groupBodyContent">
                <xs:choice>
                    <xs:element ref="table"/>
                    <xs:element ref="create-entity"/>
                    <xsl:for-each select="tokio/table">
                        <xs:element ref="{@name}"/>
                    </xsl:for-each>
                </xs:choice>
            </xs:group>

            <xs:complexType name="groupRequired">
                <xs:sequence minOccurs="0">
                    <xs:choice maxOccurs="unbounded">
                        <xs:element type="groupRequired" name="group"/>
                        <xs:group ref="groupRequiredContent"/>
                    </xs:choice>
                </xs:sequence>
                <xs:attribute name="name" type="xs:string" use="optional"/>
            </xs:complexType>

            <xs:group name="groupRequiredContent">
                <xs:choice>
                    <xs:element ref="table"/>
                    <xsl:for-each select="tokio/table">
                        <xs:element name="{@name}" type="{@name}.required"/>
                    </xsl:for-each>
                </xs:choice>
            </xs:group>

            <xsl:call-template name="include-include-entities"/>
            <xsl:call-template name="include-create-entity"/>

            <xsl:call-template name="include-row"/>

            <xs:element name="entities">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element ref="include-entities" minOccurs="0" maxOccurs="unbounded"/>
                        <xs:element ref="entity" maxOccurs="unbounded"/>
                    </xs:sequence>
                </xs:complexType>
            </xs:element>

            <xs:element name="entity">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element ref="comment" minOccurs="0"/>
                        <xs:element ref="parameters" minOccurs="0"/>
                        <xs:element name="body">
                            <xs:complexType>
                                <xs:sequence>
                                    <xs:choice maxOccurs="unbounded">
                                        <xs:group ref="groupBodyContent"/>
                                        <xs:element name="group" type="groupBody"/>
                                    </xs:choice>
                                </xs:sequence>
                            </xs:complexType>
                        </xs:element>
                        <xs:element name="required" minOccurs="0">
                            <xs:complexType>
                                <xs:sequence>
                                    <xs:choice maxOccurs="unbounded">
                                        <xs:group ref="groupRequiredContent"/>
                                        <xs:element name="group" type="groupRequired"/>
                                    </xs:choice>
                                </xs:sequence>
                            </xs:complexType>
                        </xs:element>
                    </xs:sequence>
                    <xs:attribute name="id" type="xs:ID" use="required"/>
                </xs:complexType>
            </xs:element>

            <xs:element name="comment">
                <xs:complexType mixed="true"/>
            </xs:element>

            <xs:attributeGroup name="copy">
                <xs:attribute name="row" type="xs:string" use="required"/>
                <xs:attribute name="comment" type="xs:string" use="optional"/>
                <xs:attribute name="id" type="xs:ID" use="optional"/>
                <xs:attribute name="autoComplete" type="xs:boolean" use="optional"/>
            </xs:attributeGroup>

            <xs:element name="copy">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element ref="field" minOccurs="0" maxOccurs="unbounded"/>
                    </xs:sequence>
                    <xs:attributeGroup ref="copy"/>
                </xs:complexType>
            </xs:element>

            <xs:element name="parameters">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="parameter" maxOccurs="unbounded">
                            <xs:complexType>
                                <xs:complexContent>
                                    <xs:extension base="parameter">
                                        <xs:attribute name="default" type="xs:string" use="optional"/>
                                    </xs:extension>
                                </xs:complexContent>
                            </xs:complexType>
                        </xs:element>
                    </xs:sequence>
                </xs:complexType>
            </xs:element>

            <xsl:call-template name="include-table-attributeGroup"/>

            <xs:element name="table">
                <xs:complexType>
                    <xs:sequence>
                        <xs:choice maxOccurs="unbounded">
                            <xs:element ref="row"/>
                            <xs:element ref="copy"/>
                        </xs:choice>
                    </xs:sequence>
                    <xs:attribute name="name" type="xs:string" use="required"/>
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
                        <xs:element name="copy">
                            <xs:complexType>
                                <xs:complexContent>
                                    <xs:extension base="{@name}.fields">
                                        <xs:attributeGroup ref="copy"/>
                                    </xs:extension>
                                </xs:complexContent>
                            </xs:complexType>
                        </xs:element>
                        <xs:element name="row">
                            <xs:complexType>
                                <xs:complexContent>
                                    <xs:extension base="{@name}.fields">
                                        <xs:attribute name="id" type="xs:ID"/>
                                        <xs:attributeGroup ref="row_attributes"/>
                                    </xs:extension>
                                </xs:complexContent>
                            </xs:complexType>
                        </xs:element>
                    </xs:choice>
                </xs:sequence>
                <xs:attributeGroup ref="table"/>
            </xs:complexType>
        </xs:element>

        <xs:complexType name="{@name}.fields" abstract="true">
            <xs:all>
                <xs:element ref="field" minOccurs="0"/>
                <xsl:apply-templates select="field"/>
            </xs:all>
        </xs:complexType>

        <xs:complexType name="{@name}.required">
            <xs:sequence minOccurs="0" maxOccurs="unbounded">
                <xs:element name="row">
                    <xs:complexType>
                        <xs:all>
                            <xs:element name="unique-key" minOccurs="0">
                                <xs:complexType>
                                    <xs:all>
                                        <xsl:for-each select="field">
                                            <xs:element name="{@name}" minOccurs="0"/>
                                        </xsl:for-each>
                                    </xs:all>
                                </xs:complexType>
                            </xs:element>
                            <xs:element ref="field" minOccurs="0"/>
                            <xsl:for-each select="field">
                                <xs:element name="{@name}" minOccurs="0" type="field_attributes"/>
                            </xsl:for-each>
                        </xs:all>
                        <xs:attributeGroup ref="row_attributes"/>
                    </xs:complexType>
                </xs:element>
            </xs:sequence>
            <xs:attributeGroup ref="table"/>
        </xs:complexType>
    </xsl:template>

    <xsl:template match="field">
        <xs:element name="{@name}" minOccurs="0" type="field_attributes"/>
    </xsl:template>
</xsl:stylesheet>

