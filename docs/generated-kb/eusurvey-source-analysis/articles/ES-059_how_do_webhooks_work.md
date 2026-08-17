# ES-059 — How do webhooks work?

## Intent / Description

This article explains how webhook notifications work when contributions are submitted.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Integration
* Environment: All
* Article type: Concept
* UI location: Properties page
* Backend location: BasicService.java

## Short Answer

A webhook URL is called each time a contribution is submitted, allowing external systems to be notified in real-time. Configure the URL in survey Properties.

## Prerequisites / Required Permissions

* Survey owner
* Valid webhook endpoint URL

## Procedure

1. Open survey Properties.
2. Enter the webhook URL.
3. Save.
4. Each submission triggers a request to the URL.

## Important Conditions / Limitations

* URL called on each submission.
* No retry logic identified in source.
* Exact payload format requires functional validation.
* Configured per survey.

## Troubleshooting

No specific troubleshooting items identified.

## Related Articles

* ES-012 — How do respondents submit a contribution?
* ES-046 — How do I use the Web Service API?

## Evidence / Source Traceability

* Frontend: N/A
* Backend: src/main/java/com/ec/survey/service/BasicService.java, src/main/java/com/ec/survey/model/survey/Survey.java
* Classes: BasicService, Survey
* Methods: callHook
* Routes: N/A
* Message keys: info.Webhook
* Config keys: N/A

## Confidence and Review Status

Medium

## Metadata

* Domain: Integration
* EUSurvey area: Webhooks
* Feature: Webhook
* User intent: How do webhooks work?
* Article type: Concept
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Published
* Environment: All
* Keywords: webhook, callback, notification, real-time, integration
* Synonyms: webhook notification, HTTP callback, real-time alert
* Acronyms: N/A
* Related entities: Survey
* Security / privacy relevance: Sends data to external URL
* Search boost terms: webhook, HTTP callback, real-time notification
* Source files: src/main/java/com/ec/survey/service/BasicService.java, src/main/java/com/ec/survey/model/survey/Survey.java
* Duplicate status: New
