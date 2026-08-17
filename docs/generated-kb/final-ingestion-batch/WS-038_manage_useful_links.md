# WS-038_manage_useful_links — WS-038 - Manage useful links

## Intent / Description

Explain how to add and remove useful links attached to a survey.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use addUsefulLink to add a useful link and removeUsefulLink to remove an existing useful link identified by its label. API summary Status Active Endpoint <base_url>/webservice/addUsefulLink/[Alias] and <base_url>/webservice/removeUsefulLink/[Alias] Supported method(s) PUT for add, DELETE for removal Required privilege Authenticated system user allowed for the survey Parameters and input Alias Survey alias. Add headers label for the link and url containing an http or https address. Remove header label of the link to remove. Expected output Returns 1 if the link was created or removed successfully. Returns empty output in case of a problem. Main HTTP responses 200 OK Link added or removed. 412 Precondition Failed Survey does not exist. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

* Use only valid http or https URLs.
* For published surveys, call applyChanges if the useful link change must become visible in the published version.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-039 - Apply pending survey changes
* WS-029 - Retrieve survey metadata

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-038_manage_useful_links.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-038, manage, useful, links
* Synonyms: How do I add a useful link through the API?, How do I remove a useful link through the API?
* Authority: Document-derived guidance
