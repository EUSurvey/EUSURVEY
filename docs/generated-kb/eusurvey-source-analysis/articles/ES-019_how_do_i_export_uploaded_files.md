# ES-019 — How do I export uploaded files?

## Intent / Description

This article explains how to download all files uploaded by respondents.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Results and Export
* Environment: All
* Article type: How-To
* UI location: Exports page
* Backend location: ExportsController.startExport

## Short Answer

To export uploaded files, navigate to the Exports page and select 'Export Uploaded Elements'. The system creates a ZIP archive containing all files uploaded by respondents through file upload questions. The export runs asynchronously.

## Prerequisites / Required Permissions

* AccessResults privilege or survey ownership
* Survey must have file upload questions with submitted files

## Procedure

1. Navigate to the Exports page.
2. Select 'Export Uploaded Elements' type.
3. Click Start.
4. Wait for the ZIP archive to be generated.
5. Download the ZIP file when ready.

## Important Conditions / Limitations

* All uploaded files are packaged into a single ZIP archive.
* The export runs asynchronously and may take considerable time for many files.
* Files are organized by contribution in the ZIP archive.
* Only files from submitted contributions are included.
* You will receive an email notification when the download is ready.

## Troubleshooting

* ZIP file very large: This is expected if many files were uploaded.
* Missing files: Files from deleted contributions are not included.

## Related Articles

* ES-017 — How do I export survey results?
* ES-018 — How do I export statistics?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/WEB-INF/views/exports/
* Backend files: src/main/java/com/ec/survey/controller/ExportsController.java, src/main/java/com/ec/survey/tools/export/FileExportCreator.java
* Classes: ExportsController, FileExportCreator
* Methods: startExport, exportContent
* Routes: POST /exports/start/Files/zip
* Message keys: label.ExportUploadedElements, info.DownloadStarted
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Results and Export
* EUSurvey area: Exports
* Feature: Export Files
* User intent: How do I export uploaded files?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Published
* Environment: All
* Keywords: files, upload, download, ZIP, attachments
* Synonyms: download uploaded files, get attachments, export file uploads
* Acronyms: N/A
* Related entities: Export, File
* Security / privacy relevance: Uploaded files may contain personal data
* Search boost terms: export uploaded files, download attachments
* Source files: src/main/java/com/ec/survey/controller/ExportsController.java, src/main/java/com/ec/survey/tools/export/FileExportCreator.java
* Duplicate status: New
