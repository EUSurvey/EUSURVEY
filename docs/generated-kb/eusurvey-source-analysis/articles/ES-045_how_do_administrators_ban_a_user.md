# ES-045 — How do administrators ban a user?

## Intent / Description

This article explains how administrators ban a user account to prevent login access.

## Applies To

* Role(s): Administrator
* EUSurvey area: Administration
* Environment: All
* Article type: How-To
* UI location: Administration > Users
* Backend location: UserController.banuser

## Short Answer

To ban a user, navigate to Administration > Users, find the user, and click 'Ban'. Banning prevents the user from logging in and shows them a notification message. Banned users can be unbanned later.

## Prerequisites / Required Permissions

* UserManagement global privilege

## Procedure

1. Navigate to Administration > Users.
2. Find the user to ban.
3. Click 'Ban' or 'Ban/Unban'.
4. Confirm the ban action.
5. The user is immediately blocked from logging in.

## Important Conditions / Limitations

* Banning immediately blocks the user's login access.
* A configurable email message is sent to the banned user.
* Banned users' email addresses cannot be used for new registrations.
* Banning does not delete the user's surveys or contributions.
* The ban can be reversed using the 'Unban' action.
* Administrators can configure the ban/unban email messages from System settings.

## Troubleshooting

* Banned user still accessing system: The ban takes effect on next login attempt, not active sessions.

## Related Articles

* ES-041 — How do administrators manage users?
* ES-043 — How do administrators freeze a survey?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/WEB-INF/views/administration/users.jsp
* Backend files: src/main/java/com/ec/survey/controller/UserController.java, src/main/java/com/ec/survey/service/AdministrationService.java
* Classes: UserController, AdministrationService
* Methods: banuser, unbanuser
* Routes: POST /administration/users/banuser, POST /administration/users/unbanuser
* Message keys: label.BanUser, label.BanUnban, label.MessageBannedUser, label.MessageBanningUser, error.EmailBanned, label.confirmfreezeuser, label.ConfigureBanUserMessage
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Administration
* EUSurvey area: User Management
* Feature: Ban User
* User intent: How do administrators ban a user?
* Article type: How-To
* User type: Administrator
* Required permission: Administrator
* Survey status: N/A
* Environment: All
* Keywords: ban, block, user, account, suspend
* Synonyms: block user, suspend account, ban login
* Acronyms: N/A
* Related entities: User
* Security / privacy relevance: Affects user access
* Search boost terms: ban user, block account, suspend login
* Source files: src/main/java/com/ec/survey/controller/UserController.java, src/main/java/com/ec/survey/service/AdministrationService.java
* Duplicate status: New
