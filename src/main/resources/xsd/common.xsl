<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      version="1.0"
      xmlns:xs="http://www.w3.org/2001/XMLSchema">
    <xsl:output method="xml" encoding="iso-8859-1" indent="yes"/>

    <xsl:template name="include-group">
        <xs:complexType name="groupBody">
            <xs:sequence minOccurs="0">
                <xs:choice maxOccurs="unbounded">
                    <xs:element type="groupBody" name="group"/>
                    <xs:group ref="groupBodyContent"/>
                </xs:choice>
            </xs:sequence>
            <xs:attribute name="name" type="xs:string" use="optional"/>
        </xs:complexType>
    </xsl:template>

    <xsl:template name="include-include-entities">
        <xs:element name="include-entities">
            <xs:complexType>
                <xs:attribute name="file" type="xs:string" use="required"/>
            </xs:complexType>
        </xs:element>
    </xsl:template>

    <xsl:template name="include-create-entity">
        <xs:element name="create-entity">
            <xs:complexType>
                <xs:sequence>
                    <xs:element name="parameter" minOccurs="0" maxOccurs="unbounded">
                        <xs:complexType>
                            <xs:complexContent>
                                <xs:extension base="parameter">
                                    <xs:attribute name="null" type="xs:boolean" use="optional"/>
                                    <xs:attribute name="value" type="xs:string" use="optional"/>
                                </xs:extension>
                            </xs:complexContent>
                        </xs:complexType>
                    </xs:element>
                </xs:sequence>
                <xs:attribute name="name" type="xs:string" use="required"/>
                <xs:attribute name="comment" type="xs:string" use="optional"/>
                <xs:attribute name="id" type="xs:ID" use="optional"/>
            </xs:complexType>
        </xs:element>

        <xs:complexType name="parameter">
            <xs:group ref="generators" minOccurs="0" maxOccurs="1"/>
            <xs:attribute name="name" type="xs:string" use="required"/>
        </xs:complexType>
    </xsl:template>

    <xsl:template name="include-copy-entity">
        <xs:element name="copy-entity">
            <xs:complexType>
                <xs:attribute name="entity" type="xs:string" use="required"/>
                <xs:attribute name="comment" type="xs:string" use="optional"/>
                <xs:attribute name="id" type="xs:ID" use="required"/>
            </xs:complexType>
        </xs:element>
    </xsl:template>

    <xsl:template name="include-row">
        <xs:attributeGroup name="row_attributes">
            <xs:attribute name="comment" type="xs:string" use="optional"/>
            <xs:attribute name="autoComplete" type="xs:boolean" use="optional"/>
        </xs:attributeGroup>

        <xs:element name="row">
            <xs:complexType>
                <xs:sequence>
                    <xs:element ref="field" minOccurs="0" maxOccurs="unbounded"/>
                </xs:sequence>
                <xs:attribute name="id" type="xs:ID" use="optional"/>
                <xs:attributeGroup ref="row_attributes"/>
            </xs:complexType>
        </xs:element>

        <xsl:call-template name="include-field"/>
    </xsl:template>

    <xsl:template name="include-field">
        <xs:complexType name="field_attributes">
            <xs:group ref="generators" minOccurs="0" maxOccurs="1"/>
            <xs:attribute name="value"/>
        </xs:complexType>

        <xs:element name="field">
            <xs:complexType>
                <xs:complexContent>
                    <xs:extension base="field_attributes">
                        <xs:attribute name="name" use="required"/>
                    </xs:extension>
                </xs:complexContent>
            </xs:complexType>
        </xs:element>

        <xs:group name="generators">
            <xs:choice>
                <xs:element name="generateString">
                    <xs:complexType>
                        <xs:attribute name="precision" type="xs:integer" use="required"/>
                    </xs:complexType>
                </xs:element>
                <xs:element name="generateNumeric">
                    <xs:complexType>
                        <xs:attribute name="precision" type="xs:string" use="required"/>
                    </xs:complexType>
                </xs:element>
                <xs:element name="generateInt">
                    <xs:complexType>
                        <xs:attribute name="precision" type="xs:string" use="required"/>
                    </xs:complexType>
                </xs:element>
                <xs:element name="generateBoolean"/>
                <xs:element name="generateDate"/>
                <xs:element name="generateDateTime"/>
            </xs:choice>
        </xs:group>
    </xsl:template>

    <xsl:template name="include-modify-row">
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

        <xs:element name="remove">
            <xs:complexType>
                <xs:attribute name="row" type="xs:string" use="required"/>
                <xs:attribute name="comment" type="xs:string" use="optional"/>
            </xs:complexType>
        </xs:element>

        <xs:attributeGroup name="replace">
            <xs:attribute name="row" type="xs:string" use="required"/>
            <xs:attribute name="comment" type="xs:string" use="optional"/>
            <xs:attribute name="autoComplete" type="xs:boolean" use="optional"/>
        </xs:attributeGroup>

        <xs:element name="replace">
            <xs:complexType>
                <xs:sequence>
                    <xs:element ref="field" minOccurs="0" maxOccurs="unbounded"/>
                </xs:sequence>
                <xs:attributeGroup ref="replace"/>
            </xs:complexType>
        </xs:element>
    </xsl:template>

    <xsl:template name="include-table-attributeGroup">
        <xs:attributeGroup name="table">
            <xs:attribute name="orderClause" type="xs:string" use="optional"/>
            <xs:attribute name="identityInsert" use="optional">
                <xs:simpleType>
                    <xs:restriction base="xs:string">
                        <xs:enumeration value="on"/>
                        <xs:enumeration value="off"/>
                        <xs:enumeration value="true"/>
                        <xs:enumeration value="false"/>
                    </xs:restriction>
                </xs:simpleType>
            </xs:attribute>
        </xs:attributeGroup>
    </xsl:template>

    <xsl:template match="table" mode="cases">
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
                        <xs:element ref="remove"/>
                        <xs:element name="replace">
                            <xs:complexType>
                                <xs:complexContent>
                                    <xs:extension base="{@name}.fields">
                                        <xs:attributeGroup ref="replace"/>
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
                <xsl:apply-templates select="field" mode="cases"/>
            </xs:all>
        </xs:complexType>
    </xsl:template>

    <xsl:template match="field" mode="cases">
        <xs:element name="{@name}" minOccurs="0" type="field_attributes"/>
    </xsl:template>
</xsl:stylesheet>

