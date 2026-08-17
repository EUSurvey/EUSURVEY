# How to update or delete existing contacts in the EUSurvey Address Book

## Intent / Description

Explains how to modify or remove contacts that are already in the Address Book.

## Applies To

* Role(s): Survey Manager
* Feature: Address Book management
* Context: A survey owner needs to update contact details or remove contacts

## Short Answer

EUSurvey provides the ability to edit individual contacts and delete contacts from the Address Book. You can update email addresses, names, and attribute values for existing contacts. Deletion removes the contact from the Address Book.

## Steps / Procedure

**To edit an existing contact:**

1. Go to the **Address Book**.
2. Find the contact you want to edit (use search if needed).
3. Click on the contact or use the **Edit** action.
4. Update the name, email address, or attribute values as needed.
5. Save the changes.

**To delete a contact:**

1. Go to the **Address Book**.
2. Find the contact(s) you want to delete.
3. Select the contact(s).
4. Use the **Delete** action.
5. Confirm the deletion.

**To delete multiple contacts:**

1. Select multiple contacts using the checkboxes.
2. Use the bulk delete action.
3. Confirm the deletion.

**To update contacts in bulk (batch edit):**

1. Select multiple contacts.
2. Use the **Batch Edit** function if available.
3. Update the desired attribute for all selected contacts at once.
4. Save the changes.

## Important Conditions / Limitations

* **Deletion is permanent**: Deleted contacts cannot be recovered. Re-import them if needed.
* **Invitations are separate**: Deleting a contact from the Address Book does not automatically deactivate invitations that were already sent using that contact. The invitation link remains functional unless explicitly deactivated.
* **Email uniqueness**: If you change a contact's email to one that already exists in the address book, the system may reject the change.
* **Import does not update**: Re-importing a file with an existing email address skips the duplicate rather than updating it. To update contacts via file, you may need to delete them first and re-import.
* **Contact ownership**: You can only edit and delete contacts in your own Address Book or in address books shared with you (with appropriate permissions).
* **Audit trail**: Changes to contacts may not be logged in the activity log.

## Troubleshooting / Related Cases

* If you cannot find the delete option: ensure you have selected at least one contact.
* If you need to update many contacts: consider deleting and re-importing with the updated information, or use batch edit for attribute changes.
* If deleted contacts still appear in a participation group: removing from the Address Book may not automatically remove from existing participation groups. Check the group membership.

## Out of Scope / Separate Topics

* How to import contacts (see: SM-103)
* How to use contact attributes (see: KB-EUSURVEY-CONTACT-003)
* How to deactivate an invitation (see: KB-EUSURVEY-INVITE-005)
* How to edit attributes for multiple contacts (see: SM-104)
* How to export contacts (see: SM-105)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: contacts_invitations
* user_role: survey_manager
* feature: address_book_management
* tags: update contact, delete contact, edit contact, remove contact, modify address book
* synonyms: how to change contact email, remove contacts from address book, delete attendee, edit existing contact, update contact information
* product_terms: Address Book, Edit, Delete, Batch Edit, contacts
* exclude: import contacts, invitation deactivation, participation group removal
