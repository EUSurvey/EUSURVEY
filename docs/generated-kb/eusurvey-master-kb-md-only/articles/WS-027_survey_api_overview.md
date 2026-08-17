# WS-027_survey_api_overview — WS-027 - Survey API overview

## Intent / Description

Explain the Survey API endpoints for retrieving metadata and automating survey lifecycle and configuration operations.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Concept
* UI location: Not specified
* Backend location: Not specified

## Short Answer

The Survey API allows technical integrations to retrieve survey metadata, check publication status, publish or unpublish surveys, archive or restore surveys, delete surveys, retrieve an empty survey PDF, update title or contact information, manage background documents and useful links, apply changes, retrieve privileged users, delete contributions, retrieve deleted contributions, and generate organisation reports.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. Use read-only endpoints for inventory and monitoring, such as getMySurveys, getSurveyMetadata and getSurveyPublicationStatus.
2. Use lifecycle endpoints carefully for publish, unpublish, archive, restore and delete operations.
3. Use PATCH, PUT and DELETE endpoints only when the integration is explicitly responsible for configuration changes.
4. After changing published survey settings, call applyChanges when needed so pending changes become visible in the published survey.

## Important Conditions / Limitations

* Many Survey API endpoints return “1” for success and an empty response in case of a problem.
* Most Survey API calls require the authenticated system user to be the creator or otherwise allowed for the survey.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-028 - Retrieve the user’s surveys
* WS-031 - Publish a survey
* WS-039 - Apply pending survey changes

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-027_survey_api_overview.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: Survey API, metadata, publish, archive, delete, lifecycle
* Synonyms: What can I do with the Survey API?, Which endpoints manage survey publication and metadata?
* Authority: Document-derived guidance
