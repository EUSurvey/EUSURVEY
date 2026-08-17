# SRC-005 — How do I share my address book?

## Intent / Description

This article explains how to share your address book contacts with other EUSurvey users.

## Applies To

* Role(s): Registered User
* EUSurvey area: Contacts
* Environment: All
* Article type: How-To
* UI location: Settings > Shares
* Backend location: SettingsController.shares

## Short Answer

To share your address book, navigate to Settings > Shares and create a new share. Select the recipient user and choose what contacts to share. Shared contacts become available to the recipient for use in their guest lists.

## Prerequisites / Required Permissions

* Authenticated user
* Contacts must exist in your address book

## Procedure

1. Navigate to Settings > Shares.
2. Click 'Create new Share'.
3. Search for the recipient user.
4. Select contacts to share.
5. Configure share settings.
6. Save the share.

## Important Conditions / Limitations

* Shares allow other users to access your contacts for invitations.
* The recipient can use shared contacts in their guest lists.
* Shares can be read-only or read-write.
* You can view shares you have created and shares received from others.
* Shares can be edited or deleted after creation.
* There is a rate limit on share creation.

## Troubleshooting

* 'You have exceeded the number of shares per hour': Wait before creating more shares.
* 'Share not found': The share may have been deleted by the creator.

## Related Articles

* ES-028 — How do I manage the address book?
* ES-025 — How do I create a guest list?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/WEB-INF/views/settings/shares.jsp, src/main/webapp/resources/js/shares.js
* Backend files: src/main/java/com/ec/survey/controller/SettingsController.java, src/main/java/com/ec/survey/service/AttendeeService.java
* Classes: SettingsController, AttendeeService
* Methods: shares, sharesPOST, shareEdit, createStaticShare
* Routes: GET /settings/shares, POST /settings/shares
* Message keys: label.Shares, label.CreateNewShare, label.MyShares, label.ReceivedShares, error.UsersTooOftenShares, error.ShareNotFound, error.ShareUnauthorized
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Contacts
* EUSurvey area: Settings
* Feature: Share Address Book
* User intent: How do I share my address book?
* Article type: How-To
* User type: Registered User
* Required permission: Registered User
* Survey status: N/A
* Environment: All
* Keywords: share, contacts, address book, collaborate
* Synonyms: share contacts, give access to address book, collaborate on contacts
* Acronyms: N/A
* Related entities: Attendee, User
* Security / privacy relevance: Sharing exposes contact personal data to recipients
* Search boost terms: share address book, share contacts
* Source files: src/main/java/com/ec/survey/controller/SettingsController.java, src/main/java/com/ec/survey/service/AttendeeService.java
* Duplicate status: New
