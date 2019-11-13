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
	<div class="page-wrap">
		<%@ include file="../header.jsp" %>		
	
		<a name="topAnchor"></a>
	
		<c:choose>
			<c:when test="${USER != null && runnermode == null }">
				<%@ include file="../menu.jsp" %>	
				<div class="page" style="padding-top: 110px">
			</c:when>
			<c:when test="${responsive != null}">
				<div class="page" style="max-width: 100%; padding: 10px; padding-top: 40px;">
				<div class="alert alert-warning">Information important: Pour créer et gérer un questionnaire, veuillez accéder à EUSurvey à partir d'un ordinateur. Il n'est pas recommandé de se connecter à EUSurvey à partir d'un téléphone portable ou tablette.</div>
			</c:when>
			<c:otherwise>
				<div class="page" style="padding-top: 40px;">
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
					<li><a class="anchorlink" href="#_Toc1">Comment puis-je entrer en contact avec l'auteur de l'enquête?</a></li>
						
					<li><a class="anchorlink head" href="#_Toc369865010">Accès à une enquête</a>
						<ul>
							<li><a class="anchorlink" href="#_Toc369865012">Que signifie &quot;L'adresse URL que vous avez indiquée n'est pas correcte&quot;?</a></li>
							<li><a class="anchorlink" href="#_Toc369865013">Que signifie &quot;Page non trouvée&quot;?</a></li>
							<li><a class="anchorlink" href="#_Toc369865026">Avec quels navigateurs l'application EUSurvey est-elle compatible?</a></li>
							<li><a class="anchorlink" href="#_Toc369865027">Puis-je r&eacute;pondre &agrave; un questionnaire &agrave; partir de mon t&eacute;l&eacute;phone mobile ou ma tablette PC?</a></li>
						</ul>
					</li>
					<li><a class="anchorlink head" href="#_Toc369865014">Répondre à une enquête</a>
						<ul>
							<li><a class="anchorlink" href="#_Toc369865015">Que signifient &quot;Cette valeur n'est pas un nombre valise&quot;, &quot;Cette valeur n'est pas une date &quot; et &quot;Cette adresse électronique n'est pas valide&quot;?</a></li>
							<li><a class="anchorlink" href="#_Toc369865016">Pourquoi ma sélection disparaît-elle lorsque je réponds à une question en matrice?</a></li>
						</ul>
					</li>
					
					<li><a class="anchorlink head" href="#_Toc369865016a">Signaler un problème sur une enquête</a></li>
									
					<li><a class="anchorlink head" href="#_Toc369865017">Après avoir soumis mes réponses</a>
						<ul>
							<li><a class="anchorlink" href="#_Toc369865018">Puis-je visualiser/imprimer mes réponses une fois que je les ai soumises?</a></li>
							<li><a class="anchorlink" href="#_Toc369865019">Puis-je sauvegarder mes réponses en format PDF?</a></li>
							<li><a class="anchorlink" href="#_Toc369865020">Puis-je modifier mes réponses une fois que je les ai soumises?</a></li>
							<li><a class="anchorlink" href="#_Toc369865021">Je viens de répondre à une enquête. Puis-je consulter les réponses des autres participants?</a></li>
							<li><a class="anchorlink" href="#_Toc369865022">Mon visionneur de fichiers PDF génère le message d'erreur suivant: &quot;Insufficient Image data&quot; (données d'image insuffisantes)&quot;</a></li>
							<li><a class="anchorlink" href="#_Toc369865023">Pourquoi de petits carrés apparaissent-ils dans l'enquête exportée au format PDF?</a></li>
							<li><a class="anchorlink" href="#_Toc369865028">O&ugrave; puis-je trouver mes r&eacute;ponses qui sont enregistr&eacute;s comme brouillon?</a></li>
						</ul>
					</li>
					<li><a class="anchorlink head" href="#_Toc369865024">Protection de la vie privée</a>
						<ul>
							<li><a class="anchorlink" href="#_Toc369865025">Ce système utilise des cookies. Quelles informations y sont enregistrées?</a></li>
						</ul>
					</li>
				</ul>
			</div>
			<br/ ><br />
			
			<h2><a class="anchor" name="_Toc1"></a>Comment puis-je entrer en contact avec l'auteur de l'enquête?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			<p>Par courrier électronique: cliquez sur <i>Contact</i> (en haut à droite de la 1re page de l'enquête).</p>
			
			<h1><a class="anchor" name="_Toc369865010"></a>Accès à une enquête</h1>
					
			<h2><a class="anchor" name="_Toc369865012"></a>Que signifie &quot;L'adresse URL que vous avez indiquée n'est pas correcte&quot;?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			<p>Cela signifie que le système ne peut pas vous autoriser à accéder à l'enquête. C'est généralement le cas lorsqu'une invitation a été supprimée ou désactivée parce que la période d'activation a expiré.</p>
			<p>Si vous estimez que votre lien d'accès est valide, veuillez prendre contact avec l'auteur de l'enquête.</p>
			
			<h2><a class="anchor" name="_Toc369865013"></a>Que signifie &quot;Page non trouvée&quot;?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			<p>
				Deux possibilités:
				<ul>
					<li>soit vous utilisez un lien incorrect pour accéder à l'enquête;</li>
					<li>soit l'enquête que vous recherchez a déjà été retirée du système.</li>
				</ul>
		 		Si vous pensez que le lien est valide, adressez-vous directement à l'auteur de l'enquête. Sinon, informez l'organisme qui a publié le lien que ce dernier est incorrect.
			</p>
			
			<h2><a class="anchor" name="_Toc369865026"></a>Avec quels navigateurs l'application EUSurvey est-elle compatible?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			<p>EUSurvey est compatible avec les deux derni&egrave;res versions d'Internet Explorer, de Mozilla Firefox et Google Chrome.</p>
			<p>L'utilisation d'autres navigateurs pourrait poser des probl&egrave;mes de compatibilit&eacute;.</p>
					
			<h2><a class="anchor" name="_Toc369865027"></a>Puis-je r&eacute;pondre &agrave; un questionnaire &agrave; partir de mon t&eacute;l&eacute;phone mobile ou ma tablette PC?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			<p>Oui, EUSurvey est con&ccedil;u pour des sites web adaptatifs. Cela signifie que la page sera adapt&eacute;e &agrave; la r&eacute;solution de l'&eacute;cran utilis&eacute;.</p>
			
			<h1><a class="anchor" name="_Toc369865014"></a>Répondre à une enquête</h1>
			<h2><a class="anchor" name="_Toc369865015"></a>Que signifient &quot;Cette valeur n'est pas un nombre valide&quot;, &quot;Cette valeur n'est pas une date valide&quot; et &quot;Cette adresse électronique n'est pas valide&quot;?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			<p>Dans le système EUSurvey, l'auteur d'une enquête peut préciser des types de questions particuliers, qui nécessitent de répondre dans un format particulier: nombre, date ou adresse électronique, par exemple. Les dates doivent être saisies dans le format JJ/MM/AAAA.</p>
			
			
			<h2><a class="anchor" name="_Toc369865016"></a>Pourquoi ma sélection disparaît-elle lorsque je réponds à une question en matrice?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			
			<p>Ce type de questions peut être paramétré pour ne permettre de sélectionner qu'une seule réponse à la fois. Cette fonctionnalité permet notamment un «classement» des réponses.</p>
			
			<h1><a class="anchor" name="_Toc369865016a"></a>Signaler un problème sur une enquête<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h1>
			<p>Si une enquête contient un contenu illégal ou viole les droits d'autrui (y compris les droits de propriété intellectuelle, le droit de la concurrence et le droit général), veuillez utiliser le lien : "Signaler un problème avec cette enquête" dans la partie droite de l’écran.</p>
			<p>Veuillez-vous référer aux  <a href="${contextpath}/home/tos">conditions d’utilisation</a> pour plus d'informations à ce sujet.</p>
			
			<h1><a class="anchor" name="_Toc369865017"></a>Après avoir soumis mes réponses</h1>
			
			<h2><a class="anchor" name="_Toc369865018"></a>Puis-je visualiser/imprimer mes réponses une fois que je les ai soumises?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			
			<p>Oui. Juste après la réception d'une contribution, le système propose une option d'impression.</p>
			
			<h2><a class="anchor" name="_Toc369865019"></a>Puis-je sauvegarder mes réponses en format PDF?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			
			<p>Le système proposera une version PDF de votre contribution à télécharger, juste après que vous l'ayez soumise.</p>
			
			<h2><a class="anchor" name="_Toc369865020"></a>Puis-je modifier mes réponses une fois que je les ai soumises?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			
			<p>Tout dépend de la façon dont l'enquête a été configurée.</p>
			<p>Certaines enquêtes permettent <a target="_blank" href="${contextpath}/home/editcontribution">de réaccéder à une contribution après soumission</a>, tandis que d'autres n'offrent pas cette possibilité.
			</p>
			
			<h2><a class="anchor" name="_Toc369865021"></a>Je viens de répondre à une enquête. Puis-je consulter les réponses des autres participants?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			
			<p>Tout dépend de la façon dont l'enquête a été configurée.</p>
			<p>Si aucun lien ne s'affiche vers les résultats publiés une fois que vous avez soumis votre contribution, il se peut que cette fonctionnalité ne soit pas disponible.</p>
			<p>Si vous estimez que les résultats d'une enquête pourraient présenter un intérêt public, veuillez contacter <a href="#_Toc1">l'auteur de l'enquête</a>.</p>
				
			<h2><a class="anchor" name="_Toc369865022"></a>Mon visionneur de fichiers PDF génère le message d'erreur suivant: &quot;Insufficient Image data&quot; (données d'image insuffisantes)<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			
			<p>Le téléversement d'un fichier d'image corrompu peut compromettre son affichage et provoquer une erreur interne du visionneur de fichiers PDF.</p>
			<p>Pour résoudre ce problème, vous devez soit réparer le fichier image, soit le retirer.</p>
			
			<h2><a class="anchor" name="_Toc369865023"></a>Pourquoi de petits carrés apparaissent-ils dans l'enquête exportée au format PDF?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			
			<p>Cela peut se produire si les polices de caractères utilisées par l'auteur de l'enquête ou les participants ne sont pas compatibles avec l'application.</p>
			<p>Si un caractère donné n'est pas présent sur l'ordinateur, l'application le remplace par un petit carré qui indique que ce caractère n'est pas compatible avec le moteur de création de PDF.</p>
			<p>N'hésitez pas à signaler aux personnes de contact, indiquées dans la zone prévue à cet effet, que des caractères non compatibles ont été utilisés.</p>
			<p>Remarque: ce problème n'a aucune incidence sur votre contribution. Une fois celle-ci enregistrée, elle peut facilement être visualisée et exportée par l'autorité chargée de l'enquête, même si la fonction PDF de l'application n'affiche pas correctement votre PDF.</p>
	
			<h2><a class="anchor" name="_Toc369865028"></a>O&ugrave; puis-je trouver mes r&eacute;ponses qui sont enregistr&eacute;s comme brouillon?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Top of the page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			<p>Apr&egrave;s avoir cliqu&eacute; sur &laquo;Enregistrer comme brouillon&raquo;, vous serez automatiquement redirig&eacute; vers une page indiquant le lien o&ugrave; vous pouvez r&eacute;cup&eacute;rer votre bouillon afin de l'&eacute;diter et soumettre vos r&eacute;ponses. <b>Veuillez enregistrer ce lien!</b> Vous pouvez l'envoyer par email, l'enregistrer dans vos favoris ou le copier dans le presse-papier &laquo;clipboard&raquo;.</p>
			
			<h1><a class="anchor" name="_Toc369865024"></a>Protection de la vie privée</h1>
			<h2><a class="anchor" name="_Toc369865025"></a>Ce système utilise des cookies. Quelles informations y sont enregistrées?<a href="#topAnchor"  class="anchorlink anchorTop" style="text-decoration:none;">Haut de la page&nbsp;<i class="icon icon-chevron-up"></i></a></h2>
			
			<p>Le système utilise des &quot;cookies&quot; (ou témoins) de session afin d’assurer une bonne communication entre le client (votre ordinateur) et le serveur. Votre navigateur doit donc être configuré pour les accepter. Les cookies disparaissent une fois la session terminée.</p>
			<p>Le système enregistre une copie de sauvegarde locale de votre contribution à une enquête. Ce fichier peut servir en cas d'indisponibilité du serveur lorsque vous envoyez votre contribution, ou si votre ordinateur s'éteint accidentellement, par exemple.
			Il contient le numéro des questions et les brouillons de vos réponses. Une fois votre contribution à l’enquête envoyée au serveur, ou après sauvegarde d'un brouillon sur celui-ci, ces données locales sont supprimées.
			Au-dessus de l'enquête figure une case à cocher avec la mention &quot;Enregistrer une copie de sauvegarde locale sur votre ordinateur (décochez cette case si vous utilisez un ordinateur public/partagé)&quot;, qui vous permet de désactiver cette fonction.
			Si vous la décochez, aucune donnée ne sera conservée sur votre ordinateur.</p>
			
			
		</div>
	</div>

	<%@ include file="../footer.jsp" %>		

</body>
</html>