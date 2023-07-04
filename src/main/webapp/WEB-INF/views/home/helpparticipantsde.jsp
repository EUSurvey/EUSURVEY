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
			margin: 20px;
		}				
		
		.anchorlink {
			margin-left: 40px;
			color: #005580;
		}
		
		.anchorlink a:hover {
			text-decoration: underline;
			color: #005580;
		}
		
		.head {
			margin-left: 20px;
		}
		
		.empty {
			margin-left: 0px;
			text-decoration: none;
		}

		#ulContainer {
			margin-bottom: 50px;
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
				<div class="page underlined" style="padding-top: 110px">
			</c:when>
			<c:when test="${responsive != null}">
				<div class="page underlined" style="max-width: 100%; padding: 10px; padding-top: 40px;">
				<div class="alert alert-warning">Wichtige Information: Um Umfragen zu erstellen und zu verwalten, öffnen Sie die EUSurvey Website bitte mit einem Computer. Es ist nicht zu empfehlen sich mit einem Handy oder Tablet in EUSurvey einzuloggen.</div>
			</c:when>
			<c:otherwise>
				<div class="page underlined" style="padding-top: 40px;">
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
				
				</ul>
			</div>
			
			<div id="faqcontent">
				<h1>
					Einloggen/Konto erstellen
				</h1>
				<h2>
					Ich habe ein EU-Login-Konto. Muss ich mich separat bei EUSurvey registrieren?
				</h2>
				<p>
					Nein, ein EU-Login-Konto ist ausreichend. 
				</p>
				<p>
					Für den Zugang zu EUSurvey klicken Sie bitte auf der <a href="https://ec.europa.eu/eusurvey/home/welcome">EUSurvey-Startseite</a> auf die Anmeldeschaltfläche
				</p>
				
				<h2>
					Wie verbinde ich mich mit EUSurvey?
				</h2>
				<p>
					Klicken Sie auf den Login-Button auf der <a href="https://ec.europa.eu/eusurvey/home/welcome">EUSurvey-Homepage</a>, Sie werden dann zum EUSurvey-Anmeldebildschirm weitergeleitet.
				</p>
				<p>
					Dort können Sie die Option auswählen, die Ihrer persönlichen Situation entspricht:
					<ul>
						<li>
							<b>Wenn Sie für eine EU-Institution arbeiten</b>, wählen Sie die zweite Option, und verwenden Sie Ihren EU-Login-Benutzernamen und Ihr Passwort.
						</li>
						<li>
							<b>Wenn Sie nicht für eine EU-Institution arbeiten (externe Nutzer)</b>wählen Sie die erste Option. Sie müssen Ihr Mobiltelefon zuvor registriert haben, um die <a href="https://de.wikipedia.org/wiki/Hilfe:Zwei-Faktor-Authentifizierung">Zwei-Faktor-Authentifizierung</a> zu durchlaufen.
						</li>
					</ul>
				</p>
				<p>
					<a href="https://webgate.ec.europa.eu/cas/eim/external/register.cg">EU-Login-Konto erstellen</a> (sofern nicht bereits vorhanden)
				</p>
				<p>
					<a href="https://ecas.ec.europa.eu/cas/userdata/mobileApp/manageMyMobileDevices.cgi">Registrieren Sie bitte Ihr Mobiltelefon</a> (sofern Sie nicht für eine EU-Institution arbeiten)
				</p>
			
				<h1>
					Kontaktaufnahme mit dem Eigentümer der Umfrage
				</h1>
				<h2>
				    Wie kann ich mit dem Eigentümer der Umfrage Kontakt aufnehmen?
				</h2>
				<p>
				    Wählen Sie auf der rechten Seite die Option „Kontakt“.
				</p>
				<h1>
				    Eine Umfrage einsehen
				</h1>
				<h2>
				    Was bedeutet die Meldung „Die eingegebene URL ist falsch“?
				</h2>
				<p>
			        EUSurvey kann Ihnen keinen Zugang zu der Umfrage gewähren.
				</p>
				<p>
				    Dies bedeutet, dass eine zuvor aktive Einladung gelöscht oder deaktiviert
				    wurde, weil der Aktivierungszeitraum abgelaufen ist.
				</p>
				<p>
				    Wenn Sie glauben, der von Ihnen benutzte Link doch gültig ist, wenden Sie
				    sich bitte an die Verfasser der Umfrage.
				</p>
				<h2>
				    Was bedeutet „Seite nicht gefunden“?
				</h2>
				<p>
				    Dies bedeutet entweder,
				</p>
				<ul>
				    <li>
				        dass Sie für den Zugriff auf die Umfrage einen ungültigen Link
				        verwenden oder
				    </li>
				    <li>
				        dass die Umfrage, auf die Sie zugreifen möchten, bereits entfernt
				        wurde.
				    </li>
				</ul>
				<p>
				    Wenn Sie glauben, dass der Link doch gültig ist, wenden Sie sich bitte an
				    die Verfasser der Umfrage. Andernfalls informieren Sie bitte die Stelle,
				    die den ungültigen Link veröffentlicht hat.
				</p>
				<h2>
			        Was bedeutet die Meldung „Diese Umfrage wurde noch nicht veröffentlicht
			        oder bereits wieder vom Portal entfernt“?
				</h2>
				<p>
				    Dies bedeutet, dass die Einladung zur Teilnahme verschickt wurde, die
				    Organisatoren der Umfrage diese jedoch noch nicht veröffentlicht oder
				    bereits wieder entfernt haben.
				</p>
				<p>
				    Für weitere Einzelheiten wenden Sie sich bitte an den Eigentümer der
				    Umfrage.
				</p>
				<h2>
				    Welche Browser unterstützt EUSurvey?
				</h2>
				<p>
				    Microsoft Edge (die beiden letzten Versionen) sowie Mozilla Firefox und
				    Google Chrome (die neuesten Versionen).
				</p>
				<p>
				    Die Verwendung anderer Browser kann Kompatibilitätsprobleme verursachen.
				</p>
				<h2>
				    Kann ich eine Umfrage auf meinem Mobiltelefon oder Tablet-PC beantworten?
				</h2>
				<p>
				    Ja, das Layout des Fragebogens passt sich an die Größe und Auflösung des
				    verwendeten Bildschirms an.
				</p>
				<h1>
				    Einen Beitrag einreichen
				</h1>
				<h2>
				    Was bedeuten die Meldungen „Dies ist keine gültige Zahl/kein gültiges
				    Datum/keine gültige E-Mail-Adresse“?
				</h2>
				<p>
				    Die Verfasser der Umfrage können für manche Fragentypen ein bestimmtes
				    Eingabeformat vorgeben, z. B. eine Zahl, ein Datum oder eine
				    E-Mail-Adresse.
				</p>
				<p>
				    Daten sind im Format TT/MM/JJJJ anzugeben.
				</p>
				<h2>
				    Warum verschwindet die von mir ausgewählte Antwort auf eine Matrix-Frage?
				</h2>
				<p>
				    Manche Matrixfragen sind so konfiguriert, dass jede mögliche Antwort nur
				    einmal ausgewählt werden kann. Dies wird manchmal verwendet, um die
				    gewählten Antworten in eine bestimmte Rangfolge einzuordnen.
				</p>
				<h1>
				    Nach dem Absenden des Beitrags
				</h1>
				<h2>
				    Kann ich meinen Beitrag nach dem Absenden einsehen oder drucken?
				</h2>
				<p>
				    Ja.
				</p>
				<p>
				    Klicken Sie hierfür nach dem Absenden auf der „Bestätigungs“-Seite auf die
				    Option „Drucken“.
				</p>
				<h2>
				    Kann ich meinen Beitrag als PDF-Datei speichern?
				</h2>
				<p>
				    Ja.
				</p>
				<p>
				    Nach dem Absenden können Sie per E-Mail eine PDF-Version Ihres Beitrags
				    anfordern.
				</p>
				<p>
				    Klicken Sie auf der „Bestätigungs“-Seite auf „PDF-Datei erstellen“.
				</p>
				<h2>
				    Kann ich meinen Beitrag nach dem Absenden weiter bearbeiten?
				</h2>
				<p>
				    Dies richtet sich jeweils nach den für die Umfrage gewählten Einstellungen.
				</p>
				<p>
				    Bei manchen Umfragen können Sie den Beitrag
				    <a
				        href="https://ec.europa.eu/eusurvey/home/editcontribution"
				        target="_blank"
				    >
				        nach dem Absenden erneut bearbeiten
				    </a>
				    , in anderen Fällen ist diese Funktion nicht aktiviert.
				</p>
				<p>
				    Um Ihren bereits eingereichten Beitrag zu bearbeiten, geben Sie bitte die
				    Beitrags-Kennnummer an, die auf der „Bestätigungs“-Seite angegeben wurde.
				    Diese ID erscheint auch auf manchen PDF-Dokumenten zu Ihrem Beitrag.
				</p>
				<p>
				    Wenn Sie die Beitrags-Kennnummer nicht gespeichert haben, klicken Sie bitte
				    auf die Option
				    <a href="https://ec.europa.eu/eusurvey/home/support">
				        Kontaktieren Sie uns
				    </a>
				    .
				</p>
				<h2>
					Was ist meine „Kennnummer des Beitrags“?
				</h2>
				<p>
					Die „Kennnummer des Beitrags“ ist die individuelle Nummer Ihrer Antwort, die automatisch generiert wird. Sie ist eine eindeutige Kennung, die es dem System ermöglicht, Ihren Beitrag zu erkennen.
				</p>
				<p>
					Sobald Ihr Beitrag übermittelt wurde, wird Ihre Kennnummer auf der Bestätigungsseite angezeigt. Wenn Sie die PDF-Datei speichern oder an Ihre E-Mail-Adresse senden, sehen Sie die Kennnummer oben links in der PDF-Datei.
				</p>
				<p>
					Ihre Kennnummer ist nützlich, falls Sie den EUSurvey-Support oder die Organisatoren der Umfrage kontaktieren möchten.
				</p>
				<h2>
					Wo kann ich meine „Kennnummer des Beitrags“ finden?
				</h2>
				<p>
					Sie wird direkt nach dem Absenden Ihrer Antworten angezeigt.
					<ol>
						<li>
							Öffnen Sie Ihren Umfragelink und beantworten Sie die Umfrage
						</li>
						<li>
							Nach dem Absenden Ihres Beitrags wird die Kennnummer auf der Bestätigungsseite angezeigt.
						</li>
						<li>
							Senden Sie die Beitrags-PDF-Datei an Ihre E-Mail
						</li>
						<li>
							Öffnen Sie den PDF-Beitrag
						</li>
						<li>
							Die Kennnummer wird auf der linken oberen Seite des PDF-Dokuments angezeigt
						</li>
					</ol>
				</p>
				<h2>
				    Ich habe gerade einen Beitrag zu einer Umfrage eingesandt. Kann ich
				    einsehen, was andere Befragte geantwortet haben?
				</h2>
				<p>
				    Dies richtet sich jeweils nach den für die Umfrage gewählten Einstellungen.
				</p>
				<p>
				    Wenn Ihnen nach dem Absenden Ihres Beitrags kein Link zu den
				    veröffentlichten Ergebnissen angezeigt wird, ist diese Funktion
				    möglicherweise nicht verfügbar.
				</p>
				<p>
				    Wenn Sie der Ansicht sind, dass die Ergebnisse dieser Umfrage von
				    öffentlichem Interesse sein könnten, wenden Sie sich bitte an den
				    <a
				        href="#_Toc_1_0"
				    >
				        Verfasser
				    </a>
				    der Umfrage.
				</p>
				<h2>
					Mein Beitrag wurde nicht fristgerecht eingereicht. Was kann ich tun?
				</h2>
				<p>
					Bitte wenden Sie sich direkt an die Organisatoren der Umfrage. Wenn die Kontaktdaten der Umfrageorganisatoren nicht auf der Umfrageseite verfügbar sind, wenden Sie sich bitte an das  <a href="https://ec.europa.eu/eusurvey/home/support">EUSurvey Support-Team</a>.
				</p>
				<h2>
				    Warum erscheint beim Öffnen der PDF Version meines Beitrags die
				    Fehlermeldung „Unzureichende Bildinformation“?
				</h2>
				<p>
				    Wird ein beschädigtes Bild hochgeladen, kann der PDF-Leser es nicht
				    ordnungsgemäß anzeigen.
				    <br/>
				    Dies löst einen internen Fehler in Ihrem PDF-Leser aus.
				</p>
				<p>
				    In einem solchen Fall müssen Sie das Bild entweder reparieren oder
				    entfernen.
				</p>
				<h2>
				    Warum erscheinen in der PDF-Exportdatei der Umfrage kleine Kästchen?
				</h2>
				<p>
				    Solche Kästchen können erscheinen, wenn die Verfasser oder Teilnehmer der
				    Umfrage Schriftarten verwenden, die nicht von EUSurvey unterstützt werden.
				</p>
				<p>
				    Wenn EUSurvey nicht über das entsprechende Zeichen verfügt, wird es durch
				    ein kleines Kästchen ersetzt, um anzuzeigen, dass es nicht im PDF-Format
				    wiedergegeben werden kann.
				</p>
				<p>
				    Wenn Sie nicht unterstützte Zeichen verwenden, sollten Sie dies über die
				    Rubrik „Kontakt“ auf der rechten Seite melden.
				</p>
				<p>
				    Dies hat keinen Einfluss auf Ihren Beitrag. Sobald Ihre Antworten korrekt
				    gespeichert wurden, können sie vom Verfasser der Umfrage leicht eingesehen
				    und exportiert werden, auch wenn das PDF-Rendering-Engine nicht in der Lage
				    ist, Ihre PDF-Datei korrekt anzuzeigen.
				</p>
				<h2>
				    Wo finde ich meine als Entwurf gespeicherten Antworten?
				</h2>
				<p>
				    Nachdem Sie auf „Als Entwurf speichern“ geklickt haben, werden Sie
				    automatisch auf eine andere Seite weitergeleitet. Über einen dort
				    aufgeführten Link können Sie auf ihren Entwurf zugreifen, um ihre Antworten
				    weiter zu bearbeiten und einzureichen.
				</p>
				<p>
				    <b>Bitte speichern Sie diesen Link!</b>
				    Sie können ihn per E-Mail versenden, zu Ihrer Favoritenliste hinzufügen
				    oder in die Zwischenablage kopieren.
				</p>
				<h1 class="empty">
				    Missbrauch melden
				</h1>
				<p>
				    Wenn eine Umfrage illegale Inhalte enthält oder die Rechte anderer verletzt
				    (einschließlich geistiger Eigentumsrechte, Wettbewerbsrecht und allgemeines
				    Recht), verwenden Sie bitte den Link „Missbrauch melden“ auf der rechten
				    Seite.
				</p>
				<p>
				Näheres hierzu siehe die    <a href="https://ec.europa.eu/eusurvey/home/tos">Nutzungsbedingungen</a>
				    von EUSurvey.
				</p>
				<h1>
				    Datenschutz
				</h1>
				<h2>
				    Dieses System verwendet Cookies. Welche Informationen werden dabei
				    gespeichert?
				</h2>
				<p>
				    EUSurvey verwendet sogenannte Sitzungscookies, um die Kommunikation
				    zwischen Client und Server zu gewährleisten. Ihr Browser muss so
				    konfiguriert sein, dass er Cookies akzeptiert. Nach Beendigung der Sitzung
				    werden die Cookies gelöscht.
				</p>
				<p>
				    Das System speichert Ihre Antworten zu einer Umfrage lokal, um
				    beispielsweise bei einer Unterbrechung der Serververbindung während der
				    Übermittlung der Antworten oder bei versehentlichem Abschalten des
				    Computers über eine Sicherheitskopie zu verfügen.
				</p>
				<p>
				    Gespeichert werden die Kennungen der Fragen und die zugehörigen Antworten
				    in der jeweils letzten Fassung.
				</p>
				<p>
				    Sobald Sie Ihre Antworten an den Server übermittelt oder einen Entwurf
				    erfolgreich auf dem Server gespeichert haben, werden die lokal
				    gespeicherten Daten gelöscht.
				</p>
				<p>
				    Sie können diese Funktion ausschalten, indem Sie folgendes Kästchen über
				    der Umfrage anklicken: „Lokale Sicherheitskopie anlegen (bei öffentlichen /
				    gemeinsam genutzten Computern deaktivieren)“. Danach werden hierzu keine
				    Daten mehr auf Ihrem Rechner gespeichert.
				</p>
			
			</div>
		</div>
	</div>

	<%@ include file="../footer.jsp" %>		

</body>
</html>
