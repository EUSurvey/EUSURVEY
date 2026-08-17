# ES-034 — What is anonymous survey mode?

## Intent / Description

This article explains what the anonymous survey mode does and how to enable it.

## Applies To

* Role(s): Survey Owner (configuration), Respondent (impact)
* EUSurvey area: Privacy / Survey Properties
* Environment: All
* Article type: Concept
* UI location: Properties page (Privacy section)
* Backend location: Survey.java (isAnonymous)

## Short Answer

Anonymous survey mode prevents EUSurvey from saving personal data such as the respondent's IP address alongside their contribution. When enabled, contributions cannot be traced back to individual respondents through system metadata. This setting must be enabled by the survey owner before publication. Note that the survey design itself may still collect personal data through questions — anonymous mode only controls what the system stores automatically.

## Prerequisites / Required Permissions

* Only the survey owner can enable or disable anonymous mode.
* The setting is configured in the survey properties before or after publication (requires apply changes if changed after publication).

## Procedure

1. Open the survey management area.
2. Navigate to Properties.
3. Locate the Privacy/Security section.
4. Enable "Anonymous survey mode".
5. Save the properties.
6. If the survey is already published, apply changes to make the setting effective.

## Important Conditions / Limitations

* Anonymous mode controls system-level data storage (IP address, connection data). It does NOT prevent the survey owner from designing questions that collect personal data.
* The UI displays the message to respondents: "The anonymous option has been activated. As a result, your contribution to this survey will be anonymous as the system will not save any personal data such as your IP address."
* For eVote surveys, the message variant is: "The anonymous option has been activated. As a result, your vote to this electronic election will be anonymous."
* Survey owners are advised: "If you want your survey to be fully anonymous, do not include questions collecting personal data in your survey design."
* When anonymous mode is active, the `AnswerSet.IP` field is not populated.
* The method `Survey.isAnonymous()` returns the setting value.
* This is NOT a legal guarantee of anonymity — it is a technical configuration of what metadata the system stores.

## Troubleshooting

* **Respondent still sees personal data questions**: Anonymous mode does not affect survey content design. The owner should review questions for personal data collection.
* **IP address visible in exports**: Anonymous mode may not have been enabled when the contribution was submitted. Check the survey property history.

## Related Articles

* ES-031 — How do I secure my survey?
* ES-007 — How do I configure survey properties?
* ES-012 — How do respondents submit a contribution?

## Evidence / Source Traceability

* Backend: `src/main/java/com/ec/survey/model/survey/Survey.java` — method `isAnonymous()`
* Message keys: `info.AnonymousMode`, `info.AnonymousModeEVote`, `info.AnonymousSurveyModeNew`
* Message keys: `form.Privacy.Anonymous`, `form.Privacy.Identified`
* Frontend: Confirmation/disclaimer display in runner views
* Configuration: Privacy section in survey properties JSP

## Confidence and Review Status

High — behaviour is directly visible in UI labels and backend code.

## Metadata

* Domain: Privacy
* EUSurvey area: Survey Properties
* Feature: Anonymous Mode
* User intent: What is anonymous survey mode?
* Article type: Concept
* User type: Survey Owner, Respondent
* Required permission: Survey Owner (to configure)
* Survey status: Any
* Environment: All
* Keywords: anonymous, privacy, IP address, personal data, anonymity, GDPR
* Synonyms: anonymous mode, privacy mode, hide IP, anonymous responses, no tracking
* Acronyms: IP, GDPR
* Related entities: Survey, AnswerSet
* Security / privacy relevance: High — directly controls personal data collection
* Search boost terms: anonymous survey, privacy, hide IP address, anonymous mode
* Source files: Survey.java
* Duplicate status: New
