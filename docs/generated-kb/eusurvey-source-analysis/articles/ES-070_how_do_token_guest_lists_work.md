# ES-070 — How do token guest lists work?

## Intent / Description

This article explains how token-based guest lists provide anonymous access control.

## Applies To

* Role(s): Invitation Manager
* EUSurvey area: Participants
* Environment: All
* Article type: Concept
* UI location: Participants page
* Backend location: ParticipantsController.java

## Short Answer

Token guest lists generate unique codes as anonymous invitation links. Each token creates a unique URL. Tokens can be activated, deactivated, or deleted. They provide access control without requiring contact information.

## Prerequisites / Required Permissions

* ManageInvitations privilege or survey ownership

## Procedure

The analysed source code does not provide a complete user-facing procedure.

## Important Conditions / Limitations

* Each token generates a unique URL.
* Maximum 1 million tokens per list.
* Tokens individually activatable/deactivatable.
* Anonymous - no email required.
* Single-use (one submission per token).
* Can be exported for distribution.
* New tokens can be added to existing lists.

## Troubleshooting

* Token limit exceeded: Reduce count or create new list.
* Token link not working: May be deactivated or already used.

## Related Articles

* ES-025 — How do I create a guest list?
* ES-026 — How do I send invitations to participants?
* ES-031 — How do I secure my survey?

## Evidence / Source Traceability

* Frontend: N/A
* Backend: src/main/java/com/ec/survey/controller/ParticipantsController.java, src/main/java/com/ec/survey/tools/TokenCreator.java, src/main/java/com/ec/survey/service/ParticipationService.java
* Classes: ParticipantsController, TokenCreator, ParticipationService
* Methods: createTokens, participantsActivate, participantsDeactive
* Routes: POST /{shortname}/management/participants
* Message keys: label.CreateNewTokenGuestlist, label.CreateTokens, label.AddTokens, info.AddTokens, info.TokenList, label.ActivateSelectedTokens, label.DeactivateSelectedTokens, error.MaxTokenNumberExceeded
* Config keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Participants
* EUSurvey area: Invitations
* Feature: Token Lists
* User intent: How do token guest lists work?
* Article type: Concept
* User type: Invitation Manager
* Required permission: Invitation Manager
* Survey status: Any
* Environment: All
* Keywords: token, guest list, anonymous, unique, code, link
* Synonyms: token links, anonymous invitations, unique access codes
* Acronyms: N/A
* Related entities: ParticipationGroup, Invitation
* Security / privacy relevance: Tokens enable anonymous access
* Search boost terms: token guest lists, anonymous invitations, unique links
* Source files: src/main/java/com/ec/survey/controller/ParticipantsController.java, src/main/java/com/ec/survey/tools/TokenCreator.java, src/main/java/com/ec/survey/service/ParticipationService.java
* Duplicate status: New
