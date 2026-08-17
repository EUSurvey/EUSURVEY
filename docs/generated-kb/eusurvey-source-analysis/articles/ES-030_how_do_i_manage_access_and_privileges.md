# ES-030 — How do I manage access and privileges?

## Intent / Description

This article explains how to grant other users access to manage a survey.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Access Control
* Environment: All
* Article type: How-To
* UI location: Access page
* Backend location: ManagementController.access

## Short Answer

To manage access, navigate to the survey's Access page. Here you can add users or departments and assign specific privileges: Form Management, Access Results, Manage Invitations, or Access Draft. Each privilege level controls what the user can do with the survey.

## Prerequisites / Required Permissions

* The user must be the survey owner

## Procedure

1. Navigate to the survey management area.
2. Go to the Access/Privileges page.
3. Click 'Add User' to grant access.
4. Search for the user by login or email.
5. Select the user from search results.
6. Assign privilege levels: Form Management, Access Results, Manage Invitations, Access Draft.
7. Click OK to save.
8. The user now has the assigned access to the survey.

## Important Conditions / Limitations

* Four local privilege types: AccessDraft, AccessResults, FormManagement, ManageInvitations.
* Privileges can be set to No access, Read, or Read/Write.
* Users can be added individually or by department.
* The survey owner always has full access.
* Privileges can be updated or removed at any time.
* Adding a user as Form Manager gives them edit access to the survey structure.
* Result Access can be read-only (view only) or read/write (view and delete contributions).

## Troubleshooting

* User not found in search: Ensure you are searching in the correct domain (EU Login vs External).
* 'Please select a user': Make sure to select a result from the search before confirming.

## Related Articles

* ES-031 — How do I secure my survey?
* ES-006 — How do I edit a survey?
* ES-016 — How do I view survey results?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/resources/js/access.js
* Backend files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/model/Access.java, src/main/java/com/ec/survey/service/SurveyService.java
* Classes: ManagementController, Access, SurveyService
* Methods: access, accessPOST, addUser, addUserEmail, removeUser
* Routes: GET /{shortname}/management/access, POST /{shortname}/management/access
* Message keys: label.Privileges, label.AddUser, label.FormManagement, label.ManageInvitations, label.ReadingAccess, label.ReadWriteAccess, info.AddUserAccess
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Access Control
* EUSurvey area: Privileges
* Feature: Manage Privileges
* User intent: How do I manage access and privileges?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Any
* Environment: All
* Keywords: access, privileges, permissions, users, share, grant
* Synonyms: grant access, add user, manage permissions, share survey
* Acronyms: N/A
* Related entities: Access, User, LocalPrivilege
* Security / privacy relevance: Controls who can access survey data
* Search boost terms: manage access, grant privileges, add users to survey
* Source files: src/main/java/com/ec/survey/controller/ManagementController.java, src/main/java/com/ec/survey/model/Access.java, src/main/java/com/ec/survey/service/SurveyService.java
* Duplicate status: New
