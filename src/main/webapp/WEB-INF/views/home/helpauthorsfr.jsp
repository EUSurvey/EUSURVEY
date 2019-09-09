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

	<%@ include file="../header.jsp" %>

	<a name="topAnchor"></a>

	<c:choose>
		<c:when test="${USER != null && runnermode == null }">
			<%@ include file="../menu.jsp" %>	
			<div class="page" style="margin-top: 110px">
		</c:when>
		<c:otherwise>
			<div class="page" style="margin-top: 40px">
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
		<li><a class="anchorlink head" href="#_Toc0">Questions g&eacute;n&eacute;rales</a>
			<ul>
				<li><a class="anchorlink" href="#_Toc0-1">Qu'est-ce qu'EUSurvey?</a></li>
				<li><a class="anchorlink" href="#_Toc0-2">Quand utiliser EUSurvey?</a></li>
				<li><a class="anchorlink" href="#_Toc0-3">Quelles sont les limitations d'EUSurvey?</a></li>
				<li><a class="anchorlink" href="#_Toc0-4">Quelles sont les fonctionnalit&eacute;s d'EUSurvey?</a></li>
				<li><a class="anchorlink" href="#_Toc0-5">Gestion des formulaires</a></li>
				<li><a class="anchorlink" href="#_Toc0-6">Exploitation des r&eacute;sultats</a></li>
				<li><a class="anchorlink" href="#_Toc0-7">Où trouver des informations compl&eacute;mentaires sur EUSurvey?</a></li>
				<li><a class="anchorlink" href="#_Toc0-8">Qui contacter en cas de probl&egrave;mes techniques li&eacute;s &agrave; EUSurvey?</a></li>
				<li><a class="anchorlink" href="#_Toc0-9">Qui contacter pour sugg&eacute;rer des am&eacute;liorations &agrave; EUSurvey?</a></li>
				<li><a class="anchorlink" href="#_Toc0-10">Avec quels navigateurs l'application EUSurvey est-elle compatible?</a></li>
				<li><a class="anchorlink" href="#_Toc0-11">EUSurvey: clause de non-responsabilit&eacute; (pour les utilisateurs non membres des institutions de l'UE)</a></li>
				<li><a class="anchorlink" href="#_Toc0-12">Mes participants peuvent-ils r&eacute;pondre &agrave; mon questionnaire &agrave; partir d'un appareil mobile?</a></li>
				<li><a class="anchorlink" href="#_Toc0-13">Y a-t-il une taille minimale d'écran?</a></li>
			</ul>
		</li>
		<li><a class="anchorlink head" href="#_Toc1">Connexion et enregistrement</a>
			<ul>
				<li><a class="anchorlink" href="#_Toc1-1">Je dispose d'un compte EU Login. Dois-je m'enregistrer s&eacute;par&eacute;ment pour EUSurvey?</a></li>
				<li><a class="anchorlink" href="#_Toc1-2">Comment s'enregistrer dans EUSurvey?</a></li>
				<li><a class="anchorlink" href="#_Toc1-3">Comment se connecter &agrave; EUSurvey?</a></li>
			</ul>
		</li>
		<li><a class="anchorlink head" href="#_Toc2">Cr&eacute;ation d'une enqu&ecirc;te</a>
		<ul>
				<li><a class="anchorlink " href="#_Toc2-1">Comment cr&eacute;er une nouvelle enqu&ecirc;te?</a></li>
				<li><a class="anchorlink" href="#_Toc2-2">Comment importer une enqu&ecirc;te existante depuis un ordinateur?</a></li>
				<li><a class="anchorlink" href="#_Toc2-3">Comment importer une enqu&ecirc;te existante depuis l'application IPM?</a></li>
				<li><a class="anchorlink" href="#_Toc2-4">Où se trouvent toutes les enqu&ecirc;tes que j'ai cr&eacute;&eacute;es?</a></li>
				<li><a class="anchorlink " href="#_Toc2-5">Comment ouvrir une enqu&ecirc;te existante pour la modifier, par exemple?</a></li>
				<li><a class="anchorlink" href="#_Toc2-6">Comment exporter une enqu&ecirc;te existante?</a></li>
				<li><a class="anchorlink" href="#_Toc2-7">Comment copier une enqu&ecirc;te existante?</a></li>
				<li><a class="anchorlink" href="#_Toc2-8">Comment supprimer une enqu&ecirc;te existante?</a></li>
				<li><a class="anchorlink" href="#_Toc2-9">Comment cr&eacute;er un questionnaire conforme aux normes WCAG avec EUSurvey?</a></li>
				<li><a class="anchorlink" href="#_Toc2-10">Comment cr&eacute;er un questionnaire de type quiz?</a></li>
			</ul>
		</li>
		<li><a class="anchorlink head" href="#_Toc3">Modifier une enqu&ecirc;te</a>
			<ul>
				<li><a class="anchorlink" href="#_Toc3-1">Comment lancer l'&eacute;diteur?</a></li>
				<li><a class="anchorlink" href="#_Toc3-2">Comment cr&eacute;er un questionnaire avec l'&eacute;diteur EUSurvey?</a></li>
				<li><a class="anchorlink" href="#_Toc3-3">Comment ajouter ou supprimer des questions de mon questionnaire?</a></li>
				<li><a class="anchorlink" href="#_Toc3-4">Comment modifier les &eacute;l&eacute;ments de mon questionnaire?</a></li>
				<li><a class="anchorlink" href="#_Toc3-10">Comment copier les &eacute;l&eacute;ments?</a></li>
				<li><a class="anchorlink" href="#_Toc3-11">Comment ajouter ou supprimer des r&eacute;ponses dans les questions &agrave; choix ?</a></li>
				<li><a class="anchorlink" href="#_Toc3-12">Puis-je rendre une question obligatoire?</a></li>
				<li><a class="anchorlink" href="#_Toc3-13">Comment d&eacute;placer les &eacute;l&eacute;ments dans le questionnaire?</a></li>
				<li><a class="anchorlink" href="#_Toc3-14">Comment utiliser la fonction visibilit&eacute; (d&eacute;pendances)?</a></li>
				<li><a class="anchorlink" href="#_Toc3-7">Peut-on modifier l'ordre des r&eacute;ponses aux questions simples ou &agrave; choix multiple?</a></li>
				<li><a class="anchorlink" href="#_Toc3-5">Comment autoriser d'autres utilisateurs &agrave; modifier une enqu&ecirc;te?</a></li>
				<li><a class="anchorlink" href="#_Toc3-8">Quelles langues sont compatibles avec l'application?</a></li>
				<li><a class="anchorlink" href="#_Toc3-9">Pourquoi l'UTF-8? Quelles polices de caract&egrave;res utiliser?</a></li>
				<li><a class="anchorlink" href="#_Toc3-6">Que signifie la notion de "Complexit&eacute;" ?</a></li>
			</ul>
		</li>
		<li><a class="anchorlink head" href="#_Toc4">S&eacute;curit&eacute; des enqu&ecirc;tes</a>
			<ul>
				<li><a class="anchorlink" href="#_Toc4-1">Comment restreindre l'acc&egrave;s &agrave; une enqu&ecirc;te?</a></li>
				<li><a class="anchorlink" href="#_Toc4-3">Comment d&eacute;finir un mot de passe pour une enqu&ecirc;te?</a></li>
				<li><a class="anchorlink" href="#_Toc4-4">Comment s'assurer qu'un utilisateur ne soumette pas plus qu'un nombre donn&eacute; de contributions &agrave; une enqu&ecirc;te?</a></li>
				<li><a class="anchorlink " href="#_Toc4-5">Comment &eacute;viter que des logiciels robots ne soumettent des contributions en masse &agrave; une enqu&ecirc;te?</a></li>
				<li><a class="anchorlink" href="#_Toc4-6">Est-il possible d'autoriser les participants &agrave; acc&eacute;der &agrave; leurs contributions apr&egrave;s envoi?</a></li>
			</ul>
		</li>
		<li><a class="anchorlink head" href="#_Toc5">Tester une enqu&ecirc;te</a>
			<ul>
				<li><a class="anchorlink" href="#_Toc5-1">Est-il possible d'afficher l'enqu&ecirc;te telle qu'elle apparaîtra une fois publi&eacute;e?</a></li>
				<li><a class="anchorlink" href="#_Toc5-2">Des coll&egrave;gues peuvent-ils tester l'enqu&ecirc;te avant sa publication?</a></li>
			</ul>
		</li>
		<li><a class="anchorlink head" href="#_Toc6">Traductions</a>
			<ul>
				<li><a class="anchorlink" href="#_Toc6-1">Comment traduire une enqu&ecirc;te?</a></li>
				<li><a class="anchorlink" href="#_Toc6-2">Comment ajouter une traduction existante &agrave; une enqu&ecirc;te?</a></li>
				<li><a class="anchorlink" href="#_Toc6-3">Est-il possible de modifier une traduction existante en ligne?</a></li>
				<li><a class="anchorlink" href="#_Toc6-4">Est-il possible de cr&eacute;er des traductions hors ligne?</a></li>
				<li><a class="anchorlink" href="#_Toc6-6">Comment publier/d&eacute;publier des traductions? Pourquoi est-il parfois impossible de publier une traduction? Qu'entend-on par traduction &laquo;incompl&egrave;te&raquo;?</a></li>
				<li><a class="anchorlink" href="#_Toc6-7">Est-il possible de t&eacute;l&eacute;verser des traductions dans des langues non europ&eacute;ennes?</a></li>	
				<li><a class="anchorlink" href="#_Toc6-8">Qu'entend-on par &laquo;Demander une traduction automatique&raquo;?</a></li>	
				<li><a class="anchorlink" href="#_Toc6-5">Instructions pour le personnel des institutions europ&eacute;ennes</a></li>
			</ul>
		</li>
		<li><a class="anchorlink head" href="#_Toc7">Publication d'une enqu&ecirc;te</a>
			<ul>	
				<li><a class="anchorlink" href="#_Toc7-1">Comment publier une enqu&ecirc;te?</a></li>
				<li><a class="anchorlink" href="#_Toc7-2">Est-il possible de personnaliser l'URL d'une enqu&ecirc;te?</a></li>
				<li><a class="anchorlink" href="#_Toc7-7">Puis-je envoyer un lien direct vers une traduction de mon enqu&ecirc;te?</a></li>
				<li><a class="anchorlink" href="#_Toc7-3">Comment programmer la publication d'une enqu&ecirc;te pendant un cong&eacute;?</a></li>
				<li><a class="anchorlink" href="#_Toc7-4">Est-il possible de programmer un rappel de la date de fin de l'enqu&ecirc;te?</a></li>
				<c:if test="${enablepublicsurveys}">
				<li><a class="anchorlink" href="#_Toc7-5">Comment afficher une enqu&ecirc;te dans la liste des enqu&ecirc;tes publiques dans EUSurvey?</a></li>
				</c:if>
				<li><a class="anchorlink" href="#_Toc7-6">Pour le personnel des institutions europ&eacute;ennes: quelles sont les exigences officielles pour le lancement d'une consultation publique ouverte (site web &laquo;Votre point de vue sur l'Europe&raquo;)?</a></li>
			</ul>
		</li>
		<li><a class="anchorlink head" href="#_Toc8">Gestion des enqu&ecirc;tes</a>
			<ul>
				<li><a class="anchorlink" href="#_Toc8-1">Est-il possible de corriger les erreurs d&eacute;couvertes dans une enqu&ecirc;te?</a></li>
				<li><a class="anchorlink" href="#_Toc8-2">En cas de modification de l'enqu&ecirc;te, des contributions sont-elles supprim&eacute;es?</a></li>
				<li><a class="anchorlink" href="#_Toc8-3">Comment modifier le titre d'une enqu&ecirc;te?</a></li>
				<li><a class="anchorlink" href="#_Toc8-4">Comment modifier l'adresse de contact d'une enqu&ecirc;te?</a></li>
				<li><a class="anchorlink" href="#_Toc8-5">Comment personnaliser le message de confirmation par d&eacute;faut? </a></li>
				<li><a class="anchorlink" href="#_Toc8-6">Comment personnaliser le message de sortie par d&eacute;faut? </a></li>
				<li><a class="anchorlink" href="#_Toc8-7">Fonctionnalit&eacute; d'archivage</a></li>
				<li><a class="anchorlink" href="#_Toc8-8">Comment donner acc&egrave;s &agrave; mon enqu&ecirc;te &agrave; d'autres utilisateurs?</a></li>
				<li><a class="anchorlink" href="#_Toc8-9">Que sont les journaux d'activité ?</a></li>
			</ul>
		</li>
		<li><a class="anchorlink head" href="#_Toc9">Analyse, exportation et publication des r&eacute;sultats</a>
			<ul>
				<li><a class="anchorlink" href="#_Toc9-1">Où trouver les contributions reçues des participants?</a></li>
				<li><a class="anchorlink" href="#_Toc9-2">Comment t&eacute;l&eacute;charger les contributions reçues?</a></li>
				<li><a class="anchorlink" href="#_Toc9-3">Comment rechercher et analyser un sous-ensemble d&eacute;fini de contributions?</a></li>
				<li><a class="anchorlink" href="#_Toc9-4">Comment retourner &agrave; l'ensemble des contributions apr&egrave;s avoir d&eacute;fini un sous-ensemble?</a></li>
				<li><a class="anchorlink" href="#_Toc9-5">Comment publier les r&eacute;sultats? </a></li>
				<li><a class="anchorlink" href="#_Toc9-6">Comment acc&eacute;der aux r&eacute;sultats publi&eacute;s?</a></li>
				<li><a class="anchorlink" href="#_Toc9-7">Comment autoriser d'autres utilisateurs &agrave; acc&eacute;der aux r&eacute;sultats d'une enqu&ecirc;te?</a></li>
				<li><a class="anchorlink" href="#_Toc9-8">Je ne parviens pas à décompresser mes fichiers exportés</a></li>
				<li><a class="anchorlink" href="#_Toc9-9">Les r&eacute;sultats publi&eacute;s - protection des donn&eacute;es &agrave; caract&egrave;re personnel, charg&eacute;es par les participants</a></li>
				<li><a class="anchorlink" href="#_Toc9-10">Comment concevoir une enqu&ecirc;te afin de publier les r&eacute;sultats avec ou sans donn&eacute;es personnelles?</a></li>
				<li><a class="anchorlink" href="#_Toc9-11">Pourquoi mes résultats ne sont-ils pas à jour?</a></li>
			</ul>
		</li>
		<li><a class="anchorlink head" href="#_Toc10">Styles et mise en pages</a>
			<ul>
				<li><a class="anchorlink" href="#_Toc10-1">Comment modifier l'apparence d'une enqu&ecirc;te?</a></li>
				<li><a class="anchorlink" href="#_Toc10-2">Comment cr&eacute;er son propre th&egrave;me visuel?</a></li>
				<li><a class="anchorlink" href="#_Toc10-3">Comment ajouter un logo &agrave; une enqu&ecirc;te?</a></li>
				<li><a class="anchorlink" href="#_Toc10-4">Comment ajouter des liens utiles &agrave; une enqu&ecirc;te?</a></li>
				<li><a class="anchorlink" href="#_Toc10-5">Comment ajouter des documents de r&eacute;f&eacute;rence &agrave; une enqu&ecirc;te?</a></li>
				<li><a class="anchorlink" href="#_Toc10-6">Comment cr&eacute;er une enqu&ecirc;te en plusieurs pages?</a></li>
				<li><a class="anchorlink" href="#_Toc10-7">Comment activer la num&eacute;rotation automatique pour une enqu&ecirc;te?</a></li>
				<li><a class="anchorlink" href="#_Toc10-8">Comment cr&eacute;er une apparence personnalis&eacute;e pour une enqu&ecirc;te?</a></li>
			</ul>
		</li>
		<li><a class="anchorlink head" href="#_Toc11">Gestion des contacts et des invitations</a>
			<ul>
				<li><a class="anchorlink" href="#_Toc11-1">Qu'est-ce que le &laquo;carnet d'adresses&raquo;?</a></li>
				<li><a class="anchorlink" href="#_Toc11-2">Que sont les &laquo;attributs&raquo; des contacts?</a></li>
				<li><a class="anchorlink" href="#_Toc11-3">Comment ajouter de nouveaux contacts dans le carnet d'adresses?</a></li>
				<li><a class="anchorlink" href="#_Toc11-4">Qu'est-ce qu'un &laquo;formulaire d'enregistrement&raquo;?</a></li>
				<li><a class="anchorlink" href="#_Toc11-5">Comment importer un fichier avec plusieurs contacts dans le carnet d'adresses?</a></li>
				<li><a class="anchorlink" href="#_Toc11-6">Comment modifier un attribut pour plusieurs contacts &agrave; la fois?</a></li>
				<li><a class="anchorlink" href="#_Toc11-7">Est-il possible d'exporter les contacts du carnet d'adresses dans un fichier?</a></li>
			</ul>
		</li>
		<li><a class="anchorlink head" href="#_Toc12">Invitation de participants</a>
			<ul>
				<li><a class="anchorlink" href="#_Toc12-1">Comment d&eacute;finir un groupe de participants potentiels? Qu'est-ce qu'une &laquo;liste d'invit&eacute;s&raquo;?</a></li>
				<li><a class="anchorlink" href="#_Toc12-2">Comment modifier/supprimer une liste d'invit&eacute;s existante?</a></li>
				<li><a class="anchorlink" href="#_Toc12-3">Comment envoyer un courriel d'invitation aux participants?</a></li>
			</ul>
		<li><a class="anchorlink head" href="#_Toc13">Gestion du compte personnel</a>
			<ul>
				<li><a class="anchorlink" href="#_Toc13-1">Comment modifier le mot de passe?</a></li>
				<li><a class="anchorlink" href="#_Toc13-2">Comment modifier l'adresse &eacute;lectronique?</a></li>
				<li><a class="anchorlink" href="#_Toc13-3">Comment modifier la langue par d&eacute;faut?</a></li>
			</ul>
		</li>
		<li><a class="anchorlink head" href="#_Toc14">Protection de la vie priv&eacute;e</a>
			<ul>
				<li><a class="anchorlink" href="#_Toc14-1">Ce syst&egrave;me utilise des cookies. Quelles informations y sont enregistr&eacute;es?</a></li>
				<li><a class="anchorlink" href="#_Toc14-2">Quelles informations sont enregistr&eacute;es par EUSurvey lorsqu'un participant soumet une contribution?</a></li>
				<li><a class="anchorlink" href="#_Toc14-3">Les enqu&ecirc;tes doivent-elles inclure une d&eacute;claration relative &agrave; la protection de la vie priv&eacute;e?</a></li>
			</ul>
		</li>
	
	</ul>
</div>

<h1 style="margin-top: 40px"><a class="anchor" name="_Toc0"></a>Questions g&eacute;n&eacute;rales</h1>
<h2><a class="anchor" name="_Toc0-1"></a>Qu'est-ce qu'EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>EUSurvey est un outil de gestion d'enqu&ecirc;tes en ligne qui permet de cr&eacute;er, publier et g&eacute;rer des questionnaires et d'autres formulaires interactifs, compatibles avec la plupart des logiciels de navigation.</p>
<h2><a class="anchor" name="_Toc0-2"></a>Quand utiliser EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Utilisez EUSurvey si vous souhaitez cr&eacute;er un questionnaire ou un formulaire interactif accessibles en ligne, ou pour enregistrer un grand nombre de jeux de donn&eacute;es semblables.</p>
<h2><a class="anchor" name="_Toc0-3"></a>Quelles sont les limitations d'EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>EUSurvey n'est peut-&ecirc;tre pas adapt&eacute; &agrave; votre projet dans les situations suivantes:</p>
	<ul>
		<li>si plusieurs participants doivent travailler sur la m&ecirc;me contribution avant qu'elle ne soit soumise</li>
		<li>si les contributions doivent &ecirc;tre valid&eacute;es avant de pouvoir &ecirc;tre soumises</li>
	</ul>
<p>Veuillez contacter DIGIT-EUSURVEY-SUPPORT pour obtenir plus d'informations, &ecirc;tre tenu au courant des fonctionnalit&eacute;s futures et savoir comment contourner les probl&egrave;mes rencontr&eacute;s.</p>
<h2><a class="anchor" name="_Toc0-4"></a>Quelles sont les fonctionnalit&eacute;s d'EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p><b>Personnalisation des formulaires</b><br />Facile &agrave; utiliser, l'&eacute;diteur permet de choisir diff&eacute;rents types de questions: texte simple, questions &agrave; choix multiple, tableaux, &eacute;l&eacute;ments multim&eacute;dias, etc. Vous pouvez structurer votre questionnaire au moyen d'&eacute;l&eacute;ments structurels sp&eacute;cifiques.</p>
<p><b>Questions d&eacute;pendantes</b><br />EUSurvey peut afficher des questions et champs suppl&eacute;mentaires en fonction des r&eacute;ponses du participant, ce qui rend les questionnaires interactifs.</p>
<p><b>Programmation de la publication</b><br />Vous pouvez programmer la publication et la d&eacute;publication automatiques de votre enqu&ecirc;te &agrave; la date et &agrave; l'heure que vous souhaitez.</p>
<p><b>Modification des formulaires apr&egrave;s publication</b><br />Vous pouvez modifier un formulaire publi&eacute; sans perdre aucune contribution.</p>
<p><b>Langues</b><br />L'interface utilisateur est disponible dans 23 langues de l'UE. Vous pouvez &eacute;galement traduire votre formulaire dans l'une des 136 langues couvertes par la norme ISO 639-1, de l'abkhaze au zoulou.</p>
<p><b>S&eacute;curit&eacute;</b><br />EUSurvey dispose de l'infrastructure n&eacute;cessaire pour assurer la s&eacute;curit&eacute; des formulaires en ligne.</p>
<p><b>Envoi d'invitations directement &agrave; partir de l'application</b><br />Vous pouvez s&eacute;lectionner des contacts dans le &laquo;carnet d'adresses&raquo; et leur envoyer &agrave; chacun un message contenant un lien d'acc&egrave;s individuel.</p>
<p><b>Protection des donn&eacute;es &agrave; caract&egrave;re personnel</b><br />Vous pouvez garantir le respect des donn&eacute;es &agrave; caract&egrave;re personnel des participants en cr&eacute;ant des formulaires anonymes. Dans ce cas, en tant qu'auteur, vous n'aurez pas acc&egrave;s aux param&egrave;tres de connexion des participants.</p>
<p><b>Personnalisation de l'apparence</b><br />Vous pouvez personnaliser compl&egrave;tement la mise en pages des formulaires grâce &agrave; l'&eacute;diteur de styles CSS et aux &eacute;diteurs de texte enrichi, qui permettent de configurer tous les &eacute;l&eacute;ments visibles. Vous pouvez &eacute;galement adapter le formulaire &agrave; l'identit&eacute; de votre projet au moyen d'une vari&eacute;t&eacute; de th&egrave;mes graphiques. Les questionnaires peuvent tenir sur une page ou sur plusieurs pages, &agrave; votre convenance.</p>
<p><b>Sauvegarde de brouillons</b><br />Vous pouvez permettre aux participants de sauvegarder leur contribution &agrave; l'&eacute;tat de brouillon sur le serveur, et de la terminer plus tard.</p>
<p><b>Remplissage de formulaires hors ligne</b><br />EUSurvey permet de compl&eacute;ter les formulaires hors ligne avant de les envoyer sur le serveur au moment voulu.</p>
<p><b>Num&eacute;rotation automatique</b><br />Pour structurer votre enqu&ecirc;te, EUSurvey peut num&eacute;roter les &eacute;l&eacute;ments des formulaires &agrave; votre place.</p>
<p><b>Contraste renforc&eacute;</b><br />Les personnes malvoyantes peuvent afficher une version &agrave; contraste renforc&eacute; du questionnaire. Cette version est cr&eacute;&eacute;e automatiquement pour tous les formulaires.</p>
<p><b>Ajout de fichiers compl&eacute;mentaires</b><br />Vous pouvez ajouter des fichiers &agrave; votre questionnaire en les envoyant sur le serveur. Tous les participants pourront alors t&eacute;l&eacute;charger ces fichiers.</p>

<h3><a class="anchor" name="_Toc0-5"></a>Gestion des formulaires</h3>
<p><b>Publication d'une enqu&ecirc;te</b><br />Pour accroître la visibilit&eacute; de votre enqu&ecirc;te, vous pouvez la faire publier automatiquement dans la <a href="https://ec.europa.eu${contextpath}/home/publicsurveys" target="_blank">liste des enqu&ecirc;tes publiques</a> accessibles par l'application EUSurvey de la Commission europ&eacute;enne.</p>
<p><b>Travailler ensemble</b><br />Pour les enqu&ecirc;tes g&eacute;r&eacute;es par plusieurs utilisateurs, EUSurvey permet de d&eacute;finir des droits avanc&eacute;s pour d'autres utilisateurs, afin de tester une enqu&ecirc;te ou d'analyser des r&eacute;sultats.</p>

<h3><a class="anchor" name="_Toc0-6"></a>Exploitation des r&eacute;sultats</h3>
<p><b>Analyse des r&eacute;sultats</b><br />EUSurvey propose des fonctions basiques d'analyse des r&eacute;sultats et de visualisation des donn&eacute;es sous forme d'histogrammes et de graphiques. Vous pouvez &eacute;galement exporter les r&eacute;sultats d'enqu&ecirc;tes dans des formats tabulaires standards, afin de les importer dans des logiciels statistiques.</p>
<p><b>Publication des r&eacute;sultats</b><br />L'application EUSurvey permet de publier des sous-ensembles de toutes les r&eacute;ponses soumises. Le syst&egrave;me peut calculer et cr&eacute;er automatiquement des statistiques et des graphiques.</p>
<p><b>Modification des contributions envoy&eacute;es</b><br />Vous pouvez permettre aux participants de modifier leur contribution apr&egrave;s l'avoir soumise.</p>

<h2><a class="anchor" name="_Toc0-7"></a>Où trouver des informations compl&eacute;mentaires sur EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Vous trouverez des informations pratiques en cliquant sur le lien &laquo;<a href="https://ec.europa.eu${contextpath}/home/documentation" target="_blank">Documentation</a>&raquo; dans le bandeau sup&eacute;rieur de l'application EUSurvey. Consultez la page &laquo;<a href="https://ec.europa.eu${contextpath}/home/about" target="_blank">&agrave; propos</a>&raquo; pour en savoir plus sur l'historique et le financement de l'application.</p>
<h2><a class="anchor" name="_Toc0-8"></a>Qui contacter en cas de probl&egrave;mes techniques li&eacute;s &agrave; EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p> Les membres du personnel des institutions europ&eacute;ennes doivent d&eacute;crire le probl&egrave;me aussi pr&eacute;cis&eacute;ment que possible &agrave; leur service d'aide informatique qui le signalera &agrave; DIGIT-EUSURVEY-SUPPORT.<br /><br /> Les utilisateurs externes doivent contacter le <a href="mailto:EC-CENTRAL-HELPDESK@ec.europa.eu?subject=Incident%20Creation%20Request%20for%20DIGIT%20EUSURVEY%20SUPPORT%20&amp;body=%20Dear%20Helpdesk,%0D%0DCould%20you%20please%20open%20a%20ticket%20to%20DIGIT%20EUSURVEY%20SUPPORT%20with%20the%20following%20description:" target="_blank">SERVICE D'AIDE CENTRAL</a> de la Commission.</p>
<h2><a class="anchor" name="_Toc0-9"></a>Qui contacter pour sugg&eacute;rer des am&eacute;liorations &agrave; EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Tous les commentaires et remarques sont les bienvenus. Veuillez adresser vos suggestions &agrave; votre service d'aide informatique ou au <a href="mailto:EC-CENTRAL-HELPDESK@ec.europa.eu?subject=Incident%20Creation%20Request%20for%20DIGIT%20EUSURVEY%20SUPPORT%20&amp;body=%20Dear%20Helpdesk,%0D%0DCould%20you%20please%20open%20a%20ticket%20to%20DIGIT%20EUSURVEY%20SUPPORT%20with%20the%20following%20description:" target="_blank">SERVICE D'AIDE CENTRAL</a> de la Commission, en lui demandant de les transmettre &agrave; l'&eacute;quipe technique d'EUSurvey. L'&eacute;quipe technique prendra contact avec vous dans les meilleurs d&eacute;lais afin de discuter des applications possibles de vos suggestions et de leur int&eacute;gration &eacute;ventuelle dans une future version de l'application.</p>
<h2><a class="anchor" name="_Toc0-10"></a>Avec quels navigateurs l'application EUSurvey est-elle compatible?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>EUSurvey est compatible avec les deux derni&egrave;res versions d'Internet Explorer, de Mozilla Firefox et Google Chrome.</p>
<p>L'utilisation d'autres navigateurs pourrait poser des probl&egrave;mes de compatibilit&eacute;.</p>
<h2><a class="anchor" name="_Toc0-11"></a>EUSurvey: clause de non-responsabilit&eacute; (pour les utilisateurs non membres des institutions de l'UE)<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Pour tous les questionnaires et messages d'invitation provenant d'une enqu&ecirc;te cr&eacute;&eacute;e par un utilisateur <b>qui ne travaille pas officiellement pour les institutions de l'UE</b>, la clause de non-responsabilit&eacute; suivante sera affich&eacute;e dans le questionnaire et dans les courriels associ&eacute;s:</p>
<p>Clause de non-responsabilit&eacute;<br> 
<i>La Commission europ&eacute;enne ne saurait &ecirc;tre tenue responsable du contenu des questionnaires cr&eacute;&eacute;s au moyen du service EUSurvey; celui-ci rel&egrave;ve de la responsabilit&eacute; exclusive du cr&eacute;ateur et du gestionnaire de ces questionnaires. L'utilisation du service EUSurvey n'entra&icirc;ne aucunement l'approbation ou la recommandation, par la Commission europ&eacute;enne, des points de vue exprim&eacute;s dans les questionnaires.</i></p> 
<h2><a class="anchor" name="_Toc0-12"></a>Mes participants peuvent-ils r&eacute;pondre &agrave; mon questionnaire &agrave; partir d'un appareil mobile?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Oui, EUSurvey est con&ccedil;u pour des sites web adaptatifs. Cela signifie que la page sera adapt&eacute;e &agrave; la r&eacute;solution de l'&eacute;cran utilis&eacute;. Ceci permettra &agrave; vos participants de r&eacute;pondre &agrave; partir d'un appareil mobile (t&eacute;l&eacute;phone portable ou tablette PC).</p>

<h2><a class="anchor" name="_Toc0-13"></a>Y a-t-il une taille minimale d'écran?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Les questionnaires en ligne sont entièrement réactifs et s'adaptent à la taille de votre appareil, ceci vous permet de compléter le questionnaire avec n'importe quelle taille d'écran.<br />Pour la création et la gestion de vos enquêtes, nous recommandons d'utiliser une résolution minimale de 1680x1050 pixels pour une bonne expérience utilisateur.</p>

<h1><a class="anchor" name="_Toc1"></a>Connexion et enregistrement</h1>
<h2><a class="anchor" name="_Toc1-1"></a>Je dispose d'un compte EU Login. Dois-je m'enregistrer s&eacute;par&eacute;ment pour EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>NON. Connectez-vous en cliquant sur le bouton &laquo;Identifiant&raquo; de la <a href="https://ec.europa.eu/eusurvey/home/welcome" target="_blank">page d'accueil d'EUSurvey</a>.</p>
<h2><a class="anchor" name="_Toc1-2"></a>Comment s'enregistrer dans EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Les membres du personnel des institutions europ&eacute;ennes peuvent acc&eacute;der &agrave; EUSurvey au moyen de leur compte EU Login. Les autres utilisateurs sont invit&eacute;s &agrave; cr&eacute;er un compte EU Login.</p>
<h2><a class="anchor" name="_Toc1-3"></a>Comment se connecter &agrave; EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Cliquez sur le bouton &laquo;Identifiant&raquo; de la <a href="https://ec.europa.eu/eusurvey/home/welcome" target="_blank">page d'accueil d'EUSurvey</a> pour vous authentifier aupr&egrave;s d'EU Login. Vous serez redirig&eacute; vers l'application EUSurvey. Si vous n'avez pas encore de compte EU Login, veuillez vous r&eacute;f&eacute;rer &agrave; &laquo;<a href="#_Toc1-2">Comment s'enregistrer dans EUSurvey</a>&raquo;?</p>

<h1><a class="anchor" name="_Toc2"></a>Cr&eacute;ation d'une enqu&ecirc;te</h1>
<h2><a class="anchor" name="_Toc2-1"></a>Comment cr&eacute;er une nouvelle enqu&ecirc;te?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p> Sur la page &laquo;Bienvenue&raquo; ou la page &laquo;Enqu&ecirc;tes&raquo;, cliquez sur &laquo;Cr&eacute;er une nouvelle enqu&ecirc;te maintenant&raquo;. Une fen&ecirc;tre s'ouvre alors. Apr&egrave;s avoir saisi toutes les informations obligatoires, cliquez sur &laquo;Cr&eacute;er&raquo;. L'application chargera votre nouvelle enqu&ecirc;te dans le syst&egrave;me et ouvrira automatiquement l'&eacute;diteur. Vous pouvez commencer directement &agrave; ajouter des &eacute;l&eacute;ments &agrave; votre enqu&ecirc;te.</p>
<h2><a class="anchor" name="_Toc2-2"></a>Comment importer une enqu&ecirc;te existante depuis un ordinateur?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p> Sur la page &laquo;Bienvenue&raquo; ou la page &laquo;Enqu&ecirc;tes&raquo;, cliquez sur &laquo;Importer l'enqu&ecirc;te&raquo;. Une fen&ecirc;tre s'ouvre alors. S&eacute;lectionnez un fichier d'enqu&ecirc;te sur votre ordinateur, puis cliquez sur &laquo;Importer&raquo;. Votre enqu&ecirc;te est ajout&eacute;e &agrave; EUSurvey. Remarque: vous ne pouvez importer d'enqu&ecirc;tes qu'au format zip ou avec l'extension de fichier .eus.</p>
<h2><a class="anchor" name="_Toc2-3"></a>Comment importer une enqu&ecirc;te existante depuis l'application IPM?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p> Tout d'abord, exportez votre enqu&ecirc;te depuis l'application IPM. Pour ce faire, connectez-vous &agrave; IPM et ouvrez votre questionnaire. Dans la partie gauche de la page, cliquez sur &laquo;Exporter&raquo; pour sauvegarder le questionnaire sous la forme d'un fichier zip.</p>
<p> Connectez-vous &agrave; pr&eacute;sent &agrave; EUSurvey. Sur la page &laquo;Bienvenue&raquo;, cliquez sur &laquo;Importer l'enqu&ecirc;te&raquo;. S&eacute;lectionnez l'enqu&ecirc;te &agrave; importer (le fichier zip, qui se trouve g&eacute;n&eacute;ralement dans le dossier &laquo;T&eacute;l&eacute;chargements&raquo; si vous venez de l'exporter d'IPM). Si l'importation r&eacute;ussit, vous pouvez ouvrir et utiliser le sondage dans EUSurvey.</p>
<h2><a class="anchor" name="_Toc2-4"></a>Où se trouvent toutes les enqu&ecirc;tes que j'ai cr&eacute;&eacute;es?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>La liste se trouve sur la page &laquo;Enqu&ecirc;tes&raquo;. Vous pouvez rechercher des enqu&ecirc;tes au moyen de mots-cl&eacute;s, ou rechercher, filtrer et classer les enqu&ecirc;tes &agrave; l'aide d'autres crit&egrave;res: date de cr&eacute;ation, langue, statut, etc. N'oubliez pas de cliquer sur &laquo;Rechercher&raquo; pour appliquer les nouveaux crit&egrave;res.</p>
<h2><a class="anchor" name="_Toc2-5"></a>Comment ouvrir une enqu&ecirc;te existante pour la modifier, par exemple?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Sur la page &laquo;Enqu&ecirc;tes&raquo;, cliquez sur l'icône &laquo;Ouvrir&raquo; de l'enqu&ecirc;te en question. La page &laquo;Aperçu&raquo; qui s'affiche alors pr&eacute;sente plusieurs nouveaux onglets. En cliquant sur les onglets respectifs, vous pourrez acc&eacute;der &agrave; l'&eacute;diteur, tester votre enqu&ecirc;te, acc&eacute;der aux r&eacute;sultats de l'enqu&ecirc;te, &agrave; ses traductions, &agrave; ses propri&eacute;t&eacute;s, etc.</p>
<h2><a class="anchor" name="_Toc2-6"></a>Comment exporter une enqu&ecirc;te existante?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Sur la page &laquo;Enqu&ecirc;tes&raquo;, recherchez l'enqu&ecirc;te &agrave; exporter. Vous pouvez:</p>
<p>soit cliquer sur l'icône &laquo;Exporter&raquo;;</p>
<p>soit cliquer sur l'icône &laquo;Ouvrir&raquo;, puis, sur la page &laquo;Aperçu&raquo;, cliquer sur l'icône &laquo;Exporter&raquo;.</p>
<p>Votre enqu&ecirc;te sera sauvegard&eacute;e sur votre ordinateur, ainsi que tous ses param&egrave;tres. Les fichiers des enqu&ecirc;tes EUSurvey portent l'extension &laquo;.eus&raquo;.</p>
<h2><a class="anchor" name="_Toc2-7"></a>Comment copier une enqu&ecirc;te existante?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Sur la page &laquo;Enqu&ecirc;tes&raquo;, ouvrez l'enqu&ecirc;te en question et cliquez sur l'icône &laquo;Copier&raquo;. Dans la fen&ecirc;tre qui s'ouvre, vous pouvez ajuster les param&egrave;tres n&eacute;cessaires, puis cliquer sur &laquo;Cr&eacute;er&raquo;. Votre enqu&ecirc;te sera ajout&eacute;e &agrave; la liste de la page &laquo;Enqu&ecirc;tes&raquo;. Vous pouvez commencer directement &agrave; travailler sur la nouvelle enqu&ecirc;te.</p>
<h2><a class="anchor" name="_Toc2-8"></a>Comment supprimer une enqu&ecirc;te existante?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Sur la page &laquo;Enqu&ecirc;tes&raquo;, ouvrez l'enqu&ecirc;te en question et cliquez sur l'icône &laquo;Supprimer&raquo;. Confirmez ensuite cette action. Votre enqu&ecirc;te est alors supprim&eacute;e de la liste. Attention: la suppression d'une enqu&ecirc;te effacera du syst&egrave;me EUSurvey toute trace de vos questions et des r&eacute;sultats! Cette action est irr&eacute;versible!</p>
<h2><a class="anchor" name="_Toc2-9"></a>Comment cr&eacute;er un questionnaire conforme aux normes WCAG avec EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Les lignes directrices sur l'accessibilit&eacute; des contenus web (WCAG) sont un ensemble de recommandations visant &agrave; rendre les contenus plus accessibles, principalement pour les personnes handicap&eacute;es, mais &eacute;galement pour les applications de t&eacute;l&eacute;phonie mobile.</p>
<p>Si vous souhaitez que votre enqu&ecirc;te soit compatible avec la norme WCAG, veuillez suivre les instructions pr&eacute;sent&eacute;es <a href="https://circabc.europa.eu/sd/a/ff07d724-10bd-41aa-b99b-3b0799a995e8/WCAG_tutorial.pdf" target="_blank">dans ce document</a>.</p>
<h2><a class="anchor" name="_Toc2-10"></a>Comment cr&eacute;er un questionnaire de type quiz?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Lorsque vous cr&eacute;ez un nouveau questionnaire, vous pouvez choisir entre une enqu&ecirc;te &laquo;normale&raquo; et une enqu&ecirc;te de type &laquo;quiz&raquo;.</p>
<p>Un quiz est un type particulier d'enqu&ecirc;te permettant de d&eacute;finir de &laquo;bonnes&raquo; ou &laquo;mauvaises&raquo; r&eacute;ponses et de calculer un score final pour chaque participant. De telles enqu&ecirc;tes peuvent &ecirc;tre utilis&eacute;es, par ex. comme tests de comp&eacute;tence ou examens &eacute;lectroniques.</p>
<p>Vous trouverez des informations d&eacute;taill&eacute;es dans le <a href="https://circabc.europa.eu/sd/a/400e1268-1329-413b-b873-b42e41369a07/EUSurvey_Quiz_Guide.pdf" target="_blank">manuel d&eacute;di&eacute; &agrave; la cr&eacute;ation de quiz avec EUSurvey</a>.</p>
<p>Le mode quiz contient entre autres:</p>
	<ul>
		<li>Un m&eacute;canisme de notation </li>
		<li>La v&eacute;rification des r&eacute;ponses des participants</li>
		<li>La possibilit&eacute; de fournir des commentaires &agrave; vos participants, en fonction de leurs r&eacute;ponses</li>
		<li>Analyse des r&eacute;sultats et rapport sp&eacute;cifique pour les quiz </li>
	</ul>

<h1><a class="anchor" name="_Toc3"></a>Modifier une enqu&ecirc;te</h1>
<h2><a class="anchor" name="_Toc3-1"></a>Comment lancer l'&eacute;diteur?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Ouvrez le questionnaire dans la page &laquo;Enqu&ecirc;tes&raquo;, ic&ocirc;ne &laquo;Ouvrir&raquo;. &Agrave; partir de la page &laquo;Aperçu&raquo; cliquez sur &laquo;Editeur&raquo; pour &eacute;diter votre questionnaire.</p>
<p>Veuillez sauvegarder votre travail de temps en temps.</p>
<h2><a class="anchor" name="_Toc3-2"></a>Comment cr&eacute;er un questionnaire avec l'&eacute;diteur EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>L'&eacute;diteur est utilis&eacute; pour cr&eacute;er un questionnaire. Vous pouvez l'utiliser pour ajouter des questions et d'autres &eacute;l&eacute;ments &agrave; votre questionnaire.</p>
<p>La documentation relative &agrave; l'Editeur EUSurvey (Manuel de l'&eacute;diteur) est disponible <a href="https://ec.europa.eu/eusurvey/resources/documents/Editor_Guide_FR.pdf" target="_blank">ici</a>.</p>
<p>L'&eacute;diteur est compos&eacute; de cinq diff&eacute;rentes zones:</p>
<p><b>Volet de navigation:</b><br>Le volet de navigation donne une vue structur&eacute;e du questionnaire. Tous les &eacute;l&eacute;ments sont repr&eacute;sent&eacute;s par leur libell&eacute; respectif dans l'enqu&ecirc;te. Lorsque vous s&eacute;lectionnez un &eacute;l&eacute;ment dans le volet de navigation, la zone du formulaire se positionne sur cet &eacute;l&eacute;ment, qui est mis en &eacute;vidence en bleu.</p>
<p><b>Volet de la bo&icirc;te &agrave; outils:</b><br>La boite &agrave; outils contient les diff&eacute;rents types d'&eacute;l&eacute;ments qui peuvent &ecirc;tre ajout&eacute;s au questionnaire. Vous pouvez ajouter des &eacute;l&eacute;ments en les glissant-d&eacute;posant (drag-and-drop) dans le formulaire, ou en double-cliquant dessus.</p>
<p><b>Zone du formulaire:</b><br>Tous les &eacute;l&eacute;ments sont ajout&eacute;s dans cette zone. Elle  donne un aperçu de la pr&eacute;sentation des &eacute;l&eacute;ments dans le questionnaire.</p>
<p><b>Volet des propri&eacute;t&eacute;s de l'&eacute;l&eacute;ment:</b><br>Affiche toutes les options disponibles pour les &eacute;l&eacute;ments s&eacute;lectionn&eacute;s. Vous pouvez modifier les &eacute;l&eacute;ments dans ce volet: changer le texte de la question, ajouter des messages d'aide, ou modifier tout autre param&egrave;tre de la question.</p>
<p><b>La barre d'outils:</b><br>Contient des boutons permettant d'effectuer des actions de base.</p>
<h2><a class="anchor" name="_Toc3-3"></a>Comment ajouter ou supprimer des questions de mon questionnaire?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Pour ajouter ou supprimer des &eacute;l&eacute;ments de votre questionnaire, veuillez d'abord acc&eacute;der &agrave; l'&eacute;diteur.</p>
<p>Dans l'&eacute;diteur, vous trouverez une boite &agrave; outils &agrave; gauche et la zone du formulaire au centre de la page. Les &eacute;l&eacute;ments contiennent des textes par d&eacute;faut; leur nom est affich&eacute; comme texte de la question. Vous pouvez ajouter des &eacute;l&eacute;ments (question, texte, image, etc.) en les glissant-d&eacute;posant (drag-and-drop) dans le formulaire, ou en double-cliquant dessus.</p> 
<p>Pour supprimer un &eacute;l&eacute;ment de l'enqu&ecirc;te, cliquez sur l'&eacute;l&eacute;ment pour le s&eacute;lectionner. L'&eacute;l&eacute;ment s&eacute;lectionn&eacute; apparaitra en bleu. Cliquez sur l'ic&ocirc;ne &laquo;Supprimer&raquo; disponible dans la barre d'outils, confirmez en cliquant sur OK.</p>
<p>Voir &laquo;<a href="#_Toc3-2">Comment cr&eacute;er un questionnaire avec l'&eacute;diteur EUSurvey?</a>&raquo;</p> 
<h2><a class="anchor" name="_Toc3-4"></a>Comment modifier les &eacute;l&eacute;ments de mon questionnaire?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Les &eacute;l&eacute;ments de votre questionnaire peuvent &ecirc;tre <b>s&eacute;lectionn&eacute;s dans la zone du formulaire</b> et <b>modifi&eacute;s dans le volet des propri&eacute;t&eacute;s de l'&eacute;l&eacute;ment</b>. Voir &laquo;<a href="#_Toc3-2">Comment cr&eacute;er un questionnaire avec l'&eacute;diteur EUSurvey?</a>&raquo;</p>
<p>Pour modifier un &eacute;l&eacute;ment de l'enqu&ecirc;te, cliquez sur l'&eacute;l&eacute;ment pour le s&eacute;lectionner. L'&eacute;l&eacute;ment s&eacute;lectionn&eacute; apparaitra en bleu. Vous pouvez le modifier dans le volet &laquo;Propri&eacute;t&eacute;s de l'&eacute;l&eacute;ment&raquo; (ex: changer le texte de la question, ajouter un message d'aide, ou modifier tout autre param&egrave;tre de la question).</p>
<p>Pour modifier un texte:</p>
	<ol>
		<li>Cliquez sur le texte ou sur l'ic&ocirc;ne en forme de crayon.</li>
		<li>Modifiez le texte.</li>
		<li>Cliquez sur &laquo;Appliquer&raquo; pour afficher les modifications dans la zone du formulaire.</li>
	</ol>
<p>Par d&eacute;faut, le volet affiche uniquement les options de base. Pour afficher plus d'options, cliquez sur &laquo;Avanc&eacute;&raquo;.</p>
<p>Pour les matrices, les tableaux, et les questions &agrave; choix, vous pouvez &eacute;galement s&eacute;lectionner une question, une r&eacute;ponse, une ligne ou une colonne de l'&eacute;l&eacute;ment en cliquant sur le libell&eacute; correspondant. Vous pouvez par exemple rendre une question de la matrice ou tableau obligatoire.</p>
<h2><a class="anchor" name="_Toc3-10"></a>Comment copier les &eacute;l&eacute;ments?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Pour copier des &eacute;l&eacute;ments de votre questionnaire, veuillez d'abord acc&eacute;der &agrave; l'&eacute;diteur.</p>
<p>Tous les &eacute;l&eacute;ments qui ont &eacute;t&eacute; copi&eacute;s ou coup&eacute;s sont symbolis&eacute;s par un espace r&eacute;serv&eacute; dans la partie sup&eacute;rieure de la bo&icirc;te &agrave; outils. Vous pouvez les ajouter au questionnaire ou les replacer en les glissant-d&eacute;posant (drag-and-drop). Vous pouvez &eacute;galement cliquer sur le bouton situ&eacute; &agrave; c&ocirc;t&eacute; de l'&eacute;l&eacute;ment pour annuler cette op&eacute;ration.</p>
	<ol>
		<li>S&eacute;lectionnez un ou plusieurs &eacute;l&eacute;ments.</li>
		<li>Cliquez sur Copier.</li>
		<li>Glissez-d&eacute;posez (drag-and-drop) l'espace r&eacute;serv&eacute; de la bo&icirc;te &agrave; outils vers la zone du formulaire, ou s&eacute;lectionnez l'&eacute;l&eacute;ment dans la zone du formulaire et cliquez sur &laquo;Coller apr&egrave;s&raquo;.</li>
	</ol>
<p>Voir &laquo;<a href="#_Toc3-2">Comment cr&eacute;er un questionnaire avec l'&eacute;diteur EUSurvey?</a>&raquo;</p>
<h2><a class="anchor" name="_Toc3-11"></a>Comment ajouter ou supprimer des r&eacute;ponses dans les questions &agrave; choix?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Vous pouvez ajouter ou supprimer des r&eacute;ponses en cliquant sur le bouton +/- dans le volet des propri&eacute;t&eacute;s de l'&eacute;l&eacute;ment. Modifiez  les r&eacute;ponses en cliquant sur l'ic&ocirc;ne en forme de crayon situ&eacute;e &agrave; c&ocirc;t&eacute; de &laquo;R&eacute;ponses possibles&raquo;. Vous pouvez les modifier dans l'&eacute;diteur de texte enrichi.</p>
<p>Voir aussi  &laquo;<a href="#_Toc3-2">Comment cr&eacute;er un questionnaire avec l'&eacute;diteur EUSurvey?</a>&raquo;</p>
<h2><a class="anchor" name="_Toc3-12"></a>Puis-je rendre une question obligatoire?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>S&eacute;lectionnez la question, puis cochez la case &laquo;Obligatoire&raquo; dans le volet des propri&eacute;t&eacute;s de l'&eacute;l&eacute;ment.</p>
<p>Les questions obligatoires seront indiqu&eacute;es par un ast&eacute;risque rouge figurant &agrave; gauche du texte de la question.</p>
<h2><a class="anchor" name="_Toc3-13"></a>Comment d&eacute;placer les &eacute;l&eacute;ments dans le questionnaire?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Vous pouvez modifier la position d'un &eacute;l&eacute;ment dans votre questionnaire de plusieurs mani&egrave;res:</p>
<p>Glisser-d&eacute;poser (Drag-and-drop):<br> S&eacute;lectionnez l'&eacute;l&eacute;ment dans la zone du formulaire et glissez-le &agrave; l'emplacement souhait&eacute; dans le questionnaire.</p>
<p>Boutons de d&eacute;placement:<br> S&eacute;lectionnez l'&eacute;l&eacute;ment &agrave; d&eacute;placer et cliquez sur les boutons en forme de fl&egrave;che pour le faire monter ou descendre.</p>
<p>Couper-coller:<br>Coupez l'&eacute;l&eacute;ment &agrave; d&eacute;placer et utilisez la fonction glisser-d&eacute;poser (drag-and-drop) pour d&eacute;placer l'espace r&eacute;serv&eacute; &agrave; l'emplacement souhait&eacute;.</p>
<h2><a class="anchor" name="_Toc3-14"></a>Comment utiliser la fonction visibilit&eacute; (d&eacute;pendances)?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Cette fonction permet d'afficher ou de masquer des &eacute;l&eacute;ments en fonction des r&eacute;ponses donn&eacute;es aux questions &agrave; choix unique, multiple ou &agrave; des matrices. (voir aussi &laquo;<a href="#_Toc3-2">Comment cr&eacute;er un questionnaire avec l'&eacute;diteur EUSurvey?</a>&raquo;.</p>
<p>Par d&eacute;faut, tous les &eacute;l&eacute;ments sont param&eacute;tr&eacute;s pour &ecirc;tre toujours visibles.</p>
<p>Pour utiliser la fonction visibilit&eacute;:</p>
	<ol>
		<li>Ajoutez une question &agrave; choix unique, multiple ou une matrice &agrave; votre questionnaire.</li>
		<li>Ajoutez d'autres &eacute;l&eacute;ments &agrave; votre questionnaire.</li>
		<li>S&eacute;lectionnez  un &eacute;l&eacute;ment qui suit une question &agrave; choix unique, multiple ou une matrice qui n'appara&icirc;tra que si une r&eacute;ponse sp&eacute;cifique est coch&eacute;e.</li>
		<li>Cliquez sur l'ic&ocirc;ne en forme de crayon pour modifier les param&egrave;tres de visibilit&eacute;. Toutes les questions &agrave; choix unique, multiple et  matrice qui figurent au-dessus de l'&eacute;l&eacute;ment ou des &eacute;l&eacute;ments s&eacute;lectionn&eacute;(s) sont affich&eacute;es, avec le texte des questions et les r&eacute;ponses possibles.</li>
		<li>S&eacute;lectionnez la r&eacute;ponse dont la s&eacute;lection entra&icirc;nera l'affichage de l'&eacute;l&eacute;ment s&eacute;lectionn&eacute;.</li>
		<li>Cliquez sur &laquo;Appliquer&raquo; pour confirmer les param&egrave;tres de visibilit&eacute;.</li>
	</ol>
<p>Si vous avez s&eacute;lectionn&eacute; plusieurs &eacute;l&eacute;ments, vous pouvez modifier leurs param&egrave;tres de visibilit&eacute; en une seule fois.</p>
<p><b>Remarque:</b> Tous les &eacute;l&eacute;ments resteront visibles dans l'&eacute;diteur. La visibilit&eacute; fonctionne dans la page test ou lors de la publication du questionnaire.</p>
<p>Une fois activ&eacute;s, les param&egrave;tres de visibilit&eacute; sont indiqu&eacute;s dans la zone du formulaire par des fl&egrave;ches situ&eacute;es &agrave; c&ocirc;t&eacute; des &eacute;l&eacute;ments li&eacute;s. Les r&eacute;ponses qui d&eacute;clenchent un &eacute;l&eacute;ment sont indiqu&eacute;es par une fl&egrave;che pointant vers le bas. Les &eacute;l&eacute;ments qui sont d&eacute;clench&eacute;s par une r&eacute;ponse sont indiqu&eacute;s par une fl&egrave;che pointant vers le haut.</p>
<p>Lorsque vous pointez la souris sur les fl&egrave;ches ou les identifiants dans le volet des propri&eacute;t&eacute;s de l'&eacute;l&eacute;ment, les &eacute;l&eacute;ments li&eacute;s sont mis en &eacute;vidence dans la zone du formulaire et dans le volet de navigation.</p>
<p>Les &eacute;l&eacute;ments dont les param&egrave;tres de visibilit&eacute;s ont &eacute;t&eacute; modifi&eacute;s ne s'affichent que si au moins une r&eacute;ponse configur&eacute;e a &eacute;t&eacute; s&eacute;lectionn&eacute;e.</p>

<h2><a class="anchor" name="_Toc3-7"></a>Peut-on modifier l'ordre des r&eacute;ponses aux questions simples ou &agrave; choix multiple?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Lors de la création d'une question simple ou &agrave; choix multiple, vous pouvez programmer l'affichage des réponses de trois façons différentes:</p>
	<ul>
		<li>Ordre original</li>
		<li>Ordre alphabétique</li>
		<li>Ordre aléatoire</li>
	</ul>
<p>Ordre original: cette option affiche les réponses dans l'ordre dans lequel vous les avez saisies.</p>
<p>Ordre alphabétique: sélectionnez cette option si vous voulez que les réponses s'affichent dans l'ordre alphabétique.</p>
<p>Ordre aléatoire: sélectionnez cette option si vous voulez que les réponses s'affichent dans un ordre aléatoire.</p>
<h2><a class="anchor" name="_Toc3-5"></a>Comment autoriser d'autres utilisateurs &agrave; modifier une enqu&ecirc;te?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p> Ouvrez votre enqu&ecirc;te et ouvrez la page &laquo;Droits&raquo;. Cliquez sur &laquo;Ajouter un utilisateur&raquo; ou &laquo;Ajouter un service&raquo;. L'assistant d'ajout d'utilisateurs s'ouvre alors. Vous pouvez leur donner des droits d'acc&egrave;s sp&eacute;cifiques. Cliquez simplement sur la couleur pour modifier les droits.</p>
	<ul>
		<li>Vert: 	acc&egrave;s en lecture et &eacute;criture</li>
		<li>Jaune: 	acc&egrave;s en lecture</li>
		<li>Rouge: 	pas d'acc&egrave;s</li>
	</ul>
<p>Les utilisateurs ajout&eacute;s verront automatiquement votre enqu&ecirc;te apparaître dans leur liste lors de leur prochaine connexion &agrave; EUSurvey. Pour en savoir plus, voir &laquo;<a href="#_Toc8-8">Comment donner acc&egrave;s &agrave; mon enqu&ecirc;te &agrave; d'autres utilisateurs?</a>&raquo;</p>
<h2><a class="anchor" name="_Toc3-8"></a>Quelles langues sont compatibles avec l'application?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Vous pouvez créer une enqu&ecirc;te dans toute langue encodable au format «UTF-8 &agrave; trois octets».</p>
<h2><a class="anchor" name="_Toc3-9"></a>Pourquoi l'UTF-8? Quelles polices de caractères utiliser?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>N'oubliez pas que les participants pourront afficher l'enqu&ecirc;te sans problèmes si la police choisie est installée dans leur logiciel de navigation. L'UTF-8 est la norme d'encodage la plus courante pour les pages HTML.<br> Si vous choisissez une police non compatible, vous risquez de rencontrer des problèmes pour l'exportation au format PDF.</p> 
<p>Nous recommandons ces jeux de caractères compatibles:</p>
	<ul>
		<li>Freesans <a href="https://circabc.europa.eu/d/a/workspace/SpacesStore/602784e1-bb06-4b0d-a474-eae77dbe2d11/EUSurvey-SupportedCharacterSet(freesans).txt" target="_blank">(https://circabc.europa.eu/d/a/workspace/SpacesStore/602784e1-bb06-4b0d-a474-eae77dbe2d11/EUSurvey-SupportedCharacterSet(freesans).txt)</a></li>
		<li>Freemono <a href="https://circabc.europa.eu/d/a/workspace/SpacesStore/dfc640e9-56ac-4d25-8361-4b07dbbd0579/EUSurvey-SupportedCharacterSet(freemono).txt" target="_blank">(https://circabc.europa.eu/d/a/workspace/SpacesStore/dfc640e9-56ac-4d25-8361-4b07dbbd0579/EUSurvey-SupportedCharacterSet(freemono).txt)</a></li>
		<li>Freeserif <a href="https://circabc.europa.eu/d/a/workspace/SpacesStore/5b98b11a-f306-4d97-aab3-ec1c7a24965f/EUSurvey-SupportedCharacterSet(freeserif).txt" target="_blank">(https://circabc.europa.eu/d/a/workspace/SpacesStore/5b98b11a-f306-4d97-aab3-ec1c7a24965f/EUSurvey-SupportedCharacterSet(freeserif).txt)</a></li>
		<li>Jeu de caractères couramment compatible <a href="https://circabc.europa.eu/d/a/workspace/SpacesStore/621396c0-92d3-49a3-acd0-546b0c1a170b/EUSurvey-SupportedCharacterSet(common).txt" target="_blank">(https://circabc.europa.eu/d/a/workspace/SpacesStore/621396c0-92d3-49a3-acd0-546b0c1a170b/EUSurvey-SupportedCharacterSet(common).txt)</a></li>
	</ul>
<p><b>"Freesans" est la police utilisée par défaut.</b></p>
<p>En cas de doute, exportez votre enqu&ecirc;te en PDF pour vérifier si elle s'affiche correctement dans ce format. Il est possible que certaines contributions ne s'affichent pas correctement en PDF, car les participants peuvent librement choisir toute police de caractères supportée par l'application. M&ecirc;me si l'application ne parvient pas &agrave; afficher correctement les caractères qu'ils ont utilisés, ceux-ci sont bien enregistrés dans la base de données EUSurvey. Ces contributions peuvent donc être exportées depuis la page des résultats.</p>
<h2><a class="anchor" name="_Toc3-6"></a>Que signifie la notion de "Complexité" ?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Ajouter trop d'éléments ou trop de dépendances &agrave; une enquête risque d'affecter les performances du système pour vos participants.</p>
<p>Un niveau de compléxité critique peut être dû &agrave;:</p>
	<ul>
		<li>Trop de tables/matrices</li>
		<li>Trop de dépendances</li>
		<li>Trop de dépendances imbriquées</li>
	</ul>
<p>Pour plus d'informations, suivez ce lien <a href="https://circabc.europa.eu/sd/d/281e626e-279e-45df-8581-1f6e04feff51/BestPractices-EUSurvey.pdf" target="_blank">best practices</a>.</p>

<h1><a class="anchor" name="_Toc4"></a>S&eacute;curit&eacute; des enqu&ecirc;tes</h1>
<h2><a class="anchor" name="_Toc4-1"></a>Comment restreindre l'acc&egrave;s &agrave; une enqu&ecirc;te?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Par d&eacute;faut, les formulaires en ligne EUSurvey sont accessibles publiquement d&egrave;s leur publication. Si vous le souhaitez, vous pouvez n'autoriser l'acc&egrave;s &agrave; l'enqu&ecirc;te qu'&agrave; des utilisateurs privil&eacute;gi&eacute;s en s&eacute;lectionnant &laquo;<b>S&eacute;curis&eacute;</b>&raquo; dans les &laquo;Param&egrave;tres de s&eacute;curit&eacute;&raquo; de la section &laquo;Propri&eacute;t&eacute;s&raquo;. Vous pouvez par apr&egrave;s donner l'acc&egrave;s aux participants de trois façons:</p>
	<ul>
		<li>soit en envoyant des courriels d'invitation aux invit&eacute;s avec EUSurvey. Consultez la section suivante: &laquo;<a href="#_Toc12">Invitation de participants</a>&raquo;. Chaque participant recevra un lien d'acc&egrave;s unique.</li>
		<li>soit en s&eacute;curisant votre enqu&ecirc;te avec EU Login. Dans la section &laquo;Propri&eacute;t&eacute;s&raquo;, &eacute;ditez &laquo;S&eacute;curis&eacute;&raquo; dans les &laquo;Param&egrave;tres de s&eacute;curit&eacute;&raquo; et s&eacute;lectionnez &laquo;Enable EU Login&raquo;. Si vous &ecirc;tes un membre interne d'un organe de l'UE, vous pouvez autoriser l'acc&egrave;s &agrave; votre questionnaire &agrave; tous les utilisateurs disposants d'un compte EU Login (comptes d'un membre d'un organe de l'UE ainsi qu'externes), ou donner acc&egrave;s aux comptes des membres d'un organe de l'UE uniquement.</li>
		<li>soit en d&eacute;finissant un mot de passe g&eacute;n&eacute;ral. Ce mot de passe sera identique pour tous les participants et devra leur &ecirc;tre communiqu&eacute;. En pratique, vous envoyez le lien du l'emplacement de l'enqu&ecirc;te et le mot de passe g&eacute;n&eacute;ral. Voir &laquo;<a href="#_Toc4-3">Comment d&eacute;finir un mot de passe pour une enqu&ecirc;te?</a>&raquo;.</li>
	</ul>		
<h2><a class="anchor" name="_Toc4-3"></a>Comment d&eacute;finir un mot de passe pour une enqu&ecirc;te?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Pour prot&eacute;ger votre enqu&ecirc;te par un mot de passe, modifiez les &laquo;Param&egrave;tres de s&eacute;curit&eacute;&raquo; de la section &laquo;Propri&eacute;t&eacute;s&raquo;. Pour inviter des personnes donn&eacute;es &agrave; acc&eacute;der &agrave; votre enqu&ecirc;te s&eacute;curis&eacute;e, voir &laquo;<a href="#_Toc12">Inviter des participants</a>&raquo;.</p>
<h2><a class="anchor" name="_Toc4-4"></a>Comment s'assurer qu'un utilisateur ne soumette pas plus qu'un nombre donn&eacute; de contributions &agrave; une enqu&ecirc;te?</a><a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Une fois que vous aurez envoy&eacute; les liens d'acc&egrave;s individuel &agrave; vos participants, le syst&egrave;me pourra identifier chacun d'entre eux.</p>
<h2><a class="anchor" name="_Toc4-5"></a>Comment &eacute;viter que des logiciels robots ne soumettent des contributions en masse &agrave; une enqu&ecirc;te?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Des scripts automatis&eacute;s pourraient fausser les r&eacute;sultats d'une enqu&ecirc;te en ligne en soumettant un grand nombre de contributions. Pour &eacute;viter cela, EUSurvey dispose d'une fonction demandant aux participants de saisir les caract&egrave;res d'un <a href="http://fr.wikipedia.org/wiki/CAPTCHA" target="_blank">CAPTCHA</a> (code de s&eacute;curit&eacute; visuel) pour soumettre leur contribution.</p>
<h2><a class="anchor" name="_Toc4-6"></a>Est-il possible d'autoriser les participants &agrave; acc&eacute;der &agrave; leurs contributions apr&egrave;s envoi?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Oui, dans les param&egrave;tres de s&eacute;curit&eacute; de la section &laquo;Propri&eacute;t&eacute;s&raquo;. Les participants auront besoin du num&eacute;ro de r&eacute;f&eacute;rence de leur contribution, qui s'affiche apr&egrave;s la soumission de celle-ci. Pour modifier une contribution apr&egrave;s coup, les participants doivent ouvrir la page d'accueil d'EUSurvey: <a href="https://ec.europa.eu/eusurvey" target="_blank">https://ec.europa.eu/eusurvey</a>. Sous le bouton «Inscrivez-vous dès maintenant!», un lien permet <a href="${contextpath}/home/editcontribution" target="_blank">d'acc&eacute;der aux diff&eacute;rentes contributions.</a> Sur cette page, les participants doivent indiquer leur num&eacute;ro de r&eacute;f&eacute;rence pour que le syst&egrave;me affiche leur contribution. Ils peuvent ainsi modifier leur contribution apr&egrave;s qu'elle a &eacute;t&eacute; soumise.</p>

<h1><a class="anchor" name="_Toc5"></a>Tester une enqu&ecirc;te</h1>
<h2><a class="anchor" name="_Toc5-1"></a>Est-il possible d'afficher l'enqu&ecirc;te telle qu'elle apparaîtra une fois publi&eacute;e?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Oui: ouvrez l'enqu&ecirc;te dans EUSurvey et cliquez sur &laquo;Test&raquo;. Vous verrez le brouillon de votre enqu&ecirc;te et pourrez acc&eacute;der &agrave; tous les &eacute;l&eacute;ments du formulaire tel que publi&eacute;. Vous pouvez enregistrer le test &agrave; l'&eacute;tat de brouillon, ou le soumettre directement en tant que contribution.</p>
<h2><a class="anchor" name="_Toc5-2"></a>Des coll&egrave;gues peuvent-ils tester l'enqu&ecirc;te avant sa publication?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Vous pouvez rendre la page de test de votre enqu&ecirc;te accessible &agrave; vos coll&egrave;gues. Pour donner acc&egrave;s &agrave; cette page, ouvrez votre enqu&ecirc;te dans EUSurvey, cliquez sur l'onglet &laquo;Droits&raquo; et cliquez sur &laquo;Ajouter un utilisateur&raquo; ou &laquo;Ajouter un service&raquo;. L'assistant qui s'ouvre alors permet d'ajouter vos coll&egrave;gues. Pour leur donner les droits d'acc&egrave;s de test, s&eacute;lectionnez la couleur verte pour la fonction &laquo;Acc&egrave;s aperçu formulaire&raquo;. Cliquez simplement sur la couleur pour modifier les droits. Pour en savoir plus, voir &laquo;<a href="#_Toc8-8">Comment donner acc&egrave;s &agrave; mon enqu&ecirc;te &agrave; d'autres utilisateurs?</a>&raquo;</p>

<h1><a class="anchor" name="_Toc6"></a>Traductions</h1>
<h2><a class="anchor" name="_Toc6-1"></a>Comment traduire une enqu&ecirc;te?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>EUSurvey propose diff&eacute;rentes mani&egrave;res de rendre l'enqu&ecirc;te disponible en plusieurs langues. Important: terminez de modifier et de tester votre enqu&ecirc;te avant d'entamer sa traduction!</p>
<p>Ouvrez votre enqu&ecirc;te et allez &agrave; la page &laquo;Traductions&raquo;. Cliquez sur &laquo;Ajouter une nouvelle traduction&raquo; et s&eacute;lectionnez la langue dans la liste des langues accept&eacute;es. Si la langue souhait&eacute;e n'apparaît pas dans la liste, s&eacute;lectionnez &laquo;autre&raquo; et indiquez les deux lettres identifiant cette langue conform&eacute;ment &agrave; la norme ISO 639-1. Cliquez sur &laquo;OK&raquo; pour ajouter un formulaire de traduction vierge &agrave; votre enqu&ecirc;te. Voyez &laquo;<a href="#_Toc6-3">Est-il possible de modifier une traduction existante en ligne?</a>&raquo; pour savoir comment ajouter du texte &agrave; la traduction que vous venez de cr&eacute;er.</p>
<p>N'oubliez pas de cocher la case &laquo;Publier&raquo; si la traduction doit &ecirc;tre publi&eacute;e en m&ecirc;me temps que votre enqu&ecirc;te. Les langues ajout&eacute;es pour publication pourront &ecirc;tre choisies par les participants &agrave; partir du lien vers l'enqu&ecirc;te.</p>
<h2><a class="anchor" name="_Toc6-2"></a>Comment ajouter une traduction existante &agrave; une enqu&ecirc;te?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Ouvrez votre enqu&ecirc;te et allez &agrave; la page &laquo;Traductions&raquo;. Cliquez sur le bouton &laquo;T&eacute;l&eacute;verser la traduction existante&raquo;. L'assistant qui s'ouvre alors permet d'envoyer le fichier de traduction.</p>
<h2><a class="anchor" name="_Toc6-3"></a>Est-il possible de modifier une traduction existante en ligne?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Oui: ouvrez votre enqu&ecirc;te, allez &agrave; la page &laquo;Traductions&raquo; et s&eacute;lectionnez une ou plusieurs traductions &agrave; modifier. S&eacute;lectionnez &laquo;Modifier les traductions&raquo; dans le menu des actions, sous la liste des traductions disponibles, et cliquez sur le bouton &laquo;Aller&raquo;. L'&eacute;diteur de traductions en ligne s'ouvre alors. Il permet de modifier plusieurs traductions &agrave; la fois. N'oubliez pas de cliquer sur le bouton &laquo;Enregistrer&raquo; pour veiller &agrave; ce que votre travail soit sauvegard&eacute; dans le syst&egrave;me.</p>
<h2><a class="anchor" name="_Toc6-4"></a>Est-il possible de cr&eacute;er des traductions hors ligne?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Oui: ouvrez votre enqu&ecirc;te et allez &agrave; la page &laquo;Traductions&raquo;. De l&agrave;, vous pouvez exporter votre enqu&ecirc;te au format XLS, ODS ou XML, afin d'effectuer la traduction hors ligne. La traduction pourra ensuite &ecirc;tre import&eacute;e dans l'enqu&ecirc;te.</p>
<p>La proc&eacute;dure courante est d'exporter une version linguistique avec le statut &laquo;Compl&egrave;te&raquo;, puis de traduire tous les &eacute;l&eacute;ments textuels dans la nouvelle langue. N'oubliez pas d'indiquer le code de cette langue au d&eacute;but du formulaire, afin que le syst&egrave;me puisse reconnaître la langue de la traduction. Une fois l'enqu&ecirc;te traduite hors ligne, cliquez sur &laquo;T&eacute;l&eacute;verser la traduction existante&raquo; pour l'ajouter dans le syst&egrave;me. Pour &eacute;viter d'&eacute;craser une traduction accidentellement, vous devrez pr&eacute;ciser la version linguistique du fichier que vous allez t&eacute;l&eacute;verser. Pour des raisons de s&eacute;curit&eacute;, vous pouvez &eacute;galement s&eacute;lectionner individuellement des &eacute;l&eacute;ments textuels &agrave; remplacer, si vous ne voulez pas que tous les &eacute;l&eacute;ments soient pris en compte.</p>
<h2><a class="anchor" name="_Toc6-6"></a>Comment publier/d&eacute;publier des traductions? Pourquoi est-il parfois impossible de publier une traduction? Qu'entend-on par traduction &laquo;incompl&egrave;te&raquo;?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Pour publier une enqu&ecirc;te dans plusieurs langues, ouvrez votre enqu&ecirc;te, allez &agrave; la page &laquo;Traductions&raquo; et cochez ou d&eacute;cochez les traductions que vous souhaitez publier (ou d&eacute;publier), dans la section &laquo;Publier&raquo;. Passez ensuite &agrave; la page &laquo;Aperçu&raquo; de l'enqu&ecirc;te, où vous pourrez la publier. Si l'enqu&ecirc;te avait &eacute;t&eacute; publi&eacute;e avant l'ajout ou la modification des traductions, cliquez sur &laquo;Appliquer les modifications&raquo;.</p>
<p>Pour &eacute;viter la publication de traductions dont tout le texte n'aurait pas &eacute;t&eacute; traduit, il n'est pas possible de publier des traductions comportant des &eacute;l&eacute;ments vides - des traductions qui ne sont pas &laquo;compl&egrave;tes&raquo;. V&eacute;rifiez que votre traduction ne comporte aucun &eacute;l&eacute;ment vide au moyen de l'&eacute;diteur de traduction en ligne. Ces &eacute;l&eacute;ments se distinguent par leur fond rouge.</p>
<h2><a class="anchor" name="_Toc6-7"></a>Est-il possible de t&eacute;l&eacute;verser des traductions dans des langues non europ&eacute;ennes?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>L'application est &eacute;galement compatible avec d'autres langues que les langues officielles de l'UE. Lors de l'envoi du fichier comportant la traduction, indiquez les deux lettres identifiant cette langue conform&eacute;ment &agrave; la norme ISO 639-1.</p>
<h2><a class="anchor" name="_Toc6-8"></a>Qu'entend-on par &laquo;Demander une traduction automatique&raquo;?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>EUSurvey peut fournir des <b>traductions automatiques</b> de votre questionnaire. À cette fin, l'application utilise le service MT@EC, fourni par la Commission europ&eacute;enne.</p>
<p>À partir de la page &laquo;Traductions&raquo;, il existe plusieurs façons de demander des traductions automatiques:</p>
	<ul>
		<li>Pour ajouter une nouvelle traduction, cochez la case &laquo;Demander une traduction&raquo; (pour une traduction depuis la langue pivot de votre enqu&ecirc;te)</li>
		<li>Cliquez sur le bouton &laquo;Demander une traduction&raquo; dans la colonne &laquo;Action&raquo; (pour une traduction depuis la langue pivot de votre enqu&ecirc;te)</li>
		<li>Sélectionnez toutes les langues vers lesquelles vous souhaitez faire traduire votre enqu&ecirc;te (s&eacute;lectionnez &eacute;galement au moins une version linguistique). S&eacute;lectionnez ensuite &laquo;Demander une traduction&raquo; dans la zone de s&eacute;lection en dessous de vos traductions et cliquez sur &laquo;OK&raquo;</li>
	</ul>
<p>Le statut des traductions sera &laquo;Demandé&raquo; jusqu'à ce qu'elles soient termin&eacute;es. Pour savoir si ce statut a chang&eacute;, consultez la page &laquo;Traductions&raquo;.</p> 
<p>Les traductions automatiques se comporteront comme les autres traductions que vous avez ajout&eacute;es manuellement, c'est-&agrave;-dire qu'elles ne seront pas publi&eacute;es automatiquement, et le fait d'ajouter de nouveaux &eacute;l&eacute;ments &agrave; votre enqu&ecirc;te les rendra incompl&egrave;tes (pour les compl&eacute;ter, vous devrez demander une nouvelle traduction).
<p><i>Nous ne pouvons garantir ni la qualit&eacute; du texte produit, ni le d&eacute;lai de livraison des traductions.</i></p>
<p><a href="https://mtatec.ec.testa.eu/mtatec/html/help_en.htm" target="_blank">Aide pour la traduction automatique</a> (uniquement pour le personnel des institutions de l'UE).</p>
<h2><a class="anchor" name="_Toc6-5"></a>Instructions pour le personnel des institutions europ&eacute;ennes<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Nous vous recommandons de contacter la DGT avant de finaliser votre enqu&ecirc;te. Leurs services de la r&eacute;vision linguistique (email: DGT-EDIT) peuvent vous aider &agrave; v&eacute;rifier la clart&eacute; et l'attrait du texte de votre enqu&ecirc;te. Pour en savoir plus, voyez le site de la <a href="https://myintracomm.ec.europa.eu/serv/fr/dgt/Pages/index.aspx" target="_blank">DGT sur MyIntraComm</a>.</p>
<p>Les utilisateurs de la Commission europ&eacute;enne peuvent demander &agrave; la DG Traduction (DGT) de traduire leurs enqu&ecirc;tes dans les langues officielles de l'UE. L'enqu&ecirc;te doit &ecirc;tre export&eacute;e au format XML et envoy&eacute;e &agrave; la DGT via l'application Poetry, en s&eacute;lectionnant le code de leur DG. Le texte de l'enqu&ecirc;te ne doit pas d&eacute;passer 15 000 caract&egrave;res, espaces non compris, selon la fonction de comptage de MS Word.</p>

<h1><a class="anchor" name="_Toc7"></a>Publication d'une enqu&ecirc;te</h1>
<h2><a class="anchor" name="_Toc7-1"></a>Comment publier une enqu&ecirc;te?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Pour publier une enqu&ecirc;te &agrave; partir d'un brouillon en cours, ouvrez la page &laquo;Aperçu&raquo; et cliquez sur &laquo;Publier&raquo;. Apr&egrave;s confirmation, le syst&egrave;me cr&eacute;e automatiquement une copie fonctionnelle de votre enqu&ecirc;te et la met en ligne, ainsi que toutes les traductions s&eacute;lectionn&eacute;es pour publication sur la page &laquo;Traductions&raquo; (voir &laquo;<a  href="#_Toc6-6">Comment publier/d&eacute;publier des traductions?</a>&raquo;). Le lien vers votre enqu&ecirc;te publi&eacute;e se trouve &agrave; la rubrique &laquo;Emplacement de l'enqu&ecirc;te&raquo; de la page &laquo;Aperçu&raquo;.</p>
<p>Pour d&eacute;publier l'enqu&ecirc;te, cliquez simplement sur le bouton &laquo;D&eacute;publier&raquo;. Vous pourrez toujours acc&eacute;der &agrave; l'enqu&ecirc;te d&eacute;publi&eacute;e, dans la forme sous laquelle elle &eacute;tait publi&eacute;e, ainsi qu'&agrave; votre brouillon en cours. Cela signifie que l'enqu&ecirc;te d&eacute;publi&eacute;e ne sera pas automatiquement remplac&eacute;e par votre brouillon en cours, mais que vous pourrez la republier telle quelle si n&eacute;cessaire.</p>
<h2><a class="anchor" name="_Toc7-2"></a>Est-il possible de personnaliser l'URL d'une enqu&ecirc;te?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Oui: en modifiant l'&laquo;alias&raquo; de votre enqu&ecirc;te, vous pourrez disposer d'une adresse URL plus compr&eacute;hensible. Ouvrez votre enqu&ecirc;te et allez &agrave; la page &laquo;Propri&eacute;t&eacute;s&raquo;. Cliquez sur le bouton &laquo;Modifier&raquo; dans la rubrique &laquo;Param&egrave;tres de base&raquo;, puis modifiez l'alias de l'enqu&ecirc;te. Les alias ne peuvent contenir que des caract&egrave;res alphanum&eacute;riques et des traits d'union. Si vous modifiez l'alias d'une enqu&ecirc;te publi&eacute;e, ouvrez la page &laquo;Aperçu&raquo; et cliquez sur &laquo;Appliquer les modifications&raquo;.</p>
<p>Remarque: un m&ecirc;me alias ne peut &ecirc;tre utilis&eacute; qu'une seule fois dans tout le syst&egrave;me EUSurvey. Vous serez averti si votre alias est d&eacute;j&agrave; utilis&eacute; par une autre enqu&ecirc;te.</p>
<h2><a class="anchor" name="_Toc7-7"></a>Puis-je envoyer un lien direct vers une traduction de mon enqu&ecirc;te?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Lorsque vous envoyez des invitations, ou utilisez le lien vers le formulaire publi&eacute; sur la page &laquo;Aperçu&raquo;, le lien renvoie par d&eacute;faut vers le formulaire dans la langue pivot.</p>
<p>Cependant, il est &eacute;galement possible de <b>rediriger les r&eacute;pondants directement</b> vers la traduction souhait&eacute;e, &agrave; l'aide du lien suivant:<br /><b>https://ec.europa.eu${contextpath}/runner/<span style="color:red">SurveyAlias</span>?surveylanguage=<span style="color:red">LC</span></b></p>
<p>Il vous suffit de remplacer:</p>
	<ul>
		<li>&laquo;<b><span style="color:red">SurveyAlias</span></b>&raquo; par l'<b>alias de votre enqu&ecirc;te</b></li>
		<li>&laquo;<b><span style="color:red">LC</span></b>&raquo; par le <b>code de la langue</b> (FR pour le français, DE pour l'allemand, etc.)</li>
	</ul>
<h2><a class="anchor" name="_Toc7-3"></a>Comment programmer la publication d'une enqu&ecirc;te pendant un cong&eacute;?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Vous pouvez programmer la publication automatique de votre enqu&ecirc;te au moment de votre choix. Ouvrez votre enqu&ecirc;te et allez &agrave; la page &laquo;Propri&eacute;t&eacute;s&raquo;. Cliquez sur le bouton &laquo;Modifier&raquo; dans la rubrique &laquo;Param&egrave;tres avancés&raquo;, puis indiquez les dates de d&eacute;but et de fin de publication de votre enqu&ecirc;te.</p>
<h2><a class="anchor" name="_Toc7-4"></a>Est-il possible de programmer un rappel de la date de fin de l'enqu&ecirc;te?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>EUSurvey peut vous envoyer un courrier &eacute;lectronique pour vous rappeler que l'enqu&ecirc;te est sur le point de se terminer. Vous pourrez ainsi pr&eacute;parer les &eacute;tapes suivantes (organiser les ressources n&eacute;cessaires &agrave; l'analyse des r&eacute;sultats, par ex.).</p>
<p>Pour activer cette fonction, ouvrez votre enqu&ecirc;te et allez &agrave; la page &laquo;Propri&eacute;t&eacute;s&raquo;. Cliquez sur &laquo;Param&egrave;tres avanc&eacute;s&raquo;, puis sur le bouton &laquo;Modifier&raquo;. Cochez l'option &laquo;Notification de fin&raquo;, indiquez &agrave; quel moment vous souhaitez recevoir le courrier de rappel et pr&eacute;cisez si tous les autres gestionnaires de formulaires doivent &eacute;galement recevoir ce message. Cliquez enfin sur &laquo;Enregistrer&raquo;.</p>
<c:if test="${enablepublicsurveys}">
<h2><a class="anchor" name="_Toc7-5"></a>Comment afficher une enqu&ecirc;te dans la liste des enqu&ecirc;tes publiques dans EUSurvey?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Vous pouvez publier un lien vers votre enqu&ecirc;te dans la &laquo;<a href="http://ec.europa.eu${contextpath}/home/publicsurveys" target="_blank">liste des enqu&ecirc;tes publiques</a>&raquo; d'EUSurvey.</p>
<p>Ouvrez votre enqu&ecirc;te et allez &agrave; la page &laquo;Propri&eacute;t&eacute;s&raquo;. S&eacute;lectionnez &laquo;Param&egrave;tres de s&eacute;curit&eacute;&raquo; et cliquez sur le bouton &laquo;Modifier&raquo;. Sous la rubrique &laquo;Public&raquo;, s&eacute;lectionnez &laquo;Oui&raquo; et cliquez sur le bouton &laquo;Enregistrer&raquo;.</p>
<p><b>N.B.:</b> l'ajout de votre enqu&ecirc;te &agrave; la liste des enqu&ecirc;tes publiques d'EUSurvey doit &ecirc;tre valid&eacute; par l'&eacute;quipe administrative d'EUSurvey. Celle-ci reçoit automatiquement un email du syst&egrave;me quand, sur la page &laquo;Aperçu&raquo;, vous cliquez sur &laquo;Publier&raquo; ou si vous appliquez des changements. Une fois l'autorisation accord&eacute;e, vous recevez un message de confirmation et votre enqu&ecirc;te est disponible dans la liste des enqu&ecirc;tes publiques.</p>
<p>Pour toute enqu&ecirc;te affich&eacute;e dans la liste publique d'EUSurvey, les participants doivent r&eacute;soudre un CAPTCHA avant de soumettre leur contribution. Ce CAPTCHA est cr&eacute;&eacute; automatiquement.</p>
</c:if>
<h2><a class="anchor" name="_Toc7-6"></a>Pour le personnel des institutions européennes: quelles sont les exigences officielles pour le lancement d'une consultation publique ouverte (site web &laquo;Votre point de vue sur l'Europe&raquo;)?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Veuillez suivre pas &agrave; pas <a href="https://circabc.europa.eu/sd/d/fc02d2ac-d94f-42ed-b866-b3429e0d717b/Survey_publication_your_voice_in_europe_NEW.pdf" target="_blank">la proc&eacute;dure du Secr&eacute;tariat G&eacute;n&eacute;ral</a> pour publier une consultation publique ouverte sur le site <a href="http://ec.europa.eu/yourvoice/consultations/index_en.htm" target="_blank">&laquo;Votre point de vue sur l'Europe&raquo;</a>.</p>

<h1><a class="anchor" name="_Toc8"></a>Gestion des enqu&ecirc;tes</h1>
<h2><a class="anchor" name="_Toc8-1"></a>Est-il possible de corriger les erreurs d&eacute;couvertes dans une enqu&ecirc;te?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Oui, vous pouvez modifier l'enqu&ecirc;te aussi souvent que vous le souhaitez, ainsi qu'ajouter ou modifier des questions (y compris des questions d&eacute;pendantes). Nous vous signalons toutefois que plus vous effectuerez de modifications, moins les donn&eacute;es recueillies seront utilisables, compte tenu de la possibilit&eacute; que diff&eacute;rents participants &agrave; votre enqu&ecirc;te aient r&eacute;pondu &agrave; des versions diff&eacute;rentes de l'enqu&ecirc;te. D&egrave;s lors, si vous souhaitez toujours pouvoir comparer toutes les r&eacute;ponses, il est recommand&eacute; de ne pas modifier du tout la structure de votre enqu&ecirc;te. Veuillez noter que vous conservez l'enti&egrave;re responsabilit&eacute; de toute modification appliqu&eacute;e &agrave; votre enqu&ecirc;te au cours de son existence.</p>
<p>Si vous tenez &agrave; modifier une enqu&ecirc;te d&eacute;j&agrave; publi&eacute;e, n'oubliez pas de cliquer sur le bouton &laquo;Appliquer les modifications&raquo; de la page &laquo;Aperçu&raquo;, afin que ces modifications apparaissent dans l'enqu&ecirc;te publi&eacute;e.</p>
<p>Si vous souhaitez supprimer des r&eacute;ponses de votre enqu&ecirc;te, voyez &laquo;En cas de modification de l'enqu&ecirc;te, des contributions sont-elles supprim&eacute;es?&raquo;</p>
<h2><a class="anchor" name="_Toc8-2"></a>En cas de modification de l'enqu&ecirc;te, des contributions sont-elles supprim&eacute;es?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Aucune contribution ne sera supprim&eacute;e, sauf si vous effacez votre enqu&ecirc;te du syst&egrave;me. Il se pourrait toutefois que vous ne soyez pas en mesure de visualiser l'ensemble des donn&eacute;es recueillies si vous supprimez certaines questions de l'enqu&ecirc;te au cours de sa p&eacute;riode d'activit&eacute;: cela tient au fait que le masque de recherche repr&eacute;sente toujours la derni&egrave;re version publi&eacute;e de l'enqu&ecirc;te. Pour visualiser toutes les r&eacute;ponses, m&ecirc;me celles &agrave; des questions supprim&eacute;es pendant la p&eacute;riode d'activit&eacute; de votre enqu&ecirc;te, voyez &laquo;Comment afficher l'ensemble des questions enregistr&eacute;es?&raquo;.</p>
<h2><a class="anchor" name="_Toc8-3"></a>Comment modifier le titre d'une enqu&ecirc;te?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Ouvrez votre enqu&ecirc;te et allez &agrave; la page &laquo;Propri&eacute;t&eacute;s&raquo;. Cliquez sur le bouton &laquo;Modifier&raquo; dans la rubrique &laquo;Param&egrave;tres de base&raquo;, puis modifiez le titre de l'enqu&ecirc;te. Si vous avez d&eacute;j&agrave; publi&eacute; votre enqu&ecirc;te, n'oubliez pas d'ouvrir la page &laquo;Aperçu&raquo; et de cliquer sur &laquo;Appliquer les modifications&raquo;.</p>
<h2><a class="anchor" name="_Toc8-4"></a>Comment modifier l'adresse de contact d'une enqu&ecirc;te?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Le message de confirmation s'affiche apr&egrave;s que les participants soumettent leur contribution. Pour modifier le message par d&eacute;faut, ouvrez l'enqu&ecirc;te, allez &agrave; l'&eacute;diteur et cliquez sur le bouton &laquo;Modifier la page de confirmation&raquo;. Si vous avez d&eacute;j&agrave; publi&eacute; votre enqu&ecirc;te, n'oubliez pas d'ouvrir la page &laquo;Aperçu&raquo; et de cliquer sur &laquo;Appliquer les modifications&raquo;.</p>
<h2><a class="anchor" name="_Toc8-5"></a>Comment personnaliser le message de confirmation par d&eacute;faut?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>La page de sortie contient le message que verront les participants si votre enqu&ecirc;te n'est pas accessible. Pour modifier le message par d&eacute;faut, ouvrez l'enqu&ecirc;te, allez &agrave; l'&eacute;diteur et cliquez sur le bouton &laquo;Modifier la page de sortie&raquo;. Si vous avez d&eacute;j&agrave; publi&eacute; votre enqu&ecirc;te, n'oubliez pas d'ouvrir la page &laquo;Aperçu&raquo; et de cliquer sur &laquo;Appliquer les modifications&raquo;.</p>
<h2><a class="anchor" name="_Toc8-6"></a>Comment personnaliser le message de sortie par d&eacute;faut? <a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>La page de sortie contient le message que verront les participants si votre enqu&ecirc;te n'est pas accessible. Pour modifier le message par d&eacute;faut, ouvrez l'enqu&ecirc;te, allez &agrave; l'&eacute;diteur et cliquez sur le bouton &laquo;Modifier la page de sortie&raquo;. Si vous avez d&eacute;j&agrave; publi&eacute; votre enqu&ecirc;te, n'oubliez pas d'ouvrir la page &laquo;Aperçu&raquo; et de cliquer sur &laquo;Appliquer les modifications&raquo;.</p>
<h2><a class="anchor" name="_Toc8-7"></a>Fonctionnalit&eacute; d'archivage<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Vous pouvez archiver votre formulaire avec toutes ses r&eacute;ponses soumises pour le recharger ou le lancer plus tard. Pour archiver votre formulaire, cliquez sur l'ic&ocirc;ne &laquo;Archiver le questionnaire&raquo; dans le menu des actions de la page &laquo;Aper&ccedil;u&raquo;.</p>
<p>Les questionnaires archiv&eacute;s ne peuvent ni &ecirc;tre &eacute;dit&eacute;s ni recevoir des nouvelles r&eacute;ponses. Vous pouvez par contre exporter les r&eacute;sultats ou exporter une version PDF de votre survey. Les questionnaires archiv&eacute;s sont disponibles dans le &laquo;Tableau de bord&raquo; d'o&ugrave; ils peuvent &ecirc;tre restaur&eacute;s. Les questionnaires restaur&eacute;s peuvent &ecirc;tre &eacute;dit&eacute;s de nouveau.</p>
<h2><a class="anchor" name="_Toc8-8"></a>Comment donner acc&egrave;s &agrave; mon enqu&ecirc;te &agrave; d'autres utilisateurs?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Dans EUSurvey, vous pouvez donner acc&egrave;s &agrave; d'autres utilisateurs pour diff&eacute;rentes t&acirc;ches:</p>
	<ul>
		<li>tester l'enqu&ecirc;te (&laquo;Acc&egrave;s aperçu formulaire&raquo;);</li>
		<li>acc&eacute;der aux r&eacute;sultats (&laquo;R&eacute;sultats&raquo;);</li>
		<li>modifier l'enqu&ecirc;te (&laquo;Gestion du formulaire&raquo;).</li>
	</ul>
<p>Pour leur donner l'acc&egrave;s, ouvrez votre enqu&ecirc;te et allez &agrave; la page &laquo;Droits&raquo;. Vous pouvez octroyer l'acc&egrave;s &agrave; une personne ou &agrave; un service. Les droits d'acc&egrave;s suivants sont possibles:</p>
	<ul>
		<li>vert: acc&egrave;s en lecture et &eacute;criture;</li>
		<li>jaune: acc&egrave;s en lecture;</li>
		<li>rouge: pas d'acc&egrave;s.</li>
	</ul>
<p>Depuis la page &laquo;Droits&raquo;, cliquez sur &laquo;Ajouter un utilisateur&raquo; ou &laquo;Ajouter un service&raquo;. L'assistant d'ajout d'utilisateurs s'ouvre alors.</p>
<p>Apr&egrave;s avoir cliqu&eacute; sur &laquo;Ajouter un utilisateur&raquo;, vous devez s&eacute;lectionner le domaine (&agrave; savoir la Commission europ&eacute;enne), puis indiquer le nom d'utilisateur, l'adresse &eacute;lectronique ou tout autre champ, et cliquer sur &laquo;Rechercher&raquo;. S&eacute;lectionnez l'utilisateur et cliquez sur &laquo;OK&raquo;.</p>
<p>Si vous cliquez sur &laquo;Ajouter un service&raquo;, s&eacute;lectionnez le domaine correct et naviguez jusqu'au service voulu. Cliquez ensuite sur &laquo;OK&raquo;.</p>
<p>Vous serez redirig&eacute; vers la page &laquo;Droits&raquo;, o&ugrave; vous pourrez d&eacute;terminer les autorisations en cliquant sur les ic&ocirc;nes rouges:</p>
	<ul>
		<li>Pour octroyer le droit de tester votre enqu&ecirc;te:<br>
			S&eacute;lectionnez la couleur verte pour la fonction &laquo;Acc&egrave;s aperçu formulaire&raquo;. Cliquez simplement sur la couleur pour modifier les droits. Les utilisateurs ajout&eacute;s verront automatiquement votre enqu&ecirc;te appara&icirc;tre dans leur page &laquo;Enqu&ecirc;tes&raquo; lors de leur prochaine connexion &agrave; EUSurvey. (Voir &eacute;galement "<a href="#_Toc5-2">Des coll&egrave;gues peuvent-ils tester l'enqu&ecirc;te avant sa publication?</a>").</li>
		<li>Pour octroyer le droit d'acc&eacute;der aux r&eacute;sultats de votre enqu&ecirc;te:<br>
			S&eacute;lectionnez la couleur jaune pour la fonction &laquo;R&eacute;sultats&raquo;. Les utilisateurs pourront afficher les r&eacute;sultats, sans pouvoir modifier ou supprimer quoi que ce soit. Si vous s&eacute;lectionnez la couleur verte, ils pourront afficher, modifier et supprimer les r&eacute;ponses. (Voir &eacute;galement "<a href="#_Toc9-7">Comment autoriser d'autres utilisateurs &agrave; acc&eacute;der aux r&eacute;sultats d'une enqu&ecirc;te?</a>").</li>
		<li>Pour octroyer le droit de modifier votre enqu&ecirc;te:<br>
			Si vous s&eacute;lectionnez la couleur jaune, les utilisateurs autoris&eacute;s pourront seulement afficher votre enqu&ecirc;te. Si vous s&eacute;lectionnez la couleur verte, ils pourront &eacute;galement la modifier, et votre enqu&ecirc;te appara&icirc;tra automatiquement dans leur liste d'enqu&ecirc;tes. (Voir &eacute;galement "<a href="#_Toc3-5">Comment autoriser d'autres utilisateurs &agrave; modifier une enqu&ecirc;te?</a>").</li>
	</ul>
<p>Si vous s&eacute;lectionnez la couleur verte pour les trois cercles, l'utilisateur disposera de tous les droits pour votre enqu&ecirc;te.</p>

<h2><a class="anchor" name="_Toc8-9"></a>Que sont les journaux d'activité ?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>
Les journaux d'activité surveillent et enregistrent l'activité sur votre enquête. De cette façon, vous pouvez vérifier quel utilisateur a appliqué quelle modification à votre enquête et à quel moment. Vous pouvez également exporter les journaux d'activités dans plusieurs formats de fichiers tels que xls, csv et ods. Entrez le journal d'activité de votre enquête en cliquant sur le lien "Activité", à côté de "Propriétés". Si les journaux d'activités sont vides, il se peut qu'ils soient désactivés à l'échelle du système. Vous trouverez <a href="${contextpath}/resources/documents/ActivityLogEvents.xlsx">ici</a> une liste des événements enregistrés.
</p>

<h1><a class="anchor" name="_Toc9"></a>Analyse, exportation et publication des r&eacute;sultats</h1>
<h2><a class="anchor" name="_Toc9-1"></a>Où trouver les contributions reçues des participants?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Ouvrez votre enqu&ecirc;te dans EUSurvey (voir aussi &laquo;<a href="#_Toc2-5">Comment ouvrir une enqu&ecirc;te existante pour la modifier, par exemple?</a>&raquo;) et allez &agrave; la page &laquo;R&eacute;sultats&raquo;. Vous verrez d'abord un tableau pr&eacute;sentant le contenu int&eacute;gral de toutes les contribution soumises. Vous pouvez afficher les r&eacute;sultats de 2 façons:</p>
	<ul>
		<li>Contenu int&eacute;gral</li>
		<li>Statistiques</li>
	</ul>
<p>Vous pouvez passer d'un mode &agrave; l'autre en cliquant sur les ic&ocirc;nes du coin sup&eacute;rieur gauche de la page.</p>
<h2><a class="anchor" name="_Toc9-2"></a>Comment t&eacute;l&eacute;charger les contributions reçues?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Pour exporter les r&eacute;ponses d'EUSurvey sur votre ordinateur, ouvrez votre enqu&ecirc;te et allez &agrave; la page &laquo;R&eacute;sultats&raquo;. Les icônes du coin sup&eacute;rieur droit de la page indiquent les formats de fichiers disponibles pour l'exportation. Cliquez sur une des icônes, puis indiquez un nom de fichier dans la fen&ecirc;tre qui s'affiche. Le fichier d'exportation apparaîtra sous ce nom sur la page &laquo;Exporter&raquo;. Diff&eacute;rents formats de fichier d'exportation sont disponibles, en fonction du mode d'affichage (contenu int&eacute;gral, graphiques ou statistiques). Remarque: le fichier d'exportation ne contiendra que l'ensemble de questions configur&eacute;, ainsi que les r&eacute;sultats de recherche correspondant au filtre utilis&eacute;.</p>
<h2><a class="anchor" name="_Toc9-3"></a>Comment rechercher et analyser un sous-ensemble d&eacute;fini de contributions?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Sur la page &laquo;R&eacute;sultats&raquo; (voir &laquo;<a href="#_Toc9-1">Où trouver les contributions reçues des participants?</a>&raquo;), vous pouvez rechercher des mots cl&eacute;s dans les r&eacute;ponses en texte libre ou s&eacute;lectionner des r&eacute;ponses sp&eacute;cifiques dans les questions &agrave; choix, au moyen des filtres propos&eacute;s. Cela r&eacute;duit l'ensemble des r&eacute;ponses &agrave; un sous-ensemble de contributions. Vous pouvez changer le mode d'affichage &agrave; tout moment afin d'effectuer une analyse statistique d&eacute;taill&eacute;e des donn&eacute;es recueillies. Remarque: pour afficher et analyser les r&eacute;sultats, vous devez disposer de certains droits (voir &laquo;<a href="#_Toc9-7">Comment autoriser d'autres utilisateurs d'acc&eacute;der aux r&eacute;sultats d'une enqu&ecirc;te?</a>&raquo;). Pour exporter un sous-ensemble de contributions, voir &laquo;Comment t&eacute;l&eacute;charger les contributions reçues?&raquo;.</p>
<h2><a class="anchor" name="_Toc9-4"></a>Comment retourner &agrave; l'ensemble des contributions apr&egrave;s avoir d&eacute;fini un sous-ensemble?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Pour afficher l'ensemble des r&eacute;ponses, cliquez sur le bouton &laquo;R&eacute;initialiser&raquo;, en haut de la page &laquo;R&eacute;sultats&raquo;, ou d&eacute;sactivez toutes les recherches effectu&eacute;es au moyen des filtres de cette page.</p>
<h2><a class="anchor" name="_Toc9-5"></a>Comment publier les r&eacute;sultats? <a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Ouvrez votre enqu&ecirc;te, allez &agrave; la page &laquo;Propri&eacute;t&eacute;s&raquo; et s&eacute;lectionnez &laquo;Publier les r&eacute;sultats&raquo;. Vous verrez alors l'URL menant aux r&eacute;sultats publi&eacute;s. En cliquant sur le bouton &laquo;Modifier&raquo;, vous pouvez choisir les questions, r&eacute;ponses ou contributions &agrave; publier. Vous pouvez aussi y acc&eacute;der directement en cliquant sur le bouton &laquo;Modifier la publication des r&eacute;sultats&raquo; de la page &laquo;Aperçu&raquo; de votre enqu&ecirc;te.</p>
<p>Veillez &agrave; effectuer une s&eacute;lection dans la rubrique &laquo;Publier les r&eacute;sultats&raquo; de la section &laquo;Publier&raquo;, faute de quoi le syst&egrave;me ne publiera aucun r&eacute;sultat. </p>
<h2><a class="anchor" name="_Toc9-6"></a>Comment acc&eacute;der aux r&eacute;sultats publi&eacute;s?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Ouvrez la page &laquo;Aperçu&raquo; et cliquez sur le lien &laquo;Publi&eacute;&raquo;, juste &agrave; côt&eacute; du mot &laquo;R&eacute;sultats&raquo;, pour acc&eacute;der aux r&eacute;sultats publi&eacute;s. Toute personne connaissant cette adresse pourra acc&eacute;der &agrave; vos r&eacute;sultats.</p>
<h2><a class="anchor" name="_Toc9-7"></a>Comment autoriser d'autres utilisateurs &agrave; acc&eacute;der aux r&eacute;sultats d'une enqu&ecirc;te?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Ouvrez votre enqu&ecirc;te, allez &agrave; la page &laquo;Droits&raquo; et donnez acc&egrave;s aux r&eacute;sultats &agrave; d'autres utilisateurs. Pour en savoir plus, voir &laquo;<a href="#_Toc8-8">Comment donner acc&egrave;s &agrave; mon enqu&ecirc;te &agrave; d'autres utilisateurs?</a>&raquo;</p>
<h2><a class="anchor" name="_Toc9-8"></a>Je ne parviens pas à décompresser mes fichiers exportés<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Cela peut se produire si le nom des fichiers contenus dans votre dossier est trop long. Dans Windows, la longueur du chemin d'accès d'un fichier ne peut pas dépasser 260 caractères. Voici les solutions possibles:</p>
	<ul>
		<li>Décompressez le dossier dans le répertoire racine de votre système d'exploitation, dans le répertoire "C:" au lieu de "C:\Users\NOMD'UTILISATEUR\Desktop", par exemple</li>
		<li>Lorsque vous décompressez les fichiers, renommez le dossier afin de réduire la longueur du chemin d'accès</li>
	</ul>
<h2><a class="anchor" name="_Toc9-9"></a>Les r&eacute;sultats publi&eacute;s - protection des donn&eacute;es &agrave; caract&egrave;re personnel, charg&eacute;es par les participants<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Pour des raisons de protection des donn&eacute;es, le gestionnaire du formulaire doit valider la publication des documents charg&eacute;s par les participants en parall&egrave;le aux r&eacute;sultats. Pour cela, il faut cocher l'option  &laquo;Document charg&eacute;s&raquo; dans la page &laquo;Propri&eacute;t&eacute;s - Publier les r&eacute;sultats&raquo;.</p>
<p>Veuillez noter que cette option apparait uniquement si le questionnaire contient des fichiers chargés.</p>
<h2><a class="anchor" name="_Toc9-10"></a>Comment concevoir une enqu&ecirc;te afin de publier les r&eacute;sultats avec ou sans donn&eacute;es personnelles?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Si vous voulez donner le choix &agrave; vos participants de publier ou non leurs donn&eacute;es personnelles avec leurs r&eacute;ponses, suivez <a href="https://circabc.europa.eu/sd/d/e68ff760-226f-40e9-b7cb-d3dcdd04bfb1/How_to_publish_survey_results_anonymously.pdf" target="_blank">ces instructions</a>.</p>
<h2><a class="anchor" name="_Toc9-11"></a>Pourquoi mes résultats ne sont-ils pas à jour?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Une nouvelle base de données a été introduite pour améliorer les performances d'EUSurvey lors de l'interrogation des résultats de votre enquête. Toutefois, cela peut entraîner des retards jusqu'à ce que les données les plus récentes apparaissent sur la page de résultats de votre enquête. Ce délai ne doit pas dépasser 12 heures.</p>
<p>Si les données affichées ont plus de 12 heures, veuillez contacter <a href="https://ec.europa.eu/eusurvey/home/support">l'assistance</a> EUSurvey.</p>

<h1><a class="anchor" name="_Toc10"></a>Styles et mise en pages</h1>
<h2><a class="anchor" name="_Toc10-1"></a>Comment modifier l'apparence d'une enqu&ecirc;te?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Ouvrez votre enqu&ecirc;te, allez &agrave; la page &laquo;Propri&eacute;t&eacute;s&raquo; et s&eacute;lectionnez &laquo;Apparence&raquo;. Cliquez sur &laquo;Modifier&raquo; et choisissez l'habillage de votre enqu&ecirc;te parmi les habillages disponibles. Cliquez sur &laquo;Enregistrer&raquo;. Si vous avez d&eacute;j&agrave; publi&eacute; votre enqu&ecirc;te, n'oubliez pas d'ouvrir la page &laquo;Aperçu&raquo; et de cliquer sur &laquo;Appliquer les modifications&raquo;.</p>
<h2><a class="anchor" name="_Toc10-2"></a>Comment cr&eacute;er son propre th&egrave;me visuel?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Sur la page &laquo;Param&egrave;tres&raquo; d'EUSurvey, en haut de l'&eacute;cran, s&eacute;lectionnez &laquo;Habillages&raquo; et cliquez sur &laquo;Cr&eacute;er un nouvel habillage&raquo;. L'&eacute;diteur d'habillages s'ouvre alors. Vous pouvez partir d'un th&egrave;me visuel existant et utiliser l'&eacute;diteur d'habillages en ligne pour adapter ce mod&egrave;le &agrave; vos besoins.</p>
<h2><a class="anchor" name="_Toc10-3"></a>Comment ajouter un logo &agrave; une enqu&ecirc;te?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Pour que le logo de votre projet ou de votre entreprise apparaisse dans le coin sup&eacute;rieur droit de votre enqu&ecirc;te, t&eacute;l&eacute;chargez un fichier d'image au moyen du sous-menu &laquo;Apparence&raquo; de la page &laquo;Propri&eacute;t&eacute;s&raquo;. Si vous avez d&eacute;j&agrave; publi&eacute; votre enqu&ecirc;te, n'oubliez pas d'ouvrir la page &laquo;Aperçu&raquo; et de cliquer sur &laquo;Appliquer les modifications&raquo;.</p>
<h2><a class="anchor" name="_Toc10-4"></a>Comment ajouter des liens utiles &agrave; une enqu&ecirc;te?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Ouvrez votre enqu&ecirc;te, allez &agrave; la page &laquo;Propri&eacute;t&eacute;s&raquo; et s&eacute;lectionnez &laquo;Param&egrave;tres avanc&eacute;s&raquo;. Cliquez sur le bouton &laquo;Modifier&raquo; pour ajouter des URL et leurs titres dans la rubrique &laquo;Liens utiles&raquo;. Ces liens figureront sur chaque page de votre enqu&ecirc;te, sur le côt&eacute; droit. Si vous avez d&eacute;j&agrave; publi&eacute; votre enqu&ecirc;te, n'oubliez pas d'ouvrir la page &laquo;Aperçu&raquo; et de cliquer sur &laquo;Appliquer les modifications&raquo;.</p>
<h2><a class="anchor" name="_Toc10-5"></a>Comment ajouter des documents de r&eacute;f&eacute;rence &agrave; une enqu&ecirc;te?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Ouvrez votre enqu&ecirc;te, allez &agrave; la page &laquo;Propri&eacute;t&eacute;s&raquo; et s&eacute;lectionnez &laquo;Param&egrave;tres avanc&eacute;s&raquo;. Cliquez sur le bouton &laquo;Modifier&raquo;. Dans la rubrique &laquo;Documents de r&eacute;f&eacute;rence&raquo;, choisissez le document &agrave; t&eacute;l&eacute;charger et indiquez son titre. Les documents ajout&eacute;s figureront sur chaque page de votre enqu&ecirc;te, sur le côt&eacute; droit. Si vous avez d&eacute;j&agrave; publi&eacute; votre enqu&ecirc;te, n'oubliez pas d'ouvrir la page &laquo;Aperçu&raquo; et de cliquer sur &laquo;Appliquer les modifications&raquo;.</p>
<h2><a class="anchor" name="_Toc10-6"></a>Comment cr&eacute;er une enqu&ecirc;te en plusieurs pages?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Les sections de haut niveau de votre enqu&ecirc;te peuvent &ecirc;tre divis&eacute;es automatiquement en pages distinctes. Ouvrez votre enqu&ecirc;te, allez &agrave; la page &laquo;Propri&eacute;t&eacute;s&raquo; et s&eacute;lectionnez &laquo;Apparence&raquo;. Cliquez sur &laquo;Modifier&raquo;. Activez l'option &laquo;Pr&eacute;sentation sur plusieurs pages&raquo; et cliquez sur &laquo;Enregistrer&raquo;. Si vous avez d&eacute;j&agrave; publi&eacute; votre enqu&ecirc;te, n'oubliez pas d'ouvrir la page &laquo;Aperçu&raquo; et de cliquer sur &laquo;Appliquer les modifications&raquo;.</p>
<h2><a class="anchor" name="_Toc10-7"></a>Comment activer la num&eacute;rotation automatique pour une enqu&ecirc;te? <a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Pour num&eacute;roter automatiquement toutes les sections et questions de votre formulaire, ouvrez votre enqu&ecirc;te, allez &agrave; la page &laquo;Propri&eacute;t&eacute;s&raquo;, s&eacute;lectionnez &laquo;Apparence&raquo; et cliquez sur &laquo;Modifier&raquo;. Activez l'option &laquo;Num&eacute;rotation automatique&raquo; et enregistrez. Si vous avez d&eacute;j&agrave; publi&eacute; votre enqu&ecirc;te, n'oubliez pas d'ouvrir la page &laquo;Aperçu&raquo; et de cliquer sur &laquo;Appliquer les modifications&raquo;.</p>
<h2><a class="anchor" name="_Toc10-8"></a>Comment cr&eacute;er une apparence personnalis&eacute;e pour une enqu&ecirc;te?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Pour cr&eacute;er une nouvelle apparence de votre questionnaire, allez &agrave; la page &laquo;Param&egrave;tres&raquo;, puis s&eacute;lectionnez &laquo;Apparences&raquo;. Cliquez sur &laquo;Cr&eacute;er une nouvelle apparence&raquo; o&ugrave; vous pourrez configurer le visuel des diff&eacute;rents &eacute;l&eacute;ments d'une enqu&ecirc;te: les questions, son texte, les titres, les bulles d'aides et bien d'autres.</p>
<p>Vous devez d'abord donner un nom &agrave; votre nouvelle apparence. Puis s&eacute;lectionnez l'&eacute;l&eacute;ment que vous d&eacute;sirez configurer. &Agrave; droite de la page, vous trouverez une bo&icirc;te o&ugrave; vous pourrez changer les diff&eacute;rents aspects tels que: les couleurs d'affichage, la police utilis&eacute;e, sa taille et son &eacute;paisseur. Chaque changement sera visible dans la zone de pr&eacute;visualisation, qui se trouve en dessous de &laquo;Skin Preview Survey&raquo;. N'oubliez pas de cliquer sur &laquo;Enregistrer&raquo;!</p>
<p>Il n'est pas n&eacute;cessaire de sauvegarder &agrave; chaque configuration d'&eacute;l&eacute;ment. Vous pouvez enregistrer toutes vos modifications &agrave; la fin.</p> 
<p>Enfin, pour appliquer une &laquo;Apparence&raquo; &agrave; vos enqu&ecirc;tes, allez dans l'onglet &laquo;Propri&eacute;t&eacute;s&raquo; d'un questionnaire, puis &laquo;Apparence&raquo;. Cliquez sur &laquo;Modifier&raquo; et choisissez parmi les apparences disponibles dans le menu &laquo;Style&raquo;. Sauvegardez pour finaliser la configuration.</p>

<h1><a class="anchor" name="_Toc11"></a>Gestion des contacts et des invitations</h1>
<h2><a class="anchor" name="_Toc11-1"></a>Qu'est-ce que le &laquo;carnet d'adresses&raquo;? <a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Le carnet d'adresses permet de cr&eacute;er vos propres groupes de participants. Vous pouvez ainsi inviter des personnes ou organisations qui correspondent &agrave; certains crit&egrave;res (par exemple &laquo;masculin&raquo; et &laquo;plus de 21 ans&raquo;). Chaque participant potentiel constitue un contact du carnet d'adresses. Pour chaque contact, vous pouvez sp&eacute;cifier autant d'attributs que vous le souhaitez. Vous pouvez ajouter n'importe quel contact dans votre carnet d'adresses, du moment que vous indiquez son identifiant (&laquo;Nom&raquo;) et son adresse &eacute;lectronique.</p>
<h2><a class="anchor" name="_Toc11-2"></a>Que sont les &laquo;attributs&raquo; des contacts?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Tout contact enregistr&eacute; dans le carnet d'adresses peut &ecirc;tre caract&eacute;ris&eacute; par un ensemble d'attributs variables, tels que &laquo;Pays&raquo;, &laquo;T&eacute;l&eacute;phone&raquo;, &laquo;Remarques&raquo;, etc. Vous pouvez cr&eacute;er de nouveaux attributs en modifiant les contacts. Dans la fen&ecirc;tre &laquo;Modifier le contact&raquo;, ouvrez le menu des attributs et cliquez sur &laquo;Nouveau...&raquo;. Dans la fen&ecirc;tre qui s'affiche, vous pouvez indiquer le nom du nouvel attribut. L'attribut ainsi cr&eacute;&eacute; s'affichera dans une colonne du carnet d'adresses. Il peut &ecirc;tre ajout&eacute; &agrave; une s&eacute;rie de contacts.</p>
<h2><a class="anchor" name="_Toc11-3"></a>Comment ajouter de nouveaux contacts dans le carnet d'adresses?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Pour ajouter un seul contact, allez &agrave; la page &laquo;Carnet d'adresses&raquo; et cliquez &laquo;Ajouter un contact&raquo;. Vous pouvez &eacute;galement cliquer sur &laquo;Importer&raquo; pour t&eacute;l&eacute;charger une liste de contacts existante au format XLS, ODS, CSV ou TXT. Voir &eacute;galement &laquo;<a  href="#_Toc11-5">Comment importer un fichier avec plusieurs contacts dans le carnet d'adresses?</a>&raquo;</p>
<h2><a class="anchor" name="_Toc11-4"></a>Qu'est-ce qu'un &laquo;formulaire d'enregistrement&raquo;?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Un formulaire d'enregistrement est une forme d'&laquo;enqu&ecirc;te&raquo; qui permet de cr&eacute;er automatiquement des contacts &agrave; partir des donn&eacute;es &agrave; caract&egrave;re personnel introduites par les participants. Pour activer cette fonction, ouvrez votre enqu&ecirc;te, allez &agrave; la page &laquo;Propri&eacute;t&eacute;s&raquo; et s&eacute;lectionnez &laquo;Param&egrave;tres avanc&eacute;s&raquo;. Cliquez sur &laquo;Modifier&raquo;, cochez &laquo;Oui&raquo; &agrave; côt&eacute; de l'option &laquo;Cr&eacute;er des contacts&raquo;, puis sur &laquo;Enregistrer&raquo;. D&egrave;s que cette option est s&eacute;lectionn&eacute;e, le syst&egrave;me ins&egrave;re 2 questions obligatoires en texte libre (&laquo;Nom&raquo; et &laquo;Adresse &eacute;lectronique&raquo;), afin de garantir que chaque participant indique valablement ses coordonn&eacute;es.</p>
<p>Si vous activez l'option &laquo;Attribut&raquo; pour des questions sp&eacute;cifiques, vous pouvez choisir quelles autres informations seront enregistr&eacute;es pour chaque nouveau contact (exemple: vous pouvez associer l'attribut &laquo;T&eacute;l&eacute;phone&raquo; &agrave; une question en texte libre afin d'enregistrer le num&eacute;ro de t&eacute;l&eacute;phone du participant dans le carnet d'adresses).</p>
<h2><a class="anchor" name="_Toc11-5"></a>Comment importer un fichier avec plusieurs contacts dans le carnet d'adresses?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>EUSurvey dispose d'un assistant qui facilite l'importation de listes de contacts dans le syst&egrave;me. Les formats de fichiers suivants sont pris en charge: XLS, ODS, CSV et TXT (avec s&eacute;parateurs).</p>
<p>Pour d&eacute;marrer l'assistant, ouvrez la page &laquo;Carnet d'adresses&raquo; et cliquez sur &laquo;Importer&raquo;. Tout d'abord, s&eacute;lectionnez le fichier dans lequel vous avez enregistr&eacute; vos contacts. Indiquez si votre fichier contient une ligne d'en-t&ecirc;te. Pour un fichier CSV ou TXT, pr&eacute;cisez le type de s&eacute;parateur utilis&eacute; (le caract&egrave;re le plus probable est propos&eacute; par d&eacute;faut).</p>
<p>Ensuite, le syst&egrave;me vous demandera d'indiquer quelles colonnes correspondent &agrave; quels attributs pour l'importation des contacts dans EUSurvey. Remarque: vous devez indiquer quelles colonnes contiennent les attributs obligatoires &laquo;Nom&raquo; et &laquo;Adresse &eacute;lectronique&raquo; avant de pouvoir continuer. Cliquez sur &laquo;Suivant&raquo;; le syst&egrave;me charge le fichier et affiche les contacts individuels qui seront import&eacute;s. Vous pouvez d&eacute;s&eacute;lectionner les contacts que vous ne souhaitez pas importer. Cliquez sur &laquo;Enregistrer&raquo; pour ajouter les contacts &agrave; votre carnet d'adresses.</p>
<h2><a class="anchor" name="_Toc11-6"></a>Comment modifier un attribut pour plusieurs contacts &agrave; la fois?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Pour modifier un attribut pour plusieurs contacts, s&eacute;lectionnez les contacts en question dans votre carnet d'adresses, cliquez sur l'action &laquo;Modifier en bloc&raquo;, puis sur &laquo;OK&raquo;.</p>
<p>La fen&ecirc;tre qui s'affiche permet de conserver, d'effacer ou de modifier les attributs de plusieurs contacts &agrave; la fois. Par d&eacute;faut, seuls les attributs configur&eacute;s sont affich&eacute;s. Cliquez sur la croix verte pour afficher les autres attributs. Apr&egrave;s avoir introduit les modifications voulues, cliquez sur &laquo;Mettre &agrave; jour&raquo; et confirmez. L'application enregistre les modifications dans le carnet d'adresses.</p>
<h2><a class="anchor" name="_Toc11-7"></a>Est-il possible d'exporter les contacts du carnet d'adresses dans un fichier?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Oui: sur la page &laquo;Carnet d'adresses&raquo;, cliquez sur une des icônes du coin sup&eacute;rieur droit, qui indiquent les formats de fichiers disponibles. Vous trouverez les contacts export&eacute;s sur la page &laquo;Exportations&raquo;.</p>

<h1><a class="anchor" name="_Toc12"></a>Invitation de participants</h1>
<h2><a class="anchor" name="_Toc12-1"></a>Comment d&eacute;finir un groupe de participants potentiels? Qu'est-ce qu'une &laquo;liste d'invit&eacute;s&raquo;?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>EUSurvey permet de s&eacute;lectionner plusieurs contacts &agrave; la fois et d'envoyer &agrave; chacun de ces contacts un message contenant un lien d'acc&egrave;s individuel. Ces groupes de contacts sont appel&eacute;s &laquo;<a href="#_Toc11-1">listes d'invit&eacute;s</a>&raquo;. Il s'agit de la deuxi&egrave;me façon - outre la d&eacute;finition d'un mot de passe global pour l'enqu&ecirc;te - de permettre &agrave; des personnes de participer &agrave; votre enqu&ecirc;te.</p>
<p>Pour inviter plusieurs contacts &agrave; participer, ouvrez votre enqu&ecirc;te et allez &agrave; la page &laquo;Participants&raquo;. Cliquez sur &laquo;Cr&eacute;er une nouvelle liste d'invit&eacute;s&raquo; pour d&eacute;marrer un assistant qui vous guidera tout au long de cette proc&eacute;dure. Choisissez un nom pour le groupe et s&eacute;lectionnez un des types de liste suivants:</p>
	<ul>
		<li>Contacts du carnet d'adresses EUSurvey (d&eacute;faut)<br/>S&eacute;lectionnez des contacts du carnet d'adresses (voir &laquo;Qu'est-ce que le "carnet d'adresses"?&raquo;) pour les ajouter &agrave; votre liste d'invit&eacute;s.</li>
		<li>Institutions et autres organes de l'UE (uniquement pour le personnel des institutions européennes)<br/>S&eacute;lectionnez plusieurs services de votre institution ou agence pour ajouter &agrave; la liste toutes les personnes de ces services.</li>
		<li>Jetons<br/>Cette fonction cr&eacute;e une liste de &laquo;jetons&raquo; (des codes d'acc&egrave;s) qui peuvent &ecirc;tre distribu&eacute;s hors ligne afin d'acc&eacute;der &agrave; une enqu&ecirc;te en ligne s&eacute;curis&eacute;e.</li>
	</ul>
<p>Utilisez la fonction de recherche de votre carnet d'adresses pour trouver les contacts voulus, puis cliquez sur le bouton &laquo;Ajouter&raquo; pour les ajouter &agrave; votre nouvelle liste d'invit&eacute;s. Cliquez sur &laquo;Enregistrer&raquo;. Une nouvelle liste d'invit&eacute;s est cr&eacute;&eacute;e, contenant tous les contacts que vous souhaitez inviter &agrave; participer &agrave; l'enqu&ecirc;te.</p>
<p>Maintenant, nous allons voir comment envoyer des liens d'acc&egrave;s individuels par courrier &eacute;lectronique &agrave; des contacts configur&eacute;s au moyen d'une liste d'invit&eacute;s.</p>
<h2><a class="anchor" name="_Toc12-2"></a>Comment modifier/supprimer une liste d'invit&eacute;s existante?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Ouvrez votre enqu&ecirc;te et allez &agrave; la page &laquo;Participants&raquo;. Pour modifier la liste d'invit&eacute;s, cliquez sur la petite icône repr&eacute;sentant un crayon. Pour supprimer une liste, cliquez d'abord sur le bouton &laquo;D&eacute;sactiver&raquo;. Vous pouvez ensuite cliquer sur le bouton &laquo;Supprimer&raquo;.</p>
<h2><a class="anchor" name="_Toc12-3"></a>Comment envoyer un courriel d'invitation aux participants?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Une fois la nouvelle liste d'invit&eacute;s cr&eacute;&eacute;e, vous pouvez envoyer des courriels d'invitation aux invit&eacute;s. Pour les questionnaires &laquo;S&eacute;curis&eacute;&raquo; et &laquo;Ouvert&raquo;, ils recevront chacun un lien d'acc&egrave;s individuel. <b>Chaque invit&eacute; ne peut soumettre qu'une seule contribution.</b></p>
<p>Sur la page &laquo;Participants&raquo;, cliquez sur la petite ic&ocirc;ne en forme d'enveloppe. Une fen&ecirc;tre s'ouvre o&ugrave; vous pouvez choisir un mod&egrave;le de courriel &agrave; partir de la boite &laquo;Style&raquo;. Par d&eacute;faut, le style utilis&eacute; est &laquo;EUSurvey&raquo;. Vous pouvez changer l'objet, le contenu de votre courriel et l'adresse email &laquo;r&eacute;ponse&raquo;. Les r&eacute;ponses &agrave; vos invitations seront envoy&eacute;es &agrave; cette adresse. Par apr&egrave;s, enregistrez votre texte du courriel. Il sera disponible dans toutes vos listes d'invit&eacute;es ainsi que tous vos questionnaires. Vous le trouverez dans la liste d&eacute;roulante &laquo;Texte&raquo;. Puis cliquez sur &laquo;Suivant&raquo;. Un assistant vous aidera à envoyer les invitations.</p>

<h1><a class="anchor" name="_Toc13"></a>Gestion du compte personnel</h1>
<h2><a class="anchor" name="_Toc13-1"></a>Comment modifier le mot de passe?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>L'acc&egrave;s &agrave; la version de r&eacute;f&eacute;rence d'EUSurvey, &agrave; la Commission europ&eacute;enne, est g&eacute;r&eacute; via EU Login. Les utilisateurs d'EUSurvey doivent donc modifier leur mot de passe EU Login s'ils ont perdu celui-ci. Pour ce faire, cliquez sur &laquo;Mot de passe perdu?&raquo; sur la page d'accueil d'EU Login.</p>
<h2><a class="anchor" name="_Toc13-2"></a>Comment modifier l'adresse &eacute;lectronique?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Si vous acc&eacute;dez &agrave; EUSurvey au moyen de votre compte EU Login, vous ne pouvez pas modifier votre adresse &eacute;lectronique dans EUSurvey. Connectez-vous &agrave; EU Login, cliquez sur l'onglet &laquo;Donn&eacute;es personnelles&raquo;, puis sur &laquo;Modifiez vos donn&eacute;es personnelles&raquo;.</p>
<p>Si vous utilisez la version open source d'EUSurvey ou l'interface API, connectez-vous &agrave; l'application. Cliquez sur &laquo;Param&egrave;tres, puis sur &laquo;Mon compte&raquo;, puis sur &laquo;Modifier l'adresse &eacute;lectronique&raquo;.</p>
<h2><a class="anchor" name="_Toc13-3"></a>Comment modifier la langue par d&eacute;faut?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Vous pouvez modifier la langue par d&eacute;faut pour les nouvelles enqu&ecirc;tes. Cliquez sur &laquo;Param&egrave;tres, puis sur &laquo;Mon compte&raquo;, puis sur &laquo;Modifier la langue&raquo;. Confirmez votre choix. Le syst&egrave;me proposera d'utiliser la langue s&eacute;lectionn&eacute;e comme langue principale pour toutes vos nouvelles enqu&ecirc;tes.</p>

<h1><a class="anchor" name="_Toc14"></a>Protection de la vie priv&eacute;e</h1>
<h2><a class="anchor" name="_Toc14-1"></a>Ce syst&egrave;me utilise des cookies. Quelles informations y sont enregistr&eacute;es?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Le système utilise des «cookies» (ou témoins) de session afin d'assurer une bonne communication entre le client (l'ordinateur de l'utilisateur) et le serveur. Le navigateur de l'utilisateur doit donc être configuré pour les accepter. Les cookies disparaissent une fois la session terminée.</p>
<p>Le système enregistre une copie de sauvegarde locale de la contribution d'un participant à une enquête. Ce fichier peut servir en cas d'indisponibilité du serveur lorsque l'utilisateur envoie sa contribution, ou si son ordinateur s'éteint accidentellement, par exemple. Il contient le numéro des questions et les brouillons de réponses. Une fois la contribution à l'enquête envoyée au serveur, ou après sauvegarde d'un brouillon sur celui-ci, ces données locales sont supprimées. Au-dessus de l'enquête figure une case à cocher avec la mention «Enregistrer une copie de sauvegarde locale sur votre ordinateur (décochez cette case si vous utilisez un ordinateur public/partagé)», qui permet de désactiver cette fonction. Si l'utilisateur la décoche, aucune donnée ne sera conservée sur son ordinateur.</p>
<h2><a class="anchor" name="_Toc14-2"></a>Quelles informations sont enregistr&eacute;es par EUSurvey lorsqu'un participant soumet une contribution?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Les informations enregistr&eacute;es par EUSurvey d&eacute;pendent des param&egrave;tres de s&eacute;curit&eacute; de votre enqu&ecirc;te ainsi que de la m&eacute;thode que vous utilisez pour inviter vos participants &agrave; contribuer &agrave; votre enqu&ecirc;te.</p>
<p><b>Enqu&ecirc;tes ouvertes au public:</b> Par d&eacute;faut - si votre enqu&ecirc;te n'est pas s&eacute;curis&eacute;e -  EUSurvey ne stocke aucune information li&eacute;e &agrave; l'utilisateur. Toutefois, l'adresse IP de chaque connexion au serveur est enregistr&eacute;e pour des raisons de s&eacute;curit&eacute; (voir <a href=" https://ec.europa.eu/eusurvey/home/privacystatement " target="_blank">d&eacute;claration relative &agrave; la protection de la vie priv&eacute;e</a>).</p>
<p><b>Enqu&ecirc;tes prot&eacute;g&eacute;es par mot de passe individuel:</b> Lorsque votre sondage est s&eacute;curis&eacute; par un mot de passe seulement, EUSurvey ne stocke aucune information li&eacute;e &agrave; l'utilisateur. Toutefois, l'adresse IP de chaque connexion au serveur est enregistr&eacute;e pour des raisons de s&eacute;curit&eacute; (voir <a href=" https://ec.europa.eu/eusurvey/home/privacystatement " target="_blank">d&eacute;claration relative &agrave; la protection de la vie priv&eacute;e</a>).</p>
<p><b>Enqu&ecirc;te s&eacute;curis&eacute;e avec l'authentification EU Login:</b> Lorsque votre enqu&ecirc;te sera s&eacute;curis&eacute;e par l'authentification EU Login, EUSurvey enregistrera l'adresse e-mail du compte EU Login. Toutefois, l'adresse IP de chaque connexion au serveur est enregistr&eacute;e pour des raisons de s&eacute;curit&eacute; (voir <a href=" https://ec.europa.eu/eusurvey/home/privacystatement " target="_blank">d&eacute;claration relative &agrave; la protection de la vie priv&eacute;e</a>).</p>
<p><b>Envoi d'invitations via EUSurvey:</b> Si vous utilisez EUSurvey pour envoyer des invitations &agrave; vos participants via une liste d'invit&eacute;s, ils recevront chacun un lien d'acc&egrave;s individuel. Lors de la soumission, EUSurvey enregistrera un num&eacute;ro d'invitation pouvant &ecirc;tre utilis&eacute; pour associer le participant invit&eacute; aux contributions soumises. Ce comportement est ind&eacute;pendant des param&egrave;tres de s&eacute;curit&eacute; de votre enqu&ecirc;te. En outre, l'adresse IP de chaque connexion au serveur est enregistr&eacute;e pour des raisons de s&eacute;curit&eacute;. (voir <a href=" https://ec.europa.eu/eusurvey/home/privacystatement " target="_blank">d&eacute;claration relative &agrave; la protection de la vie priv&eacute;e</a>).</p>
<p><b>Cr&eacute;er un sondage anonyme:</b> Vous pouvez choisir de cr&eacute;er un sondage anonyme en mettant la &laquo;Protection de la vie priv&eacute;e&raquo; dans les &laquo;Param&egrave;tres de s&eacute;curit&eacute;&raquo; de vos Propri&eacute;t&eacute;s sur &laquo;Non&raquo;. Ensuite, toutes les informations utilisateur collect&eacute;es seront remplac&eacute;es par &laquo;Anonyme&raquo;. Toutefois, l'adresse IP de chaque connexion au serveur est enregistr&eacute;e pour des raisons de s&eacute;curit&eacute; (voir <a href="https://ec.europa.eu/eusurvey/home/privacystatement" target="_blank">d&eacute;claration relative &agrave; la protection de la vie priv&eacute;e</a>).</p>
<h2><a class="anchor" name="_Toc14-3"></a>Les enqu&ecirc;tes doivent-elles inclure une d&eacute;claration relative &agrave; la protection de la vie priv&eacute;e?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
<p>Cela dépend des questions posées et du type de données recueillies dans le cadre de votre enquête. Veuillez noter que certaines personnes pourraient refuser de répondre à votre enquête si vous n'êtes pas en mesure de garantir la confidentialité des données fournies.</p>
<p><b>Pour le personnel des institutions europ&eacute;ennes:</b></p>
<p>Nous attirons votre attention sur la politique en matière de «protection des personnes physiques à l'égard du traitement des données à caractère personnel» <a href="http://eur-lex.europa.eu/LexUriServ/LexUriServ.do?uri=OJ:L:2001:008:0001:0022:EN:PDF" target="_blank">(règlement (CE) n° 45/2001)</a>. Si des données à caractère personnel sont recueillies, une déclaration relative à la protection de la vie privée doit être rédigée et publiée avec le questionnaire. Veuillez prendre contact avec le coordinateur de la protection des données de votre DG, afin de valider cette déclaration. <p>En outre, toute collecte de données à caractère personnel doit être notifiée au délégué à la protection des données (DPD). Veuillez contacter votre coordinateur de la protection des données si vous avez besoin d'aide concernant cette notification.</p>
<p>Vous trouverez ci-joint des modèles de déclaration relative à la protection de la vie privée, que vous pouvez utiliser pour vos enquêtes. Vous pouvez les adapter à vos besoins:</p>
	<ul>
		<li>Modèle de <a href="https://circabc.europa.eu/sd/a/a8f80d78-8620-4326-95ee-7bceb5b18fbc/Template_privacy_statement_surveys_or_consultations.doc" target="_blank">«déclaration relative à la protection de la vie privée pour les enquêtes et les consultations»</a></li>
		<li>Modèle de <a href="https://circabc.europa.eu/sd/a/650ea0ea-79d4-4cf3-93d4-5feb37af10a1/Template_privacy_statement_online_registrations.doc" target="_blank">«déclaration relative à la protection de la vie privée pour les inscriptions à des conférences et autres événements»</a></li>
	</ul>


	</div>

<%@ include file="../footer.jsp" %>	

</body>
</html>
