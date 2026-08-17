# Phase 4 — Validation Mapping

**Generated**: 2026-07-13

This document maps the KB changes to expected evaluation improvements. After reingestion, these cases should be re-tested.

---

## Validation Table

| Case ID | User Question | Theme | Expected KB Change | Expected Result After Reingestion |
|---------|--------------|-------|-------------------|----------------------------------|
| VAL-001 | "How do I report a spam survey?" | Abuse | KB-EUSURVEY-ABUSE-001 provides exact match | Better retrieval — direct answer with steps and abuse types |
| VAL-002 | "I received a phishing email from EUSurvey" | Abuse | KB-EUSURVEY-ABUSE-002 fills complete gap | Eliminates fallback — phishing has dedicated coverage |
| VAL-003 | "How do I report offensive content in a survey?" | Abuse | KB-EUSURVEY-ABUSE-001 + ABUSE-003 cover this | Better completeness — lists abuse types instead of generic "report abuse" |
| VAL-004 | "Can EUSurvey remove a fake survey?" | Abuse | KB-EUSURVEY-ABUSE-004 fills gap | Eliminates wrong-topic — direct answer about removal process |
| VAL-005 | "Our organisation got suspicious emails about EUSurvey" | Abuse | KB-EUSURVEY-ABUSE-005 fills gap | Eliminates fallback — org-specific guidance provided |
| VAL-006 | "What does incomplete contribution mean?" | Contributions | KB-EUSURVEY-CONTRIB-002 fills gap | Eliminates fallback — draft/submitted distinction explained |
| VAL-007 | "How do I reset a respondent?" | Contributions | KB-EUSURVEY-CONTRIB-001 fills gap | Eliminates wrong-topic — no longer retrieves SM-50 config article |
| VAL-008 | "Can I reopen a submission without deleting data?" | Contributions | KB-EUSURVEY-CONTRIB-001 directly answers | Better retrieval — explains reset preserves data |
| VAL-009 | "Can a respondent change their submitted answer?" | Contributions | KB-EUSURVEY-CONTRIB-003 unified answer | Better completeness — covers all paths (self-edit, manager reset) |
| VAL-010 | "What if someone submits twice?" | Contributions | KB-EUSURVEY-CONTRIB-004 fills gap | Eliminates wrong-topic — no longer retrieves anti-bot articles |
| VAL-011 | "What happens when I delete a survey?" | Deletion | KB-EUSURVEY-DELETE-001 replaces SM-23 | Better completeness — full data loss explanation |
| VAL-012 | "Can I undo a survey deletion?" | Deletion | KB-EUSURVEY-DELETE-002 fills gap | Eliminates fallback — grace period recovery path explained |
| VAL-013 | "How do I delete a contribution?" | Deletion | KB-EUSURVEY-DELETE-003 end-user version | Better retrieval — no longer returns API documentation |
| VAL-014 | "Why can't I find the delete button?" | Deletion | KB-EUSURVEY-DELETE-004 fills gap | Eliminates fallback — troubleshooting for missing delete |
| VAL-015 | "Why were some contacts skipped during import?" | Invitations | KB-EUSURVEY-INVITE-001 fills gap | Eliminates fallback — explains validation rules |
| VAL-016 | "Why do Results and Contributions show different numbers?" | Results | KB-EUSURVEY-RESULTS-001 fills gap | Eliminates wrong-topic — directly addresses sync + draft distinction |
| VAL-017 | "My export doesn't have recent submissions" | Results | KB-EUSURVEY-RESULTS-002 enriches SM-87 | Better completeness — actionable steps beyond "wait 12 hours" |
| VAL-018 | "Why does the survey freeze when I submit?" | Technical | KB-EUSURVEY-TECH-001 fills gap | Eliminates fallback — troubleshooting steps provided |
| VAL-019 | "Why was I asked to log in again?" | Technical | KB-EUSURVEY-TECH-002 fills gap | Eliminates fallback — session timeout explanation |
| VAL-020 | "A conditional question doesn't appear for respondents" | Configuration | KB-EUSURVEY-CONFIG-001 fills gap | Eliminates fallback — dependency troubleshooting provided |
| VAL-021 | "Can I edit a survey after publishing?" | Configuration | KB-EUSURVEY-CONFIG-002 enriches SM-67 | Better completeness — Apply Changes workflow explicit |
| VAL-022 | "Why are my results not up to date?" | Results | KB-EUSURVEY-RESULTS-001 + 002 context | Better answer — not just "12 hours" but explains why and what to do |
| VAL-023 | "How do I report abusive survey content?" | Abuse | KB-EUSURVEY-ABUSE-001 retrieves precisely | Fewer wrong-topic answers — no longer confused with phishing/technical |
| VAL-024 | "What happens after I reset a respondent?" | Contributions | KB-EUSURVEY-CONTRIB-001 covers this | Eliminates fallback — explains data preserved, draft created |
| VAL-025 | "I deleted a survey by mistake" | Deletion | KB-EUSURVEY-DELETE-002 directly addresses | Better answer — grace period + contact support path |

---

## Expected Improvement Categories

| Category | Estimated Cases Improving | Mechanism |
|----------|--------------------------|-----------|
| Eliminates fallback (missing KB) | 14 cases | New articles fill gaps where no content existed |
| Better retrieval (replaces broad article) | 5 cases | Specific articles retrieve instead of generic ones |
| Better completeness (enriched answers) | 4 cases | More steps, conditions, limitations provided |
| Eliminates wrong-topic | 4 cases | Distinct articles prevent retrieval confusion |

---

## Regression Risk Assessment

| Concern | Mitigation |
|---------|-----------|
| Retired PM-06_01 was retrieving correctly for basic "report abuse" queries | KB-EUSURVEY-ABUSE-001 uses same keywords + more synonyms |
| Retired SM-23 was retrieving correctly for "delete survey" queries | KB-EUSURVEY-DELETE-001 uses same keywords + more synonyms |
| De-prioritized SM-87 covers "results not up to date" | KB-EUSURVEY-RESULTS-001/002 cover same + more queries |
| New articles may compete with each other | Each article has distinct intent, distinct title, distinct synonyms |
