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
				<div class="alert alert-warning">Information important: Pour créer et gérer un questionnaire, veuillez accéder à EUSurvey à partir d'un ordinateur. Il n'est pas recommandé de se connecter à EUSurvey à partir d'un téléphone portable ou tablette.</div>
			</c:when>
			<c:otherwise>
				<div class="page underlined" style="padding-top: 40px;">
			</c:otherwise>
		</c:choose>	
		
			<div class="pageheader">
				<div style="float:right; font-size:125%" >
				[<a href="helpparticipants?faqlanguage=en">EN</a>] [<a href="helpparticipants?faqlanguage=fr">FR</a>] [<a href="helpparticipants?faqlanguage=de">DE</a>]
				</div>
				<h1>Aide pour les participants</h1>
			</div>
	
			<h2>Contenu</h2>
			<div id="ulContainer">
		
				<a href="javascript:ddtreemenu.flatten('treemenu', 'expand')">Expand All</a>&nbsp;|&nbsp;<a href="javascript:ddtreemenu.flatten('treemenu', 'contact')">Collapse All</a>
				<br/><br/>
				<ul id="treemenu" class="treeview" rel="closed">
				
			</div>
			
			<div id="faqcontent">
				<h1>
					Connexion/création d’un compte
				</h1>
				<h2>
					J’ai un compte EU Login. Dois-je aussi créer un compte EUSurvey?
				</h2>
				<p>
					Non, un compte EU Login est suffisant. 
				</p>
				<p>
					Pour accéder à EUSurvey, cliquez sur le bouton «Login» sur la <a href="https://ec.europa.eu/eusurvey/home/welcome?language=fr">page d’accueil d’EUSurvey</a>.
				</p>
				<h2>
					Comment puis-je me connecter à EUSurvey?
				</h2>
				<p>
					Après avoir cliqué sur «Login» sur la <a href="https://ec.europa.eu/eusurvey/home/welcome?language=fr">page d’accueil d’EUSurvey</a>, vous serez redirigé vers l’écran de connexion à EUSurvey.
				</p>
				<p>
					Une fois arrivé sur l’écran de connexion, vous devez choisir l’option correspondant à votre cas personnel:
					<ul>
						<li>
							<b>Si vous travaillez pour une institution de l’UE</b>, choisissez la seconde option pour vous connecter à l’aide de votre nom d’utilisateur et de votre mot de passe EU Login.
						</li>
						<li>
							<b>Si vous ne travaillez pas pour une institution de l’UE (utilisateurs externes)</b>), choisissez la première option pour vous connecter. Vous devrez avoir préalablement enregistré votre téléphone portable pour passer <a href="https://meta.wikimedia.org/wiki/Help:Two-factor_authentication/fr">l’authentification à deux facteurs</a>.
						</li>
					</ul>
				</p>
				<p>
					<a href="https://webgate.ec.europa.eu/cas/eim/external/register.cg">Créez un compte EU Login</a> (si ce n’est déjà fait)
				</p>
				<p>
					<a href="https://ecas.ec.europa.eu/cas/userdata/mobileApp/manageMyMobileDevices.cgi">Enregistrez votre téléphone portable</a> (si vous ne travaillez pas pour une institution de l’UE)
				</p>
			
				<h1>
					Contacter le propriétaire de l'enquête
				</h1>
				<h2>
				    Comment puis-je entrer en contact avec le propriétaire de l’enquête?
				</h2>
				<p>
				    Utilisez l’option «Contact» dans le panneau à droite.
				</p>
				<h1>
				    Accéder à une enquête
				</h1>
				<h2>
				    Que signifie le message suivant? «L'adresse URL que vous avez indiquée
				    n'est pas correcte»
				</h2>
				<p>
			        EUSurvey ne peut pas vous autoriser à accéder à l’enquête.
				</p>
				<p>
				    Cela signifie qu’une invitation précédemment active a été supprimée ou
				    désactivée parce que la période d'activation a expiré.
				</p>
				<p>
				    Si vous estimez que votre lien d'accès est valide, veuillez prendre contact
				    avec l'auteur de l'enquête.
				</p>
				<h2>
				    Que signifie le message suivant? «Page non trouvée»
				</h2>
				<p>
				    Deux possibilités:
				</p>
				<ul>
				    <li>
				        soit vous utilisez un lien incorrect pour accéder à l'enquête; or
				    </li>
				    <li>
				        soit l’enquête à laquelle vous souhaitez accéder a déjà été retirée.
				    </li>
				</ul>
				<p>
				    Si vous pensez que le lien est valide, adressez-vous à l'auteur de
				    l'enquête. Sinon, informez l'organisme qui a publié le lien incorrect.
				</p>
				<h2>
			        Que signifie le message suivant? «Cette enquête n’a pas encore été
			        publiée ou a déjà été dépubliée entre-temps»
				</h2>
				<p>
				    Cela signifie que l’invitation à participer a été envoyée mais que les
				    organisateurs de l’enquête ne l’ont pas encore publiée ou l’ont déjà
				    dépubliée.
				</p>
				<p>
				    Pour de plus amples informations, veuillez contacter le propriétaire de
				    l’enquête.
				</p>
				<h2>
				    Avec quels navigateurs l’application EUSurvey est-elle compatible?
				</h2>
				<p>
				    Microsoft Edge (deux dernières versions) ainsi que Mozilla Firefox et
				    Google Chrome (dernières versions).
				</p>
				<p>
				    L’utilisation d’autres navigateurs pourrait poser des problèmes de
				    compatibilité.
				</p>
				<h2>
				    Puis-je répondre à une enquête à partir de mon téléphone portable ou de ma
				    tablette PC?
				</h2>
				<p>
				    Oui, la conception du questionnaire s’adaptera à la taille et à la
				    résolution de l’écran de votre appareil.
				</p>
				<h1>
				    Répondre à une enquête
				</h1>
				<h2>
				    Que signifient les messages suivants? «Cette valeur n'est pas un nombre
				    valide», «Cette valeur n'est pas une date valide» et «Cette adresse
				    électronique n'est pas valide»
				</h2>
				<p>
				    L'auteur d'une enquête peut préciser certains types de questions qui
				    impliquent de répondre dans un format particulier: nombre, date ou adresse
				    électronique, par exemple.
				</p>
				<p>
				    Les dates doivent être au format JJ/MM/AAAA.
				</p>
				<h2>
				    Pourquoi ma sélection disparaît-elle lorsque je réponds à une question en
				    matrice?
				</h2>
				<p>
				    Certaines questions en matrice sont configurées pour ne permettre de
				    sélectionner qu'une seule réponse à la fois. Cette fonctionnalité est
				    parfois utilisée pour classer les réponses données.
				</p>
				<h1>
				    Après avoir soumis votre contribution
				</h1>
				<h2>
				    Puis-je visualiser/imprimer mes réponses une fois que je les ai soumises?
				</h2>
				<p>
				    Oui.
				</p>
				<p>
				    Après avoir soumis vos réponses, cliquez sur l’option d’impression proposée
				    sur la page de confirmation.
				</p>
				<h2>
				    Puis-je sauvegarder mes réponses en format PDF?
				</h2>
				<p>
				    Oui.
				</p>
				<p>
				    Après avoir soumis vos réponses, vous pouvez demander qu’une version PDF de
				    votre contribution vous soit envoyée par courrier électronique.
				</p>
				<p>
				    Cliquez sur «Obtenir le PDF» sur la page de confirmation.
				</p>
				<h2>
				    Puis-je modifier mes réponses une fois que je les ai soumises?
				</h2>
				<p>
				    Tout dépend de la façon dont l'enquête a été configurée.
				</p>
				<p>
				    Certaines enquêtes permettent de
				    <a
				        href="https://ec.europa.eu/eusurvey/home/editcontribution?language=fr"
				        target="_blank"
				    >
				        réaccéder à une contribution après soumission
				    </a>
				    , tandis que d'autres n'offrent pas cette possibilité.
				</p>
				<p>
				    Pour modifier la contribution que vous avez soumise, utilisez le numéro
				    d’identification de la contribution que vous avez reçu à la page de
				    confirmation. Ce numéro d’identification figure également sur certains
				    documents PDF relatifs à votre contribution.
				</p>
				<p>
				    Si vous n’avez pas sauvé le numéro d’identification de votre contribution,
				    cliquez sur le formulaire «
				    <a href="https://ec.europa.eu/eusurvey/home/support?language=fr">
				        Contactez-nous
				    </a>
				    ».
				</p>
				<h2>
					Qu'est-ce que mon «Numéro d'identification de la contribution» ?
				</h2>
				<p>
					«Numéro d'identification de la contribution» est le numéro individuel de votre réponse qui est généré automatiquement. Il s'agit d'un identifiant unique permettant au système de reconnaître votre contribution.
				</p>
				<p>
					Une fois votre contribution soumise, votre «Numéro d'identification de la contribution» est affiché sur la page de confirmation. Si vous enregistrez le PDF ou l'envoyez à votre adresse électronique, vous verrez «Numéro d'identification de la contribution» dans la partie supérieure gauche du PDF de la contribution.
				</p>
				<p>
					Votre identifiant de contribution est utile si vous souhaitez contacter le service d'assistance EUSurvey ou les organisateurs de l'enquête.
				</p>
				<h2>
					Où puis-je trouver mon «Numéro d'identification de la contribution» ?
				</h2>
				<p>
					Il est affiché juste après la soumission de vos réponses.
					<ol>
						<li>
							Ouvrez le lien de votre enquête et répondez à l'enquête
						</li>
						<li>
							Une fois les réponses soumises, vous verrez votre «Numéro d'identification de la contribution» à l'écran
						</li>
						<li>
							Envoyez le PDF de la contribution à votre adresse électronique
						</li>
						<li>
							Ouvrir la contribution PDF
						</li>
						<li>
							«Numéro d'identification de la contribution» sera affiché dans la partie supérieure gauche du PDF
						</li>
					</ol>
				</p>
				<h2>
				    Je viens de répondre à une enquête. Puis-je consulter les réponses des
				    autres participants?
				</h2>
				<p>
				    Tout dépend de la façon dont l'enquête a été configurée.
				</p>
				<p>
				    Si aucun lien ne s'affiche vers les résultats publiés une fois que vous
				    avez soumis votre contribution, il se peut que cette fonctionnalité ne soit
				    pas disponible.
				</p>
				<p>
				    Si vous estimez que les résultats d'une enquête pourraient présenter un
				    intérêt public, contactez l'
				    <a
				        href="https://ec.europa.eu/eusurvey/home/helpparticipants?faqlanguage=fr#_Toc1"
				    >
				        auteur
				    </a>
				    de l'enquête.
				</p>
				<h2>
					Ma contribution n'a pas été soumise à temps. Que puis-je faire ?
				</h2>
				<p>
					Veuillez contacter directement les organisateurs de l'enquête. Si les coordonnées des organisateurs de l'enquête ne sont pas disponibles sur la page de l'enquête, veuillez <a href="https://ec.europa.eu/eusurvey/home/support">contacter</a> l'équipe d'assistance EUSurvey
				</p>
				<h2>
				    Pourquoi mon visionneur de fichiers PDF génère-t-il le message d'erreur
				    suivant: «Insufficient Image data» (données d'image insuffisantes)?
				</h2>
				<p>
				    Si vous chargez des images corrompues, le visionneur de PDF ne peut pas
				    afficher correctement l’image.
				    <br/>
				    Cala provoquera une erreur interne du visionneur de fichiers PDF.
				</p>
				<p>
				    Pour y remédier, vous devez soit réparer le fichier image, soit le retirer.
				</p>
				<h2>
				    Pourquoi de petits carrés apparaissent-ils dans l'enquête exportée au
				    format PDF?
				</h2>
				<p>
				    Ces carrés peuvent apparaître si les auteurs de l’enquête ou les
				    participants utilisent des polices de caractères qui ne sont pas
				    compatibles avec EUSurvey.
				</p>
				<p>
				    Si EUSurvey ne dispose pas du caractère que vous souhaitez, ce caractère
				    est remplacé par un petit carré pour montrer qu’il n’est pas compatible
				    avec le moteur de création de PDF.
				</p>
				<p>
				    Si vous utilisez un caractère non compatible, vous êtes invité à le
				    signaler via la section «Contact» située sur le côté droit.
				</p>
				<p>
				    Cela n'a aucune incidence sur votre contribution. Une fois que vos réponses
				    ont été enregistrées correctement, elles peuvent facilement être
				    visualisées et exportées par l’auteur de l’enquête, même si le moteur de
				    création de PDF est incapable d’afficher votre PDF correctement.
				</p>
				<h2>
				    Où puis-je trouver les brouillons enregistrés de mes réponses?
				</h2>
				<p>
				    Après avoir cliqué sur «Enregistrer comme brouillon», vous serez
				    automatiquement redirigé vers une page comportant un lien où vous pouvez
				    récupérer votre brouillon afin de l’éditer et de soumettre vos réponses.
				</p>
				<p>
				    <b>Veillez à enregistrer ce lien!</b>
				    Envoyez-le par mail, enregistrez-le dans vos favoris ou copiez-le dans le
				    presse-papier «clipboard».
				</p>
				<h1 class="empty">
				    Signaler un abus
				</h1>
				<p>
				    Cliquez sur «Signaler un abus» dans le panneau à droite si une enquête
				    comporte un contenu illicite ou viole les droits d’autrui (y compris les
				    droits de propriété intellectuelle, le droit de la concurrence et le droit
				    général).
				</p>
				<p>
				    Pour de plus amples informations, voir les conditions d’utilisation
				d’EUSurvey dans la rubrique    <a href="https://ec.europa.eu/eusurvey/home/tos">Conditions de service</a>.
				</p>
				<h1>
				    Protection de la vie privée
				</h1>
				<h2>
				    Ce système utilise des cookies. Quelles informations y sont enregistrées?
				</h2>
				<p>
				    EUSurvey utilise des «cookies» (ou témoins) de session afin d’assurer une
				    bonne communication entre le client et le serveur. Votre navigateur doit
				    être configuré pour les accepter. Ils disparaissent une fois la session
				    terminée.
				</p>
				<p>
				    Le système enregistre une copie de sauvegarde locale de votre contribution
				    à l’enquête, qui sert en cas d'indisponibilité du serveur lorsque vous
				    envoyez votre contribution, ou si votre ordinateur s'éteint
				    accidentellement, par exemple.
				</p>
				<p>
				    La copie locale contient le numéro des questions et les brouillons de vos
				    réponses.
				</p>
				<p>
				    Une fois votre contribution à l’enquête envoyée au serveur, ou après
				    sauvegarde d'un brouillon sur celui-ci, ces données locales sont
				    supprimées.
				</p>
				<p>
				    Vous pouvez désactiver cette fonction en décochant la case située au-dessus
				    de l’enquête avec la mention: «Enregistrer une copie de sauvegarde locale
				    sur votre ordinateur (décochez cette case si vous utilisez un ordinateur
				    public/partagé)» Aucune donnée ne sera conservée par la suite sur votre
				    ordinateur.
				</p>
			
			</div>
			
		</div>
	</div>

	<%@ include file="../footer.jsp" %>		

</body>
</html>