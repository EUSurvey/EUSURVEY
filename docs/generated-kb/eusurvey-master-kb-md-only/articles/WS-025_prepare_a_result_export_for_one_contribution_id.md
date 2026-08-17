# WS-025_prepare_a_result_export_for_one_contribution_id — WS-025 - Prepare a result export for one contribution ID

## Intent / Description

Explain how to request a single contribution export using the contribution ID.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use prepareResultFromContributionId to start an asynchronous export of one contribution identified by its contribution ID. This endpoint was added in version 1.5.3.6 and returns a ticket that can be used with getResults. API summary Status Active Endpoint <base_url>/webservice/prepareResultFromContributionId/[Alias]/[ContributionId]/[ID] Supported method(s) GET Required privilege Results RO Parameters and input Alias Survey alias. ContributionId Contribution ID for which the answer should be exported. ID true/false flag indicating whether question and answer IDs should be added. Expected output Returns HTTP 201 with a ticket. getResults returns a ZIP containing result.xml, a PDF version of the contribution, and uploaded files. Main HTTP responses 201 Created Ticket created successfully. 412 Precondition Failed Alias does not exist, alias was never published, contribution ID does not exist or does not match the survey, or survey is archived. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

* If the PDF cannot be created, it is substituted by a text file of the same name to be checked with the helpdesk.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-024 - Prepare a result export for one token
* WS-021 - Understand XML results structure

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-025_prepare_a_result_export_for_one_contribution_id.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-025, prepare, result, export, for, one, contribution
* Synonyms: How do I export one contribution by contribution ID?, What is prepareResultFromContributionId used for?
* Authority: Document-derived guidance
