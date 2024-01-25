<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" session="true" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Documentation" /></title>	
	<%@ include file="../includes.jsp" %>	
	<link href="${contextpath}/resources/css/yellowfocus.css?version=<%@include file="../version.txt" %>" rel="stylesheet" type="text/css"></link>
	
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
		
		#faqcontent img {
			border: 1px solid #999;
		}
		
		#ulContainer {
			margin-bottom: 50px;
		}

		figcaption {
			font-style: italic;
			padding: 2px;
			text-align: center;
		}

		#faqcontent a {
			text-decoration: underline;
		}

	</style>

	<script language="javascript" type="text/javascript" src="${contextpath}/resources/js/tree/treemenu.js?version=<%@include file="../version.txt" %>"></script>
	<link rel="stylesheet" href="${contextpath}/resources/js/tree/treeview.css?version=<%@include file="../version.txt" %>" type="text/css">
	<script language="javascript" type="text/javascript" src="${contextpath}/resources/js/tree/treemenu2.js?version=<%@include file="../version.txt" %>"></script>
	
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
		</ul>
	
	</div>
	
	<div id="faqcontent">
	
		<h1>
				Allgemeine Fragen
			</h1>
			<h2>
				Was ist EUSurvey?				
			</h2>
			<p>
				EUSurvey ist ein Online-Tool, mit dem Sie Fragebögen und andere interaktive
				Formulare erstellen, veröffentlichen und verwalten können.
			</p>
			<h2>
				Wann sollte ich EUSurvey verwenden?				
			</h2>
			<p>
				EUSurvey bietet sich zur Nutzung an, wenn Sie:
			</p>
			<p>
				<ul>
					<li>
						einen Fragebogen oder ein interaktives Formular online zugänglich machen
					</li>
					<li>
						oder eine große Anzahl ähnlicher Datensätze erheben wollen.
					</li>
				</ul>
			</p>
			<h2>
				Was kann EUSurvey technisch nicht leisten?				
			</h2>
			<p>
				EUSurvey ist unter Umständen nicht für Ihr Projekt geeignet,
			</p>
			<ul>
				<li>
					wenn es erforderlich ist, dass verschiedene Befragte an ein und
					demselben Beitrag (Antwort) arbeiten, bevor dieser eingereicht wird,
				</li>
				<li>
					wenn Antworten vor dem Absenden validiert werden müssen.
				</li>
			</ul>
			<p>
				Weitere Informationen:
			</p>
			<ul>
				<li>
					Zu den Nutzungsbeschränkungen siehe die Seite
					<a href="https://ec.europa.eu/eusurvey/home/documentation?language=de">
						Unterstützung
					</a>.
				</li>
				<li>
					Wenden Sie sich        <a href="https://ec.europa.eu/eusurvey/home/support">hier</a> an das
					EUSurvey Support-Team.
				</li>
			</ul>
			<h2>
				Funktionen von EUSurvey				
			</h2>
			<p>
				<b>Individuell anpassbare Fragebögen</b>
				<br/>
				Sie können Sie aus einer Reihe von Fragetypen wählen, z. B.
			</p>
			<ul>
				<li>
					einfache Text- und Auswahlfragen
				</li>
				<li>
					Fragen im Tabellenformat
				</li>
				<li>
					Fragen mit Multimedia-Elementen
				</li>
			</ul>
			<p>
				Strukturieren Sie Ihre Umfrage durch spezielle Gliederungselemente.
			</p>
			<p>
				<b>Abhängige Fragen</b>
				<br/>
				EUSurvey kann antwortabhängig zusätzliche Fragen und Felder anzeigen.
				Dadurch wird die Umfrage interaktiver.
			</p>
			<p>
				<b>Veröffentlichung nach Zeitplan</b>
				<br/>
				Sie können frei auswählen, wann Ihre Umfrage automatisch veröffentlicht
				bzw. wann die Veröffentlichung beendet werden soll.
			</p>
			<p>
				<b>Veränderungen nach Veröffentlichung der Umfrage</b>
				<br/>
				Veröffentlichte Umfrage können ohne Verlust bereits eingegangener Antworten
				geändert werden.
			</p>
			<p>
				<b>Sprachen</b>
				<br/>
				Die Bedienoberfläche steht in 23 offiziellen EU-Sprachen zur Verfügung.
			</p>
			<p>
				Sie können Ihren Fragebogen in jede beliebige der 136 von der Norm ISO
				639-1 erfassten Sprachen übersetzen lassen (ISO 639 ist eine internationale
				Norm für Sprachencodes).
			</p>
			<p>
				<b>Sicherheit</b>
				<br/>
				EUSurvey verfügt über die für die Sicherung der Online-Fragebögen
				erforderliche Struktur.
			</p>
			<p>
				<b>Einladungen direkt verschicken</b>
				<br/>
				In EUSurvey können Sie Ihre Kontakte verwalten und E-Mails mit
				individuellen Umfragelinks verschicken.
			</p>
			<p>
				<b>Umfassender Datenschutz</b>
				<br/>
				Sie können Ihren Teilnehmern garantieren, anonym zu bleiben, indem Sie den Modus 'Anonyme Umfrage' aktivieren. Wenn aktiviert, werden alle Beiträge anonymisiert. Das bedeutet, dass keine benutzerbezogenen Daten vom System gespeichert werden.
			</p>
			<p>
				<b>Erscheinungsbild individuell anpassen</b>
			</p>
			<ul>
				<li>
					Alle Layout-Aspekte des Formulars können mit flexiblen Werkzeugen
					angepasst werden.
				</li>
				<li>
					Sie können Sie Ihr Formular an ein bestimmtes Projekt anpassen und dafür
					unsere große Auswahl an Vorlagen verwenden.
				</li>
				<li>
					Auswahl zwischen ein- und mehrseitigen Umfragen.
				</li>
			</ul>
			<p>
				<b>Antwort als Entwurf speichern </b>
				<br/>
				Die Befragten können ihre Antworten als Entwurf auf dem Server speichern
				und später mit der Bearbeitung fortfahren.
			</p>
			<p>
				<b>Fragebogen offline beantworten</b>
				<br/>
				Die Befragten können einen Fragebogen offline beantworten und die
				vollständige Antwort zu einem späteren Zeitpunkt an den Server übermitteln.
			</p>
			<p>
				<b>Automatische Durchnummerierung</b>
				<br/>
				Um Ihre Umfrage zu strukturieren, können Sie die verschiedenen Abschnitte
				Ihres Fragebogens von EUSurvey automatisch durchnummerieren lassen.
			</p>
			<p>
				<b>Version mit hohem Kontrast</b>
				<br/>
				Befragte mit eingeschränkter Sehfähigkeit können sich die Umfragen in einer
				Version mit hohem Kontrast anzeigen lassen. Diese Version wird für jede
				Umfrage automatisch erstellt.
			</p>
			<p>
				<b>Hochladen von unterstützenden Dateien</b>
				<br/>
				Sie können zu ihrer Umfrage Dateien hochladen, die alle Befragten
				herunterladen können.
			</p>
			<h2>
				Fragebogenverwaltung
			</h2>
			<p>
				<b>Zusammenarbeit</b>
				<br/>
				Bei Umfragen, die von mehreren Nutzern verwaltet werden, können Sie in
				EUSurvey andere Nutzer dazu berechtigen, eine Umfrage zu testen oder die
				Ergebnisse zu analysieren.
			</p>
			<h2>
				Ergebnisverwaltung
			</h2>
			<p>
				<b>Analyse Ihrer Ergebnisse</b>
				<br/>
				Sie können eine einfache Ergebnisanalyse vornehmen und Daten visuell in
				Histogrammen und Diagrammen darstellen lassen.
			</p>
			<p>
				Sie können Umfrageergebnisse auch in Standard-Tabellenformate überführen
				und in statistischen Anwendungen weiterverarbeiten.
			</p>
			<p>
				<b>Veröffentlichung Ihrer Ergebnisse</b>
				<br/>
				Sie können eine Teilmenge aller eingereichten Antworten auf den internen
				Seiten der Anwendung veröffentlichen. Das System kann automatisch
				Statistiken berechnen und Diagramme erstellen.
			</p>
			<p>
				<b>Bearbeitung bereits eingesandter Antworten</b>
				<br/>
				Die Befragten können ihre Antworten bei Bedarf nach dem Einsenden ändern.
			</p>
			<h2>
				Wo finde ich weitere Informationen über EUSurvey?				
			</h2>
			<p>
				Brauchen Sie <b>praktische Hilfe</b>? Klicken Sie auf „
				<a href="https://ec.europa.eu/eusurvey/home/documentation?language=de" target="_blank">
					Unterstützung
				</a>
				“ (unter <i>„Hilfe“</i> oben rechts auf dem Bildschirm).
			</p>
			<p>
				Möchten Sie mehr über den Hintergrund und zur Finanzierung von EUSurvey
				wissen? Klicken Sie auf „
				<a
						href="https://ec.europa.eu/eusurvey/home/about?language=de"
						target="_blank"
						>
					Info
				</a>
				“.
			</p>
			<h2>
				An wen kann ich mich bei technischen Problemen wenden?				
			</h2>
			<p>
				<b>EU-Bedienstete</b>
				– bitte kontaktieren Sie Ihr IT-Helpdesk und bitten um Weiterleitung des
				Problems an das Support-Team von EUSurvey (bitte beschreiben Sie das
				Problem so genau wie möglich).
			</p>
			<p>
				Externe Nutzer – bitte wenden Sie sich an
				<a
						href="mailto:EC-CENTRAL-HELPDESK@ec.europa.eu?subject=Incident%20Creation%20Request%20for%20DIGIT%20EUSURVEY%20SUPPORT%20&amp;body=%20Dear%20Helpdesk,%20%20Could%20you%20please%20open%20a%20ticket%20to%20DIGIT%20EUSURVEY%20SUPPORT%20with%20the%20following%20description:"
						target="_blank"
						>
					Zentrale Helpdesk
				</a>
				der Kommission.
			</p>
			<h2>
				Wie kann ich Feedback zur Verbesserung von EUSurvey geben?				
			</h2>
			<p>
				Bitten Sie Ihr IT-Helpdesk bzw. das Zentrale Helpdesk darum, Ihre
				Kommentare und Anregungen an das Support-Team von EUSurvey weiterzuleiten.
			</p>
			<p>
				Das Support-Team wird sich so schnell wie möglich bei Ihnen melden.
			</p>
			<h2>
				Welche Browser unterstützt EUSurvey?			
			</h2>
			<p>
				Microsoft Edge, Mozilla Firefox und Google Chrome (jeweils die letzten
				beiden Versionen).
			</p>
			<p>
				Die Verwendung anderer Browser kann Kompatibilitätsprobleme verursachen.
			</p>
			<h2>
				EUSurvey-Haftungsausschluss (nur für externe Nutzer)			
			</h2>
			<p>
				Bei allen Fragebögen und Einladungen per E-Mail, die von Nutzern erstellt
				wurden, die keine <b>EU-Bediensteten </b>sind, erscheint folgender
				Haftungsausschluss:
			</p>
			<p>
				Haftungsausschluss
				<br/>
				<i>
					Die Europäische Kommission haftet nicht für den Inhalt der unter
					Nutzung des Dienstes EUSurvey verfassten Fragebögen. Die Verantwortung
					dafür liegt allein beim Ersteller des Formulars und Ausrichter der
					Umfrage. Die Nutzung des Dienstes EUSurvey impliziert keine Empfehlung
					oder Billigung der in den damit erstellten Umfragen zum Ausdruck
					gebrachten Ansichten durch die Europäische Kommission.
				</i>
			</p>
			<h2>
				Können die Befragten zum Ausfüllen mobile Endgeräte nutzen?				
			</h2>
			<p>
				Ja, die Befragten können den Fragebogen auf einem Smartphone oder Tablet-PC
				beantworten.
			</p>
			<h2>
				Gibt es eine Mindest-Bildschirmgröße?
			</h2>
			<p>
				Nein, die Fragebögen passen sich jeweils der Größe des verwendeten
				Bildschirms an.
			</p>
			<p>
				Für die benutzerfreundliche<i> Erstellung </i>und <i>Verwaltung</i> von
				Umfragen empfehlen wir allerdings eine Mindestauflösung von 1680x1050
				Pixel.
			</p>
			<h1>
				Einloggen/Konto erstellen
			</h1>
			<h2>
				Ich habe ein EU-Login-Konto. Muss ich mich separat bei EUSurvey
				registrieren?				
			</h2>
			<p>
				Nein, ein EU-Login-Konto ist ausreichend.
			</p>
			<p>
				Für den Zugang zu EUSurvey klicken Sie bitte auf der
				<a
						href="https://ec.europa.eu/eusurvey/home/welcome?language=de"
						target="_blank"
						>
					EUSurvey-Startseite
				</a>
				auf die Anmeldeschaltfläche.
			</p>
			<h2>
				Wie verbinde ich mich mit EUSurvey?<u> </u>
			</h2>
			<p>
				Klicken Sie auf den Login-Button auf der <u>EUSurvey-Homepage</u>, Sie
				werden dann zum EUSurvey-Anmeldebildschirm weitergeleitet.
			</p>
			<p>
				Dort können Sie die Option auswählen, die Ihrer persönlichen Situation
				entspricht:
			</p>
			<ul>
				<li>
					<b>Wenn Sie für eine EU-Institution arbeiten</b>
					, wählen Sie die zweite Option, und verwenden Sie Ihren
					EU-Login-Benutzernamen und Ihr Passwort.
				</li>
				<li>
					<b>Wenn Sie nicht für eine EU-Institution arbeiten</b>
					<b>(externe Nutzer)</b>
					, wählen Sie die erste Option. Sie müssen Ihr Mobiltelefon zuvor
					registriert haben, um die
					<a
							href="https://de.wikipedia.org/wiki/Hilfe:Zwei-Faktor-Authentifizierung"
							>
						Zwei-Faktor-Authentifizierung
					</a>
					zu durchlaufen.
				</li>
			</ul>
			<p>
				<a href="https://webgate.ec.europa.eu/cas/eim/external/register.cg">
					EU-Login-Konto erstellen
				</a>
				(sofern nicht bereits vorhanden)
			</p>
			<p>
				<a
						href="https://ecas.ec.europa.eu/cas/userdata/mobileApp/manageMyMobileDevices.cgi"
						>
					Registrieren Sie bitte Ihr Mobiltelefon
				</a>
				(sofern Sie nicht für eine EU-Institution arbeiten)
			</p>
			<h1>
				Umfrage erstellen
			</h1>
			<h2>
				Wie erstelle ich eine neue Umfrage?
			</h2>
			<p>
				Auf der Seite „Begrüßung“ oder der Seite „Umfragen“:
			</p>
			<p>
				<ol>
					<li>
						1. Klicken Sie auf <b>Neue Umfrage</b> ? und dann auf    <b>Neue Umfrage erstellen</b>. Es öffnet sich ein Dialogfenster.
					</li>
					<li>
						Wenn Sie alle Pflichtangaben gemacht haben, klicken Sie auf „Erstellen“.
					</li>
					<li>
						Die Anwendung wird Ihre neue Umfrage in das System laden und automatisch
						den „Editor“ öffnen. So können Sie direkt mit der Bearbeitung der Umfrage
						beginnen.
					</li>
				</ol>
			</p>
			<h2>
				Welche Arten von Umfragen kann ich erstellen?				
			</h2>
			<p>
				Bitte wählen Sie die entsprechende Option aus:
			</p>
			<p>
				<ul>
					<li>
						<b>Standard-Umfrage</b><br />
						Konventioneller Fragebogen.
					</li>
					<li>
						<b>Quiz</b><br />
						<br/>
						Bei Umfragen im Quiz-Format wird für jeden Befragten ein Endergebnis
						berechnet. Sie können z.B. als Fähigkeitstests oder elektronische Prüfungen
						genutzt werden. Weitere Einzelheiten sind dem
						<a
								href="https://circabc.europa.eu/sd/a/400e1268-1329-413b-b873-b42e41369a07/EUSurvey_Quiz_Guide.pdf"
								target="_blank"
								>
							EUSurvey Quiz-Leitfaden
						</a>
						zu entnehmen.<br/><br/>
						Der Quiz-Modus bietet:
						<ul>
							<li>
								eine Bewertungsfunktion
							</li>
							<li>
								die Überprüfung der Antworten der Befragten
							</li>
							<li>
								die Möglichkeit, den Befragten aufgrund ihrer Antworten Feedback zu geben
							</li>
							<li>
								eine zusätzliche Ergebnisanalyse, die speziell für Quizfragen entwickelt
								wurde
							</li>
						</ul>
					</li>
					<li>
						<b>Öffentliche BPR-Konsultation</b><br /><br />
				
						Speziell für öffentliche Konsultationen über das Portal „EU-Recht
						vereinfachen“ (BPR) (veröffentlicht auf der Europa-Website
						<a href="https://ec.europa.eu/info/law/better-regulation/have-your-say">
							„Ihre Meinung zählt“
						</a>
						).
						<br/>
						<br/>
						Die Vorlage für BRP-Umfragen bietet folgende Möglichkeiten:
						<ul>		
							<li>
								Festlegung von <b>Metadatenfeldern</b>, die eine einheitliche
								Identifizierung der Befragten in verschiedenen Erhebungen ermöglichen,
								wodurch die Berichterstattung vereinfacht wird
							</li>
							<li>
								maßgeschneiderte <b>Datenschutzerklärung</b>‚ die den besonderen
								Anforderungen der öffentlichen Konsultationen Rechnung trägt
							</li>
							<li>
								<b>automatisches Öffnen und Schließen</b> der Umfrage über das BRP-Portal
							</li>
							<li>
								automatische Synchronisierung (Übermittlung von Daten) der Antworten der
								Befragten an das BRP-Portal zur Weiterverarbeitung
							</li>
						</ul>
					</li>
				</ul>
			</p>		
			<h2>
				Wie importiere ich eine bereits vorhandene Umfrage aus meinem Rechner?				
			</h2>
			<p>
				<ol>
					<li>
						Gehen Sie zur Seite „Begrüßung“ oder zur Seite „Umfragen“.
					</li>
					<li>
						Klicken Sie auf die Option „Neue Umfrage“ „Umfrage importieren“. Es
						öffnet sich ein Dialogfenster.
					</li>
					<li>
						Wählen Sie die Umfrage-Datei auf Ihrem Computer aus und klicken Sie dann
						auf „Importieren“ und Ihre Umfrage wird in EUSurvey importiert.
					</li>
				</ol>
			</p>			
			<p>
				Hinweis: Umfragen können nur als Zip-Datei oder mit der Dateierweiterung
				.eus importiert werden.
			</p>
			<h2>
				Wo finde ich die von mir erstellten Umfragen?
			</h2>
			<p>
				Hier gibt es zwei Möglichkeiten:
			</p>
			<p>
				<ul>
					<li>
						auf der Übersichtsseite finden Sie eine Liste aller von Ihnen angelegten Umfragen oder
					</li>
					<li>
						Sie gehen zur Seite „Umfragen“ ? und wählen im Suchfeld die Option „Meine
						Umfragen“ aus.
					</li>
				</ul>
			</p>
			<h2>
				Wie öffne ich eine vorhandene Umfrage zur Bearbeitung usw.?
			</h2>
			<p>
				Gehen Sie zur Seite „Umfragen“.
			</p>
			<p>
				<ol>
					<li>
						Klicken Sie bei der gewünschten Umfrage auf das Symbol „Öffnen“. Dann
						wird die Seite „Übersicht“ mit weiteren Registerkarten geöffnet.
					</li>
					<li>
						Wählen Sie „Editor“ aus.
					</li>
					<li>
						Testen Sie Ihre Umfrage oder lassen Sie sich Informationen zu den
						„Ergebnissen“, „Übersetzungen“, „Eigenschaften“ usw. anzeigen.
					</li>
				</ol>
			</p>
			<h2>
				Wie exportiere ich eine vorhandene Umfrage?
			</h2>
			<p>
				Suchen Sie auf der Seite „Umfragen“ nach der Umfrage, die Sie exportieren
				möchten. Sie können entweder:
			</p>
			<ul>
				<li>
					auf das Symbol „Exportieren“ klicken oder
				</li>
				<li>
					auf das Symbol „Öffnen“ klicken ? auf der Seite „Übersicht“ auf das
					Symbol „Exportieren“ klicken.
				</li>
			</ul>			
			<p>
				Ihre Umfrage wird dann mit allen gewählten Einstellungen auf Ihrem Computer
				gespeichert.
			</p>
			<p>
				Die Dateierweiterung der Dateien im EUSurvey-Format lautet „.eus“.
			</p>
			<h2>
				Wie kopiere ich eine vorhandene Umfrage?
			</h2>
			<p>
				Gehen Sie zur Seite „Umfragen“:
			</p>
			<p>
				<ol>
					<li>
						Öffnen Sie die gewünschte Umfrage und klicken Sie auf das Symbol „Kopieren“.
					</li>
					<li>
						In dem Dialogfenster, das erscheint, können Sie die erforderlichen
						Einstellungen ändern.
					</li>
					<li>
						Klicken Sie auf „Erstellen“.
					</li>
					<li>
						Ihre Umfrage wird der Liste auf der Seite „Umfragen“ hinzugefügt ? Sie
						können mit der Bearbeitung beginnen.
					</li>
				</ol>
			</p>
			<h2>
				Wie entferne ich eine vorhandene Umfrage?
			</h2>
			<p>
				Gehen Sie zur Seite „Umfragen“:
			</p>
			<p>
				<ol>
					<li>
						Öffnen Sie die gewünschte Umfrage.
					<li>
					</li>
						Klicken Sie auf das Symbol „Löschen“.
					</li>
				</ol>
			</p>
			<p>
				Nachdem Sie diesen Vorgang bestätigt haben, wird Ihre Umfrage aus der Liste
				der Umfragen entfernt.
			</p>
			<p>
				<b><u>Achtung:</u></b>
				Wenn Sie eine Umfrage löschen, werden <b><u>alle Spuren</u></b> Ihrer
				entsprechenden Fragen und Ergebnisse aus dem EUSurvey-System gelöscht!    <u>Dieser Vorgang lässt sich nicht rückgängig machen</u>!
			</p>
			<h2>
				Wie erstelle ich mit EUSurvey einen Fragebogen gemäß der Richtlinien für
				barrierefreie Webinhalte (WCAG)?
			</h2>
			<p>
				Die Richtlinien für barrierefreie Webinhalte müssen beachtet werden, um
				Inhalte insbesondere für Menschen mit Behinderungen, aber auch über Geräte
				wie Mobiltelefone (durch entsprechende Software) zugänglich zu machen.
			</p>
			<p>
				Möchten Sie Ihre Umfrage gemäß dieser Richtlinien gestalten, folgen Sie
				bitte den Anweisungen
				<a
						href="https://circabc.europa.eu/d/a/workspace/SpacesStore/78b03213-5cf4-4aab-8e90-ada7e2eb1101/WCAG_tutorial%20.pdf"
						target="_blank"
						>
					in diesem Dokument
				</a>
				.
			</p>
			<h2>
				Was ist das „Motivations-Popup“ und wie wird es verwendet?
			</h2>
			<p>
				Das „Motivations-Popup“ ist ein Dialogfenster, welches dem Umfrageteilnehmer während des Ausfüllens
				des Formulars angezeigt wird. Es zeigt eine Nachricht an, um den Teilnehmer zu motivieren, seine Arbeit
				fortzusetzen. Diese Nachricht ist individuell gestaltbar, ebenso wie der Zeitpunkt, zu dem das
				Popup-Fenster angezeigt wird.
			</p>
			<p>
				Das „Motivations-Popup“/ „Motivation popup“ ist in den „Eigenschaften“ der Umfrage in dem Reiter
				„Aussehen“ verfügbar.
			</p>
			<figure>
				<img alt="Screenshot" src="${contextpath}/resources/images/documentation/motivation_popup_1_DE.png" style="width: 75%"/>
				<figcaption>Motivations-Popup in den „Eigenschaften“ der Umfrage</figcaption>
			</figure>
			<p>
				Sobald der Schalter aktiviert wurde, erscheinen die Optionen.
			</p>
			<p>
				Der „Trigger”, zur Anzeige des Popups, kann auf dem Fortschritt/ „progress” oder einem Zeitschalter/
				„timer” basieren.
			</p>
			<p>
				Der Fortschritt wird in Prozent angegeben, z.B. 50%. Das bedeutet, dass das „Motivations-Popup“
				angezeigt wird, sobald der Umfrageteilnehmer 50% der Fragen beantwortet hat.
			</p>
			<p>
				Falls die Option „timer” ausgewählt wurde, wird das „Motivations-Popup“ nach X Minuten angezeigt. X ist
				die Anzahl der Minuten, die im Feld Schwellenwert/ „Threshold value“ angegeben wurde.
			</p>
			<p>
				Schließlich kann der Text über das Feld „Motivationstext“/ „Motivation text“ angepasst werden.
			</p>
			<figure>
				<img alt="Screenshot" src="${contextpath}/resources/images/documentation/motivation_popup_2_DE.png" style="width: 75%"/>
				<figcaption>Optionen des Motivations-Popups</figcaption>
			</figure>
		<h1>
				Umfrage bearbeiten
			</h1>
			<h2>
				Wie starte ich den Editor?
			</h2>
			<p>
				Vergewissern Sie sich zunächst, dass Sie eine vorhandene Umfrage geöffnet
				haben &#8594;  gehen Sie zur Seite „Umfragen:
			</p>
			<p>
				<ol>
					<li>
						Klicken Sie bei der gewünschten Umfrage auf das Symbol „Öffnen“.
					</li>
					<li>
						Klicken Sie auf der Seite „Übersicht“ auf „Editor“ und beginnen Sie mit
						der Bearbeitung.
					</li>
				</ol>
			</p>
			<p>
				Denken Sie daran, Ihre Arbeit in regelmäßigen Abständen zu speichern.
			</p>
			<h2>
				Wie erstelle ich einen Fragebogen mit dem EUSurvey Editor?
			</h2>
			<p>
				Der Editor umfasst 5 Hauptbereiche:
			</p>
			<p>
				<b>i) Navigation:</b>
				Dieser Bereich bietet eine strukturierte Ansicht, in der alle Elemente des
				Fragebogens durch ihr entsprechendes Textlabel dargestellt sind.
			</p>
			<p>
				Wenn Sie im Bereich Navigation ein Element auswählen, wird dieses im
				Formular-Bereich blau markiert.
			</p>
			<p>
				<b>ii) Werkzeugkasten:</b>
				Dieser Bereich enthält alle Elemente, die Sie mit der
				Drag-und-Drop-Funktion oder durch Doppelklick in den Fragebogen einfügen
				können.
			</p>
			<p>
				<b>iii) Formular:</b>
				In diesem Bereich sehen Sie eine Vorschau des Fragebogens; Sie können hier
				Elemente hinzufügen und zur Bearbeitung auswählen.
			</p>
			<p>
				<b>iv) Elementeigenschaften:</b>
				Hier werden die Einstellungen für ausgewählte Elemente angezeigt.
			</p>
			<p>
				Sie können die Elemente hier bearbeiten, also z. B. den Text der Frage
				ändern, Hinweise für die Nutzer hinzufügen und Einstellungen nach Bedarf
				ändern.
			</p>
			<p>
				<b>v) Funktionsleiste:</b>
				Hier finden Sie alle grundlegenden Funktionen, die Sie bei der Erstellung
				des Fragebogens verwenden können.
			</p>
			<p>
				Detaillierte Informationen zur Benutzung des EUSurvey Editors, siehe den
				<a
						href="https://ec.europa.eu/eusurvey/resources/documents/Editor_Guide_DE.pdf"
						target="_blank"
						>
					EUSurvey Editor-Leitfaden
				</a>
				.
			</p>
			<h2>
				Wie füge ich Fragen hinzu oder entferne sie?
			</h2>
			<p>
				Um Ihrem Fragebogen neue Elemente hinzuzufügen oder vorhandene zu
				entfernen,
			</p>
			<p>
				&#8594;  öffnen Sie bitte zunächst den Editor.
			</p>
			<p>
				Hier finden Sie links den Werkzeugkasten mit den verfügbaren Elementen und
				in der Mitte das Formular, in das die Elemente eingefügt werden.
			</p>
			<p>
				Die Elemente enthalten Standardtexte und die Bezeichnung des Elements wird
				als Fragetext angezeigt.
			</p>
			<p>
				Um ein neues Element (Frage, Textfeld, Bild usw.) hinzuzufügen,
			</p>
			<p>
				&#8594;  wählen Sie ein Element aus dem Werkzeugkasten aus – per
				Drag-und-Drop-Funktion oder durch Doppelklick.
			</p>
			<p>
				Um ein Element aus dem Formular zu entfernen,
			</p>
			<p>
				&#8594;  wählen Sie das Element durch Anklicken aus und klicken Sie auf „Löschen“;
				sobald Sie den Vorgang bestätigt haben, wird das Element entfernt.
			</p>
			<p>
				Siehe auch
				<a
						href="#_Toc_4_2"
						>
					„Wie erstelle ich einen Fragebogen mit dem EUSurvey Editor?“
				</a>
			</p>
			<h2>
				Wie bearbeite ich einzelne Elemente in meinem Fragebogen?
			</h2>
			<p>
				Die zu <b>bearbeitenden Elemente können im Formular-Bereich ausgewählt</b>
				und <b>im Bereich Elementeigenschaften des Editors bearbeitet</b> werden –
				siehe
				<a
						href="_Toc_4_2"
						>
					„Wie erstelle ich einen Fragebogen mit dem EUSurvey Editor?“
				</a>
				).
			</p>
			<p>
				Klicken Sie auf ein Element im Formular-Bereich, um es zu auszuwählen.
			</p>
			<p>
				Ausgewählte Elemente erscheinen in Blau, wobei die jeweils verfügbaren
				Optionen im Bereich Elementeigenschaften angezeigt werden. Sie können die
				Elemente hier bearbeiten, also z. B. den Fragetext ändern, Hinweise für die
				Nutzer hinzufügen und Einstellungen nach Bedarf ändern.
			</p>
			<p>
				Textbearbeitung im Rich-Text-Editor:
			</p>
			<ol>
				<li>
					Text oder Stift-Symbol anklicken.
				</li>
				<li>
					Text ändern.
				</li>
				<li>
					„Anwenden“ anklicken, um die Änderungen im Formular-Bereich zu sehen.
				</li>
			</ol>
			<p>
				Standardmäßig werden im Bereich Elementeigenschaften alle grundlegenden
				Optionen angezeigt.
			</p>
			<p>
				Zur Anzeige weiterer Optionen klicken Sie auf „Erweitert“.
			</p>
			<p>
				Bei Matrix- und Textfragen können Sie auch die einzelnen
				Fragen/Antworten/Zeilen/Spalten des Elements durch Anklicken des
				entsprechenden Labeltexts auswählen. So können Sie z. B. einzelne Fragen
				eines Matrix- oder Tabellenelements auswählen und zu Pflichtfragen machen.
			</p>
			<h2>
				Wie kann ich Elemente kopieren?
			</h2>
			<p>
				Um Elemente in Ihr Formular zu kopieren:
			</p>
			<p>
				&#8594;  öffnen Sie bitte zunächst den Editor.
			</p>
			<ol>
				<li>
					Wählen Sie das Element/die Elemente aus.
				</li>
				<li>
					Klicken Sie auf „Kopieren“.
				</li>
				<li>
					Platzhalter wie oben beschrieben vom Werkzeugkasten-Bereich in den
					Formular-Bereich verschieben oder gewünschtes Element im
					Formular-Bereich auswählen und Symbol „Einfügen nach“ anklicken.
				</li>
			</ol>
			<p>
				Alle kopierten oder ausgeschnittenen Elemente werden im oberen
				Werkzeugkasten-Bereich durch ein Symbol dargestellt.
			</p>
			<p>
				&#8594;  mit der Drag-und-Drop-Funktion können Sie diese wieder in den Fragebogen
				einfügen.
			</p>
			<p>
				Zum Abbrechen:
			</p>
			<p>
				&#8594;  verwenden Sie Schaltfläche neben dem Element.
			</p>
			<p>
				Siehe auch
				<a
						href="_Toc_4_2"
						>
					„Wie erstelle ich einen Fragebogen mit dem EUSurvey Editor?“
				</a>
			</p>
			<h2>
				Wie füge ich meinen Fragen mögliche Antworten hinzu und wie entferne ich
				Antworten?
			</h2>
			<p>
				<ol>
					<li>
						Durch Anklicken des Plus- oder Minus-Zeichens im Bereich
						Elementeigenschaften können Sie Antworten hinzufügen bzw. entfernen.
					</li>
					<li>
						Durch Anklicken des Stift-Symbols neben „Mögliche Antworten“ können Sie
						die Antworten bearbeiten.
					</li>
					<li>
						Sie können die Antworten im Rich-Text-Editor bearbeiten.
					</li>
				</ol>
			</p>
			<p>
				Siehe auch
				<a
						href="_Toc_4_2"
						>
					„Wie erstelle ich einen Fragebogen mit dem EUSurvey Editor?“
				</a>
			</p>
			<h2>
				Wie mache ich eine Frage zu einer Pflichtfrage?
			</h2>
			<p>
				<ol>
					<li>
						Wählen Sie im Editor ein Element aus, das ein Pflichtfeld werden soll.
					</li>
					<li>
						Gehen Sie dann zum Bereich Elementeigenschaften.
					</li>
					<li>
						Kreuzen Sie dort das Kästchen „Obligatorisch“ an.
					</li>
				</ol>
			</p>
			<p>
				Pflichtfragen werden links vom Fragetext mit einem roten Sternchen
				versehen.
			</p>
			<h2>
				Wie kann ich Elemente innerhalb des Fragebogens verschieben?
			</h2>
			<p>
				Sie können Elemente in Ihrem Fragebogen wie folgt an eine andere Position
				verschieben:
			</p>
			<p>
				&#8594;  <b> </b>per Drag-und-Drop:
				<br/>
				Wählen Sie das Element im Formular-Bereich aus und ziehen Sie es an die
				gewünschte Position im Fragebogen.
			</p>
			<p>
				&#8594;  Schaltflächen verschieben:
				<br/>
				Wählen Sie das zu verschiebende Element aus und klicken Sie auf den
				entsprechenden Pfeil in der Funktionsleiste oben im Formular-Bereich, um es
				nach oben oder unten zu verschieben.
			</p>
			<p>
				&#8594;  Ausschneiden und Einfügen:
				<br/>
				Schneiden Sie das zu verschiebende Element aus und bringen Sie es mit der
				Drag-und-Drop-Funktion in die gewünschte Position.
			</p>
			
			<h2>Sichtbarkeit (abhängige Fragen)</h2>
			<p>
				Mit dieser Funktion können Sie Fragen abhängig von den vorherigen Antworten des Teilnehmers entweder anzeigen oder ausblenden.
			</p>
			<p>
				Standardmäßig sind alle Elemente immer sichtbar, so dass jeder die Frage beim Beantworten der Umfrage sieht.
			</p>
			<p>
				Die Sichtbarkeitsfunktion bietet 2 Möglichkeiten, die Anzeige von Folgefragen auszulösen:
				<ul>
					<li>'ODER': der Benutzer muss mindestens eines der ausgewählten Elemente auswählen, damit die Frage angezeigt wird;</li>
					<li>'UND': der Benutzer muss alle ausgewählten Elemente auswählen, damit die Frage angezeigt wird.</li>
				</ul>			
			</p>
			<p>
				Nachfolgend die Schritte zum Erstellen einer abhängigen Frage:
				<ol>
					<li>Gehen Sie zu der Frage, die Sie ausblenden/anzeigen möchten - klicken Sie auf Eigenschaften.</li>
					<li>Klicken Sie auf das Stiftsymbol neben der Sichtbarkeitsfunktion.</li>
					<li>Wählen Sie die anzuwendende Option: 'ODER' (Standard) oder 'UND'.</li>
					<li>Verwenden Sie die Kontrollkästchen, um die Antwort(en) auszuwählen, die die Anzeige der Frage auslösen soll(en).</li>
					<li>Klicken Sie auf "Übernehmen".</li>
				</ol>
			</p>
			<p>
			Wenn die Funktion verwendet wird, werden Pfeile neben den ausgewählten Elementen angezeigt, um die Sichtbarkeitseinstellungen im Formularbereich (in dem der Fragebogen dargestellt ist) anzuzeigen. Antworten, die eine Frage sichtbar machen, sind mit einem nach unten zeigenden Pfeil markiert. Fragen, die Sie entweder anzeigen oder ausblenden können, sind mit einem nach oben zeigenden Pfeil markiert.
			</p>
		
			<p>
			Wenn Sie den Mauszeiger über die Pfeile (oder IDs im Bedienfeld "Elementeigenschaften") bewegen, werden die entsprechenden Elemente im Formularbereich und im Navigationsbereich hervorgehoben.</p>
			<p>
				<b>Massenbearbeitung:</b> Wenn Sie mehrere Fragen auswählen, können Sie die Sichtbarkeitseinstellungen für alle Fragen gleichzeitig bearbeiten.
			</p>
			
			<h2>
				Kann ich die Reihenfolge der Antworten auf eine Frage mit mehreren
				Antworten, aus denen nur eine oder aber mehrere ausgewählt werden können,
				ändern?
			</h2>
			<p>
				Wenn Sie eine solche Frage erstellen, können Sie die Antworten auf drei
				verschiedene Arten sortieren lassen:
			</p>
			<ul>
				<li>
					ursprüngliche Reihenfolge
				</li>
				<li>
					alphabetische Reihenfolge
				</li>
				<li>
					zufällige Reihenfolge
				</li>
			</ul>
			<p>
				Ursprüngliche Reihenfolge: Die Antworten werden in der ursprünglichen
				Reihenfolge angezeigt.
			</p>
			<p>
				Alphabetische Reihenfolge: Wählen Sie diese Option, wenn die Antworten
				alphabetisch sortiert angezeigt werden sollen.
			</p>
			<p>
				Zufällige Reihenfolge: Wählen Sie diese Option, wenn die Antworten nach dem
				Zufallsprinzip angezeigt werden sollen.
			</p>
			<h2>
				Wie erlaube ich anderen Nutzern, meine Umfrage zu bearbeiten?
			</h2>
			<p>
				<ol>
					<li>
						Öffnen Sie Ihre Umfrage und die Seite „Rechte“.
					</li>
					<li>
						Klicken Sie auf „Nutzer hinzufügen“ oder „Abteilung hinzufügen“.
					</li>
					<li>
						Es erscheint ein Pop-up mit einem Assistenten, der Ihnen Schritt für
						Schritt erklärt, wie Sie Nutzer Hinzufügen können.
					</li>
					<li>
						Sie können den Nutzern bestimmte Zugangsrechte zuweisen – klicken Sie
						einfach auf die Farbe, um die Rechte zu ändern.
						
						<ul>
							<li>
								Grün: Lese- und Schreibzugriff
							</li>
							<li>
								Gelb: Lesezugriff
							</li>
							<li>
								Rot: Kein Zugriff
							</li>
						</ul>
					</li>
				</ol>
			</p>			
			<p>
				Nutzer, die Sie hinzugefügt haben, sehen Ihre Umfrage beim nächsten
				Anmelden bei bei EUSurvey automatisch in ihrer eigenen Umfragenliste.
			</p>
			<p>
				Externe Umfrage-Eigentümer oder -Organisatoren können auf der Registerkarte
				„Rechte“/auf der Schaltfläche „Nutzer hinzufügen“ keine EU-Felder sehen.
				Sie können diesen Personen daher nicht direkt Zugang gewähren.
			</p>
			<p>
				<a href="https://ec.europa.eu/eusurvey/home/support">
					Kontaktieren Sie uns
				</a>
				‚ um Zugang für externe Nutzer zu beantragen.
			</p>
			<p>
				Weitere Informationen erhalten Sie unter
				<a
						href="#_Toc_9_8"
						>
					„Wie erhalten andere Nutzer Zugang zu meiner Umfrage?“
				</a>
				.
			</p>
			<h2>
				Welche Sprachen werden von der Anwendung unterstützt?
			</h2>
			<p>
				Sprachen, die in „UTF-8 in 3 Byte“ kodiert werden können, eignen sich zur
				Erstellung einer Umfrage.
			</p>
			<h2>
				Warum UTF-8? Welche Zeichensätze sollte ich verwenden?
			</h2>
			<p>
				Die Umfrageteilnehmer aus Ihrer Zielgruppe können die Umfrage einfacher
				anzeigen lassen, wenn der verwendete Zeichensatz bereits in ihrem Browser
				installiert ist. UTF-8 ist die am häufigsten verwendete Kodierung für
				HTML-Seiten. Wird ein nicht unterstützter Zeichensatz gewählt, kann dies
				hingegen die Wiedergabe einer PDF-Exportdatei beeinträchtigen.
			</p>
			<p>
				Wir empfehlen die Verwendung der folgenden    <b>unterstützten Zeichensätze</b>:
			</p>
			<ul>
				<li>
					Freesans
					<a
							href="https://circabc.europa.eu/d/a/workspace/SpacesStore/602784e1-bb06-4b0d-a474-eae77dbe2d11/EUSurvey-SupportedCharacterSet(freesans).txt"
							target="_blank"
							>
						(https://circabc.europa.eu/sd/a/36f72861-fc6e-4fe1-87d6-0a8e1c6fa161/EUSurvey-SupportedCharacterSet(freesans).txt)
					</a>
				</li>
				<li>
					Freemono
					<a
							href="https://circabc.europa.eu/d/a/workspace/SpacesStore/dfc640e9-56ac-4d25-8361-4b07dbbd0579/EUSurvey-SupportedCharacterSet(freemono).txt"
							target="_blank"
							>
						(https://circabc.europa.eu/sd/a/55ce0f35-b3cc-4712-80bf-af42800a278f/EUSurvey-SupportedCharacterSet(freemono).txt)
					</a>
				</li>
				<li>
					Freeserif
					<a
							href="https://circabc.europa.eu/d/a/workspace/SpacesStore/5b98b11a-f306-4d97-aab3-ec1c7a24965f/EUSurvey-SupportedCharacterSet(freeserif).txt"
							target="_blank"
							>
						(https://circabc.europa.eu/sd/a/29cd78bb-9eeb-40b1-a22f-b54700750537/EUSurvey-SupportedCharacterSet(freeserif).txt)
					</a>
				</li>
				<li>
					Allgemein unterstützter Zeichensatz
					<a
							href="https://circabc.europa.eu/d/a/workspace/SpacesStore/621396c0-92d3-49a3-acd0-546b0c1a170b/EUSurvey-SupportedCharacterSet(common).txt"
							target="_blank"
							>
						(https://circabc.europa.eu/sd/a/1eb30efd-e2d8-4c3b-9f55-533bb903f7d0/EUSurvey-SupportedCharacterSet(common).txt)
					</a>
				</li>
			</ul>
			<p>
				<b>„Freesans“ ist der vorinstallierte Zeichensatz</b>
			</p>
			<p>
				Im Zweifelsfall sollten Sie die fertige Umfrage als PDF-Datei exportieren,
				um zu überprüfen, ob sie korrekt wiedergegeben wird.
			</p>
			<p>
				Beachten Sie dabei allerdings, dass manche Antworten in der PDF-Datei
				möglicherweise nicht korrekt wiedergegeben werden. Die Befragten können
				jede beliebige Schriftart auswählen, die von der Anwendung unterstützt
				wird.
			</p>
			<p>
				Auch wenn die Anwendung die von den Befragten gewählten Zeichen nicht
				wiedergeben kann, werden sie in der Datenbank von EUSurvey korrekt
				gespeichert. Sie können somit von der Ergebnisseite aus exportiert werden.
			</p>
			<h2>
				Indikator für Komplexität  
			</h2>
			<p>
				Wenn Sie Ihre Umfrage kurz und einfach halten, erleichtert dies den Teilnehmern das Ausfüllen der Umfrage und führt zu einer besseren Benutzererfahrung. Natürlich müssen Sie manchmal eine Verzweigungslogik mithilfe von Abhängigkeiten hinzufügen (d. h. Fragen, die abhängig von den zuvor gegebenen Antworten ausgeblendet/angezeigt werden). Das ist in Ordnung, aber bedenken Sie bitte, dass das Hinzufügen von zu vielen Elementen oder Abhängigkeiten Ihre Umfrage zu "komplex" macht. Dies kann dazu führen, dass das System für die Teilnehmer beim Ausfüllen Ihres Fragebogens langsamer wird.
			</p>
			<p>
				Aus diesem Grund befindet sich in der oberen rechten Ecke des Formular-Editors ein kleiner Indikator:<br />
				<img alt="Screenshot" src="${contextpath}/resources/images/documentation/complexityDE.png" />
			</p>
			<p>
				Ihre Umfrage könnte aus verschiedenen Gründen einen hohen Komplexitätsgrad aufweisen: 
				<ul>
					<li>zu viele Abhängigkeiten</li>
					<li>zu viele kaskadierende Abhängigkeiten</li>
					<li>zu viele Tabellen-/Matrixelemente</li>
				</ul>
			</p>
			<p>
				Weitere Informationen finden Sie in unserem <a href="https://circabc.europa.eu/sd/d/281e626e-279e-45df-8581-1f6e04feff51/BestPractices-EUSurvey.pdf">Leitfaden für bewährte Praktiken</a>, und zögern Sie nicht, sich an das <a href="${contextpath}/home/support?assistance=1">EUSurvey-Supportteam</a> zu wenden, um bei Bedarf Unterstützung bei der Neugestaltung Ihres Formulars zu erhalten.
			</p>
			<h2>
				Was ist die „Kennnummer des Beitrags“?
			</h2>
			<p>
				Die  „Kennnummer des Beitrags“ (Beitrags-ID) ist ein Code, der als eindeutiger Bezeichner für einen Beitrag verwendet wird.
			</p>
			<p>
				Sie kann von Umfragemanagern verwendet werden, um einen Beitrag auf der Ergebnisseite wiederzufinden. Sie kann auch von einem Umfrageteilnehmer verwendet werden, um seinen Beitrag einzureichen und ihn später abzurufen.
			</p>
			<h2>
				Wie kann ich einen Beitrag in den Ergebnissen mit Hilfe einer „Kennnummer des Beitrags“ finden?
			</h2>
			<p>
				Die „Kennnummer des Beitrags“ (Beitrags-ID) kann von den Eigentümern der Umfrage verwendet werden, um einen Beitrag unter allen Ergebnissen zu finden:
				<ol>
					<li>
						Öffnen Sie Ihre Umfrage
					</li>
					<li>
						Gehen Sie zur Registerkarte „Ergebnisse“
					</li>
					<li>
						Klicken Sie auf die Schaltfläche „Einstellungen“
					</li>
					<li>
						Wählen Sie „Kennnummer des Beitrags“ in beiden Spalten aus
					</li>
					<li>
						Gehen Sie zum Filter „Kennnummer des Beitrags“
					</li>
					<li>
						Geben Sie die Kennnummer ein und klicken Sie auf Enter
					</li>
				</ol>
			</p>
			<h2>Randomisierungsfunktion</h2>
			<p>
				Wenn Sie einen <strong>Abschnitt</strong> der ersten Stufe verwenden, haben Sie die Möglichkeit, die darunter liegenden Fragen/Elemente in ihrer ursprünglichen Reihenfolge zu belassen oder ihre Position zu randomisieren. Die Zufallsanordnung kann in den Abschnittseigenschaften direkt neben "Ordnen nach" ausgewählt werden.
			</p>
			<img alt="Screenshot" src="${contextpath}/resources/images/documentation/randomization_DE.png" style="margin-bottom: 1em" />
			<p>Bitte beachten Sie auch die folgenden Punkte:</p>
			<ul>
				<li>Alle Umfrageelemente (einschließlich Bilder und statischer Text) werden zufällig angeordnet.</li>
				<li>Wenn eine Frage eine Sichtbarkeitsänderung auslöst oder durch eine Sichtbarkeitsänderung (nicht) sichtbar wird, wird ihre Position nicht verändert. Sie wird immer vor den anderen "randomisierten" Elementen angezeigt.</li>
				<li>Unterabschnitte und ihre Fragen (Stufe 2 und 3) werden ebenfalls randomisiert, aber ihre Reihenfolge innerhalb des Abschnitts der Stufe 1, zu dem sie gehören, wird beibehalten. Das bedeutet, dass die Fragen eines Unterabschnitts ebenfalls randomisiert werden, wenn der Abschnitt der Stufe 1 randomisiert wird.</li>
				<li>Die PDF-Version der Umfrage (Funktion "PDF-Version herunterladen") zeigt die Fragen immer in der ursprünglichen Reihenfolge.</li>
				<li>PDFs von Beiträgen zeigen die Fragen immer in der ursprünglichen Reihenfolge.</li>
				<li>Wenn die automatische Nummerierung der Abschnitte/Fragen gleichzeitig mit der Randomisierungsfunktion aktiviert ist, werden auch die Fragennummern zusammen mit den Fragen randomisiert.
					<div><img alt="Screenshot" src="${contextpath}/resources/images/documentation/randomization_sections_DE.png" style="max-width: 100%;" /></div>
				</li>
			</ul>
			<p>Es gibt noch einen weiteren Punkt, der speziell für DELPHI-Umfragen gilt:</p>
			<ul>
				<li>Auf der DELPHI-Startseite werden die Fragen in ihrer ursprünglichen Reihenfolge angezeigt.</li>
			</ul>
			<h2>Formel</h2>
			<p>
				Der Fragetyp "Formel" berechnet und zeigt einen Wert auf der Grundlage der vom Teilnehmer eingegebenen Daten an.
				Damit können Sie z. B. eine Gesamtsumme oder einen Durchschnitt anzeigen und Ihrer Umfrage einen interaktiven Aspekt hinzufügen.
			</p>
			<img alt="Screenshot" src="${contextpath}/resources/images/documentation/formular_field_1_DE.png" style="margin-bottom: 1em;" />
			<p>Die wichtigsten Punkte des Frageelements:</p>
			<ul>
				<li>
					Für verschiedene Anwendungsfälle stehen gebrauchsfertige Funktionen zur Verfügung:<br>
					<img alt="Screenshot" src="${contextpath}/resources/images/documentation/formular_field_2_DE.png" style="margin-bottom: 1em" />
				</li>
				<li>
					Die Benutzer können eigene Formeln in das "Formel"-Feld eingeben:<br>
					<img alt="Screenshot" src="${contextpath}/resources/images/documentation/formular_field_3_DE.png" style="margin-bottom: 1em" />
				</li>
			</ul>
			<p>Die Element-IDs werden zum Zusammenstellen der Formel verwendet. Die Liste der IDs wird angezeigt, damit Sie die gewünschten IDs direkt auswählen können.
				Sie können die IDs auch direkt in das Eingabefeld eingeben.</p>
			<img alt="Screenshot" src="${contextpath}/resources/images/documentation/formular_field_4_DE.png" style="margin-bottom: 1em" />
			<p>Bitte beachten Sie, dass die Element-IDs im Abschnitt "Erweitert" der Elementeigenschaften sichtbar sind.</p>
			<img alt="Screenshot" src="${contextpath}/resources/images/documentation/formular_field_5_DE.png" style="margin-bottom: 1em" />
			<p><span style="text-decoration: underline;">Beispiel 1:</span><br>
				Im folgenden Beispiel ergibt sich das Feld "Formel" aus der Summe der beiden obigen Nummer-Fragen ("Nummernschieber").
				Sobald der Umfrageteilnehmer den zweiten Wert (in unserem Beispiel 5) eingegeben hat, wird die Summe berechnet und in Echtzeit angezeigt.
			</p>
			<img alt="Screenshot" src="${contextpath}/resources/images/documentation/formular_field_6_DE.png" style="margin-bottom: 1em" />
			<p><span style="text-decoration: underline;">Beispiel 2:</span><br>
				Im folgenden Beispiel können Sie Ihren Umfrageteilnehmer auffordern, entweder seine monatliche oder seine jährliche Miete einzugeben.
				Die Anwendung berechnet automatisch die jeweils andere Miete. Beide Felder bleiben für den Benutzer editierbar.
			</p>
			<img alt="Screenshot" src="${contextpath}/resources/images/documentation/formular_field_7_DE.png" style="margin-bottom: 1em" />
			<p>
				<span style="font-style: italic;">Der Umfrageteilnehmer hat 500 eingegeben und 6000 wurde berechnet.</span>
			</p>
			<p><span style="text-decoration: underline;">Beispiel 3 "Schreibgeschützt"-Option:</span></p>
			<img alt="Screenshot" src="${contextpath}/resources/images/documentation/formular_field_8_DE.png" style="margin-bottom: 1em" />
			<p>Im folgenden Beispiel wird in Zeile 5 die Summe für jede Spalte angezeigt.
				Diese Felder verwenden die Option "Schreibgeschützt", so dass die Summe vom Benutzer nicht geändert werden kann.</p>
			<img alt="Screenshot" src="${contextpath}/resources/images/documentation/formular_field_9_DE.png" style="margin-bottom: 1em" />
			<h2>Komplexe Tabelle</h2>
			<p>Die "Komplexe Tabelle" ist ein tabellenähnliches Umfrageelement, mit dem andere Umfrageelemente auf komplexe Weise miteinander kombiniert werden können.
				Sie ermöglicht die visuelle Verknüpfung verschiedener Fragen und die Gestaltung von Textpassagen (z.B. die Darstellung von Text in mehreren Spalten).</p>
			<img alt="screenshot complex_table element" src="${contextpath}/resources/images/documentation/complex_table_1_DE.png" style="margin-bottom: 1em" />
			<p><b>Wie konfiguriere ich eine komplexe Tabelle?</b></p>
			<p>Fügen Sie im "Editor" ein Element "Komplexe Tabelle" hinzu und wählen Sie es aus.
				Die Elementeigenschaften bieten die gleichen Einstellungsmöglichkeiten wie die reguläre "Tabelle".</p>
			<img alt="screenshot complex_table element" src="${contextpath}/resources/images/documentation/complex_table_2_DE.png" style="margin-bottom: 1em" />
			<p><b>Wie konfiguriere ich einzelne Zellen einer komplexen Tabelle?</b></p>
			<p>Wählen Sie eine einzelne Zelle im Vorschaubereich des Editors aus.</p>
			<p>Sie können unterschiedliche Fragetypen für verschiedene Zellen festlegen.
				Diese "Zelltypen" entsprechen denen der regulären Standardfragen.</p>
			<img alt="screenshot complex_table element" src="${contextpath}/resources/images/documentation/complex_table_3_DE.png" style="margin-bottom: 1em" />
			<p>"Statischer Text" kann in Spalten angezeigt werden, indem die Eigenschaft "Zellen über Spalten hinweg verbinden" verwendet wird.
				Beachten Sie, dass diese Eigenschaft nur auf aufeinanderfolgende Zellen in einer Tabellenzeile angewendet werden kann.
				Wenn diese Zellen bereits konfiguriert waren, wird der Inhalt entfernt.</p>
			<p>Die anderen verfügbaren Zellentypen ("Freier Text", "Formel", "Einfachauswahl", "Mehrfachauswahl", "Zahl")
				sind identisch mit ihren regulären Gegenstücken außerhalb einer komplexen Tabelle und können auf die gleiche Weise bearbeitet werden.</p>
			<p><b>Wie lässt sich Text in Spalten anzeigen?</b></p>
			<p>Textpassagen können in Spalten angezeigt werden, indem der Text auf mehrere Zellen der komplexen Tabelle aufgeteilt wird.
				Setzen Sie den Zellentyp der entsprechenden Zellen auf "Statischer Text".
				Unter "Elementeigenschaften > Text" kann nun für jede Zelle der gewünschte Text separat eingegeben werden.</p>
			<p><b>Wie können Zellen so konfiguriert werden, dass sie nicht bearbeitet werden können?</b></p>
			<p>Die Zelltypen der komplexen Tabelle, die nicht bearbeitet werden können, sind "Statischer Text" und "leer".</p>
			<p>Für andere Zellentypen können mit "Elementeigenschaften > Schreibgeschützt" direkte Benutzereingaben verhindert werden.</p>
			<p><b>Wie kann eine Zelle so konfiguriert werden, dass sich ihr Text über mehrere Spalten erstreckt?</b></p>
			<p>Text in komplexen Tabellen kann so konfiguriert werden, dass er sich über mehrere Spalten erstreckt.
				Wählen Sie eine Zelle mit dem "Zellentyp > Statischer Text" aus.
				Unter Elementeigenschaften ist die Option "Zellen über Spalten hinweg verbinden" dafür verantwortlich, wie viele Spalten zusammengeführt werden.
				Beachten Sie, dass diese Funktion nachfolgende Zellen verdeckt und somit den Inhalt dieser Zellen entfernt, falls diese bereits konfiguriert waren.</p>
			<p><b>Wie kann ich eine Frage in einer Zelle einer komplexen Tabelle löschen?</b></p>
			<p>Der Inhalt einzelner Zellen einer komplexen Tabelle kann nicht mithilfe der Löschfunktion des Editors entfernt werden.
				Stattdessen muss der Zellentyp der Zelle auf "leer" zurückgesetzt werden.</p>
			<p><b>Wie kann ich ein Diagramm in der Statistikansicht anzeigen lassen?</b></p>
			<p>Damit die entsprechenden Diagramme in der Statistikansicht angezeigt werden, müssen für die Zellentypen "Zahl" und "Formel" Werte für "min." und "max." festgelegt werden.
				Insgesamt darf es maximal 10 Werte geben.</p>
			<img alt="screenshot complex_table element" src="${contextpath}/resources/images/documentation/complex_table_4_DE.png" style="margin-bottom: 1em; max-width: 920px" />
			<h1>
				Sicherheit der Umfrage
			</h1>
			<h2>
				Wie schränke ich den Zugriff auf meine Umfrage ein?
			</h2>
			<p>
				Standardmäßig sind Umfragen direkt nach ihrer Veröffentlichung öffentlich
				verfügbar.
			</p>
			<p>
				Wenn nur bevorrechtigte Nutzer Zugang zur Umfrage haben sollen:
			</p>
			<p>
				&#8594;  wählen Sie unter in den „Sicherheitseinstellungen“ auf der Seite
				„Eigenschaften“ die Option „<b>Geschützt</b>“ aus.
			</p>
			<p>
				Um dann den bevorrechtigten Nutzern Zugang zu gewähren, haben Sie die
				folgenden Möglichkeiten:
			</p>
			<ul>
				<li>
					Sie können einzelne Befragte über EUSurvey einladen (siehe „
					<a
							href="#_Toc_13_0"
							>
						Teilnehmer einladen
					</a>
					“). Jeder Befragte erhält dann eine eigene E-Mail mit individuellem
					Zugangslink. ODER
				</li>
				<li>
					Sie können Ihre Umfrage über EU Login schützen. Hierfür aktivieren Sie
					auf der Seite „Eigenschaften“ die Optionen „Umfrage schützen“ und „Via
					EU Login schützen“. EU-Bedienstete können zwischen folgenden
					Möglichkeiten wählen:
					
					<ul>
						<li>
							Sie können allen Nutzern mit einem EU Login-Konto (EU-Bediensteten und
							Nutzern mit externen EU Login-Konten) Zugang zu Ihrer Umfrage gewähren,
							oder
						</li>
						<li>
							nur EU-Bediensteten Zugang gewähren. ODER
						</li>					
					</ul>					
					
				</li>			
				<li>
					Sie richten ein Passwort ein. Es ist dann ein und dasselbe Passwort für
					alle Befragte, denen Sie den Umfragelink und das globale Passwort
					senden (siehe
					<a
							href="#_Toc_5_2"
							>
						„Wie lege ich ein Passwort für meine Umfrage fest?“
					</a>
					).
				</li>
			</ul>
			</ul>
		
			<h2>
				Wie lege ich ein Passwort für meine Umfrage fest?
			</h2>
			<p>
				Gehen Sie unter „Eigenschaften“ zur Option „Passwortgeschützt“.
			</p>
			<p>
				Wie Sie einzelne Kontakte dazu einzuladen können, auf Ihre geschützte
				Umfrage zuzugreifen, erfahren Sie im Abschnitt
				<a href="#_Toc_13_0">
					„Teilnehmer einladen“
				</a>
				.
			</p>
			<h2>
				Wie sorge ich dafür, dass einzelne Nutzer keine übermäßig große Zahl von
				Antworten einsenden können?
			</h2>
			<p>
				Hierfür aktivieren Sie auf der Seite „Eigenschaften“ die Optionen „Umfrage
				schützen“ und „Via EU Login schützen“.
			</p>
			<p>
				Legen Sie unter der Option „Beiträge pro Nutzer“ die gewünschte Anzahl
				fest.
			</p>
						
			<h2>Wie kann die Anzahl der Beiträge einer Umfrage begrenzt werden?</h2>
			<p>Sie können die Anzahl der Beiträge, die eine Umfrage akzeptieren kann, unter Eigenschaften >> Erweitert ändern. Standardmäßig gibt es keine Begrenzung der Anzahl der Beiträge, aber wenn ein Limit gesetzt wird, ist die Umfrage nicht mehr zugänglich, sobald sie erreicht ist. Dies kann z.B. für Formulare zur Anmeldung von Veranstaltungen genutzt werden.</p>
						
			<h2>
				Wie verhindere ich, dass Bots mehrfach Antworten übermitteln?
			</h2>
			<p>
				Automatische Scripts können das Ergebnis einer Umfrage durch die Einsendung
				einer hohen Anzahl von Antworten verfälschen. Um das zu verhindern, fordern
				Sie die Befragten auf, vor dem Absenden ihrer Antwort eine Sicherheitsfrage
				(    <a href="https://de.wikipedia.org/wiki/Captcha" target="_blank">CAPTCHA</a>
				) zu beantworten.
			</p>
			<p>
				Diese Frage kann unter „Eigenschaften“ im Abschnitt „Sicherheit“ aktiviert
				oder deaktiviert werden.
			</p>
			<p>
				Hinweis: Auch wenn sich ein Betrug so nicht vollständig ausschließen lässt,
				können wiederholte Versuche einer Verfälschung von Umfrageergebnissen auf
				diese Weise erschwert werden.
			</p>
			<h2>
				Kann ich den Umfrageteilnehmern die Möglichkeit geben, nach der Übermittlung auf ihren Beitrag zuzugreifen?
			</h2>
			<p>
				Ja. Klicken Sie unter der Registerkarte "Eigenschaften" auf "Sicherheit" und aktivieren Sie dann die Option "Ermöglicht Teilnehmern, ihren Beitrag zu ändern".
			</p>
			<p>
				Um ihren Beitrag nach der Übermittlung zu bearbeiten/ändern, können die Teilnehmer auf diese Seite gehen: <a href="https://ec.europa.eu/eusurvey/home/editcontribution" target="_blank">https://ec.europa.eu/eusurvey/home/editcontribution</a>
			</p>
			<p>
				Ihre Umfrageteilnehmer müssen ihre Beitrags-ID kennen. Diese ID wird ihnen bei der Übermittlung ihrer Antworten auf der Bestätigungsseite mitgeteilt.
			</p>
			<p>
				Bitte beachten Sie, dass der Link "Beitrag bearbeiten" auf der EUSurvey-Startseite zu finden ist: <a href="https://ec.europa.eu/eusurvey/home/welcome" target="_blank">https://ec.europa.eu/eusurvey/home/welcome</a>
			</p>
			<h2>
				Wie kann ich den Teilnehmern erlauben, ihren Beitrag auszudrucken oder herunterzuladen?
			</h2>
			<p>
				Wenn diese Funktion aktiviert ist, können die Teilnehmer ihre Antworten im PDF-Format speichern.
			</p>
			<p>
				Um diese Funktion zu aktivieren, folgen Sie den nachstehenden Schritten:
				<ol>
					<li>
						Öffnen Sie Ihre Umfrage
					</li>
					<li>
						Gehen Sie auf die Registerkarte „Eigenschaften“
					</li>
					<li>
						Gehen Sie auf die Registerkarte „Sicherheit“
					</li>
					<li>
						Aktivieren Sie die Option „Ermöglicht Teilnehmern, ihren Beitrag auszudrucken und als PDF-Datei zu erhalten“.
					</li>
				</ol>
				<img alt="Screenshot" src="${contextpath}/resources/images/documentation/printDE.png" />
			</p>
			<h2>
				Wie kann ich den Teilnehmern erlauben, ihren Beitrag zu ändern (zu bearbeiten)?
			</h2>
			<p>
				Wenn diese Funktion aktiviert ist, können die Teilnehmer ihre Antworten nach dem Absenden ändern/bearbeiten.
			</p>
			<p>
				So aktivieren Sie diese Funktion:
				<ol>
					<li>
						Öffnen Sie Ihre Umfrage
					</li>
					<li>
						Gehen Sie auf die Registerkarte „Eigenschaften“
					</li>
					<li>
						Gehen Sie auf die Registerkarte „Sicherheit“
					</li>
					<li>
						Aktivieren Sie die Option „Ermöglicht Teilnehmern, ihren Beitrag zu ändern“
					</li>
				</ol>
			<img alt="Screenshot" src="${contextpath}/resources/images/documentation/changecontributionDE.png" />
			</p>
			<h1>
				Umfrage testen
			</h1>
			<h2>
				Kann ich meine Umfrage testen und mir ansehen, wie sie nach der
				Veröffentlichung aussehen wird?
			</h2>
			<p>
				Ja. Öffnen Sie die Umfrage in EUSurvey und klicken Sie auf „Test“.
			</p>
			<p>
				Dann wird der aktuelle Entwurf Ihrer Umfrage angezeigt und Sie können jedes
				Element des veröffentlichten Fragebogens testen.
			</p>
			<p>
				Sie können den Test als Entwurf speichern oder direkt als Antwort
				abschicken.
			</p>
			<h2>
				Wie können meine Kollegen meine Umfrage vor der Veröffentlichung testen?
			</h2>
			<p>
				Um Ihren Kollegen Zugang zu der „Test-Seite“ Ihrer Umfrage zu gewähren:
			</p>
			<p>
				<ol>
					<li>
						Öffnen Sie Ihre Umfrage in EUSurvey.
					</li>
					<li>
						Gehen Sie zur Registerkarte „Rechte“ und klicken Sie auf „Nutzer
						hinzufügen“ oder „Abteilung hinzufügen“.
					</li>
					<li>
						Ein Assistent führt Sie Schritt für Schritt durch den Prozess zum
						Hinzufügen von Kollegen.
					</li>
				</ol>
			</p>
			<p>
				Zur Vergabe der Zugriffsrechte für das Testen
			</p>
			<p>
				&#8594;  stellen Sie die Farbe bei „Zugang zur Formularvorschau“ auf grün um.
				Klicken Sie hierfür einfach auf den Farbpunkt, um die Rechte zu ändern.
			</p>
			<p>
				Die hinzugefügten Nutzer sehen die Umfrage dann nach dem Anmelden
				automatisch auf ihrer „Umfragen“-Seite.
			</p>
			<p>
				Weitere Informationen hierzu siehe:
				<a
						href="#_Toc_9_8"
						>
					„Wie erhalten andere Nutzer Zugang zu meiner Umfrage?“
				</a>
				.
			</p>
			<p>
				Externe Umfrage-Eigentümer oder -Organisatoren können auf der Registerkarte
				„Rechte“/auf der Schaltfläche „Nutzer hinzufügen“ keine EU-Felder sehen.
				Sie können diesen Personen daher nicht direkt Zugang gewähren.
			</p>
			<p>
				<a href="https://ec.europa.eu/eusurvey/home/support">
					Kontaktieren Sie uns
				</a>
				‚ um Rechte für externe Nutzer zu beantragen.
			</p>
			<h1>
				Übersetzungen
			</h1>
			<h2>
				Wie übersetze ich eine Umfrage?				
			</h2>
			<p>
				Wichtig: Schließen Sie zunächst die Bearbeitungs- und Testphase Ihrer
				Umfrage ab, bevor Sie mit dem Übersetzungsschritt beginnen!
			</p>
			<p>
				<ol>
					<li>
						Öffnen Sie Ihre Umfrage und gehen Sie zur Seite „Übersetzungen“.
					</li>
					<li>
						Klicken Sie auf „Neue Übersetzung hinzufügen“.
					</li>
					<li>
						Wählen Sie aus der Liste der unterstützten Sprachen die gewünschte
						Sprache aus.
					</li>
					<li>
						Ist die gewünschte Sprache nicht in der Liste aufgeführt, wählen Sie
						„Sonstige“ aus und geben den entsprechenden gültigen Sprachcode nach ISO
						639-1 an.
					</li>
					<li>
						Klicken Sie auf „OK“, um das leere Übersetzungsformular zu Ihrer Umfrage
						hinzuzufügen.
					</li>
				</ol>
			</p>
			<p>
				Lesen Sie auch „
				<a
						href="#_Toc_7_3"
						>
					Kann ich eine vorhandene Übersetzung online bearbeiten?
				</a>
				“. Sie erfahren dort, wie Sie Ihrer neu angelegten Übersetzung neue Labels
				hinzufügen.
			</p>
			<p>
				Wenn die Übersetzung mit Ihrer Umfrage veröffentlicht werden soll, klicken
				Sie bitte das Kästchen „Veröffentlichen“ an.
			</p>
			<p>
				Sobald eine Übersetzung veröffentlicht ist, können die Umfrageteilnehmer
				direkt im Umfrage-Link eine der verfügbaren Sprachen auswählen.
			</p>
			<h2>
				Wie kann ich eine vorhandene Übersetzung in meine Umfrage hochladen?
			</h2>
			<p>
				<ol>
					<li>
						Öffnen Sie Ihre Umfrage und öffnen Sie die Seite „Übersetzungen“.
					</li>
					<li>
						Klicken Sie auf „Vorhandene Übersetzung hochladen“.
					</li>
					<li>
						Ein Assistent führt Sie Schritt für Schritt durch den Prozess zum
						Hochladen der Übersetzung.
					</li>
				</ol>
			</p>
			<h2>
				Kann ich eine vorhandene Übersetzung online bearbeiten?
			</h2>
			<p>
				Ja!
			</p>
			<p>
				<ol>
					<li>
						Öffnen Sie Ihre Umfrage und gehen Sie zur Seite „Übersetzungen“.
					</li>
					<li>
						Wählen Sie eine oder mehrere Übersetzungen aus, die Sie bearbeiten
						möchten.
					</li>
					<li>
						Wählen Sie „Übersetzungen bearbeiten“ aus dem Aktionsmenü direkt unter
						der Liste der verfügbaren Übersetzungen.
					</li>
					<li>
						Klicken Sie auf „OK“ ? damit wird der Online-Editor für Übersetzungen
						geöffnet, in dem Sie mehrere Übersetzungen gleichzeitig bearbeiten können.
					</li>
					<li>
						Klicken Sie auf „Speichern“, um sicherzustellen, dass Ihre Änderungen
						ins System übernommen werden.
					</li>
				</ol>
			</p>
			<p>
				<br/>
				Wenn Sie nur eine einzelne Übersetzung bearbeiten möchten:
			</p>
			<p>
				<ol>
					<li>
						Öffnen Sie Ihre Umfrage.
					</li>
					<li>
						Gehen Sie zur Seite „Übersetzungen“.
					</li>
					<li>
						Klicken Sie in der Spalte „Maßnahmen“ auf das Stift-Symbol.
					</li>
				</ol>
			</p>
			<h2>
				Kann ich meine Übersetzungen offline erstellen?
			</h2>
			<p>
				Ja! Dies funktioniert wie folgt:
			</p>
			<p>
				<ol>
					<li>
						Gehen Sie zu „Übersetzungen“.
					</li>
					<li>
						Exportieren Sie eine Sprachfassung mit dem Status „Fertig“ als
						XLS-Datei.
					</li>
					<li>
						Ändern Sie den Sprachcode (ISO 639-1) am Anfang der Datei (Zelle B1).
					</li>
					<li>
						Übersetzen Sie alle verfügbaren Textlabels in die neue Sprache (Spalte
						C).
					</li>
					<li>
						Ist die offline erstellte Übersetzung fertig, speichern Sie sie.
					</li>
					<li>
						Klicken Sie auf „Vorhandene Übersetzung hochladen“, um die Übersetzung
						zu importieren.
					</li>
				</ol>
			</p>
			<p>
				Dies ist der letzte Schritt. Überprüfen Sie anschließend die Übersetzung
				über die Registerkarte „Test“.
			</p>
			<h2>
				Wie veröffentliche ich meine Übersetzungen bzw. wie hebe ich
				Veröffentlichungen auf?
			</h2>
			<p>
				Veröffentlichung einer Umfrage in mehreren Sprachen:
			</p>
			<p>
				<ol>
					<li>
						Öffnen Sie Ihre Umfrage.
					</li>
					<li>
						Gehen Sie zur Seite „Übersetzungen“.
					</li>
					<li>
						Setzen Sie unter „Veröffentlichen“ ein Häkchen bei den jeweiligen
						Übersetzungen, die Sie veröffentlichen möchten, bzw. entfernen Sie das
						Häkchen bei den Sprachen, deren Veröffentlichung Sie aufheben möchten.
					</li>
					<li>
						Gehen Sie dann zur Seite „Übersicht“ Ihrer Umfrage, um die Umfrage zu
						veröffentlichen.
					</li>
				</ol>
			</p>
			<p>
				Wurde die Umfrage bereits veröffentlicht, bevor die Übersetzungen
				hinzugefügt/entfernt wurden, klicken Sie auf „Änderungen anwenden“.
			</p>
			<p>
				Damit gewährleistet ist, dass keine Übersetzungen veröffentlicht werden, in
				denen Text fehlt, ist es nicht möglich, Übersetzungen mit leeren Labels
				(Übersetzungen, die nicht den Status „Fertig“ haben) zu veröffentlichen.
			</p>
			<p>
				Überprüfen Sie mit dem Online-Editor für Übersetzungen, dass Ihre
				Übersetzung keine leeren Labels enthält. Achten Sie auf rot unterlegte
				Zellen.
			</p>
			<h2>
				Kann ich Übersetzungen in nichteuropäischen Sprachen hochladen?
			</h2>
			<p>
				Die Anwendung unterstützt auch nichteuropäische Sprachen.
			</p>
			<p>
				Wählen Sie beim Hochladen „Sonstige“ aus und geben Sie einen gültigen
				zweistelligen Sprachcode nach
				<a href="https://de.wikipedia.org/wiki/Liste_der_ISO-639-1-Codes">
					ISO 639-1
				</a>
				an.
			</p>
			<h2>
				Was bedeutet „Maschinelle Übersetzung anfordern“?
			</h2>
			<p>
				EUSurvey kann Ihren Fragebogen mittels <b>maschineller Übersetzung</b>
				automatisch übersetzen lassen. Dafür nutzt EUSurvey den von der
				Europäischen Kommission bereitgestellten Dienst MT@EC.
			</p>
			<p>
				Auf der Seite „Übersetzungen“ haben Sie verschiedene Möglichkeiten,
				maschinelle Übersetzungen anzufordern:
			</p>
			<ul>
				<li>
					Möchten Sie eine neue Übersetzung hinzufügen, klicken Sie das Kästchen
					„Übersetzung anfordern“ an (für eine Übersetzung aus der Relaissprache
					Ihrer Umfrage).
				</li>
				<li>
					Klicken Sie in der Spalte „Maßnahmen“ auf „Übersetzung anfordern“ (für
					eine Übersetzung aus der Relaissprache Ihrer Umfrage).
				</li>
				<li>
					Wählen Sie alle Sprachen aus, in die die Umfrage übersetzt werden soll
					(sowie mindestens eine „fertige“ Übersetzung, die als Ausgangssprache
					dienen soll). Wählen Sie dann „Übersetzungen anfordern“ aus dem
					Auswahlfeld unterhalb Ihrer Übersetzungen aus und klicken Sie auf „OK“.
				</li>
			</ul>
			<p>
				Der Status der Übersetzungen lautet jetzt so lange „Angefordert“, bis die
				Übersetzungen angefertigt sind.
			</p>
			<p>
				Änderungen des Status können Sie auf der Seite „Übersetzungen“ verfolgen.
			</p>
			<p>
				Mit maschinellen Übersetzungen verfahren Sie wie mit anderen, manuell
				hinzugefügten Übersetzungen, d. h. sie werden nicht automatisch
				veröffentlicht und wenn Sie Ihrer Umfrage neue Elemente hinzufügen, müssen
				auch die Übersetzungen entsprechend ergänzt werden (fordern Sie dafür eine
				neue Übersetzung an).
			</p>
			<p>
				<i>
					Wir übernehmen keine Garantie für Qualität oder Lieferzeit der
					Übersetzungen.
				</i>
			</p>
			<p>
				<a
						href="https://webgate.ec.europa.eu/etranslation/help.html"
						target="_blank"
						>
					Maschinelle Übersetzung - Hilfe
				</a>
				(nur für EU-Bedienstete).
			</p>
			<h2>
				Hinweise für Mitarbeiter der EU
			</h2>
			<p>
				Wir empfehlen, vor der Fertigstellung Ihrer Umfrage mit dem Team für
				sprachliche Aufbereitung der DGT (GD Übersetzung) Kontakt aufzunehmen
				(E-Mail: DGT-EDIT). Das Team kann mit Ihnen gemeinsam prüfen, ob Ihre
				Umfrage klar strukturiert ist. Weitere Informationen finden Sie hier:
				<a
						href="https://myintracomm.ec.europa.eu/serv/en/dgt/Pages/index.aspx"
						target="_blank"
						>
					MyIntraComm-Seiten der DGT
				</a>
				.
			</p>
			<p>
				Die DGT kann Ihre Umfrage auch in die EU-Amtssprachen übersetzen.
			</p>
			<p>
				Die Umfrage sollte als XML-Datei exportiert und über Poetry mit dem
				„requester code“ der betreffenden Generaldirektion übermittelt werden. Die
				Umfrage sollte bei Zählung in Word nicht mehr als 15 000 Zeichen ohne
				Leerzeichen umfassen.
			</p>
			<h1>
				Umfrage veröffentlichen
			</h1>
			<h2>
				Wie veröffentliche ich meine Umfrage?
			</h2>
			<p>
				Um eine Umfrage auf Grundlage eines aktuellen Arbeitsentwurfs zu
				veröffentlichen:
			</p>
			<p>
				&#8594;  gehen Sie auf die Seite „Übersicht“ und klicken Sie auf
				„Veröffentlichen“.
			</p>
			<p>
				Nach der Bestätigung erstellt das System automatisch eine Arbeitskopie
				Ihrer Umfrage und stellt sie online, ebenso wie die Übersetzungen, die Sie
				zur Veröffentlichung ausgewählt haben (Siehe „
				<a
						href="#_Toc_7_5"
						>
					Wie veröffentliche ich meine Übersetzungen bzw. wie hebe ich
					Veröffentlichungen auf?
				</a>
				“).
			</p>
			<p>
				Sie finden den Link zu Ihrer veröffentlichten Umfrage auf der Seite
				„Übersicht“ unter „Umfrageort“.
			</p>
			<p>
				Um die Veröffentlichung Ihrer Umfrage aufzuheben, &#8594;  klicken Sie auf die
				Schaltfläche „Veröffentlichung zurückziehen“.
			</p>
			<p>
				Sie können dann weiterhin auf die nicht mehr veröffentlichte Umfrage ebenso
				wie auf Ihren aktuellen Arbeitsentwurf zugreifen.
			</p>
			<p>
				Das bedeutet, dass die nicht mehr veröffentlichte Umfrage nicht durch Ihren
				aktuellen Arbeitsentwurf ersetzt werden muss, sondern bei Bedarf erneut
				veröffentlicht werden kann.
			</p>
			<h2>
				Kann ich die URL anpassen, die zu meiner Umfrage führt?
			</h2>
			<p>
				Ja!
			</p>
			<p>
				Indem Sie den „Aliasnamen“ Ihrer Umfrage ändern, können Sie eine
				aussagekräftigere URL festlegen.
			</p>
			<p>
				<ol>
					<li>
						Öffnen Sie die Umfrage und gehen Sie zur Seite „Eigenschaften“.
					</li>
					<li>
						Ändern Sie den Aliasnamen Ihrer Umfrage im Abschnitt
						„Grundeinstellungen“.
					</li>
				</ol>
			</p>
			<p>
				Ein Aliasname darf nur alphanumerische Zeichen und Bindestriche enthalten.
			</p>
			<p>
				Wenn Sie den Aliasnamen einer veröffentlichten Umfrage ändern, &#8594;  gehen Sie
				zur Seite „Übersicht“ und klicken Sie auf „Änderungen anwenden“.
			</p>
			<p>
				Aliasnamen müssen in EUSurvey eindeutig sein. Wenn Ihr Aliasname bereits
				von einer anderen Umfrage verwendet wird, erhalten Sie eine Warnung.
			</p>
			<h2>
				Kann ich einen direkten Link zu einer Übersetzung meiner Umfrage angeben?
			</h2>
			<p>
				Wenn Sie Einladungen mit dem Umfragelink versenden oder den Link zum
				veröffentlichten Fragebogen auf der Seite „Übersicht“ verwenden, verweist
				der Link standardmäßig auf den Fragebogen in der Hauptsprache.
			</p>
			<p>
				Sie können die <b>Umfrageteilnehmer</b> jedoch auch <b>direkt</b> zur
				richtigen Übersetzung <b>leiten</b>. Nutzen Sie dafür folgenden Link:
			</p>
			<p>
				<b>https://ec.europa.eu/eusurvey/runner/</b>
				<b>SurveyAlias</b>
				<b>?surveylanguage=</b>
				<b>LC</b>
			</p>
			<p>
				Ersetzen Sie dabei:
			</p>
			<ul>
				<li>
					<b>SurveyAlias</b>
					durch den <b>Aliasnamen Ihrer Umfrage</b> und
				</li>
				<li>
					<b>LC</b>
					durch den entsprechenden <b>ISO 639-1 Sprachcode</b> (z. B. DE für
					Deutsch, FR für Französisch usw.)
				</li>
			</ul>
			<h2>
				Wie kann ich veranlassen, dass meine Umfrage zu einem festgelegten
				Termin automatisch veröffentlicht wird, wenn ich im Urlaub bin?
			</h2>
			<p>
				Sie können für Ihre Umfrage einen automatischen Veröffentlichungszeitpunkt
				festlegen.
			</p>
			<p>
				<ol>
					<li>
						Öffnen Sie die Umfrage und gehen Sie zur Seite „Eigenschaften“.
					</li>
					<li>
						Aktivieren Sie unter „Erweitert“ die Option „Automatische
						Veröffentlichung der Umfrage“.
					</li>
					<li>
						Geben Sie das Anfangs- und Enddatum für Ihre Umfrage an.
					</li>
				</ol>
			</p>
			<h2>
				Kann ich eine Erinnerung erhalten, bevor meine Umfrage endet?
			</h2>
			<p>
				Ja, EUSurvey kann Ihnen per E-Mail eine Erinnerung schicken, kurz bevor
				Ihre Umfrage endet. So können Sie die nächsten Schritte vorbereiten (z. B.
				die Ressourcen für die Analyse der Ergebnisse organisieren).
			</p>
			<p>
				Um diese Option zu aktivieren:
			</p>
			<p>
				<ol>
					<li>
						Öffnen Sie die Umfrage und gehen Sie zur Seite „Eigenschaften“.
					</li>
					<li>
						Gehen Sie zum Abschnitt „Erweitert“ und geben Sie unter
						„Benachrichtigung“ an, mit wie viel Vorlauf Sie diese Erinnerung erhalten
						möchten.
					</li>
					<li>
						Klicken Sie auf „Speichern“.
					</li>
				</ol>
			</p>
			<p>
				Die E-Mail mit der Erinnerung wird allen Fragebogen-Verwaltern zugesandt.
			</p>
			<h2>
				Für EU-Bedienstete:
			</h2>
			<h2>
				Was muss ich beachten, wenn ich eine öffentlich zugängliche, offene
				Umfrage veröffentlichen möchte („Ihre Stimme in Europa“ Webseite)?
			</h2>
			<p>
				Bitte befolgen Sie sorgfältig das
				<a
						href="https://circabc.europa.eu/sd/d/fc02d2ac-d94f-42ed-b866-b3429e0d717b/Survey_publication_your_voice_in_europe_NEW.pdf"
						target="_blank"
						>
					Verfahren
				</a>
				für die Veröffentlichung von öffentlich zugänglichen, offenen Umfragen auf
				den vom Generalsekretariat unterhaltenen Seiten von
				<a href="https://ec.europa.eu/info/consultations_de" target="_blank">
					„Ihre Stimme in Europa“
				</a>
				.
			</p>
			<h1>
				Umfrage verwalten
			</h1>
			<h2>
				Kann ich Fehler in meiner Umfrage korrigieren, die mir erst später
				auffallen?
			</h2>
			<p>
				Ja, Sie können Ihre Umfrage nach Belieben bearbeiten und ändern oder
				zusätzliche (abhängige) Fragen hinzufügen oder ändern.
			</p>
			<p>
				Allerdings werden die erhobenen Daten immer weniger aussagekräftig, je
				öfter Sie Änderungen vornehmen, da die einzelnen Umfrageteilnehmer dann
				unter Umständen verschiedene Versionen Ihrer Umfrage beantwortet haben.
			</p>
			<p>
				Um sicherzustellen, dass Sie alle eingegangenen Antworten noch vergleichen
				können, sollten Sie daher die Struktur Ihrer Umfrage nicht ändern.
			</p>
			<p>
				Sie sind in vollem Umfang für jede Änderung verantwortlich, die Sie an
				Ihrer Umfrage während ihrer Laufzeit durchführen.
			</p>
			<p>
				Um eine bereits veröffentlichte Umfrage zu ändern &#8594;  klicken Sie auf der
				Seite „Übersicht“ auf „Änderungen anwenden“, um die Änderungen auch in der
				veröffentlichten Umfrage sichtbar zu machen.
			</p>
			<h2>
				Wenn Sie Antworten aus Ihrer Umfrage entfernen möchten, lesen Sie
				bitte: „Gehen eingereichte Antworten verloren, wenn ich meine Umfrage
				ändere?“
			</h2>
			<h2>
				Gehen eingereichte Antworten verloren, wenn ich meine Umfrage ändere?
			</h2>
			<p>
				Nein, es sei denn, Sie löschen Ihre Umfrage aus dem System.
			</p>
			<p>
				Möglicherweise sind aber nicht alle erhobenen Daten sichtbar, wenn Sie
				einzelne Fragen aus Ihrer Umfrage entfernt haben, während die Umfrage noch
				lief, da die Suchmaske den Fragebogen immer nur in der neuesten
				veröffentlichten Fassung darstellt.
			</p>
			<p>
				Um alle Antworten zu sehen, einschließlich der Antworten auf Fragen, die
				während der Laufzeit Ihrer Umfrage entfernt wurden:
			</p>
			<p>
				&#8594;  wählen Sie aus der Drop-down-Liste auf der Registerkarte „Ergebnisse“
				„Beiträge (einschließlich gelöschter Fragen)“ aus.
			</p>
			<h2>
				Wie kann ich den Titel meiner Umfrage ändern?
			</h2>
			<p>
				Öffnen Sie die Umfrage, gehen Sie zur Seite „Eigenschaften“ &#8594;  ändern Sie
				den Titel Ihrer Umfrage im Abschnitt „Grundeinstellungen“.
			</p>
			<p>
				Wenn Ihre Umfrage bereits veröffentlicht ist, gehen Sie auf die Seite
				„Übersicht“ und klicken Sie auf „Änderungen anwenden“.
			</p>
			<h2>
				Wie kann ich die Kontaktinformationen meiner Umfrage ändern?
			</h2>
			<p>
				Öffnen Sie die Umfrage und gehen Sie zur Seite „Eigenschaften“.
			</p>
			<p>
				Wählen Sie unter „Grundeinstellungen“ zwischen den folgenden
				„Kontakt“-Optionen:
			</p>
			<p>
				<ul>
					<li>
						Kontaktformular: Nutzer können sich über ein Formular an Sie wenden.
					</li>
					<li>
						E-Mail: Nutzer können Sie direkt per E-Mail kontaktieren (Ihre
						E-Mail-Adresse wird angezeigt).
					</li>
					<li>
						Webseite: Die Nutzer werden zur einer bestimmten Website geleitet.
					</li>
				</ul>
			</p>
			<p>
				Wenn Ihre Umfrage bereits veröffentlicht ist, gehen Sie auf die Seite
				„Übersicht“ und klicken Sie auf „Änderungen anwenden“.
			</p>
			<h2>
				Wie kann ich die Bestätigungsnachricht abändern?
			</h2>
			<p>
				Nachdem die Befragten ihre Antworten übermittelt haben, erhalten sie eine
				Bestätigungsnachricht.
			</p>
			<p>
				Um die Standardnachricht abzuändern:
			</p>
			<p>
				<ol>
					<li>
						Öffnen Sie die Umfrage und gehen Sie zur Seite „Eigenschaften“.
					</li>
					<li>
						Ändern Sie die Bestätigungsnachricht im Abschnitt „Spezielle Seiten“.
					</li>
				</ol>
			</p>
			<p>
				Wenn Ihre Umfrage bereits veröffentlicht ist, gehen Sie auf die Seite
				„Übersicht“ und klicken Sie auf „Änderungen anwenden“.
			</p>
			<p>
				Wie passe ich die Standard-Abbruchmeldung an?
			</p>
			<p>
				Die Escape-Seite enthält die Meldung, die Ihre Befragten sehen, wenn Ihre
				Umfrage nicht verfügbar ist.
			</p>
			<p>
				Um die Standardnachricht abzuändern:
			</p>
			<p>
				<ol>
					<li>
						Öffnen Sie die Umfrage und gehen Sie zu „Eigenschaften“.
					</li>
					<li>
						Klicken Sie unter „Spezielle Seiten“ auf „Bearbeiten“ ? ändern Sie die
						Option unter „Bei Nichtverfügbarkeit erscheinende Seite“.
					</li>
				</ol>
			</p>
			<p>
				Wenn Ihre Umfrage bereits veröffentlicht ist, gehen Sie auf die Seite
				„Übersicht“ und klicken Sie auf „Änderungen anwenden“.
			</p>
			<h2>
				Kann ich eine Umfrage archivieren?<u> </u>
			</h2>
			<p>
				Ja, Sie können Ihre Umfrage archivieren und zu einem späteren Zeitpunkt
				wieder aufrufen und veröffentlichen.
			</p>
			<p>
				Um Ihre Umfrage zu archivieren &#8594;  öffnen Sie die Seite „Übersicht“ und
				klicken auf das Symbol „Umfrage Archivieren“.
			</p>
			<p>
				Archivierte Umfragen können nicht bearbeitet werden und auch nicht
				verwendet werden, um weitere Daten zu erfassen.
			</p>
			<p>
				Sie können aber die Ergebnisse Ihrer Umfrage exportieren und eine PDF-Datei
				der Umfrage erstellen lassen.
			</p>
			<p>
				Von der Seite „Übersicht“ können Sie können auf archivierte Umfragen
				zugreifen und diese hier auch wieder aktivieren.
			</p>
			<p>
				Eine wieder aktivierte Umfrage kann dann erneut bearbeitet werden.
			</p>
			<h2>
				Wie erhalten andere Nutzer Zugang zu meiner Umfrage?
			</h2>
			<p>
				Sie können anderen Nutzern Zugang zu Ihrer Umfrage gewähren, um
			</p>
			<ul>
				<li>
					die Umfrage zu testen („Zugang zur Formularvorschau“),
				</li>
				<li>
					die Ergebnisse einzusehen („Ergebnisse“) oder
				</li>
				<li>
					die Umfrage zu bearbeiten („Formularverwaltung“).
				</li>
			</ul>
			<p>
				Um einer Person oder einer Abteilung Zugang zu gewähren: Öffnen Sie Ihre
				Umfrage und gehen Sie zur Seite „Rechte“.
			</p>
			<p>
				Folgende Zugangsrechte können gewährt werden:
			</p>
			<ul>
				<li>
					Grün: Lese- und Schreibzugriff
				</li>
				<li>
					Gelb: Lesezugriff
				</li>
				<li>
					Rot: Kein Zugriff
				</li>
			</ul>
			<p>
				<ol>
					<li>
						Klicken Sie auf der Seite „Rechte“ auf „Nutzer hinzufügen“ oder
						„Abteilung hinzufügen“.
					</li>
					<li>
						Es erscheint ein Fenster mit einem Assistenten, der Ihnen Schritt für
						Schritt erklärt, wie Sie Nutzer hinzufügen können.
					</li>
					<li>
						Klicken Sie auf „Nutzer hinzufügen“ und wählen Sie zunächst die richtige
						Domäne aus (z. B. „Europäische Kommission“).
					</li>
					<li>
						Geben Sie dann das Login, die E-Mail-Adresse oder einen anderen
						Parameter ein und klicken Sie auf „Suchen“.
					</li>
					<li>
						Wählen Sie den Nutzer aus und klicken Sie auf „OK“.
					</li>
					<li>
						Möchten Sie eine „Abteilung hinzufügen“, wählen Sie das entsprechende
						Feld aus.
					</li>
					<li>
						Wählen Sie dann die gewünschte Abteilung aus und klicken Sie auf „OK“.
					</li>
				</ol>
			</p>
			<p>
				Sie gelangen dann auf die Seite „Rechte“.
			</p>
			<p>
				Dort können Sie die geeigneten Berechtigungen einstellen, indem Sie auf die
				roten Symbole klicken:
			</p>
			<ul type="disc">
				<li>
					Zum Testen Ihrer Umfrage:					
					<ul>
						<li>
							Stellen Sie die Farbe bei „Zugang zur Formularvorschau“ auf grün um.
							(Klicken Sie hierfür einfach auf den Farbpunkt, um die Rechte zu ändern).
						</li>
						<li>
							Die Umfrage erscheint dann bei den Nutzern automatisch auf ihrer
							„Umfragen“-Seite, wenn sie sich bei EUSurvey anmelden (siehe auch „
							<a
									href="#_Toc_6_2"
									>
								Wie können meine Kollegen meine Umfrage vor der Veröffentlichung
								testen?
							</a>
							“).
						</li>
					</ul>					
				</li>			
				<li>
					Zum Einsehen der Ergebnisse Ihrer Umfrage:
					<ul>
						<li>
							Stellen Sie die Farbe bei „Ergebnisse“ auf Gelb um. Die Nutzer können die
							Ergebnisse einsehen, jedoch nicht bearbeiten oder löschen.
						</li>
						<li>
							Stellen Sie die Farbe auf Grün um, können sie die Antworten sowohl
							einsehen als auch bearbeiten und löschen (siehe auch „
							<a
									href=#_Toc_9_8"
									>
								Wie erhalten andere Nutzer Zugang zu den Ergebnissen meiner Umfrage?
							</a>
							“).
						</li>
					</ul>
				</li>
				<li>
					Zum Bearbeiten Ihrer Umfrage:
					
					<ul>
						<li>
							Stellen Sie die Farbe auf Grün um &#8594;  die Nutzer können die Umfrage nun
							bearbeiten.
						</li>
						<li>
							- Die Nutzer sehen Ihre Umfrage dann automatisch in ihrer Umfragenliste
							(siehe auch „
							<a
									href="#_Toc_4_11"
									>
								Wie erlaube ich anderen Nutzern, meine Umfrage zu bearbeiten?
							</a>
							“).
						</li>
					</ul>
				</li>
				<li>
					Zum Verwalten der Einladungen zu Ihrer Umfrage:
					
					<ul>
						<li>
							Ist die Farbe Gelb gewählt &#8594;  können die Nutzer lediglich Ihre Einladungen
							einsehen.
						</li>
						<li>
							Stellen Sie die Farbe auf Grün um &#8594;  können die Nutzer sie auch
							bearbeiten.
						</li>
						<li>
							Die Nutzer sehen Ihre Umfrage dann automatisch in ihrer Umfragenliste
							(siehe auch „
							<a
									href="#_Toc_4_11"
									>
								Wie erlaube ich anderen Nutzern, meine Umfrage zu bearbeiten?
							</a>
							“).
						</li>
					</ul>
				</li>
			</ul>
			
			<p>
				Stellen Sie alle vier Optionen auf Grün um, erhalten die ausgewählten
				Nutzer uneingeschränkte Zugangsrechte zu Ihrer Umfrage.
			</p>
			<p>
				Externe Umfrage-Eigentümer oder -Organisatoren können auf der Registerkarte
				„Rechte“/auf der Schaltfläche „Nutzer hinzufügen“ keine EU-Felder sehen.
				Daher können sie diesen Nutzern keinen direkten Zugang gewähren.
			</p>
			<p>
				<a href="https://ec.europa.eu/eusurvey/home/support">
					Kontaktieren Sie uns
				</a>
				‚ um Rechte für externe Nutzer zu beantragen.
			</p>
			<h2>
				Was sind Aktivitätsprotokolle?
			</h2>
			<p>
				Aktivitätsprotokolle überwachen und protokollieren die Aktivitäten in Ihrer
				Umfrage. Sie können so überprüfen, welcher Benutzer zu welchem Zeitpunkt
				welche Änderung an Ihrer Umfrage vorgenommen hat.
			</p>
			<p>
				Sie können die Aktivitätsprotokolle auch in verschiedene Dateiformate wie
				xls, csv und ods exportieren.
			</p>
			<p>
				Um zum Aktivitätsprotokoll Ihrer Umfrage zu gelangen, &#8594;  klicken Sie neben
				„Eigenschaften“ auf die Seite „Tätigkeit“.
			</p>
			<p>
				Wenn die Aktivitätsprotokolle leer sind, kann es sein, dass sie im gesamten
				System deaktiviert sind.
			</p>
			<p>
				<a
						href="https://ec.europa.eu/eusurvey/resources/documents/ActivityLogEvents.xlsx"
						>
					Hier
				</a>
				finden Sie eine Liste der protokollierten Ereignisse.
			</p>
			<h1>
				Ergebnisse analysieren, exportieren und veröffentlichen
			</h1>
			<h2>
				Wo finde ich die von meinen Befragten übermittelten Antwort?
			</h2>
			<p>
				Öffnen Sie Ihre Umfrage ? klicken Sie auf die Seite „Ergebnisse“.
			</p>
			<p>
				Zunächst wird der vollständige Inhalt aller übermittelten Antworten in
				einer Tabelle angezeigt.
			</p>
			<p>
				Sie können sich die Ergebnisse auf zweierlei Art und Weise anzeigen lassen:
			</p>
			<ul>
				<li>
					Vollständiger Inhalt
				</li>
				<li>
					Statistiken
				</li>
			</ul>
			<p>
				Zum Wechseln des Anzeigemodus &#8594;  klicken Sie auf die Symbole in der linken
				oberen Ecke des Bildschirms.
			</p>
			<p>
				Siehe auch „
				<a
						href="#_Toc_3_5"
						>
					Wie öffne ich eine vorhandene Umfrage zur Bearbeitung usw.?
				</a>
				“
			</p>
			<h2>
				Wie kann ich eingereichte Antworten herunterladen?
			</h2>
			<p>
				<ol>
					<li>
						Öffnen Sie Ihre Umfrage und gehen Sie zur Seite „Ergebnisse“.
					</li>
					<li>
						Klicken Sie rechts oben auf „Exportieren“.
					</li>
					<li>
						Wählen Sie eines der verfügbaren Dateiformate für den Export aus.
					</li>
					<li>
						Geben Sie im Dialogfenster einen Dateinamen ein. Unter diesem Namen wird
						die Exportdatei dann auf der Seite „Export“ angezeigt.
					</li>
				</ol>
			</p>
			<p>
				Es stehen verschiedene Exportdateiformate zur Verfügung, je nach
				Anzeigemodus (Vollständiger Inhalt/Diagramme/Statistiken).
			</p>
			<p>
				Hinweis: Die Exportdatei enthält nur die als exportierbar markierten Fragen
				sowie die gefilterten Suchergebnisse.
			</p>
			<h2>
				Wie kann ich die Antworten in der Entwurfsfassung extrahieren?
			</h2>
			<p>
				Dies ist derzeit nach unserer Datenschutzpolitik nicht zulässig.
			</p>
			<p>
				In Ihrer Übersicht wird die Anzahl der als Entwurf gespeicherten Antworten
				auf Ihre Umfrage angezeigt.
			</p>
			<h2>
				Wie kann ich auf eine definierte Teilmenge aller Antworten zugreifen und
				diese auswerten?
			</h2>
			<p>
				Sie können auf der Seite „Ergebnisse“:
			</p>
			<p>
				<ul>
					<li>
						Freitext-Antworten anhand von Schlagwörtern durchsuchen oder
					</li>
					<li>
						einzelne Antworten aus den Auswahlfragen in der Filterleiste auswählen.
					</li>
				</ul>
			</p>
			<p>
				Das reduziert den Gesamtbestand der Antworten auf eine Teilmenge von
				Beiträgen.
			</p>
			<p>
				Aus technischen Gründen können Sie nur maximal 3 Filter festlegen!
			</p>
			<p>
				Sie können jederzeit den Anzeigemodus wechseln, um eine umfassende
				statistische Analyse der erhobenen Daten durchzuführen.
			</p>
			<p>
				Hinweis: Um Ergebnisse anzusehen und zu analysieren, benötigen Sie
				bestimmte Rechte (siehe „
				<a
						href="#_Toc_9_8"
						>
					Wie erhalten andere Nutzer Zugang zu den Ergebnissen meiner Umfrage?
				</a>
				“).
			</p>
			<p>
				Für den Export einer Teilmenge von Beiträgen siehe „Wie kann ich
				eingereichte Antworten herunterladen?“.
			</p>
			<p>
				Siehe auch „
				<a
						href="#_Toc_10_1"
						>
					Wo finde ich die von meinen Befragten übermittelten Antwort?
				</a>
				“
			</p>
			<h2>
				Wie veröffentliche ich meine Ergebnisse?
			</h2>
			<p>
				<ol>
					<li>
						Öffnen Sie die Umfrage.
					</li>
					<li>
						Gehen Sie zur Seite „Eigenschaften“ und wählen Sie „Ergebnisse
						veröffentlichen“ aus.
					</li>
					<li>
						Hier finden Sie die URL der veröffentlichten Ergebnisse.
					</li>
					<li>
						Wählen Sie aus, welche Fragen/Antworten/Beiträge Sie veröffentlichen
						möchten.
					</li>
					<li>
						Sie können auch direkt dorthin gelangen: &#8594;  klicken Sie auf der Seite
						„Überblick“ auf „Veröffentlichung der Ergebnisse bearbeiten“.
					</li>
					<li>
						Achten Sie darauf, dass Sie bei „Ergebnisse veröffentlichen“ unter
						„Veröffentlichen“ eine Auswahl treffen, anderenfalls veröffentlicht das
						System gar keine Ergebnisse.
					</li>
				</ol>
			</p>
			<h2>
				Wie kann ich auf die veröffentlichten Ergebnisse zugreifen?
			</h2>
			<p>
				Öffnen Sie die Seite „Übersicht“ &#8594;  klicken Sie auf den Link
				„Veröffentlicht“ neben dem Wort „Ergebnisse“.
			</p>
			<p>
				Alle Nutzer, denen diese Adresse bekannt ist, können auf Ihre Ergebnisse
				zugreifen.
			</p>
			<h2>
				Wie erhalten andere Nutzer Zugang zu den Ergebnissen meiner Umfrage?
			</h2>
			<p>
				Öffnen Sie Ihre Umfrage &#8594;  gehen Sie auf die Seite „Rechte“ und gewähren Sie
				anderen Nutzern Zugang zur Ihren Ergebnissen.
			</p>
			<p>
				Weitere Informationen hierzu erhalten Sie unter
				<a
						href="#_Toc_9_8"
						>
					„Wie erhalten andere Nutzer Zugang zu meiner Umfrage?“
				</a>
				.
			</p>
			<h2>
				Meine Exportdateien lassen sich nicht entpacken – kann ich das lösen?
			</h2>
			<p>
				Dies kann vorkommen, wenn der Pfadname Ihres Ordners zu lang ist.
			</p>
			<p>
				In Windows dürfen Verzeichnispfade auf der Festplatte höchstens 260 Zeichen
				lang sein.
			</p>
			<p>
				Mögliche Lösungen:
			</p>
			<ul>
				<li>
					Entpacken Sie den Ordner in das Stammverzeichnis Ihres Betriebssystems,
					z. B. direkt unter „C:“ anstatt unter „C:\Nutzer\NUTZERNAME\Desktop“,
					oder
				</li>
				<li>
					benennen Sie beim Entpacken der Dateien den Zielordner um und verkürzen
					Sie dabei den Verzeichnispfad.
				</li>
			</ul>
			<h2>
				Veröffentlichte Ergebnisse - wie können von Befragten hochgeladene
				personenbezogene Daten geschützt werden?
			</h2>
			<p>
				Gemäß den Datenschutzvorschriften kann der Formular-Verwalter die mit der
				Antwort eines Befragten hochgeladenen Dateien zusammen mit den anderen
				Ergebnissen veröffentlichen.
			</p>
			<p>
				Hierfür müssen Sie &#8594;  das Kästchen „Hochgeladene Elemente“ ankreuzen.
			</p>
			<p>
				Dies befindet sich im entsprechenden Abschnitt auf der Seite
				„Eigenschaften“ unter „Ergebnisse veröffentlichen“.
			</p>
			<p>
				Dieses Dialogfenster erscheint nur, wenn Ihre Umfrage eine hochgeladene
				Datei enthält.
			</p>
			<h2>
				Wie kann ich meine Umfrage gestalten, um die Ergebnisse mit oder ohne
				personenbezogene Daten zu veröffentlichen?
			</h2>
			<p>
				Wenn Sie die Befragten wählen lassen möchten, ob ihre personenbezogenen
				Daten mit ihren Antworten veröffentlicht werden sollen oder nicht,
				erstellen Sie Ihren Fragebogen bitte nach den hier beschriebenen
				<a
						href="https://circabc.europa.eu/sd/d/e68ff760-226f-40e9-b7cb-d3dcdd04bfb1/How_to_publish_survey_results_anonymously.pdf"
						target="_blank"
						>
					Anweisungen
				</a>
				.
			</p>
			<h2>
				Warum sind meine Ergebnisse nicht aktuell?
			</h2>
			<p>
				Um die Leistung von EUSurvey bei der Abfrage der Umfrage-Ergebnisse zu
				verbessern, wurde eine neue Datenbank eingeführt.
			</p>
			<p>
				Dies kann jedoch zu Verzögerungen bei der Anzeige der neuesten Daten auf
				der Ergebnisseite führen.
			</p>
			<p>
				Diese Verzögerung sollte nicht mehr als 12 Stunden betragen; sollte die
				Verzögerung 12 Stunden übersteigen, wenden Sie sich bitte &#8594;  an EUSurvey    <a href="https://ec.europa.eu/eusurvey/home/support">Support</a>.
			</p>
			<h2>Wie kann ich Dateien abrufen, die von Teilnehmern hochgeladen wurden?</h2>

			<p>EUSurvey bietet verschiedene Exportformate an: XLS, PDF, ODS und XML. </p>

			<p>Je nach ausgewähltem Format sind Struktur und Inhalt der exportierten Dateien
				des "Datei hochladen"-Elementes unterschiedlich und so wie in den folgenden Abschnitten beschrieben:</p>

			<h4>Ergebnisexport im XLS-Format</h4>
			<ol>
				<li>
					<p>Eine Excel-Datei, die die folgenden Informationen enthält:</p>
					<p>Alias: Umfrage-Alias (Beispiel: 6459a3c9-e517-4a34-8e5d-70185db022c3)<br>
						Export Date: Datum im Format "dd-mm-yyyy hh:mm" (Beispiel: 28-09-2020 15:28)</p>

					<p>Eine Tabelle mit folgendem Aufbau:</p>

					<ul>
						<li>Jede Spalte steht für eine andere "Datei hochladen"-Frage.</li>

						<li>Jede Zeile steht für einen anderen Beitrag.</li>

						<li>Jede Zelle enthält alle Namen der hochgeladenen Dateien.</li>
					</ul><br>

				</li>
				<li>
					<p>Ordner, die den einzelnen Beiträgen entsprechen und nach der Kennnummer des Beitrags
						benannt sind. Sie enthalten Unterordner für jede "Datei hochladen"-Frage (Upload_1, Upload_2 usw.).  </p>

					<p>Zum Beispiel:</p>

					<p>Ordner:
						6cf0463c-29f4-4bea-a195-10e77c61dda1<br>

						Unterordner: Upload_1 (für die erste "Datei hochladen"-Frage ) beinhaltet alle hochgeladenen Dateien.<br>

						Unterordner: Upload_2 (für die zweite "Datei hochladen"-Frage ) beinhaltet alle hochgeladenen Dateien.</p>
				</li>
			</ol>
			<h4>Ergebnisexport im PDF-Format</h4>
			<ol>
				<li>
					<p>Ordner mit dem Namen "PDFs", der alle Umfragebeiträge als PDF-Dokumente enthält.</p>
				</li>
				<li>
					<p>Ordner, die den einzelnen Beiträgen entsprechen und nach der Kennnummer des Beitrags
						benannt sind. Sie enthalten Unterordner für jede "Datei hochladen"-Frage (Upload_1, Upload_2 usw.). </p>
				</li>
			</ol>

			<h4>Ergebnisexport im ODS-Format</h4>

			<ol>
				<li>
					<p>Eine Open-Office-Datei, die die folgenden Informationen enthält:</p>
					<p>Alias: Umfrage-Alias (Beispiel: 6459a3c9-e517-4a34-8e5d-70185db022c3)<br>
						Export Date: Datum im Format "dd-mm-yyyy hh:mm" (Beispiel: 28-09-2020 15:28)</p>

					<p>Eine Tabelle mit folgendem Aufbau:</p>

					<ul>
						<li>Jede Spalte steht für eine andere "Datei hochladen"-Frage.</li>

						<li>Jede Zeile steht für einen anderen Beitrag.</li>

						<li>Jede Zelle enthält alle Namen der hochgeladenen Dateien.</li>
					</ul><br>
				</li>
				<li>
					<p>Ordner, die den einzelnen Beiträgen entsprechen und nach der Kennnummer des Beitrags
						benannt sind. Sie enthalten Unterordner für jede "Datei hochladen"-Frage (Upload_1, Upload_2 usw.). </p>
				</li>
			</ol>

			<h4>Ergebnisexport im XML-Format</h4>

			<p>Dieses Exportformat beinhaltet die Ergebnisse im strukturierten XML-Format.<br>
			<b>Hochgeladene Dateien sind in diesem Fall nicht einsehbar.</b></p>

			<h2>Wie wird der Score von Sortieren-Fragen berechnet?</h2>
			<p>
				Sortieren-Fragen werden verwendet, um Ihren Umfrageteilnehmern die Möglichkeit zu geben, eine Reihe von Elementen nach ihrer Wichtigkeit zu ordnen.
				Es wird empfohlen, die Anzahl der zu bewertenden Elemente auf 5 zu begrenzen.
				Wenn Sie mehr Fragen stellen, kann es für die Teilnehmer schwierig sein, alle Elemente richtig zu bewerten.
			</p>
			<p>
				Das von den Umfrageteilnehmern am meisten bevorzugte Element (das sie an die erste Stelle setzen) erhält die höchste Gewichtung, und das am wenigsten bevorzugte Element (welches sie an die letzte Stelle setzen) erhält eine Gewichtung von 1.
				Die Gewichtung erfolgt also umgekehrt proportional zur Rangfolge des Elements.
			</p>
			<p>
				Wenn eine Sortieren-Frage beispielsweise aus 5 Elementen besteht, erfolgt die Gewichtung wie folgt:
			</p>
			<ul>
				<li>Das am höchsten eingestufte Element hat eine Gewichtung von 5</li>
				<li>Das zweite Element hat eine Gewichtung von 4</li>
				<li>Das dritte Element hat eine Gewichtung von 3</li>
				<li>Das vierte Item hat eine Gewichtung von 2</li>
				<li>Das fünfte Element hat eine Gewichtung von 1</li>
			</ul>
			<p>
				Der Score errechnet sich aus der durchschnittlichen Gewichtung durch die Umfrageteilnehmer.
			</p>
			<h2>
				Exportieren des Datensatzes (d. h. der Umfrageantworten)
			</h2>
			<p>
				Sie können die Antworten Ihrer Umfrage mithilfe der Exportfunktion exportieren.
				Dies kann z. B. für die weitere Datenverarbeitung in Excel nützlich sein.
			</p>
			<p>
				Schritte zum Exportieren der Antworten:
			</p>
			</p>
			<ol>
				<li>Gehen Sie zur Registerkarte „Ergebnisse“ (erste Ansicht, Ergebnistabelle).</li>
				<li>Klicken Sie „Exportieren“ (ein Dialogfenster öffnet sich).</li>
				<li>Geben Sie einen Namen für Ihren Export ein.</li>
				<li>Wählen Sie das Dateiformat aus (wenn die Exportdatei erstellt wurde, werden Sie eine Benachrichtigung erhalten).</li>
				<li>Gehen Sie zur Seite „Exporte“.</li>
				<li>Laden Sie Ihre Datei herunter.</li>
			</ol>

			<h1>
				Design und Layout
			</h1>
			<h2>
				Wie ändere ich das allgemeine Erscheinungsbild meiner Umfrage?
			</h2>
			<p>
				1. Öffnen Sie Ihre Umfrage und gehen Sie zur Seite „Eigenschaften“.
			</p>
			<p>
				2. Wählen Sie „Aussehen“ aus.
			</p>
			<p>
				3. Wählen Sie für Ihre Umfrage mit dem Drop-down-Menü unter „Skin“ eine
				neue Skin aus &#8594;  klicken Sie auf „Speichern“.
			</p>
			<p>
				Wenn Ihre Umfrage bereits veröffentlicht ist, gehen Sie auf die Seite
				„Übersicht“ und klicken Sie auf „Änderungen anwenden“.
			</p>
			<h2>
				Wie kann ich meine eigenen Umfrage-„Themen“ erstellen?
			</h2>
			<p>
				<ol>
					<li>
						Gehen Sie zur Seite „Einstellungen“ &#8594;  wählen Sie oben auf Ihrem
						Bildschirm „Skins“ aus.
					</li>
					<li>
						Klicken Sie auf „Neue Skin erstellen“.
					</li>
					<li>
						Es öffnet sich der Skin-Editor für Umfrage-„Themen“.
					</li>
				</ol>
			</p>
			<p>
				Sie können ein bestehendes Thema kopieren und diese Vorlage mit dem
				Online-Editor für Skins nach Wunsch ändern.
			</p>
			<h2>
				Wie füge ich meiner Umfrage ein Logo hinzu?
			</h2>
			<p>
				Um in der oberen rechten Ecke Ihrer Umfrage Ihr Projekt-/Unternehmenslogo
				einzufügen, &#8594;  laden Sie auf der Seite „Eigenschaften“ im Abschnitt
				„Aussehen“ eine Bilddatei hoch.
			</p>
			<p>
				Wenn Ihre Umfrage bereits veröffentlicht ist, gehen Sie auf die Seite
				„Übersicht“ und klicken Sie auf „Änderungen anwenden“.
			</p>
			<h2>
				Wie füge ich meiner Umfrage nützliche Links hinzu?
			</h2>
			<p>
				<ol>
					<li>
						Öffnen Sie Ihre Umfrage.
					</li>
					<li>
						Gehen Sie zur Seite „Eigenschaften“ und wählen Sie „Erweitert“ aus.
					</li>
					<li>
						Fügen Sie unter „Nützliche Links“ Labels und URL hinzu.
					</li>
					<li>
						Diese Links erscheinen rechts auf jeder Seite Ihrer Umfrage.
					</li>
				</ol>
			</p>
			<p>
				Wenn Ihre Umfrage bereits veröffentlicht ist, gehen Sie auf die Seite
				„Übersicht“ und klicken Sie auf „Änderungen anwenden“.
			</p>
			<h2>
				Wo lade ich Hintergrunddokumente für meine Umfrage hoch?
			</h2>
			<p>
				<ol>
					<li>
						Öffnen Sie Ihre Umfrage.
					</li>
					<li>
						Gehen Sie zur Seite „Eigenschaften“ und wählen Sie „Erweitert“ aus.
					</li>
					<li>
						Laden Sie unter „Hintergrunddokumente“ eine Datei hoch.
					</li>
					<li>
						Diese Dokumente erscheinen rechts auf jeder Seite Ihrer Umfrage.
					</li>
				</ol>
			</p>
			<p>
				Wenn Ihre Umfrage bereits veröffentlicht ist, gehen Sie auf die Seite
				„Übersicht“ und klicken Sie auf „Änderungen anwenden“.
			</p>
			<h2>
				Wie erstelle ich eine Umfrage mit mehreren Seiten?
			</h2>
			<p>
				Übergeordnete Abschnitte Ihrer Umfrage lassen sich automatisch in einzelne
				Seiten unterteilen.
			</p>
			<p>
				<ol>
					<li>
						Öffnen Sie Ihre Umfrage.
					</li>
					<li>
						Gehen Sie zur Seite „Eigenschaften“ und wählen Sie „Aussehen“ aus.
					</li>
					<li>
						Aktivieren Sie „Mehrstufige Seitenadressierung“ und klicken Sie auf
						„Speichern“.
					</li>
				</ol>
			</p>
			<p>
				Wenn Ihre Umfrage bereits veröffentlicht ist, gehen Sie auf die Seite
				„Übersicht“ und klicken Sie auf „Änderungen anwenden“.
			</p>
			<h2>
				Wie aktiviere ich eine automatische Nummerierung für meine Umfrage?
			</h2>
			<p>
				Um alle Abschnitte/Fragen automatisch durchnummerieren zu lassen:
			</p>
			<p>
				<ul>
					<li>
						Öffnen Sie Ihre Umfrage, gehen Sie zur Seite „Eigenschaften“ und wählen
						Sie „Aussehen“ aus.
					</li>
					<li>
						Aktivieren Sie „Abschnitte automatisch nummerieren“ und/oder „Fragen
						automatisch nummerieren“ und wählen Sie die gewünschte Einstellung aus.
					</li>
					<li>
						Klicken Sie auf „Speichern“.
					</li>
				</ul>
			</p>
			<p>
				Wenn Ihre Umfrage bereits veröffentlicht ist, gehen Sie auf die Seite
				„Übersicht“ und klicken Sie auf „Änderungen anwenden“.
			</p>
			<h2>
				Kann ich eine individuelle Skin für meine Umfrage erstellen?
			</h2>
			<p>
				Ja, um eine neue Skin für Ihre Umfrage zu erstellen, gehen Sie bitte wie
				folgt vor:
			</p>				
			<p>
				<ol>
					<li>
						Gehen Sie zur Seite „Eigenschaften“ ? wählen Sie „Skins“ aus.
					</li>
					<li>
						Öffnen Sie den Reiter „Neue Skin erstellen“ ? ändern Sie das
						Erscheinungsbild verschiedener Elemente Ihrer Umfrage: Frage- und
						Antworttext, Titel, Hilfetext und andere Elemente.
					</li>
					<li>
						Geben Sie Ihrer neuen Skin einen Namen.
					</li>
					<li>
						Wählen Sie ein Element aus, das Sie bearbeiten möchten.
					</li>
					<li>
						Rechts finden Sie die verschiedenen Schriftoptionen, die Sie ändern
						können:
						
						<ul>
							<li>
								Vorder- und Hintergrundfarbe
							</li>
							<li>
								Schriftart, Schriftfamilie, Schrittgröße und Schriftstärke.
							</li>
						</ul>
					</li>
					<li>
						Darunter, im „Skin Preview Survey“, können Sie sehen, wie das geänderte
						Schriftbild in Ihrer Umfrage aussieht.
					</li>
					<li>
						Klicken Sie auf „Speichern“.
					</li>
				</ol>
			</p>
			<p>
				Wenn Sie mehrere Elemente ändern möchten &#8594; können Sie eins nach dem anderen
				ändern &#8594;  und dann am Ende, wenn alle Elemente fertiggestellt sind,
				speichern (es ist nicht nötig, jedes Mal auf Speichern zu klicken, wenn Sie
				ein Element geändert haben).
			</p>
			<p>
				Um die neue Skin für Ihre Umfrage zu übernehmen, &#8594; gehen Sie zur Seite
				„Eigenschaften“ und wählen Sie „Aussehen“ aus.
			</p>
			<p>
				Wählen Sie Ihre neue Skin im Drop-down-Menü „Skin“ aus &#8594; klicken Sie auf
				„Speichern“.
			</p>
			<h1>
				Kontakte und Einladungen verwalten
			</h1>
			<h2>
				Was ist das „Adressbuch“?
			</h2>
			<p>
				Im „Adressbuch“ können Sie Ihre eigene Teilnehmer-Gruppe erstellen.
			</p>
			<p>
				Auf diese Weise können Sie Personen oder Organisationen einladen, die
				bestimmten Kriterien entsprechen (z. B. „männlich“, „älter als 21“).
			</p>
			<p>
				Alle potenziellen Teilnehmer werden als Kontakte im Adressbuch gespeichert,
				mit einer unbegrenzten Liste von Attributen, die bearbeitet werden kann.
			</p>
			<p>
				Um Kontakte in Ihrem Adressbuch zu speichern, brauchen Sie einen
				Identifikator („Name“) und eine E-Mail-Adresse für jeden Kontakt.
			</p>
			<h2>
				Was sind die „Attribute“ eines Kontakts?
			</h2>
			<p>
				Jeder Kontakt im Adressbuch kann durch einen variablen Satz von Attributen
				wie „Land“, „Telefonnummer“, „Anmerkungen“ usw. charakterisiert werden.
			</p>
			<p>
				Durch Bearbeiten eines Kontakts können Sie ein neues Attribut hinzufügen.
			</p>
			<p>
				<ol>
					<li>
						Öffnen Sie im Fenster „Kontakt bearbeiten“ &#8594; das Menü „Attribute“ und
						wählen Sie „Neu...“ aus.
					</li>
					<li>
						Es wird ein neues Fenster angezeigt, in dem Sie das neue Attribut
						bearbeiten können.
					</li>
					<li>
						Das neu erstellte Attribut wird als Spalte im Adressbuch angezeigt - es
						kann auch einem Satz von Kontakten hinzugefügt werden.
					</li>
				</ol>
			</p>
			<h2>
				Wie füge ich dem Adressbuch neue Kontakte hinzu?
			</h2>
			<p>
				Gehen Sie zum „Adressbuch“ &#8594; klicken Sie auf „Kontakt hinzufügen“, wenn Sie
				einen einzelnen Kontakt hinzufügen möchten.
			</p>
			<p>
				Sie können auch eine Liste von Kontakten im XLS-, ODS-, CSV- oder
				TXT-Format hochladen. Klicken Sie dazu auf „Importieren“.
			</p>
			<p>
				Siehe auch „
				<a
						href="#_Toc_12_5"
						>
					Wie importiere ich mehrere Kontakte aus einer Datei in mein Adressbuch?
				</a>
				“.
			</p>
			<h2>
				Was ist ein „Registrierungsformular“?				
			</h2>
			<p>
				Es handelt sich um ein Formular, mit dem anhand der persönlichen Daten, die
				die Befragten übermitteln, automatisch Kontakte erstellt werden.
			</p>
			<p>
				Zur Erstellung eines solchen Formulars gehen Sie wie folgt vor:
			</p>
			<p>
				<ol>
					<li>
						Öffnen Sie Ihre Umfrage.
					</li>
					<li>
						Gehen Sie zur Seite „Eigenschaften“ und wählen Sie „Erweitert“ aus.
					</li>
					<li>
						Aktivieren Sie „Kontakte einrichten“ und klicken Sie auf „Speichern“.
					</li>
				</ol>
			</p>
			<p>
				Sobald Sie diese Auswahl getroffen haben, fügt das System zwei
				Freitext-Pflichtfragen („Name“ und „E-Mail-Adresse“) in Ihre Umfrage ein.
			</p>
			<p>
				Damit wird sichergestellt, dass jeder Teilnehmer gültige persönliche Daten
				angibt
			</p>
			<p>
				Durch Aktivierung der Option „Attribute“ für einzelne Fragen können Sie
				wählen, welche weiteren Informationen über den neu erstellten Kontakt
				gespeichert werden sollen (z. B. kann eine Textfrage mit dem Attribut
				„Telefon“ dazu verwendet werden, die Telefonnummer des Befragten im
				Adressbuch zu speichern).
			</p>
			<h2>
				Wie importiere ich mehrere Kontakte aus einer Datei in mein Adressbuch?
			</h2>
			<p>
				Für den Import einer Liste mit Kontakten bietet EUSurvey einen Assistenten
				an, der Sie Schritt für Schritt durch den Importprozess führt.
			</p>
			<p>
				Die folgenden Dateiformate werden unterstützt:
			</p>
			<ul>
				<li>
					XLS
				</li>
				<li>
					ODS
				</li>
				<li>
					CSV
				</li>
				<li>
					TXT (mit Trennzeichen).
				</li>
			</ul>
			<p>
				So starten Sie den Assistenten:
			</p>
			<p>
				<ol>
					<li>
						Wählen Sie auf der Seite „Adressbuch“ die Option „Importieren“
					</li>
					<li>
						Wählen Sie die Datei aus, in der Sie Ihre Kontakte gespeichert haben.
					</li>
					<li>
						Geben Sie an, ob Ihre Datei eine Kopfzeile enthält oder nicht.
					</li>
					<li>
						Geben Sie an, welche Art Trennzeichen Sie für CSV- und TXT-Dateien
						verwendet haben (standardmäßig wird das wahrscheinlichste Zeichen
						vorgeschlagen).
					</li>
				</ol>
			</p>
			<p>
				Zweiter Schritt:
			</p>
			<p>
				<ol>
					<li>
						Das System wird Sie auffordern, die einzelnen Spalten auf neue Attribute
						für Ihre Kontakte in EUSurvey abzubilden (damit Sie fortfahren können,
						müssen die Pflichtattribute „Name“ und „E-Mail“ abgebildet sein).
					</li>
					<li>
						Wenn Sie auf „Weiter“ klicken, lädt das System Ihre Datei in das System
						und zeigt die einzelnen Kontakte an, die importiert werden.
					</li>
					<li>
						Sie können einzelne Kontakte abwählen, die nicht importiert werden
						sollen.
					</li>
					<li>
						Klicken Sie auf „Speichern“, um Ihre Kontakte im Adressbuch zu
						speichern.
					</li>
				</ol>
			</p>
			<h2>
				Wie bearbeite ich ein Attribut für mehrere Kontakte gleichzeitig?
			</h2>
			<p>
				<ol>
					<li>
						Suchen Sie die Kontakte in Ihrem Adressbuch und wählen Sie sie aus.
					</li>
					<li>
						Wählen Sie den „Massenbearbeitung“ aus und klicken Sie auf „OK“.
					</li>
					<li>
						Im Pop-up-Fenster können Sie auswählen, ob Werte für mehrere Kontakte
						beibehalten, gelöscht oder festgelegt werden sollen – standardmäßig werden
						nur die konfigurierten Attribute angezeigt.
					</li>
					<li>
						Klicken Sie auf das grüne Kreuz, um weitere Attribute anzuzeigen.
					</li>
					<li>
						Nachdem Sie auf „Aktualisieren“ geklickt und die Sicherheitsmeldung
						bestätigt haben, speichert die Anwendung Ihre Änderungen im Adressbuch.
					</li>
				</ol>
			</p>
			<h2>
				Kann ich Kontakte aus meinem Adressbuch auf meinen Rechner exportieren?    <u> </u>
			</h2>
			<p>
				Ja.
			</p>
			<p>
				Klicken Sie auf der Seite „Adressbuch“ auf eines der Dateiformat-Symbole in
				der rechten oberen Ecke.
			</p>
			<p>
				Sie finden die exportierten Kontakte auf der Seite „Exporte“.
			</p>
			<h1>
				Teilnehmer einladen
			</h1>
			<h2>
				Wie lege ich eine Gruppe möglicher Befragter fest? Was ist eine
				„Gästeliste“?
			</h2>
			<p>
				Sie können ausgewählte Kontakte zu einer Gruppe zusammenfassen und jedem
				einzelnen Kontakt eine eigene E-Mail mit individuellem Zugangslink
				schicken. Diese Gruppe wird „Gästeliste“ genannt.
			</p>
			<p>
				Neben dem allgemeinen Passwort für eine Umfrage ist dies eine weitere
				Möglichkeit, Personen zur Teilnahme an Ihrer Umfrage einzuladen.
			</p>
			<p>
				Um mehrere Kontakte einzuladen, &#8594;  öffnen Sie Ihre Umfrage und gehen Sie auf
				die Seite „Teilnehmer“.
			</p>
			<p>
				Wählen Sie eine der folgenden Gästelistearten aus, um einen Assistenten
				aufzurufen, der Sie Schritt für Schritt durch den Prozess führt:
			</p>
			<ul>
				<li>
					<b>Kontaktliste</b>
					aus dem „Adressbuch“
					<br/>
					Wählen Sie Kontakte aus Ihrem „Adressbuch“ aus (siehe
					<a
							href="#_Toc_12_1"
							>
						Was ist das „Adressbuch?“
					</a>
					), um sie Ihrer Gästeliste hinzuzufügen
				</li>
				<li>
					<b>EU-Liste</b>
					„EU-Organe und andere Einrichtungen“ (nur EU-Personal)
					<br/>
					Wählen Sie mehrere Abteilungen Ihrer Institution/Agentur aus, um alle
					Personen, die dort arbeiten, Ihrer Gästeliste hinzuzufügen
				</li>
				<li>
					<b>Zugangscodes</b>
					<br/>
					Erstellen Sie eine Liste von Zugangscodes (individuelle Zugangscodes),
					die offline verteilt werden können, und den Zugriff auf eine geschützte
					Online-Umfrage ermöglichen
				</li>
			</ul>
			<p>
				Nutzen Sie die Suchfunktion Ihres Adressbuches &#8594;  klicken Sie auf die
				Schaltfläche „&gt;&gt;“ im nächsten Bildschirm, um Kontakte aus Ihrem
				Adressbuch auf Ihre neue Gästeliste zu verschieben.
			</p>
			<p>
				Klicken Sie auf „Speichern“, um eine neue Gästeliste mit allen Kontakten
				für Ihre Umfrage zu erstellen.
			</p>
			<p>
				Siehe unten, wie E-Mails mit individuellen Zugangslinks an konfigurierte
				Kontakte aus einer Ihrer Gästelisten versandt werden können.
			</p>
			<h2>
				Wie bearbeite/entferne ich eine bestehende Gästeliste?
			</h2>
			<p>
				<ol>
					<li>
						Öffnen Sie Ihre Umfrage.
					</li>
					<li>
						Gehen Sie zur Seite „Teilnehmer“.
					</li>
					<li>
						Um die Gästeliste zu bearbeiten, &#8594;  klicken Sie auf das Symbol mit dem
						Stift.
					</li>
					<li>
						Um eine Gästeliste zu entfernen, &#8594;  klicken Sie zunächst auf die
						Schaltfläche „Sperren“.
					</li>
					<li>
						Klicken Sie dann auf die Schaltfläche „Entfernen“, um die Liste zu
						löschen.
					</li>
				</ol>
			</p>
			<h2>
				Wie schicke ich meinen Teilnehmern eine E-Mail mit einer Einladung?
			</h2>
			<p>
				Sobald Sie eine neue Gästeliste erstellt haben, können Sie an diese
				Kontakte Einladungs-E-Mails verschicken.
			</p>
			<p>
				Bei „geschützten“ sowohl als auch bei „offenen“ Umfragen erhält jeder
				Teilnehmer einen persönlichen Zugangslink.
			</p>
			<p>
				<b>
					Dies bedeutet, dass jeder Teilnehmer, der eine automatische E-Mail-
					Einladung über EUSurvey erhält, den Fragebogen nur einmal beantworten
					kann.
				</b>
			</p>
			<p>
				<ol>
					<li>
						Klicken Sie auf der Seite „Teilnehmer“ auf das Umschlagsymbol.
					</li>
					<li>
						In dem Dialogfenster, das sich öffnet, können Sie eine E-Mail-Vorlage
						aus den Schriftart-Einstellungen auswählen. Standardmäßig ist die
						ausgewählte Grundeinstellung „EUSurvey“.
					</li>
					<li>
						Sie können den Betreff und Inhalt Ihrer E-Mail festlegen sowie die
						„Antworten“-E-Mail-Adresse. Alle Antworten auf Ihre Einladungs-E-Mail
						werden dann an diese Adresse gesendet.
					</li>
					<li>
						Speichern Sie Ihren E-Mail-Text &#8594;  er steht Ihnen dann für alle weiteren
						Gästelisten und Umfragen zur Verfügung. Sie finden ihn in der Drop-Down-
						Liste unter „E-Mail Vorlage verwenden“.
					</li>
					<li>
						Klicken Sie dann auf „Weiter“ &#8594;  ein Assistent führt Sie Schritt für
						Schritt durch den Einladungsprozess.
					</li>
				</ol>
			</p>
			<h2>
				Wie kann ich Token verwenden, um einen Link zu erstellen?
			</h2>
			<p>
				Um eine „Token-Liste“ (Authentifizierungs-Token) zu erstellen, die für den Zugriff auf
				einen gesicherten Online-Fragebogen verteilt werden können, öffnen Sie Ihre Umfrage und gehen Sie auf die Seite „Teilnehmer“.
				Klicken Sie auf „Token-Liste“, um einen Assistenten zu starten, der Sie durch den Prozess führen wird.
				Wählen Sie einen Namen für die Gruppe und wählen Sie „Token“ aus den Arten von Gästelisten.
			</p>
			<p>
				Verwenden Sie die erstellten Token, um individuelle Einladungslinks zu erstellen,
				die Sie per E-Mail an die Teilnehmer senden können,
				indem Sie die folgende URL verwenden:
			</p>
			<p>
				https://ec.europa.eu/eusurvey/runner/<span style="color: #e50000; font-weight: bold">SurveyAlias</span>/<span style="color: #e50000; font-weight: bold">TOKEN</span>
			</p>
			<p>
				Ersetzen Sie dabei:
			</p>
			<ul>
				<li>
					<span style="color: #e50000; font-weight: bold">SurveyAlias</span> mit dem <b>Alias Ihrer Umfrage</b>
				</li>
				<li>
					<span style="color: #e50000; font-weight: bold">TOKEN</span> mit einem der Token aus der „Token-Liste“
				</li>
			</ul>
			<h1>
				Das eigene Konto verwalten
			</h1>
			<h2>
				Wie ändere ich mein Passwort?
			</h2>
			<p>
				Wenn Nutzer ihr EU Login-Passwort verloren haben, müssen sie es ändern.
			</p>
			<p>
				Dazu sollten Sie auf der EU Login-Seite ? auf „Passwort vergessen?“
				klicken.
			</p>
			<h2>
				Wie ändere ich meine E-Mail-Adresse?
			</h2>
			<p>
				Wenn Sie über ein EU Login-Konto auf EUSurvey zugreifen, können Sie Ihre
				E-Mail-Adresse wie folgt ändern:
			</p>
			<p>
				Verbinden Sie sich mit EU Login &#8594;  wählen Sie nach dem Einloggen auf der
				Registerkarte „Kontoinformationen“ die Option „Persönliche Daten ändern“.
			</p>
			<p>
				Nutzer der OSS-Version von EUSurvey und geschäftliche Nutzer der
				API-Bedienoberfläche &#8594;  verbinden sich bitte mit der Anwendung &gt; klicken
				Sie unter „Einstellungen“ &gt; „Mein Konto“ &gt; auf „E-Mail-Adresse“.
			</p>
			<h2>
				Wie ändere ich meine Standard-Spracheinstellung?
			</h2>
			<p>
				Gehen Sie zu „Einstellungen“ &gt;„Mein Konto“ und klicken Sie auf
				„Sprache“.
			</p>
			<p>
				Ist die Aktualisierung gespeichert, schlägt das System bei jeder neu
				erstellten Umfrage die konfigurierte Sprache als Hauptsprache vor.
			</p>
			<h1>
				Datenschutz und Privatsphäre
			</h1>
			<h2>
				Dieses System verwendet Cookies. Welche Informationen werden dabei
				gespeichert?
			</h2>
			<p>
				Das Informatiksystem verwendet sogenannte Cookies, um die Kommunikation
				zwischen Client und Server zu gewährleisten.
			</p>
			<p>
				Die Nutzer müssen ihren Browser also so konfiguriert haben, dass er Cookies
				akzeptiert. Nach dem Abmelden werden die Cookies gelöscht.
			</p>
			<p>
				Das System speichert die Antworten der Befragten lokal, um beispielsweise
				bei einer Unterbrechung der Serververbindung während der Übermittlung des
				Beitrags oder bei versehentlichem Abschalten des Computers über eine
				Sicherheitskopie zu verfügen.
			</p>
			<p>
				Gespeichert werden die Kennungen der Fragen und die zugehörigen Antworten
				in der jeweils letzten Fassung.
			</p>
			<p>
				Sobald Teilnehmer ihre Antwort an den Server übermittelt oder einen Entwurf
				auf dem Server gespeichert haben, werden die lokal gespeicherten Daten
				gelöscht.
			</p>
			<p>
				Über der Umfrage befindet sich ein Kästchen „Lokale Sicherheitskopie
				anlegen (bei öffentlichen / gemeinsam genutzten Computern deaktivieren)“,
				mit der diese Funktion ausgeschaltet werden kann, sodass keine Daten auf
				dem verwendeten Computer gespeichert werden.
			</p>
			<h2>
				Welche Informationen speichert EUSurvey, wenn Befragte eine Antwort
				einsenden?
			</h2>
			<p>
				Dies hängt von den Sicherheitseinstellungen Ihrer Umfrage sowie der Art und
				Weise ab, wie Sie Ihre Teilnehmer zu Ihrer Umfrage einladen.
			</p>
			<p>
				<b>Öffentlich zugängliche, offene Umfragen:</b>
			</p>
			<p>
				Standardmäßig ist Ihre Umfrage <b>nicht geschützt</b> und EUSurvey    <b>speichert keine nutzerbezogenen Informationen</b>.
			</p>
			<p>
				Zusätzlich wird aus Sicherheitsgründen bei jedem Server-Zugriff die
				IP-Adresse der Verbindung gespeichert (siehe
				<a
						href="https://ec.europa.eu/eusurvey/home/privacystatement"
						target="_blank"
						>
					Datenschutzerklärung
				</a>
				).
			</p>
			<p>
				<b>Passwortgeschützte Umfragen:</b>
			</p>
			<p>
				Ist Ihre Umfrage <b>nur passwortgeschützt</b>, speichert EUSurvey keine
				nutzerbezogenen Informationen.
			</p>
			<p>
				Zusätzlich wird aus Sicherheitsgründen bei jedem Server-Zugriff die
				IP-Adresse der Verbindung gespeichert (siehe
				<a
						href="https://ec.europa.eu/eusurvey/home/privacystatement"
						target="_blank"
						>
					Datenschutzerklärung
				</a>
				).
			</p>
			<p>
				<b>Umfragen mit EU Login-Authentifizierung:</b>
			</p>
			<p>
				Ist Ihre Umfrage mittels <b>EU Login-Authentifizierung</b> gesichert,    <b>speichert </b>EUSurvey die E-Mail-Adresse des EU Login-Kontos des
				Nutzers.
			</p>
			<p>
				Zusätzlich wird aus Sicherheitsgründen bei jedem Server-Zugriff die
				IP-Adresse der Verbindung gespeichert (siehe
				<a
						href="https://ec.europa.eu/eusurvey/home/privacystatement"
						target="_blank"
						>
					Datenschutzerklärung
				</a>
				).
			</p>
			<p>
				<b>Einladungen mit EUSurvey verschicken:</b>
				<b></b>
			</p>
			<p>
				Wenn Sie die Einladungen an Ihre Teilnehmer über die Gästeliste in EUSurvey
				verschicken, erhält jeder Teilnehmer einen <b>persönlichen Zugangslink</b>.
			</p>
			<p>
				EUSurvey speichert dabei eine Einladungsnummer, die später genutzt werden
				kann, um die eingeladenen Teilnehmer mit deren eingereichten Beiträgen    <b>zu verknüpfen</b>. Dieser Schritt ist unabhängig von den
				Sicherheitseinstellungen Ihrer Umfrage.
			</p>
			<p>
				Ferner wird aus Sicherheitsgründen bei jedem Server-Zugriff die IP-Adresse
				der Verbindung gespeichert (siehe
				<a
						href="https://ec.europa.eu/eusurvey/home/privacystatement"
						target="_blank"
						>
					Datenschutzerklärung
				</a>
				).
			</p>
			<p>
				<b>Erstellen einer anonymen Umfrage:</b>
			</p>
			<p>
				Sie können eine anonyme Umfrage erstellen, indem Sie den Modus „Anonyme Umfrage“ in den Umfrageeigenschaften verwenden. Wenn diese Option aktiviert ist, werden die Beiträge zu Ihrer Umfrage anonym sein, da EUSurvey keine persönlichen Daten wie IP-Adressen speichert. Wenn Sie möchten, dass Ihre Umfrage vollständig anonym ist, sollten Sie keine Fragen in Ihren Umfrageentwurf aufnehmen, die persönliche Daten erfassen.
			</p>
			<h2>
				Muss ich eine Datenschutzerklärung in meine Umfrage aufnehmen?
			</h2>
			<p>
				Das hängt von Ihren Fragen und von der Art der Daten ab, die Sie mit Ihrer
				Umfrage erheben.
			</p>
			<p>
				Beachten Sie, dass Personen Ihrer Zielgruppe möglicherweise nicht gewillt
				sind, an der Umfrage teilzunehmen, wenn Sie die Vertraulichkeit der
				übermittelten Daten nicht garantieren können.
			</p>
			<p>
				<b>Für Mitarbeiter der EU:</b>
			</p>
			<p>
				Beachten Sie die Bestimmungen zum „Schutz natürlicher Personen bei der
				Verarbeitung personenbezogener Daten …“ gemäß der
				<a
						href="https://eur-lex.europa.eu/legal-content/DE/TXT/?uri=uriserv%3AOJ.L_.2018.295.01.0039.01.ENG&amp;toc=OJ%3AL%3A2018%3A295%3ATOC"
						target="_blank"
						>
					Verordnung (EU) Nr. 2018/1725
				</a>
				.
			</p>
			<p>
				Werden personenbezogene Daten erhoben, muss eine Datenschutzerklärung
				zusammen mit dem Fragebogen veröffentlicht werden.
			</p>
			<p>
				Für die Genehmigung der Datenschutzerklärung wenden Sie sich bitte an den
				Datenschutzkoordinator Ihrer GD.
			</p>
			<p>
				Darüber hinaus müssen Sie jede Erhebung personenbezogener Daten dem
				Datenschutzbeauftragten (DSB) melden. Bitte wenden Sie sich an Ihren
				Datenschutzkoordinator, wenn Sie für die Meldung an den DSB Hilfe
				benötigen.
			</p>
			<p>
				Nachstehend finden Sie ein Muster für Datenschutzerklärungen, das Sie für
				Ihre Umfragen verwenden können. Sie müssen das Muster ändern und an Ihre
				Bedürfnisse anpassen:
			</p>
			<p>
				Muster:
				<u>
					<a
							href="https://circabc.europa.eu/ui/group/599f39d2-e0cc-4765-bfdc-c9917c931509/library/dfed4f34-fa25-42ed-af44-e1acc4f0a58f/details"
							>
						„Muster für Datenschutzerklärungen für Umfragen und Konsultationen“
					</a>
				</u>
			</p>
			<h2>
				Es wird eine Vereinbarung zur Datenverarbeitung benötigt. Wo finde ich die DPA von EUSurvey?
			</h2>
			<p>Die EUSurvey Vereinbarung zur Datenverarbeitung (EUSurvey Data Processing Agreement, DPA) ist für
				alle Personen oder Organisationen verfügbar, die als Datenverantwortliche gelten, während wir Ihre
				Daten über die EUSurvey-Plattform verarbeiten. Die DPA von EUSurvey ist
				<a href = "${contextpath}/home/dpa">hier</a> verfügbar.
			</p>
			<p><a href="${contextpath}/home/support?dataprotection=1">Kontaktieren Sie uns</a>‚
				wenn Sie Fragen haben.</p>

			<h2>
				Jemand hat mich kontaktiert, um auf seine/ihre personenbezogenen Daten zuzugreifen, sie zu ändern, ganz oder teilweise zu löschen - was muss ich tun?
			</h2>
			<p>
				<div><b>VERORDNUNG (EU)  2018/1725</b></div>
				<div><b>Auskunftsrecht der betroffenen Person - Artikel 17</b></div>
				<div><b>Recht auf Berichtigung - Artikel 18</b></div>
				<div><b>Recht auf Löschung („Recht auf Vergessenwerden“) - Artikel 19</b></div>
			</p>
			<p>
				Umfragemanager sind für die Verwaltung der im Rahmen der Umfrage erhobenen personenbezogenen Daten verantwortlich.
				Als Umfragemanager müssen Sie auf Anfragen Ihrer Umfrageteilnehmer in Bezug auf den Datenschutz reagieren.
			</p>
			<p>
				Um auf die im Rahmen Ihrer Umfrage erhobenen personenbezogenen Daten zuzugreifen, sie zu berichtigen oder zu löschen, können Sie unter „Ergebnisse" mit Hilfe der verfügbaren Filter nach den betreffenden personenbezogenen Daten suchen.
				Anschließend können Sie diese entweder entfernen oder ändern, indem Sie den jeweiligen Beitrag bearbeiten oder ihn ganz löschen.
			</p>
			<p>
				<figure>
					<img alt="Screenshot" style="max-width: 920px" src="${contextpath}/resources/images/documentation/personal_data_modification.png">
				</figure>
			</p>
			<p>
				Was E-Mail-Adressen sowie Vor- und Nachnamen betrifft, so sind die anderen Teile des Systems, in denen diese Daten gespeichert werden können, die Gästelisten (Registerkarte „Teilnehmer“) oder Ihr „Adressbuch“.
			</p>
			<h2>
				Mehrere Antworten gleichzeitig löschen
			</h2>
			<p>
				Sie können eine ganze Spalte aus Ihren Umfrageergebnissen löschen.
				Dies hat zur Folge, dass alle Antworten der entsprechenden Frage „geleert“ (d. h. endgültig gelöscht) werden.
				Das kann nützlich sein, um die Ergebnisse zu anonymisieren, z. B. im Zusammenhang mit der Einhaltung der Datenschutz-Grundverordnung (DSGVO).
			</p>
			<p>
				Schritte zum Löschen mehrerer Antworten auf einmal:
			</p>
			<ol>
				<li>Gehen Sie zur Registerkarte „Ergebnisse“.</li>
				<li>Suchen Sie die Spalte, in der mehrere Antworten gleichzeitig gelöscht werden sollen.</li>
				<li>Nutzen Sie das Drei-Punkte-Menü.</li>
				<li>Wählen Sie „Antworten löschen“ aus.</li>
				<li>Bestätigen Sie das Löschen.</li>
			</ol>
			<p>
				Alle Antworten der ausgewählten Frage werden endgültig gelöscht.
			</p>

			</div>
			</div>
			</div>

	<%@ include file="../footer.jsp" %>	

</body>
</html>