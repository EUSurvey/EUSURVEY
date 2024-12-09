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
				<h1>Aide pour les auteurs</h1>
			</div>
			
			<h2>Contenu</h2>
			
	<div id="ulContainer">	
	
		<a href="javascript:ddtreemenu.flatten('treemenu', 'expand')">Tout ouvrir</a>&nbsp;|&nbsp;<a href="javascript:ddtreemenu.flatten('treemenu', 'contact')">Tout fermer</a>
		<br/>
		<br/>
		<ul id="treemenu" class="treeview" rel="closed">
			
		
		</ul>
	</div>
	
	<div id="faqcontent">

			<h1>
				Questions d’ordre général
			</h1>
			<h2>
				Qu’est-ce qu’EUSurvey?
			</h2>
			<p>
				EUSurvey est un outil en ligne qui permet de créer, publier et gérer des
				questionnaires et d’autres formulaires interactifs.
			</p>
			<h2>
				Quand utiliser EUSurvey?
			</h2>
			<p>
				Utilisez EUSurvey si vous souhaitez:
			</p>
			<p>
				<ul>
					<li>
						créer un questionnaire ou un formulaire interactif accessible en ligne;
					</li>
					<li>
						enregistrer un grand nombre de jeux de données semblables.
					</li>
				</ul>
			</p>
			<h2>
				Quelles sont les limitations techniques d’EUSurvey?
			</h2>
			<p>
				EUSurvey n’est peut-être pas adapté à votre projet dans les situations
				suivantes:
			</p>
			<ul>
				<li>
					si plusieurs participants doivent travailler sur la même contribution
					(réponse) avant qu’elle ne soit soumise;
				</li>
				<li>
					si les réponses doivent être validées avant de pouvoir être soumises.
				</li>
			</ul>
			<p>
				Pour de plus amples informations:
			</p>
			<p>
				<ul>
					<li>
						consultez la section «Taille et facilité d’utilisation» de la page
						<a href="https://ec.europa.eu/eusurvey/home/documentation?language=fr">
							aide
						</a>
						de l’outil;
					</li>		
					<li>
						contactez l’équipe d’assistance EUSurvey
						<a href="https://ec.europa.eu/eusurvey/home/support?language=fr">
							ici
						</a>
						.
					</li>
				</ul>
			</p>
			<h2>
				Quelles sont les fonctionnalités d’EUSurvey?
			</h2>
			<p>
				<b>Personnalisation des formulaires</b>
				<br/>
				Vous pouvez choisir différents types de questions, par exemple:
			</p>
			<ul>
				<li>
					texte simple et questions à choix multiple;
				</li>
				<li>
					tableaux;
				</li>
				<li>
					éléments multimédias.
				</li>
			</ul>
			<p>
				Vous pouvez structurer votre questionnaire au moyen d’éléments structurels
				spécifiques.
			</p>
			<p>
				<b>Questions dépendantes</b>
				<br/>
				EUSurvey peut afficher des questions et champs supplémentaires en fonction
				des réponses du participant, ce qui rend les questionnaires plus
				interactifs.
			</p>
			<p>
				<b>Programmation de la publication</b>
				<br/>
				Vous pouvez programmer la publication et la dépublication automatiques de
				votre enquête à la date et à l’heure que vous souhaitez.
			</p>
			<p>
				<b>Modification après publication</b>
				<br/>
				Vous pouvez modifier une enquête publiée sans perdre aucune contribution.
			</p>
			<p>
				<b>Langues</b>
				<br/>
				L’interface utilisateur est disponible dans 23 langues de l’UE.
			</p>
			<p>
				Vous pouvez traduire votre formulaire dans l’une des 136 langues couvertes
				par la norme ISO 639-1 (l’ISO 639 est une nomenclature normalisée qui est
				utilisée pour classer les langues).
			</p>
			<p>
				<b>Sécurité</b>
				<br/>
				EUSurvey est doté du dispositif nécessaire pour assurer la sécurité des
				formulaires en ligne.
			</p>
			<p>
				<b>Envoi direct d’invitations</b>
				<br/>
				Vous pouvez gérer vos contacts et leur envoyer à chacun un courrier
				électronique contenant un lien d’accès individuel à votre enquête.
			</p>
			<p>
				<b>Confidentialité avancée</b>
				<br/>
				Vous pouvez garantir à vos participants de rester anonymes en activant le "mode enquête anonyme". Si cette option est activée, toutes les contributions seront anonymes. Cela signifie qu'aucune donnée relative à l'utilisateur ne sera enregistrée par le système.
			</p>
			<p>
				<b>Personnalisation de l’apparence</b>
			</p>
			<p>
				<ul>
					<li>
						Vous pouvez configurer tous les éléments de la mise en page du formulaire
						à l’aide d’outils flexibles.
					</li>
					<li>
						Vous pouvez adapter votre formulaire à un projet spécifique au moyen
						d’une grande variété de thèmes graphiques.
					</li>
					<li>
						Vous pouvez choisir de faire tenir votre formulaire sur une page ou sur
						plusieurs pages.
					</li>
				</ul>
			</p>
			<p>
				<b>Sauvegarde des réponses à l’état de brouillon</b>
				<br/>
				Les participants peuvent enregistrer leur réponse à l’état de brouillon sur
				le serveur, et la terminer plus tard.
			</p>
			<p>
				<b>Remplissage de formulaires hors ligne </b>
				<br/>
				Les participants peuvent remplir un formulaire hors ligne avant de le
				soumettre au serveur lorsqu’il est terminé.
			</p>
			<p>
				<b>Numérotation automatique</b>
				<br/>
				Pour structurer votre enquête, EUSurvey peut numéroter les différentes
				sections.
			</p>
			<p>
				<b>Version à contraste élevé</b>
				<br/>
				Les personnes malvoyantes peuvent choisir d’afficher une version à
				contraste élevé du questionnaire. Cette version est créée automatiquement
				pour tous les formulaires.
			</p>
			<p>
				<b>Ajout de fichiers complémentaires</b>
				<br/>
				Vous pouvez ajouter des fichiers à votre questionnaire, de telle sorte que
				tous les participants puissent les télécharger.
			</p>
			<h2>
				Gestion des formulaires
			</h2>
			<p>
				<b>Travailler ensemble</b>
				<br/>
				Pour les enquêtes gérées par plusieurs utilisateurs, EUSurvey permet de
				définir des droits pour d’autres utilisateurs les autorisant à tester une
				enquête ou à analyser des résultats.
			</p>
			<h2>
				Gestion des résultats
			</h2>
			<p>
				<b>Analyse des résultats</b>
				<br/>
				Vous pouvez effectuer des analyses de résultats basiques et présenter les
				données sous forme d’histogrammes et de graphiques.
			</p>
			<p>
				Vous pouvez également créer des formats tabulaires standards avec les
				résultats de l’enquête, en vue de les utiliser dans des logiciels
				statistiques.
			</p>
			<p>
				<b>Publication des résultats</b>
				<br/>
				Vous pouvez publier un sous-ensemble de toutes les réponses soumises sur
				les pages internes de l’application. Le système peut calculer et créer
				automatiquement des statistiques et des graphiques.
			</p>
			<p>
				<b>Modification des réponses envoyées</b>
				<br/>
				Les participants peuvent modifier leurs réponses après avoir soumis
				l’enquête, si nécessaire.
			</p>
			<h2>
				Où trouver des informations complémentaires sur EUSurvey?
			</h2>
			<p>
				Si vous avez besoin<b> d’une aide pratique</b>, cliquez sur
				<a
						href="https://ec.europa.eu/eusurvey/home/documentation?language=fr"
						target="_blank"
						>
					«Aide»
				</a>
				(dans le menu déroulant <i>«Aide» </i>en haut à droite de l’écran).
			</p>
			<p>
				Pour en savoir plus sur l’historique et le financement d’EUSurvey, cliquez
				sur
				<a
						href="https://ec.europa.eu/eusurvey/home/about?language=fr"
						target="_blank"
						>
					«À propos»
				</a>
				.
			</p>
			<h2>
				Qui contacter en cas de problèmes techniques?
			</h2>
			<p>
				<b>Personnel des institutions de l’UE</b>
				: contactez votre service d’aide informatique et demandez-leur de
				transmettre votre problème à l’équipe d’assistance EUSurvey (veillez à
				décrire le problème aussi précisément que possible).
				décrire le problème aussi précisément que possible).
			</p>
			<p>
				<b>Utilisateurs externes</b>
				: contactez le
				<a
						href="mailto:EC-CENTRAL-HELPDESK@ec.europa.eu?subject=Demande%20de%20cr%C3%A9ation%20d%E2%80%99un%20ticket%20d%E2%80%99incident%20%C3%A0%20l%E2%80%99attention%20de%20DIGIT%20EUSURVEY%20SUPPORT%20&amp;body=%20Chers%20membres%20du%20service%20d%E2%80%99assistance,%20%20Pourriez-vous%20cr%C3%A9er%20un%20ticket%20%C3%A0%20l%E2%80%99attention%20de%20DIGIT%20EUSURVEY%20SUPPORT,%20accompagn%C3%A9%20de%20la%20description%20suivante?"
						target="_blank"
						>
					service d’aide central
				</a>
				de la Commission.
			</p>
			<h2>
				Qui contacter pour suggérer des améliorations à EUSurvey?
			</h2>
			<p>
				Veuillez adresser vos commentaires et vos retours à votre service d’aide
				informatique ou au service d’aide central afin qu’ils les transmettent à
				l’équipe d’assistance d’EUSurvey.
			</p>
			<p>
				L’équipe d’assistance prendra contact avec vous dans les meilleurs délais.
			</p>
			<h2>
				Avec quels navigateurs l’application EUSurvey est-elle compatible?
			</h2>
			<p>
				EUSurvey est compatible avec Microsoft Edge, Mozilla Firefox et Google
				Chrome (les deux dernières versions).
			</p>
			<p>
				L’utilisation d’autres navigateurs pourrait poser des problèmes de
				compatibilité.
			</p>
			<h2>
				Clause de non-responsabilité EUSurvey (pour les utilisateurs non membres
				des institutions de l’UE uniquement)
			</h2>
			<p>
				Pour tous les questionnaires et courriers électroniques d’invitation
				envoyés par un utilisateur qui n’est pas un <b>fonctionnaire de l’UE</b>,
				la clause de non-responsabilité suivante sera affichée:
			</p>
			<p>
				Clause de non-responsabilité
				<br/>
				<i>
					La Commission européenne ne saurait être tenue responsable du contenu
					des questionnaires créés au moyen du service EUSurvey; celui-ci relève
					de la responsabilité exclusive du créateur et du gestionnaire de ces
					questionnaires. L’utilisation du service EUSurvey n’entraîne aucunement
					l’approbation ou la recommandation, par la Commission européenne, des
					points de vue exprimés dans les questionnaires.
				</i>
			</p>
			<h2>
				Les participants peuvent-ils répondre à l’aide d’un appareil mobile?
			</h2>
			<p>
				Oui, les participants peuvent répondre à l’aide d’un téléphone portable ou
				d’une tablette.
			</p>
			<h2>
				Y a-t-il une taille minimale d’écran?
			</h2>
			<p>
				Non, les questionnaires s’adaptent à la taille de l’écran de l’appareil
				utilisé par les participants.
			</p>
			<p>
				Toutefois, pour la <i>création</i> et la <i>gestion</i> de vos enquêtes,
				nous recommandons d’utiliser une résolution minimale de 1680x1050 pixels
				pour une bonne expérience utilisateur.
			</p>
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
				Pour accéder à EUSurvey, cliquez sur le bouton «Login» sur la
				<a
						href="https://ec.europa.eu/eusurvey/home/welcome?language=fr"
						target="_blank"
						>
					page d’accueil d’EUSurvey
				</a>
				.
			</p>
			<h2>
				Comment puis-je me connecter à EUSurvey?<u> </u>
			</h2>
			<p>
				Après avoir cliqué sur «Login» sur la <u>page d’accueil d’EUSurvey</u>,
				vous serez redirigé vers l’écran de connexion à EUSurvey.
			</p>
			<p>
				Une fois arrivé sur l’écran de connexion, vous devez choisir l’option
				correspondant à votre cas personnel:
			</p>
			<ul>
				<li>
					<b>Si vous travaillez pour une institution de l’UE</b>
					, choisissez la seconde option pour vous connecter à l’aide de votre
					nom d’utilisateur et de votre mot de passe EU Login.
				</li>
				<li>
					<b>
						Si vous ne travaillez pas pour une institution de l’UE
						(utilisateurs externes)
					</b>
					, choisissez la première option pour vous connecter. Vous devrez avoir
					préalablement enregistré votre téléphone portable pour passer
					<a
							href="https://meta.wikimedia.org/wiki/Help:Two-factor_authentication/fr"
							>
						l’authentification à deux facteurs
					</a>
					.
				</li>
			</ul>
			<p>
				<a href="https://webgate.ec.europa.eu/cas/eim/external/register.cg">
					Créez un compte EU Login
				</a>
				(si ce n’est déjà fait)
			</p>
			<p>
				<a
						href="https://ecas.ec.europa.eu/cas/login?loginRequestId=ECAS_LR-37272549-D8c5uCnxYk4PagAJ3h57kPGJsddgLE9XSWDL8YHzmZTBSSFVlpDiPelqx5No9wn3NMBjIgk0VbPzW7dIrr1OF0-jpJZscgsw0K7uFRvGtGsEu-3NVKmHt3EdjogOH0oML41MpKiD9pay1L6oNS2dTn5G1CLx0S4y2y1sp2IF8JNJOzr0l8w43phzzOFa1CSm45RJ0"
						>
					Enregistrez votre téléphone portable
				</a>
				(si vous ne travaillez pas pour une institution de l’UE)
			</p>
			<h1>
				Création d’une enquête
			</h1>
			<h2>
				Comment créer une nouvelle enquête?
			</h2>
			<p>
				Sur la page «Bienvenue» ou la page «Enquêtes»:
			</p>
			<p>
				<ol>
					<li>
						cliquez sur <b>«Nouvelle enquête»</b> &#8594; puis sur    <b>«Créer une nouvelle enquête»</b>, et une fenêtre s’ouvrira.
					</li>
					<li>
						Après avoir saisi toutes les informations obligatoires, cliquez sur
						«Créer».
					</li>
					<li>
						L’outil chargera votre nouvelle enquête dans le système et ouvrira
						automatiquement l’éditeur afin que vous puissiez commencer à ajouter les
						renseignements.
					</li>
				</ol>
			</p>
			<h2>
				Quels types d’enquêtes puis-je créer?				
			</h2>
			<p>
				Vous pouvez choisir entre les options suivantes:
			</p>
			<p>
				<ul>
					<li>
						<b>Enquête normale</b><br />
						Un questionnaire classique.
					</li>
					<li>
						<b>Quiz</b>
						<br/>
						<br/>
						Dans une enquête de type «quiz», une note finale est attribuée à chaque
						participant. De telles enquêtes peuvent être utilisées, par exemple, pour
						des tests de compétence ou des examens électroniques. Pour de plus amples
						informations, veuillez consulter le
						<a
								href="https://circabc.europa.eu/sd/a/400e1268-1329-413b-b873-b42e41369a07/EUSurvey_Quiz_Guide.pdf"
								target="_blank"
								>
							manuel dédié à la création de quiz avec EUSurvey
						</a>
						.
						<br /><br />
						Le mode quiz contient entre autres:
						<ul>			
							<li>
								un mécanisme de notation;
							</li>
							<li>
								la vérification des réponses des participants;
							</li>
							<li>
								la possibilité de fournir des commentaires à vos participants, en
								fonction de leurs réponses;
							</li>
							<li>
								une analyse des résultats supplémentaire conçue spécifiquement pour les
								quiz.
							</li>
						</ul>
					</li>
					<li>
						<b>Consultation publique sur le portail «Mieux légiférer»</b><br />

						Format spécifique pour les consultations publiques menées par
						l’intermédiaire du portail «Mieux légiférer» (publiées sur la page
						<a href="https://ec.europa.eu/info/law/better-regulation/have-your-say">
							«Donnez votre avis»
						</a>
						du site web officiel de l’Union européenne).
						<br/>
						<br/>

						Le format utilisé pour le portail «Mieux légiférer» inclut:
						<ul>			
							<li>
								des <b>champs de métadonnées</b> prédéfinis permettant l’identification
								uniforme des participants d’une enquête à l’autre, ce qui simplifie
								l’élaboration de rapports;
							</li>
							<li>
								une<b> déclaration de confidentialité</b> sur mesure tenant compte des
								contraintes spécifiques des consultations publiques;
							</li>
							<li>
								<b>l’ouverture et la fermeture automatiques</b> de l’enquête depuis le
								portail «Mieux légiférer»;
							</li>
							<li>
								o la synchronisation automatique (envoi de données) des réponses des
								participants vers le portail «Mieux légiférer» en vue d’un traitement
								ultérieur.
							</li>
						</ul>
					</li>
				</ul>
			</p>

			<h2>
				Comment importer une enquête existante depuis un ordinateur?
			</h2>
			<p>
				<ol>
					<li>
						Rendez-vous sur la page «Bienvenue» ou sur la page «Enquêtes».
					</li>
					<li>
						Cliquez sur «Nouvelle enquête», puis sur «Importer une enquête», et une
						fenêtre s’ouvrira.
					</li>
					<li>
						Après avoir sélectionné un fichier d’enquête sur votre ordinateur,
						cliquez sur «Importer» et votre enquête sera ajoutée à EUSurvey.
					</li>
				</ol>
			</p>
			<p>
				Remarque: vous pouvez uniquement importer des enquêtes au format zip ou
				avec l’extension de fichier «.eus».
			</p>
			<h2>
				Où se trouvent toutes les enquêtes que j’ai créées?
			</h2>
			<p>
				Deux possibilités s’offrent à vous:
			</p>
			<p>
				<ul>
					<li>
						allez sur la page du tableau de bord, où vous trouverez une liste de
						toutes les enquêtes que vous avez créées; ou
					</li>
					<li>
						allez sur la page «Enquêtes» &#8594; sélectionnez l’option «My surveys» dans
						les critères de recherche.
					</li>
				</ul>
			</p>
			<h2>
				Comment ouvrir une enquête existante pour la modifier, par exemple?
			</h2>
			<p>
				Rendez-vous sur la page «Enquêtes».
			</p>
			<p>
				<ol>
					<li>
						Cliquez sur l’icône «Ouvrir» de l’enquête que vous souhaitez ouvrir; la
						page «Aperçu» qui s’affiche alors présente plusieurs nouveaux onglets.
					</li>
					<li>
						Cliquez sur «Éditeur» pour tester votre enquête, accéder aux résultats
						de l’enquête, à ses traductions, à ses propriétés, etc.
					</li>
				</ol>
			</p>
			<h2>
				Comment exporter une enquête existante?
			</h2>
			<p>
				Sur la page «Enquêtes», recherchez l’enquête à exporter. Vous pouvez:
			</p>
			<ul>
				<li>
					soit cliquer sur l’icône «Exporter»;
				</li>
				<li>
					soit cliquer sur l’icône «Ouvrir» &#8594; puis, sur la page «Aperçu», cliquer
					sur l’icône «Exporter».
				</li>
			</ul>
			
			<p>
				Votre enquête sera sauvegardée sur votre ordinateur, ainsi que tous ses
				paramètres.
			</p>
			<p>
				Les fichiers des enquêtes EUSurvey portent l’extension «.eus».
			</p>
			<h2>
				Comment copier une enquête existante?
			</h2>
			<p>
				Rendez-vous sur la page «Enquêtes».
			</p>
			<p>
				<ol>
					<li>
						Ouvrez l’enquête que vous souhaitez copier et cliquez sur l’icône
						«Copier».
					</li>
					<li>
						Dans la fenêtre qui s’ouvre, vous pouvez modifier les paramètres
						nécessaires.
					</li>
					<li>
						Cliquez sur «Créer».
					</li>
					<li>
						Votre enquête sera ajoutée à la liste de la page «Enquêtes» &#8594; vous
						pouvez commencer à travailler sur l’enquête.
					</li>
				</ol>
			</p>
			<h2>
				Comment supprimer une enquête existante?
			</h2>
			<p>
				Rendez-vous sur la page «Enquêtes».
			</p>
			<p>
				<ol>
					<li>
						Ouvrez l’enquête que vous souhaitez supprimer.
					</li>
					<li>
						Cliquez sur l’icône «Supprimer».
					</li>
				</ol>
			</p>
			<p>
				Confirmez ensuite cette action, et votre enquête sera supprimée de la liste
				des enquêtes.
			</p>
			<p>
				<b><u>Attention</u></b>
				: la suppression d’une enquête effacera du système EUSurvey<b><u>toute trace</u> </b>de vos questions et des résultats!    <u>Cette action est irréversible</u>!
			</p>
			<h2>
				Comment créer un questionnaire conforme aux normes WCAG avec EUSurvey?
			</h2>
			<p>
				Les lignes directrices sur l’accessibilité des contenus web (WCAG) sont un
				ensemble de recommandations visant à rendre les contenus plus accessibles,
				principalement pour les personnes handicapées, mais également pour les
				applications de téléphonie mobile.
			</p>
			<p>
				Si vous souhaitez que votre enquête soit compatible avec la norme WCAG,
				veuillez suivre les instructions présentées
				<a
						href="https://circabc.europa.eu/d/a/workspace/SpacesStore/78b03213-5cf4-4aab-8e90-ada7e2eb1101/WCAG_tutorial%20.pdf"
						target="_blank"
						>
					dans ce document
				</a>
				.
			</p>
			<h2>
				Qu'est-ce que la «popup de motivation» et comment l'utiliser ?
			</h2>
			<p>
				La «popup de motivation» est une fenêtre pop-up qui s'ouvre au participant à l'enquête pendant qu'il
				remplit le formulaire. Elle affiche un message pour motiver le participant à continuer à remplir le
				formulaire. Ce message est personnalisable et l'heure à laquelle la popup s'affiche peut également
				être configurée.
			</p>
			<p>
				La «popup de motivation» est disponible dans les propriétés de l'enquête sous l'onglet Apparence.
			</p>
			<figure>
				<img alt="Screenshot" src="${contextpath}/resources/images/documentation/motivation_popup_1_FR.png" style="width: 75%"/>
				<figcaption>Motivation popup dans les propriétés de l’enquête</figcaption>
			</figure>
			<p>
				Une fois l'interrupteur activé, les options apparaissent :
			</p>
			<p>
				Le déclencheur peut être basé sur la progression ou sur une minuterie. La progression est exprimée en
				pourcentage. Par exemple, 50%. Cela signifie que le popup s'affichera dès que le participant à
				l'enquête aura répondu à 50% des questions. Si l'option Minuterie a été sélectionnée, la fenêtre
				contextuelle s'affichera après X minutes. X étant le nombre de minutes spécifié dans le champ de
				valeur Seuil.
			</p>
			<p>
				Enfin, le texte est personnalisable à l'aide du dernier champ.
			</p>
			<figure>
				<img alt="Screenshot" src="${contextpath}/resources/images/documentation/motivation_popup_2_FR.png" style="width: 75%"/>
				<figcaption>Configuration de la «popup de motivation»</figcaption>
			</figure>
			<h1>
				Modifier une enquête
			</h1>
			<h2>
				Comment lancer l’éditeur?
			</h2>
			<p>
				Vérifiez tout d’abord que vous avez ouvert une enquête existante &#8594; allez à
				la page «Enquêtes»:
			</p>
			<p>
				<ol>
					<li>
						cliquez sur l’icône «Ouvrir» pour l’enquête que vous souhaitez modifier;
					</li>
					<li>
						à partir de la page «Aperçu», cliquez sur la page «Éditeur» pour
						l’ouvrir et commencer à effectuer des modifications.
					</li>
				</ol>
			</p>
			<p>
				Veillez à sauvegarder régulièrement votre travail.
			</p>
			<h2>
				Comment créer un questionnaire avec l’éditeur EUSurvey?
			</h2>
			<p>
				L’éditeur est composé de cinq zones différentes:
			</p>
			<p>
				<b>i) Volet de navigation:</b>
				le volet de navigation donne une vue structurée du questionnaire, où tous
				les éléments sont représentés par leur libellé respectif dans l’enquête.
			</p>
			<p>
				Lorsque vous sélectionnez un élément dans le volet de navigation, la zone
				du formulaire se positionne sur cet élément, qui est mis en évidence en
				bleu.
			</p>
			<p>
				<b>ii) Volet de la boîte à outils:</b>
				la boîte à outils contient les différents types d’éléments qui peuvent être
				ajoutés au questionnaire, soit en utilisant la méthode du glisser-déposer
				soit en double-cliquant dessus.
			</p>
			<p>
				<b>iii) Zone du formulaire:</b>
				affiche un aperçu du questionnaire; des éléments peuvent être ajoutés dans
				cette zone et sélectionnés en vue d’être modifiés.
			</p>
			<p>
				<b>iv) Volet propriétés de l’élément:</b>
				affiche toutes les options disponibles pour les éléments sélectionnés.
			</p>
			<p>
				Vous pouvez modifier les éléments, par exemple en changeant le texte de la
				question, en ajoutant des messages d’aide, ou en modifiant tous les
				paramètres pertinents pour adapter la question à vos besoins.
			</p>
			<p>
				<b>v) Barre d’outils:</b>
				contient toutes les tâches de base disponibles que vous pouvez effectuer
				lors de la création du questionnaire.
			</p>
			<p>
				Pour de plus amples informations sur la manière d’utiliser l’éditeur,
				consultez le
				<a
						href="https://ec.europa.eu/eusurvey/resources/documents/Editor_Guide_FR.pdf"
						target="_blank"
						>
					manuel de l’éditeur EUSurvey
				</a>
				.
			</p>
			<h2>
				Comment ajouter ou supprimer des questions de mon questionnaire?
			</h2>
			<p>
				Pour ajouter ou supprimer des éléments de votre questionnaire, veuillez
				d’abord:
			</p>
			<p>
				&#8594; accéder à l’éditeur.
			</p>
			<p>
				Dans l’éditeur, vous trouverez une boîte à outils avec les éléments
				disponibles à gauche et la zone du formulaire au centre de l’écran.
			</p>
			<p>
				Les éléments contiennent des textes par défaut; leur nom est affiché comme
				texte de la question.
			</p>
			<p>
				Pour ajouter des éléments (question, texte, image, etc.):
			</p>
			<p>
				&#8594; sélectionnez un élément depuis la boîte à outils, soit en utilisant la
				méthode du glisser-déposer soit en double-cliquant dessus.
			</p>
			<p>
				Pour supprimer un élément du formulaire:
			</p>
			<p>
				&#8594; cliquez sur l’élément pour le sélectionner, et cliquez sur «Supprimer»;
				dès que vous aurez confirmé, l’élément sera supprimé.
			</p>
			<p>
				Voir également
				<a
						href="#_Toc_4_2"
						>
					«Comment créer un questionnaire avec l’éditeur EUSurvey?»
				</a>
			</p>
			<h2>
				Comment modifier les éléments de mon questionnaire?
			</h2>
			<p>
				Les éléments de votre questionnaire peuvent être<b>sélectionnés dans la zone du formulaire </b>et    <b>modifiés dans le volet des propriétés de l’élément</b> de l’éditeur;
				voir
				<a
						href="#_Toc_4_2"
						>
					«Comment créer un questionnaire avec l’éditeur EUSurvey?»
				</a>
			</p>
			<p>
				Cliquez sur l’élément dans la zone du formulaire pour le sélectionner.
			</p>
			<p>
				L’élément sélectionné apparaît en bleu et les options correspondantes sont
				visibles dans le volet des propriétés de l’élément. Vous pouvez modifier
				les éléments dans ce volet, par exemple en changeant/modifiant le texte de
				la question, en ajoutant des messages d’aide, ou en modifiant tous les
				paramètres pertinents pour adapter la question à vos besoins.
			</p>
			<p>
				Pour modifier un texte:
			</p>
			<ol>
				<li>
					cliquez sur le texte ou sur l’icône en forme de crayon;
				</li>
				<li>
					modifiez le texte;
				</li>
				<li>
					cliquez sur «Appliquer» pour afficher les modifications dans la zone du
					formulaire.
				</li>
			</ol>
			<p>
				Par défaut, le volet des propriétés de l’élément affiche uniquement les
				options de base.
			</p>
			<p>
				Pour afficher plus d’options, cliquez sur «Avancé».
			</p>
			<p>
				Pour les questions de type matrice et texte, vous pouvez également
				sélectionner séparément une question, une réponse, une ligne ou une colonne
				de l’élément en cliquant sur le libellé correspondant, comme indiqué
				ci-dessous. Ainsi, vous pouvez par exemple sélectionner séparément des
				questions d’un élément présenté sous la forme d’une matrice ou d’un
				tableau, et les rendre obligatoires.
			</p>
			<h2>
				Comment copier les éléments?
			</h2>
			<p>
				Pour copier des éléments de votre questionnaire:
			</p>
			<p>
				&#8594; ouvrez l’éditeur.
			</p>
			<ol>
				<li>
					Sélectionnez le(s) élément(s) à copier.
				</li>
				<li>
					Cliquez sur «Copier».
				</li>
				<li>
					Déplacez l’espace réservé de la boîte à outils vers la zone du
					formulaire, ou sélectionnez l’élément dans la zone du formulaire et
					cliquez sur «Coller après».
				</li>
			</ol>
			<p>
				Tous les éléments qui ont été copiés ou coupés sont symbolisés par une
				icône dans la partie supérieure du volet de la boîte à outils.
			</p>
			<p>
				&#8594; ajoutez-les à nouveau au questionnaire en utilisant la méthode du
				glisser-déposer.
			</p>
			<p>
				Pour annuler l’opération:
			</p>
			<p>
				&#8594; cliquez sur le bouton situé à côté de l’élément.
			</p>
			<p>
				Voir également
				<a
						href="#_Toc_4_2"
						>
					«Comment créer un questionnaire avec l’éditeur EUSurvey?»
				</a>
			</p>
			<h2>
				Comment ajouter ou supprimer des réponses possibles dans les questions à
				choix?
			</h2>
			<p>
				<ol>
					<li>
						Cliquez sur le bouton «+» dans le volet des propriétés de l’élément pour
						ajouter des réponses; cliquez sur le bouton «-» pour en supprimer.
					</li>
					<li>
						Modifiez les réponses existantes en cliquant sur l’icône en forme de
						crayon située à côté de «Réponses possibles».
					</li>
					<li>
						Vous pouvez les modifier dans l’éditeur de texte enrichi.
					</li>
				</ol>
			</p>
			<p>
				Voir également
				<a
						href="#_Toc_4_2"
						>
					«Comment créer un questionnaire avec l’éditeur EUSurvey?»
				</a>
			</p>
			<h2>
				Puis-je rendre une question obligatoire?
			</h2>
			<p>
				<ol>
					<li>
						Dans l’éditeur, sélectionnez la question que vous souhaitez rendre
						obligatoire.
					</li>
					<li>
						Ensuite, dans le volet des propriétés de l’élément,
					</li>
					<li>
						cochez la case «Obligatoire».
					</li>
				</ol>
			</p>
			<p>
				La question obligatoire sera précédée d’un astérisque rouge.
			</p>
			<h2>
				Comment déplacer les éléments dans le questionnaire?
			</h2>
			<p>
				Dans l’éditeur, vous pouvez modifier la position d’un élément dans votre
				questionnaire de plusieurs manières:
			</p>
			<p>
				&#8594; Glisser-déposer:
				<br/>
				sélectionnez l’élément dans la zone du formulaire, puis faites-le glisser
				jusqu’à l’endroit où vous souhaitez qu’il soit dans le questionnaire.
			</p>
			<p>
				&#8594; Boutons de déplacement:
				<br/>
				sélectionnez l’élément que vous souhaitez déplacer, puis utilisez les
				boutons «Déplacer vers le haut» et «Déplacer vers le bas» dans la barre
				d’outils au-dessus de la zone du formulaire.
			</p>
			<p>
				&#8594; Couper-coller:
				<br/>
				coupez l’élément à déplacer et utilisez la méthode du glisser-déposer pour
				déplacer l’espace réservé à l’emplacement où vous souhaitez coller
				l’élément.
			</p>			
			
			<h2>Fonction de visibilité (questions dépendantes)</h2>
			<p>
				Cette fonctionnalité vous permet d'afficher ou de masquer les questions en fonction des réponses précédentes données par le participant.
			</p>
			<p>
				Par défaut, toutes les questions sont toujours visibles, de sorte que tout le monde verra la question lorsqu'il répondra à l'enquête.	</p>
			<p>
				La fonction de visibilité offre 2 options pour déclencher l'affichage des questions suivantes :
				<ul>
					<li>'OR' : l'utilisateur doit choisir au moins un des éléments sélectionnés pour que la question soit affichée ;</li>
					<li>'AND' : l'utilisateur doit choisir tous les éléments sélectionnés pour que la question soit affichée.</li>
				</ul>			
			</p>
			<p>
				Voici les étapes à suivre pour créer une question dépendante :
				<ol>
					<li>Allez à la question que vous voulez cacher/afficher - cliquez sur les propriétés.</li>
					<li>Cliquez sur l'icône du stylo à côté de la fonction de visibilité.</li>
					<li>Sélectionnez l'option à appliquer : "OR" (par défaut) ou "AND".</li>
					<li>Utilisez les cases à cocher pour sélectionner la ou les réponses qui déclencheront l'affichage de la question.</li>
					<li>Cliquez sur "Appliquer".</li>
				</ol>
			</p>
		
			<p>
			Lorsqu'il est activé, des flèches s'affichent à côté des éléments connectés pour indiquer les paramètres de visibilité dans la zone du formulaire (où le questionnaire est affiché). Les réponses qui déclenchent un élément sont marquées d'une flèche pointant vers le bas. Les éléments que vous pouvez afficher ou masquer sont marqués d'une flèche pointant vers le haut.</p>
		
			<p>
			Lorsque vous déplacez le pointeur sur les flèches (ou les ID dans le panneau Propriétés de l'élément), les éléments connectés sont mis en évidence dans la zone du formulaire et le volet de navigation.
			</p>
			<p>
				<b>Modification en masse : </b> Si vous sélectionnez plusieurs questions, vous pouvez modifier les paramètres de visibilité de toutes ces questions en même temps.
			</p>
			
			<h2>
				Peut-on modifier l’ordre des réponses aux questions à choix unique ou
				multiple?<u> </u>
			</h2>
			<p>
				Lors de la création d’une question à choix unique ou multiple, vous pouvez
				programmer l’affichage des réponses de trois façons différentes:
			</p>
			<ul>
				<li>
					ordre original;
				</li>
				<li>
					ordre alphabétique;
				</li>
				<li>
					ordre aléatoire.
				</li>
			</ul>
			<p>
				Ordre original: cette option affiche les réponses dans l’ordre dans lequel
				vous les avez saisies.
			</p>
			<p>
				Ordre alphabétique: sélectionnez cette option si vous voulez que les
				réponses s’affichent dans l’ordre alphabétique.
			</p>
			<p>
				Ordre aléatoire: sélectionnez cette option si vous voulez que les réponses
				s’affichent dans un ordre aléatoire.
			</p>
			<h2>
				Comment autoriser d’autres utilisateurs à modifier une enquête?
			</h2>
			<p>
				<ol>
					<li>
						Ouvrez votre enquête et ouvrez la page «Privilèges».
					</li>
					<li>
						Cliquez sur «Ajouter un utilisateur» ou «Ajouter un service».
					</li>
					<li>
						Un assistant s’ouvre alors pour vous guider tout au long du processus
						d’ajout d’utilisateurs.
					</li>
					<li>
						Vous pouvez leur donner des droits d’accès spécifiques; cliquez
						simplement sur la couleur pour modifier les droits.
						
						<ul>
							<li>
								Vert: accès en lecture et en écriture
							</li>
							<li>
								Jaune: accès en lecture
							</li>
							<li>
								Rouge: pas d’accès
							</li>
						</ul>
					</li>
				</ol>
			</p>
			<p>
				Les utilisateurs ajoutés verront automatiquement votre enquête apparaître
				dans leur liste d’enquêtes lors de leur prochaine connexion à EUSurvey.
			</p>
			<p>
				Les propriétaires ou organisateurs d’enquêtes externes ne peuvent pas voir
				les champs de l’UE sur le bouton «Ajouter un utilisateur» de la page
				«Privilèges». Par conséquent, ils ne peuvent pas donner un accès direct à
				ces personnes.
			</p>
			<p>
				<a href="https://ec.europa.eu/eusurvey/home/support?language=fr">
					Contactez-nous
				</a>
				si vous souhaitez demander l’accès pour des utilisateurs externes.
			</p>
			<p>
				Pour en savoir plus, voir également
				<a
						href="#_Toc_9_9"
						>
					«Comment donner accès à mon enquête à d’autres utilisateurs?»
				</a>
			</p>
			<h2>
				Quelles langues sont prises en charge par l’application?
			</h2>
			<p>
				Vous pouvez créer une enquête dans toute langue encodable au format «UTF-8
				à trois octets».
			</p>
			<h2>
				Pourquoi l’UTF-8 et quelles polices de caractères utiliser?
			</h2>
			<p>
				Les participants ciblés peuvent afficher l’enquête sans encombre si la
				police choisie est installée dans leur navigateur internet. L’UTF-8 est la
				norme d’encodage la plus courante pour les pages HTML. En revanche, si vous
				choisissez une police non compatible, vous risquez de rencontrer des
				problèmes pour l’exportation au format PDF.
			</p>
			<p>
				Nous recommandons d’utiliser les <b>jeux de caractères compatibles</b>
				suivants:
			</p>
			<ul>
				<li>
					Freesans
					<a
							href="https://circabc.europa.eu/d/a/workspace/SpacesStore/602784e1-bb06-4b0d-a474-eae77dbe2d11/EUSurvey-SupportedCharacterSet(freesans).txt"
							target="_blank"
							>
						(
						https://circabc.europa.eu/sd/a/36f72861-fc6e-4fe1-87d6-0a8e1c6fa161/EUSurvey-SupportedCharacterSet(freesans).txt)
					</a>
				</li>
				<li>
					Freemono
					<a
							href="https://circabc.europa.eu/d/a/workspace/SpacesStore/dfc640e9-56ac-4d25-8361-4b07dbbd0579/EUSurvey-SupportedCharacterSet(freemono).txt"
							target="_blank"
							>
						(
						https://circabc.europa.eu/sd/a/55ce0f35-b3cc-4712-80bf-af42800a278f/EUSurvey-SupportedCharacterSet(freemono).txt)
					</a>
				</li>
				<li>
					Freeserif
					<a
							href="https://circabc.europa.eu/d/a/workspace/SpacesStore/5b98b11a-f306-4d97-aab3-ec1c7a24965f/EUSurvey-SupportedCharacterSet(freeserif).txt"
							target="_blank"
							>
						(
						https://circabc.europa.eu/sd/a/29cd78bb-9eeb-40b1-a22f-b54700750537/EUSurvey-SupportedCharacterSet(freeserif).txt)
					</a>
				</li>
				<li>
					Jeu de caractères couramment pris en charge
					<a
							href="https://circabc.europa.eu/d/a/workspace/SpacesStore/621396c0-92d3-49a3-acd0-546b0c1a170b/EUSurvey-SupportedCharacterSet(common).txt"
							target="_blank"
							>
						(
						https://circabc.europa.eu/sd/a/1eb30efd-e2d8-4c3b-9f55-533bb903f7d0/EUSurvey-SupportedCharacterSet(common).txt)
					</a>
				</li>
			</ul>
			<p>
				<b>«Freesans» est la police utilisée par défaut.</b>
			</p>
			<p>
				En cas de doute, exportez votre enquête finale en PDF pour vérifier si elle
				s’affiche correctement dans ce format.
			</p>
			<p>
				Sachez toutefois qu’il est possible que certaines réponses ne s’affichent
				pas correctement en PDF. Vos participants peuvent choisir n’importe quelle
				police de caractères compatible avec l’application.
			</p>
			<p>
				Même si l’outil ne parvient pas à afficher correctement les caractères
				utilisés, ceux-ci seront bien enregistrés dans la base de données EUSurvey.
				Ces contributions peuvent donc être exportées depuis la page des résultats.
			</p>
			<h2>
				Indicateur de complexité
			</h2>
			<p>
				Si votre enquête est courte et simple, les personnes interrogées auront plus de facilité à la remplir et l'expérience utilisateur sera meilleure. Bien sûr, il est parfois nécessaire d'ajouter une logique de branche en utilisant des dépendances (c'est-à-dire des questions dépendantes qui sont cachées/affichées en fonction des réponses précédentes). C'est faisable, mais n'oubliez pas que l'ajout de trop d'éléments ou de dépendances à votre enquête la rend trop ‘complexe’. Cela peut entraîner un ralentissement du système pour les participants qui remplissent votre questionnaire.
			</p>
			<p>
				C'est pourquoi, il y a un petit indicateur dans le coin supérieur droit de l'éditeur du questionnaire:<br />
				<img alt="Screenshot" src="${contextpath}/resources/images/documentation/complexityFR.png" />
			</p>
			<p>
				Votre enquête peut présenter un niveau de complexité élevé pour plusieurs raisons:
				<ul>
					<li>trop de dépendances</li>
					<li>trop de dépendances en cascade</li>
					<li>trop d'éléments de tableau/matrice</li>
				</ul>
			</p>
			<p>
				Pour plus d'informations, consultez notre <a href="https://circabc.europa.eu/sd/d/281e626e-279e-45df-8581-1f6e04feff51/BestPractices-EUSurvey.pdf">guide des meilleures pratiques</a> et n'hésitez pas à contacter <a href="${contextpath}/home/support?assistance=1">l'équipe de support EUSurvey</a> pour obtenir de l'aide afin de revoir la conception de votre formulaire si nécessaire.
			</p>
			<h2>Quel est «Numéro d'identification de la contribution»?</h2>
			<p>
				«Numéro d'identification de la contribution» est un code utilisé comme identifiant unique pour une contribution effectuée.
			</p>
			<p>
				Il peut être utilisé par les responsables d'enquêtes pour retrouver une contribution à partir de l'écran «Résultats». Il peut également être utilisé par un participant au enquête pour soumettre sa contribution et y accéder ultérieurement.
			</p>
			<h2>Comment puis-je trouver une contribution à partir des résultats à l'aide d'un «Numéro d'identification de la contribution»?</h2>
			<p>
				«Numéro d'identification de la contribution» peut être utilisé par les propriétaires d’enquête pour trouver une contribution parmi tous les résultats :
				<ol>
					<li>
						Ouvrez votre enquête
					</li>
					<li>
						Allez dans l'onglet «Résultats»
					</li>
					<li>
						Cliquez sur le bouton «Paramètres»
					</li>
					<li>
						Cochez «Numéro d'identification de la contribution» dans les deux colonnes et appuyez sur «OK»
					</li>
					<li>
						Aller au filtre  «Numéro d'identification de la contribution»
					</li>
					<li>
						Insérez l'ID de contribution et cliquez sur Entrer
					</li>
				</ol>
			</p>
			<h2>Fonction de randomisation</h2>
			<p>
				Lorsque vous utilisez une <strong>Section</strong> de premier niveau, vous avez la possibilité de conserver les questions/éléments situés en dessous dans leur ordre d'origine ou de rendre leur position aléatoire.
				La randomisation peut être sélectionnée dans les propriétés de la section, juste à côté de l'ordre.
			</p>
			<img alt="Screenshot" src="${contextpath}/resources/images/documentation/randomization_FR.png" style="margin-bottom: 1em" />
			<p>Veuillez également noter les points suivants:</p>
			<ul>
				<li>Tous les éléments de l'enquête (y compris les images et le texte statique) sont positionnés aléatoirement.</li>
				<li>Si une question déclenche un changement de visibilité ou est déclenchée par un changement de visibilité, sa position n'est pas modifiée. Elle est toujours affichées avant les autres éléments aléatoires.</li>
				<li>Les sous-sections et leurs questions (niveaux 2 et 3) sont également randomisées aléatoirement, mais l’ordre de la sous-section au sein de la section de niveau 1 à laquelle elle appartient est conservé. Cela signifie que les questions d'une sous-section sont également randomisées lorsque la section de niveau 1 est randomisée.</li>
				<li>La version PDF de l'enquête (fonction «Télécharger la version PDF») montre toujours les questions dans l'ordre d’origine.</li>
				<li>Les contributions PDF montrent toujours les questions dans l'ordre d’origine.</li>
				<li>Si la numérotation des sections/questions est activée en même temps que la fonction de randomisation, les numéros des questions seront également randomisés avec les questions.
					<div><img alt="Screenshot" src="${contextpath}/resources/images/documentation/randomization_sections_FR.png" /></div>
				</li>
			</ul>
			<p>Il y a un autre point spécifique aux enquêtes DELPHI:</p>
			<ul>
				<li>La page d'accueil de DELPHI présente les questions dans leur ordre original.</li>
			</ul>
			<h2>Formule</h2>
			<p>
				Ce type de question appelé « Formule » calcule et affiche une valeur basée sur les données saisies par le participant.
				Il vous permet d'afficher un total ou une moyenne par exemple et ajoute un aspect interactif à votre enquête.
			</p>
			<img alt="Screenshot" src="${contextpath}/resources/images/documentation/formular_field_1_FR.png" style="margin-bottom: 1em;" />
			<p>Principaux points de l'élément de question :</p>
			<ul>
				<li>
					Des fonctions prêtes à l'emploi sont disponibles pour différents cas d'utilisation :<br>
					<img alt="Screenshot" src="${contextpath}/resources/images/documentation/formular_field_2_FR.png" style="margin-bottom: 1em" />
				</li>
				<li>
					Les utilisateurs peuvent également saisir leur formule dans le champ prévu à cet effet :<br>
					<img alt="Screenshot" src="${contextpath}/resources/images/documentation/formular_field_3_FR.png" style="margin-bottom: 1em" />
				</li>
			</ul>
			<p>Les ID des éléments sont utilisés pour composer la formule.
				La liste des ID est affichée pour vous permettre de sélectionner directement les ID dont vous avez besoin.
				Vous pouvez également les saisir directement dans le champ de saisie.</p>
			<img alt="Screenshot" src="${contextpath}/resources/images/documentation/formular_field_4_FR.png" style="margin-bottom: 1em" />
			<p>Veuillez noter que les ID des éléments sont visibles dans la section Avancé des propriétés de l'élément.</p>
			<img alt="Screenshot" src="${contextpath}/resources/images/documentation/formular_field_5_FR.png" style="margin-bottom: 1em" />
			<p><span style="text-decoration: underline;">Exemple 1 :</span><br>
				Dans l'exemple ci-dessous, le champ « Formule » correspond à la somme des deux questions « Nombre/Curseur ».
				Par conséquent, dès que le participant à l'enquête a saisi la deuxième valeur (5 dans notre exemple),
				la somme est calculée et affichée en temps réel.
			</p>
			<img alt="Screenshot" src="${contextpath}/resources/images/documentation/formular_field_6_FR.png" style="margin-bottom: 1em" />
			<p><span style="text-decoration: underline;">Exemple 2 :</span><br>
				Dans l'exemple ci-dessous, vous pouvez demander à votre participant à l'enquête d'indiquer son loyer mensuel ou annuel.
				L'application calculera automatiquement l'autre. Les deux champs restent modifiables par l'utilisateur.
			</p>
			<img alt="Screenshot" src="${contextpath}/resources/images/documentation/formular_field_7_FR.png" style="margin-bottom: 1em" />
			<p>
				<span style="font-style: italic;">Le participant à l'enquête a saisi 500 et 6000 a été calculé.</span>
			</p>
			<p><span style="text-decoration: underline;">Exemple 3 option « En lecture seule » :</span></p>
			<img alt="Screenshot" src="${contextpath}/resources/images/documentation/formular_field_8_FR.png" style="margin-bottom: 1em" />
			<p>Dans l'exemple ci-dessous, la ligne 5 affiche le total de chaque colonne.
				Ces champs utilisent l'option « En lecture seule », de sorte que le total ne peut être modifié par l'utilisateur.</p>
			<img alt="Screenshot" src="${contextpath}/resources/images/documentation/formular_field_9_FR.png" style="margin-bottom: 1em" />
			<h2>Tableau Complexe</h2>
			<p>Le tableau complexe est un élément d'enquête semblable à un tableau qui vous permet de composer d'autres éléments d'enquête de manière plus complexe.
				Il permet la liaison visuelle des différentes questions et la mise en page de texte (par exemple, l'affichage du texte en colonnes).</p>
			<img alt="screenshot complex_table element" src="${contextpath}/resources/images/documentation/complex_table_1_FR.png" style="margin-bottom: 1em" />
			<p><b>Comment puis-je configurer un « Tableau Complexe » ?</b></p>
			<p>Dans « l'éditeur » d'enquête, ajoutez un élément « Tableau complexe » et sélectionnez-le.
				Les « Propriétés de l'élément » fournissent les mêmes options de configuration que l'élément classique « Tableau ».</p>
			<img alt="screenshot complex_table element" src="${contextpath}/resources/images/documentation/complex_table_2_FR.png" style="margin-bottom: 1em" />
			<p><b>Comment puis-je configurer les cellules d’un « Tableau Complexe » ?</b></p>
			<p>Sélectionnez une seule cellule dans l’éditeur de l'enquête.</p>
			<p>Vous pouvez spécifier différents types de questions dans différentes cellules. Ces types correspondent aux types des questions standards.</p>
			<img alt="screenshot complex_table element" src="${contextpath}/resources/images/documentation/complex_table_3_FR.png" style="margin-bottom: 1em" />
			<p>Le « Texte Statique » peut être affiché dans des colonnes à l'aide de la propriété « Column Span ».
				Notez qu'il ne peut s'étendre que sur les cellules suivantes, supprimant ainsi le contenu de ces cellules si elles étaient déjà configurées.</p>
			<p>Les autres types de cellules disponibles (« Texte libre », « Formule », « Choix unique », « Choix multiples », « Nombre »)
				sont fondamentalement les mêmes que leurs homologues en dehors d'un tableau complexe et peuvent être modifiés de la même manière.</p>
			<p><b>Comment afficher du texte en colonnes ?</b></p>
			<p>Les passages de texte peuvent être affichés en colonnes en divisant le texte sur plusieurs cellules d’un tableau complexe.
				Configurez le type des cellules en « Texte Statique ».
				Sous « Propriétés des éléments > Texte », le texte souhaité peut maintenant être saisi individuellement pour chaque cellule.</p>
			<p><b>Comment les cellules peuvent-elles être configurées pour qu'elles ne soient pas modifiables ?</b></p>
			<p>Les types de cellule du tableau complexe qui ne peuvent pas être modifiés sont « Texte Statique » et « Vide ».</p>
			<p>Pour les autres types de cellules, les « Propriétés de l'élément > Lecture seule » peuvent être utilisées pour empêcher la saisie directe de l'utilisateur.</p>
			<p><b>Comment une cellule peut être configurée de sorte que le texte s’étende sur plusieurs colonnes ?</b></p>
			<p>Le texte des tableaux complexes peut être configuré pour s'étendre sur plusieurs colonnes.
				Sélectionnez une cellule avec « Type de cellule > Texte statique ».
				Sous « Propriétés des éléments », l'option « Étendue des colonnes » est responsable du nombre de colonnes couvertes.
				Notez que cette fonction couvre les cellules suivantes, supprimant ainsi le contenu de ces cellules si elles étaient déjà configurées.</p>
			<p><b>Comment puis-je supprimer une question dans une cellule de tableau complexe ?</b></p>
			<p>Le contenu des cellules d'un tableau complexe ne peut pas être supprimé individuellement à l'aide de la fonction de suppression de l'éditeur.
				Au lieu de cela, le « Type de cellule » de la cellule doit être réinitialisé sur « Vide ».</p>
			<p><b>Comment puis-je obtenir un graphique dans l'écran statistiques ?</b></p>
			<p>Les valeurs Min et Max doivent être définies ; 10 valeurs au maximum sont possibles.</p>
			<img alt="screenshot complex_table element" src="${contextpath}/resources/images/documentation/complex_table_4_FR.png" style="margin-bottom: 1em; max-width: 920px" />
			<h1>
				Sécurité des enquêtes
			</h1>
			<h2>
				Comment restreindre l’accès à une enquête?
			</h2>
			<p>
				Par défaut, les enquêtes sont accessibles publiquement dès leur
				publication.
			</p>
			<p>
				Si vous souhaitez n’autoriser l’accès à l’enquête qu’à des utilisateurs
				privilégiés:
			</p>
			<p>
				&#8594; activez l’option <b>«Secure your survey»</b> dans la section «Sécurité»
				de la page «Propriétés».
			</p>
			<p>
				Vous pouvez par la suite donner l’accès aux utilisateurs privilégiés de
				différentes façons:
			</p>
			<ul>
				<li>
					vous pouvez inviter les participants à l’aide du module d’invitation
					d’EUSurvey (voir
					<a
							href="#_Toc_13_0"
							>
						«Inviter des participants»
					</a>
					). Chaque participant recevra un lien d’accès unique; ou
				</li>
				<li>
					vous pouvez sécuriser votre enquête avec EU Login. Dans la page
					«Propriétés», activez les options «Secure your survey» et «Secure with
					EU Login». Si vous êtes un membre du personnel des institutions de
					l’UE, vous pouvez:
					
					<ul>
						<li>
							soit autoriser l’accès à votre questionnaire à tous les utilisateurs
							disposant d’un compte EU Login (personnel des institutions de l’UE et
							utilisateurs externes);
						</li>
						<li>
							soit donner accès aux membres du personnel des institutions de l’UE
							uniquement; ou
						</li>
					</ul>
					
				</li>
				<li>
					vous pouvez définir un mot de passe qui sera identique pour tous les
					participants auxquels vous enverrez le lien vers l’enquête et le mot de
					passe général (voir
					<a
							href="#_Toc_5_2"
							>
						«Comment définir un mot de passe pour une enquête?»
					</a>
					).
				</li>
			</ul>
			<h2>
				Comment définir un mot de passe pour une enquête?
			</h2>
			<p>
				Utilisez l’option «Secure with password» dans la section «Propriétés».
			</p>
			<p>
				Pour inviter des personnes à accéder à votre enquête sécurisée, voir
				<a href="#_Toc_13_0">
					«Inviter des participants»
				</a>
				.
			</p>
			<h2>
				Comment s’assurer qu’un utilisateur ne soumette pas un nombre trop élevé de
				contributions?
			</h2>
			<p>
				Dans la page «Propriétés», activez les options «Secure your survey» et
				«Secure with EU Login».
			</p>
			<p>
				Activez l’option «Limit number of contributions» et indiquez le nombre
				maximal de contribution(s) par utilisateur.
			</p>
			
			<h2>Comment limiter le nombre de contributions d’une enquête?</h2>
			<p>Vous pouvez modifier le nombre de contributions que peut accepter une enquête en allant dans Propriétés >> Avancé. Par défaut, il n’y a pas de limite au nombre de contributions mais si une limite est définie alors l’enquête ne sera plus accessible dès lors qu’elle est atteinte. Ceci peut être utilisé pour les formulaires d’inscription à des évènements par exemple.</p>
			
			<h2>
				Comment éviter que des bots ne soumettent des contributions en masse à une
				enquête?
			</h2>
			<p>
				Des scripts automatisés pourraient fausser les résultats d’une enquête en
				ligne en soumettant un grand nombre de contributions. Pour éviter cela,
				EUSurvey dispose d’une fonction demandant aux participants de saisir les
				caractères d’une
				<a href="https://fr.wikipedia.org/wiki/CAPTCHA" target="_blank">
					image de vérification (CAPTCHA)
				</a>
				avant de soumettre leur contribution.
			</p>
			<p>
				Vous pouvez activer/désactiver l’option «Image de vérification (CAPTCHA)»
				dans la section «Sécurité» de la page «Propriétés».
			</p>
			<p>
				Remarque: bien que cette option ne permette pas d’empêcher toute fraude,
				elle pourrait décourager les personnes qui tentent sans cesse de falsifier
				les résultats de l’enquête.
			</p>
			<h2>
				Puis-je permettre aux personnes interrogées d'accéder à leur contribution après sa soumission?
			</h2>
			<p>
				Oui. Cliquez sur «Sécurité» sous l'onglet «Propriétés», puis activez l'option <b>«Permettre aux participants de modifier leur contribution»</b>.
			</p>
			<p>
				Pour éditer/modifier leur contribution après soumission, les participants peuvent se rendre sur cette page: <a href="https://ec.europa.eu/eusurvey/home/editcontribution" target="_blank">https://ec.europa.eu/eusurvey/home/editcontribution</a>
			</p>
			<p>
				Les participants à votre enquête devront connaître leur identifiant de contribution. Cet identifiant leur est fourni lors de la soumission de leur contribution sur la page de confirmation.
			</p>
			<p>
				Veuillez noter que le lien «Modifier une contribution» est accessible sur la page d'accueil d'EUSurvey: <a href="https://ec.europa.eu/eusurvey/home/welcome" target="_blank">https://ec.europa.eu/eusurvey/home/welcome</a>
			</p>
			<h2>
				Comment puis-je permettre aux participants d'imprimer ou de télécharger leur contribution?
			</h2>
			<p>
				Si elle est activée, cette fonctionnalité permet aux participants d'enregistrer leurs réponses au format PDF. Pour activer cette fonctionnalité, suivez les étapes ci-dessous :
				<ol>
					<li>
						Ouvrez votre enquête
					</li>
					<li>
						Allez dans l'onglet «Propriétés»
					</li>
					<li>
						Allez dans l'onglet «Sécurité»
					</li>
					<li>
						Activez l'option «Permet aux participants d'imprimer leur contribution et de la recevoir au format PDF»
					</li>
				</ol>
				<img alt="Screenshot" src="${contextpath}/resources/images/documentation/printdownloadFR.png" />
			</p>
			<h2>
				Comment puis-je permettre aux participants de modifier (éditer) leur contribution?
			</h2>
			<p>
				Si elle est activée, cette fonctionnalité permet aux participants de modifier/éditer leurs réponses après avoir été soumises.
			</p>
			<p>
				Pour activer cette fonctionnalité :
				<ol>
					<li>
						Ouvrez votre sondage
					</li>
					<li>
						Allez dans l'onglet «Propriétés»
					</li>
					<li>
						Allez dans l'onglet «Sécurité»
					</li>
					<li>
						Activez l'option «Permettre aux participants de modifier leur contribution»
					</li>
				</ol>
				<img alt="Screenshot" src="${contextpath}/resources/images/documentation/changecontributionFR.png" />
			</p>
			<h1>
				Tester une enquête
			</h1>
			<h2>
				Est-il possible de tester mon enquête et de l’afficher telle qu’elle
				apparaîtra une fois publiée?
			</h2>
			<p>
				Oui. Ouvrez l’enquête dans EUSurvey et cliquez sur «Test».
			</p>
			<p>
				Vous verrez le brouillon de votre enquête et pourrez tester tous les
				éléments du formulaire tel que publié.
			</p>
			<p>
				Vous pouvez aussi enregistrer vos réponses au test à l’état de brouillon,
				ou les soumettre directement.
			</p>
			<h2>
				Comment tester mon enquête auprès de mes collègues avant sa publication?
			</h2>
			<p>
				Pour donner à vos collègues accès à la page de test de votre enquête:
			</p>
			<p>
				<ol>
					<li>
						ouvrez votre enquête dans EUSurvey;
					</li>
					<li>
						dans la page «Privilèges», cliquez sur «Ajouter un utilisateur» ou
						«Ajouter un service».
					</li>
					<li>
						L’assistant qui s’ouvre alors permet d’ajouter vos collègues.
					</li>
				</ol>
			</p>
			<p>
				Pour leur donner les droits d’accès de test:
			</p>
			<p>
				&#8594; sélectionnez la couleur verte pour la fonction «Accès aperçu formulaire»
				(il suffit de cliquer sur la couleur pour modifier les droits).
			</p>
			<p>
				Les utilisateurs ajoutés verront automatiquement votre enquête apparaître
				dans leur page «Enquêtes» lors de leur prochaine connexion à EUSurvey.
			</p>
			<p>
				Pour en savoir plus, voir également
				<a
						href="#_Toc_9_9"
						>
					«Comment donner accès à mon enquête à d’autres utilisateurs?»
				</a>
			</p>
			<p>
				Les propriétaires ou organisateurs d’enquêtes externes ne peuvent pas voir
				les champs de l’UE sur le bouton «Ajouter un utilisateur» de la page
				«Privilèges». Par conséquent, ils ne peuvent pas donner un accès direct à
				ces personnes.
			</p>
			<p>
				<a href="https://ec.europa.eu/eusurvey/home/support?language=fr">
					Contactez-nous
				</a>
				si vous souhaitez demander l’accès pour des utilisateurs externes.
			</p>
			<h1>
				Traductions
			</h1>
			<h2>
				Comment traduire une enquête?
			</h2>
			<p>
				Remarque importante: assurez-vous d’avoir terminé de modifier et de tester
				votre enquête avant d’entamer sa traduction!
			</p>
			<p>
				<ol>
					<li>
						Ouvrez votre enquête et allez à la page «Traductions».
					</li>
					<li>
						Cliquez sur «Ajouter nouvelle traduction».
					</li>
					<li>
						Sélectionnez la langue dans la liste des langues prises en charge.
					</li>
					<li>
						Si la langue souhaitée n’apparaît pas dans la liste, sélectionnez
						«autre» et indiquez les deux lettres identifiant cette langue conformément
						à la norme ISO 639-1.
					</li>
					<li>
						Cliquez sur «OK» pour ajouter un formulaire de traduction vierge à votre
						enquête.
					</li>
				</ol>
			</p>
			<p>
				Pour de plus amples informations sur la manière d’ajouter de nouveaux
				éléments textuels à votre traduction nouvellement créée, veuillez consulter
				<a
						href="#_Toc_7_3"
						>
					«Est-il possible de modifier une traduction existante en ligne?»
				</a>
			</p>
			<p>
				Cochez la case «Publier» si la traduction doit être publiée en même temps
				que votre enquête.
			</p>
			<p>
				Si vous avez ajouté une traduction pour la publication, les participants
				peuvent choisir une langue parmi les langues disponibles directement à
				partir du lien vers l’enquête.
			</p>
			<h2>
				Comment ajouter une traduction existante à une enquête?
			</h2>
			<p>
				<ol>
					<li>
						Ouvrez votre enquête et ouvrez la page «Traductions».
					</li>
					<li>
						Cliquez sur «Charger une traduction existante».
					</li>
					<li>
						L’assistant qui s’ouvre alors permet de charger le fichier de
						traduction.
					</li>
				</ol>
			</p>
			<h2>
				Est-il possible de modifier une traduction existante en ligne?
			</h2>
			<p>
				Oui!
			</p>
			<p>
				<ol>
					<li>
						Ouvrez votre enquête et allez à la page «Traductions».
					</li>
					<li>
						Sélectionnez une ou plusieurs traductions à modifier.
					</li>
					<li>
						Sélectionnez «Modifier les traductions» dans les icôns d’action.
					</li>
					<li>
						Cliquez sur «OK» &#8594; l’éditeur de traductions en ligne s’ouvre alors, et
						vous pouvez l’utiliser pour modifier plusieurs traductions à la fois.
					</li>
					<li>
						Cliquez sur «Enregistrer» pour veiller à ce que votre travail soit
						sauvegardé dans le système.
					</li>
				</ol>
			</p>
			<p>
				Pour modifier une seule traduction:
			</p>
			<p>
				<ol>
					<li>
						ouvrez votre enquête;
					</li>
					<li>
						allez à la page «Traductions»;
					</li>
					<li>
						cliquez sur l’icône en forme de crayon dans la colonne «Actions».
					</li>
				</ol>
			</p>
			<h2>
				Est-il possible de créer des traductions hors ligne?
			</h2>
			<p>
				Oui! Les étapes à suivre sont les suivantes:
			</p>
			<p>
				<ol>
					<li>
						allez à la page «Traductions»;
					</li>
					<li>
						exportez une version linguistique ayant le statut «Complète» en tant que
						fichier XLS;
					</li>
					<li>
						modifiez le code de la langue (ISO 639-1) en haut du fichier (cellule
						B1);
					</li>
					<li>
						traduisez tous les éléments textuels disponibles dans la nouvelle langue
						(colonne C);
					</li>
					<li>
						une fois que l’enquête a été traduite hors ligne, sauvegardez-la;
					</li>
					<li>
						cliquez sur «Charger une traduction existante» pour importer la
						traduction.
					</li>
				</ol>
			</p>
			<p>
				C’est terminé. Vous pouvez vérifier la traduction depuis la page «Test».
			</p>
			<h2>
				Comment publier/dépublier mes traductions?
			</h2>
			<p>
				Pour publier une enquête dans plusieurs langues:
			</p>
			<p>
				<ol>
					<li>
						ouvrez votre enquête;
					</li>
					<li>
						ouvrez la page «Traductions»;
					</li>
					<li>
						cochez ou décochez les traductions que vous souhaitez publier (ou
						dépublier) dans la section «Publier»;
					</li>
					<li>
						rendez-vous ensuite à la page «Aperçu» de l’enquête, où vous pourrez la
						publier.
					</li>
				</ol>
			</p>
			<p>
				Si l’enquête avait été publiée avant l’ajout ou la suppression des
				traductions, cliquez sur «Appliquer les modifications».
			</p>
			<p>
				Pour éviter la publication de traductions dont tout le texte n’aurait pas
				été traduit, il n’est pas possible de publier des traductions comportant
				des éléments vides (traductions qui ne sont pas «complètes»).
			</p>
			<p>
				Vérifiez que votre traduction ne comporte aucun élément vide au moyen de
				l’éditeur de traduction en ligne. Les cellules vides se distinguent par
				leur fond rouge.
			</p>
			<h2>
				Est-il possible de charger des traductions dans des langues non
				européennes?
			</h2>
			<p>
				L’application est également compatible avec d’autres langues que les
				langues officielles de l’UE.
			</p>
			<p>
				Sélectionnez «Autre» au moment de charger la traduction et indiquez les
				deux lettres correspondant à la langue souhaitée conformément à la norme
				<a href="https://fr.wikipedia.org/wiki/Liste_des_codes_ISO_639-1">
					ISO 639-1
				</a>
				.
			</p>
			<h2>
				Qu’entend-on par «Demander une traduction automatique»?
</h2>
			<p>
				Un <b>moteur de traduction automatique</b> peut être utilisé pour traduire
				automatiquement votre questionnaire sur EUSurvey. L’application utilise le
				système eTranslation de la Commission européenne.
			</p>
			<p>
				À partir de la page «Traductions», il existe plusieurs façons de demander
				des traductions automatiques:
			</p>
			<ul>
				<li>
					au moment d’ajouter une nouvelle traduction, cochez la case «Demander
					une traduction automatique» (pour une traduction depuis la langue principale
					de votre enquête);
				</li>
				<li>
					cliquez sur l’icône «Demander une traduction» dans la colonne «Actions»
					(pour une traduction depuis la langue principale de votre enquête);
				</li>
				<li>
					sélectionnez toutes les langues vers lesquelles vous souhaitez faire
					traduire votre enquête (sélectionnez aussi au moins une traduction
					complète); sélectionnez ensuite «Demander une traduction» et cliquez
					sur «OK».
				</li>
			</ul>
			<p>
				Le statut de la traduction passera à «Demandée» jusqu’à ce qu’elle soit
				terminée.
			</p>
			<p>
				Pour savoir si ce statut a changé, consultez la page «Traductions».
			</p>
			<p>
				Les traductions automatiques se comporteront comme les autres traductions
				que vous avez ajoutées manuellement, c’est-à-dire qu’elles ne seront pas
				publiées automatiquement, et le fait d’ajouter de nouveaux éléments à votre
				enquête les rendra incomplètes (pour les compléter, vous devrez demander
				une nouvelle traduction).
			</p>
			<p>
				<i>
					Nous ne pouvons garantir ni la qualité du texte produit ni le délai de
					livraison des traductions.
				</i>
			</p>
			<p>
				<a
						href="https://webgate.ec.europa.eu/etranslation/help.html"
						target="_blank"
						>
					Aide pour la traduction automatique
				</a>
				(uniquement pour le personnel des institutions de l’UE).
			</p>
			<h2>
				Instructions pour le personnel des institutions de l’UE
		</h2>
			<p>
				Nous vous recommandons de contacter la DGT avant de finaliser votre
				enquête. Ses réviseurs vérifieront que votre enquête est clairement rédigée
				et présentée. Pour en savoir plus, consultez
				<a
						href="https://myintracomm.ec.europa.eu/serv/fr/dgt/Pages/index.aspx"
						target="_blank"
						>
					le site de la DGT sur MyIntraComm
				</a>
				.
			</p>
			<p>
				La DGT peut aussi se charger de traduire votre enquête dans les langues
				officielles de l’UE.
			</p>
			<p>
				Exportez-la en tant que fichier XML et envoyez-la au moyen de l’application
				Poetry, en sélectionnant le code de votre DG. Le texte de l’enquête ne doit
				pas dépasser 15 000 caractères, espaces non compris (selon la fonction de
				comptage de MS Word).
			</p>
			<h1>
				Publication d’une enquête
			</h1>
			<h2>
				Comment publier une enquête?
			</h2>
			<p>
				Pour publier une enquête à partir d’un brouillon en cours:
			</p>
			<p>
				&#8594; rendez-vous sur la page «Aperçu» et cliquez sur «Lancer».
			</p>
			<p>
				Après confirmation, le système crée automatiquement une copie fonctionnelle
				de votre enquête et la met en ligne, ainsi que toutes les traductions
				sélectionnées pour publication (voir
				<a
						href="#_Toc_7_5"
						>
					«Comment publier/dépublier mes traductions?»
				</a>
				).
			</p>
			<p>
				Le lien vers votre enquête publiée se trouve à la rubrique «Lien de
				l’enquête publiée» de la page «Aperçu».
			</p>
			<p>
				Pour dépublier votre enquête &#8594; cliquez sur le bouton «Arrêter».
			</p>
			<p>
				Vous pourrez toujours accéder à l’enquête dépubliée, ainsi qu’à votre
				brouillon en cours.
			</p>
			<p>
				Cela signifie que l’enquête dépubliée ne sera pas automatiquement remplacée
				par votre brouillon en cours, mais que vous pourrez la republier telle
				quelle si nécessaire.
			</p>
			<h2>
				Est-il possible de personnaliser l’URL d’une enquête?
			</h2>
			<p>
				Oui!
			</p>
			<p>
				En modifiant l’«alias» de votre enquête, vous pourrez disposer d’une
				adresse URL plus compréhensible.
			</p>
			<p>
				<ol>
					<li>
						Ouvrez votre enquête et allez à la page «Propriétés».
					</li>
					<li>
						Modifiez l’alias de votre enquête dans la section «de base».
					</li>
				</ol>
			</p>
			<p>
				Les alias ne peuvent contenir que des caractères alphanumériques et des
				traits d’union.
			</p>
			<p>
				Si vous modifiez l’alias d’une enquête publiée &#8594; allez à la page «Aperçu»
				et cliquez sur «Appliquer les modifications».
			</p>
			<p>
				Un même alias ne peut être utilisé qu’une seule fois dans tout le système
				EUSurvey. Vous serez averti si votre alias est déjà utilisé par une autre
				enquête.
			</p>
			<h2>
				Puis-je envoyer un lien direct vers une traduction de mon enquête?
			</h2>
			<p>
				Lorsque vous envoyez des invitations, ou utilisez le lien vers le
				formulaire publié figurant sur la page «Aperçu», le lien renvoie par défaut
				vers le formulaire dans la langue principale.
			</p>
			<p>
				Cependant, il est également possible de    <b>rediriger les participants directement</b> vers la traduction souhaitée,
				à l’aide du lien suivant:
			</p>
			<p>
				<b>https://ec.europa.eu/eusurvey/runner/</b>
				<b>SurveyAlias</b>
				<b>?surveylanguage=</b>
				<b>LC</b>
			</p>
			<p>
				Il vous suffit de remplacer:
			</p>
			<ul>
				<li>
					«<b>SurveyAlias</b>» par <b>l’alias de votre enquête</b>; et
				</li>
				<li>
					«<b>LC</b>» par le        <b>code de la langue souhaitée conformément à la norme ISO 639-1</b>
					(FR pour le français, DE pour l’allemand, etc.)
				</li>
			</ul>
			<h2>
				Comment programmer la publication d’une enquête pendant un congé?
			</h2>
			<p>
				Vous pouvez programmer la publication automatique de votre enquête au
				moment de votre choix.
			</p>
			<p>
				<ol>
					<li>
						Ouvrez votre enquête et allez à la page «Propriétés».
					</li>
					<li>
						Activez l’option «Automatic survey publishing» dans la section «Avancé».
					</li>
					<li>
						Indiquez les dates de début et de fin de publication de votre enquête.
					</li>
				</ol>
			</p>
			<h2>
				Est-il possible de programmer un rappel de la date de fin de l’enquête?
			</h2>
			<p>
				Oui, EUSurvey peut vous envoyer un courrier électronique de rappel avant
				que votre enquête ne prenne fin. Vous pourrez ainsi préparer les étapes
				suivantes (organiser les ressources nécessaires à l’analyse des résultats,
				par exemple).
			</p>
			<p>
				Pour activer cette fonction:
			</p>
			<p>
				<ol>
					<li>
						ouvrez votre enquête et allez à la page «Propriétés»;
					</li>
					<li>
						à la section «Avancé», rendez-vous à la rubrique «Reminder» et indiquez
						combien de temps à l’avance vous souhaitez recevoir un rappel;
					</li>
					<li>
						cliquez sur «Enregistrer».
					</li>
				</ol>
			</p>
			<p>
				Le courrier électronique de rappel sera envoyé à tous les gestionnaires du
				formulaire.
			</p>
			<h2>
				Pour le personnel des institutions de l’UE:
			</h2>
			<h2>
				quelles sont les exigences officielles pour le lancement d’une
				consultation publique ouverte (site web «Votre point de vue sur
				l’Europe»)?
			</h2>
			<p>
				Veuillez suivre
				<a
						href="https://circabc.europa.eu/sd/d/fc02d2ac-d94f-42ed-b866-b3429e0d717b/Survey_publication_your_voice_in_europe_NEW.pdf"
						target="_blank"
						>
					la procédure
				</a>
				du secrétariat général de la Commission pour publier une consultation
				publique ouverte sur le site
				<a href="https://ec.europa.eu/info/consultations_fr" target="_blank">
					«Votre point de vue sur l’Europe»
				</a>
				.
			</p>
			<h1>
				Gestion des enquêtes
			</h1>
			<h2>
				Est-il possible de corriger les erreurs découvertes dans une enquête?
			</h2>
			<p>
				Oui, vous pouvez éditer et modifier l’enquête aussi souvent que vous le
				souhaitez, ainsi qu’ajouter ou modifier des questions (y compris des
				questions dépendantes).
			</p>
			<p>
				Toutefois, plus vous effectuerez des modifications, moins les données
				recueillies seront utilisables, car il se peut que différents participants
				à votre enquête aient répondu à des versions différentes de l’enquête.
			</p>
			<p>
				Dès lors, si vous souhaitez toujours pouvoir comparer toutes les réponses,
				il est recommandé de ne pas modifier du tout la structure de votre enquête.
			</p>
			<p>
				Veuillez noter que vous conservez l’entière responsabilité de toute
				modification appliquée à votre enquête au cours de son existence.
			</p>
			<p>
				Pour modifier une enquête déjà publiée &#8594; cliquez sur le bouton «Appliquer
				les modifications» de la page «Aperçu», afin que ces modifications
				apparaissent dans l’enquête publiée.
			</p>
			<h2>
				Si vous souhaitez supprimer des réponses de votre enquête, veuillez
				consulter la section «En cas de modification de l’enquête, des
				contributions sont-elles supprimées?».
			</h2>
			<h2>
				En cas de modification de l’enquête, des contributions sont-elles
				supprimées?
			</h2>
			<p>
				Aucune contribution ne sera supprimée, sauf si vous effacez votre enquête
				du système.
			</p>
			<p>
				Il se pourrait toutefois que vous ne soyez pas en mesure de visualiser
				l’ensemble des données recueillies si vous supprimez certaines questions de
				l’enquête au cours de sa période d’activité, car les résultats de recherche
				affichés sont toujours issus de la dernière version publiée de l’enquête.
			</p>
			<p>
				Pour visualiser toutes les réponses, même celles à des questions supprimées
				pendant la période d’activité de votre enquête:
			</p>
			<p>
				&#8594; sélectionnez l’option «Contributions (including deleted questions)» dans
				le menu déroulant de la page «Résultats».
			</p>
			<h2>
				Comment modifier le titre d’une enquête?
			</h2>
			<p>
				Ouvrez votre enquête et allez à la page «Propriétés» &#8594; modifiez le titre de
				votre enquête depuis la rubrique «paramètres de base».
			</p>
			<p>
				Si vous avez déjà publié votre enquête, allez à la page «Aperçu» et cliquez
				sur «Appliquer les modifications».
			</p>
			<h2>
				Comment modifier l’adresse de contact d’une enquête?
			</h2>
			<p>
				Ouvrez votre enquête et allez à la page «Propriétés».
			</p>
			<p>
				Dans la rubrique «paramètres de base», choisissez une des options du menu
				déroulant sous la rubrique «Contact»:
			</p>
			<p>
				<ul>
					<li>
						«Contact Form»: les utilisateurs peuvent vous contacter au moyen d’un
						formulaire;
					</li>
					<li>
						«Courriel»: les utilisateurs peuvent vous contacter directement par
						courrier électronique (votre adresse électronique sera affichée);
					</li>
					<li>
						«Page web»: cette option redirige les utilisateurs vers une page web
						spécifique.
					</li>
				</ul>
			</p>
			<p>
				Si vous avez déjà publié votre enquête, allez à la page «Aperçu» et cliquez
				sur «Appliquer les modifications».
			</p>
			<h2>
				Comment personnaliser le message de confirmation par défaut?
			</h2>
			<p>
				Une fois que les participants ont soumis leur contribution, ils reçoivent
				un message de confirmation.
			</p>
			<p>
				Pour modifier le message par défaut:
			</p>
			<p>
				<ol>
					<li>
						ouvrez votre enquête et allez à la page «Propriétés»;
					</li>
					<li>
						dans la section «Pages spéciales», modifiez le message de confirmation.
					</li>
				</ol>
			</p>
			<p>
				Si vous avez déjà publié votre enquête, allez à la page «Aperçu» et cliquez
				sur «Appliquer les modifications».
			</p>
			<h2>
				Comment personnaliser le message d’indisponibilité par défaut?
			</h2>
			<p>
				La page d’indisponibilité contient le message que verront les participants
				si votre enquête n’est pas accessible.
			</p>
			<p>
				Pour modifier le message par défaut:
			</p>
			<p>
				<ol>
					<li>
						ouvrez votre enquête et allez à la page «Propriétés»;
					</li>
					<li>
						dans la section «Pages spéciales», cliquez sur l’icône en forme de
						crayon &#8594; changez le texte de la «Page d’indisponibilité».
					</li>
				</ol>
			</p>
			<p>
				Si vous avez déjà publié votre enquête, allez à la page «Aperçu» et cliquez
				sur «Appliquer les modifications».
			</p>
			<h2>
				Est-il possible d’archiver une enquête?<u> </u>
			</h2>
			<p>
				Oui, vous pouvez archiver votre enquête et la recharger ou la relancer plus
				tard.
			</p>
			<p>
				Pour archiver votre enquête &#8594; cliquez sur l’icône «Archiver» dans la barre
				d’outils de la page «Aperçu».
			</p>
			<p>
				Les questionnaires archivés ne peuvent ni être édités ni recevoir de
				nouvelles réponses.
			</p>
			<p>
				Toutefois, vous pouvez exporter les résultats ou télécharger une version
				PDF de votre enquête.
			</p>
			<p>
				Les questionnaires archivés sont disponibles dans le «Tableau de bord» d’où
				ils peuvent être restaurés.
			</p>
			<p>
				Les questionnaires restaurés peuvent être édités de nouveau.
			</p>
			<h2>
				Comment donner accès à mon enquête à d’autres utilisateurs?
			</h2>
			<p>
				Vous pouvez donner accès à d’autres utilisateurs pour différentes tâches:
			</p>
			<ul>
				<li>
					tester l’enquête («Accès aperçu formulaire»);
				</li>
				<li>
					accéder aux résultats («Résultats»);
				</li>
				<li>
					modifier l’enquête («Gestion du formulaire»).
				</li>
			</ul>
			<p>
				Pour octroyer un accès à une personne ou à un service: ouvrez votre enquête
				et allez à la page «Privilèges».
			</p>
			<p>
				Les droits d’accès suivants sont possibles:
			</p>
			<ul>
				<li>
					vert: accès en lecture et en écriture;
				</li>
				<li>
					jaune: accès en lecture;
				</li>
				<li>
					rouge: pas d’accès.
				</li>
			</ul>
			<p>
				<ol>
					<li>
						Dans la page «Privilèges», cliquez sur «Ajouter un utilisateur» ou
						«Ajouter un service».
					</li>
					<li>
						L’assistant d’ajout d’utilisateurs s’ouvre alors.
					</li>
					<li>
						Après avoir cliqué sur «Ajouter un utilisateur», vous devez sélectionner
						le domaine approprié (par exemple, «Commission européenne»).
					</li>
					<li>
						Indiquez le nom d’utilisateur, l’adresse électronique ou tout autre
						champ, et cliquez sur «Rechercher».
					</li>
					<li>
						Sélectionnez l’utilisateur et cliquez sur «OK».
					</li>
					<li>
						Si vous cliquez sur «Ajouter un service», sélectionnez le domaine
						approprié.
					</li>
					<li>
						Cherchez le service souhaité, et cliquez ensuite sur «OK».
					</li>
				</ol>
			</p>
			<p>
				Vous serez ensuite redirigé vers la page «Privilèges»,
			</p>
			<p>
				où vous pourrez déterminer les droits d’accès appropriés en cliquant sur
				les icônes rouges:
			</p>
			<ul>
				<li>
					Pour octroyer le droit de tester votre enquête:
					
					<ul>
						<li>
							sélectionnez la couleur verte pour la fonction «Accès aperçu formulaire»
							(il suffit de cliquer sur la couleur pour modifier les droits);
						</li>
						<li>
							les utilisateurs ajoutés verront automatiquement votre enquête apparaître
							dans leur page «Enquêtes» lors de leur prochaine connexion à EUSurvey (voir
							également
							<a
									href="#_Toc_6_2"
									>
								«Comment tester mon enquête auprès de mes collègues avant sa
								publication?»
							</a>
							).
						</li>
					</ul>
				</li>
			
				<li>
					Pour octroyer le droit de consulter les résultats de votre enquête:
					
					<ul>
						<li>
							sélectionnez la couleur jaune pour la fonction «Résultats». Les
							utilisateurs pourront afficher les résultats, sans pouvoir modifier ou
							supprimer quoi que ce soit;
						</li>
						<li>
							si vous sélectionnez la couleur verte, ils pourront afficher, modifier et
							supprimer les réponses (voir également
							<a
									href="#_Toc_10_7"
									>
								«Comment autoriser d’autres utilisateurs à accéder aux résultats de mon
								enquête?»
							</a>
							).
						</li>
					</ul>
				</li>
			
				<li>
					Pour octroyer le droit de modifier votre enquête:
					
					<ul>
						<li>
							sélectionnez la couleur verte &#8594; les utilisateurs peuvent maintenant la
							modifier;
						</li>
						<li>
							votre enquête apparaîtra automatiquement dans leur liste d’enquêtes (voir
							également
							<a
									href="#_Toc_4_11"
									>
								«Comment autoriser d’autres utilisateurs à modifier une enquête?»
							</a>
							).
						</li>
					</ul>
				</li>
			
				<li>
					Pour octroyer le droit de gérer les invitations à votre enquête:
					
					<ul>
						<li>
							si la couleur est jaune &#8594; les utilisateurs peuvent uniquement consulter
							les invitations;
						</li>
						<li>
							sélectionnez la couleur verte &#8594; les utilisateurs peuvent modifier les
							invitations;
						</li>
						<li>
							votre enquête apparaîtra automatiquement dans leur liste d’enquêtes (voir
							également
							<a
									href="#_Toc_4_11"
									>
								«Comment autoriser d’autres utilisateurs à modifier une enquête?»
							</a>
							).
						</li>
					</ul>
				</li>
			</ul>
		
			<p>
				Si vous sélectionnez la couleur verte pour les quatre cercles,
				l’utilisateur disposera de tous les droits d’accès pour votre enquête.
			</p>
			<p>
				Les propriétaires ou organisateurs d’enquêtes externes ne peuvent pas voir
				les champs de l’UE sur le bouton «Ajouter un utilisateur» de la page
				«Privilèges». Par conséquent, ils ne peuvent pas accorder un accès direct à
				ces utilisateurs.
			</p>
			<p>
				Veuillez
				<a href="https://ec.europa.eu/eusurvey/home/support?language=fr">
					nous contacter
				</a>
				si vous souhaitez demander l’accès pour des utilisateurs externes.
			</p>
			<h2>
				Que sont les journaux d’activité?
			</h2>
			<p>
				Les journaux d’activité surveillent et enregistrent l’activité sur votre
				enquête. Vous pouvez ainsi vérifier quel utilisateur a appliqué quelle
				modification à votre enquête et à quel moment.
			</p>
			<p>
				Vous pouvez également exporter les journaux d’activité dans plusieurs
				formats de fichiers tels que XLS, CSV et ODS.
			</p>
			<p>
				Pour consulter le journal d’activité de votre enquête &#8594; cliquez sur la page
				«Activité», à côté de la page «Propriétés».
			</p>
			<p>
				Si les journaux d’activité sont vides, il se peut qu’ils soient désactivés
				à l’échelle du système.
			</p>
			<p>
				Vous trouverez
				<a
						href="https://ec.europa.eu/eusurvey/resources/documents/ActivityLogEvents.xlsx"
						>
					ici
				</a>
				une liste des événements enregistrés.
			</p>
			<h1>
				Analyse, exportation et publication des résultats
			</h1>
			<h2>
				Où trouver les contributions reçues des participants?
			</h2>
			<p>
				Ouvrez votre enquête dans EUSurvey &#8594; cliquez sur la page «Résultats».
			</p>
			<p>
				Vous verrez d’abord un tableau présentant le contenu intégral de toutes les
				contributions soumises.
			</p>
			<p>
				Vous pouvez afficher les résultats de 2 façons:
			</p>
			<ul>
				<li>
					contenu intégral;
				</li>
				<li>
					statistiques.
				</li>
			</ul>
			<p>
				Pour passer d’un mode à l’autre &#8594; cliquez sur les icônes dans le coin
				supérieur gauche de la page.
			</p>
			<p>
				Voir également
				<a
						href="#_Toc_3_5"
						>
					«Comment ouvrir une enquête existante pour la modifier, par exemple?»)
				</a>
			</p>
			<h2>
				Comment télécharger les contributions reçues?
			</h2>
			<p>
				<ol>
					<li>
						Ouvrez votre enquête et allez à la page «Résultats».
					</li>
					<li>
						Cliquez sur «Exporter» dans le coin supérieur droit.
					</li>
					<li>
						Sélectionnez un format parmi les formats de fichiers disponibles pour
						l’exportation.
					</li>
					<li>
						Indiquez un nom de fichier dans la fenêtre qui s’affiche; le fichier
						d’exportation apparaîtra sous ce nom sur la page «Exports».
					</li>
				</ol>
			</p>
			<p>
				Différents formats de fichier d’exportation sont disponibles, en fonction
				du mode d’affichage (contenu intégral ou statistiques).
			</p>
			<p>
				Remarque: le fichier d’exportation ne contiendra que l’ensemble de
				questions définies comme exportables, ainsi que les résultats de recherche
				correspondant au filtre utilisé.
			</p>
			<h2>
				Comment extraire les brouillons de contributions?
			</h2>
			<p>
				Notre politique actuelle en matière de protection de la vie privée ne vous
				autorise pas à extraire les brouillons de contributions.
			</p>
			<p>
				Sur votre tableau de bord, vous pouvez voir le nombre de brouillons
				enregistrés pour votre enquête.
			</p>
			<h2>
				Comment rechercher et analyser un sous-ensemble défini de contributions?
			</h2>
			<p>
				Sur la page «Résultats»:
			</p>
			<p>
				<ul>
					<li>
						recherchez des mots clés dans les réponses en texte libre; ou
					</li>
					<li>
						sélectionnez des réponses spécifiques dans les questions à choix, au
						moyen des filtres proposés.
					</li>
				</ul>
			</p>
			<p>
				Cela réduit l’ensemble des réponses à un sous-ensemble de contributions.
			</p>
			<p>
				Pour des raisons de performance, vous ne pouvez utiliser qu’un maximum de
				trois filtres!
			</p>
			<p>
				Vous pouvez changer le mode d’affichage à tout moment afin d’effectuer une
				analyse statistique détaillée des données recueillies.
			</p>
			<p>
				Remarque: pour afficher et analyser les résultats, vous devez disposer de
				certains droits (voir
				<a
						href="#_Toc_10_7"
						>
					«Comment autoriser d’autres utilisateurs à accéder aux résultats de mon
					enquête?»
				</a>
				).
			</p>
			<p>
				Pour exporter un sous-ensemble de contributions, voir «Comment télécharger
				les contributions reçues?»
			</p>
			<p>
				Voir également
				<a
						href="#_Toc_10_1"
						>
					«Où trouver les contributions reçues des participants?»
				</a>
			</p>
			<h2>
				Comment publier les résultats?
			</h2>
			<p>
				<ol>
					<li>
						Ouvrez l’enquête.
					</li>
					<li>
						Allez à la page «Propriétés» et sélectionnez «Publier les résultats».
					</li>
					<li>
						Vous verrez alors l’URL menant aux résultats publiés.
					</li>
					<li>
						Choisissez les questions, réponses ou contributions que vous souhaitez
						publier.
					</li>
					<li>
						Pour y accéder directement &#8594; cliquez sur le bouton «Modifier la
						publication des résultats» de la page «Aperçu».
					</li>
					<li>
						Veillez à effectuer une sélection dans la rubrique «Lancer» dans la
						section «Publier les résultats», faute de quoi le système ne publiera aucun
						résultat.
					</li>
				</ol>
			</p>
			<h2>
				Comment accéder aux résultats publiés?
			</h2>
			<p>
				Ouvrez la page «Aperçu» &#8594; cliquez sur l’hyperlien «Publié», juste à côté de
				«Résultats».
			</p>
			<p>
				Toute personne connaissant cette adresse pourra accéder à vos résultats.
			</p>
			<h2>
				Comment autoriser d’autres utilisateurs à accéder aux résultats d’une
				enquête?
			</h2>
			<p>
				Ouvrez votre enquête &#8594; allez à la page «Privilèges» et donnez accès aux
				résultats à d’autres utilisateurs.
			</p>
			<p>
				Pour en savoir plus, voir également
				<a
						href="#_Toc_9_9"
						>
					«Comment donner accès à mon enquête à d’autres utilisateurs?»
				</a>
			</p>
			<h2>
				Je ne parviens pas à décompresser mes fichiers exportés; comment faire?
			</h2>
			<p>
				Cela peut se produire si le nom des fichiers contenus dans votre dossier
				est trop long.
			</p>
			<p>
				Dans Windows, la longueur du chemin d’accès d’un fichier sur disque dur ne
				peut pas dépasser 260 caractères.
			</p>
			<p>
				Voici les solutions possibles:
			</p>
			<ul>
				<li>
					décompressez le dossier dans le répertoire racine de votre système
					d’exploitation, dans le répertoire «C:» au lieu de
					«C:\Users\NOMD’UTILISATEUR\Bureau», par exemple; ou
				</li>
				<li>
					lorsque vous décompressez les fichiers, renommez le dossier afin de
					réduire la longueur du chemin d’accès.
				</li>
			</ul>
			<h2>
				Les résultats publiés - protection des données à caractère personnel
				chargées par les participants
			</h2>
			<p>
				En vertu des règles en matière de protection des données, le gestionnaire
				du formulaire peut publier les fichiers chargés par les participants en
				parallèle aux résultats.
			</p>
			<p>
				Pour ce faire &#8594; cochez l’option «Documents chargés».
			</p>
			<p>
				Celle-ci se trouve dans la section «Publier les résultats» de la page
				«Propriétés».
			</p>
			<p>
				Cette option apparaît uniquement si l’enquête contient des fichiers
				chargés.
			</p>
			<h2>
				Comment concevoir une enquête afin de publier les résultats avec ou sans
				données personnelles?
			</h2>
			<p>
				Si vous voulez donner le choix à vos participants de publier ou non leurs
				données personnelles avec leurs réponses, suivez
				<a
						href="https://circabc.europa.eu/sd/d/e68ff760-226f-40e9-b7cb-d3dcdd04bfb1/How_to_publish_survey_results_anonymously.pdf"
						target="_blank"
						>
					ces instructions
				</a>
				pour concevoir une enquête conforme aux exigences.
			</p>
			<h2>
				Pourquoi mes résultats ne sont-ils pas à jour?
			</h2>
			<p>
				Une nouvelle base de données a été introduite pour améliorer les
				performances d’EUSurvey lors des recherches portant sur des résultats
				d’enquêtes.
			</p>
			<p>
				Toutefois, cela peut entraîner des retards jusqu’à ce que les données les
				plus récentes apparaissent sur la page de résultats de votre enquête.
			</p>
			<p>
				Ce délai ne devrait pas dépasser 12 heures; si le retard dépasse 12 heures
				&#8594; contactez
				<a href="https://ec.europa.eu/eusurvey/home/support?language=fr">
					l’assistance
				</a>
				d’EUSurvey.
			</p>
			<h2>Comment puis-je récupérer les fichiers téléchargés par les contributeurs ?</h2>

			<p>EUSurvey propose différents formats d'export : XLS, PDF, ODS et XML.
				<br>
				En fonction du format sélectionné, la structure et le contenu des
				fichiers exportés pour l'élément «Charger un fichier» sont décrits ci-dessous :</p>

			<h4>Export
				des résultats en XLS</h4>

			<ol>
				<li>
					<p>Un fichier Excel contenant les informations suivantes :</p>
					<p>Alias : Alias de l'enquête (exemple :
						6459a3c9-e517-4a34-8e5d-70185db022c3)<br>
						Date d'export : Date au format «dd-mm-yyyy hh:mm» (exemple :
						28-09-2020 15:28)</p>

					<p>Un tableau composé comme ci-dessous :</p>
					<ul>
						<li>Chaque colonne représente une question «Charger un fichier» différente.</li>

						<li>Chaque ligne représente une contribution
							différente.</li>

						<li>Chaque cellule contient tous les noms des
							fichiers téléchargés.</li>
					</ul><br>
				</li>
				<li>
					<p>Des dossiers correspondant à chaque contribution et nommés avec
						l'ID de la contribution, contenant des sous-dossiers pour chaque question «Charger un fichier» (Upload_1, Upload_2, etc.). </p>

					<p>Par exemple :</p>

					<p>Dossier : 6cf0463c-29f4-4bea-a195-10e77c61dda1<br>

						Sous-dossier : Upload_1 (correspondant à la première question de
						type «Charger un fichier») contient tous les fichiers téléchargés.<br>

						Sous-dossier : Upload_2 (correspondant à la deuxième question de type
						«Charger un fichier») contient tous les fichiers téléchargés.</p>
				</li>
			</ol>
			<h4>Export
				des résultats au format PDF</h4>
			<ol>
				<li>
					<p>Dossier nommé «PDFs» contenant toutes les contributions à
						l'enquête sous forme de documents PDF.</p>
				</li>
				<li>
					<p>Des dossiers correspondant à chaque contribution et nomméd avec
						l'ID de la contribution.<br>

						Chaque dossier contient des sous-dossiers pour chaque question de type
						«Charger un fichier» (Upload_1, Upload_2, etc.).</p>
				</li>
			</ol>

			<h4>Export des résultats au format ODS</h4>
			<ol>
				<li>
					<p>Un fichier Open Office contenant les informations suivantes :</p>

					<p>Alias : Alias de l'enquête (exemple :
						6459a3c9-e517-4a34-8e5d-70185db022c3)<br>

						Date d'export : Date au format «dd-mm-yyyy hh:mm» (exemple :
						28-09-2020 15:28)</p>

					<p>Un tableau composé comme ci-dessous :</p>

					<ul>
						<li>Chaque colonne représente une question «Charger un fichier» différente.</li>

						<li>Chaque ligne représente une contribution
							différente.</li>

						<li>Chaque cellule contient tous les noms des
							fichiers téléchargés.</li>
					</ul><br>
				</li>
				<li>
					<p>Des dossiers correspondant à chaque contribution et nommés avec
						l'ID de la contribution.<br>
						Chaque dossier contient des sous-dossiers pour chaque question de
						téléchargement de fichiers (Upload_1, Upload_2, etc.).</p>
				</li>
			</ol>
			<h4>Export
				des résultats au format XML</h4>

			<p>Cet export est constitué d'un fichier XML contenant les résultats de
				manière structurée.<br>

			<b>Les fichiers téléchargés ne sont pas disponibles dans ce cas.</b></p>

			<h2>Comment le score des questions de classement est-il calculé ?</h2>
			<p>
				Les questions de classement sont utilisées pour offrir aux participants à votre enquête la possibilité de classer un ensemble d'éléments par ordre d'importance.
				Il est recommandé de limiter le nombre d'éléments à classer à 5, car si vous en demandez davantage aux participants à l'enquête, il pourrait leur être difficile de classer correctement tous les éléments.
			</p>
			<p>
				Le choix le plus préféré des répondants (qu'ils classent en premier) obtient le poids le plus élevé, et le choix le moins préféré (qu'ils classent en dernier) obtient un poids de 1.
				Le poids est donc inversé proportionnellement par rapport au classement de l'élément.
			</p>
			<p>
				Par exemple, si une question de classement est composée de 5 éléments, les poids sont attribués comme suit :
			</p>
			<ul>
				<li>L'élément le mieux classé a un poids de 5</li>
				<li>Le deuxième élément a un poids de 4</li>
				<li>Le troisième élément a un poids de 3</li>
				<li>Le quatrième élément a un poids de 2</li>
				<li>Le cinquième élément a un poids de 1</li>
			</ul>
			<p>
				Le score est calculé comme étant le poids moyen donné par les répondants à l'enquête.
			</p>
			<h2>
				Exporter l'ensemble de données (c'est-à-dire les réponses à l'enquête)
			</h2>
			<p>
				Vous pouvez exporter les réponses de votre enquête à l'aide de la fonction d'exportation.
				Cela peut s'avérer utile, par exemple, pour le traitement ultérieur des données dans Excel.
			</p>
			<p>
				Pour exporter vos réponses :
			</p>
			<ol>
				<li>Allez dans l'onglet «Résultats» (premier écran, la vue tabulaire).</li>
				<li>Cliquez sur le bouton «Exporter» (une fenêtre contextuelle s'ouvre).</li>
				<li>Saisissez un nom pour votre export.</li>
				<li>Sélectionnez le format de fichier (lorsque le fichier d'export est généré, vous recevez une notification).</li>
				<li>Accédez à l'onglet «Exports».</li>
				<li>Téléchargez votre fichier.</li>
			</ol>
			<h1>
				Style et mise en page
			</h1>
			<h2>
				Comment modifier l’apparence générale d’une enquête?
			</h2>
			<p>
				<ol>
					<li>
						Ouvrez votre enquête, allez à la page «Propriétés»
					</li>
					<li>
						et sélectionnez la section «Apparence».
					</li>
					<li>
						Choisissez une nouvelle apparence pour votre enquête à l’aide du menu
						déroulant de la rubrique «Apparence» &#8594; cliquez sur «Enregistrer».
					</li>
				</ol>
			</p>
			<p>
				Si vous avez déjà publié votre enquête &#8594; allez à la page «Aperçu» et
				cliquez sur «Appliquer les modifications».
			</p>
			<h2>
				Comment créer son propre thème visuel?
			</h2>
			<p>
				<ol>
					<li>
				Allez à l’onglet «Paramètres», en haut de l’écran &#8594; cliquez sur
				«Apparences».
			</li>
			<li>
				Cliquez sur «Créer une nouvelle apparence».
			</li>
			<li>
				 L’éditeur d’apparences s’ouvre alors.
			</p>
			<p>
				Vous pouvez reprendre un thème visuel existant et utiliser l’éditeur
				d’apparences en ligne pour adapter ce modèle à vos besoins.
			</p>
			<h2>
				Comment ajouter un logo à une enquête?
			</h2>
			<p>
				Pour que le logo de votre projet ou de votre entreprise apparaisse dans le
				coin supérieur droit de votre enquête &#8594; téléchargez un fichier d’image à la
				section «Apparence» de la page «Propriétés».
			</p>
			<p>
				Si vous avez déjà publié votre enquête &#8594; allez à la page «Aperçu» et
				cliquez sur «Appliquer les modifications».
			</p>
			<h2>
				Comment ajouter des liens utiles à une enquête?
			</h2>
			<p>
				<ol>
					<li>
						Ouvrez votre enquête.
					</li>
					<li>
						Allez à la page «Propriétés» et sélectionnez «Avancé».
					</li>
					<li>
						Ajoutez des URL et leurs titres dans la rubrique «Liens utiles».
					</li>
					<li>
						Ces liens figureront sur chaque page de votre enquête, sur le côté
						droit.
					</li>
				</ol>
			</p>
			<p>
				Si vous avez déjà publié votre enquête &#8594; allez à la page «Aperçu» et
				cliquez sur «Appliquer les modifications».
			</p>
			<h2>
				Comment ajouter des documents de référence à une enquête?
			</h2>
			<p>
				<ol>
					<li>
						Ouvrez votre enquête.
					</li>
					<li>
						Allez à la page «Propriétés» et sélectionnez «Avancé».
					</li>
					<li>
						Chargez un fichier à la rubrique «Documents de référence».
					</li>
					<li>
						Ces documents figureront sur chaque page de votre enquête, sur le côté
						droit.
					</li>
				</ol>
			</p>
			<p>
				Si vous avez déjà publié votre enquête &#8594; allez à la page «Aperçu» et
				cliquez sur «Appliquer les modifications».
			</p>
			<h2>
				Comment créer une enquête en plusieurs pages?
			</h2>
			<p>
				Les sections de haut niveau de votre enquête peuvent être divisées
				automatiquement en pages distinctes.
			</p>
			<p>
				<ol>
					<li>
						Ouvrez votre enquête.
					</li>
					<li>
						Allez à la page «Propriétés» et sélectionnez la section «Apparence».
					</li>
					<li>
						 Activez l’option «Présentation sur plusieurs pages» et cliquez sur
						«Enregistrer».
					</li>
				</ol>
			</p>
			<p>
				Si vous avez déjà publié votre enquête &#8594; allez à la page «Aperçu» et
				cliquez sur «Appliquer les modifications».
			</p>
			<h2>
				Comment activer la numérotation automatique pour mon enquête?
			</h2>
			<p>
				Pour numéroter automatiquement toutes les sections et questions de votre
				formulaire:
			</p>
			<p>
				<ol>
					<li>
						ouvrez votre enquête, allez à la page «Propriétés» et sélectionnez
						«Apparence»;
					</li>
					<li>
						activez les options «Automatically number sections» et/ou «Automatically
						number questions» et sélectionnez vos préférences;
					</li>
					<li>
						cliquez sur «Enregistrer».
					</li>
				</ol>
			</p>
			<p>
				Si vous avez déjà publié votre enquête &#8594; allez à la page «Aperçu» et
				cliquez sur «Appliquer les modifications».
			</p>
			<h2>
				Est-il possible de créer une apparence personnalisée pour une enquête?
			</h2>
			<p>
				Oui, vous pouvez créer une nouvelle apparence pour votre enquête en suivant
				les étapes ci-dessous.
			</p>
			<p>
				<ol>
					<li>
						Allez à la page «Paramètres» &#8594; sélectionnez «Apparences».
					</li>
					<li>
						Cliquez sur «Créer une nouvelle apparence» &#8594; modifiez le visuel des
						différents éléments de votre enquête: les questions, son texte, les titres,
						les bulles d’aides et bien d’autres.
					</li>
					<li>
						Donnez un nom à votre nouvelle apparence.
					</li>
					<li>
						Sélectionnez l’élément dont vous voulez personnaliser l’apparence.
					</li>
					<li>
						À droite de la page, vous trouverez une boîte où vous pourrez modifier
						la police de l’élément:
						<ul>
							<li>
								couleurs d’avant-plan et d’arrière-plan;
							</li>
							<li>
								style de police, polices de caractère, taille et épaisseur.
							</li>
						</ul>
					</li>		
					<li>
						Vous pouvez visualiser l’apparence des éléments modifiés dans la zone de
						prévisualisation, qui se trouve en bas de la page.
					</li>
					<li>
						Cliquez sur «Enregistrer».
					</li>
				</ol>
			</p>
			<p>
				Si vous souhaitez modifier plusieurs éléments &#8594; effectuez toutes vos
				modifications &#8594; enregistrez tous les éléments modifiés une fois que vous
				avez terminé (il n’est pas nécessaire de sauvegarder à chaque modification
				d’élément).
			</p>
			<p>
				Pour appliquer votre nouvelle apparence à votre enquête &#8594; allez à la page
				«Propriétés» et sélectionnez «Apparence».
			</p>
			<p>
				Choisissez votre nouvelle apparence dans le menu déroulant de la rubrique
				«Apparence» &#8594; cliquez sur «Enregistrer».
			</p>
			<h1>
				Gestion des contacts et des invitations
			</h1>
			<h2>
				Qu’est-ce que le «carnet d’adresses»?
			</h2>
			<p>
				Le carnet d’adresses permet de créer vos propres groupes de participants.
			</p>
			<p>
				Vous pouvez ainsi inviter des personnes ou organisations qui correspondent
				à certains critères (par exemple, «masculin» et «plus de 21 ans»).
			</p>
			<p>
				Chaque participant potentiel constitue un contact du carnet d’adresses.
				Pour chaque contact, vous pouvez spécifier autant d’attributs que vous le
				souhaitez.
			</p>
			<p>
				Pour ajouter un contact dans votre carnet d’adresses, il vous faut un
				identifiant («Nom») et une adresse électronique.
			</p>
			<h2>
				Que sont les «attributs» des contacts?
			</h2>
			<p>
				Tout contact enregistré dans le carnet d’adresses peut être caractérisé par
				un ensemble d’attributs variables, tels que «Pays», «Téléphone»,
				«Remarques», etc.
			</p>
			<p>
				Vous pouvez créer de nouveaux attributs en modifiant un contact.
			</p>
			<p>
				<ol>
					<li>
						Ouvrez la fenêtre «Modifier le contact» &#8594; dans le menu déroulant des
						attributs, sélectionnez «Nouveau...».
					</li>
					<li>
						Dans la fenêtre qui s’affiche, vous pouvez modifier le nouvel attribut.
					</li>
					<li>
						L’attribut ainsi créé s’affichera dans une colonne du carnet d’adresses;
						il peut aussi être ajouté à une série de contacts.
					</li>
				</ol>
			</p>
			<h2>
				Comment ajouter de nouveaux contacts dans le carnet d’adresses?
			</h2>
			<p>
				Allez à la page «Carnet d’adresses» &#8594; cliquez «Ajouter un contact» pour
				ajouter un seul contact.
			</p>
			<p>
				Vous pouvez cliquer sur «Importer» pour télécharger une liste de contacts
				existante au format XLS, ODS, CSV ou TXT.
			</p>
			<p>
				Voir également
				<a
						href="#_Toc_12_5"
						>
					«Comment importer un fichier avec plusieurs contacts dans le carnet
					d’adresses?»
				</a>
			</p>
			<h2>
				Qu’est-ce qu’un «formulaire d’enregistrement»?
			</h2>
			<p>
				Il s’agit d’un formulaire qui permet de créer automatiquement des contacts
				à partir des données à caractère personnel introduites par les
				participants.
			</p>
			<p>
				Vous pouvez en créer un en suivant les étapes ci-dessous.
			</p>
			<p>
				<ol>
					<li>
						Ouvrez votre enquête.
					</li>
					<li>
						Allez à la page «Propriétés» et sélectionnez «Avancé».
					</li>
					<li>
						Activez l’option «Créer des contacts» et cliquez sur «Enregistrer».
					</li>
				</ol>
			</p>
			<p>
				Dès que cette option est activée, le système insère deux questions
				obligatoires en texte libre («Nom» et «Adresse électronique»),
			</p>
			<p>
				afin de garantir que chaque participant indique des coordonnées valables.
			</p>
			<p>
				Si vous activez l’option «Attribut» pour des questions spécifiques, vous
				pouvez choisir quelles autres informations seront enregistrées pour chaque
				nouveau contact (par exemple, vous pouvez associer l’attribut «Téléphone» à
				une question en texte libre afin d’enregistrer le numéro de téléphone du
				participant dans le carnet d’adresses).
			</p>
			<h2>
				Comment importer un fichier avec plusieurs contacts dans le carnet
				d’adresses?
			</h2>
			<p>
				EUSurvey dispose d’un assistant qui facilite l’importation de listes de
				contacts dans le système.
			</p>
			<p>
				Les formats de fichiers suivants sont pris en charge:
			</p>
			<ul>
				<li>
					XLS;
				</li>
				<li>
					ODS;
				</li>
				<li>
					CSV;
				</li>
				<li>
					TXT (avec séparateurs).
				</li>
			</ul>
			<p>
				Pour démarrer l’assistant:
			</p>
			<p>
				<ol>
					<li>
						ouvrez la page «Carnet d’adresses» et cliquez sur «Importer»;
					</li>
					<li>
						sélectionnez le fichier dans lequel vous avez enregistré vos contacts;
					</li>
					<li>
						indiquez si votre fichier contient une ligne d’en-tête;
					</li>
					<li>
						pour un fichier CSV ou TXT, précisez le type de séparateur utilisé (le
						caractère le plus probable est proposé par défaut).
					</li>
				</ol>
			</p>
			<p>
				Ensuite:
			</p>
			<p>
				<ol>
					<li>
						le système vous demandera d’indiquer quelles colonnes correspondent à
						quels attributs pour l’importation des contacts (vous devez indiquer
						quelles colonnes contiennent les attributs obligatoires «Nom» et «Adresse
						électronique» avant de pouvoir continuer);
					</li>
					<li>
						cliquez sur «Suivant»; le système charge votre fichier dans le système
						et affiche les contacts individuels qui seront importés;
					</li>
					<li>
						vous pouvez désélectionner les contacts que vous ne souhaitez pas
						importer;
					</li>
					<li>
						cliquez sur «Enregistrer» pour ajouter les contacts à votre carnet
						d’adresses.
					</li>
				</ol>
			</p>
			<h2>
				Comment modifier un attribut pour plusieurs contacts à la fois?
			</h2>
			<p>
				<ol>
					<li>
						Recherchez puis sélectionnez les contacts en question dans votre carnet
						d’adresses.
					</li>
					<li>
						Cliquez sur «Modifier en bloc», puis sur «OK».
					</li>
					<li>
						La fenêtre qui s’affiche permet de conserver, d’effacer ou de modifier
						les attributs de plusieurs contacts à la fois; par défaut, seuls les
						attributs configurés sont affichés.
					</li>
					<li>
						Cliquez sur la croix verte pour afficher les autres attributs.
					</li>
					<li>
						Après avoir introduit les modifications voulues, cliquez sur «Mettre à
						jour» et confirmez. L’application enregistre les modifications dans le
						carnet d’adresses.
					</li>
				</ol>
			</p>
			<h2>
				Est-il possible d’exporter les contacts du carnet d’adresses dans un
				fichier?
			</h2>
			<p>
				Oui.
			</p>
			<p>
				Sur la page «Carnet d’adresses», cliquez sur une des icônes du coin
				supérieur droit, qui indiquent les formats de fichiers disponibles.
			</p>
			<p>
				Vous trouverez les contacts exportés sur la page «Exports».
			</p>
			<h1>
				Inviter des participants
			</h1>
			<h2>
				Comment définir un groupe de participants potentiels? Qu’est-ce qu’une
				«liste d’invités»?
			</h2>
			<p>
				Vous pouvez sélectionner plusieurs contacts à la fois et envoyer à chacun
				de ces contacts un message contenant un lien d’accès individuel. Ces
				groupes de contacts sont appelés «listes d’invités».
			</p>
			<p>
				Il s’agit de la deuxième façon (outre la définition d’un mot de passe pour
				l’enquête) de permettre à des personnes de participer à votre enquête.
			</p>
			<p>
				Pour inviter plusieurs contacts à participer &#8594; ouvrez votre enquête et
				allez à la page «Participants».
			</p>
			<p>
				Choisissez un type parmi les types de «liste d’invités» proposés pour
				lancer un assistant qui vous guidera tout au long du processus:
			</p>
			<ul>
				<li>
					<b>«Contact list»</b>
					: contacts du carnet d’adresses
					<br/>
					Sélectionnez des contacts du carnet d’adresses pour les ajouter à votre
					liste d’invités (voir
					<a
							href="#_Toc_12_1"
							>
						«Qu’est-ce que le “carnet d’adresses”?»
					</a>
					).
				</li>
				<li>
					<b>«EU list»</b>
					: institutions et autres organes de l’UE (uniquement pour le personnel
					des institutions de l’UE)
					<br/>
					Sélectionnez plusieurs services de votre institution ou agence pour
					ajouter à la liste toutes les personnes de ces services.
				</li>
				<li>
					<b>«Token list»</b>
					<br/>
					Cette fonction crée une liste de jetons (des codes d’accès uniques) qui
					peuvent être distribués hors ligne afin d’accéder à une enquête en
					ligne sécurisée.
				</li>
			</ul>
			<p>
				Utilisez la fonction de recherche de votre carnet d’adresses &#8594; cliquez sur
				le bouton «&gt;&gt;» au centre de la page pour déplacer vos contacts depuis
				votre carnet d’adresses vers votre nouvelle liste d’invités.
			</p>
			<p>
				Cliquez sur «Enregistrer» pour créer une nouvelle liste d’invités contenant
				tous les contacts que vous souhaitez inviter à participer à l’enquête.
			</p>
			<p>
				Consultez la section ci-après pour voir comment envoyer des liens d’accès
				individuels par courrier électronique à des contacts configurés au moyen
				d’une liste d’invités.
			</p>
			<h2>
				Comment modifier/supprimer une liste d’invités existante?
			</h2>
			<p>
				<ol>
					<li>
						Ouvrez votre enquête.
					</li>
					<li>
						Allez à la page «Participants».
					</li>
					<li>
						Pour modifier la liste d’invités &#8594; cliquez sur la petite icône en forme
						de crayon.
					</li>
					<li>
						Pour supprimer une liste &#8594; cliquez d’abord sur le bouton «Désactiver».
					</li>
					<li>
						5. Cliquez ensuite sur le bouton «Supprimer».
					</li>
				</ol>
			</p>
			<h2>
				Comment envoyer un courrier électronique d’invitation aux participants?
			</h2>
			<p>
				Une fois la nouvelle liste d’invités créée, vous pouvez envoyer des
				courriers électroniques d’invitation aux invités.
			</p>
			<p>
				Pour les questionnaires configurés comme sécurisés et ouverts, ils
				recevront chacun un lien d’accès individuel.
			</p>
			<p>
				<b>
					Cela signifie que toute personne recevant un courrier électronique
					d’invitation EUSurvey ne peut soumettre qu’une seule contribution
					(réponse).
				</b>
			</p>
			<p>
				<ol>
					<li>
						Sur la page «Participants», cliquez sur la petite icône en forme
						d’enveloppe.
					</li>
					<li>
						Une fenêtre s’ouvre où vous pouvez choisir un modèle de courrier
						électronique à partir de la boîte «Select mail design»; par défaut, le
						style utilisé est «EUSurvey».
					</li>
					<li>
						Vous pouvez ensuite modifier l’objet et le corps du message de votre
						courrier électronique, et ajouter un courriel de réponse; toutes les
						réponses à vos invitations seront envoyées à cette adresse.
					</li>
					<li>
						Par après, enregistrez le corps du message de votre courrier
						électronique &#8594; il sera disponible pour toutes vos listes d’invités et
						enquêtes; vous le trouverez dans le menu déroulant de la boîte «Use mail
						template».
					</li>
					<li>
						Cliquez ensuite sur «Suivant» &#8594; un assistant vous aidera à envoyer les
						invitations.
					</li>
				</ol>
			</p>
			<h2>
				Comment utiliser des jetons (tokens) pour créer un lien ?
			</h2>
			<p>
				Afin de créer une liste de jetons (jeton d'authentification) qui peuvent être distribués pour accéder à un questionnaire en ligne sécurisé, ouvrez votre enquête et allez à la page «Participants».
				Cliquez sur «Créer une nouvelle liste d'invités» pour lancer un assistant qui vous guidera tout au long du processus.
				Choisissez un nom pour le groupe et sélectionnez «Tokens» parmi les types de listes d'invités.
			</p>
			<p>
				Utilisez les jetons créés pour créer des liens d'accès individuels que vous pouvez envoyer par courrier électronique aux participants en utilisant l'URL ci-dessous :
			</p>
			<p>
				https://ec.europa.eu/eusurvey/runner/<span style="color: #e50000; font-weight: bold">SurveyAlias</span>/<span style="color: #e50000; font-weight: bold">TOKEN</span>
			</p>
			<p>
				Il suffit de remplacer :
			</p>
			<ul>
				<li>
					<span style="color: #e50000; font-weight: bold">SurveyAlias</span> par <b>l’alias</b> <b>de votre enquête</b>
				</li>
				<li>
					<span style="color: #e50000; font-weight: bold">TOKEN</span> avec un des jetons de la liste
				</li>
			</ul>
			<h1>
				Gestion du compte personnel
			</h1>
			<h2>
				Comment modifier mon mot de passe?
			</h2>
			<p>
				Les utilisateurs doivent modifier leur mot de passe EU Login s’ils ont
				perdu celui-ci.
			</p>
			<p>
				Pour ce faire: rendez-vous sur la page d’accueil d’EU Login &#8594; cliquez sur
				«Mot de passe perdu?».
			</p>
			<h2>
				Comment modifier mon adresse électronique?
			</h2>
			<p>
				Si vous accédez à EUSurvey au moyen de votre compte EU Login, vous pouvez
				modifier votre adresse électronique en suivant les étapes ci-dessous:
			</p>
			<p>
				connectez-vous à EU Login &#8594; une fois connecté, cliquez sur «Modifier mes
				données personnelles» à la page «Mon compte»;
			</p>
			<p>
				si vous utilisez la version open source d’EUSurvey ou l’interface API &#8594;
				connectez-vous à l’application &#8594; cliquez sur «Paramètres», puis sur «Mon
				compte», puis sur l’icône en forme de crayon à la rubrique «Courriel».
			</p>
			<h2>
				Comment modifier la langue par défaut?
			</h2>
			<p>
				Cliquez sur «Paramètres», puis sur «Mon compte», puis sur l’icône en forme
				de crayon à la rubrique «Langue».
			</p>
			<p>
				Une fois la modification enregistrée, le système proposera d’utiliser la
				langue sélectionnée comme langue principale pour toutes vos nouvelles
				enquêtes.
			</p>
			<h1>
				Accord de traitement des données et protection de la vie privée
			</h1>
			<h2>
				Ce système utilise des cookies. Quelles informations y sont enregistrées?
			</h2>
			<p>
				Le système utilise des «cookies» (ou témoins) de session afin d’assurer une
				bonne communication entre le client et le serveur.
			</p>
			<p>
				Le navigateur de l’utilisateur doit donc être configuré pour accepter les
				«cookies», qui disparaissent une fois la session terminée.
			</p>
			<p>
				Le système enregistre une copie de sauvegarde locale de la contribution
				d’un participant à une enquête. Ce fichier peut servir en cas
				d’indisponibilité du serveur lorsque l’utilisateur envoie sa contribution,
				ou si son ordinateur s’éteint accidentellement, par exemple.
			</p>
			<p>
				Il contient le numéro des questions et les brouillons de réponses.
			</p>
			<p>
				Une fois la contribution à l’enquête envoyée au serveur, ou après
				sauvegarde d’un brouillon sur celui-ci, ces données locales sont
				supprimées.
			</p>
			<p>
				Au-dessus de l’enquête figure une case à cocher avec la mention
				«Enregistrer une copie de sauvegarde locale sur votre ordinateur (à
				désactiver si vous utilisez un ordinateur public/partagé)», qui permet de
				désactiver cette fonction, de telle sorte qu’aucune donnée ne sera
				conservée sur son ordinateur.
			</p>
			<h2>
				Quelles informations sont enregistrées par EUSurvey lorsqu’un participant
				soumet une contribution (réponse)?
			</h2>
			<p>
				Les informations enregistrées par EUSurvey dépendent des paramètres de
				sécurité de votre enquête ainsi que de la méthode que vous utilisez pour
				inviter vos participants à contribuer à votre enquête.
			</p>
			<p>
				<b>Enquêtes ouvertes au public:</b>
			</p>
			<p>
				par défaut, si votre enquête n’est <b>pas sécurisée</b>, EUSurvey    <b>n’enregistre aucune information liée à l’utilisateur</b>.
			</p>
			<p>
				Toutefois, l’adresse IP de chaque connexion au serveur est enregistrée pour
				des raisons de sécurité (voir
				<a
						href="https://ec.europa.eu/eusurvey/home/privacystatement?language=fr"
						target="_blank"
						>
					déclaration relative à la protection de la vie privée
				</a>
				).
			</p>
			<p>
				<b>Enquêtes protégées par mot de passe:</b>
			</p>
			<p>
				lorsque votre enquête est sécurisée par un <b>mot de passe seulement</b>,
				EUSurvey <b>n’enregistre aucune</b> information liée à l’utilisateur.
			</p>
			<p>
				Toutefois, l’adresse IP de chaque connexion au serveur est enregistrée pour
				des raisons de sécurité (voir
				<a
						href="https://ec.europa.eu/eusurvey/home/privacystatement?language=fr"
						target="_blank"
						>
					déclaration relative à la protection de la vie privée
				</a>
				).
			</p>
			<p>
				<b>Enquête sécurisée avec l’authentification EU Login:</b>
			</p>
			<p>
				lorsque votre enquête est sécurisée par <b>l’authentification EU Login</b>,
				EUSurvey <b>enregistrera</b> l’adresse électronique du compte EU Login.
			</p>
			<p>
				Toutefois, l’adresse IP de chaque connexion au serveur est enregistrée pour
				des raisons de sécurité (voir
				<a
						href="https://ec.europa.eu/eusurvey/home/privacystatement?language=fr"
						target="_blank"
						>
					déclaration relative à la protection de la vie privée
				</a>
				).
			</p>
			<p>
				<b>Envoi d’invitations par l’intermédiaire d’EUSurvey:</b>
				<b></b>
			</p>
			<p>
				si vous utilisez EUSurvey pour envoyer des invitations à vos participants
				au moyen d’une liste d’invités sur la page «Participants», ils recevront
				chacun <b>un lien d’accès individuel</b>.
			</p>
			<p>
				Lors de la soumission, EUSurvey enregistrera un numéro d’invitation pouvant
				être utilisé pour <b>associer</b> le participant invité aux contributions
				soumises. Ce comportement est indépendant des paramètres de sécurité de
				votre enquête.
			</p>
			<p>
				De plus, l’adresse IP de chaque connexion au serveur est enregistrée pour
				des raisons de sécurité (voir
				<a
						href="https://ec.europa.eu/eusurvey/home/privacystatement?language=fr"
						target="_blank"
						>
					déclaration relative à la protection de la vie privée
				</a>
				).
			</p>
			<p>
				<b>Créer une enquête anonyme:</b>
			</p>
			<p>
				Vous pouvez choisir de créer une enquête anonyme en utilisant l'option "Mode enquête anonyme" dans les propriétés de l'enquête. Si cette option est activée, les contributions à votre enquête seront anonymes car EUSurvey n'enregistrera pas de données personnelles telles que les adresses IP. Si vous souhaitez que votre enquête soit totalement anonyme, n'incluez pas de questions collectant des données personnelles dans la conception de votre enquête.
			</p>
			<h2>
				Les enquêtes doivent-elles inclure une déclaration relative à la protection
				de la vie privée?
			</h2>
			<p>
				Cela dépend des questions posées et du type de données recueillies dans le
				cadre de votre enquête.
			</p>
			<p>
				Veuillez noter que certaines personnes pourraient refuser de répondre à
				votre enquête si vous n’êtes pas en mesure de garantir la confidentialité
				des données fournies.
			</p>
			<p>
				<b>Pour le personnel des institutions de l’UE uniquement:</b>
			</p>
			<p>
				Nous attirons votre attention sur la politique en matière de «protection
				des personnes physiques à l’égard du traitement des données à caractère
				personnel [...]» adoptée conformément au
				<a
						href="https://eur-lex.europa.eu/legal-content/FR/TXT/?uri=CELEX:32018R1725"
						target="_blank"
						>
					règlement (UE) 2018/1725
				</a>
				.
			</p>
			<p>
				Si des données à caractère personnel sont recueillies, une déclaration
				relative à la protection de la vie privée doit être publiée avec le
				questionnaire.
			</p>
			<p>
				Veuillez prendre contact avec le coordinateur de la protection des données
				(CPD) de votre DG afin de valider cette déclaration.
			</p>
			<p>
				En outre, toute collecte de données à caractère personnel doit être
				notifiée au délégué à la protection des données (DPD). Veuillez contacter
				votre CPD si vous avez besoin d’aide concernant cette notification.
			</p>
			<p>
				Vous trouverez ci-joint un modèle de déclaration relative à la protection
				de la vie privée, que vous pouvez utiliser pour vos enquêtes. Vous devez le
				modifier et l’adapter à vos besoins:
			</p>
			<p>
				Modèle:
				<u>
					<a
							href="https://circabc.europa.eu/ui/group/599f39d2-e0cc-4765-bfdc-c9917c931509/library/dfed4f34-fa25-42ed-af44-e1acc4f0a58f/details"
							>
						«Déclaration relative à la protection de la vie privée pour les
						enquêtes et les consultations»
					</a>
				</u>
			</p>
			<h2>
				Nous avons besoin d'un accord de traitement des données, où se trouve le DPA de EUSurvey ?
			</h2>
			<p>
				L’accord de traitement des données EUSurvey (DPA) est disponible pour toutes les entités qui sont
				considérées comme des contrôleurs de données lorsque nous traitons vos données par le biais de la
				plate-forme EUSurvey. Le DPA EUSurvey est disponible <a href = "${contextpath}/home/dpa">ici</a>.
			</p>
			<p>
				Si vous avez des questions, n'hésitez pas à
				<a href="${contextpath}/home/support?dataprotection=1">nous contacter</a>.
			</p>
			<h2>
				Quelqu’un m’a contacter pour accéder, modifier ou supprimer tout ou partie de ses données personnelles – que dois-je faire ?
			</h2>
			<p>
				<div><b>RÈGLEMENT (UE) 2018/1725</b></div>
				<div><b>Droit d’accès de la personne concernée – Article 17</b></div>
				<div><b>Droit de rectification – Article 18</b></div>
				<div><b>Droit à l’effacement («droit à l’oubli») – Article 19</b></div>
			</p>
			<p>
				C’est aux gestionnaires d'enquêtes que reviens la responsabilité de gérer les données personnelles collectées dans le cadre de l’enquête.
				En tant que responsable de l’enquête vous devez répondre aux requêtes reçues de la part des participants à votre enquête en lien avec la protection des données.
			</p>
			<p>
				Afin d’accéder, de rectifier ou de supprimer des données personnelles collectées dans le cadre de votre enquête, vous pouvez vous rendre dans l’écran de résultat et chercher les données personnelles en question grâce aux filtres disponibles.
				Vous pouvez ensuite les supprimer ou les modifier soit en éditant la contribution soit en supprimant la contribution entièrement.
			</p>
			<p>
				<figure>
					<img alt="Screenshot" style="max-width: 920px" src="${contextpath}/resources/images/documentation/personal_data_modification.png">
				</figure>
			</p>
			<p>
				Concernant les adresses emails ainsi que les noms et prénoms, les autres parties du système où ces données peuvent éventuellement être enregistrées sont les listes d’invités (onglet Participants) ou votre carnet d’adresses.
			</p>
			<h2>
				Suppression en bloc de plusieurs réponses
			</h2>
			<p>
				Vous pouvez supprimer une colonne entière des résultats de votre enquête.
				Cela aura pour effet de «vider» (c'est-à-dire de supprimer définitivement) toutes les réponses à la question correspondante.
				Cela peut être utile pour rendre les résultats anonymes, par exemple dans le contexte de la conformité GDPR.
			</p>
			<p>
				Pour supprimer des réponses en bloc :
			</p>
			<ol>
				<li>Allez dans l'onglet «Résultats».</li>
				<li>Recherchez la colonne pour laquelle vous souhaitez supprimer les réponses en bloc.</li>
				<li>Cliquez sur l'icône 3 points.</li>
				<li>Sélectionnez «Effacer les réponses».</li>
				<li>Confirmez la suppression.</li>
			</ol>
			<p>
				Toutes les réponses collectées pour la question correspondante seront définitivement supprimées.
			</p>

		</div>
	
	
		</div>
	</div>
	
	<%@ include file="../footer.jsp" %>	

</body>
</html>
