# WS-028_retrieve_the_user_s_surveys — WS-028 - Retrieve the user’s surveys

## Intent / Description

Explain how to list surveys visible to the authenticated system user and filter the result.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use getMySurveys to return the survey UID, alias and title for surveys associated with the authenticated user. Optional query parameters can filter by survey type, publication state, creator/privileged relationship, dates, archived/deleted/frozen status, minimum reports, minimum contributions or title text. API summary Status Active Endpoint <base_url>/webservice/getMySurveys?param1=value1&param2=value2 Supported method(s) GET Required privilege Authenticated system user with access to the surveys Parameters and input surveyType all, standard, quiz, or brp. published 0 for unpublished surveys, 1 for published surveys. creator 1 to return only surveys created by the current user. privileged 1 to return only surveys where the user has local privileges. date filters createdFrom/To, firstPublicationFrom/To, endFrom/To, archivedFrom/To, deletedFrom/To in yyyy-MM-dd_HH-mm-ss. archived/deleted/frozen 1 to filter by that status. minReported/minContributions Integer greater than 0. title Search string contained in the title. Expected output Returns XML with Surveys user and Survey uid/alias/title entries. Main HTTP responses 200 OK Survey list returned. 412 Precondition Failed One or more parameters have the wrong format. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

No specific limitations identified in source document.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-029 - Retrieve survey metadata

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-028_retrieve_the_user_s_surveys.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-028, retrieve, the, user’s, surveys
* Synonyms: How do I get all my surveys through the API?, Can I filter API survey lists by publication status or date?
* Authority: Document-derived guidance
