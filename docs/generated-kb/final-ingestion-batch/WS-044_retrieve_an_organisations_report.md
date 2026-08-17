# WS-044_retrieve_an_organisations_report — WS-044 - Retrieve an organisations report

## Intent / Description

Explain how to request an organisations report in JSON format.

## Applies To

* Role(s): API User
* EUSurvey area: WebServices
* Environment: All
* Article type: Technical how-to
* UI location: Not specified
* Backend location: Not specified

## Short Answer

Use getOrganisationsReport to return a JSON report of organisations and their published surveys and submitted contributions for a given time frame. API summary Status Active Endpoint <base_url>/webservice/getOrganisationsReport?code=[Code]&year=[Year]&month=[Month]&monthEnd=[MonthEnd]&minPublishedSurveys=[MinPublishedSurveys] Supported method(s) GET Required privilege Authenticated system user Parameters and input Code Optional organisation code, for example DIGIT. Year Mandatory year of the report, for example 2025. Month Optional month. If monthEnd is provided, this is the start month. MonthEnd Optional end month. MinPublishedSurveys Optional minimum number of published surveys; only organisations above the threshold are considered. Expected output Returns JSON containing organisation name, monthly values and totals. v1 is the number of published surveys, v1_2 is the number of multi-annual published surveys, and v2 is the number of submitted contributions. Main HTTP responses 200 OK Report returned. 404 Not Found Mandatory parameters are missing. 412 Precondition Failed Parameters have a wrong format. 403 Forbidden Wrong username/password, missing required privilege, or user not allowed to perform the operation. 404 Not Found Unknown service or missing parameters. 412 Precondition Failed A prerequisite is not met, such as missing survey, wrong parameter format, archived survey, or invalid identifier. 429 Too Many Requests Daily API request limit exceeded.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

No detailed procedure provided in source document.

## Important Conditions / Limitations

No specific limitations identified in source document.

## Troubleshooting

Not specified in source document.

## Related Articles

* WS-028 - Retrieve the user’s surveys

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 04-WebServices/WS-044_retrieve_an_organisations_report.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: WebServices
* Keywords: ws-044, retrieve, organisations, report
* Synonyms: How do I get an organisation usage report through the API?, What does v1, v1_2 and v2 mean in getOrganisationsReport?
* Authority: Document-derived guidance
