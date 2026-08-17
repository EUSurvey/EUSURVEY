# ES-020 — How do I publish results publicly?

## Intent / Description

This article explains how to make survey results publicly accessible via a web page.

## Applies To

* Role(s): Survey Owner
* EUSurvey area: Results
* Environment: All
* Article type: How-To
* UI location: Results page
* Backend location: PublicationController.publication

## Short Answer

To publish results publicly, navigate to the Results page and use the 'Publish Results' option. You can publish individual contributions, statistics, charts, and uploaded documents. A public URL is generated where anyone (or password-protected access) can view the published results.

## Prerequisites / Required Permissions

* The user must be the survey owner
* The survey must have contributions

## Procedure

1. Navigate to the survey Results page.
2. Click 'Publish Results' or 'Edit Result Publication'.
3. Configure what to publish: contributions, statistics, charts, uploaded documents.
4. Select which questions to include.
5. Optionally select which contributions to publish.
6. Optionally set a password for the publication page.
7. Enable the publication.
8. Share the publication URL with stakeholders.

## Important Conditions / Limitations

* You can publish all contributions or a filtered subset.
* You can choose which questions are visible in the publication.
* Publication can be password-protected.
* Statistics and charts can be included.
* Uploaded documents can be included.
* The publication URL format is: /publication/{shortname}.
* Publication can be enabled/disabled without deleting the configuration.

## Troubleshooting

* Results not visible on publication page: Ensure the publication is active and at least one contribution is selected.

## Related Articles

* ES-016 — How do I view survey results?
* ES-017 — How do I export survey results?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/WEB-INF/views/publication/
* Backend files: src/main/java/com/ec/survey/controller/PublicationController.java, src/main/java/com/ec/survey/model/Publication.java
* Classes: PublicationController, Publication
* Methods: publication, putParameterFilters
* Routes: GET /publication/{shortname}
* Message keys: label.PublishResults, label.PublishedResults, label.PublishStatistics, label.PublishCharts, label.PublishUploadedElements, label.PublishQuestionSelection, label.PublishAnswerSelection
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Results
* EUSurvey area: Publication
* Feature: Publish Results
* User intent: How do I publish results publicly?
* Article type: How-To
* User type: Survey Owner
* Required permission: Survey Owner
* Survey status: Published
* Environment: All
* Keywords: publish, results, public, share, URL
* Synonyms: share results publicly, make results visible, publish answers
* Acronyms: N/A
* Related entities: Publication, Survey
* Security / privacy relevance: Published results are publicly accessible
* Search boost terms: publish results, share results, public results page
* Source files: src/main/java/com/ec/survey/controller/PublicationController.java, src/main/java/com/ec/survey/model/Publication.java
* Duplicate status: New
