# WS-003_understand_api_permissions_and_survey_privileges — WS-003 - Understand API permissions and survey privileges

## Intent / Description

Clarify which application privileges are required to call EUSurvey Web-services API endpoints.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical concept
* UI location: Not specified
* Backend location: Not specified

## Short Answer

A valid system user can access the API in general, but individual survey operations require survey-level privileges. For example, invitation management requires Form Management read/write access, result export requires Results read-only access, and contribution deletion requires read/write result access on the relevant survey.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. Identify the API family you want to call.
2. Check the required privilege in the corresponding endpoint article.
3. Ask the survey owner or delegated manager to grant the system user the required local privilege in EUSurvey.
4. Retry the call after the privilege has been granted.

## Important Conditions / Limitations

* Authentication and authorization are separate. Correct credentials do not guarantee access to a specific survey.
* Privileges are checked when asynchronous jobs are created; only the original requesting user can download the prepared result.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-007 - Invitations API overview
* WS-018 - Results API overview
* WS-027 - Survey API overview

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-003_understand_api_permissions_and_survey_privileges.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: permissions, privileges, Form Management, Results RO, access rights
* Synonyms: Which permissions are required to use the API?, Why does an authenticated API call return forbidden?, What privileges are needed for result exports or invitation management?
* Authority: Document-derived guidance
