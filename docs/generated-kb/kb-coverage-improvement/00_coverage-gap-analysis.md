# EUSurvey KB Coverage Gap Analysis

**Generated**: 2026-07-13
**Method**: Source code analysis + existing KB review + evaluation findings

---

## 1. Coverage Gap Table

| # | Theme | User Intent | Status | Evidence Source | Priority | Action |
|---|-------|-------------|--------|-----------------|----------|--------|
| 1 | Abuse | How do I report spam surveys? | partial | code + doc | high | rewrite |
| 2 | Abuse | How do I report phishing emails pretending to come from EUSurvey? | missing | — | high | create |
| 3 | Abuse | What should I do if my organisation receives suspicious EUSurvey emails? | missing | — | high | create |
| 4 | Abuse | How do I report abusive or offensive survey content? | partial | code + doc | high | enrich |
| 5 | Abuse | Can a malicious or cloned survey be removed? | missing | code | high | create |
| 6 | Abuse | What abuse types can I report? | missing | code | high | create |
| 7 | Contributions | What does "incomplete contribution" mean? | missing | code | high | create |
| 8 | Contributions | How do I reset a respondent? | missing | code | high | create |
| 9 | Contributions | What happens after I reset a respondent? | missing | code | high | create |
| 10 | Contributions | Can a respondent change a submitted answer? | partial | doc | high | enrich |
| 11 | Contributions | Can I reopen a submission without deleting data? | missing | code | high | create |
| 12 | Contributions | What happens if a respondent submits twice? | partial | code + doc | medium | enrich |
| 13 | Contributions | Why is the contribution summary not available after submission? | missing | — | medium | create |
| 14 | Deletion | How do I delete a survey? | partial | code + doc | high | rewrite |
| 15 | Deletion | Can I delete a contribution? | partial | code + doc | high | rewrite |
| 16 | Deletion | What happens when I delete a survey? | missing | code | high | create |
| 17 | Deletion | Can I undo survey deletion? | missing | code | high | create |
| 18 | Deletion | Why is the delete option missing? | missing | code | medium | create |
| 19 | Deletion | What is the automatic inactive survey deletion? | partial | doc | medium | enrich |
| 20 | Invitations | Why are some imported contacts skipped? | missing | code | high | create |
| 21 | Invitations | What causes "invalid email address" during contact import? | missing | code | high | create |
| 22 | Invitations | How do I update or delete contacts? | partial | doc | medium | enrich |
| 23 | Invitations | Why do recipients get duplicate invitations? | missing | — | medium | create |
| 24 | Invitations | How do I check invitation delivery status? | missing | code | medium | create |
| 25 | Results | Why do Contributions and Results show different numbers? | missing | code | high | create |
| 26 | Results | Why are charts not updated? | partial | doc | high | enrich |
| 27 | Results | Why does my export not include the latest responses? | partial | doc | high | enrich |
| 28 | Results | How do I download all responses? | partial | doc | medium | enrich |
| 29 | Results | What causes a technical problem during export? | missing | — | medium | create |
| 30 | Technical | Why am I asked to log in again? | missing | — | high | create |
| 31 | Technical | Why do I see a blank screen? | missing | — | medium | create |
| 32 | Technical | Why does the survey freeze during submission? | missing | — | high | create |
| 33 | Technical | What should I do if I see a technical problem message? | partial | doc | high | rewrite |
| 34 | Configuration | What types of surveys exist in EUSurvey? | partial | doc | medium | enrich |
| 35 | Configuration | Why does a conditional question not appear? | missing | code | high | create |
| 36 | Configuration | Can I edit a survey after publishing? | partial | doc | medium | enrich |
| 37 | Configuration | How do translations work in published and PDF views? | partial | doc | medium | enrich |

---

## 2. Key Code-Discovered Behaviors Not Clearly Covered

### 2.1 Abuse Reporting Types (from `SurveyService.reportAbuse`)
The system supports 6 predefined abuse types: **fake**, **propaganda**, **hate**, **images**, **promo**, **others** (with free text). Reports are sent by email to configured recipients. A configurable max report count (`MaxReports`) limits the number of emails sent per survey.

### 2.2 Contribution Reset Behavior (from `AnswerService.resetContribution`)
- Reset converts a submitted contribution back to draft
- The existing answer data is preserved (not deleted)
- A new draft entry is created linked to the same answer set
- eVote contributions cannot be reset (throws IllegalStateException)
- If an invitation was linked, the invitation answer count is decremented
- Only users with FormManagement privilege ≥ 2 can reset

### 2.3 Contribution Deletion Behavior (from `AnswerService.deleteAnswer`)
- Deletion removes the answer set from the database
- A `DeletedContribution` record is created with the original code, survey UID, creation date, and deletion date
- Files uploaded by the respondent are deleted
- The draft associated with the contribution is also deleted
- Statistics are invalidated and need recomputation
- If Delphi survey, explanations are deleted too

### 2.4 Survey Deletion Behavior (from `SurveyService.delete`)
- Deletes the draft survey and ALL published versions
- Deletes all answers, translations, accesses, participation groups, invitations
- Deletes exported files and activity logs (optionally)
- Deletes survey files from filesystem
- Soft-delete: survey is first marked `isDeleted=true` with a timestamp; actual deletion runs after a configurable number of days (`DeleteSurveysAge`)
- Surveys marked `doNotDelete=true` are excluded from automatic deletion

### 2.5 Contact Import Validation (from `AddressBookController.importAttendees`)
- Supported formats: CSV, XLS, XLSX, ODS
- CSV delimiter auto-detected (semicolons vs commas)
- File must have at least one data row
- Header row is optional (checkbox)
- The email column must be mapped by the user during import
- Email validation uses standard regex pattern
- Rows with invalid/empty email are skipped (not imported)
- Duplicate detection is based on email within the same address book

### 2.6 Results vs Contributions Count Difference
- "Contributions" page shows answers linked to the **draft** survey version (includes all submissions across versions)
- "Results" page uses a separate OLAP/reporting database that syncs with up to 12-hour delay
- Draft/incomplete contributions are counted separately
- Deleted contributions reduce the count but deletion records are kept for audit

---

## 3. Existing Articles to Rewrite/Split/Retire

| Article | Problem | Action | Reason |
|---------|---------|--------|--------|
| PM-06_01 (Report abuse) | Too generic, missing abuse types, no mention of phishing/spam | rewrite + split into 3 | Covers too many user intents at once |
| SM-23 (Remove existing survey) | Doesn't explain soft-delete, grace period, or what data is lost | rewrite | Missing critical workflow details |
| SM-87 (Results not up to date) | Vague, doesn't distinguish contributions vs results pages | enrich | Needs code-verified explanation |
| SM-50 (Edit contribution after submission) | Focuses on survey manager config, not on respondent experience | split | Two different user intents mixed |

---

## 4. Priority Generation Order

1. Abuse/phishing/spam articles (5 articles)
2. Contribution reset/reopen articles (4 articles)
3. Deletion workflow articles (4 articles)
4. Contact import/invitation articles (4 articles)
5. Results/export articles (3 articles)
6. Technical access/error articles (3 articles)
7. Configuration/editor articles (3 articles)

Total: ~26 new or rewritten articles
