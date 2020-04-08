<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
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
		
		#ulContainer {
			margin-bottom: 50px;
		}
	</style>

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
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>
	
		<a name="topAnchor"></a>
	
		<c:choose>
			<c:when test="${USER != null && runnermode == null }">
				<%@ include file="../menu.jsp" %>	
				<div class="page" style="padding-top: 110px">
			</c:when>
			<c:otherwise>
				<div class="page" style="padding-top: 40px">
			</c:otherwise>
		</c:choose>	
	
		
			<div class="pageheader">
				<div style="float:right; font-size:125%" >
				[<a href="helpauthors?faqlanguage=en">EN</a>] [<a href="helpauthors?faqlanguage=fr">FR</a>] [<a href="helpauthors?faqlanguage=de">DE</a>]
				</div>
				<h1>Fragen und Antworten für Autoren</h1>
			</div>
			
			<h2>Inhalt</h2>
	
	<div id="ulContainer">
	
		<a href="javascript:ddtreemenu.flatten('treemenu', 'expand')">Alles &ouml;ffnen</a>&nbsp;|&nbsp;<a href="javascript:ddtreemenu.flatten('treemenu', 'contact')">Alles schlie&szlig;en</a>
		<br/>
		<br/>
		<ul id="treemenu" class="treeview" rel="closed">
			<li><a class="anchorlink head" href="#_Toc0">Allgemeine Fragen</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc0-1">Was ist EUSurvey?</a></li>
					<li><a class="anchorlink" href="#_Toc0-2">Wann nutze ich EUSurvey?</a></li>
					<li><a class="anchorlink" href="#_Toc0-3">Was kann EUSurvey nicht leisten?</a></li>
					<li><a class="anchorlink" href="#_Toc0-4">Welche Funktionen bietet EUSurvey?</a></li>
					<li><a class="anchorlink" href="#_Toc0-5">Fragebogenverwaltung</a></li>
					<li><a class="anchorlink" href="#_Toc0-6">Ergebnisverwaltung</a></li>
					<li><a class="anchorlink" href="#_Toc0-7">Wo finde ich weitere Informationen über EUSurvey?</a></li>
					<li><a class="anchorlink" href="#_Toc0-8">Wohin kann ich mich bei technischen Problemen mit EUSurvey wenden?</a></li>
					<li><a class="anchorlink" href="#_Toc0-9">Wohin wende ich mich, wenn ich Verbesserungsvorschl&auml;ge im Zusammenhang mit EUSurvey habe?</a></li>
					<li><a class="anchorlink" href="#_Toc0-10">Welche Browser unterstützt EUSurvey?</a></li>
					<li><a class="anchorlink" href="#_Toc0-11">EUSurvey-Haftungsausschluss (nur f&uuml;r externe Nutzer)</a></li>
					<li><a class="anchorlink" href="#_Toc0-12">K&ouml;nnen meine Teilnehmer mobile Endger&auml;te nutzen um meine Umfrage zu beantworten?</a></li>
					<li><a class="anchorlink" href="#_Toc0-13">Gibt es eine minimale Bildschirmgröße?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc1">Anmeldung und EU Login Registrierung</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc1-1">Ich habe ein EU-Login-Konto. Muss ich mich separat bei EUSurvey registrieren?</a></li>
					<li><a class="anchorlink" href="#_Toc1-2">Wie verbinde ich mich mit EUSurvey?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc2">Umfrage erstellen</a>
				<ul>
					<li><a class="anchorlink " href="#_Toc2-1">Wie erstelle ich eine neue Umfrage?</a></li>
					<li><a class="anchorlink" href="#_Toc2-2">Wie importiere ich eine bereits vorhandene Umfrage aus meinem Rechner?</a></li>
					<li><a class="anchorlink" href="#_Toc2-3">Wie importiere ich eine bereits vorhandene Umfrage aus IPM?</a></li>
					<li><a class="anchorlink" href="#_Toc2-4">Wo finde ich die von mir erstellten Umfragen?</a></li>
					<li><a class="anchorlink " href="#_Toc2-5">Wie &ouml;ffne ich eine vorhandene Umfrage zur Bearbeitung usw.?</a></li>
					<li><a class="anchorlink" href="#_Toc2-6">Wie exportiere ich eine vorhandene Umfrage?</a></li>
					<li><a class="anchorlink" href="#_Toc2-7">Wie kopiere ich eine vorhandene Umfrage?</a></li>
					<li><a class="anchorlink" href="#_Toc2-8">Wie entferne ich eine vorhandene Umfrage?</a></li>
					<li><a class="anchorlink" href="#_Toc2-9">Wie erstelle ich mit EUSurvey einen Fragebogen gem&auml;ß der Richtlinien für barrierefreie Webinhalte (WCAG)?</a></li>
					<li><a class="anchorlink" href="#_Toc2-10">Wie erstelle ich einen Quiz-Fragebogen?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc3">Umfrage bearbeiten</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc3-1">Wie starte ich den Editor?</a></li>
					<li><a class="anchorlink" href="#_Toc3-2">Wie erstelle ich einen Fragebogen mit dem EUSurvey Editor?</a></li>
					<li><a class="anchorlink" href="#_Toc3-3">Wie f&uuml;ge ich Fragen hinzu oder entferne sie?</a></li>
					<li><a class="anchorlink" href="#_Toc3-4">Wie bearbeite ich einzelne Elemente in meiner Umfrage?</a></li>
					<li><a class="anchorlink" href="#_Toc3-10">Wie kann ich Elemente kopieren?</a></li>
					<li><a class="anchorlink" href="#_Toc3-11">Wie f&uuml;ge ich meinen Fragen m&ouml;gliche Antworten hinzu, oder wie entferne ich Antworten?</a></li>
					<li><a class="anchorlink" href="#_Toc3-12">Was kann ich tun, damit eine Frage zu einer Pflichtfrage wird?</a></li>
					<li><a class="anchorlink" href="#_Toc3-13">Wie kann ich Elemente innerhalb des Fragebogens verschieben?</a></li>
					<li><a class="anchorlink" href="#_Toc3-14">Wie erreiche ich, dass gewisse Fragen nur angezeigt werden, wenn eine bestimmte Antwort gegeben wurde (&bdquo;abh&auml;ngige Elemente&ldquo;)?</a></li>
					<li><a class="anchorlink" href="#_Toc3-7">Kann ich die Antworten auf eine Frage mit mehreren Antworten, aus denen nur eine oder mehrere ausgew&auml;hlt werden k&ouml;nnen, sortiert erscheinen lassen?</a></li>
					<li><a class="anchorlink" href="#_Toc3-5">Wie erlaube ich anderen Nutzern, meine Umfrage zu bearbeiten?</a></li>
					<li><a class="anchorlink" href="#_Toc3-8">Welche Sprachen werden von der Anwendung unterst&uuml;tzt?</a></li>
					<li><a class="anchorlink" href="#_Toc3-9">Warum UTF-8? Welche Zeichens&auml;tze sollte ich verwenden?</a></li>
					<li><a class="anchorlink" href="#_Toc3-6">Was bedeutet &bdquo;Komplexit&auml;t&ldquo;?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc4">Sicherheit der Umfrage</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc4-1">Wie schr&auml;nke ich den Zugriff auf meine Umfrage ein?</a></li>
					<li><a class="anchorlink" href="#_Toc4-3">Wie lege ich ein Passwort für meine Umfrage fest?</a></li>
					<li><a class="anchorlink" href="#_Toc4-4">Wie sorge ich dafür, dass ein Nutzer nicht mehr als eine festgelegte Anzahl Beitr&auml;ge zu meiner Umfrage einsendet?</a></li>
					<li><a class="anchorlink " href="#_Toc4-5">Wie verhindere ich, dass Bots mehrfach Beitr&auml;ge zu meiner Umfrage übermitteln?</a></li>
					<li><a class="anchorlink" href="#_Toc4-6">Kann ich meinen Teilnehmern erlauben, nach dem Einsenden ihrer Beitr&auml;ge auf diese zuzugreifen?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc5">Umfrage testen</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc5-1">Kann ich mir ansehen, wie sich meine Umfrage nach der Ver&ouml;ffentlichung verhalten wird?</a></li>
					<li><a class="anchorlink" href="#_Toc5-2">Wie k&ouml;nnen meine Kollegen meine Umfrage vor der Ver&ouml;ffentlichung testen?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc6">Übersetzungen</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc6-1">Wie übersetze ich eine Umfrage?</a></li>
					<li><a class="anchorlink" href="#_Toc6-2">Wie kann ich eine vorhandene Übersetzung in meine Umfrage hochladen?</a></li>
					<li><a class="anchorlink" href="#_Toc6-3">Kann ich eine vorhandene Übersetzung online bearbeiten?</a></li>
					<li><a class="anchorlink" href="#_Toc6-4">Kann ich meine Übersetzungen offline erstellen?</a></li>
					<li><a class="anchorlink" href="#_Toc6-6">Wie ver&ouml;ffentliche ich meine Übersetzungen bzw. wie hebe ich Ver&ouml;ffentlichungen auf? Warum kann ich diese Übersetzung nicht ver&ouml;ffentlichen? Was ist eine &bdquo;unfertige&ldquo; Übersetzung?</a></li>
					<li><a class="anchorlink" href="#_Toc6-7">Kann ich Übersetzungen in nichteurop&auml;ischen Sprachen hochladen?</a></li>	
					<li><a class="anchorlink" href="#_Toc6-8">Was bedeutet &bdquo;Maschinelle Übersetzung anfordern&ldquo;?</a></li>
					<li><a class="anchorlink" href="#_Toc6-5">Hinweise für Mitarbeiter der EU</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc7">Umfrage ver&ouml;ffentlichen</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc7-1">Wie ver&ouml;ffentliche ich meine Umfrage?</a></li>
					<li><a class="anchorlink" href="#_Toc7-2">Kann ich die URL anpassen, die zu meiner Umfrage führt?</a></li>
					<li><a class="anchorlink" href="#_Toc7-7">Kann ich einen direkten Link zu einer Übersetzung meiner Umfrage angeben?</a></li>
					<li><a class="anchorlink" href="#_Toc7-3">Wie kann ich veranlassen, dass meine Umfrage von selbst ver&ouml;ffentlicht wird, wenn ich im Urlaub bin?</a></li>
					<li><a class="anchorlink" href="#_Toc7-4">Kann ich eine Erinnerung erhalten, bevor meine Umfrage endet?</a></li>
					<c:if test="${enablepublicsurveys}">
					<li><a class="anchorlink" href="#_Toc7-5">Wie setze ich meine Umfrage auf die Liste der &ouml;ffentlichen Umfragen in EUSurvey?</a></li>
					</c:if>
					<li><a class="anchorlink" href="#_Toc7-6">F&uuml;r Mitarbeiter der EU: Was muss ich beachten, wenn ich eine &ouml;ffentlich zug&auml;ngliche, offene Umfrage ver&ouml;ffentlichen m&ouml;chte (&bdquo;Ihre Stimme in Europa&ldquo; Webseite)?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc8">Umfrage verwalten</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc8-1">Kann ich Fehler in meiner Umfrage korrigieren, die mir erst sp&auml;ter auffallen?</a></li>
					<li><a class="anchorlink" href="#_Toc8-2">Gehen eingereichte Antworten verloren, wenn ich meine Umfrage &auml;ndere?</a></li>
					<li><a class="anchorlink" href="#_Toc8-3">Wie kann ich den Titel meiner Umfrage &auml;ndern?</a></li>
					<li><a class="anchorlink" href="#_Toc8-4">Wie kann ich die Kontaktadresse meiner Umfrage &auml;ndern?</a></li>
					<li><a class="anchorlink" href="#_Toc8-5">Wie kann ich die Bestätigungsnachricht abändern? </a></li>
					<li><a class="anchorlink" href="#_Toc8-6">Wie passe ich die Standard-Abbruchmeldung an? </a></li>
					<li><a class="anchorlink" href="#_Toc8-7">Archivierung</a></li>
					<li><a class="anchorlink" href="#_Toc8-8">Wie erhalten andere Nutzer Zugang zu meiner Umfrage?</a></li>
					<li><a class="anchorlink" href="#_Toc8-9">Was sind Aktivitätsprotokolle?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc9">Ergebnisse analysieren, exportieren und ver&ouml;ffentlichen</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc9-1">Wo finde ich die von meinen Teilnehmern eingereichten Beitr&auml;ge?</a></li>
					<li><a class="anchorlink" href="#_Toc9-2">Wie kann ich eingereichte Antworten herunterladen?</a></li>
					<li><a class="anchorlink" href="#_Toc9-3">Wie kann ich eine definierte Teilmenge aller Beitr&auml;ge finden und analysieren?</a></li>
					<li><a class="anchorlink" href="#_Toc9-4">Wie gelange ich zum vollst&auml;ndigen Bestand der Antworten zurück, nachdem ich eine Teilmenge von Beitr&auml;gen definiert habe?</a></li>
					<li><a class="anchorlink" href="#_Toc9-5">Wie ver&ouml;ffentliche ich meine Ergebnisse?</a></li>
					<li><a class="anchorlink" href="#_Toc9-6">Wie kann ich auf die ver&ouml;ffentlichten Ergebnisse zugreifen?</a></li>
					<li><a class="anchorlink" href="#_Toc9-7">Wie erhalten andere Nutzer Zugang zu den Ergebnissen meiner Umfrage?</a></li>
					<li><a class="anchorlink" href="#_Toc9-8">Meine Exportdateien lassen sich nicht entpacken</a></li>
					<li><a class="anchorlink" href="#_Toc9-9">Ver&ouml;ffentlichte Ergebnisse - von Teilnehmern hochgeladene Dokumente, die personenbezogene Daten enthalten</a></li>
					<li><a class="anchorlink" href="#_Toc9-10">Wie kann ich meine Umfrage gestalten, um die Ergebnisse mit oder ohne personenbezogene Daten zu ver&ouml;ffentlichen?</a></li>
					<li><a class="anchorlink" href="#_Toc9-11">Warum sind meine Ergebnisse nicht aktuell?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc10">Design und Layout</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc10-1">Wie &auml;ndere ich das allgemeine Erscheinungsbild meiner Umfrage?</a></li>
					<li><a class="anchorlink" href="#_Toc10-2">Wie kann ich meine eigenen Umfrage-&bdquo;Themen&ldquo; erstellen?</a></li>
					<li><a class="anchorlink" href="#_Toc10-3">Wie füge ich meiner Umfrage ein Logo hinzu?</a></li>
					<li><a class="anchorlink" href="#_Toc10-4">Wie füge ich meiner Umfrage nützliche Links hinzu?</a></li>
					<li><a class="anchorlink" href="#_Toc10-5">Wo lade ich Hintergrunddokumente für meine Umfrage hoch?</a></li>
					<li><a class="anchorlink" href="#_Toc10-6">Wie erstelle ich eine Umfrage mit mehreren Seiten?</a></li>
					<li><a class="anchorlink" href="#_Toc10-7">Wie aktiviere ich eine automatische Nummerierung für meine Umfrage? </a></li>
					<li><a class="anchorlink" href="#_Toc10-8">Kann ich eine individuelle Skin f&uuml;r meine Umfrage erstellen?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc11">Kontakte und Einladungen verwalten</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc11-1">Was ist das &bdquo;Adressbuch&ldquo;?</a></li>
					<li><a class="anchorlink" href="#_Toc11-2">Was sind die &bdquo;Attribute&ldquo; eines Kontakts?</a></li>
					<li><a class="anchorlink" href="#_Toc11-3">Wie füge ich dem Adressbuch neue Kontakte hinzu?</a></li>
					<li><a class="anchorlink" href="#_Toc11-4">Was ist ein &bdquo;Registrierungsformular&ldquo;?</a></li>
					<li><a class="anchorlink" href="#_Toc11-5">Wie importiere ich mehrere Kontakte aus einer Datei in mein Adressbuch?</a></li>
					<li><a class="anchorlink" href="#_Toc11-6">Wie bearbeite ich einen Attributwert für mehrere Kontakte gleichzeitig?</a></li>
					<li><a class="anchorlink" href="#_Toc11-7">Kann ich Kontakte aus meinem Adressbuch auf meinen Rechner exportieren?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc12">Teilnehmer einladen</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc12-1">Wie lege ich eine Gruppe m&ouml;glicher Teilnehmer fest? Was ist eine &bdquo;G&auml;steliste&ldquo;?</a></li>
					<li><a class="anchorlink" href="#_Toc12-2">Wie bearbeite/entferne ich eine bestehende G&auml;steliste?</a></li>
					<li><a class="anchorlink" href="#_Toc12-3">Wie schicke ich meinen Teilnehmern eine E-Mail mit einer Einladung?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc13">Das eigene Konto verwalten</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc13-1">Wie &auml;ndere ich mein Passwort?</a></li>
					<li><a class="anchorlink" href="#_Toc13-2">Wie &auml;ndere ich meine E-Mail-Adresse?</a></li>
					<li><a class="anchorlink" href="#_Toc13-3">Wie &auml;ndere ich meine Standard-Spracheinstellung?</a></li>
				</ul>
			</li>
			<li><a class="anchorlink head" href="#_Toc14">Datenschutz</a>
				<ul>
					<li><a class="anchorlink" href="#_Toc14-1">Dieses System verwendet Cookies. Welche Informationen werden dabei gespeichert?</a></li>
					<li><a class="anchorlink" href="#_Toc14-2">Welche Informationen speichert EUSurvey, wenn Teilnehmer einen Beitrag einsenden?</a></li>
					<li><a class="anchorlink" href="#_Toc14-3">Muss ich eine Datenschutzerkl&auml;rung in meine Umfrage aufnehmen?</a></li>
				</ul>
			</li>
		</ul>
	
	</div>
	
	<h1 style="margin-top: 40px"><a class="anchor" name="_Toc0"></a>Allgemeine Fragen</h1>
	<h2><a class="anchor" name="_Toc0-1"></a>Was ist EUSurvey??<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>EUSurvey ist ein Instrument  zur Verwaltung von Online-Umfragen. Damit lassen sich in den meisten Web-Browsern Frageb&ouml;gen und sonstige interaktive Formulare erstellen, ver&ouml;ffentlichen und verwalten.</p>
	<h2><a class="anchor" name="_Toc0-2"></a>Wann nutze ich EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>EUSurvey bietet sich zur Nutzung an, wenn Sie einen Fragebogen oder ein interaktives Formular online zug&auml;nglich machen oder eine gro&szlig;e Anzahl &auml;hnlicher Datens&auml;tze erheben wollen.</p>
	<h2><a class="anchor" name="_Toc0-3"></a>Was kann EUSurvey nicht leisten?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>EUSurvey ist unter Umst&auml;nden nicht für Ihr Projekt geeignet, wenn:</p>
		<ul>
			<li>es erforderlich ist, dass verschiedene Teilnehmer an ein und demselben Beitrag arbeiten, bevor dieser eingereicht wird</li>
			<li>Beitr&auml;ge vor dem Absenden validiert werden müssen</li>
		</ul>
	<p>Für weitere Informationen, Auskunft über anstehende neue Funktionen und m&ouml;gliche Behelfsl&ouml;sungen wenden Sie sich bitte an DIGIT-EUSURVEY-SUPPORT.</p>
	<h2><a class="anchor" name="_Toc0-4"></a>Welche Funktionen bietet EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p><b>Individuell anpassbare Frageb&ouml;gen</b><br />Im bedienfreundlichen Editor k&ouml;nnen Sie aus einer Reihe von Fragetypen w&auml;hlen - von einfachen Text- und Auswahlfragen bis hin zu Fragen im Tabellenformat oder Multimedia-Elementen. Strukturieren Sie Ihre Umfrage mit speziellen Gliederungselementen.</p>
	<p><b>Abh&auml;ngige Fragen</b><br />EUSurvey kann antwortabh&auml;ngig zus&auml;tzliche Fragen und Felder anzeigen. Dadurch wird die Umfrage interaktiv.</p>
	<p><b>Ver&ouml;ffentlichung nach Zeitplan</b><br />Ver&ouml;ffentlichen Sie Ihre Umfrage automatisch zu einem festgelegten Zeitpunkt bzw. heben Sie die Ver&ouml;ffentlichung zu einem bestimmten Datum auf.</p>
	<p><b>Änderungen am Fragebogen nach Ver&ouml;ffentlichung</b><br />Sie k&ouml;nnen eine ver&ouml;ffentlichte Umfrage ohne den Verlust bereits eingegangener Beitr&auml;ge &auml;ndern.</p>
	<p><b>Sprachen</b><br />Die Bedienoberfl&auml;che steht in 23 der offiziellen EU-Sprachen zur Verf&uuml;gung, und Sie k&ouml;nnen Ihren Fragebogen in eine beliebige der 136 Sprachen gem&auml;&szlig; der Norm ISO 639-1 übersetzen lassen, von Abchasisch bis Zulu.</p>
	<p><b>Sicherheit</b><br />EUSurvey verfügt über die für die Sicherung der Online-Frageb&ouml;gen erforderliche Infrastruktur.</p>
	<p><b>Versand von Einladungen unmittelbar aus der Anwendung</b><br />Im &bdquo;Adressbuch&ldquo; k&ouml;nnen ausgew&auml;hlte Kontakte verwaltet werden. Beispielsweise k&ouml;nnen Sie jedem einzelnen Kontakt eine eigene E-Mail mit individuellem Zugangslink schicken.</p>
	<p><b>Umfassender Datenschutz</b><br />Sie k&ouml;nnen den Schutz der pers&ouml;nlichen Daten Ihrer Teilnehmer garantieren, wenn Sie einen anonymen Fragebogen erstellen. In diesem Fall erhalten Sie als Autor der Umfrage keinerlei Verbindungsdaten.</p>
	<p><b>Erscheinungsbild individuell anpassen</b><br />Mit dem integrierten CSS-Style-Editor und den eingebetteten Rich-Text-Editoren für alle sichtbaren Elemente liegt die Gestaltung ganz in Ihrer Hand. Eine umfassende Auswahl an Themen macht es Ihnen leicht, den Fragebogen gestalterisch auf Ihr Projektkonzept abzustimmen. Sie k&ouml;nnen zwischen ein- und mehrseitigen Umfragen w&auml;hlen.</p>
	<p><b>Beitrag als Entwurf speichern</b><br />Sie k&ouml;nnen es einrichten, dass Teilnehmer ihren Beitrag als Entwurf auf dem Server speichern und sp&auml;ter mit der Bearbeitung fortfahren k&ouml;nnen.</p>
	<p><b>Beantwortung im Offline-Modus</b><br />Bei EUSurvey ist es m&ouml;glich, einen Fragebogen offline zu beantworten, bevor der Beitrag zu einem sp&auml;teren Zeitpunkt an den Server übermittelt wird.</p>
	<p><b>Automatische Durchnummerierung</b><br />Ihre Umfrage erh&auml;lt eine übersichtliche Struktur, wenn Sie die Elemente Ihres Fragebogens in EUSurvey automatisch durchnummerieren.</p>
	<p><b>Erh&ouml;hter Kontrast</b><br />Teilnehmer mit eingeschr&auml;nkter Sehf&auml;higkeit k&ouml;nnen sich die Umfrage in einer Version mit einem h&ouml;heren Kontrast anzeigen lassen. Diese Version wird für jede Umfrage automatisch erstellt.</p>
	<p><b>Hochladen von unterstützenden Dateien</b><br />Sie k&ouml;nnen Ihrer Umfrage Dateien hinzufügen, indem Sie sie mit hochladen. Diese Dateien kann jeder Teilnehmer an Ihrer Umfrage herunterladen.</p>
	
	<h3><a class="anchor" name="_Toc0-5"></a>Fragebogenverwaltung</h3>
	<c:if test="${enablepublicsurveys}">
	<p><b>Ver&ouml;ffentlichung einer Umfrage</b><br /></b>Sie k&ouml;nnen entscheiden, dass Ihre Umfrage automatisch in der <a href="https://ec.europa.eu${contextpath}/home/publicsurveys" target="_blank">Liste der &ouml;ffentlichen Umfragen</a> ver&ouml;ffentlicht wird, die in der EUSurvey-Anwendung der Europ&auml;ischen Kommission zur Verfügung steht. Dadurch erh&auml;lt sie noch mehr Reichweite.</p>
	</c:if>
	<p><b>Zusammenarbeit</b><br />Bei Umfragen, die von mehreren Nutzern verwaltet werden, k&ouml;nnen Sie in EUSurvey spezielle Berechtigungen  für andere Nutzer festlegen, die eine Umfrage testen oder die Ergebnisse analysieren sollen.</p>
	
	<h3><a class="anchor" name="_Toc0-6"></a>Ergebnisverwaltung</h3>
	<p><b>Analyse Ihrer Ergebnisse</b><br />EUSurvey bietet Grundfunktionen für die Ergebnisanalyse und die visuelle Darstellung von Daten in Histogrammen und Diagrammen an. Sie k&ouml;nnen Umfrageergebnisse auch in Standard-Tabellenformate exportieren, um sie anschlie&szlig;end in statistische Anwendungen zu importieren.</p>
	<p><b>Ver&ouml;ffentlichung Ihrer Ergebnisse</b><br />EUSurvey bietet die M&ouml;glichkeit, eine Teilmenge aller eingereichten Antworten auf den internen Seiten der Anwendung zu ver&ouml;ffentlichen. Das System kann automatisch Statistiken und Diagramme berechnen und erstellen.</p>
	<p><b>Bearbeitung bereits eingesandter Beitr&auml;ge</b><br />Sie k&ouml;nnen es einrichten, dass Ihre Teilnehmer ihre Beitr&auml;ge nach dem Absenden noch einmal &auml;ndern k&ouml;nnen.</p>
	
	<h2><a class="anchor" name="_Toc0-7"></a>Wo finde ich weitere Informationen über EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Praktische Informationen finden Sie unter &bdquo;<a href="https://ec.europa.eu${contextpath}/home/documentation" target="_blank">Dokumentation</a>&ldquo; im Banner der EUSurvey-Anwendung. Auf der Seite  &bdquo;<a href="https://ec.europa.eu${contextpath}/home/about" target="_blank">Mehr zu EUSurvey</a>&ldquo; erfahren Sie mehr über den Hintergrund der Anwendung und die Finanzierung.</p>
	<h2><a class="anchor" name="_Toc0-8"></a>Wohin kann ich mich bei technischen Problemen mit EUSurvey wenden?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Mitarbeiter der EU sollten ihr  Helpdesk kontaktieren, das Problem so genau wie m&ouml;glich beschreiben und darum bitten, dass das Problem an DIGIT-EUSURVEY-SUPPORT weitergeleitet wird.</p>
	<p>Externe Nutzer sollten sich an das  <a href="mailto:EC-CENTRAL-HELPDESK@ec.europa.eu?subject=Incident%20Creation%20Request%20for%20DIGIT%20EUSURVEY%20SUPPORT%20&amp;body=%20Dear%20Helpdesk,%0D%0DCould%20you%20please%20open%20a%20ticket%20to%20DIGIT%20EUSURVEY%20SUPPORT%20with%20the%20following%20description:" target="_blank">ZENTRALE HELPDESK</a> wenden.</p>
	<h2><a class="anchor" name="_Toc0-9"></a>Wohin wende ich mich, wenn ich Verbesserungsvorschl&auml;ge im Zusammenhang mit EUSurvey habe?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Kommentare und Rückmeldungen sind uns immer willkommen! Bitten Sie das Helpdesk bzw. das <a href="mailto:EC-CENTRAL-HELPDESK@ec.europa.eu?subject=Incident%20Creation%20Request%20for%20DIGIT%20EUSURVEY%20SUPPORT%20&amp;body=%20Dear%20Helpdesk,%0D%0DCould%20you%20please%20open%20a%20ticket%20to%20DIGIT%20EUSURVEY%20SUPPORT%20with%20the%20following%20description:" target="_blank">ZENTRALE HELPDESK</a> um die Weiterleitung Ihrer Anregungen an das Support-Team von EUSurvey. Das Support-Team wird sich so schnell wie m&ouml;glich bei Ihnen melden, um über relevante Anwendungsf&auml;lle zu sprechen und mit Ihnen zu überlegen, ob Ihre Idee Eingang in eine zukünftige Version der Anwendung finden kann.</p>
	<h2><a class="anchor" name="_Toc0-10"></a>Welche Browser unterstützt EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>EUSurvey unterst&uuml;tzt die letzten beiden Versionen des Internet Explorers, von Mozilla Firefox und Google Chrome.</p>
	<p>Die Verwendung anderer Browser kann Kompatibilit&auml;tsprobleme verursachen.</p>
	<h2><a class="anchor" name="_Toc0-11"></a>EUSurvey-Haftungsausschluss (nur f&uuml;r externe Nutzer)<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Bei allen Frageb&ouml;gen und Einladungen per E-Mail in Bezug auf Umfragen, die durch einen <b>nicht offiziell für die EU-Institutionen t&auml;tigen</b> Nutzer erstellt wurden, erscheint in der Umfrage und der E-Mail-Nachricht folgender Haftungsausschluss:</p>
	<p>Haftungsausschluss<br> 
	<i>Die Europ&auml;ische Kommission haftet nicht f&uuml;r den Inhalt der unter Nutzung des Dienstes EUSurvey verfassten Frageb&ouml;gen. Die Verantwortung daf&uuml;r liegt allein beim Ersteller des Formulars und Ausrichter der Umfrage. Die Nutzung des Dienstes EUSurvey impliziert keine Empfehlung oder Billigung der in den damit erstellten Umfragen zum Ausdruck gebrachten Ansichten durch die Europ&auml;ische Kommission.</i></p>
	<h2><a class="anchor" name="_Toc0-12"></a>K&ouml;nnen meine Teilnehmer mobile Endger&auml;te nutzen um meine Umfrage zu beantworten?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Ja, EUSurvey stellt ein sog. &bdquo;Responsive Webdesign&ldquo; f&uuml;r ver&ouml;ffentlichte Umfragen zur Verf&uuml;gung. Dies bedeutet, dass Inhalts- und Navigationselemente sowie auch der strukturelle Aufbau einer Webseite sich der Bildschirmaufl&ouml;sung des mobilen Endger&auml;tes anpassen. EUSurvey bietet dadurch Ihren Teilnehmern eine gleichbleibende Benutzerfreundlichkeit auf dem Computer-Desktop, Tablet und Smartphone.</p>
	
	<h2><a class="anchor" name="_Toc0-13"></a>Gibt es eine minimale Bildschirmgröße?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Die Online-Fragebögen sind vollständig  responsiv und passen sich der Größe Ihres Geräts an, so dass Sie den Fragebogen mit beliebiger Bildschirmgröße ausfüllen können.
	<br />
	Für die Erstellung und Verwaltung von Umfragen empfehlen wir eine Mindestauflösung von 1680x1050 Pixel.</p>
	
	<h1><a class="anchor" name="_Toc1"></a>Anmeldung und EU Login Registrierung</h1>
	<h2><a class="anchor" name="_Toc1-1"></a>Ich habe ein EU-Login-Konto. Muss ich mich separat bei EUSurvey registrieren?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>NEIN, eine separate Registrierung bei EUSurvey ist nicht erforderlich. Ein EU-Login-Konto ist ausreichend. Sie können auf EUSurvey zugreifen, indem Sie auf der <a href="https://ec.europa.eu/eusurvey/home/welcome" target="_blank">EUSurvey-Startseite</a> auf die Anmeldeschaltfläche klicken. Sie gelangen auf den Anmeldebildschirm (siehe unten für weitere Details).</p>
	<h2><a class="anchor" name="_Toc1-2"></a>Wie verbinde ich mich mit EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Bitte klicken Sie auf den Login-Button auf der <a href="https://ec.europa.eu/eusurvey/home/welcome" target="_blank">EUSurvey-Homepage</a>. Sie werden zum EUSurvey-Anmeldebildschirm weitergeleitet.</p>
	<p>Sobald Sie auf dem Anmeldebildschirm angekommen sind, müssen Sie die Option auswählen, die Ihrem persönlichen Fall entspricht:
		<ul>
			<li><b>Wenn Sie für eine EU-Institution arbeiten</b>, wählen Sie die zweite Option, um eine Verbindung zur EUSurvey-Anwendung herzustellen. Ihr EU-Login-Benutzername und Ihr Passwort reichen dann aus.</li>
			<li><b>Wenn Sie nicht für eine EU-Institution arbeiten</b>, wählen Sie die erste Option, um eine Verbindung zur EUSurvey-Anwendung herzustellen. Sie müssen Ihr Mobiltelefon zuvor registriert haben, um die Zwei-Faktor-Authentifizierung zu bestehen.</li>
		</ul>
	</p>
	<p>Wenn Sie noch kein EU-Login-Anmeldekonto haben, erstellen Sie bitte eines, indem Sie <a href="https://webgate.ec.europa.eu/cas/eim/external/register.cgi">hier</a> klicken.</p>
	<p>Wenn Sie nicht für eine EU-Institution arbeiten, registrieren Sie bitte auch Ihr Mobiltelefon, indem Sie <a href="https://ecas.ec.europa.eu/cas/userdata/mobileApp/manageMyMobileDevices.cgi">hier</a> klicken.</p>
	
	<h1><a class="anchor" name="_Toc2"></a>Umfrage erstellen</h1>
	<h2><a class="anchor" name="_Toc2-1"></a>Wie erstelle ich eine neue Umfrage?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Klicken Sie auf der Seite &bdquo;Begrü&szlig;ung&ldquo; oder der Seite &bdquo;Umfragen&ldquo; auf &bdquo;Neue Umfrage erstellen - JETZT!&ldquo;. Es &ouml;ffnet sich ein Dialogfenster. Wenn Sie alle Pflichtangaben gemacht haben, klicken Sie auf &bdquo;Erstellen/Einrichten/Erzeugen&ldquo;. Die Anwendung wird Ihre neue Umfrage in das System laden und automatisch den &bdquo;Editor&ldquo; &ouml;ffnen. So k&ouml;nnen Sie direkt damit beginnen, Ihrer Umfrage Elemente hinzuzufügen.</p>
	<h2><a class="anchor" name="_Toc2-2"></a>Wie importiere ich eine bereits vorhandene Umfrage aus meinem Rechner?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p> Klicken Sie auf der Seite &bdquo;Begrü&szlig;ung&ldquo; oder der Seite &bdquo;Umfragen&ldquo; auf &bdquo;Umfrage importieren&ldquo;. Es &ouml;ffnet sich ein Dialogfenster. W&auml;hlen Sie eine Umfrage-Datei auf Ihrem Computer aus und klicken Sie dann auf &bdquo;Importieren&ldquo;. Dann wird Ihre Umfrage in EUSurvey importiert. Hinweis: Umfragen k&ouml;nnen nur als Zip-Datei oder mit der Dateierweiterung .eus importiert werden.</p>
	<h2><a class="anchor" name="_Toc2-3"></a>Wie importiere ich eine bereits vorhandene Umfrage aus IPM?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Exportieren Sie zun&auml;chst Ihre Umfrage aus IPM. Melden Sie sich bei IPM an und &ouml;ffnen Sie Ihren Fragebogen. Klicken Sie links auf der Seite auf &bdquo;Exportieren&ldquo;. Dann wird der Fragebogen auf Ihrem Computer als Zip-Datei gespeichert. </p>
	<p>Melden Sie sich bei EUSurvey an. Klicken Sie auf der Seite &bdquo;Begrü&szlig;ung&ldquo; auf &bdquo;Umfrage importieren&ldquo;. W&auml;hlen Sie die Umfrage aus, die Sie importieren m&ouml;chten (die gesamte Zip-Datei, die sich in der Regel im Ordner &bdquo;Downloads&ldquo; befindet, wenn Sie sie zuvor aus IPM heruntergeladen haben). War der Import erfolgreich, k&ouml;nnen Sie die Umfrage in EUSurvey &ouml;ffnen und verwenden.</p>
	<h2><a class="anchor" name="_Toc2-4"></a>Wo finde ich die von mir erstellten Umfragen?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Gehen Sie zur Seite &bdquo;Umfragen&ldquo;. Dort finden Sie eine Liste. Sie k&ouml;nnen mittels Schlagw&ouml;rtern nach Umfragen suchen oder Sie suchen, filtern und sortieren nach anderen Kriterien wie Erstellungsdatum, Sprache, Status usw. Vergessen Sie nicht, auf &bdquo;Suchen&ldquo; zu klicken, um die Suchkriterien anzuwenden.</p>
	<h2><a class="anchor" name="_Toc2-5"></a>Wie &ouml;ffne ich eine vorhandene Umfrage zur Bearbeitung usw.?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Klicken Sie auf der Seite &bdquo;Umfragen&ldquo; auf das Symbol &bdquo;&ouml;ffnen&ldquo; der gewünschten Umfrage. Dann &ouml;ffnet sich die Seite &bdquo;Übersicht&ldquo; mit weiteren Registerkarten. Von hier k&ouml;nnen Sie den &bdquo;Editor&ldquo; &ouml;ffnen, Ihre Umfrage testen oder auf die &bdquo;Ergebnisse&ldquo;, &bdquo;Übersetzungen&ldquo;, &bdquo;Eigenschaften&ldquo; usw. der Umfrage zugreifen.</p>
	<h2><a class="anchor" name="_Toc2-6"></a>Wie exportiere ich eine vorhandene Umfrage?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Suchen Sie auf der Seite &bdquo;Umfragen&ldquo; nach der Umfrage, die Sie exportieren m&ouml;chten. Sie k&ouml;nnen entweder:</p>
	<p>auf das Symbol &bdquo;Exportieren&ldquo; klicken ODER</p>
	<p>auf das Symbol &bdquo;&ouml;ffnen&ldquo; klicken und auf der Seite &bdquo;Übersicht&ldquo; auf das Symbol &bdquo;Exportieren&ldquo; klicken.</p>
	<p>Ihre Umfrage wird dann mit allen Einstellungen auf Ihrem Computer gespeichert. Die Dateierweiterung einer Datei im EUSurvey-Format lautet &bdquo;.eus&ldquo;.</p>
	<h2><a class="anchor" name="_Toc2-7"></a>Wie kopiere ich eine vorhandene Umfrage?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>&ouml;ffnen Sie auf der Seite &bdquo;Umfragen&ldquo; die gewünschte Umfrage und klicken Sie auf das Symbol &bdquo;Kopieren&ldquo;. In dem Dialogfenster, das sich &ouml;ffnet, k&ouml;nnen Sie die erforderlichen Einstellungen vornehmen. Klicken Sie dann auf &bdquo;Erstellen/Einrichten/Erzeugen&ldquo;. Ihre Umfrage wird der Liste auf der Seite &bdquo;Umfragen&ldquo; hinzugefügt. Sie k&ouml;nnen direkt mit der Bearbeitung beginnen.</p>
	<h2><a class="anchor" name="_Toc2-8"></a>Wie entferne ich eine vorhandene Umfrage?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>&ouml;ffnen Sie auf der Seite &bdquo;Umfragen&ldquo; die gewünschte Umfrage und klicken Sie auf das Symbol &bdquo;L&ouml;schen&ldquo;. Nachdem Sie diesen Vorgang best&auml;tigt haben, wird Ihre Umfrage aus der Liste der Umfragen entfernt. Achtung: Wenn Sie eine Umfrage l&ouml;schen, werden alle Fragen und Ergebnisse im Zusammenhang mit dieser Umfrage aus dem EUSurvey-System gel&ouml;scht! Dieser Vorgang l&auml;sst sich nicht rückg&auml;ngig machen!</p>
	<h2><a class="anchor" name="_Toc2-9"></a>Wie erstelle ich mit EUSurvey einen Fragebogen gem&auml;ß der Richtlinien für barrierefreie Webinhalte (WCAG)?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Die Richtlinien für barrierefreie Webinhalte müssen beachtet werden, um Inhalte insbesondere für Menschen mit Behinderungen, aber auch über Ger&auml;te wie Mobiltelefone (durch entsprechende Software) zug&auml;nglich zu machen.</p>
	<p>Möchten Sie Ihre Umfrage gem&auml;ß dieser Richtlinien gestalten, folgen Sie bitte den Anweisungen <a href="https://circabc.europa.eu/d/a/workspace/SpacesStore/78b03213-5cf4-4aab-8e90-ada7e2eb1101/WCAG_tutorial%20.pdf" target="_blank">in diesem Dokument</a>.</p>
	<h2><a class="anchor" name="_Toc2-10"></a>Wie erstelle ich einen Quiz-Fragebogen?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Bei Erstellung eines neuen Fragebogens in EUSurvey k&ouml;nnen Sie zwischen einem normalen und einem Quiz-Fragebogen w&auml;hlen.</p>
	<p>Ein Quiz ist eine spezielle Art von Umfrage, die die Berechnung eines Endergebnisses f&uuml;r jeden Teilnehmer erm&ouml;glicht. Solche Umfragen k&ouml;nnen z.B. als F&auml;higkeitstests oder elektronische Pr&uuml;fungen genutzt werden. Im <a href="https://circabc.europa.eu/sd/a/400e1268-1329-413b-b873-b42e41369a07/EUSurvey_Quiz_Guide.pdf" target="_blank">EUSurvey Quiz-Leitfaden</a> finden Sie detaillierte Informationen &uuml;ber die Erstellung eines Quiz-Fragebogens.</p>
	<p>Der Quiz-Modus beinhaltet unter anderem</p>
		<ul>
			<li>Eine Bewertungsfunktion</li>
			<li>Die &Uuml;berpr&uuml;fung der Antworten der Teilnehmer</li>
			<li>Die M&ouml;glichkeit, Ihren Teilnehmern Feedback zu geben, abh&auml;ngig von deren Antworten </li>
			<li>Zus&auml;tzliche Ergebnisanalyse, die speziell f&uuml;r Quizfragen entwickelt wurde</li>
		</ul>
	
	<h1><a class="anchor" name="_Toc3"></a>Umfrage bearbeiten</h1>
	<h2><a class="anchor" name="_Toc3-1"></a>Wie starte ich den Editor?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Vergewissern Sie sich zun&auml;chst, dass Sie eine vorhandene Umfrage ge&ouml;ffnet haben: Gehen Sie auf die Seite &bdquo;Umfragen&ldquo; und klicken Sie auf das Symbol &bdquo;&Ouml;ffnen&ldquo; der Umfrage, die Sie bearbeiten m&ouml;chten. Klicken Sie auf der Seite &bdquo;&Uuml;bersicht&ldquo; auf &bdquo;Editor&ldquo; und beginnen Sie mit der Erstellung Ihres Fragebogens.</p>
	<p>Denken Sie daran, Ihre Arbeit in regelm&auml;ßigen Abst&auml;nden zu speichern.</p>
	<h2><a class="anchor" name="_Toc3-2"></a>Wie erstelle ich einen Fragebogen mit dem EUSurvey Editor?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Der Editor dient dem Erstellen von Fragebogen. Mit ihm k&ouml;nnen Sie Fragen und weitere Elemente in Ihre Umfrage aufnehmen.</p>
	<p>Im <a href="https://ec.europa.eu/eusurvey/resources/documents/Editor_Guide_DE.pdf" target="_blank">EUSurvey Editor-Leitfaden</a> finden Sie detaillierte Informationen &uuml;ber die Benutzung des EUSurvey Editors.</p>
	<p>Der Editor umfasst f&uuml;nf Hauptbereiche:</p>
	<p><b>Navigation:</b><br>Dieser Bereich bietet eine strukturierte Ansicht des Fragebogens. Alle Elemente sind durch ihr entsprechendes Textlabel im Fragebogen dargestellt. Wenn Sie ein Element im Bereich Navigation ausw&auml;hlen, erscheint das ausgew&auml;hlte Element blau markiert im Bereich Formular.</p>
	<p><b>Werkzeugkasten:</b><br>Dieser Bereich enth&auml;lt die verschiedenen Arten von Elementen, die Sie Ihrem Fragebogen hinzuf&uuml;gen k&ouml;nnen. Weitere Elemente k&ouml;nnen Sie mit der Drag-und-Drop-Funktion oder durch Doppelklick hinzuf&uuml;gen.</p>
	<p><b>Formular:</b><br>In diesem Bereich sehen Sie eine Vorschau des Fragebogens; hier k&ouml;nnen Sie Elemente hinzuf&uuml;gen und zur Bearbeitung ausw&auml;hlen.</p>
	<p><b>Elementeigenschaften:</b><br>Hier werden die Einstellungen f&uuml;r ausgew&auml;hlte Elemente angezeigt. Sie k&ouml;nnen die Elemente hier bearbeiten, also z. B. den Fragetext &auml;ndern, Hinweise f&uuml;r die Nutzer hinzuf&uuml;gen und Einstellungen nach Bedarf &auml;ndern.</p>
	<p><b>Funktionsleiste:</b><br>Hier finden Sie alle verf&uuml;gbaren grundlegenden Funktionen f&uuml;r die Erstellung des Fragebogens.</p>
	<h2><a class="anchor" name="_Toc3-3"></a>Wie f&uuml;ge ich Fragen hinzu oder entferne sie?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Um Ihrem Fragebogen neue Elemente hinzuzuf&uuml;gen oder vorhandene zu entfernen, &ouml;ffnen Sie zun&auml;chst den Editor. </p>
	<p>Im Editor finden Sie links den Werkzeugkasten mit den verf&uuml;gbaren Elementen und in der Mitte das Formular, in dem die Elemente hinzugef&uuml;gt werden. Die Elemente enthalten Standardtexte, ihre Namen werden als Fragetext angezeigt. Um ein neues Element (Frage, Textfeld, Bild usw.) hinzuzuf&uuml;gen, w&auml;hlen Sie ein Element aus dem Werkzeugkasten aus. Sie k&ouml;nnen die Elemente mit der Drag-und-Drop-Funktion oder durch Doppelklick hinzuf&uuml;gen.</p>
	<p>Um ein Element aus dem Fragebogen zu entfernen, w&auml;hlen Sie das Element durch Anklicken aus. Klicken Sie auf das Symbol &bdquo;L&ouml;schen&ldquo;. Sobald Sie den Vorgang best&auml;tigt haben, wird das Element aus der Umfrage entfernt.</p>
	<p>Siehe auch &bdquo;<a href="#_Toc3-2">Wie erstelle ich einen Fragebogen mit dem EUSurvey Editor?</a>&ldquo;</p>
	<h2><a class="anchor" name="_Toc3-4"></a>Wie bearbeite ich einzelne Elemente in meiner Umfrage?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Die Elemente in Ihrem Fragebogen werden <b>im Formular zum Bearbeiten ausgew&auml;hlt</b> und dann <b>in den Elementeigenchaften bearbeitet</b>. Siehe auch &bdquo;<a href="#_Toc3-2">Wie erstelle ich einen Fragebogen mit dem EUSurvey Editor?</a>&ldquo;</p>
	<p>Klicken Sie auf ein Element, um es zu bearbeiten. Ausgew&auml;hlte Elemente erscheinen in Blau, wobei die jeweils verf&uuml;gbaren Optionen im Bereich Elementeigenschaften sichtbar sind. Sie k&ouml;nnen die Elemente hier bearbeiten, also z. B. den Fragetext &auml;ndern, Hinweise f&uuml;r die Nutzer hinzuf&uuml;gen und Einstellungen nach Bedarf &auml;ndern.</p>
	<p>Textbearbeitung im Rich-Text-Editor:</p>
		<ol>
			<li>Text oder Stift-Symbol anklicken.</li>
			<li>&Auml;nderungen am Text vornehmen.</li>
			<li>&bdquo;Anwenden&ldquo; anklicken, um die &Auml;nderungen im Bereich Formular zu sehen.</li>
		</ol>
	<p>Standardm&auml;ßig zeigt dieser Bereich alle grundlegenden Optionen. Zur Anzeige weiterer Optionen klicken Sie auf &bdquo;Erweitert&ldquo;.</p>
	<p>Bei Matrix- und Textfragen k&ouml;nnen Sie auch die einzelnen Fragen/Antworten/Zeilen/Spalten des Elements durch Anklicken des entsprechenden Labeltexts ausw&auml;hlen. So k&ouml;nnen Sie z. B. einzelne Fragen eines Matrix- oder Tabellenelements ausw&auml;hlen und zu Pflichtfragen machen.</p>
	<h2><a class="anchor" name="_Toc3-10"></a>Wie kann ich Elemente kopieren?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Um Elemente zu kopieren, &ouml;ffnen Sie zun&auml;chst den Editor.</p>
	<p>Alle kopierten oder ausgeschnittenen Elemente werden im oberen Werkzeugkastenbereich durch einen Platzhalter dargestellt. Mit der Drag-und-Drop-Funktion k&ouml;nnen Sie diese wieder dem Fragebogen hinzuf&uuml;gen. Mit der Schaltfl&auml;che &bdquo;Abbrechen&ldquo; daneben k&ouml;nnen Sie diese Aktion auch abbrechen.</p>
		<ol>
			<li>Element(e) ausw&auml;hlen.</li>
			<li>&bdquo;Kopieren&ldquo; anklicken.</li>
			<li>Platzhalter wie oben beschrieben vom Bereich Werkzeugkasten in den Bereich Formular verschieben oder Element im Bereich Formular ausw&auml;hlen und Symbol &bdquo;Einf&uuml;gen nach&ldquo; anklicken.</li>
		</ol>
	<p>Siehe auch &bdquo;<a href="#_Toc3-2">Wie erstelle ich einen Fragebogen mit dem EUSurvey Editor?</a>&ldquo;</p>
	<h2><a class="anchor" name="_Toc3-11"></a>Wie f&uuml;ge ich meinen Fragen m&ouml;gliche Antworten hinzu, oder wie entferne ich Antworten?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Durch Anklicken des Plus- oder Minuszeichens im Bereich Elementeigenschaften k&ouml;nnen Sie Antwortoptionen hinzuf&uuml;gen oder entfernen. Durch Anklicken des Stift-Symbols neben &bdquo;M&ouml;gliche Antworten&ldquo; k&ouml;nnen Sie den Text von Antwortoptionen &auml;ndern. Die Bearbeitung erfolgt im Rich-Text-Editor.</p>
	<p>Siehe auch &bdquo;<a href="#_Toc3-2">Wie erstelle ich einen Fragebogen mit dem EUSurvey Editor?</a>&ldquo;</p>
	<h2><a class="anchor" name="_Toc3-12"></a>Was kann ich tun, damit eine Frage zu einer Pflichtfrage wird?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>W&auml;hlen Sie ein Element, das Pflichtfrage werden soll, und markieren Sie das Kontrollk&auml;stchen &bdquo;Obligatorisch&ldquo; im Bereich Elementeigenschaften.</p>
	<p>Pflichtfragen erhalten links vom Fragetext ein rotes Sternchen.</p>
	<h2><a class="anchor" name="_Toc3-13"></a>Wie kann ich Elemente innerhalb des Fragebogens verschieben?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Sie k&ouml;nnen die Position eines Elements in Ihrem Fragebogen wie folgt verschieben:</p>
	<p>Drag-und-Drop-Funktion:<br>W&auml;hlen Sie das Element im Bereich Formular und ziehen Sie es an die gew&uuml;nschte Position im Fragebogen.</p>
	<p>Schaltfl&auml;chen:<br>W&auml;hlen Sie das zu verschiebende Element und klicken Sie auf den entsprechenden Pfeil, um es nach oben oder unten zu verschieben.</p>
	<p>Ausschneiden und Einf&uuml;gen:<br>Schneiden Sie das zu verschiebende Element aus und bringen Sie es mit der Drag-und-Drop-Funktion in die gew&uuml;nschte Position.</p>
	<h2><a class="anchor" name="_Toc3-14"></a>Wie erreiche ich, dass gewisse Fragen nur angezeigt werden, wenn eine bestimmte Antwort gegeben wurde (&bdquo;abh&auml;ngige Elemente&ldquo;)?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Mit der Sichtbarkeits-Funktion k&ouml;nnen Sie Elemente in Abh&auml;ngigkeit von den gegebenen Antworten auf Einfachauswahl-/Mehrfachauswahl- und Matrixfragen anzeigen und ausblenden. (siehe auch &bdquo;<a href="#_Toc3-2">Wie erstelle ich einen Fragebogen mit dem EUSurvey Editor?)</a>&ldquo;</p>
	<p>Standardm&auml;ßig ist f&uuml;r alle Elemente st&auml;ndige Sichtbarkeit voreingestellt, Fragen und Antworten sind also stets f&uuml;r alle Teilnehmer/-innen sichtbar.</p>
	<p>Um ein solches Element anzuzeigen, gehen Sie wie folgt vor:</p>
		<ol>
			<li>Einfachauswahl-/Mehrfachauswahl- oder Matrixfrage hinzuf&uuml;gen.</li>
			<li>Weitere Elemente hinzuf&uuml;gen.</li>
			<li>Ein auf eine Einfachauswahl-/Mehrfachauswahl- oder Matrixfrage folgendes Element ausw&auml;hlen, das nur bei einer bestimmten Antwort angezeigt werden soll.</li>
			<li>Stift-Symbol anklicken und Sichtbarkeitseinstellungen vornehmen. Alle &uuml;ber dem/den ausgew&auml;hlten Element/-en vorhandenen Einfachauswahl-, Mehrfachauswahl- und Matrixfragen, werden mit dem Fragetext und den m&ouml;glichen Antworten angezeigt.</li>
			<li>Die Antwort ausw&auml;hlen, die - falls sie angekreuzt wird - zur Anzeige des ausgew&auml;hlten Elements f&uuml;hrt.</li>
			<li>Sichtbarkeitseinstellung durch Klick auf &bdquo;Anwenden&ldquo; best&auml;tigen.</li>
		</ol>
	<p>Bei Auswahl mehrerer Elemente k&ouml;nnen Sie die Sichtbarkeitseinstellungen f&uuml;r alle gleichzeitig vornehmen.</p>
	<p><b>Hinweis: </b>Die vorgenommenen Einstellungen werden nur im Fragebogen auf der Testseite und in der Ver&ouml;ffentlichung wirksam. Im Editor bleiben alle Elemente weiterhin sichtbar.</p>
	<p>Bei Aktivierung erscheinen neben den verbundenen Elementen Pfeile, um die Sichtbarkeitseinstellungen im Bereich Formular anzuzeigen. Antworten, die zur Anzeige eines Elements f&uuml;hren, werden mit einem nach unten zeigenden Pfeil markiert. Elemente, die infolge einer Antwort erscheinen, werden mit einem nach oben zeigenden Pfeil markiert.</p>
	<p>Wenn Sie den Mauszeiger &uuml;ber die Pfeile oder Bezeichnungen im Bereich Elementeigenschaften bewegen, werden verbundene Elemente in den Bereichen Formular und Navigation hervorgehoben.</p>
	<p>Elemente mit Sichtbarkeitseinstellungen, die bearbeitet wurden, bleiben beim Ausf&uuml;llen des Fragebogens verborgen, bis mindestens eine der konfigurierten Antworten ausgew&auml;hlt wurde.</p>
	<h2><a class="anchor" name="_Toc3-7"></a>Kann ich die Antworten auf eine Frage mit mehreren Antworten, aus denen nur eine oder mehrere ausgew&auml;hlt werden k&ouml;nnen, sortiert erscheinen lassen?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Wenn Sie eine solche Frage erstellen, k&ouml;nnen Sie die Antworten auf drei verschiedene Arten sortieren lassen:</p>
		<ul>
			<li>Ursprüngliche Reihenfolge</li>
			<li>Alphabetische Reihenfolge</li>
			<li>Zuf&auml;llige Reihenfolge </li>
		</ul>
	<p>Ursprüngliche Reihenfolge:  Die Antworten werden in der ursprünglichen Reihenfolge angezeigt. </p>
	<p>Alphabetische Reihenfolge:   Die Antworten werden alphabetisch sortiert angezeigt.</p>
	<p>Zufällige Reihenfolge:   Das System verteilt die Antworten nach dem Zufallsprinzip.</p>
	<h2><a class="anchor" name="_Toc3-5"></a>Wie erlaube ich anderen Nutzern, meine Umfrage zu bearbeiten?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>&ouml;ffnen Sie Ihre Umfrage und &ouml;ffnen Sie die Seite &bdquo;Vorrechte&ldquo;. Klicken Sie auf &bdquo;Neuen Nutzer hinzufügen&ldquo; oder &bdquo;Abteilung hinzufügen&ldquo;. Es erscheint ein Fenster mit einem Assistenten, der Sie Schritt für Schritt durch den Prozess zum Hinzufügen von Nutzern führt. Als N&auml;chstes k&ouml;nnen Sie ihnen individuelle Zugangsrechte zuweisen. Klicken Sie einfach auf die Farbe, um die Rechte zu &auml;ndern.</p>
		<ul>
			<li>Grün: 	Lese- und Schreibzugriff</li>
			<li>Gelb: 	Lesezugriff</li>
			<li>Rot: 	kein Zugriff</li>
		</ul>
	<p>Hinzugefügte Nutzer sehen Ihre Umfrage automatisch auf der Umfragenliste, wenn Sie sich das n&auml;chste Mal bei EUSurvey anmelden. Weitere Informationen erhalten Sie unter &bdquo;<a href="#_Toc8-8">Wie erhalten andere Nutzer Zugang zu meiner Umfrage?</a>&ldquo;.</p>
	<h2><a class="anchor" name="_Toc3-8"></a>Welche Sprachen werden von der Anwendung unterstützt?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Sprachen, die in &bdquo;UTF-8 in 3 Byte&ldquo; kodiert werden können, eignen sich zur Erstellung einer Umfrage.</p>
	<h2><a class="anchor" name="_Toc3-9"></a>Warum UTF-8? Welche Zeichensätze sollte ich  verwenden?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Bedenken Sie, dass die Umfrageteilnehmer aus Ihrer Zielgruppe die Umfrage einfacher anzeigen lassen können, wenn der von Ihnen verwendete Zeichensatz bereits in ihrem Browser installiert ist. UTF-8 ist die am häufigsten verwendete Kodierung für HTML-Seiten.</p>  
	<p>Andererseits kann ein nicht unterstützter Zeichensatz die Wiedergabe einer PDF-Exportdatei beeinträchtigen.</p>
	<p>Wir empfehlen die Verwendung der nachstehend aufgeführten unterstützten Zeichensätze:</p> 
		<ul>
			<li>Freesans <a href="https://circabc.europa.eu/d/a/workspace/SpacesStore/602784e1-bb06-4b0d-a474-eae77dbe2d11/EUSurvey-SupportedCharacterSet(freesans).txt" target="_blank">(https://circabc.europa.eu/d/a/workspace/SpacesStore/602784e1-bb06-4b0d-a474-eae77dbe2d11/EUSurvey-SupportedCharacterSet(freesans).txt)</a></li>
			<li>Freemono <a href="https://circabc.europa.eu/d/a/workspace/SpacesStore/dfc640e9-56ac-4d25-8361-4b07dbbd0579/EUSurvey-SupportedCharacterSet(freemono).txt" target="_blank">(https://circabc.europa.eu/d/a/workspace/SpacesStore/dfc640e9-56ac-4d25-8361-4b07dbbd0579/EUSurvey-SupportedCharacterSet(freemono).txt)</a></li>
			<li>Freeserif <a href="https://circabc.europa.eu/d/a/workspace/SpacesStore/5b98b11a-f306-4d97-aab3-ec1c7a24965f/EUSurvey-SupportedCharacterSet(freeserif).txt" target="_blank">(https://circabc.europa.eu/d/a/workspace/SpacesStore/5b98b11a-f306-4d97-aab3-ec1c7a24965f/EUSurvey-SupportedCharacterSet(freeserif).txt)</a></li>
			<li>Allgemein unterstützter Zeichensatz <a href="https://circabc.europa.eu/d/a/workspace/SpacesStore/621396c0-92d3-49a3-acd0-546b0c1a170b/EUSurvey-SupportedCharacterSet(common).txt" target="_blank">(https://circabc.europa.eu/d/a/workspace/SpacesStore/621396c0-92d3-49a3-acd0-546b0c1a170b/EUSurvey-SupportedCharacterSet(common).txt)</a></li>
		</ul>
	<p><b>&bdquo;Freesans&ldquo;</b> ist der vorinstallierte Zeichensatz</p>
	<p>Im Zweifelsfalle sollten Sie die abgeschlossene Umfrage als PDF-Datei exportieren, um zu überprüfen, ob sie so korrekt wiedergegeben wird. Beachten Sie dabei, dass einige Beiträge in PDF nicht korrekt wiedergegeben werden könnten. Die Umfrageteilnehmer können jeden Zeichensatz auswählen, der von der Anwendung unterstützt wird. Auch wenn die Anwendung die von ihnen gewählten Zeichen nicht wiedergeben kann, werden sie in der Datenbank von EUSurveys korrekt gespeichert. Sie können somit von der Ergebnisseite aus exportiert werden.</p>
	<h2><a class="anchor" name="_Toc3-6"></a>Was bedeutet &bdquo;Komplexit&auml;t&ldquo;?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Wenn Sie zu viele Elemente oder Abh&auml;ngigkeiten in Ihre Umfrage einbauen, kann dies zu langen Ladezeiten für die Teilnehmer Ihrer Umfrage führen, da diese zu komplex ist.</p>
	<p>Dass Ihr Fragebogen eine hohe Komplexit&auml;t besitzt, kann mehrere Gründe haben:</p>
	<ul>
		<li>Sie nutzen zu viele Tabellen/Matrix Elemente</li>
		<li>Sie nutzen zu viele Abh&auml;ngigkeiten</li>
		<li>Sie nutzen zu viele kaskadierende Abh&auml;ngigkeiten</li>
	</ul>
	<p>Für mehr Information, schauen Sie sich unsere <a href="https://circabc.europa.eu/sd/d/281e626e-279e-45df-8581-1f6e04feff51/BestPractices-EUSurvey.pdf" target="_blank">best practices</a> an.</p>
	
	<h1><a class="anchor" name="_Toc4"></a>Sicherheit der Umfrage</h1>
	<h2><a class="anchor" name="_Toc4-1"></a>Wie schr&auml;nke ich den Zugriff auf meine Umfrage ein?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Standardm&auml;&szlig;ig ist ein EUSurvey-Online-Fragebogen direkt nach Ver&ouml;ffentlichung &ouml;ffentlich verf&uuml;gbar. Das k&ouml;nnen Sie &auml;ndern, sodass nur noch bevorrechtigte Nutzer Zugang zur Umfrage haben. Um diesen Nutzern den Zugang zu gew&auml;hren, gehen Sie in &bdquo;Eigenschaften&ldquo; zu den &bdquo;Sicherheitseinstellungen&ldquo; und legen f&uuml;r die Umfrage den Status <b>&bdquo;Gesch&uuml;tzt&ldquo;</b> fest. Sie k&ouml;nnen dann den Teilnehmern Zugang gew&auml;hren indem Sie entweder
		<ul>
			<li>einzelne Teilnehmer &uuml;ber EUSurvey einladen (siehe <a href="#_Toc12">Teilnehmer einladen</a>). Jeder Teilnehmer erh&auml;lt eine eigene E-Mail mit individuellem Zugangslink, ODER</li>
			<li>Ihre Umfrage &uuml;ber EU Login sch&uuml;tzen. Bearbeiten Sie unter &bdquo;Eigenschaften&ldquo; die &bdquo;Sicherheitseinstellungen&ldquo; und w&auml;hlen Sie in &bdquo;Sicherheit&ldquo; den Punkt &bdquo;EU Login ausw&auml;hlen&ldquo;. Wenn Sie Bediensteter einer der EU-Institutionen sind, k&ouml;nnen Sie entweder allen Nutzern mit einem EU Login-Konto Zugang zu Ihrer Umfrage gew&auml;hren (Nutzer der EU-Institutionen und Nutzer mit externen EU Login-Konten), oder Sie geben nur den Nutzern der EU-Institutionen Zugang, ODER</li>
			<li>ein allgemeines Passwort einrichten. Es ist dann ein und dasselbe Passwort f&uuml;r alle Teilnehmer, und Sie teilen es Ihrer Teilnehmergruppe mit. Sie k&ouml;nnen Ihnen eine E-Mail zusenden, die den Zugangslink der Umfrage und das Passwort enth&auml;lt. Siehe <a href="#_Toc4-3">Wie lege ich ein Passwort f&uuml;r meine Umfrage fest?</a>.</li>
		</ul>
	<h2><a class="anchor" name="_Toc4-3"></a>Wie lege ich ein Passwort für meine Umfrage fest?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Um Ihre Umfrage durch ein Passwort zu schützen, bearbeiten Sie unter &bdquo;Eigenschaften&ldquo; die &bdquo;Sicherheitseinstellungen&ldquo;. Um einzelne Kontakte dazu einzuladen, auf Ihre geschützte Umfrage zuzugreifen, lesen Sie den Abschnitt &bdquo;<a href="#_Toc12">Teilnehmer einladen</a>&ldquo;.</p>
	<h2><a class="anchor" name="_Toc4-4">Wie sorge ich dafür, dass ein Nutzer nicht mehr als eine festgelegte Anzahl Beitr&auml;ge zu meiner Umfrage einsendet?</a><a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Sobald Sie individuelle Zugangslinks an Ihre Teilnehmer verschicken, kann das System diese individuell zuordnen.</p>
	<h2><a class="anchor" name="_Toc4-5"></a>Wie verhindere ich, dass Bots mehrfach Beitr&auml;ge zu meiner Umfrage übermitteln?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Automatische Scripts k&ouml;nnen das Ergebnis einer Online-Umfrage durch das Absenden einer hohen Anzahl Beitr&auml;ge verf&auml;lschen. Um das zu verhindern, fordern Sie Ihre Teilnehmer auf, eine Sicherheitsfrage (<a href="http://fr.wikipedia.org/wiki/CAPTCHA" target="_blank">CAPTCHA</a>) zu beantworten, bevor Sie einen Beitrag absenden.</p>
	<p>Diese Frage kann unter &bdquo;Eigenschaften&ldquo; in den &bdquo;Sicherheitseinstellungen&ldquo; aktiviert oder deaktiviert werden.</p>
	<p>Hinweis: Auch wenn sich ein Betrug so nicht vollst&auml;ndig ausschlie&szlig;en l&auml;sst, k&ouml;nnen Versuche einer Verf&auml;lschung von Umfrageergebnissen auf diese Weise erschwert werden.</p>
	<h2><a class="anchor" name="_Toc4-6"></a>Kann ich meinen Teilnehmern erlauben, nach dem Einsenden ihrer Beitr&auml;ge auf diese zuzugreifen?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Ja! Gehen Sie unter &bdquo;Eigenschaften&ldquo; zu &bdquo;Sicherheitseinstellungen&ldquo;. Die Teilnehmer müssen die Fall-ID kennen, die ihnen nach Einreichung ihres Beitrags angezeigt wurde. Um Beitr&auml;ge nach der Einsendung zu &auml;ndern, müssen die Teilnehmer auf die Startseite von EUSurvey gehen: <a href="https://ec.europa.eu/eusurvey" target="_blank">https://ec.europa.eu/eusurvey</a>. Unterhalb der Schaltfläche &bdquo;Jetzt registrieren&ldquo; befindet sich ein Link <a href="${contextpath}/home/editcontribution" target="_blank">zur Zugangsseite für individuelle Beiträge</a>. Auf dieser Seite geben die Teilnehmer ihre jeweilige Fall-ID an. Dann &ouml;ffnet das System ihren Beitrag. So k&ouml;nnen sie ihren Beitrag auch nach der Einsendung noch bearbeiten.</p>
	
	<h1><a class="anchor" name="_Toc5"></a>Umfrage testen</h1>
	<h2><a class="anchor" name="_Toc5-1"></a>Kann ich mir ansehen, wie sich meine Umfrage nach der Ver&ouml;ffentlichung verhalten wird?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Ja. &ouml;ffnen Sie dazu die Umfrage in EUSurvey und klicken Sie auf &bdquo;Prüfung&ldquo;. Dann wird der aktuelle Entwurf Ihrer Umfrage angezeigt und Sie k&ouml;nnen auf jedes Element des ver&ouml;ffentlichten Fragebogens zugreifen. Sie k&ouml;nnen den Test als Entwurf speichern oder direkt als Ihren Beitrag einreichen.</p>
	<h2><a class="anchor" name="_Toc5-2"></a>Wie k&ouml;nnen meine Kollegen meine Umfrage vor der Ver&ouml;ffentlichung testen?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Auch Ihre Kollegen k&ouml;nnen die Seite &bdquo;Prüfung&ldquo; Ihrer Umfrage testen. Den Zugang zu dieser Seite richten Sie ein, indem Sie Ihre Umfrage in EUSurvey &ouml;ffnen, die Registerkarte &bdquo;Vorrechte&ldquo; w&auml;hlen und auf &bdquo;Neuen Nutzer hinzufügen&ldquo; oder &bdquo;Abteilung hinzufügen&ldquo; klicken. Ein Assistent führt Sie Schritt für Schritt durch den Prozess zum Hinzufügen von Kollegen. Diese erhalten die passenden Zugriffsrechte zum Testen, wenn Sie die Farbe bei &bdquo;Zugang zur Formularvorschau&ldquo; auf Grün umstellen. Klicken Sie einfach auf die Farbe, um die Rechte zu &auml;ndern.</p>
	<p>Die hinzugefügten Nutzer sehen die Umfrage dann automatisch auf ihrer Seite &bdquo;Umfragen&ldquo;, wenn sie sich bei der EUSurvey-Anwendung anmelden. Weitere Informationen erhalten Sie unter &bdquo;<a href="#_Toc8-8">Wie erhalten andere Nutzer Zugang zu meiner Umfrage?</a>&ldquo;.</p>
	
	<h1><a class="anchor" name="_Toc6"></a>Übersetzungen</h1>
	<h2><a class="anchor" name="_Toc6-1"></a>Wie übersetze ich eine Umfrage?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>EUSurvey bietet verschiedene M&ouml;glichkeiten, um eine Umfrage mehrsprachig zur Verfügung zu stellen. Wichtig: Schlie&szlig;en Sie zun&auml;chst die Bearbeitungs- und Testphase Ihrer Umfrage ab, bevor Sie mit dem Übersetzungsschritt beginnen!</p>
	<p>&ouml;ffnen Sie Ihre Umfrage und gehen Sie zur Seite &bdquo;Übersetzungen&ldquo;. Klicken Sie auf &bdquo;Neue Sprachfassung hinzufügen&ldquo;. W&auml;hlen Sie aus der Liste der unterstützten Sprachen die gewünschte Sprache aus. Ist die gewünschte Sprache nicht in der Liste vorhanden, w&auml;hlen Sie &bdquo;Sonstige&ldquo; aus und geben den entsprechenden gültigen Sprachcode nach ISO 639-1 an. Klicken Sie auf &bdquo;OK&ldquo;, um Ihrer Umfrage das leere Übersetzungsformular hinzuzufügen. Lesen Sie auch &bdquo;<a href="#_Toc6-3">Kann ich eine vorhandene Übersetzung online bearbeiten?</a>&ldquo;. Sie erfahren dort, wie Sie Ihrer neu erstellten Übersetzung neue Labels hinzufügen.</p>
	<p>Vergessen Sie nicht, das K&auml;stchen &bdquo;Zu ver&ouml;ffentlichen&ldquo; anzuklicken, wenn die Übersetzung mit Ihrer Umfrage ver&ouml;ffentlicht werden soll. Wenn eine Übersetzung ver&ouml;ffentlicht ist, k&ouml;nnen die Teilnehmer direkt im Umfrage-Link eine der verfügbaren Sprachen ausw&auml;hlen.</p>
	<h2><a class="anchor" name="_Toc6-2"></a>Wie kann ich eine vorhandene Übersetzung in meine Umfrage hochladen?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>&ouml;ffnen Sie Ihre Umfrage und &ouml;ffnen Sie die Seite &bdquo;Übersetzungen&ldquo;. Klicken Sie auf &bdquo;Neue Übersetzung hochladen&ldquo;. Ein Assistent führt Sie Schritt für Schritt durch den Prozess zum Hochladen der Übersetzung.</p>
	<h2><a class="anchor" name="_Toc6-3"></a>Kann ich eine vorhandene Übersetzung online bearbeiten?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Ja! &ouml;ffnen Sie Ihre Umfrage, gehen Sie zur Seite &bdquo;Übersetzungen&ldquo; und w&auml;hlen Sie eine oder mehrere Übersetzungen aus, die Sie bearbeiten m&ouml;chten. W&auml;hlen Sie &bdquo;Übersetzungen bearbeiten&ldquo; aus dem Aktionsmenü direkt unter der Liste der verfügbaren Übersetzungen aus und klicken Sie auf die Schaltfl&auml;che &bdquo;Los!&ldquo; Es &ouml;ffnet sich der Online-Editor für Übersetzungen, in dem Sie mehrere Übersetzungen gleichzeitig bearbeiten k&ouml;nnen. Klicken Sie auf die Schaltfl&auml;che &bdquo;Speichern&ldquo;, damit sichergestellt ist, dass Ihre &Auml;nderungen ins System übernommen werden.</p>
	<h2><a class="anchor" name="_Toc6-4"></a>Kann ich meine Übersetzungen offline erstellen?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Ja! &ouml;ffnen Sie Ihre Umfrage, gehen Sie zur Seite &bdquo;Übersetzungen&ldquo; und exportieren Sie Ihre Umfrage als XLS-, ODS- oder XML-Datei, um die Übersetzung offline durchzuführen. Die Übersetzung kann dann anschlie&szlig;end wieder in Ihre Umfrage importiert werden.</p>
	<p>Der übliche Ablauf ist in der Regel so, dass eine Sprachversion mit dem Status &bdquo;Fertig&ldquo; exportiert wird und anschlie&szlig;end alle verfügbaren Textlabels in die neue Sprache übersetzt werden. Stellen Sie sicher, dass der Sprachcode der neuen Sprache am Anfang des Fragebogens angegeben ist, damit das System die Sprache Ihrer Übersetzung erkennt. Ist die offline erstellte Übersetzung der Umfrage fertig, klicken Sie auf &bdquo;Vorhandene Übersetzung hochladen&ldquo;, damit sie dem System hinzugefügt wird. Um zu verhindern, dass eine Übersetzung versehentlich überschrieben wird, müssen Sie angeben, welche Sprachversion Sie gerade hochladen. Aus Sicherheitsgründen k&ouml;nnen Sie einzelne Labels ausw&auml;hlen, die ausgetauscht werden sollen, wenn es nicht alle Labels betrifft.</p>
	<h2><a class="anchor" name="_Toc6-6"></a>Wie ver&ouml;ffentliche ich meine Übersetzungen bzw. wie hebe ich Ver&ouml;ffentlichungen auf? Warum kann ich diese Übersetzung nicht ver&ouml;ffentlichen? Was ist eine &bdquo;unfertige&ldquo; Übersetzung?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Um eine Umfrage in mehreren Sprachen zu ver&ouml;ffentlichen, &ouml;ffnen Sie Ihre Umfrage, gehen auf die Seite &bdquo;Übersetzungen&ldquo; und setzen unter &bdquo;Zu ver&ouml;ffentlichen&ldquo; ein H&auml;kchen bei den jeweiligen Übersetzungen, die Sie ver&ouml;ffentlichen m&ouml;chten, bzw. entfernen das H&auml;kchen bei den Sprachen, deren Ver&ouml;ffentlichung Sie aufheben m&ouml;chten. Wechseln Sie dann zur Seite &bdquo;Übersicht&ldquo; Ihrer Umfrage, um die Umfrage zu ver&ouml;ffentlichen. Wurde die Umfrage bereits ver&ouml;ffentlicht, bevor die Übersetzungen hinzugefügt/entfernt wurden, klicken Sie auf &bdquo;&Auml;nderungen anwenden&ldquo;.</p>
	<p>Damit gew&auml;hrleistet ist, dass keine Übersetzungen ver&ouml;ffentlicht werden, in denen Text fehlt, k&ouml;nnen Sie Übersetzungen mit leeren Labels (Übersetzungen, die nicht &bdquo;fertig&ldquo; sind) nicht ver&ouml;ffentlichen. Überprüfen Sie mit dem Online-Editor für Übersetzungen, dass Ihre Übersetzung keine leeren Labels enth&auml;lt. Achten Sie auf rot unterlegte Zellen.</p>
	<h2><a class="anchor" name="_Toc6-7"></a>Kann ich Übersetzungen in nichteurop&auml;ischen Sprachen hochladen?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Die Anwendung unterstützt auch nichteurop&auml;ische Sprachen. W&auml;hlen Sie beim Hochladen &bdquo;Sonstige&ldquo; aus und geben Sie einen gültigen zweistelligen Sprachcode nach ISO 639-1 an.</p>
	<h2><a class="anchor" name="_Toc6-8"></a>Was bedeutet &bdquo;Maschinelle Übersetzung anfordern&ldquo;?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>EUSurvey kann Ihren Fragebogen mittels <b>maschineller Übersetzung</b> automatisch übersetzen lassen. Dafür nutzt EUSurvey den von der Europäischen Kommission bereitgestellten Dienst MT@EC.</p>
	<p>Auf der Seite &bdquo;Übersetzungen&ldquo; haben Sie verschiedene Möglichkeiten, maschinelle Übersetzungen anzufordern:</p>
		<ul>
			<li>Möchten Sie eine neue Übersetzung hinzufügen, klicken Sie das Kästchen &bdquo;Übersetzung anfordern&ldquo; an (für eine Übersetzung aus der Relaissprache Ihrer Umfrage).</li>
			<li>Klicken Sie in der Spalte &bdquo;Maßnahmen&ldquo; auf &bdquo;Übersetzung anfordern&ldquo; (für eine Übersetzung aus der Relaissprache Ihrer Umfrage).</li>
			<li>Wählen Sie alle Sprachen aus, in die übersetzt werden soll (sowie mindestens eine &bdquo;fertige&ldquo; Übersetzung, die als Ausgangssprache dienen soll). Wählen Sie dann &bdquo;Übersetzung anfordern&ldquo; aus dem Auswahlfeld unterhalb Ihrer Übersetzungen aus und klicken Sie auf &bdquo;Los!&ldquo;.</li>
		</ul>
	<p>Der Status der Übersetzungen lautet jetzt so lange &bdquo;Angefordert&ldquo;, bis die Übersetzungen angefertigt sind. Änderungen des Status können Sie auf der Seite &bdquo;Übersetzungen&ldquo; verfolgen. </p>
	<p>Mit maschinellen Übersetzungen verfahren Sie wie mit anderen, manuell hinzugefügten Übersetzungen, d. h. sie werden nicht automatisch veröffentlicht und wenn Sie Ihrer Umfrage neue Elemente hinzufügen, müssen auch die Übersetzungen entsprechend ergänzt werden (fordern Sie dafür eine neue Übersetzung an).</p>
	<p><i>Wir übernehmen keine Garantie für Qualität oder Lieferzeit der Übersetzungen.</i></p>
	<p><a href="https://mtatec.ec.testa.eu/mtatec/html/help_en.htm" target="_blank">Maschinelle Übersetzung - Hilfe </a>(nur für EU-Bedienstete).</p>
	<h2><a class="anchor" name="_Toc6-5"></a>Hinweise f&uuml;r Mitarbeiter der EU<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Wir empfehlen vor der Fertigstellung Ihrer Umfrage mit der DGT Kontakt aufzunehmen. Das Team der DGT f&uuml;r sprachliche Aufbereitung (E-Mail: DGT-EDIT) kann mit Ihnen gemeinsam pr&uuml;fen, ob Ihre Umfrage klar strukturiert ist. Weitere Informationen finden Sie hier: <a href="https://myintracomm.ec.europa.eu/serv/de/dgt/Seiten/index.aspx" target="_blank">MyIntraComm-Seiten der DGT</a>.</p>
	<p>Nutzer, die Mitarbeiter der Europ&auml;ischen Kommission sind, k&ouml;nnen veranlassen, dass ihre Umfragen durch die DGT (GD &Uuml;bersetzung) in die EU-Amtssprachen &uuml;bersetzt werden. Die Umfrage sollte als XML-Datei exportiert und &uuml;ber Poetry mit dem &bdquo;requester code&ldquo; der Generaldirektion &uuml;bermittelt werden. Die Umfrage sollte bei Z&auml;hlung in Word nicht mehr als 15 000 Zeichen ohne Leerzeichen umfassen.</p>
	  
	<h1><a class="anchor" name="_Toc7"></a>Umfrage ver&ouml;ffentlichen</h1>
	<h2><a class="anchor" name="_Toc7-1"></a>Wie ver&ouml;ffentliche ich meine Umfrage?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Um eine Umfrage auf Grundlage eines aktuellen Arbeitsentwurfs zu ver&ouml;ffentlichen, gehen Sie auf die Seite &bdquo;Übersicht&ldquo; und klicken Sie auf &bdquo;Ver&ouml;ffentlichen&ldquo;. Nach der Best&auml;tigung erstellt das System automatisch eine Arbeitskopie Ihrer Umfrage und stellt sie online, ebenso wie die Übersetzungen, die Sie auf der Seite &bdquo;Übersetzungen&ldquo; zur Ver&ouml;ffentlichung ausgew&auml;hlt haben (Siehe &bdquo;<a href="#_Toc6-6">Wie ver&ouml;ffentliche ich meine Übersetzungen bzw. wie hebe ich Ver&ouml;ffentlichungen auf?</a>&ldquo;).  Sie finden den Link zu Ihrer ver&ouml;ffentlichten Umfrage auf der Seite &bdquo;Übersicht&ldquo; unter &bdquo;Umfrageort&ldquo;.</p>
	<p>Um die Ver&ouml;ffentlichung Ihrer Umfrage aufzuheben, klicken Sie auf die Schaltfl&auml;che &bdquo;Ver&ouml;ffentlichung zurückziehen&ldquo;. Die nicht mehr ver&ouml;ffentlichte Umfrage bleibt Ihnen in der Form zug&auml;nglich, in der sie ver&ouml;ffentlicht war, ebenso wie Ihr aktueller Arbeitsentwurf. Das bedeutet, dass die nicht mehr ver&ouml;ffentlichte Umfrage nicht durch Ihren aktuellen Arbeitsentwurf ersetzt werden muss, sondern bei Bedarf erneut ver&ouml;ffentlicht werden kann.</p>
	<h2><a class="anchor" name="_Toc7-2"></a>Kann ich die URL anpassen, die zu meiner Umfrage führt?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Ja. Indem Sie den &bdquo;Aliasnamen&ldquo; Ihrer Umfrage &auml;ndern, k&ouml;nnen Sie eine aussagekr&auml;ftigere URL festlegen. &ouml;ffnen Sie die Umfrage und gehen Sie zur Seite &bdquo;Eigenschaften&ldquo;. Klicken Sie unter &bdquo;Grundeinstellungen&ldquo; auf die Schaltfl&auml;che &bdquo;Bearbeiten&ldquo; und &auml;ndern Sie den Alias Ihrer Umfrage. Ein Alias darf nur alphanumerische Zeichen und Bindestriche enthalten. Wenn Sie den Alias einer ver&ouml;ffentlichten Umfrage &auml;ndern, gehen Sie zur Seite &bdquo;Übersicht&ldquo; und klicken Sie auf &bdquo;&Auml;nderungen anwenden&ldquo;.</p>
	<p>Bitte beachten Sie, dass Aliase in EUSurvey eindeutig sein müssen. Sie erhalten eine Warnung, wenn Ihr Alias bereits von einer anderen Umfrage verwendet wird.</p>
	<h2><a class="anchor" name="_Toc7-7"></a>Kann ich einen direkten Link zu einer Übersetzung meiner Umfrage angeben?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Wenn Sie Einladungen versenden (oder den Link zum veröffentlichten Fragebogen auf der Seite &bdquo;Übersicht&ldquo; verwenden), verweist der Link standardmäßig auf den Fragebogen in der Relaissprache.</p>
	<p>Sie können die <b>Umfrageteilnehmer</b> jedoch auch <b>direkt zur richtigen Übersetzung leiten</b>. Nutzen Sie dafür folgenden Link:<br /><b>https://ec.europa.eu${contextpath}/runner/<span style="color:red">SurveyAlias</span>?surveylanguage=<span style="color:red">LC</span></b></p>
	<p>Ersetzen Sie dabei:</p>
		<ul>
			<li><b><span style="color:red">SurveyAlias</span></b> durch den <b>Aliasnamen Ihrer Umfrage</b> und</li>
			<li><b><span style="color:red">LC</span></b> durch den entsprechenden <b>Sprachcode </b> (z. B. DE für Deutsch, FR für Französisch usw.)</li>
		</ul>
	<h2><a class="anchor" name="_Toc7-3"></a>Wie kann ich veranlassen, dass meine Umfrage von selbst ver&ouml;ffentlicht wird, wenn ich im Urlaub bin?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Sie k&ouml;nnen für Ihre Umfrage einen automatischen Ver&ouml;ffentlichungszeitpunkt festlegen. &ouml;ffnen Sie die Umfrage und gehen Sie zur Seite &bdquo;Eigenschaften&ldquo;. Klicken Sie unter &bdquo;Erweiterte Einstellungen&ldquo; auf die Schaltfl&auml;che &bdquo;Bearbeiten&ldquo; und geben Sie das Anfangs- und Enddatum für Ihre Umfrage an.</p>
	<h2><a class="anchor" name="_Toc7-4"></a>Kann ich eine Erinnerung erhalten, bevor meine Umfrage endet?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>EUSurvey kann Ihnen per E-Mail eine Erinnerung schicken, kurz bevor Ihre Umfrage endet. So k&ouml;nnen Sie die n&auml;chsten Schritte vorbereiten (z. B. die Ressourcen für die Analyse der Ergebnisse organisieren).</p>
	<p>Um diese Option zu aktivieren, &ouml;ffnen Sie die Umfrage und gehen Sie zur Seite &bdquo;Eigenschaften&ldquo;. W&auml;hlen Sie &bdquo;Erweiterte Einstellungen&ldquo; aus, klicken Sie auf die Schaltfl&auml;che &bdquo;Bearbeiten&ldquo; und aktivieren Sie &bdquo;Benachrichtigung über bevorstehendes Ende&ldquo;. Geben Sie dabei an, mit wie viel Vorlauf Sie diese E-Mail erwarten und ob alle anderen Fragebogen-Verwalter ebenfalls eine E-Mail erhalten sollen. Klicken Sie auf &bdquo;Speichern&ldquo;.</p>
	<c:if test="${enablepublicsurveys}">
	<h2><a class="anchor" name="_Toc7-5"></a>Wie setze ich meine Umfrage auf die Liste der &ouml;ffentlichen Umfragen in EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Sie k&ouml;nnen einen Link auf Ihre Umfrage in der <a href="http://ec.europa.eu${contextpath}/home/publicsurveys" target="_blank">Liste der &ouml;ffentlichen Umfragen in EUSurvey</a> ver&ouml;ffentlichen.</p>
	<p>&Ouml;ffnen Sie Ihre Umfrage und gehen Sie zur Seite &bdquo;Eigenschaften&ldquo;. W&auml;hlen Sie &bdquo;Sicherheitseinstellungen&ldquo; aus und klicken Sie auf die Schaltfl&auml;che &bdquo;Bearbeiten&ldquo;. W&auml;hlen Sie unter &bdquo;&Ouml;ffentlich&ldquo; &bdquo;Ja&ldquo; und klicken Sie auf &bdquo;Speichern&ldquo;.</p>
	<p><b>Bitte beachten Sie,</b> dass eine Ver&ouml;ffentlichung Ihrer Umfrage in der EUSurvey Liste der &ouml;ffentlichen Umfragen der Validierung durch das EUSurvey-Team bedarf. Wenn Sie auf der Seite &bdquo;Übersicht&ldquo; auf &bdquo;Ver&ouml;ffentlichen&ldquo; oder &bdquo;&Auml;nderungen anwenden&ldquo; klicken, sendet EUSurvey automatisch eine E-Mail an das EUSurvey-Team.</p>
	<p>Sobald Ihre Umfrage validiert wurde, erhalten Sie eine Best&auml;tigungsnachricht und Ihre Umfrage ist in der Liste der &ouml;ffentlichen Umfragen verf&uuml;gbar.</p>
	<p>Bei allen Umfragen, die in der Liste der &ouml;ffentlichen Umfragen in EUSurvey ver&ouml;ffentlicht werden, werden die Teilnehmer aufgefordert, eine Sicherheitsfrage (CAPTCHA) zu beantworten, bevor Sie einen Beitrag absenden. Diese Funktion wird automatisch aktiviert sobald Ihre Umfrage in der Liste erscheint.</p>
	</c:if>
	<h2><a class="anchor" name="_Toc7-6"></a>F&uuml;r Mitarbeiter der EU: Was muss ich beachten, wenn ich eine &ouml;ffentlich zug&auml;ngliche, offene Umfrage ver&ouml;ffentlichen m&ouml;chte (&bdquo;Ihre Stimme in Europa&ldquo; Webseite)?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Bitte befolgen Sie sorgf&auml;ltig <a href ="https://circabc.europa.eu/sd/d/fc02d2ac-d94f-42ed-b866-b3429e0d717b/Survey_publication_your_voice_in_europe_NEW.pdf" target="_blank">das Verfahren f&uuml;r die Ver&ouml;ffentlichung</a> von &ouml;ffentlich zug&auml;nglichen, offenen Umfragen auf den vom Generalsekretariat unterhaltenen Seiten von <a href="http://ec.europa.eu/yourvoice/consultations/index_en.htm" target="_blank">&bdquo;Ihre Stimme in Europa&ldquo;</a>.</p>
	
	<h1><a class="anchor" name="_Toc8"></a>Umfrage verwalten</h1>
	<h2><a class="anchor" name="_Toc8-1"></a>Kann ich Fehler in meiner Umfrage korrigieren, die mir erst sp&auml;ter auffallen?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Ja, Sie k&ouml;nnen Ihre Umfrage nach Belieben bearbeiten und &auml;ndern oder zus&auml;tzliche (abh&auml;ngige) Fragen hinzufügen oder &auml;ndern. Beachten Sie jedoch, dass Ihre Datenlage immer weniger aussagekr&auml;ftiger wird, je &ouml;fter Sie &Auml;nderungen vornehmen, da die verschiedenen Teilnehmer an Ihrer Umfrage unter Umst&auml;nden auf verschiedene Versionen der Umfrage geantwortet haben. Damit Sie in jedem Fall vergleichende Analysen mit dem gesamten Antworten-Bestand durchführen k&ouml;nnen, sollten Sie die Struktur Ihrer Umfrage m&ouml;glichst gar nicht ver&auml;ndern. Achtung: Sie sind in vollem Umfang für jede &auml;nderung verantwortlich, die Sie an Ihrer Umfrage w&auml;hrend ihrer Laufzeit durchführen.</p>
	<p>Wenn Sie eine bereits ver&ouml;ffentlichte Umfrage &auml;ndern m&ouml;chten, denken Sie daran, auf der Seite &bdquo;Übersicht&ldquo; auf &bdquo;&Auml;nderungen anwenden&ldquo; zu klicken, damit die &Auml;nderungen auch in der ver&ouml;ffentlichten Umfrage zu sehen sind.</p>
	<p>Wenn Sie Antworten aus Ihrer Umfrage entfernen m&ouml;chten, lesen Sie: &bdquo;Gehen eingereichte Antworten verloren, wenn ich meine Umfrage &auml;ndere?&ldquo;</p>
	<h2><a class="anchor" name="_Toc8-2"></a>Gehen eingereichte Antworten verloren, wenn ich meine Umfrage &auml;ndere?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Es gehen keine Antworten verloren, es sei denn, Sie l&ouml;schen Ihre Umfrage aus dem System. Sie k&ouml;nnen jedoch unter Umst&auml;nden nicht alle erhobenen Daten sehen, wenn Sie einzelne Fragen aus Ihrer Umfrage entfernt haben, w&auml;hrend die Umfrage noch lief. Das liegt daran, dass die Suchmaske den Fragebogen immer nur in der neuesten ver&ouml;ffentlichten Fassung darstellt. Lesen Sie unter &bdquo;Wie zeige ich den gesamten Bestand gespeicherter Fragen an?&ldquo;, wie Sie alle Antworten sehen k&ouml;nnen, einschlie&szlig;lich der Antworten auf Fragen, die w&auml;hrend der Laufzeit Ihrer Umfrage entfernt wurden.</p>
	<h2><a class="anchor" name="_Toc8-3"></a>Wie kann ich den Titel meiner Umfrage &auml;ndern?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>&Ouml;ffnen Sie die Umfrage und gehen Sie zur Seite &bdquo;Eigenschaften&ldquo;. Klicken Sie unter &bdquo;Grundeinstellungen&ldquo; auf die Schaltfl&auml;che &bdquo;Bearbeiten&ldquo; und &auml;ndern Sie den Titel Ihrer Umfrage. Wenn Ihre Umfrage bereits ver&ouml;ffentlicht ist, denken Sie daran, auf der Seite &bdquo;Übersicht&ldquo; auf &bdquo;&Auml;nderungen anwenden&ldquo; zu klicken.</p>
	<h2><a class="anchor" name="_Toc8-4"></a>Wie kann ich die Kontaktadresse meiner Umfrage &auml;ndern?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>&ouml;ffnen Sie die Umfrage und gehen Sie zur Seite &bdquo;Eigenschaften&ldquo;. Klicken Sie unter &bdquo;Grundeinstellungen&ldquo; auf die Schaltfl&auml;che &bdquo;Bearbeiten&ldquo; und &auml;ndern Sie die Kontaktadresse Ihrer Umfrage. Wenn Ihre Umfrage bereits ver&ouml;ffentlicht ist, denken Sie daran, auf der Seite &bdquo;Übersicht&ldquo; auf &bdquo;&Auml;nderungen anwenden&ldquo; zu klicken.</p>
	<h2><a class="anchor" name="_Toc8-5"></a>Wie kann ich die Bestätigungsnachricht abändern? <a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Die Bestätigungsnachricht sehen Teilnehmer, wenn sie ihren Beitrag abgegeben haben. Um die Standardnachricht abzuändern, öffnen Sie die Umfrage, gehen Sie zu "Eigenschaften", "Spezielle Seiten" und klicken Sie auf "Bearbeiten". Wenn Sie Ihre Umfrage schon veröffentlich haben, denken Sie daran, die Änderungen anzuwenden, indem Sie auf der Übersichtsseite auf "Ausstehende Änderungen anzeigen" und "Änderungen anwenden" klicken.</p>
	<h2><a class="anchor" name="_Toc8-6"></a>Wie passe ich die Standard-Abbruchmeldung an?  <a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Die Escape-Seite enth&auml;lt die Meldung, die Ihre Teilnehmer sehen, wenn Ihre Umfrage nicht verfügbar ist. Um die Standardmeldung zu &auml;ndern, &ouml;ffnen Sie die Umfrage, gehen zum &bdquo;Bearbeitungsprogramm&ldquo; und klicken auf die Schaltfl&auml;che &bdquo;Escape-Seite bearbeiten&ldquo;. Wenn Ihre Umfrage bereits ver&ouml;ffentlicht ist, denken Sie daran, auf der Seite &bdquo;Übersicht&ldquo; auf &bdquo;&Auml;nderungen anwenden&ldquo; zu klicken.</p>
	<h2><a class="anchor" name="_Toc8-7"></a>Archivierung<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Sie k&ouml;nnen Ihre Umfrage mit den dazugeh&ouml;rigen Beitr&auml;gen archivieren, um sie dann zu einem sp&auml;teren Zeitpunkt wieder aufzurufen oder zu ver&ouml;ffentlichen. Um sie zu archivieren, &ouml;ffnen Sie die Seite &bdquo;&Uuml;bersicht&ldquo; und klicken auf das Symbol &bdquo;Umfrage archivieren&ldquo;. Archivierte Umfragen k&ouml;nnen nicht bearbeitet werden und k&ouml;nnen auch keine weiteren Beitr&auml;ge von Teilnehmern erhalten. Aber die Ergebnisse einer Umfrage k&ouml;nnen exportiert werden, und eine PDF-Datei Ihrer Umfrage kann erstellt werden.</p> 
	<p>Archivierte Umfragen sind auf der &Uuml;bersichtsseite gespeichert und k&ouml;nnen von hier auch wieder aktiviert werden zur weiteren Bearbeitung oder Ver&ouml;ffentlichung.</p>
	<h2><a class="anchor" name="_Toc8-8"></a>Wie erhalten andere Nutzer Zugang zu meiner Umfrage?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>In EUSurvey k&ouml;nnen Sie anderen Nutzern Zugang zu Ihrer Umfrage gew&auml;hren, um</p>
		<ul>
			<li>die Umfrage zu testen (&bdquo;Zugang zur Formularvorschau&ldquo;),</li>
			<li>die Ergebnisse einzusehen (&bdquo;Ergebnisse&ldquo;) oder</li>
			<li>die Umfrage zu bearbeiten (&bdquo;Formularverwaltung&ldquo;).</li>
		</ul>
	<p>&Ouml;ffnen Sie daf&uuml;r Ihre Umfrage und die Seite &bdquo;Vorrechte&ldquo;. Dort k&ouml;nnen Sie einer Person oder einer Abteilung Zugang gew&auml;hren.</p>
	<p>Folgende Zugangsrechte sind m&ouml;glich:</p>
		<ul>
			<li>Gr&uuml;n: Lese- und Schreibzugriff</li>
			<li>Gelb: Lesezugriff</li>
			<li>Rot: Kein Zugriff</li>
		</ul>
	<p>Um fortzufahren, klicken Sie auf der Seite &bdquo;Vorrechte&ldquo; auf &bdquo;Nutzer hinzuf&uuml;gen&ldquo; oder &bdquo;Abteilung hinzuf&uuml;gen&ldquo;. Es erscheint ein Fenster mit einem Assistenten, der Ihnen Schritt f&uuml;r Schritt das Hinzuf&uuml;gen von Nutzern erkl&auml;rt.</p>
	<p>M&ouml;chten Sie einen &bdquo;Nutzer hinzuf&uuml;gen&ldquo;, m&uuml;ssen Sie die richtige Dom&auml;ne (z. B. Europ&auml;ische Kommission) ausw&auml;hlen, das Login, die E-Mail-Adresse oder einen anderen Parameter eingeben und auf &bdquo;Suchen&ldquo; klicken. W&auml;hlen Sie dann den Nutzer aus und klicken Sie auf &bdquo;OK&ldquo;. M&ouml;chten Sie eine &bdquo;Abteilung hinzuf&uuml;gen&ldquo;, w&auml;hlen Sie die richtige Dom&auml;ne und Abteilung aus und klicken auf &bdquo;OK&ldquo;.</p>
	<p>Sie gelangen dann auf die Seite &bdquo;Vorrechte&ldquo;. Dort k&ouml;nnen Sie die richtigen Berechtigungen einstellen, indem Sie auf die roten Symbole klicken:</p>
		<ul>
			<li>Zum Testen Ihrer Umfrage:<br>
				Stellen Sie die Farbe bei &bdquo;Zugang zur Formularvorschau&ldquo; auf Gr&uuml;n um. Klicken Sie einfach auf die Farbe, um die Rechte zu &auml;ndern. Die hinzugef&uuml;gten Nutzer sehen die Umfrage dann automatisch auf ihrer Seite &bdquo;Umfragen&ldquo;, wenn sie sich bei EUSurvey anmelden (siehe auch &bdquo;<a href="#_Toc5-2">Wie k&ouml;nnen meine Kollegen meine Umfrage vor der Ver&ouml;ffentlichung testen?</a>&ldquo;).</li>
			<li>Zum Einsehen der Ergebnisse Ihrer Umfrage:<br>
				Stellen Sie die Farbe bei &bdquo;Ergebnisse&ldquo; auf Gelb um. Die Nutzer k&ouml;nnen die Ergebnisse nur einsehen, jedoch nicht bearbeiten oder l&ouml;schen. Stellen Sie die Farbe auf Gr&uuml;n um, k&ouml;nnen sie die Antworten sowohl einsehen als auch bearbeiten und l&ouml;schen (siehe auch &bdquo;<a href="#_Toc9-7">Wie erhalten andere Nutzer Zugang zu den Ergebnissen meiner Umfrage?</a>&ldquo;).</li>
			<li>Zum Bearbeiten Ihrer Umfrage:<br>
				Stellen Sie die Farbe auf Gelb um, k&ouml;nnen bevorrechtigte Nutzer Ihre Umfrage nur einsehen, &auml;ndern Sie die Farbe jedoch auf Gr&uuml;n, k&ouml;nnen sie sie auch bearbeiten. Die Nutzer sehen Ihre Umfrage dann automatisch in ihrer Umfragenliste (siehe auch &bdquo;<a href="#_Toc3-5">Wie erlaube ich anderen Nutzern, meine Umfrage zu bearbeiten?</a>&ldquo;).</li>
		</ul>
	<p>Stellen Sie alle drei Optionen auf Gr&uuml;n um, haben bevorrechtigte Nutzer in vollem Umfang Zugang zu Ihrer Umfrage.</p>
	
	<h2><a class="anchor" name="_Toc8-9"></a>Was sind Aktivitätsprotokolle?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>
	Aktivitätsprotokolle überwachen und protokollieren die Aktivität in Ihrer Umfrage. Auf diese Weise können Sie überprüfen, welcher Benutzer zu welchem Zeitpunkt welche Änderung an Ihrer Umfrage vorgenommen hat. Sie können die Aktivitätsprotokolle auch in verschiedene Dateiformate wie xls, csv und ods exportieren. Um zum Aktivitätsprotokoll Ihrer Umfrage zu gelangen, klicken Sie auf den Link "Aktivität" neben "Eigenschaften". Wenn die Aktivitätsprotokolle leer sind, kann es sein, dass sie systemweit deaktiviert sind. <a href="${contextpath}/resources/documents/ActivityLogEvents.xlsx">Hier</a> finden Sie eine Liste der protokollierten Ereignisse.
	</p>
	
	<h1><a class="anchor" name="_Toc9"></a>Ergebnisse analysieren, exportieren und ver&ouml;ffentlichen</h1>
	<h2><a class="anchor" name="_Toc9-1"></a>Wo finde ich die von meinen Teilnehmern eingereichten Beitr&auml;ge?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Öffnen Sie Ihre Umfrage in EUSurvey (siehe auch &bdquo;<a href="#_Toc2-5">Wie &ouml;ffne ich eine vorhandene Umfrage zur Bearbeitung usw.?</a>) und gehen Sie zur Seite &bdquo;Ergebnisse&ldquo;. Zun&auml;chst wird der vollst&auml;ndige Inhalt aller eingereichten Beitr&auml;ge in einer Tabelle angezeigt. Sie k&ouml;nnen sich die Ergebnisse auf zweierlei Art und Weise anzeigen lassen:</p>
		<ul>
			<li>Vollst&auml;ndiger Inhalt</li>
			<li>Statistiken</li>
		</ul>
	<p>Sie k&ouml;nnen den Anzeigemodus wechseln, indem Sie auf die Symbole in der linken oberen Ecke des Bildschirms klicken. </p>
	<h2><a class="anchor" name="_Toc9-2"></a>Wie kann ich eingereichte Antworten herunterladen?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Um eingereichte Antworten aus EUSurvey in Ihren Rechner zu exportieren, &ouml;ffnen Sie Ihre Umfrage und gehen Sie zur Seite &bdquo;Ergebnisse&ldquo;. Verschiedene Symbole in der rechten oberen Ecke der Seite zeigen die verfügbaren Exportdateiformate. Wenn Sie auf ein Symbol klicken, &ouml;ffnet sich ein Dialogfenster, in dem Sie einen Dateinamen eingeben. Unter diesem Namen wird die Exportdatei auf der Seite &bdquo;Export&ldquo; angezeigt. Es stehen verschiedene Exportdateiformate zur Verfügung, je nach Anzeigemodus (Vollst&auml;ndiger Inhalt/Diagramme/Statistiken). Hinweis: Die Exportdatei enth&auml;lt nur die konfigurierten Fragen sowie die aktuellen Suchergebnisse anhand einer Filterung.</p>
	<h2><a class="anchor" name="_Toc9-3"></a>Wie kann ich eine definierte Teilmenge aller Beitr&auml;ge finden und analysieren?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Suchen Sie auf der Seite &bdquo;Ergebnisse&ldquo; (siehe &bdquo;<a href="#_Toc9-1">Wo finde ich die von meinen Teilnehmern eingereichten Beitr&auml;ge?</a>&ldquo;)nach Schlagw&ouml;rtern in Freitext-Antworten oder w&auml;hlen Sie einzelne Antworten aus Auswahlfragen in der Filterleiste aus. Das reduziert den Gesamtbestand der Antworten auf eine Teilmenge von Beitr&auml;gen. Sie k&ouml;nnen jederzeit den Anzeigemodus wechseln. Dadurch k&ouml;nnen Sie eine umfassende statistische Analyse der erhobenen Daten durchführen. Hinweis: Um Ergebnisse anzusehen und zu analysieren, ben&ouml;tigen Sie bestimmte Rechte (siehe &bdquo;<a href="#_Toc9-7">Wie erhalten andere Nutzer Zugang zu den Ergebnissen meiner Umfrage?</a>&ldquo;). Für den Export einer Teilmenge von Beitr&auml;gen siehe &bdquo;Wie kann ich eingereichte Antworten herunterladen?&ldquo;.</p>
	<h2><a class="anchor" name="_Toc9-4"></a>Wie gelange ich zum vollst&auml;ndigen Bestand der Antworten zurück, nachdem ich eine Teilmenge von Beitr&auml;gen definiert habe?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Um den vollst&auml;ndigen Bestand der Antworten zu sehen, klicken Sie oben auf der Seite &bdquo;Ergebnisse&ldquo; auf die Schaltfl&auml;che &bdquo;Zurücksetzen&ldquo; oder deaktivieren Sie in der Filterleiste auf dieser Seite die Suchen, die Sie durchgeführt haben.</p>
	<h2><a class="anchor" name="_Toc9-5"></a>Wie ver&ouml;ffentliche ich meine Ergebnisse?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>&Ouml;ffnen Sie die Umfrage, gehen Sie zur Seite &bdquo;Eigenschaften&ldquo; und w&auml;hlen Sie &bdquo;Ergebnisse ver&ouml;ffentlichen&ldquo; aus. Hier finden Sie die URL der ver&ouml;ffentlichten Ergebnisse. Wenn Sie auf &bdquo;Bearbeiten&ldquo; klicken, k&ouml;nnen Sie ausw&auml;hlen, welche Fragen/Antworten/Beitr&auml;ge Sie gerne ver&ouml;ffentlichen m&ouml;chten. Sie k&ouml;nnen auch direkt dahin gelangen, indem Sie auf der Seite &bdquo;Übersicht&ldquo; Ihrer Umfrage auf &bdquo;Ver&ouml;ffentlichung der Ergebnisse bearbeiten&ldquo; klicken.</p>
	<p>Achten Sie darauf, dass Sie bei &bdquo;Ergebnisse ver&ouml;ffentlichen&ldquo; unter &bdquo;Ver&ouml;ffentlichen&ldquo; eine Auswahl treffen, anderenfalls ver&ouml;ffentlicht das System gar keine Ergebnisse.</p>
	<h2><a class="anchor" name="_Toc9-6"></a>Wie kann ich auf die ver&ouml;ffentlichten Ergebnisse zugreifen?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>&Ouml;ffnen Sie die Seite &bdquo;Übersicht&ldquo; und klicken Sie auf den Link &bdquo;Ver&ouml;ffentlicht&ldquo; neben dem Wort &bdquo;Ergebnisse&ldquo;. Sie gelangen dann zu den ver&ouml;ffentlichten Ergebnissen. Wer diese Adresse kennt, kann auf Ihre Ergebnisse zugreifen.</p>
	<h2><a class="anchor" name="_Toc9-7"></a>Wie erhalten andere Nutzer Zugang zu den Ergebnissen meiner Umfrage?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>&Ouml;ffnen Sie Ihre Umfrage, gehen Sie auf die Seite &bdquo;Vorrechte&ldquo; und gew&auml;hren Sie anderen Nutzern Zugang zur Ihren Ergebnissen. Weitere Informationen erhalten Sie unter &bdquo;<a href="#_Toc8-8">Wie erhalten andere Nutzer Zugang zu meiner Umfrage?</a>&ldquo;.</p>
	<h2><a class="anchor" name="_Toc9-8"></a>Meine Exportdateien lassen sich nicht entpacken<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Dies kann vorkommen, wenn der Pfadname Ihres Ordners zu lang ist. In Windows dürfen Verzeichnispfade auf der Festplatte höchstens 260 Zeichen lang sein. Mögliche Lösungen:</p>
	<ul>
		<li>Entpacken Sie den Ordner im Stammverzeichnis Ihres Betriebssystems, z. B. unter &bdquo;C&ldquo; anstatt unter &bdquo;C:\Nutzer\NUTZERNAME\Desktop&ldquo;.</li>
		<li>oder benennen Sie beim Entpacken der Dateien den Zielordner um, um den Verzeichnispfad zu verkürzen.</li>
	</ul>
	<h2><a class="anchor" name="_Toc9-9"></a>Ver&ouml;ffentlichte Ergebnisse - von Teilnehmern hochgeladene Dokumente, die personenbezogene Daten enthalten<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Aus datenschutzrechtlichen Gr&uuml;nden muss der Fragebogen-Verwalter entscheiden, ob die hochgeladenen Dokumente der Teilnehmer mit den anderen Ergebnissen ver&ouml;ffentlicht werden sollen. Wenn Sie dies tun m&ouml;chten, dann gehen Sie zur Seite &bdquo;Eigenschaften&ldquo;, &ouml;ffnen Sie &bdquo;Ergebnisse ver&ouml;ffentlichen&ldquo; und klicken Sie auf &bdquo;hochgeladene Dokumente&ldquo;.</p> 
	<p>Bitte beachten Sie, dass dieses Dialogfenster nur erscheint, wenn Ihre Umfrage ein hochgeladenes Element enth&auml;lt.</p>
	<h2><a class="anchor" name="_Toc9-10"></a>Wie kann ich meine Umfrage gestalten, um die Ergebnisse mit oder ohne personenbezogene Daten zu ver&ouml;ffentlichen?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Wenn Sie Ihre Teilnehmer w&auml;hlen lassen m&ouml;chten, ob ihre personenbezogenen Daten mit ihren Antworten ver&ouml;ffentlicht werden sollen oder nicht, erstellen Sie Ihren Fragebogen bitte nach der in <a href="https://circabc.europa.eu/sd/d/e68ff760-226f-40e9-b7cb-d3dcdd04bfb1/How_to_publish_survey_results_anonymously.pdf" target="_blank">diesem Dokument</a> genannten Vorgehensweise.</p>
	<h2><a class="anchor" name="_Toc9-11"></a>Warum sind meine Ergebnisse nicht aktuell?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Es wurde eine neue Datenbank eingeführt, die die Leistung von EUSurvey bei der Abfrage der Ergebnisse Ihrer Umfrage verbessern soll. Dies kann jedoch zu Verzögerungen  führen, bis die neuesten Daten auf der Ergebnisseite Ihrer Umfrage angezeigt werden. Diese Verzögerung sollte nicht mehr als 12 Stunden betragen.</p>
	<p>Sollten die angezeigten Daten älter als 12 Stunden sein, wenden Sie sich bitte an den <a href="https://ec.europa.eu/eusurvey/home/support">Support</a> von EUSurvey.</p>
	
	<h1><a class="anchor" name="_Toc10"></a>Design und Layout</h1>
	<h2><a class="anchor" name="_Toc10-1"></a>Wie &auml;ndere ich das allgemeine Erscheinungsbild meiner Umfrage?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>&Ouml;ffnen Sie Ihre Umfrage, gehen Sie zur Seite &bdquo;Eigenschaften&ldquo; und w&auml;hlen Sie &bdquo;Erscheinungsbild&ldquo; aus. Klicken Sie auf &bdquo;Bearbeiten&ldquo; und w&auml;hlen Sie aus den verfügbaren Skins ein Skin für Ihre Umfrage aus. Klicken Sie auf &bdquo;Speichern&ldquo;. Wenn Ihre Umfrage bereits ver&ouml;ffentlicht ist, denken Sie daran, auf der Seite &bdquo;Übersicht&ldquo; auf &bdquo;&Auml;nderungen anwenden&ldquo; zu klicken.</p>
	<h2><a class="anchor" name="_Toc10-2"></a>Wie kann ich meine eigenen Umfrage-&bdquo;Themen&ldquo; erstellen?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>W&auml;hlen Sie auf der Seite &bdquo;Einstellungen&ldquo; von EUSurvey oben auf Ihrem Bildschirm &bdquo;Skins&ldquo; aus und klicken Sie auf &bdquo;Neuen Skin erstellen&ldquo;. Es &ouml;ffnet sich der Skin-Editor für Umfrage-&bdquo;Themen&ldquo;. Sie k&ouml;nnen ein bestehendes Thema als Grundlage nehmen und die Vorlage mit dem Online-Editor für Skins nach Wunsch &auml;ndern.</p>
	<h2><a class="anchor" name="_Toc10-3"></a>Wie füge ich meiner Umfrage ein Logo hinzu?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Damit in der oberen rechten Ecke Ihrer Umfrage Ihr Projekt-/Unternehmenslogo angezeigt wird, laden Sie auf der Seite &bdquo;Eigenschaften&ldquo; aus dem Untermenü &bdquo;Erscheinungsbild&ldquo; eine Bilddatei hoch. Wenn Ihre Umfrage bereits ver&ouml;ffentlicht ist, denken Sie daran, auf der Seite &bdquo;Übersicht&ldquo; auf &bdquo;&Auml;nderungen anwenden&ldquo; zu klicken.</p>
	<h2><a class="anchor" name="_Toc10-4"></a>Wie füge ich meiner Umfrage nützliche Links hinzu?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>&Ouml;ffnen Sie Ihre Umfrage, gehen Sie zur Seite &bdquo;Eigenschaften&ldquo; und w&auml;hlen Sie &bdquo;Erweiterte Einstellungen&ldquo; aus. Klicken Sie auf die Schaltfl&auml;che &bdquo;Bearbeiten&ldquo;, um unter &bdquo;Nützliche Links&ldquo; Labels und URLs hinzuzufügen. Diese Links werden dann rechts auf jeder Seite Ihrer Umfrage angezeigt. Wenn Ihre Umfrage bereits ver&ouml;ffentlicht ist, denken Sie daran, auf der Seite &bdquo;Übersicht&ldquo; auf &bdquo;&Auml;nderungen anwenden&ldquo; zu klicken.</p>
	<h2><a class="anchor" name="_Toc10-5"></a>Wo lade ich Hintergrunddokumente für meine Umfrage hoch?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>&Ouml;ffnen Sie Ihre Umfrage, gehen Sie zur Seite &bdquo;Eigenschaften&ldquo; und w&auml;hlen Sie &bdquo;Erweiterte Einstellungen&ldquo; aus. Klicken Sie auf die Schaltfl&auml;che &bdquo;Bearbeiten&ldquo;, um ein Label hinzuzufügen, und laden Sie unter &bdquo;Hintergrunddokumente&ldquo; eine Datei hoch. Diese Dokumente werden dann rechts auf jeder Seite Ihrer Umfrage angezeigt. Wenn Ihre Umfrage bereits ver&ouml;ffentlicht ist, denken Sie daran, auf der Seite &bdquo;Übersicht&ldquo; auf &bdquo;&Auml;nderungen anwenden&ldquo; zu klicken.</p>
	<h2><a class="anchor" name="_Toc10-6"></a>Wie erstelle ich eine Umfrage mit mehreren Seiten?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Übergeordnete Abschnitte Ihrer Umfrage lassen sich automatisch in einzelne Seiten unterteilen. &ouml;ffnen Sie Ihre Umfrage, gehen Sie zur Seite &bdquo;Eigenschaften&ldquo;, w&auml;hlen Sie &bdquo;Erscheinungsbild&ldquo; aus und klicken Sie auf &bdquo;Bearbeiten&ldquo;. Aktivieren Sie &bdquo;Mehrere Seiten&ldquo; und klicken Sie auf &bdquo;Speichern&ldquo;. Wenn Ihre Umfrage bereits ver&ouml;ffentlicht ist, denken Sie daran, auf der Seite &bdquo;Übersicht&ldquo; auf &bdquo;&Auml;nderungen anwenden&ldquo; zu klicken.</p>
	<h2><a class="anchor" name="_Toc10-7"></a>Wie aktiviere ich eine automatische Nummerierung für meine Umfrage? <a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Damit alle Abschnitte und Frageelemente Ihres Fragebogens automatisch durchnummeriert werden, &ouml;ffnen Sie Ihre Umfrage, gehen auf die Seite &bdquo;Eigenschaften&ldquo;, w&auml;hlen &bdquo;Erscheinungsbild&ldquo; aus und klicken auf &bdquo;Bearbeiten&ldquo;. Aktivieren Sie &bdquo;Automatische Nummerierung&ldquo; und speichern Sie die Einstellung. Wenn Ihre Umfrage bereits ver&ouml;ffentlicht ist, denken Sie daran, auf der Seite &bdquo;Übersicht&ldquo; auf &bdquo;&Auml;nderungen anwenden&ldquo; zu klicken.</p>
	<h2><a class="anchor" name="_Toc10-8"></a>Kann ich eine individuelle Skin f&uuml;r meine Umfrage erstellen?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Um eine neue Skin f&uuml;r Ihre Umfrage zu erstellen, gehen Sie zur Seite &bdquo;Einstellungen&ldquo; und w&auml;hlen Sie &bdquo;Skins&ldquo; aus. &ouml;ffnen Sie &bdquo;Neue Skin erstellen&ldquo;. Hier k&ouml;nnen Sie das Erscheinungsbild verschiedener Elemente Ihrer Umfrage &auml;ndern: Frage- und Antworttext, Titel, Hilfetext und andere Elemente.</p>
	<p>Geben Sie Ihrer neuen Skin zun&auml;chst einen Namen. Dann w&auml;hlen Sie ein Element aus, das Sie bearbeiten m&ouml;chten. Rechts finden Sie die verschiedenen Schriftoptionen, die Sie  &auml;ndern k&ouml;nnen: Vorder- und Hintergrundfarbe, Schriftart, Schriftfamilie, Schriftgr&ouml;sse und Schriftst&auml;rke. Darunter, im &bdquo;Skin Preview Survey&ldquo;, k&ouml;nnen Sie sogleich sehen, wie das ge&auml;nderte Schriftbild in Ihrer Umfrage dargestellt wird. Dann klicken Sie auf &bdquo;Speichern&ldquo;.</p>
	<p>Wenn Sie mehrere Elemente &auml;ndern m&ouml;chten, k&ouml;nnen Sie eine nach der anderen &auml;ndern und dann am Ende speichern. Es ist nicht n&ouml;tig, eine Speicherung nach jeder ge&auml;nderten Schriftoption durchzuf&uuml;hren.</p> 
	<p>Um die neue Skin f&uuml;r Ihre Umfrage zu &uuml;bernehmen, gehen Sie zur Seite &bdquo;Eigenschaften&ldquo; und w&auml;hlen Sie &bdquo;Erscheinungsbild&ldquo; aus. Klicken Sie auf &bdquo;Bearbeiten&ldquo; und w&auml;hlen Sie Ihre neue Skin aus den verf&uuml;gbaren Skins aus. Dann klicken Sie auf &bdquo;Speichern&ldquo;.</p> 
	
	<h1><a class="anchor" name="_Toc11"></a>Kontakte und Einladungen verwalten</h1>
	<h2><a class="anchor" name="_Toc11-1"></a>Was ist das &bdquo;Adressbuch&ldquo;?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Im &bdquo;Adressbuch&ldquo; k&ouml;nnen Sie Ihre eigene Gruppe von Teilnehmern erstellen. Auf diese Weise k&ouml;nnen Sie Personen oder Organisationen einladen, die bestimmten Kriterien entsprechen (z. B. &bdquo;m&auml;nnlich&ldquo; und &bdquo;&auml;lter als 21&ldquo;). Jeder potenzielle Teilnehmer wird als Kontakt im Adressbuch gespeichert, mit einer unbegrenzten Liste bearbeitbarer Attribute. Sie k&ouml;nnen jeden Kontakt in Ihrem Adressbuch speichern, solange ein Identifikator (&bdquo;Name&ldquo;) und eine E-Mail-Adresse dazu vorhanden sind.</p>
	<h2><a class="anchor" name="_Toc11-2"></a>Was sind die &bdquo;Attribute&ldquo; eines Kontakts?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Jeder Kontakt im Adressbuch kann durch einen variablen Satz von Attributen wie &bdquo;Land&ldquo;, &bdquo;Telefonnummer&ldquo;, &bdquo;Anmerkungen&ldquo; usw. charakterisiert werden. Durch Bearbeiten eines Kontakts k&ouml;nnen Sie ein neues Attribut hinzuf&uuml;gen. &ouml;ffnen Sie im Fenster &bdquo;Kontakt bearbeiten&ldquo; das Men&uuml; &bdquo;Attribute&ldquo; und w&auml;hlen Sie &bdquo;Neu...&ldquo; aus. Es wird ein neues Fenster angezeigt, in dem Sie die Bezeichnung des neuen Attributs festlegen k&ouml;nnen. Das neu erstellte Attribut wird als Spalte im Adressbuch angezeigt und kann auch einem Satz von Kontakten hinzugefügt werden.</p>
	<h2><a class="anchor" name="_Toc11-3"></a>Wie füge ich dem Adressbuch neue Kontakte hinzu?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Gehen Sie auf die Seite &bdquo;Adressbuch&ldquo; und klicken Sie auf &bdquo;Kontakt hinzufügen&ldquo;, wenn Sie einen einzelnen Kontakt hinzufügen m&ouml;chten. Sie k&ouml;nnen eine Liste von Kontakten im XLS-, ODS-, CSV- oder TXT-Format hochladen. Klicken Sie dazu auf &bdquo;Importieren&ldquo;. Siehe auch &bdquo;<a  href="#_Toc11-5">Wie importiere ich mehrere Kontakte aus einer Datei in mein Adressbuch?</a>".</p>
	<h2><a class="anchor" name="_Toc11-4"></a>Was ist ein &bdquo;Registrierungsformular&ldquo;?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Ein Registrierungsformular kann als Umfrage verstanden werden, die automatisch Kontakte aus den pers&ouml;nlichen Daten erstellt, welche die Teilnehmer übermitteln. &ouml;ffnen Sie dazu Ihre Umfrage, gehen Sie auf die Seite &bdquo;Eigenschaften&ldquo; und w&auml;hlen Sie &bdquo;Erweiterte Einstellungen&ldquo; aus. Klicken Sie auf &bdquo;Bearbeiten&ldquo;, w&auml;hlen Sie &bdquo;Ja&ldquo; bei &bdquo;Kontakte erstellen&ldquo; und klicken Sie auf &bdquo;Speichern&ldquo;. Sobald Sie diese Auswahl getroffen haben, fügt das System zwei Freitext-Pflichtfragen (&bdquo;Name&ldquo; und &bdquo;E-Mail-Adresse&ldquo;) ein, damit sichergestellt ist, dass jeder Teilnehmer gültige pers&ouml;nliche Daten angibt.</p>
	<p>Durch Aktivierung der Option &bdquo;Attribute&ldquo; für einzelne Fragen k&ouml;nnen Sie w&auml;hlen, welche weiteren Informationen über den neu erstellten Kontakt gespeichert werden (z. B. kann eine Textfrage mit dem Attribut &bdquo;Telefon&ldquo; dazu verwendet werden, die Telefonnummer des Teilnehmers im Adressbuch zu speichern).</p>
	<h2><a class="anchor" name="_Toc11-5"></a>Wie importiere ich mehrere Kontakte aus einer Datei in mein Adressbuch?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Für den Import einer Liste mit Kontakten in das System bietet EUSurvey einen Assistenten an, der den Nutzer Schritt für Schritt durch den Importprozess führt. Derzeit werden die folgenden Dateiformate unterstützt: XLS, ODS, CSV und TXT (mit Trennzeichen).</p>
	<p>Um den Assistenten zu starten, w&auml;hlen Sie auf der Seite &bdquo;Adressbuch&ldquo; die Option &bdquo;Importieren&ldquo;. W&auml;hlen Sie im ersten Schritt die Datei mit Ihren Kontakten. Geben Sie an, ob die Datei eine Kopfzeile hat oder nicht, und geben Sie an, welche Art Trennzeichen Sie in CSV- oder TXT-Dateien verwendet haben (das wahrscheinlichste Zeichen wird standardm&auml;&szlig;ig vorgeschlagen).</p>
	<p>In einem zweiten Schritt fordert das System Sie auf, die einzelnen Spalten auf neue Attribute für Ihre Kontakte in EUSurvey abzubilden. Bitte beachten Sie, dass die Pflichtattribute &bdquo;Name&ldquo; und &bdquo;E-Mail&ldquo; abgebildet sein müssen, damit Sie fortfahren k&ouml;nnen. Wenn Sie auf &bdquo;Weiter&ldquo; klicken, l&auml;dt das System Ihre Datei in das System und zeigt die einzelnen Kontakte an, die importiert werden. Sie k&ouml;nnen einzelne Kontakte abw&auml;hlen, die nicht importiert werden sollen. Klicken Sie auf &bdquo;Speichern&ldquo;, um Ihre Kontakte im Adressbuch zu speichern.</p>
	<h2><a class="anchor" name="_Toc11-6"></a>Wie bearbeite ich einen Attributwert für mehrere Kontakte gleichzeitig?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Um einen Attributwert für mehrere Kontakte gleichzeitig zu bearbeiten, suchen Sie die Kontakte in Ihrem Adressbuch und w&auml;hlen Sie sie aus. W&auml;hlen Sie &bdquo;Massenbearbeitung&ldquo; aus der Aktionsauswahl aus und klicken Sie auf &bdquo;OK&ldquo;.</p>
	<p>Im Pop-up-Fenster k&ouml;nnen Sie w&auml;hlen, Werte für mehrere Kontakte beizubehalten, zu l&ouml;schen oder festzulegen. Standardm&auml;&szlig;ig werden nur die konfigurierten Attribute angezeigt. Klicken Sie auf das grüne Kreuz, um weitere Attribute zu sehen. Nachdem Sie auf &bdquo;Aktualisieren&ldquo; geklickt und die Sicherheitsmeldung best&auml;tigt haben, speichert die Anwendung Ihre &Auml;nderungen ins Adressbuch.</p>
	<h2><a class="anchor" name="_Toc11-7"></a>Kann ich Kontakte aus meinem Adressbuch auf meinen Rechner exportieren?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Ja. Klicken Sie auf der Seite &bdquo;Adressbuch&ldquo; auf eins der Symbole in der rechten oberen Ecke, die für die einzelnen Dateiformate stehen. Sie finden die exportierten Kontakte auf der Seite &bdquo;Exporte&ldquo;.</p>
	
	<h1><a class="anchor" name="_Toc12"></a>Teilnehmer einladen</h1>
	<h2><a class="anchor" name="_Toc12-1"></a>Wie lege ich eine Gruppe m&ouml;glicher Teilnehmer fest? Was ist eine &bdquo;G&auml;steliste&ldquo;?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>In EUSurvey k&ouml;nnen Sie ausgew&auml;hlte Kontakte zu einer Gruppe zusammenfassen und jedem einzelnen Kontakt eine eigene E-Mail mit individuellem Zugangslink schicken. Das nennt sich &bdquo;G&auml;steliste&ldquo; und ist eine zweite M&ouml;glichkeit - neben dem allgemeinen Passwort für eine Umfrage -Personen zur Teilnahme an Ihrer Umfrage einzuladen.</p>
	<p>Um mehrere Kontakte zu Ihrer Umfrage einzuladen, &ouml;ffnen Sie Ihre Umfrage und gehen Sie auf die Seite &bdquo;Teilnehmer&ldquo;. Klicken Sie auf &bdquo;Neue G&auml;steliste erstellen&ldquo;, um einen Assistenten aufzurufen, der Sie Schritt für Schritt durch den Prozess führt. Geben Sie einen Namen für die Gruppe an und w&auml;hlen Sie eine der folgenden G&auml;stelistearten aus:</p>
		<ul>
			<li>Kontakte aus Ihrem &bdquo;Adressbuch&ldquo; in EUSurvey (Standard)<br/>W&auml;hlen Sie Kontakte aus Ihrem &bdquo;<a href="#_Toc11-1">Adressbuch</a>&ldquo; aus (siehe "Was ist das &bdquo;Adressbuch?"), um sie Ihrer G&auml;steliste hinzuzufügen</li>
			<li>EU-Organe und andere EinrichtungenEU-intern (nur EU-Personal)<br/>W&auml;hlen Sie mehrere Abteilungen Ihrer Institution/Agentur aus, um alle Personen, die dort arbeiten, Ihrer G&auml;steliste hinzuzufügen</li>
			<li>Zugangscodes<br/>Erstellen Sie eine Liste von Zugangscodes, die offline verteilt werden k&ouml;nnen, um auf eine geschützte Online-Umfrage zuzugreifen</li>
		</ul>
	<p>Nutzen Sie die Suchfunktion Ihres Adressbuches und klicken Sie auf die Schaltfl&auml;che &bdquo;Hinzufügen&ldquo; im n&auml;chsten Bildschirm, um Kontakte aus Ihrem Adressbuch auf Ihre neue G&auml;steliste zu verschieben. Durch Klicken auf &bdquo;Speichern&ldquo; wird eine neue G&auml;steliste mit allen Kontakten erstellt, die Sie zur Teilnahme an Ihrer Umfrage einladen wollen.</p>
	<p>Im weiteren Verlauf erfahren Sie, wie Sie E-Mails mit individuellen Zugangslinks an konfigurierte Kontakte aus einer Ihrer G&auml;stelisten versenden k&ouml;nnen.</p>
	<h2><a class="anchor" name="_Toc12-2"></a>Wie bearbeite/entferne ich eine bestehende G&auml;steliste?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>&Ouml;ffnen Sie Ihre Umfrage und gehen Sie zur Seite &bdquo;Teilnehmer&ldquo;. Um die G&auml;steliste zu bearbeiten, klicken Sie auf das Symbol mit dem kleinen Stift. Um eine G&auml;steliste zu entfernen, klicken Sie zun&auml;chst auf die Schaltfl&auml;che &bdquo;Sperren&ldquo;. Jetzt k&ouml;nnen Sie auf die Schaltfl&auml;che &bdquo;Entfernen&ldquo; klicken, um die Liste zu l&ouml;schen.</p>
	<h2><a class="anchor" name="_Toc12-3"></a>Wie schicke ich meinen Teilnehmern eine E-Mail mit einer Einladung?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Sobald Sie eine neue G&auml;steliste erstellt haben, k&ouml;nnen Sie an diese Kontakte Einladungs-E-Mails verschicken. Bei &bdquo;gesch&uuml;tzten&ldquo; sowohl als auch bei &bdquo;offenen&ldquo; Umfragen erh&auml;lt jeder einen pers&ouml;nlichen Zugangslink. <b>Dies bedeutet, dass jeder Teilnehmer, der eine automatische Einladung &uuml;ber EUSurvey erh&auml;lt, den Fragebogen nur einmal beantworten kann.</b></p>
	<p>Klicken Sie auf der Seite &bdquo;Teilnehmer&ldquo; auf das kleine Umschlagsymbol. In dem Dialogfenster, das sich &ouml;ffnet, k&ouml;nnen Sie eine E-Mail-Vorlage aus den Schriftart-Einstellungen ausw&auml;hlen. Standardm&auml;&szlig;ig ist die ausgew&auml;hlte Grundeinstellung &bdquo;EUSurvey&ldquo;. Sie k&ouml;nnen den Betreff und Inhalt Ihrer E-Mail festlegen sowie die &bdquo;Antworten&ldquo;-E-Mail-Adresse. Alle R&uuml;ckmeldungen zu Ihrer Einladungs-E-Mail werden dann zu dieser Adresse gesendet. Dann speichern Sie Ihren E-Mail-Text. Er steht Ihnen in der Liste der Text-Einstellungen f&uuml;r alle weiteren Umfragen und G&auml;stelisten zur Verf&uuml;gung. Klicken Sie dann auf &bdquo;Weiter&ldquo;. Ein Assistent f&uuml;hrt Sie Schritt f&uuml;r Schritt durch den Einladungsprozess.</p>
	
	<h1><a class="anchor" name="_Toc13"></a>Das eigene Konto verwalten</h1>
	<h2><a class="anchor" name="_Toc13-1"></a>Wie &auml;ndere ich mein Passwort?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Der Zugang zur EUSurvey-Referenzinstallation bei der Europ&auml;ischen Kommission wird über EU Login verwaltet. Nutzer von EUSurvey werden aufgefordert, ihr EU Login-Passwort zu &auml;ndern, wenn sie es verloren haben. Dazu steht der Link &bdquo;Passwort vergessen?&ldquo; auf der EU Login-Seite bereit.</p>
	<h2><a class="anchor" name="_Toc13-2"></a>Wie &auml;ndere ich meine E-Mail-Adresse?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Wenn Sie über ein EU Login-Konto auf EUSurvey zugreifen, k&ouml;nnen Sie Ihre E-Mail-Adresse in EUSurvey nicht &auml;ndern. Verbinden Sie sich mit EU Login und w&auml;hlen Sie auf der Registerkarte &bdquo;Kontoinformationen&ldquo; die Option &bdquo;Pers&ouml;nliche Daten &auml;ndern&ldquo; aus, wenn Sie bei EU Login angemeldet sind.</p>
	<p>Nutzer der OSS-Version von EUSurvey und gesch&auml;ftliche Nutzer der API-Bedienoberfl&auml;che verbinden sich bitte mit der Anwendung. Klicken Sie unter &bdquo;Einstellungen&ldquo; > &bdquo;Mein Konto&ldquo; > &bdquo;Sprache&ldquo; auf &bdquo;E-Mail-Adresse &auml;ndern&ldquo;.</p>
	<h2><a class="anchor" name="_Toc13-3"></a>Wie &auml;ndere ich meine Standard-Spracheinstellung?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Sie k&ouml;nnen die Standard-Spracheinstellung für neue Umfragen &auml;ndern. Gehen Sie zu &bdquo;Einstellungen&ldquo; ->&bdquo;Mein Konto&ldquo; und klicken Sie auf &bdquo;Sprache wechseln&ldquo;. Ist die Aktualisierung gespeichert, schl&auml;gt das System bei jeder neu erstellten Umfrage die konfigurierte Sprache als Hauptsprache vor.</p>
	
	<h1><a class="anchor" name="_Toc14"></a>Datenschutz</h1>
	<h2><a class="anchor" name="_Toc14-1"></a>Dieses System verwendet Cookies. Welche Informationen werden dabei gespeichert?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Das Informatiksystem verwendet sogenannte Cookies, um die Kommunikation zwischen Client und Server zu gewährleisten. Die Nutzer müssen ihren Browser also so konfiguriert haben, dass er Cookies akzeptiert. Nach dem Abmelden werden die Cookies gel&ouml;scht.</p>
	<p>Das System speichert Beiträge zu einer Umfrage lokal, um beispielsweise bei einer Unterbrechung der Serververbindung während der Übermittlung des Beitrags oder bei versehentlichem Abschalten des Computers über eine Sicherheitskopie zu verfügen. Gespeichert werden die Kennungen der Fragen und die zugeh&ouml;rigen Antworten in der jeweils letzten Fassung. Sobald Teilnehmer ihren Beitrag an den Server übermittelt und darauf gespeichert haben, werden die lokal gespeicherten Daten gelöscht. Über der Umfrage befindet sich ein Kästchen &bdquo;Lokale Sicherheitskopie anlegen (bei öffentlichen / gemeinsam genutzten Computern deaktivieren)&ldquo;, um diese Funktion auszuschalten. In dem Fall werden keine Daten auf dem betreffenden Rechner gespeichert.</p>
	<h2><a class="anchor" name="_Toc14-2"></a>Welche Informationen speichert EUSurvey, wenn Teilnehmer einen Beitrag einsenden?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Die von EUSurvey gespeicherten Informationen h&auml;ngen von den Sicherheitseinstellungen Ihrer Umfrage sowie der Art und Weise ab, wie Sie Ihre Teilnehmer zu Ihrer Umfrage einladen.</p>
	<p><b>&Ouml;ffentlich zug&auml;ngliche, offene Umfragen:</b> Standardm&auml;ßig speichert EUSurvey keine nutzerbezogenen Informationen. Es wird jedoch aus Sicherheitsgr&uuml;nden bei jedem Server-Zugriff die IP-Adresse der Verbindung gespeichert (siehe <a href="https://ec.europa.eu/eusurvey/home/privacystatement" target="_blank">Datenschutzerkl&auml;rung</a>).</p>
	<p><b>Passwortgesicherte Umfragen:</b> Haben Sie Ihre Umfrage nur durch ein Password gesichert, speichert EUSurvey keine nutzerbezogenen Informationen. Es wird jedoch aus Sicherheitsgr&uuml;nden bei jedem Server-Zugriff die IP-Adresse der Verbindung gespeichert (siehe <a href="https://ec.europa.eu/eusurvey/home/privacystatement" target="_blank">Datenschutzerkl&auml;rung</a>).</p> 
	<p><b>Umfragen mit EU Login-Authentifizerung:</b>: Haben Sie Ihre Umfrage mittels EU Login-Authentifizierung gesichert, wird EUSurvey die E-Mail-Adresse des EU Login-Kontos des Nutzers speichern. Zus&auml;tzlich wird aus Sicherheitsgr&uuml;nden bei jedem Server-Zugriff die IP-Adresse der Verbindung gespeichert (siehe <a href="https://ec.europa.eu/eusurvey/home/privacystatement" target="_blank">Datenschutzerkl&auml;rung</a>).</p>
	<p><b>Einladungen mit EUSurvey verschicken:</b>: Wenn Sie die Einladungen an Ihre Teilnehmer &uuml;ber die G&auml;steliste in EUSurvey verschicken, erh&auml;lt jeder Teilnehmer einen pers&ouml;nlichen Zugangslink. EUSurvey speichert dabei eine Einladungsnummer, die sp&auml;ter genutzt werden kann um die eingeladenen Teilnehmer mit deren eingereichten Beitr&auml;gen zu verkn&uuml;pfen. Dieses Verhalten ist unabh&auml;ngig von den Sicherheitseinstellungen Ihrer Umfrage. Zus&auml;tzlich wird aus Sicherheitsgr&uuml;nden bei jedem Server-Zugriff die IP-Adresse der Verbindung gespeichert (siehe <a href="https://ec.europa.eu/eusurvey/home/privacystatement" target="_blank">Datenschutzerkl&auml;rung</a>).</p>
	<p><b>Erstellen einer anonymen Umfrage:</b>: Sie k&ouml;nnen eine anonyme Umfrage erstellen, indem Sie in den Eigenschaften Ihrer Umfrage unter dem Punkt &bdquo;Sicherheitseinstellungen&ldquo; die Option &bdquo;Privatsph&auml;re&ldquo; auf &bdquo;Nein&ldquo; stellen. In diesem Fall werden alle nutzerbezogenen Daten durch &bdquo;Anonym&ldquo; ersetzt. Es wird jedoch aus Sicherheitsgr&uuml;nden bei jedem Server-Zugriff die IP-Adresse der Verbindung gespeichert (siehe <a href="https://ec.europa.eu/eusurvey/home/privacystatement" target="_blank">Datenschutzerkl&auml;rung</a>).</p>
	<h2><a class="anchor" name="_Toc14-3"></a>Muss ich eine Datenschutzerkl&auml;rung in meine Umfrage aufnehmen?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Anfang der Seite&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
	<p>Das hängt von Ihren Fragen und von der Art der Daten ab, die Sie mit Ihrer Umfrage erheben. Beachten Sie, dass Personen Ihrer Zielgruppe gegebenenfalls nicht an Ihrer Umfrage teilnehmen möchten, wenn Sie die Vertraulichkeit der übermittelten Daten nicht garantieren können.</p> 
	<p><b>F&uuml;r Mitarbeiter der EU:</b></p>
	<p>Wir weisen Sie auf die Politik zum &bdquo;Schutz natürlicher Personen bei der Verarbeitung personenbezogener Daten&ldquo; hin <a href="http://eur-lex.europa.eu/LexUriServ/LexUriServ.do?uri=OJ:L:2001:008:0001:0022:EN:PDF" target="_blank">(Verordnung (EG) Nr. 45/2001)</a>. Werden personenbezogene Daten erhoben, muss eine Datenschutzerklärung erstellt und zusammen mit dem Fragebogen veröffentlicht werden. Für die Genehmigung der Datenschutzerklärung wenden Sie sich bitte an den Datenschutzkoordinator Ihrer GD. Darüber hinaus müssen Sie jede Erhebung personenbezogener Daten dem Datenschutzbeauftragten (DSB) melden. Bitte wenden Sie sich an Ihren Datenschutzkoordinator, wenn Sie für die Meldung an den DSB Hilfe benötigen.</p>
	<p>Nachstehend finden Sie einige Muster für Datenschutzerklärungen, die Sie für Ihre Umfragen verwenden können. Sie können die Muster Ihren Anforderungen anpassen:</p>
		<ul>
			<li>Muster<a href="https://circabc.europa.eu/sd/a/a8f80d78-8620-4326-95ee-7bceb5b18fbc/Template_privacy_statement_surveys_or_consultations.doc" target="_blank">&bdquo;Datenschutzerklärung für Umfragen und Konsultationen&ldquo;</a></li>
			<li>Muster<a href="https://circabc.europa.eu/sd/a/650ea0ea-79d4-4cf3-93d4-5feb37af10a1/Template_privacy_statement_online_registrations.doc" target="_blank">&bdquo;Datenschutzerklärung für Anmeldungen zu Veranstaltungen und Konferenzen&ldquo;</a></li>
		</ul>
	
	
		</div>
	</div>

	<%@ include file="../footer.jsp" %>	

</body>
</html>