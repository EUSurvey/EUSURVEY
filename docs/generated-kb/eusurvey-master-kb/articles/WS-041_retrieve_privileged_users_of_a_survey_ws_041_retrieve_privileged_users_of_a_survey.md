# WS-041_retrieve_privileged_users_of_a_survey — WS-041 - Retrieve privileged users of a survey

## Intent / Description

Explain how to retrieve the list of users with privileges on a survey.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use getPrivilegedUsers to return the owner and users added on the standard privileges or dedicated result privileges tabs. The XML response shows privilege type and access levels for management areas. API summary Status Active Endpoint <base_url>/webservice/getPrivilegedUsers/[Alias] Supported method(s) GET Required privilege Authenticated system user allowed for the survey Parameters and input Alias Survey alias or survey UID. Expected output Returns XML with PrivilegedUsers, User login, PrivilegeType and Access entries. Access levels use 0 for no access, 1 for read-only access, and 2 for read/write access. Main HTTP responses 200 OK Privileged users returned. 412 Precondition Failed Survey does not exist. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

* PrivilegeType can be OWNER, STANDARD or RESULTS.
* Access includes ManageInvitations, FormManagement, AccessDraft and AccessResults values.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-003 - Understand API permissions and survey privileges

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-041_retrieve_privileged_users_of_a_survey.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-041, retrieve, privileged, users, survey
* Synonyms: How do I list privileged users through the API?, What do access values 0, 1 and 2 mean?
* Authority: Document-derived guidance
