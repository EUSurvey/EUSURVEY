# SM-88_analysing_exporting_and_publishing_resul__13_how_can_i_retrieve_files_uploaded_by_contributors — 88. How can I retrieve files uploaded by contributors?

## Intent / Description

This article helps EUSurvey survey managers understand or perform the task: How can I retrieve files uploaded by contributors.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Analysing, exporting and publishing results
* Environment: All
* Article type: How-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

EUSurvey offers different formats of export: XLS, PDF, ODS and XML. Depending on the selected format, the structure and content of the exported files for the 'File Upload' element is as described below:

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. Results export in XLS 1. An Excel file containing the following information:
2. Alias: Survey Alias (example: 6459a3c9-e517-4a34-8e5d-70185db022c3) Export Date: Date in the format 'dd-mm-yyyy hh:mm' (example: 28-09-2020 15:28)
3. A table composed as below:
4. Each column represents a different 'File Upload' question. Each line represents a different contribution. Each cell contains all names of the uploaded files.
5. 2. Folders corresponding to each contribution and named with the contribution ID. It contains sub-folders for each 'File Upload' question (Upload_1, Upload_2 etc.).
6. For instance:
7. Folder: 6cf0463c-29f4-4bea-a195-10e77c61dda1 Sub-folder: Upload_1 (corresponding to the first 'File Upload' question) contains all files uploaded. Sub-folder: Upload_2 (corresponding to the second 'File Upload' question) contains all files uploaded.
8. Results export in PDF 1. Folder named 'PDFs' containing all survey contributions as PDF documents.
9. 2. Folders corresponding to each contribution and named with the contribution ID. It contains sub-folders for each 'File Upload' question (Upload_1, Upload_2 etc.).
10. Results export in ODS 1. An Open Office file containing the following information:
11. Alias: Survey Alias (example: 6459a3c9-e517-4a34-8e5d-70185db022c3) Export Date: Date in the format 'dd-mm-yyyy hh:mm' (example: 28-09-2020 15:28)
12. A table composed as below:
13. Each column represents a different 'File Upload' question. Each line represents a different contribution. Each cell contains all names of the uploaded files.
14. 2. Folders corresponding to each contribution and named with the contribution ID. It contains sub-folders for each 'File Upload' question (Upload_1, Upload_2 etc.).
15. Results export in XML This export is made of an XML file containing the results in a structured way. Uploaded files are not available in that case.

## Important Conditions / Limitations

No specific limitations identified in source document.

## Troubleshooting

Not specified in source document.

## Related Articles

* Where can I find the contributions submitted by my respondents?
* How can I download submitted contributions?
* How can I extract the Draft answers?

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 02-FAQs_SurveyManager/88_analysing_exporting_and_publishing_resul__13_how_can_i_retrieve_files_uploaded_by_contributors.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: Analysing, exporting and publishing results
* Keywords: analysing, contributors, exporting, files, publishing, results, retrieve, uploaded
* Synonyms: How can I retrieve files uploaded by contributors?, How do I retrieve files uploaded by contributors?
* Authority: Document-derived guidance
