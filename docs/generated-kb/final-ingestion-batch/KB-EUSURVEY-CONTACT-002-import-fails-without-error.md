# How to troubleshoot an Address Book import that fails without an error message

## Intent / Description

Explains what to check when an Address Book import appears to complete but no contacts are added, or the import produces no visible error.

## Applies To

* Role(s): Survey Manager
* Feature: Address Book import
* Context: A file is uploaded for import but no contacts appear and no error is shown

## Short Answer

If an import appears to succeed but no contacts are added (or fewer than expected), the cause is typically in the file format, content structure, or mapping configuration. Common silent failure causes include:

1. **Wrong file format or encoding** — The file is not properly formatted as CSV, XLS, XLSX, or ODS.
2. **Wrong delimiter** — For CSV files, the delimiter does not match what EUSurvey expects (comma vs. semicolon).
3. **Missing or misaligned header** — The header row does not match the expected column mapping.
4. **Blank or invalid rows** — Rows that appear to have data but contain invisible characters or formatting issues.
5. **Email column not mapped correctly** — The email column was not correctly identified during the import mapping step.
6. **All rows are duplicates** — Every contact in the file already exists in the Address Book (same email), so they are all skipped.
7. **File encoding issues** — Non-UTF-8 encoding causing characters to be misread.

## Steps / Procedure

1. **Check the import summary**: After import, EUSurvey shows a summary of how many contacts were imported and how many were skipped. Review this carefully.
2. **Verify the file format**:
   - CSV: Save as UTF-8 CSV with comma or semicolon delimiter.
   - XLSX/XLS: Ensure data starts in the first sheet.
   - ODS: Ensure no empty cells before the data block.
3. **Check the delimiter**: Open the CSV in a plain text editor. Verify whether fields are separated by commas or semicolons. EUSurvey auto-detects but may guess wrong.
4. **Verify the header row**: Ensure the first row contains recognisable column headers. During import, correctly map columns to fields (especially Email).
5. **Check the email column**: Every row must have a valid email in the mapped email column. Rows without a valid email are skipped silently.
6. **Check for duplicates**: If all contacts already exist (by email), they will all be skipped.
7. **Remove invisible characters**: Open the file in a plain text editor and check for BOM characters, hidden spaces, or non-printable characters.
8. **Try a minimal test file**: Create a simple CSV with 2–3 contacts and import it. If this works, the issue is in the original file's formatting.

## Important Conditions / Limitations

* **Email is mandatory**: A row without a valid email will always be skipped, with no individual error per row.
* **Duplicate detection**: Duplicates are detected by email address within the same address book. They are skipped without a warning per row.
* **No detailed per-row error log**: EUSurvey shows a count of imported vs skipped but does not typically list the reason for each skipped row.
* **File size**: Very large files may take longer to process but are not explicitly limited by a documented maximum. However, extremely large imports may time out.
* **Encoding**: Use UTF-8 encoding for best results. Other encodings may cause name/attribute corruption rather than a clear error.

## Troubleshooting / Related Cases

* If 0 contacts imported and 0 skipped: the file likely could not be parsed at all. Check the format.
* If all contacts show as skipped: every email either already exists or is invalid. Check the email column mapping.
* If some contacts import but others don't: the missing ones likely have invalid or duplicate emails.

## Out of Scope / Separate Topics

* Why contacts are skipped during import (with error feedback) (see: KB-EUSURVEY-INVITE-001)
* Why EUSurvey reports an invalid email (see: KB-EUSURVEY-CONTACT-004)
* How to import contacts (see: SM-103)
* How imported contacts relate to invitations (see: KB-EUSURVEY-CONTACT-001)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: contacts_invitations
* user_role: survey_manager
* feature: address_book_import
* tags: import fails silently, no contacts imported, import without error, zero contacts after import, silent import failure
* synonyms: contacts not added after import, import appears to work but no contacts, address book import empty result, file uploaded but nothing imported
* product_terms: Address Book, Import, CSV, XLS, XLSX, ODS, delimiter, email, UTF-8
* exclude: successful import with skipped rows, invitation sending, contact attributes
