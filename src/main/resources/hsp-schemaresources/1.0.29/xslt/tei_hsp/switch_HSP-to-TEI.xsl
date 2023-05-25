<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ MIT License
  ~
  ~ Copyright (c) 2023 Staatsbibliothek zu Berlin - Preußischer Kulturbesitz
  ~
  ~ Permission is hereby granted, free of charge, to any person obtaining a copy
  ~ of this software and associated documentation files (the "Software"), to deal
  ~ in the Software without restriction, including without limitation the rights
  ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  ~ copies of the Software, and to permit persons to whom the Software is
  ~ furnished to do so, subject to the following conditions:
  ~
  ~ The above copyright notice and this permission notice shall be included in all
  ~ copies or substantial portions of the Software.
  ~
  ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  ~ SOFTWARE.
  -->

<xsl:stylesheet
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="tei xs"
    version="2.0">
    
    <xsl:output method="xml" encoding="UTF-8" indent="no"/>
    <xsl:param name="TEI-schema">1.0.29</xsl:param>

    <xsl:template match="node() | @* | processing-instruction() | comment()">
        <xsl:copy>
            <xsl:apply-templates select="node() | @* | processing-instruction() | comment()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:msDesc | tei:msPart">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="tei:msIdentifier"/>
            <xsl:apply-templates select="tei:head"/>
            <xsl:apply-templates select="tei:msContents"/>
            <xsl:apply-templates select="tei:physDesc"/>
            <xsl:apply-templates select="tei:history"/>
            <xsl:apply-templates select="tei:additional"/>
            <xsl:apply-templates select="tei:p"/>
            <xsl:apply-templates select="tei:msPart[@type='binding']"/>
            <xsl:apply-templates select="tei:msPart[@type='fragment']"/>
            <xsl:apply-templates select="tei:msPart[@type='booklet']"/>
            <xsl:apply-templates select="tei:msPart[@type='accMat']"/>
            <xsl:apply-templates select="tei:msPart[@type='other']"/>
        </xsl:copy>
    </xsl:template>
    
</xsl:stylesheet>
