# KB Coverage Improvement — Final Summary

**Generated**: 2026-07-13
**Scope**: Priority themes from evaluation findings

---

## Coverage Summary Table

| Theme | New Articles | Rewrites | Splits | Retirements | Expected Retrieval Impact |
|-------|-------------|----------|--------|-------------|---------------------------|
| Abuse / Spam / Phishing | 4 | 1 (PM-06_01) | 0 | 0 | **High** — fills complete gap for phishing/spam intents |
| Contributions / Submissions | 2 | 0 | 1 (SM-50) | 0 | **High** — covers reset/incomplete which had zero coverage |
| Deletion Workflows | 3 | 1 (SM-23) | 0 | 0 | **High** — code-grounded details fill critical gaps |
| Invitations / Contacts | 1 | 0 | 0 | 0 | **Medium** — covers most-asked import failure intent |
| Results / Exports | 1 | 0 | 0 | 0 | **High** — directly addresses top confusion: count mismatch |
| Technical Access / Errors | 2 | 1 (SM-08) | 0 | 0 | **High** — covers freeze & session timeout intent |
| Survey Config / Editor | 1 | 0 | 0 | 0 | **High** — addresses conditional logic failure |
| **TOTAL** | **14** | **3** | **1** | **0** | |

---

## Articles Generated

| File | Title | Theme |
|------|-------|-------|
| KB-EUSURVEY-ABUSE-001 | How do I report a spam or abusive survey? | Abuse |
| KB-EUSURVEY-ABUSE-002 | How do I report phishing emails pretending to come from EUSurvey? | Abuse |
| KB-EUSURVEY-ABUSE-003 | What abuse types can I report? | Abuse |
| KB-EUSURVEY-ABUSE-004 | Can a malicious or cloned survey be removed? | Abuse |
| KB-EUSURVEY-CONTRIB-001 | How do I reset a respondent's contribution? | Contributions |
| KB-EUSURVEY-CONTRIB-002 | What does "incomplete contribution" mean? | Contributions |
| KB-EUSURVEY-DELETE-001 | What happens when I delete a survey? | Deletion |
| KB-EUSURVEY-DELETE-002 | Can I undo the deletion of a survey? | Deletion |
| KB-EUSURVEY-DELETE-003 | How do I delete a contribution? | Deletion |
| KB-EUSURVEY-RESULTS-001 | Why do Contributions and Results show different numbers? | Results |
| KB-EUSURVEY-INVITE-001 | Why are some imported contacts skipped? | Invitations |
| KB-EUSURVEY-TECH-001 | Why does the survey freeze during submission? | Technical |
| KB-EUSURVEY-TECH-002 | Why am I asked to log in again? | Technical |
| KB-EUSURVEY-CONFIG-001 | Why does a conditional question not appear? | Configuration |

---

## Rewrite / Split / Retire Proposals

### 1. PM-06_01 (Report abuse in a survey) — REWRITE

**Problem**: Too generic, does not list abuse types, does not cover phishing, does not explain what happens after reporting.

**Recommendation**: Replace with KB-EUSURVEY-ABUSE-001 which is more operational and retrieval-friendly. Keep a reference link from the old article ID.

**Why it's weak for RAG**: The current article matches too broadly ("abuse") and provides insufficient detail to differentiate between spam, phishing, and in-survey abuse. A user asking "how to report phishing" would retrieve this article but not find a useful answer.

### 2. SM-23 (How do I remove an existing survey) — REWRITE

**Problem**: Very brief, does not explain soft-delete vs permanent deletion, does not mention data loss consequences, does not explain grace period.

**Recommendation**: Replace with KB-EUSURVEY-DELETE-001 which provides code-verified details about what actually happens.

**Why it's weak for RAG**: Fails to answer follow-up intents ("can I undo?", "what data is lost?") causing fallback.

### 3. SM-50 (Allow participants to change/edit contribution) — SPLIT

**Problem**: Mixes two user intents: (a) the survey manager enabling the setting, and (b) the respondent wanting to change their answer. Also does not explain the reset mechanism.

**Recommendation**: Keep SM-50 focused on survey manager configuration only. Create KB-EUSURVEY-CONTRIB-001 for the reset mechanism.

**Why it's weak for RAG**: Retrieval for "reset respondent" or "reopen submission" finds this article which talks about configuration settings, not the actual reset action.

### 4. SM-08 (Who do I contact for technical problems) — ENRICH

**Problem**: Generic fallback article that doesn't help users with specific technical issues (blank screen, freeze, timeout).

**Recommendation**: Keep SM-08 as a general escalation path but create specific articles (KB-EUSURVEY-TECH-001, KB-EUSURVEY-TECH-002) for the most common technical issues.

---

## Key Code Evidence Used

| Behavior | Source Class | Method |
|----------|-------------|--------|
| Abuse report types & processing | `SurveyService` | `reportAbuse()` |
| Abuse report max count limit | `SurveyService` | `reportAbuse()` (Setting.MaxReports) |
| Contribution reset mechanism | `AnswerService` | `resetContribution()` |
| eVote reset restriction | `AnswerService` | `resetContribution()` |
| Contribution deletion + audit trail | `AnswerService` | `deleteAnswer()` |
| Survey soft-delete + grace period | `SurveyService` | `getSurveysMarkedDeleted()` |
| Survey permanent deletion | `SurveyService` | `delete()` / `deleteNoTransaction()` |
| Contact import file parsing | `AddressBookController` | `importAttendees()` |
| Results count query (published) | `AnswerService` | `getNumberOfAnswerSetsPublished()` |

---

## Next Steps (Phase 2 — if needed)

The following intents remain for a second batch:

* Why do recipients get duplicate invitations?
* How do I check invitation delivery status?
* Can I cancel an invitation already sent?
* Why does my export not include the latest responses?
* What causes a technical problem during export?
* Why do I see a blank screen?
* What should I do if I see a technical problem message?
* Can I edit a survey after publishing? (details about Apply Changes)
* How do translations work in published and PDF views?
* Can I back up or archive a survey?
