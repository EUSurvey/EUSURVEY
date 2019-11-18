<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" session="true" %>
<!DOCTYPE html>
<html>
<head>
	<title>EUSurvey - <spring:message code="label.Documentation" /></title>	
	<%@ include file="../includes.jsp" %>	
		
	<style type="text/css">

		<c:choose>
			<c:when test="${USER != null && runnermode == null }">
				.anchor {
					 display: block;
					 height: 110px;
					 margin-top: -110px;
					 visibility: hidden;
				}
			</c:when>
			<c:otherwise>
				.anchor {
					 display: block;
					 height: 40px;
					 margin-top: -40px;
					 visibility: hidden;
				}
			</c:otherwise>
		</c:choose>
		
		.anchorTop
		{
			float: right;
			font-size: 13px;
			font-weight: normal;
			text-decoration: none;
		}				
		
		.anchorlink {
			margin-left: 40px;
			text-decoration: underline;
			color: #005580;
		}
		
		.anchorlink a:hover {
			text-decoration: underline;
			color: #005580;
		}
		
		.head {
			margin-left: 20px;
		}
	</style>
	
	<script type="text/javascript">
		$(function() {
		 
			 
			 $("a.anchorTop").click(function(){
				 $('html, body').animate({scrollTop : 0},100);
					return false;
			 });
			 
		});
	</script>
	
	<script language="javascript" type="text/javascript" src="${contextpath}/resources/js/tree/treemenu.js?version=<%@include file="../version.txt" %>"></script>
	<link rel="stylesheet" href="${contextpath}/resources/js/tree/treeview.css?version=<%@include file="../version.txt" %>" type="text/css">


	<script type="text/javascript">
	
		$(document).ready(function(){
			
			ddtreemenu.createTree("treemenu", false, 0,"${contextpath}");
			
			 $("a.anchorTop").click(function(){
				 $('html, body').animate({scrollTop : 0},100);
					return false;
			 });
			 
		});
	
	</script>
</head>
<body>

	<%@ include file="../header.jsp" %>	

	<a name="topAnchor"></a>

	<c:choose>
		<c:when test="${USER != null && runnermode == null }">
			<%@ include file="../menu.jsp" %>	
			<div class="page" style="margin-top: 110px">
		</c:when>
		<c:when test="${responsive != null}">
			<div class="page" style="margin-top: 40px; max-width: 100%; padding: 10px;">
			<div class="alert alert-warning">Wichtige Information: Um Umfragen zu erstellen und zu verwalten, öffnen Sie die EUSurvey Website bitte mit einem Computer. Es ist nicht zu empfehlen sich mit einem Handy oder Tablet in EUSurvey einzuloggen.</div>
		</c:when>
		<c:otherwise>
			<div class="page" style="margin-top: 40px;">
		</c:otherwise>
	</c:choose>	
	
		<div class="pageheader">
			<div style="float:right; font-size:125%" >
			[<a href="helpparticipants?faqlanguage=en">EN</a>] [<a href="helpparticipants?faqlanguage=fr">FR</a>] [<a href="helpparticipants?faqlanguage=de">DE</a>]
			</div>
			<h1>Fragen und Antworten für Teilnehmer</h1>
		</div>

		<h2>Inhalt</h2>
		<div id="ulContainer">
	
			<a href="javascript:ddtreemenu.flatten('treemenu', 'expand')">Expand All</a>&nbsp;|&nbsp;<a href="javascript:ddtreemenu.flatten('treemenu', 'contact')">Collapse All</a>
			<br/><br/>
			<ul id="treemenu" class="treeview" rel="closed">
				<li><a class="anchorlink" href="#_Toc1">Wie kann ich mit dem Autor der Umfrage Kontakt aufnehmen?</a></li>
				<li><a class="anchorlink head" href="#_Toc369865010">Eine Umfrage einsehen</a>
					<ul>
						<li><a class="anchorlink" href="#_Toc369865012">Was bedeutet &quot;Die eingegebene URL ist falsch&quot;?</a></li>
						<li><a class="anchorlink" href="#_Toc369865013">Was bedeutet &quot;Seite nicht gefunden&quot;?</a></li>
						<li><a class="anchorlink" href="#_Toc369865026">Welche Browser unterstützt EUSurvey?</a></li>
						<li><a class="anchorlink" href="#_Toc369865027">Kann ich mobile Endger&auml;te oder Tablet-PCs benutzen um meine Umfrage zu beantworten?</a></li>
											</ul>
				</li>
				<li><a class="anchorlink head" href="#_Toc369865014">Einen Beitrag einreichen</a>
					<ul>
						<li><a class="anchorlink" href="#_Toc369865015">Was bedeutet &quot;Dies ist keine gültige Zahl/kein gültiges Datum/keine gültige E-Mail-Adresse&quot;?</a></li>
						<li><a class="anchorlink" href="#_Toc369865016">Warum verschwindet die von mir gewählte Antwort auf eine Matrix-Auswahlfrage?</a></li>
					</ul>
				</li>
				
					<li><a class="anchorlink head" href="#_Toc369865016a">Missbrauch melden</a></li>
								
				<li><a class="anchorlink head" href="#_Toc369865017">Nach dem Beitrag</a>
					<ul>
						<li><a class="anchorlink" href="#_Toc369865018">Kann ich meinen Beitrag nach dem Absenden einsehen oder drucken?</a></li>
						<li><a class="anchorlink" href="#_Toc369865019">Wie kann ich eine PDF-Kopie meines Beitrags speichern?</a></li>
						<li><a class="anchorlink" href="#_Toc369865020">Kann ich meinen Beitrag nach dem Absenden bearbeiten?</a></li>
						<li><a class="anchorlink" href="#_Toc369865021">Ich habe gerade einen Beitrag zu einer Umfrage eingesandt.  Kann ich einsehen, was andere Personen geantwortet haben?</a></li>
						<li><a class="anchorlink" href="#_Toc369865022">Beim Öffnen der PDF Version meines Beitrags erscheint eine Fehlermeldung &quot;Unzureichende Bildinformation&quot;</a></li>
						<li><a class="anchorlink" href="#_Toc369865023">Warum werden in der PDF-Exportdatei der Umfrage kleine Kästchen angezeigt?</a></li>
						<li><a class="anchorlink" href="#_Toc369865028">Wo kann ich meine als Entwurf gespeicherten Antworten finden?</a></li>
					</ul>
				</li>
				<li><a class="anchorlink head" href="#_Toc369865024">Schutz der Privatsphäre</a>
					<ul>
						<li><a class="anchorlink" href="#_Toc369865025">Dieses System verwendet Cookies. Welche Informationen werden dabei gespeichert?</a></li>
					</ul>
				</li>
			</ul>
		</div>
		<br/ ><br />
		
		<h2><a class="anchor" name="_Toc1"></a>Wie kann ich mit dem Autor der Umfrage Kontakt aufnehmen?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
		<p>Per E-Mail – klicken Sie auf <i>Kontakt</i> (rechts oben auf der ersten Seite der Umfrage).</p>
		
		<h1><a class="anchor" name="_Toc369865010"></a>Eine Umfrage einsehen</h1>
		
		<h2><a class="anchor" name="_Toc369865012"></a>Was bedeutet &quot;Die eingegebene URL ist falsch&quot;?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
		<p>Das System kann Ihnen keinen Zugang gewähren.  Dies ist meistens dann der Fall, wenn eine vor einiger Zeit aktive Einladung gelöscht oder deaktiviert wurde, weil der Zeitraum für die Aktivierung verstrichen war.</p>
		<p>Wenn Sie glauben, dass der von Ihnen benutzte Zugangslink doch gültig ist, wenden Sie sich bitte an den Autor der Umfrage.</p>
		
		<h2><a class="anchor" name="_Toc369865013"></a>Was bedeutet &quot;Seite nicht gefunden&quot;?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
		<p>	Das bedeutet:
			<ul>
				<li>Sie verwenden einen fehlerhaften Link, um auf Ihre Umfrage bei EUSurvey zuzugreifen, oder</li>
				<li>die von Ihnen gesuchte Umfrage wurde bereits aus dem System entfernt.</li>
			</ul>
	 		Wenn der Link Ihrer Ansicht nach doch gültig ist, teilen Sie dies dem Autor bitte direkt mit. Anderenfalls informieren Sie bitte die für die Veröffentlichung des Links verantwortliche Stelle über den Fehler.</p>
		
		<h2><a class="anchor" name="_Toc369865026"></a>Welche Browser unterstützt EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
		<p>EUSurvey unterst&uuml;tzt die letzten beiden Versionen des Internet Explorers, von Mozilla Firefox und Google Chrome.</p>
		<p>Die Verwendung anderer Browser kann Kompatibilit&auml;tsprobleme verursachen.</p>
		
		<h2><a class="anchor" name="_Toc369865027"></a>Kann ich mobile Endger&auml;te oder Tablet-PCs benutzen um meine Umfrage zu beantworten?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
		<p>Ja, EUSurvey stellt ein sog. &bdquo;Responsive Webdesign&ldquo; f&uuml;r ver&ouml;ffentlichte Umfragen zur Verf&uuml;gung. EUSurvey bietet Ihnen dadurch eine gleichbleibende Benutzerfreundlichkeit auf dem Computer-Desktop, Tablet und Smartphone.</p>
		
		<h1><a class="anchor" name="_Toc369865014"></a>Einen Beitrag einreichen</h1>
		
		<h2><a class="anchor" name="_Toc369865015"></a>Was bedeutet &quot;Dies ist keine gültige Zahl/kein gültiges Datum/keine gültige E-Mail-Adresse&quot;?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
		<p>In EUSurvey kann der Autor einer Umfrage Fragen wählen, auf die eine Antwort in einem bestimmten Format erwartet wird – z. B. eine Zahl, ein Datum oder eine E-Mail-Adresse. So muss ein Datum das Format TT/MM/JJJJ haben.</p>
		
		<h2><a class="anchor" name="_Toc369865016"></a>Warum verschwindet die von mir gewählte Antwort auf eine Matrix-Auswahlfrage?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
		<p>Bei Matrix-Fragen kann eingestellt sein, dass Sie jede Antwort nur ein einziges Mal auswählen können. So lässt sich eine Rangordnung der Antworten erzwingen.</p>
		
			<h1><a class="anchor" name="_Toc369865016a"></a>Missbrauch melden<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h1>
			<p>Wenn eine Umfrage illegale Inhalte enthält oder die Rechte anderer verletzt (einschließlich geistiger Eigentumsrechte, Wettbewerbsrecht und allgemeines Recht), verwenden Sie bitte den Link "Missbrauch melden" auf der rechten Seite.</p>
		<p>Weitere Informationen hierzu finden Sie in den <a href="${contextpath}/home/tos">EUSurvey-Nutzungsbedingungen</a>.</p>
			
		<h1><a class="anchor" name="_Toc369865017"></a>Nach dem Beitrag</h1>
		
		<h2><a class="anchor" name="_Toc369865018"></a>Kann ich meinen Beitrag nach dem Absenden einsehen oder drucken?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
		<p>Selbstverständlich. Sobald Sie Ihr Dokument versandt haben, bietet Ihnen das System die Möglichkeit an, es auszudrucken.</p>
		
		<h2><a class="anchor" name="_Toc369865019"></a>Wie kann ich eine PDF-Kopie meines Beitrags speichern?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
		<p>Sobald Sie Ihr Dokument versandt haben, bietet Ihnen das System eine PDF-Datei Ihres Beitrags zum Herunterladen an.</p>
		
		<h2><a class="anchor" name="_Toc369865020"></a>Kann ich meinen Beitrag nach dem Absenden bearbeiten?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
		<p>Dies hängt von den Einstellungen für die jeweilige Umfrage ab.  </p>
		<p>Bei manchen Umfragen können Sie <a target="_blank" href="${contextpath}/home/editcontribution">nach Versand erneut Zugang erhalten</a>, bei anderen ist diese Möglichkeit nicht vorgesehen.</p>
		
		<h2><a class="anchor" name="_Toc369865021"></a>Ich habe gerade einen Beitrag zu einer Umfrage eingesandt.  Kann ich einsehen, was andere Personen geantwortet haben?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
		<p>Dies hängt von den Einstellungen für die jeweilige Umfrage ab. </p>
		<p>Wenn Sie nach dem Einreichen Ihres Beitrags keinen Link zu den veröffentlichten Ergebnissen sehen, ist diese Funktion möglicherweise nicht vorgesehen.</p>
		<p>Wenn Sie der Meinung sind, dass die Ergebnisse dieser Umfrage von allgemeinem Interesse sind, wenden Sie sich bitte an den <a href="#_Toc1">Autor der Umfrage</a>.</p>
			
		<h2><a class="anchor" name="_Toc369865022"></a>Beim Öffnen der PDF Version meines Beitrags erscheint eine Fehlermeldung &quot;Unzureichende Bildinformation&quot;<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
		<p>Wird ein beschädigtes Bild hochgeladen, kann der PDF Leser es nicht ordnungsgemäß anzeigen. <br/>Dies löst einen internen Fehler in Ihrem PDF-Leser aus.</p>
		<p>In einem solchen Fall müssen Sie das Bild entfernen oder reparieren.</p>
		
		<h2><a class="anchor" name="_Toc369865023"></a>Warum werden in der PDF-Exportdatei der Umfrage  kleine Kästchen angezeigt?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
		<p>Dieses Phänomen tritt auf, wenn die von den Autoren der Umfrage oder den Teilnehmern verwendeten Zeichensätze von der Anwendung nicht unterstützt werden.</p>
		<p>Wenn das System ein bestimmtes Zeichen nicht findet, ersetzt es dieses durch ein kleines Kästchen, um anzuzeigen, dass es im PDF-Format nicht wiedergegeben werden kann.</p>
		<p>Sie können über die Kontaktadresse im dafür vorgesehenen Abschnitt melden, dass ein nicht unterstütztes Zeichen verwendet wurde.</p>
		<p>Dies hat keinerlei Einfluss auf Ihren Beitrag. Sobald Ihr Beitrag korrekt gespeichert wurde, kann er von der für die Umfrage zuständigen Behörde problemlos angezeigt und exportiert werden – selbst wenn die PDF-Maschine der Anwendung Ihre Zeichen nicht korrekt wiedergeben konnte.</p>
		
		<h2><a class="anchor" name="_Toc369865028"></a>Wo kann ich meine als Entwurf gespeicherten Antworten finden?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
		<p>Nachdem Sie auf &bdquo;Als Entwurf speichern&ldquo; geklickt haben, werden Sie automatisch auf eine andere Seite weitergeleitet. Dort sehen Sie den Link, auf dem Sie Ihren Entwurf wiederfinden k&ouml;nnen um ihre Beitr&auml;ge zu bearbeiten und einzureichen. <b>Bitte speichern Sie diesen Link!</b> Sie k&ouml;nnen ihn per Email versenden, zu Ihrer Favoritenliste hinzuf&uuml;gen oder in die Zwischenablage kopieren.</p>
		
		<h1><a class="anchor" name="_Toc369865024"></a>Schutz der Privatsphäre</h1>
		
		<h2><a class="anchor" name="_Toc369865025"></a>Dieses System verwendet Cookies. Welche Informationen werden dabei gespeichert?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
		<p>Das Informatiksystem verwendet sogenannte &quot;Cookies&quot;, um die Kommunikation zwischen Client und Server zu gewährleisten. Ihr Browser muss also so konfiguriert sein, dass er Cookies akzeptiert. Nach dem Abmelden werden die Cookies gelöscht.</p>
		<p>Das System speichert Ihre Beiträge zu einer Umfrage lokal, um beispielsweise bei einer Unterbrechung der Serververbindung während der Übermittlung des Beitrags oder bei versehentlichem Abschalten Ihres Computers über eine Sicherheitskopie zu verfügen.
		   Gespeichert werden die Kennungen der Fragen und die zugehörigen Antworten in der jeweils letzten Fassung. Sobald Sie Ihren Beitrag an den Server übermittelt und darauf gespeichert haben, werden die lokal gespeicherten Daten gelöscht.
		   Über der Umfrage befindet sich ein Kästchen &quot;Lokale Sicherheitskopie anlegen (bei öffentlichen / gemeinsam genutzten Computern deaktivieren)&quot;, um diese Funktion auszuschalten. In dem Fall werden keine Daten auf Ihrem Rechner gespeichert.</p>
		
	</div>

	<%@ include file="../footer.jsp" %>		

</body>
</html>
