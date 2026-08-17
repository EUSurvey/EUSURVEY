# Why imported Address Book contacts do not appear in an invitation list

## Intent / Description

Explains why contacts that were successfully imported into the Address Book may not appear when creating or selecting recipients for an invitation.

## Applies To

* Role(s): Survey Manager
* Feature: Address Book, Invitations, Participation groups
* Context: A survey owner imported contacts but cannot find them when sending invitations

## Short Answer

Importing contacts into the Address Book and making them available for invitations are separate steps:

1. **Contacts exist in the Address Book** — After import, contacts are stored in the Address Book but not automatically linked to any survey.
2. **Contacts must be added to a participation group (guest list)** — To send invitations, you must create or select a participation group for your survey and add the contacts (or their group) to it.
3. **The participation group must be associated with the survey** — Guest lists are survey-specific.

Simply importing contacts does not make them appear in the invitation dialog of a specific survey.

## Steps / Procedure

**To make imported contacts available for invitations:**

1. **Import contacts** into the Address Book (if not already done).
2. Open the survey for which you want to send invitations.
3. Go to the **Participants** section.
4. **Create a new participation group** (guest list) or use an existing one.
5. **Add contacts** from the Address Book to the participation group. You can select individual contacts or entire address book groups.
6. Once contacts are in the participation group, they appear in the invitation dialog.
7. Send invitations to the selected contacts.

## Important Conditions / Limitations

* **Address Book ≠ invitation list**: The Address Book stores contacts globally. Participation groups are survey-specific lists of recipients.
* **Multiple surveys, same contacts**: You can add the same Address Book contacts to different participation groups for different surveys.
* **Address Book groups**: You can organise contacts into groups within the Address Book and add entire groups to participation groups.
* **No automatic linking**: Importing a CSV into the Address Book does not automatically create invitations for any survey.
* **Search when adding**: When adding contacts to a participation group, you may need to search for them by name or email.
* **Duplicate checking**: If a contact is already in the participation group, they cannot be added again to the same group.

## Troubleshooting / Related Cases

* If contacts were imported but you cannot find them: check the Address Book directly to verify the import succeeded.
* If you see the contacts in the Address Book but not in Participants: you need to explicitly add them to a participation group for this survey.
* If adding contacts to the group does not work: check if they are already in the group.

## Out of Scope / Separate Topics

* Why contacts are skipped during import (see: KB-EUSURVEY-INVITE-001)
* How to import contacts (see: SM-103)
* How to send invitations (see: SM-108)
* How to troubleshoot import failures (see: KB-EUSURVEY-CONTACT-002)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: contacts_invitations
* user_role: survey_manager
* feature: address_book, participation_group
* tags: imported contacts not in invitations, contacts not showing in guest list, cannot invite imported contacts
* synonyms: imported contacts missing from invitation list, contacts not available for sending, where are my imported contacts, cannot find contacts for invitation
* product_terms: Address Book, Participants, participation group, guest list, Import
* exclude: contacts skipped during import, duplicate invitations, email validation errors
