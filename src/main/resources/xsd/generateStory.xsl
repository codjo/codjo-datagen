<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      version="1.0"
      xmlns:xs="http://www.w3.org/2001/XMLSchema">
    <xsl:import href="/xsd/common.xsl"/>
    <xsl:output method="xml" encoding="iso-8859-1" indent="yes"/>

    <xsl:template match="/">
        <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
            <xsl:apply-templates select="tokio/table" mode="cases"/>

            <xs:element name="input">
                <xs:complexType>
                    <xs:sequence minOccurs="0">
                        <xs:choice maxOccurs="unbounded">
                            <xs:group ref="groupBodyContent"/>
                            <xs:element name="group" type="groupBody"/>
                        </xs:choice>
                    </xs:sequence>
                </xs:complexType>
            </xs:element>

            <xs:element name="output">
                <xs:complexType>
                    <xs:sequence minOccurs="0">
                        <xs:element ref="comparators" minOccurs="0"/>
                        <xs:sequence>
                            <xs:choice maxOccurs="unbounded">
                                <xs:group ref="groupBodyContent"/>
                                <xs:element name="group" type="groupBody"/>
                            </xs:choice>
                        </xs:sequence>
                    </xs:sequence>
                </xs:complexType>
            </xs:element>

            <xsl:call-template name="include-group"/>

            <xs:group name="groupBodyContent">
                <xs:choice>
                    <xs:element ref="table"/>
                    <xs:element ref="create-entity"/>
                    <xs:element ref="copy-entity"/>
                    <xsl:for-each select="tokio/table">
                        <xs:element ref="{@name}"/>
                    </xsl:for-each>
                </xs:choice>
            </xs:group>

            <xsl:call-template name="include-include-entities"/>
            <xsl:call-template name="include-create-entity"/>
            <xsl:call-template name="include-copy-entity"/>

            <xsl:call-template name="include-row"/>
            <xsl:call-template name="include-modify-row"/>

            <xs:element name="story">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element ref="comment" minOccurs="0"/>
                        <xs:element ref="properties" minOccurs="0"/>
                        <xs:element ref="property" minOccurs="0" maxOccurs="unbounded"/>
                        <xs:choice minOccurs="0" maxOccurs="unbounded">
                            <xs:element ref="include-story"/>
                            <xs:element ref="include-entities"/>
                        </xs:choice>
                        <xs:element ref="input" minOccurs="0"/>
                        <xs:element ref="output" minOccurs="0"/>
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

            <xs:element name="include-story">
                <xs:complexType>
                    <xs:attribute name="file" type="xs:string" use="required"/>
                </xs:complexType>
            </xs:element>

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

            <xsl:call-template name="include-table-attributeGroup"/>

            <xs:element name="table">
                <xs:complexType>
                    <xs:sequence minOccurs="0">
                        <xs:choice maxOccurs="unbounded">
                            <xs:element ref="row"/>
                            <xs:element ref="copy"/>
                            <xs:element ref="remove"/>
                            <xs:element ref="replace"/>
                        </xs:choice>
                    </xs:sequence>
                    <xs:attribute name="name" type="xs:string" use="required"/>
                    <xs:attribute name="temporary" type="xs:string" use="optional"/>
                    <xs:attributeGroup ref="table"/>
                </xs:complexType>
            </xs:element>
        </xs:schema>
    </xsl:template>
</xsl:stylesheet>

