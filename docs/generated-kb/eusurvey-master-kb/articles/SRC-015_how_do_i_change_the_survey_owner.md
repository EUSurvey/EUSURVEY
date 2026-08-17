# SRC-015 — How do I change the survey owner?

## Intent / Description

This article explains how to transfer survey ownership to another user.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Survey Management
* Environment: All
* Article type: How-To
* UI location: Properties page
* Backend location: ManagementController.java

## Short Answer

Transfer ownership via Properties > Change owner. Enter the new owner login. A request email is sent. If accepted, ownership transfers and they gain full control.

## Prerequisites / Required Permissions

* Survey owner

## Procedure

1. Open survey Properties.
2. Click Change owner.
3. Enter the new owner login.
4. Confirm.
5. Request email sent to new owner.
6. New owner accepts or rejects.
7. If accepted, ownership transfers.

## Important Conditions / Limitations

* New owner must be a valid user.
* Email request sent for acceptance.
* If new owner is form manager, removed from that list.
* Outdated requests auto-deleted.
* Admin can change directly.

## Troubleshooting

* 'No user with this login': Verify username.
* Transfer not completing: New owner must accept via email link.

## Related Articles

* ES-030 — How do I manage access and privileges?
* ES-007 — How do I configure survey properties?
* ES-048 — How do I perform bulk operations?

## Evidence / Source Traceability

* Frontend: N/A
* Backend: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/service/SurveyService.java
* Classes: ManagementController, SurveyService
* Methods: changeOwner, sendChangeOwnerRequest, acceptOwnershipRequest, rejectOwnershipRequest
* Routes: POST /{shortname}/management/properties
* Message keys: label.ChangeOwner, label.changeOwnership, info.changeOwnership, info.OwnerChanged, error.unknownowner, error.OwnerNotValid
* Config keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Management
* EUSurvey area: Ownership
* Feature: Change Owner
* User intent: How do I change the survey owner?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Any
* Environment: All
* Keywords: owner, transfer, change, ownership
* Synonyms: transfer ownership, change survey owner, assign new owner
* Acronyms: N/A
* Related entities: Survey, User
* Security / privacy relevance: None
* Search boost terms: change owner, transfer ownership
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/service/SurveyService.java
* Duplicate status: New
