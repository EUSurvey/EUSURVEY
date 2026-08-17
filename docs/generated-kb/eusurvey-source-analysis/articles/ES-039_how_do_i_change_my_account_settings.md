# ES-039 — How do I change my account settings?

## Intent / Description

This article explains how to modify user account settings like password, email, and language.

## Applies To

* Role(s): Registered User
* EUSurvey area: User Account
* Environment: All
* Article type: How-To
* UI location: Settings > My Account
* Backend location: SettingsController.myAccount

## Short Answer

Navigate to Settings > My Account to change your password, email address, display language, and pivot language for translations. You can also request account deletion from this page.

## Prerequisites / Required Permissions

* Authenticated user

## Procedure

1. Navigate to Settings > My Account.
2. Change desired settings: password, email, language, pivot language.
3. For password change: enter old password, new password, and confirmation.
4. For email change: enter new email and repeat.
5. Click Save.

## Important Conditions / Limitations

* Password must be between 8 and 16 characters with at least one digit and one non-alphanumeric character.
* Email change requires entering the new address twice for confirmation.
* Language setting controls the interface language.
* Pivot language controls the reference language shown during translation editing.
* Account deletion can be requested (requires confirmation within a time limit).

## Troubleshooting

* 'The two passwords do not match': Ensure new password and confirmation are identical.
* 'Please choose a password between 8 and 16 characters...': Password does not meet complexity requirements.
* 'The e-mail addresses do not match': Re-enter the new email address identically in both fields.
* 'The password is wrong': The old password entered is incorrect.

## Related Articles

* ES-040 — How do I share my address book?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/WEB-INF/views/settings/myAccount.jsp
* Backend files: src/main/java/com/ec/survey/controller/SettingsController.java, src/main/java/com/ec/survey/service/AdministrationService.java
* Classes: SettingsController, AdministrationService
* Methods: myAccount, myAccountPOST, changePassword, changeEmail, changeLanguage
* Routes: GET /settings/myAccount, POST /settings/myAccount
* Message keys: label.MyAccount, label.NewPassword, label.OldPassword, error.PasswordsDontMatch, error.PasswordWeak, error.EmailsDontMatch, error.WrongPassword, info.PasswordChanged, info.EmailChanged
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: User Account
* EUSurvey area: Settings
* Feature: Account Settings
* User intent: How do I change my account settings?
* Article type: How-To
* User type: Registered User
* Required permission: Registered User
* Survey status: N/A
* Environment: All
* Keywords: account, password, email, language, settings, profile
* Synonyms: change password, update email, account settings
* Acronyms: N/A
* Related entities: User
* Security / privacy relevance: Contains personal account data
* Search boost terms: account settings, change password, update email
* Source files: src/main/java/com/ec/survey/controller/SettingsController.java, src/main/java/com/ec/survey/service/AdministrationService.java
* Duplicate status: New
