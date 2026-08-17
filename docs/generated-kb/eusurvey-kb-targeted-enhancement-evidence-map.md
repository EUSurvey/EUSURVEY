# EUSurvey KB Targeted Enhancement — Evidence Map

**Generated**: 2026-07-15

---

## Methodology

Each article was created by inspecting the EUSurvey source code for implementation details and behaviour verification. The following source artefacts were inspected:

- Domain models: `Invitation.java`, `InvitationTemplate.java`, `ParticipationGroup.java`, `AnswerSet.java`, `Survey.java`, `Export.java`, `ResultFilter.java`, `Access.java`, `User.java`
- Controllers: `ParticipantsController.java`, `RunnerController.java`, `ManagementController.java`, `ExportsController.java`, `HttpErrorController.java`, `LoginLogoutController.java`, `HomeController.java`, `AddressBookController.java`, `TranslationController.java`, `ContributionController.java`
- Services: `AttendeeService.java`, `AnswerService.java`, `SurveyService.java`, `ExportService.java`, `ParticipationService.java`, `MailService.java`, `PDFService.java`
- Tools: `InvitationMailCreator.java`, `SurveyHelper.java`, `TranslationsHelper.java`
- Frontend: `sendinvitations.js`, `addressbook.js`, `participants.js`, `runner.js`

---

## Article Evidence Records

### KB-EUSURVEY-INVITE-002-check-invitation-status

| Field | Value |
|-------|-------|
| Action | NEW |
| Source files inspected | `Invitation.java` (fields: invited, reminded, answers, deactivated), `ParticipantsController.java` (participants view), `AttendeeService.java` |
| Tests inspected | N/A (no specific unit test for status display) |
| Official docs used | SM-108 terminology |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | None |
| Evaluation cases | case-046, case-052 |

### KB-EUSURVEY-INVITE-003-invitation-not-received

| Field | Value |
|-------|-------|
| Action | NEW |
| Source files inspected | `InvitationMailCreator.java` (email dispatch logic), `MailService.java` (SendHtmlMail), `Invitation.java` |
| Tests inspected | N/A |
| Official docs used | None |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | None |
| Evaluation cases | case-046, case-048 |

### KB-EUSURVEY-INVITE-004-invitation-email-spam-folder

| Field | Value |
|-------|-------|
| Action | NEW |
| Source files inspected | `InvitationMailCreator.java` (sender configuration, disclaimer), application.properties (sender, smtpserver) |
| Tests inspected | N/A |
| Official docs used | None |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | None — no claims about email reputation made |
| Evaluation cases | case-057 |

### KB-EUSURVEY-INVITE-005-cancel-deactivate-invitation

| Field | Value |
|-------|-------|
| Action | NEW |
| Source files inspected | `Invitation.java` (deactivated field), `AttendeeService.java` (deactivateInvitations, activateInvitations), `ParticipantsController.java` (participantsDeactive, participantsActivate) |
| Tests inspected | N/A |
| Official docs used | None |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | None |
| Evaluation cases | case-056 |

### KB-EUSURVEY-INVITE-006-invitation-link-not-working

| Field | Value |
|-------|-------|
| Action | NEW |
| Source files inspected | `RunnerController.java` (invited method - checks deactivated, survey state), `Invitation.java`, `Survey.java` (isPublished, end date) |
| Tests inspected | N/A |
| Official docs used | PM-03_03 (unpublished message) |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | None |
| Evaluation cases | case-026, case-049, case-067 |

### KB-EUSURVEY-INVITE-007-invitation-email-language

| Field | Value |
|-------|-------|
| Action | NEW |
| Source files inspected | `InvitationMailCreator.java` (text1/text2 are freeform), `InvitationTemplate.java` (template1, template2, templateSubject), `sendinvitations.js` |
| Tests inspected | N/A |
| Official docs used | SM-108 |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | None |
| Evaluation cases | case-051, case-119 |

### KB-EUSURVEY-INVITE-008-personalise-invitations

| Field | Value |
|-------|-------|
| Action | NEW |
| Source files inspected | `InvitationMailCreator.java` (replaceAttributePlaceholders method — confirmed {Name}, {Email}, {name}, {host}, and all Attribute names) |
| Tests inspected | N/A |
| Official docs used | None |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | None |
| Evaluation cases | case-054 |

### KB-EUSURVEY-INVITE-009-duplicate-invitations

| Field | Value |
|-------|-------|
| Action | NEW |
| Source files inspected | `InvitationMailCreator.java` (getInvitationForParticipationGroupAndAttendee — reuses existing invitation if present, updates Reminded date), `Invitation.java` |
| Tests inspected | N/A |
| Official docs used | None |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | None |
| Evaluation cases | case-055 |

### KB-EUSURVEY-CONTRIB-005-access-saved-draft

| Field | Value |
|-------|-------|
| Action | NEW |
| Source files inspected | `AnswerService.java` (getDraftForInvitation, getDraft, saveDraft), `RunnerController.java` (draft flow), `Survey.java` (saveAsDraft) |
| Tests inspected | N/A |
| Official docs used | PM-05_10 |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | None |
| Evaluation cases | case-023 |

### KB-EUSURVEY-CONTRIB-006-submission-not-visible-to-owner

| Field | Value |
|-------|-------|
| Action | NEW |
| Source files inspected | `AnswerService.java` (getAnswers, getNumberOfAnswerSets), `ManagementController.java` (results), `ReportingServiceProxy.java` (reporting DB) |
| Tests inspected | N/A |
| Official docs used | None |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | None |
| Evaluation cases | case-030, case-081 |

### KB-EUSURVEY-CONTRIB-007-answers-missing-after-submission

| Field | Value |
|-------|-------|
| Action | NEW |
| Source files inspected | `SurveyHelper.java` (validateAnswerSet, checkDependencies), `Survey.java` (validatedPerPage), `RunnerController.java` (processSubmit) |
| Tests inspected | N/A |
| Official docs used | None |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | None |
| Evaluation cases | case-034 |

### KB-EUSURVEY-CONTRIB-008-contribution-summary-unavailable

| Field | Value |
|-------|-------|
| Action | NEW |
| Source files inspected | `Survey.java` (downloadContribution), `PDFService.java` (createAnswerPDF), `RunnerController.java` (confirmation, createanswerpdf) |
| Tests inspected | N/A |
| Official docs used | PM-05_01, PM-05_02 |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | None |
| Evaluation cases | case-035 |

### KB-EUSURVEY-CONTRIB-009-confirmation-after-submission

| Field | Value |
|-------|-------|
| Action | NEW |
| Source files inspected | `Survey.java` (sendConfirmationEmail, downloadContribution, changeContribution), `RunnerController.java` (confirmation method, processSubmit), `SurveyService.java` (sendConfirmationEmail logic) |
| Tests inspected | N/A |
| Official docs used | PM-05_04, PM-05_05 |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | None |
| Evaluation cases | case-037 |

### KB-EUSURVEY-CONTRIB-010-see-who-answered

| Field | Value |
|-------|-------|
| Action | NEW |
| Source files inspected | `Invitation.java` (answers field), `ParticipantsController.java`, `ManagementController.java` (results), `Survey.java` (isAnonymous) |
| Tests inspected | N/A |
| Official docs used | None |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | None |
| Evaluation cases | case-029 |

### KB-EUSURVEY-TECH-003 through KB-EUSURVEY-TECH-009

| Field | Value |
|-------|-------|
| Action | NEW (7 articles) |
| Source files inspected | `HttpErrorController.java`, `RunnerController.java`, `ManagementController.java`, `ExportsController.java`, `StatisticsCreator.java` |
| Tests inspected | N/A |
| Official docs used | None |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | KB-EUSURVEY-TECH-007 (concurrency limits) — no specific limits found in source; article correctly states no guaranteed limit is published |
| Evaluation cases | case-107, case-105, case-108, case-106, case-075, case-072, case-103, case-109, case-077 |

### KB-EUSURVEY-ACCESS-001 through KB-EUSURVEY-ACCESS-006

| Field | Value |
|-------|-------|
| Action | NEW (6 articles) |
| Source files inspected | `HttpErrorController.java` (handle403, handle404, handleaccessdenied), `CustomAuthenticationManager.java`, `LoginLogoutController.java`, `RunnerController.java` (security checks) |
| Tests inspected | N/A |
| Official docs used | PM-01_01, PM-01_02 |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | KB-EUSURVEY-ACCESS-006 — 2FA methods are EU Login-controlled; cannot enumerate from EUSurvey source |
| Evaluation cases | case-068, case-058, case-060, case-066, case-063, case-069, case-061 |

### KB-EUSURVEY-TRANS-001-built-in-labels-not-translated

| Field | Value |
|-------|-------|
| Action | NEW |
| Source files inspected | `TranslationController.java`, `TranslationsHelper.java`, `Form.java` (getMessage, getMessageInSurveyLang) |
| Tests inspected | N/A |
| Official docs used | SM-53, SM-57 |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | None |
| Evaluation cases | case-113, case-118 |

### KB-EUSURVEY-TRANS-002-pdf-language

| Field | Value |
|-------|-------|
| Action | NEW |
| Source files inspected | `PDFService.java` (createSurveyPDF, createAnswerPDF), `TranslationController.java` |
| Tests inspected | N/A |
| Official docs used | None |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | None |
| Evaluation cases | case-115, case-116 |

### KB-EUSURVEY-RESULTS-003 through KB-EUSURVEY-RESULTS-007

| Field | Value |
|-------|-------|
| Action | NEW (5 articles) |
| Source files inspected | `ExportsController.java`, `ExportService.java`, `Export.java` (formats, states), `StatisticsCreator.java`, `ManagementController.java` (results, preparecharts, preparestatistics) |
| Tests inspected | N/A |
| Official docs used | SM-77 |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | None |
| Evaluation cases | case-083, case-128, case-082, case-110, case-076, case-079, case-090 |

### KB-EUSURVEY-CONTACT-001 through KB-EUSURVEY-CONTACT-005

| Field | Value |
|-------|-------|
| Action | NEW (5 articles) |
| Source files inspected | `AddressBookController.java` (importAttendees, importAttendees2, validateImport1Parameters, delete, edit, batchEditPOST), `AttendeeService.java`, `MailService.java` (isValidEmailAddress) |
| Tests inspected | N/A |
| Official docs used | SM-99, SM-100, SM-103, SM-104, SM-105 |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | KB-EUSURVEY-CONTACT-004 — exact regex pattern for email validation not extracted; documented at format-rule level |
| Evaluation cases | case-014, case-015, case-133, case-011, case-013 |

### KB-EUSURVEY-SURVEY-001-backup-options

| Field | Value |
|-------|-------|
| Action | NEW |
| Source files inspected | `ManagementController.java` (exportSurvey), `ArchiveService.java`, `SurveyExportHelper.java` |
| Tests inspected | N/A |
| Official docs used | SM-21, SM-22, SM-73 |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | None |
| Evaluation cases | case-085 |

### KB-EUSURVEY-SURVEY-002-survey-types

| Field | Value |
|-------|-------|
| Action | NEW |
| Source files inspected | `Survey.java` (isQuiz, isDelphi, isEVote, isSelfAssessment, isOPC, isECF), `SurveyCreator.java` |
| Tests inspected | N/A |
| Official docs used | SM-17 |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | None |
| Evaluation cases | case-099 |

### KB-EUSURVEY-CONFIG-002-edit-survey-after-publishing (UPDATED)

| Field | Value |
|-------|-------|
| Action | UPDATED — strengthened with deletion consequences and copy advice |
| Source files inspected | `ManagementController.java` (applyChanges), `SurveyService.java` (applyChanges, makeDirty, makeClean) |
| Tests inspected | N/A |
| Official docs used | SM-68 |
| Legacy KB used | None |
| Conflicts | None |
| Manual review required | None |
| Evaluation cases | case-096 |

### KB-EUSURVEY-TECH-001-survey-freezes-submission (UPDATED)

| Field | Value |
|-------|-------|
| Action | UPDATED — narrowed metadata to exclude unrelated scenarios |
| Source files inspected | No additional inspection needed |
| Evaluation cases | Prevents false retrieval for case-068, case-105, case-108, case-110 |

### KB-EUSURVEY-RESULTS-002-export-missing-latest-responses (UPDATED)

| Field | Value |
|-------|-------|
| Action | UPDATED — narrowed metadata to exclude export errors and report timeouts |
| Source files inspected | No additional inspection needed |
| Evaluation cases | Prevents false retrieval for case-082, case-076, case-079 |

### KB-EUSURVEY-INVITE-001-contacts-skipped-import (UPDATED)

| Field | Value |
|-------|-------|
| Action | UPDATED — narrowed metadata to exclude neighbouring topics |
| Source files inspected | No additional inspection needed |
| Evaluation cases | Prevents false retrieval for case-014, case-015, case-013 |

---

## Articles Requiring Manual Review

| Article ID | Reason |
|------------|--------|
| KB-EUSURVEY-ACCESS-006 | EU Login 2FA methods are managed externally; cannot be fully verified from EUSurvey source |
| KB-EUSURVEY-TECH-007 | No concurrency limits found in source code; article correctly avoids inventing numbers |
| KB-EUSURVEY-CONTACT-004 | Exact email regex pattern not extracted from code; documented at rule level only |

---

## Articles NOT Created (Review Required)

| Candidate | Reason |
|-----------|--------|
| KB-EUSURVEY-CONTRIB-003 split | The existing article covers the topic adequately. Rather than splitting into 4 separate articles, cross-references to the new KB-EUSURVEY-CONTRIB-005, -006, -007 articles handle the disambiguation. The article covers both respondent editing and owner reset in appropriate detail. |
| "Other, please specify" article | No dedicated built-in "Other, please specify" option confirmed in source code. The functionality is achievable via visibility/dependency logic. Existing articles on dependencies (KB-EUSURVEY-CONFIG-001, SM-34, UG-053) cover this. Marked as REVIEW_REQUIRED. |
| Publish a draft survey | Existing SM-61 adequately covers the publication procedure. No separate new article needed. |
| Generate charts and statistics | Existing SM-76 covers where to find results; new KB-EUSURVEY-RESULTS-006 and -007 cover the performance scenarios. No separate procedural article needed beyond existing coverage. |
