# PM-07_01_cookies-and-local-storage-used-by-eusurvey — 07.01 - Cookies and local storage used by EUSurvey

## Intent / Description

Explain what information EUSurvey stores in cookies and local storage for participants.

## Applies To

* Role(s): Respondent
* EUSurvey area: Privacy
* Environment: All
* Article type: Troubleshooting
* UI location: Not specified
* Backend location: Not specified

## Short Answer

EUSurvey uses session cookies to ensure reliable communication between the client and the server. These cookies disappear when the session ends. EUSurvey may also use local storage to save backup copies of your survey input until the survey is successfully submitted or saved as a draft on the server.

## Prerequisites / Required Permissions

Not specified in source document.

## Procedure

1. Configure your browser to accept cookies so EUSurvey can work reliably.
2. When answering a survey, note that local storage may temporarily contain question IDs and draft answers as a backup.
3. After successful submission or successful draft saving on the server, the local storage data is removed.
4. If you are using a public or shared computer, disable the local backup feature by clearing the checkbox labelled Save a backup on your local computer.

## Important Conditions / Limitations

* Session cookies are required for reliable communication between your browser and the EUSurvey server.
* Local storage is used as a backup if the server is unavailable during submission, if the computer is switched off accidentally, or for another interruption.
* If you disable the local backup feature, no survey data will be stored on your computer for that purpose.

## Troubleshooting

Not specified in source document.

## Related Articles

* 05.10 - Where to find answers saved as draft

## Support / Escalation

Contact the EUSurvey support team if the issue persists.

## Source Traceability

### Document-derived sources

* 01-FAQs_Participants/07_01_cookies-and-local-storage-used-by-eusurvey.docx

### Source-code-derived evidence

Not applicable for this article.

## Confidence and Review Status

High — derived from official EUSurvey documentation.

## Ingestion Status

Ready for ingestion

## Metadata

* Domain: Privacy
* Keywords: 07.01, cookies, and, local, storage, used, eusurvey
* Synonyms: This system uses cookies. What information is saved there?, What does EUSurvey store in cookies?, What is saved in local storage when I answer a survey?
* Authority: Document-derived guidance
