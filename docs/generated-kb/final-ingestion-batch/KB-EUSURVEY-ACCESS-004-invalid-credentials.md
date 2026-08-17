# How to resolve Invalid credentials when accessing EUSurvey

## Intent / Description

Explains what to do when a user sees an "Invalid credentials" error when trying to log in to EUSurvey.

## Applies To

* Role(s): Survey Manager, Respondent
* Feature: Authentication, Login
* Context: A user enters their credentials and receives an "Invalid credentials" error

## Short Answer

The "Invalid credentials" error means the username/email or password entered does not match any active account. This may be because:

1. **Incorrect password** — The password is wrong. Passwords are case-sensitive.
2. **Incorrect username or email** — A typo in the login identifier.
3. **Account not validated** — The account was created but the email validation was not completed.
4. **Account does not exist** — No account exists with the provided credentials.
5. **Account locked due to failed attempts** — Too many incorrect login attempts may temporarily lock the account.
6. **EU Login account vs local account confusion** — The user is trying to log in with EU Login credentials on the local login form (or vice versa).

## Steps / Procedure

1. **Double-check your credentials**: Verify the email/username and password. Check for typos, extra spaces, and correct case.
2. **Use the correct login method**: If you have an EU Login account, use the EU Login button, not the local login form.
3. **Reset your password**: If you forgot your password, use the "Forgot password" link on the login page.
4. **Check email validation**: If you recently created an account, check your email for a validation link and click it to activate your account.
5. **Wait and retry**: If your account may be temporarily locked due to failed attempts, wait several minutes before trying again.
6. **Try a different browser**: Ensure your browser is not auto-filling with old credentials.

## Important Conditions / Limitations

* **EU Login is separate**: EU Login (ECAS) credentials are managed outside EUSurvey. Password resets for EU Login must be done through the EU Login portal, not EUSurvey.
* **Local accounts**: Some EUSurvey instances support local accounts with separate credentials managed within EUSurvey.
* **Password strength requirements**: When resetting a password, the new password must meet security requirements.
* **Account locking**: After multiple failed attempts, the account may be temporarily locked for security.
* **Case-sensitive passwords**: Passwords are case-sensitive. Usernames/emails may not be.

## Troubleshooting / Related Cases

* If you are an EU staff member: use the EU Login button and log in with your EU Login credentials. Do not use the local login form.
* If password reset does not work: ensure you are using the correct email address associated with the account.
* If you never had an account: you need to register first (if registration is available) or use EU Login.

## Out of Scope / Separate Topics

* EU Login account not recognised by EUSurvey (see: KB-EUSURVEY-ACCESS-005)
* EU Login second-factor methods (see: KB-EUSURVEY-ACCESS-006)
* 403 Forbidden error (see: KB-EUSURVEY-ACCESS-001)
* API authentication (see: WS-002)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation
* For EU Login issues: https://webgate.ec.europa.eu/cas/

## Retrieval Metadata

* business_domain: access_authentication
* user_role: survey_manager, respondent
* feature: login, authentication
* tags: invalid credentials, wrong password, login failed, cannot login, authentication error
* synonyms: login not working, password wrong, cannot sign in, invalid username or password, login error EUSurvey
* product_terms: login, EU Login, password, credentials, Forgot password, validation
* exclude: 403 errors, access denied to survey, API authentication, invitation access
