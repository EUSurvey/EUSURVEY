# How to personalise EUSurvey invitation emails

## Intent / Description

Explains how survey owners can personalise invitation emails using placeholders that are replaced with contact-specific information for each recipient.

## Applies To

* Role(s): Survey Manager
* Feature: Invitation templates, Placeholders
* Context: A survey owner wants each invitation email to address the recipient by name or include other personalised information

## Short Answer

EUSurvey supports placeholders in invitation email subjects and body text. When an invitation is sent, the placeholders are replaced with the corresponding attribute values for each recipient.

The supported placeholders are:

- **{Name}** — the contact's name
- **{Email}** — the contact's email address
- **{host}** — the EUSurvey platform URL
- **{AttributeName}** — any custom attribute defined in the Address Book (using the exact attribute name)

Placeholders use the format `{AttributeName}` with curly braces.

## Steps / Procedure

1. Open your survey and go to **Participants**.
2. Select the participation group and click to send invitations.
3. In the **Subject** field and/or the **text before/after the link**, insert placeholders where you want personalised content. For example:
   - Subject: `Invitation for {Name}`
   - Body: `Dear {Name}, you are invited to participate in our survey.`
4. **Preview** the invitation to verify that placeholders will be replaced correctly.
5. Select recipients and send.

**Using custom attributes:**

1. If you have defined custom attributes in the Address Book (e.g. "Organisation", "Department"), you can use them as placeholders.
2. Insert `{Organisation}` or `{Department}` in the email text.
3. The placeholder name must exactly match the attribute name defined in the Address Book.

## Important Conditions / Limitations

* **Exact match required**: The placeholder name in curly braces must exactly match an attribute name. If no matching attribute is found, the placeholder is replaced with an empty string.
* **Built-in placeholders**: `{Name}`, `{Email}`, `{name}`, and `{host}` are always available regardless of custom attributes.
* **Case-sensitive**: `{Name}` and `{name}` both work for the contact's name, but custom attributes must match the exact case of the defined attribute name.
* **No conditional logic**: There is no support for conditional text (e.g. "if attribute X exists, show Y"). Empty attributes result in blank replacements.
* **Subject and body**: Placeholders work in both the subject line and the email body text.
* **Preview shows one recipient**: The preview function shows how the email will look for the first or selected contact. Verify placeholders are being replaced.
* **HTML in templates**: The email body supports HTML formatting, but placeholder values are inserted as plain text.

## Troubleshooting / Related Cases

* If a placeholder shows as `{AttributeName}` in the received email: the attribute name does not match any defined attribute for that contact. Check the exact spelling and case.
* If the Name placeholder is empty: the contact record has no name set. Verify the Address Book entry.
* If you want to include the survey link separately: the invitation link is automatically inserted between the "text before" and "text after" fields. You do not need a placeholder for it.

## Out of Scope / Separate Topics

* How to select the language of invitation emails (see: KB-EUSURVEY-INVITE-007)
* How to use contact attributes in the Address Book (see: KB-EUSURVEY-CONTACT-003)
* How to manage contacts and attributes (see: SM-100, SM-104)
* What a registration form is (see: SM-102)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: contacts_invitations
* user_role: survey_manager
* feature: invitation_personalisation, placeholders
* tags: personalise invitation, custom placeholders, invitation template variables, recipient name in email
* synonyms: add name to invitation email, dynamic fields in invitation, customise invitation text per recipient, merge fields invitation
* product_terms: Participants, invitation template, placeholder, {Name}, {Email}, attribute, Address Book
* exclude: survey prefilling, URL parameters, conditional questions, mail merge external tools
