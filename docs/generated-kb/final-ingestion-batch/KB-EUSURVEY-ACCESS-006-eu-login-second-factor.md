# Which EU Login second-factor methods can be used with EUSurvey

## Intent / Description

Explains which EU Login two-factor authentication (2FA) methods are supported when accessing EUSurvey, and what to do if 2FA fails.

## Applies To

* Role(s): Survey Manager, Respondent
* Feature: EU Login authentication, Two-factor authentication
* Context: A user needs to use 2FA to access EUSurvey or encounters issues with their 2FA method

## Short Answer

EUSurvey uses EU Login for authentication and supports whatever second-factor methods EU Login itself offers. The available 2FA methods are determined by EU Login, not by EUSurvey. Common EU Login 2FA methods include:

- EU Login Mobile App
- SMS code
- Hardware token (for institutional users)

The specific methods available to you depend on your EU Login account configuration and your organisation's security policy.

## Steps / Procedure

1. Go to EUSurvey and click **EU Login** to authenticate.
2. Enter your EU Login credentials (username and password).
3. When prompted for the second factor, use one of the methods you have configured in your EU Login account.
4. After successful 2FA, you will be redirected back to EUSurvey.

**If your 2FA method is not working:**

1. Ensure your 2FA device (phone, token) is working correctly.
2. Check that the time on your device is synchronised (important for app-based codes).
3. If using SMS: ensure your phone number is correct in EU Login settings and that you have mobile reception.
4. If using the EU Login Mobile App: ensure the app is updated and properly configured.
5. If you lost access to your 2FA device: use EU Login's account recovery process.
6. Contact the EU Login helpdesk for 2FA issues — these are managed by EU Login, not EUSurvey.

## Important Conditions / Limitations

* **2FA is managed by EU Login**: EUSurvey does not control which 2FA methods are available. All configuration is done in the EU Login portal.
* **EUSurvey cannot bypass 2FA**: If EU Login requires 2FA, EUSurvey cannot override this requirement.
* **Institutional policies may require 2FA**: Some organisations mandate 2FA for all EU Login authentications.
* **External users**: External (non-EU institution) EU Login accounts may have different 2FA options compared to institutional accounts.
* **EUSurvey-specific enforcement**: EUSurvey can be configured to require enhanced (2FA) authentication for certain surveys. If a survey specifically requires 2FA, you must complete the second factor to access it.

## Troubleshooting / Related Cases

* If you see a "weak authentication" error: the survey requires a stronger authentication level (2FA). Ensure your EU Login account has a second factor configured and use it.
* If your 2FA device was lost or changed: recover access through the EU Login self-service portal or your organisation's IT helpdesk.
* If 2FA succeeds at EU Login but EUSurvey shows an error: this is likely a session or account recognition issue (see: KB-EUSURVEY-ACCESS-005).

## Out of Scope / Separate Topics

* EU Login account not recognised by EUSurvey (see: KB-EUSURVEY-ACCESS-005)
* Invalid credentials error (see: KB-EUSURVEY-ACCESS-004)
* How to configure EU Login security for a survey (see: SM-44)
* How participants connect to EUSurvey (see: PM-01_02)

## Documentation / Support Path

* EU Login self-service: https://webgate.ec.europa.eu/cas/
* For EU Login 2FA issues, contact the EU Login helpdesk.
* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: access_authentication
* user_role: survey_manager, respondent
* feature: two_factor_authentication
* tags: 2FA, second factor, two-factor authentication, EU Login mobile app, SMS code, authentication level
* synonyms: two factor login EUSurvey, 2FA not working, second factor methods, strong authentication, enhanced authentication
* product_terms: EU Login, 2FA, second factor, EU Login Mobile App, weak authentication
* exclude: local login, invalid credentials, API authentication, survey access restrictions
