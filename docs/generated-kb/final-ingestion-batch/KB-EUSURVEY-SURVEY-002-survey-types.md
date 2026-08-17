# What types of surveys and specialised modules are available in EUSurvey

## Intent / Description

Explains the different types of surveys and specialised features available in EUSurvey.

## Applies To

* Role(s): Survey Manager
* Feature: Survey types, Specialised modules
* Context: A user wants to know what kinds of surveys can be created

## Short Answer

EUSurvey supports several survey types and specialised modules:

1. **Standard Survey** — The default survey type for collecting responses through questionnaires with various question types.
2. **Quiz** — A survey with scoring, correct answers, and automatic evaluation. Participants can see their score after submission.
3. **Delphi Survey** — A multi-round consultation method where participants can view aggregated results and revise their answers across rounds.
4. **eVote** — An electronic voting module with support for seat allocation, preferential voting, and quorum calculations.
5. **Self-Assessment (BRP)** — A specialised assessment framework, including the European Competency Framework (ECF) for self-assessment against competency profiles.
6. **OPC (Open Public Consultation)** — A mode designed for official public consultations of the European Commission.

## Steps / Procedure

**To create a specific survey type:**

1. Go to the EUSurvey dashboard.
2. Click **Create Survey**.
3. Select the desired survey type from the available options.
4. Configure the survey according to the type's specific settings.

**Survey type capabilities:**

- **Standard**: All question types, translations, invitations, exports.
- **Quiz**: All standard features plus scoring, correct answers, result messages, quiz-specific results.
- **Delphi**: Multi-round iteration, participant-visible statistics, explanation boxes, median tracking.
- **eVote**: Voter verification, ballot structure, seat counting algorithms (D'Hondt, Largest Remainder), quorum monitoring.
- **Self-Assessment**: Target datasets, scoring against criteria, competency profiles, radar charts.
- **OPC**: Standard survey features with additional publication and transparency requirements.

## Important Conditions / Limitations

* **Type selected at creation**: The survey type is set when the survey is created. Some types have specific features that are not available in other types.
* **eVote restrictions**: eVote surveys have additional security requirements and may not support all features available in standard surveys (e.g. Save as Draft is disabled).
* **Delphi requires multiple rounds**: A Delphi survey is designed for iterative consultation. Using it for a single-round survey would not leverage its specific features.
* **Not all types available to all users**: Some specialised types (eVote, OPC) may only be available to specific user categories or require activation.
* **Quiz scoring**: Quiz surveys require correct answers to be configured for scoring to work.

## Troubleshooting / Related Cases

* If you cannot find a specific survey type: not all types are available in all EUSurvey instances or for all user accounts.
* If you need to change the type after creation: this is generally not supported for active surveys. Create a new survey of the correct type.

## Out of Scope / Separate Topics

* How to create a quiz survey (see: UG-002)
* How to create a standard survey (see: SM-16_creating_a_survey)
* Survey security options (see: SM-44)
* Survey export (see: SM-21)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: survey_management
* user_role: survey_manager
* feature: survey_types
* tags: survey types, quiz, delphi, evote, self-assessment, OPC, modules
* synonyms: what types of surveys can I create, available survey formats, specialised survey features, quiz survey, voting survey
* product_terms: Standard Survey, Quiz, Delphi, eVote, Self-Assessment, ECF, OPC, BRP
* exclude: individual question types, survey design, survey creation steps
