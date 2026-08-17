# WS-037_manage_background_documents — WS-037 - Manage background documents

## Intent / Description

Explain how to upload and remove background documents attached to a survey.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use uploadBackgroundDocument to add a background document file and removeBackgroundDocument to remove an existing background document identified by its label. API summary Status Active Endpoint <base_url>/webservice/uploadBackgroundDocument/[Alias] and <base_url>/webservice/removeBackgroundDocument/[Alias] Supported method(s) PUT for upload, DELETE for removal Required privilege Authenticated system user allowed for the survey Parameters and input Alias Survey alias. Upload headers label for the file and filename for the file name. Upload body File stream. Removal header label of the document to remove. Expected output Returns 1 if the document was uploaded or removed successfully. Returns empty output in case of a problem. Main HTTP responses 200 OK Document uploaded or removed. 412 Precondition Failed Survey does not exist. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

* Background documents appear in survey metadata and may need applyChanges for published surveys.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-039 - Apply pending survey changes
* WS-029 - Retrieve survey metadata

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-037_manage_background_documents.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-037, manage, background, documents
* Synonyms: How do I upload a background document through the API?, How do I remove a background document through the API?
* Authority: Document-derived guidance
