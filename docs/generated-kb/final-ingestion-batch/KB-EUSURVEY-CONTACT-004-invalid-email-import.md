# Why EUSurvey reports an invalid email address during contact import

## Intent / Description

Explains the validation rules applied to email addresses during Address Book import and common reasons for rejection.

## Applies To

* Role(s): Survey Manager
* Feature: Address Book import, Email validation
* Context: During contact import, one or more rows are rejected because the email address is reported as invalid

## Short Answer

EUSurvey validates email addresses during import using standard email format rules. An email is rejected if it:

- Contains leading or trailing whitespace
- Is missing the @ symbol
- Has an invalid domain part (no dot, empty domain)
- Contains unsupported characters (spaces, special characters outside standard email format)
- Is empty or blank
- Contains multiple email addresses in one field
- Has the wrong column mapped as the email field

## Steps / Procedure

1. **Check the rejected rows**: Note which contacts were skipped and examine their email values.
2. **Common fixes**:
   - Remove leading/trailing spaces from email values.
   - Ensure each cell contains only ONE email address (not multiple separated by semicolons or commas).
   - Verify the @ symbol is present and the domain has a dot (e.g. `user@domain.com`).
   - Remove any special characters not valid in emails.
   - Ensure the email column is correctly mapped during import.
3. **Re-import**: After fixing the file, re-import the corrected contacts.

## Important Conditions / Limitations

* **Standard email format validation**: EUSurvey checks for RFC-compliant email format (user@domain structure with valid characters).
* **No mailbox verification**: Validation only checks format, not whether the email address actually exists or can receive mail.
* **Whitespace is not trimmed automatically**: Leading or trailing spaces in the email field cause rejection.
* **One email per field**: Multiple addresses separated by commas or semicolons in a single cell will be treated as one invalid address.
* **UTF-8 encoding**: Non-ASCII characters in email addresses may be rejected depending on the validation implementation.
* **Case-insensitive**: Email validation is typically case-insensitive. `User@Domain.com` and `user@domain.com` are treated as the same address.
* **Domain-only check**: The system checks that the domain part looks valid (has a dot separator) but does not perform DNS lookups.

## Troubleshooting / Related Cases

* If emails look correct but are rejected: check for invisible characters. Open the file in a plain text editor and look for non-printable characters before/after the email.
* If all emails are rejected: the email column may be mapped to the wrong field. Verify the column mapping during import.
* If specific emails with unusual domains are rejected: check for uncommon characters in the domain.
* If emails with accented characters are rejected: these may not be supported depending on the validation rules.

## Out of Scope / Separate Topics

* Why contacts are skipped during import (general) (see: KB-EUSURVEY-INVITE-001)
* How to troubleshoot import failures without errors (see: KB-EUSURVEY-CONTACT-002)
* How to import contacts (see: SM-103)
* How to update existing contacts (see: KB-EUSURVEY-CONTACT-005)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: contacts_invitations
* user_role: survey_manager
* feature: email_validation, address_book_import
* tags: invalid email, email validation error, email rejected during import, bad email format
* synonyms: why is my email invalid, email not accepted during import, contact email rejected, email format error import
* product_terms: Address Book, Import, email, validation, CSV
* exclude: email delivery issues, invitation sending, spam filtering
