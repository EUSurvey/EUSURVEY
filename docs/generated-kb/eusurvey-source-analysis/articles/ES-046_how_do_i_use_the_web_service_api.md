# ES-046 — How do I use the Web Service API?

## Intent / Description

This article provides an overview of the EUSurvey Web Service API for programmatic access.

## Applies To

* Role(s): API User
* EUSurvey area: Integration
* Environment: All
* Article type: Reference
* UI location: /webservice/*
* Backend location: WebServiceController

## Short Answer

EUSurvey provides a REST-like Web Service API for programmatic access to surveys and contributions. The API supports operations like publishing/unpublishing surveys, managing tokens, exporting results, and retrieving survey metadata. Authentication uses HTTP Basic Auth with a valid EUSurvey login and password.

## Prerequisites / Required Permissions

* Valid EUSurvey user account with API access
* Knowledge of the survey UID or shortname

## Procedure

1. Authenticate using HTTP Basic Auth (login and password in the request).
2. Call the desired endpoint with appropriate parameters.
3. Handle the response (XML or JSON format depending on endpoint).

## Important Conditions / Limitations

* The API uses HTTP Basic Auth for authentication.
* Rate limiting applies: maximum requests per day (default: 100, configurable via webservice.maxrequestsperday).
* Available operations include: publish, unpublish, archive, restore, delete surveys; create/activate/deactivate tokens; export results (XML, PDF); get survey metadata and status.
* Results can be exported with date filters.
* Token management: create token lists, create individual tokens, activate/deactivate tokens.
* The API returns XML responses for most endpoints.
* Async operations (exports) return a task ID that can be polled for completion.
* No OpenAPI/Swagger specification was found in the repository.

## Troubleshooting

* Authentication failed: Verify login credentials are correct.
* Rate limit exceeded: Wait or contact admin to increase the limit.
* Survey not found: Verify the survey UID or shortname.

## Related Articles

* ES-017 — How do I export survey results?
* ES-008 — How do I publish a survey?
* ES-070 — How do token guest lists work?

## Evidence / Source Traceability

* Frontend files: N/A
* Backend files: src/main/java/com/ec/survey/controller/WebServiceController.java, src/main/java/com/ec/survey/service/WebserviceService.java
* Classes: WebServiceController, WebserviceService
* Methods: getLoginAndPassword, publishSurvey, unpublishSurvey, createTokens, createResults, getSurveyInfo, getMySurveys
* Routes: GET /webservice/getMySurveys, GET /webservice/createTokens/{groupid}/{number}, GET /webservice/publishSurvey/{shortname}, GET /webservice/unpublishSurvey/{shortname}, GET /webservice/createResults/{shortname}, GET /webservice/getSurveyInfo/{shortname}
* Message keys: error.OperationFailed
* Configuration keys: webservice.maxrequestsperday

## Confidence and Review Status

High

## Metadata

* Domain: Integration
* EUSurvey area: Web Services
* Feature: API Usage
* User intent: How do I use the Web Service API?
* Article type: Reference
* User type: API User
* Required permission: API User
* Survey status: N/A
* Environment: All
* Keywords: API, web service, REST, programmatic, integration, automation
* Synonyms: API access, programmatic control, automation, web service integration
* Acronyms: N/A
* Related entities: WebserviceTask, Survey
* Security / privacy relevance: API access exposes survey data programmatically
* Search boost terms: web service API, REST API, programmatic access
* Source files: src/main/java/com/ec/survey/controller/WebServiceController.java, src/main/java/com/ec/survey/service/WebserviceService.java
* Duplicate status: New
