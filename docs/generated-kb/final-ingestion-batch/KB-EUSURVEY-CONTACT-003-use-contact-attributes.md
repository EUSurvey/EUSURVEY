# How to use contact attributes in Address Book lists and invitations

## Intent / Description

Explains what contact attributes are, how they can be used in the Address Book and invitation emails.

## Applies To

* Role(s): Survey Manager
* Feature: Address Book attributes, Invitation personalisation
* Context: A survey owner wants to use additional contact information beyond name and email

## Short Answer

Contact attributes are custom fields you can define in the EUSurvey Address Book to store additional information about contacts (e.g. Organisation, Department, Language, Country). Attributes can be used for:

1. **Organising contacts** — Filter and sort contacts by attribute values.
2. **Personalising invitation emails** — Use attribute names as placeholders in invitation templates (e.g. {Organisation}).
3. **Identifying respondents** — Store metadata about contacts for reference.

## Steps / Procedure

**To create attributes:**

1. Go to the **Address Book**.
2. Use the attribute management function to add new attribute names (e.g. "Department", "Country").
3. Attributes are text fields. You define the name; values are set per contact.

**To set attribute values:**

1. When adding or editing a contact, fill in the attribute values.
2. When importing contacts from a file, map file columns to attribute names during the import process.

**To use attributes in invitations:**

1. When composing an invitation email, insert the attribute name in curly braces as a placeholder: `{Department}`, `{Country}`.
2. When the email is sent, each recipient's placeholder is replaced with their attribute value.
3. If a contact has no value for that attribute, the placeholder is replaced with an empty string.

## Important Conditions / Limitations

* **Text values only**: Attributes are simple text fields. There is no validation on attribute values.
* **Defined per address book**: Attribute names are available for all contacts in your address book.
* **Case-sensitive placeholders**: The placeholder name in the invitation must exactly match the attribute name (case-sensitive).
* **Built-in fields**: Name and Email are built-in fields, not custom attributes. They have their own placeholders ({Name}, {Email}).
* **Import mapping**: When importing a CSV/XLSX file, columns can be mapped to existing attribute names. New attributes can be created during the mapping step.
* **No automatic segmentation**: Attributes do not automatically group contacts or control who receives invitations. You use them for reference and personalisation.

## Troubleshooting / Related Cases

* If a placeholder is not replaced in the email: the attribute name does not match. Check exact spelling and case.
* If attribute values are empty after import: the column mapping during import may not have been set correctly.
* If you need to add attributes after import: edit contacts individually or re-import with the additional columns.

## Out of Scope / Separate Topics

* How to personalise invitation emails with placeholders (see: KB-EUSURVEY-INVITE-008)
* How to import contacts (see: SM-103)
* What are the attributes of a contact (see: SM-100)
* How to edit an attribute for multiple contacts (see: SM-104)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: contacts_invitations
* user_role: survey_manager
* feature: contact_attributes
* tags: contact attributes, custom fields, address book attributes, metadata contacts
* synonyms: how to add custom fields to contacts, use contact information in invitations, additional contact data, personalise with attributes
* product_terms: Address Book, attributes, Import, placeholder, invitation template
* exclude: survey question attributes, registration forms, visibility conditions
