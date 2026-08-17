# Phase 3 — Reingestion Manifest

**Generated**: 2026-07-13
**Package**: EUSurvey KB Coverage Improvement Batch 1

---

## Manifest Table

| File Name | Action | Theme | Replaces / Related Old Article | Expected Impact | Ingestion Priority |
|-----------|--------|-------|-------------------------------|-----------------|-------------------|
| KB-EUSURVEY-ABUSE-001-report-spam-surveys.md | new (replaces PM-06_01) | Abuse | PM-06_01 (retire from primary ingestion) | Precise retrieval for spam/abuse reporting intent | 1 |
| KB-EUSURVEY-ABUSE-002-report-phishing-emails.md | new | Abuse | — | Fills gap for phishing queries (zero prior coverage) | 1 |
| KB-EUSURVEY-ABUSE-003-abuse-types.md | new | Abuse | — | Enables precise answer about abuse categories | 1 |
| KB-EUSURVEY-ABUSE-004-remove-malicious-survey.md | new | Abuse | — | Fills gap for "can fake survey be removed" queries | 1 |
| KB-EUSURVEY-ABUSE-005-suspicious-emails-organisation.md | new | Abuse | — | Covers organisational phishing guidance | 1 |
| KB-EUSURVEY-CONTRIB-001-reset-respondent.md | new | Contributions | — (SM-50 kept but narrowed) | Fills gap for reset/reopen intent | 1 |
| KB-EUSURVEY-CONTRIB-002-incomplete-contribution.md | new | Contributions | — | Fills gap for "incomplete" / "draft" confusion | 1 |
| KB-EUSURVEY-CONTRIB-003-respondent-change-submitted-answer.md | new (enriches SM-48/SM-50 scope) | Contributions | SM-48 + SM-50 (kept but de-prioritized) | Unified respondent-facing answer about editing | 1 |
| KB-EUSURVEY-CONTRIB-004-respondent-submits-twice.md | new | Contributions | — (SM-46 tangential) | Addresses "what if submit twice" directly | 2 |
| KB-EUSURVEY-DELETE-001-what-happens-delete-survey.md | new (replaces SM-23) | Deletion | SM-23 (retire from primary ingestion) | Code-grounded explanation of full deletion process | 1 |
| KB-EUSURVEY-DELETE-002-undo-deletion.md | new | Deletion | — | Fills critical gap: "can I undo?" | 1 |
| KB-EUSURVEY-DELETE-003-delete-contribution.md | new (end-user version) | Deletion | WS-042 (keep for API users) | End-user facing deletion article | 1 |
| KB-EUSURVEY-DELETE-004-delete-option-missing.md | new | Deletion | — | Troubleshooting for invisible delete button | 2 |
| KB-EUSURVEY-RESULTS-001-contributions-vs-results-count.md | new | Results | — (SM-87 tangential) | Directly addresses #1 results confusion question | 1 |
| KB-EUSURVEY-RESULTS-002-export-missing-latest-responses.md | new (enriches SM-87) | Results | SM-87 (kept but de-prioritized for this intent) | Actionable guidance for stale exports | 1 |
| KB-EUSURVEY-INVITE-001-contacts-skipped-import.md | new | Invitations | — (SM-103 procedural only) | Troubleshooting for import failures | 1 |
| KB-EUSURVEY-TECH-001-survey-freezes-submission.md | new | Technical | — | Fills top technical complaint gap | 1 |
| KB-EUSURVEY-TECH-002-asked-login-again.md | new | Technical | — | Fills session timeout gap | 1 |
| KB-EUSURVEY-CONFIG-001-conditional-question-not-appearing.md | new | Configuration | — (SM-34 how-to only) | Troubleshooting for dependency failures | 1 |
| KB-EUSURVEY-CONFIG-002-edit-survey-after-publishing.md | new (enriches SM-67) | Configuration | SM-67 (kept but de-prioritized) | Clear Apply Changes workflow | 2 |

---

## Articles to Retire / De-prioritize from Primary Ingestion

| Old Article | Action | Reason | Replacement |
|-------------|--------|--------|-------------|
| PM-06_01 (Report abuse in a survey) | **retire from primary** | Too generic, causes broad retrieval that misses specific abuse sub-intents | KB-EUSURVEY-ABUSE-001 |
| SM-23 (How do I remove an existing survey) | **retire from primary** | Lacks deletion details, misleadingly says "cannot be undone" without nuance | KB-EUSURVEY-DELETE-001 + KB-EUSURVEY-DELETE-002 |
| SM-87 (Results not up to date) | **de-prioritize** | Too brief, retrieval confused with chart/export/count questions | KB-EUSURVEY-RESULTS-001 + KB-EUSURVEY-RESULTS-002 |
| SM-50 (Allow participants to change contribution) | **de-prioritize** | Mixes config and user action; keep for config intent only | KB-EUSURVEY-CONTRIB-003 handles respondent intent |
| SM-48 (Access contribution after submission) | **de-prioritize** | Overlaps with SM-50, incomplete for respondent perspective | KB-EUSURVEY-CONTRIB-003 handles unified intent |
| WS-042 (Delete a contribution - API) | **keep for API users only** | Not relevant for end-user support queries | KB-EUSURVEY-DELETE-003 is end-user version |

---

## Split Mapping

| Original | Split Into | Rationale |
|----------|-----------|-----------|
| SM-50 (config + respondent action mixed) | SM-50 remains (config only) + KB-EUSURVEY-CONTRIB-003 (respondent can change) + KB-EUSURVEY-CONTRIB-001 (manager reset) | Three distinct user intents were merged into one article |
| PM-06_01 (all abuse in one article) | KB-EUSURVEY-ABUSE-001 (report spam/abuse) + KB-EUSURVEY-ABUSE-002 (phishing) + KB-EUSURVEY-ABUSE-005 (org guidance) | Three distinct scenarios all crammed into "report abuse" |

---

## File Locations

All new articles are located in:
- `docs/generated-kb/kb-coverage-improvement/KB-EUSURVEY-*.md` (first batch, 14 files)
- `docs/generated-kb/kb-reingestion-batch/KB-EUSURVEY-*.md` (second batch, 6 files)

Total new files for ingestion: **20 articles**
