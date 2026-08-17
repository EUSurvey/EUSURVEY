# Why EUSurvey does not recognise an EU Login account

## Intent / Description

Explains why a user with a valid EU Login account may be unable to access EUSurvey or why EUSurvey does not recognise their EU Login session.

## Applies To

* Role(s): Survey Manager, Respondent
* Feature: EU Login integration, Authentication
* Context: A user logs in successfully via EU Login but EUSurvey does not grant access or does not recognise the account

## Short Answer

A valid EU Login account may not be recognised by EUSurvey because:

1. **First-time access** — The user has never used EUSurvey before. Depending on configuration, a first login may create an account automatically, or the user may need to complete an additional registration step.
2. **Account type restriction** — The EUSurvey instance may restrict access to specific EU Login account types (e.g. only EU institutional accounts).
3. **Account not linked** — The EU Login account is valid but not yet associated with an EUSurvey profile.
4. **Session issue** — The EU Login session expired or was not properly transferred to EUSurvey. Try logging out and in again.
5. **Browser issue** — Cookies or session storage problems prevent the authentication handshake from completing.

## Steps / Procedure

1. **Try logging in again**: Go to the EUSurvey homepage and click the EU Login button. Complete the EU Login authentication flow.
2. **Ensure cookies are enabled**: EUSurvey requires cookies for session management. Enable cookies for the EUSurvey and EU Login domains.
3. **Clear browser cache and cookies**: Then try the login flow again from scratch.
4. **Try a different browser**: Rule out browser-specific issues.
5. **Check your account type**: If EUSurvey requires an institutional EU Login account, a personal/external EU Login account may not be accepted.
6. **Check for transition restrictions**: EUSurvey is transitioning to focus on EU institutional use. External accounts may have limited access.
7. **Contact support**: If the issue persists, provide your EU Login email and the exact error or behaviour.

## Important Conditions / Limitations

* **EU Login is external to EUSurvey**: EU Login is a separate authentication service. EUSurvey trusts EU Login for identity verification but may apply additional access policies.
* **Account creation may be automatic**: In many configurations, logging in via EU Login for the first time automatically creates an EUSurvey account.
* **External user restrictions**: EUSurvey may restrict survey creation for external (non-EU institution) accounts while still allowing survey participation.
* **Multiple EU Login accounts**: If you have multiple EU Login accounts, ensure you are using the correct one.
* **Browser redirection**: The authentication flow involves redirections between EU Login and EUSurvey. Browser security settings, extensions, or corporate proxies may interfere.

## Troubleshooting / Related Cases

* If you see a blank page after EU Login redirect: check for browser extension interference or pop-up blocking.
* If EU Login succeeds but EUSurvey shows an error: the account type may not be accepted. Contact support.
* If you are an EU staff member and it does not work: ensure you are using your institutional EU Login, not a personal one.

## Out of Scope / Separate Topics

* How to resolve invalid credentials (local login) (see: KB-EUSURVEY-ACCESS-004)
* EU Login second-factor methods (see: KB-EUSURVEY-ACCESS-006)
* How participants connect to EUSurvey (see: PM-01_02)
* EUSurvey transition for external users (see: SM-16 series)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation
* For EU Login issues: https://webgate.ec.europa.eu/cas/

## Retrieval Metadata

* business_domain: access_authentication
* user_role: survey_manager, respondent
* feature: eu_login_integration
* tags: EU Login not recognised, account not found, EU Login rejected, ECAS not working
* synonyms: EUSurvey does not accept my EU Login, logged in but no access, EU Login session not recognised, ECAS account not linked
* product_terms: EU Login, ECAS, authentication, account type, institutional
* exclude: invalid credentials (local login), 403 error, survey access restrictions, invitation access
