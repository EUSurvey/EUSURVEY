# Why are some imported contacts skipped during address book import?

## Intent / Description

Explains why certain rows are skipped or rejected when importing contacts from a file into the EUSurvey address book.

## Applies To

* Role(s): Survey Manager
* Feature: Address Book, Contact Import
* Context: After importing a file, some contacts are missing or marked as skipped

## Short Answer

When you import contacts from a file (CSV, XLS, XLSX, or ODS), EUSurvey validates each row before adding it to your address book. Rows may be skipped for the following reasons:

- **Invalid or empty email address**: The email column is mandatory. Rows without a valid email are skipped.
- **Duplicate email**: If the email already exists in the same address book, the row is skipped to avoid duplicates.
- **Empty row**: Rows that are entirely empty are ignored.
- **Format errors**: If the row has more or fewer columns than expected based on the header, it may be skipped.
- **File format issues**: If the file cannot be parsed correctly (wrong delimiter, encoding issues), some or all rows may fail.

## Steps / Procedure

To ensure a successful import:

1. Prepare your file with a clear **header row** (recommended).
2. Ensure one column contains **valid email addresses** for every contact.
3. Use a consistent delimiter (comma or semicolon for CSV files).
4. Remove any completely empty rows.
5. Save the file in a supported format: **CSV**, **XLS**, **XLSX**, or **ODS**.
6. In EUSurvey, go to your **Address Book** and click **Import**.
7. Upload the file, select the delimiter if applicable, and indicate whether the first row is a header.
8. Map the file columns to the address book attributes (name, email, etc.).
9. Review the import summary — it will indicate how many contacts were imported and how many were skipped.

## Important Conditions / Limitations

* **Email is required**: Every contact must have a valid email address. Rows without an email are always skipped.
* **Email format validation**: The email must follow standard email format (e.g., `user@domain.com`). Emails with spaces, missing @ symbol, or invalid characters are rejected.
* **Duplicate detection**: Based on email address within the same address book. Duplicates are skipped silently.
* **Supported formats**: CSV (comma or semicolon separated), XLS (Excel 97-2003), XLSX (Excel), ODS (OpenDocument).
* **CSV delimiter auto-detection**: EUSurvey checks whether the file uses more semicolons or commas and selects the delimiter accordingly. You can override this during import.
* **File size**: Very large files may take longer to process. There is no documented hard limit but extremely large imports may time out.
* **No partial update**: If a contact with the same email already exists, the import skips it entirely — it does not update existing fields.

## Troubleshooting / Related Cases

* If all contacts are skipped, the file format may be incorrect or the delimiter may not match. Try re-saving as CSV with comma separator.
* If emails appear valid but are skipped, check for invisible characters (leading/trailing spaces) in the email column.
* If using ODS format and contacts are missing, ensure there are no empty cells in the first column before your data (the parser stops at the first empty cell in column A).

## Out of Scope / Separate Topics

* How do I add contacts manually (see: How do I add new contacts to my address book)
* How do I send invitations to contacts (see: How do I send invitation emails)
* What are the attributes of a contact (see separate article)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: contacts_invitations
* user_role: survey_manager
* feature: address_book_import
* tags: import contacts, skipped contacts, invalid email, duplicate, CSV import, address book
* synonyms: contacts not imported, some contacts missing after import, import file errors, why contacts skipped, invalid email address import
* product_terms: Address Book, Import, CSV, XLS, XLSX, ODS, email validation, duplicate
