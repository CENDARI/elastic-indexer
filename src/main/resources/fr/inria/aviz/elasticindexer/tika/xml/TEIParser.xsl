<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:exsl="http://exslt.org/common"
  xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:tei="http://www.tei-c.org/ns/1.0">

	<xsl:output encoding="UTF-8" method="xml"/>
	
    <xsl:template match="tei:surname|tei:forename|tei:roleName|tei:addName|tei:genName|tei:date">
    	<xsl:text> </xsl:text>
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    	<xsl:text> </xsl:text>
    </xsl:template>
    
    <xsl:template match="tei:fw"/>
    
    <xsl:template match="tei:choice">
	    <xsl:choose>
	    	<xsl:when test="tei:reg">
    			<xsl:value-of select="tei:reg"/>
		    </xsl:when>
	    	<xsl:when test="tei:corr">
    			<xsl:value-of select="tei:corr"/>
		    </xsl:when>
		    <xsl:when test="tei:orig">
		    	<xsl:value-of select="tei:orig"/>
    		</xsl:when>
    		<xsl:otherwise>
		        <xsl:copy>
            		<xsl:apply-templates select="@*|node()"/>
        		</xsl:copy>
    		</xsl:otherwise>
    	</xsl:choose>
    </xsl:template>
    
	<xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    
    
</xsl:stylesheet>