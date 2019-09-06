<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" version="1.0" encoding="UTF-8" indent="no" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"/>

	<xsl:template match="SurveyTranslation">
		<html>
			<head>
				<title>EUSurvey</title>
				<meta http-equiv="Cache-Control" content="no-cache"></meta>
				<meta http-equiv="Pragma" content="no-cache"></meta>
				<meta http-equiv="Expires" content="Mon, 22 Jul 2002 11:12:01 GMT"></meta>
				<meta http-equiv="content-type" content="text/html; charset=utf-8"></meta>
				<style type="text/css">
					body {
						font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
						font-size: 13px;
					}
				
					table {
					
					}
					
					table td {
						border: 1px solid #ddd;
						padding: 5px;
					}
				
					.element {
						border-bottom: 1px solid #ddd;
						margin: 5px;
						padding: 10px;
					}
					.elementtitle {
						font-size: 110%;						
					}
					.sectiontitle {
						font-size: 160%;
						color: #004F98;
					}
					.elementshortname {
						font-size: 90%;
						color: #777;
						margin-bottom: 10px;
					}
					.elementhelp {
						color: #777;
						font-style: italic;
						margin: 5px;
					}
					.answertitle {
						
					}
				</style>
			</head>
			<body>
				<!-- TITLE -->
				<h1>
					<xsl:value-of select="Title"/> (<xsl:value-of select="Lang"/>)
				</h1>
						
				<!-- ELEMENTS -->
				<xsl:apply-templates select="Element"/>
				
				<h2>Confirmation Page Text</h2>
				<xsl:value-of select="ConfirmationPage"/>
				
				<h2>Escape Page Text</h2>
				<xsl:value-of select="EscapePage"/>

			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="Label">
       <xsl:copy-of select="node()"/>
    </xsl:template>
	
	<xsl:template match="Element">
		<div class="element">
		
			<xsl:choose>
				<xsl:when test="@type = 'Section'">
						<div class="sectiontitle"><xsl:apply-templates select="Label"/></div>
						<div class="elementshortname"><xsl:value-of select="TabTitle"/></div>
				</xsl:when>
				<xsl:otherwise>
						<div class="elementtitle"><xsl:apply-templates select="Label"/></div>
				</xsl:otherwise>
			</xsl:choose>
		
			
			<xsl:if test="Help">
				<div class="elementhelp"><xsl:value-of select="Help"/></div>
			</xsl:if>	
			
			<xsl:variable name="type" select="@type"/>
			<xsl:variable name="id" select="@key"/>
			
			<xsl:choose>
				<xsl:when test="@type = 'MultipleChoiceQuestion'">					
					<xsl:for-each select="Answer">
						<input type="checkbox" name="{id}"></input><xsl:apply-templates select="Label"/><br />
					</xsl:for-each>
				</xsl:when>
				<xsl:when test="@type = 'SingleChoiceQuestion'">					
					<xsl:for-each select="Answer">
						<input type="radio" name="{id}"></input><xsl:apply-templates select="Label"/><br />
					</xsl:for-each>
				</xsl:when>
				<xsl:when test="@type = 'FreeTextQuestion'"><input type="text" /></xsl:when>
				<xsl:when test="@type = 'DateQuestion'"><input type="text" /></xsl:when>
				<xsl:when test="@type = 'EmailQuestion'"><input type="text" /></xsl:when>
				<xsl:when test="@type = 'RegExQuestion'"><input type="text" /></xsl:when>
				<xsl:when test="@type = 'NumberQuestion'">
					<input type="text" /><xsl:value-of select="./Unit"/>
				</xsl:when>
				
				<xsl:when test="@type = 'Upload'">
					<input type="button" value="Upload File"></input>
				</xsl:when>
				<xsl:when test="@type = 'Download'">
					<a href="#">File</a>
				</xsl:when>
				<xsl:when test="@type = 'GalleryQuestion'">
					<div>[you will see images in the real survey at this position]</div>
				</xsl:when>
				<xsl:when test="@type = 'Image'">
					<div>[you will an image in the real survey at this position]</div>
				</xsl:when>
				<xsl:when test="@type = 'Text'"></xsl:when>
				
				<xsl:when test="@type = 'Table'">
					<xsl:variable name="cols" select="number(@cols)"/>
					<xsl:variable name="element" select="."/>
					<table cellspacing="0" cellpadding="0">
						<tbody>					
							<tr>
								<td></td>
								<xsl:for-each select="Children/Element">
									<xsl:if test="position() &lt; $cols">
										<td><xsl:apply-templates select="Label"/></td>
									</xsl:if>
								</xsl:for-each>
							</tr>
							<xsl:for-each select="Children/Element">
								<xsl:if test="position() &gt;= $cols">
									<tr>
										<td><xsl:apply-templates select="Label"/></td>
										<xsl:for-each select="$element/Children/Element">
											<xsl:if test="position() &lt;= $cols - 1">
												<td><input type="text"></input></td>
											</xsl:if>
										</xsl:for-each>
									</tr>
								</xsl:if>
							</xsl:for-each>
						</tbody>
					</table>
				</xsl:when>
				
				<xsl:when test="@type = 'Matrix'">
					<xsl:variable name="cols" select="number(@cols)"/>
					<xsl:variable name="element" select="."/>
					<table cellspacing="0" cellpadding="0">
						<tbody>					
							<tr>
								<td></td>
								<xsl:for-each select="Children/Element">
									<xsl:if test="position() &lt; $cols">
										<td><xsl:apply-templates select="Label"/></td>
									</xsl:if>
								</xsl:for-each>
							</tr>
							<xsl:for-each select="Children/Element">
								<xsl:if test="position() &gt;= $cols">
									<tr>
										<td><xsl:apply-templates select="Label"/></td>
										<xsl:for-each select="$element/Children/Element">
											<xsl:if test="position() &lt;= $cols - 1">
												<td><input type="checkbox"></input></td>
											</xsl:if>
										</xsl:for-each>
									</tr>
								</xsl:if>
							</xsl:for-each>
						</tbody>
					</table>
				</xsl:when>
				
				
			</xsl:choose>

		
		</div>
	</xsl:template>
	
	
</xsl:stylesheet>
