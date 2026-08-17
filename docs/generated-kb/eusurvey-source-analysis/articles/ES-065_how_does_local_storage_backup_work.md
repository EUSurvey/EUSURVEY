# ES-065 — How does local storage backup work?

## Intent / Description

This article explains how browser localStorage backs up in-progress survey answers.

## Applies To

* Role(s): Respondent
* EUSurvey area: Survey Runner
* Environment: All
* Article type: Concept
* UI location: Runner page
* Backend location: runner.js (client-side)

## Short Answer

EUSurvey automatically saves answer progress to browser localStorage. If the browser crashes, respondents can restore their answers when returning to the survey.

## Prerequisites / Required Permissions

* Browser must support localStorage

## Procedure

1. Access the survey.
2. Answers are automatically backed up as you fill them in.
3. If the page crashes or closes, return to the survey URL.
4. A prompt offers to restore from backup.
5. Accept to restore previous answers.

## Important Conditions / Limitations

* Saved automatically to browser localStorage.
* Restore prompt appears if backup exists.
* Can be disabled for shared computers.
* Uploaded files cannot be restored from backup.
* Backup cleared after successful submission.

## Troubleshooting

* Local storage disabled message: Enable localStorage in browser.
* Files not restored: File uploads cannot be saved in localStorage.

## Related Articles

* ES-012 — How do respondents submit a contribution?
* ES-013 — How do I save a survey as draft?

## Evidence / Source Traceability

* Frontend: src/main/webapp/resources/js/runner.js
* Backend: 
* Classes: runner.js
* Methods: saveLocalBackup, restoreBackup, clearLocalBackup, checkLocalBackup
* Routes: N/A
* Message keys: info.DeactivateLocalStorage, info.LocalStorageDisabled, info.LocalBackupFiles, label.DeleteLocalBackup
* Config keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Survey Runner
* EUSurvey area: Data Safety
* Feature: Local Backup
* User intent: How does local storage backup work?
* Article type: Concept
* User type: Respondent
* Required permission: Respondent
* Survey status: Published
* Environment: All
* Keywords: backup, local storage, restore, crash, save
* Synonyms: auto-save, browser backup, recover answers
* Acronyms: N/A
* Related entities: AnswerSet
* Security / privacy relevance: Data stored in user browser
* Search boost terms: local backup, auto-save, browser recovery
* Source files: 
* Duplicate status: New
