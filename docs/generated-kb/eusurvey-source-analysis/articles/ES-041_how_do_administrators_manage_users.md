# ES-041 — How do administrators manage users?

## Intent / Description

This article explains how system administrators manage user accounts.

## Applies To

* Role(s): Administrator
* EUSurvey area: Administration
* Environment: All
* Article type: How-To
* UI location: Administration > Users
* Backend location: UserController

## Short Answer

Administrators can manage user accounts from Administration > Users. This includes creating users, editing user details, assigning roles, banning/unbanning users, and deleting accounts.

## Prerequisites / Required Permissions

* UserManagement global privilege (ROLE_USER_ADMIN or ROLE_RIGHT_ADMIN)

## Procedure

1. Navigate to Administration > Users.
2. View and search existing users.
3. Click on a user to edit details.
4. Create new users with the Create button.
5. Assign roles to control system-wide permissions.
6. Ban or unban users as needed.
7. Delete user accounts when required.

## Important Conditions / Limitations

* User management requires UserManagement global privilege.
* Users can have multiple roles assigned.
* Banning a user blocks their login access.
* User deletion may fail if references to the user still exist.
* Administrators can filter users by role, language, email, login.
* User accounts can be of different types: system, ECAS, regular.

## Troubleshooting

* 'The deletion of this user is not possible': Remove all references to the user first (surveys, contributions, etc.).
* 'This login already exists': Choose a different login for new users.

## Related Articles

* ES-045 — How do administrators ban a user?
* ES-042 — How do administrators search surveys?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/WEB-INF/views/administration/users.jsp
* Backend files: src/main/java/com/ec/survey/controller/UserController.java, src/main/java/com/ec/survey/service/AdministrationService.java
* Classes: UserController, AdministrationService
* Methods: users, createUser, updateUser, deleteUser
* Routes: GET /administration/users, POST /administration/users
* Message keys: label.Administration, label.AddUser, error.LoginExists, error.UserNotFound, info.userreferenceserror
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Administration
* EUSurvey area: User Management
* Feature: User Management
* User intent: How do administrators manage users?
* Article type: How-To
* User type: Administrator
* Required permission: Administrator
* Survey status: N/A
* Environment: All
* Keywords: users, admin, manage, accounts, roles
* Synonyms: manage users, user administration, create accounts
* Acronyms: N/A
* Related entities: User, Role
* Security / privacy relevance: User accounts contain personal data
* Search boost terms: admin user management, manage accounts
* Source files: src/main/java/com/ec/survey/controller/UserController.java, src/main/java/com/ec/survey/service/AdministrationService.java
* Duplicate status: New
