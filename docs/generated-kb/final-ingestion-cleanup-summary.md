# Final Ingestion Package — Cleanup Summary

**Generated**: 2026-07-13
**Final folder**: `docs/generated-kb/final-ingestion-batch/`

---

## Package Statistics

| Metric | Count |
|--------|-------|
| **Total files in final ingestion folder** | 300 |
| Retained legacy articles | 280 |
| New articles added | 20 |
| Articles retired (removed from ingestion) | 2 |
| Articles excluded / de-prioritized (removed from ingestion) | 4 |
| Split replacements applied | 2 (PM-06_01 → 3 articles; SM-50 → 2 articles) |

---

## Breakdown by Category

### Retained legacy articles (280)

| Prefix | Count | Description |
|--------|-------|-------------|
| SM- | 145 | Survey Manager FAQs (minus 4 retired/excluded) |
| UG- | 54 | User Guides |
| WS- | 50 | Web Services / API |
| PM- | 21 | Participant FAQs (minus 1 retired) |
| SRC- | 10 | Source-code-derived articles |

### New articles (20)

| Prefix | Count | Theme |
|--------|-------|-------|
| KB-EUSURVEY-ABUSE- | 5 | Abuse / Spam / Phishing |
| KB-EUSURVEY-CONTRIB- | 4 | Contributions / Submissions |
| KB-EUSURVEY-DELETE- | 4 | Deletion Workflows |
| KB-EUSURVEY-RESULTS- | 2 | Results / Exports |
| KB-EUSURVEY-INVITE- | 1 | Invitations / Contacts |
| KB-EUSURVEY-TECH- | 2 | Technical Access / Errors |
| KB-EUSURVEY-CONFIG- | 2 | Survey Configuration |

---

## Articles Removed (Not in Final Folder)

| Article | Action | Reason | Replaced By |
|---------|--------|--------|-------------|
| PM-06_01_report-abuse-in-a-survey.md | **Retired** | Too generic, harms retrieval precision for abuse sub-intents | KB-EUSURVEY-ABUSE-001 |
| SM-23_creating_a_survey__08_how_do_i_remove_an_existing_survey.md | **Retired** | Misleadingly brief, lacks deletion details | KB-EUSURVEY-DELETE-001 + DELETE-002 |
| SM-87_analysing_...__12_why_are_my_results_not_up_to_date.md | **Excluded** | Too vague, causes retrieval confusion | KB-EUSURVEY-RESULTS-001 + RESULTS-002 |
| SM-50_survey_security__07_how_can_i_allow_participants_to_change_edit_their_contribution.md | **Excluded** | Mixes config with user action, causes wrong-topic retrieval | KB-EUSURVEY-CONTRIB-003 |
| SM-48_survey_security__05_can_i_enable_survey_respondents_to_access_their_contribution_after_submission.md | **Excluded** | Overlaps with SM-50, incomplete | KB-EUSURVEY-CONTRIB-003 |
| SM-67_managing_your_survey__01_if_i_discover_an_error_in_my_survey_can_i_correct_it.md | **Excluded** | Unclear Apply Changes workflow | KB-EUSURVEY-CONFIG-002 |

---

## Verification Checks Passed

- [x] No retired articles remain in the final folder
- [x] No de-prioritized articles remain in the final folder
- [x] All 20 new articles are present
- [x] No duplicate filenames exist
- [x] All files are .md format
- [x] WS-042 kept (API audience) alongside KB-EUSURVEY-DELETE-003 (end-user audience) — no conflict
- [x] Cross-references within new articles point to specific KB IDs
- [x] No intermediate analysis, working, or report files in the ingestion folder
