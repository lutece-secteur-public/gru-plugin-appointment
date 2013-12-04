<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="portlet">
        <div class="portlet append-bottom">
            <xsl:choose>			
                <xsl:when test="not(string(display-portlet-title)='1')">
                    <div class="portlet-header -lutece-border-radius-top">
                        <xsl:value-of disable-output-escaping="yes" select="portlet-name" />
                    </div>
                    <div class="portlet-content -lutece-border-radius-bottom">
                        <xsl:apply-templates select="appointmentportlet-portlet" />
                    </div>
                </xsl:when>
                <xsl:otherwise>
                    <div class="portlet-content -lutece-border-radius">
                        <xsl:apply-templates select="appointmentportlet-portlet" />
                    </div>
                </xsl:otherwise>
            </xsl:choose>
        </div>
    </xsl:template>

    <xsl:template match="appointmentportlet-portlet">
        <xsl:apply-templates select="appointmentportlet-portlet-content" />
    </xsl:template>

    <xsl:template match="appointmentportlet-portlet-content">
        <xsl:value-of disable-output-escaping="yes" select="." />
    </xsl:template>

</xsl:stylesheet>

