# How to select or translate the language of EUSurvey invitation emails

## Intent / Description

Explains how the language of invitation emails is determined, how to create language-specific invitation templates, and the distinction between invitation language, survey language, and interface language.

## Applies To

* Role(s): Survey Manager
* Feature: Invitation templates, Email language
* Context: A survey owner wants to send invitations in a specific language or in multiple languages

## Short Answer

The language of an EUSurvey invitation email is determined by the **invitation template** created by the survey owner. EUSurvey does not automatically translate invitation emails based on the participant's language preference. The survey owner writes the email text (subject, body before and after the link) in the desired language when composing the invitation.

To send invitations in different languages to different recipients, the survey owner must send invitations in separate batches, writing the template text in the appropriate language for each batch.

## Steps / Procedure

**To send an invitation in a specific language:**

1. Open your survey and go to **Participants**.
2. Select the participation group.
3. Click to send invitations.
4. Write the **Subject**, **text before the link**, and **text after the link** in the desired language.
5. Optionally save the template for reuse.
6. Select the recipients and send.

**To send invitations in multiple languages:**

1. Select a subset of recipients who share the same language.
2. Write the invitation text in that language.
3. Send to the selected subset.
4. Repeat with a different subset and a different language.

**To save and reuse templates:**

1. After writing the invitation text, save it as a named template.
2. When sending future invitations, load the saved template instead of rewriting the text.

## Important Conditions / Limitations

* **No automatic translation**: EUSurvey does not automatically translate the invitation email based on a contact's language attribute or the survey's active translations.
* **Template is free text**: The subject and body fields accept any language — the survey owner controls the content entirely.
* **Invitation language ≠ survey language**: The invitation email language is independent of the survey's content language. A participant receiving a French invitation may still see the survey in the default language unless a translated version exists and they select it.
* **Invitation language ≠ interface language**: The EUSurvey platform interface language (menus, buttons) is separate from both the survey content and the invitation email.
* **Placeholders are language-neutral**: Placeholders like {Name} and {Email} are replaced with the contact's data regardless of the template language.
* **Preview**: Before sending, you can preview the invitation to see how it will appear to a recipient.
* **Mail template styles**: The visual layout (header, footer, branding) is determined by the selected mail template (e.g. "eusurvey" or "ecofficial"), not by the text language.

## Troubleshooting / Related Cases

* If recipients in different countries all receive the same language: invitations were sent in one batch with one template. Send separate batches for each language.
* If the invitation contains the right language but the survey opens in a different language: share the language-specific survey link (with the language parameter) in the invitation text, or inform participants how to switch languages.

## Out of Scope / Separate Topics

* How to translate survey content (see: UG-014, SM-53)
* How to personalise invitation emails with placeholders (see: KB-EUSURVEY-INVITE-008)
* How to generate a PDF in a specific language (see: KB-EUSURVEY-TRANS-002)
* Why built-in labels appear in another language (see: KB-EUSURVEY-TRANS-001)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: contacts_invitations
* user_role: survey_manager
* feature: invitation_template_language
* tags: invitation language, translate invitation email, multilingual invitation, invitation template language
* synonyms: how to change invitation email language, send invitation in French, invitation in different language, translate email to participants
* product_terms: Participants, invitation template, Subject, mail template, language
* exclude: survey translation, interface language, built-in labels, PDF language
