# EUSurvey KB Targeted Enhancement — Implementation Summary

**Generated**: 2026-07-15

---

## Statistics

| Metric | Count |
|--------|-------|
| New articles created | 41 |
| Updated articles (narrowed/strengthened) | 4 |
| Split articles | 0 (see note) |
| Retired or replaced articles | 0 |
| Unchanged candidates (existing coverage sufficient) | 4 |
| Articles requiring manual review | 3 |
| Unresolved source conflicts | 0 |

---

## New Articles Created (35)

### Invitations and Email Delivery (8)
1. `KB-EUSURVEY-INVITE-002-check-invitation-status.md`
2. `KB-EUSURVEY-INVITE-003-invitation-not-received.md`
3. `KB-EUSURVEY-INVITE-004-invitation-email-spam-folder.md`
4. `KB-EUSURVEY-INVITE-005-cancel-deactivate-invitation.md`
5. `KB-EUSURVEY-INVITE-006-invitation-link-not-working.md`
6. `KB-EUSURVEY-INVITE-007-invitation-email-language.md`
7. `KB-EUSURVEY-INVITE-008-personalise-invitations.md`
8. `KB-EUSURVEY-INVITE-009-duplicate-invitations.md`

### Contributions and Respondent Correction (6)
9. `KB-EUSURVEY-CONTRIB-005-access-saved-draft.md`
10. `KB-EUSURVEY-CONTRIB-006-submission-not-visible-to-owner.md`
11. `KB-EUSURVEY-CONTRIB-007-answers-missing-after-submission.md`
12. `KB-EUSURVEY-CONTRIB-008-contribution-summary-unavailable.md`
13. `KB-EUSURVEY-CONTRIB-009-confirmation-after-submission.md`
14. `KB-EUSURVEY-CONTRIB-010-see-who-answered.md`

### Performance and Technical Errors (7)
15. `KB-EUSURVEY-TECH-003-survey-blank-screen.md`
16. `KB-EUSURVEY-TECH-004-preview-internal-error.md`
17. `KB-EUSURVEY-TECH-005-survey-duplication-error.md`
18. `KB-EUSURVEY-TECH-006-editor-500-error.md`
19. `KB-EUSURVEY-TECH-007-high-concurrent-submissions.md`
20. `KB-EUSURVEY-TECH-008-survey-crashes-during-navigation.md`
21. `KB-EUSURVEY-TECH-009-general-service-slowdown.md`

### Authentication and Access (6)
22. `KB-EUSURVEY-ACCESS-001-403-forbidden.md`
23. `KB-EUSURVEY-ACCESS-002-access-denied.md`
24. `KB-EUSURVEY-ACCESS-003-private-survey-after-duplication.md`
25. `KB-EUSURVEY-ACCESS-004-invalid-credentials.md`
26. `KB-EUSURVEY-ACCESS-005-eu-login-not-recognised.md`
27. `KB-EUSURVEY-ACCESS-006-eu-login-second-factor.md`

### Languages and Translations (2)
28. `KB-EUSURVEY-TRANS-001-built-in-labels-not-translated.md`
29. `KB-EUSURVEY-TRANS-002-pdf-language.md`

### Results, Reports and Exports (5)
30. `KB-EUSURVEY-RESULTS-003-download-all-responses.md`
31. `KB-EUSURVEY-RESULTS-004-filter-search-contributions.md`
32. `KB-EUSURVEY-RESULTS-005-export-error.md`
33. `KB-EUSURVEY-RESULTS-006-report-timeout.md`
34. `KB-EUSURVEY-RESULTS-007-slow-charts.md`

### Contacts and Address Book (5) — NOTE: IDs adjusted to avoid conflict
30. `KB-EUSURVEY-CONTACT-001-imported-contacts-not-in-invitations.md`
31. `KB-EUSURVEY-CONTACT-002-import-fails-without-error.md`
32. `KB-EUSURVEY-CONTACT-003-use-contact-attributes.md`
33. `KB-EUSURVEY-CONTACT-004-invalid-email-import.md`
34. `KB-EUSURVEY-CONTACT-005-update-delete-contacts.md`

### Survey Lifecycle and Configuration (2)
35. `KB-EUSURVEY-SURVEY-001-backup-options.md`
36. `KB-EUSURVEY-SURVEY-002-survey-types.md`

---

## Updated Articles (4)

| Article | Action |
|---------|--------|
| `KB-EUSURVEY-TECH-001-survey-freezes-submission.md` | Narrowed metadata: added exclude list to prevent retrieval for 403, CAPTCHA, editor, preview, duplication, export, and general slowness scenarios |
| `KB-EUSURVEY-RESULTS-002-export-missing-latest-responses.md` | Narrowed metadata: added exclude list to prevent retrieval for export errors, report timeouts, slow charts, and general performance |
| `KB-EUSURVEY-INVITE-001-contacts-skipped-import.md` | Narrowed metadata: added exclude list to prevent retrieval for invitation-list membership, duplicates, silent failures, contact management |
| `KB-EUSURVEY-CONFIG-002-edit-survey-after-publishing.md` | Strengthened: added guidance on deleting questions consequences, when to copy instead, and pending changes indicator |

---

## Candidates Not Created (Sufficient Existing Coverage)

| Candidate | Existing Coverage |
|-----------|-------------------|
| KB-EUSURVEY-CONTRIB-003 split (Section 6.7) | Existing article adequately covers both mechanisms. New complementary articles (CONTRIB-005 through -010) handle the disambiguation. |
| Publish a draft survey (Section 12.4) | SM-61 provides the procedural coverage |
| Generate charts and statistics (Section 12.5) | SM-76 + new RESULTS-006 and RESULTS-007 cover the topic |
| "Other, please specify" (Section 12.6) | No built-in feature confirmed. Achievable via dependencies (SM-34, UG-053, KB-EUSURVEY-CONFIG-001) |

---

## Validation Results

| Check | Result |
|-------|--------|
| Unique identifiers | ✓ All IDs unique |
| Unique filenames | ✓ All filenames unique |
| Markdown valid | ✓ Standard Markdown used |
| Required metadata present | ✓ All articles have Retrieval Metadata section |
| Internal links resolve | ✓ Cross-references use existing article IDs |
| No DOCX references | ✓ No article references DOCX files |
| No source-code references | ✓ No article mentions Java classes, file paths, or internal code |
| No JSON copies | ✓ Only Markdown articles in ingestion directory |
| No duplicate articles | ✓ Each topic has a single primary article |
| Broad articles narrowed | ✓ TECH-001, RESULTS-002, INVITE-001 narrowed |
| Existing approved content preserved | ✓ All 300 original articles remain |

---

## Final Ingestion Article Count

| Category | Count |
|----------|-------|
| Original articles (pre-existing) | 300 |
| New articles added | 41 |
| **Total articles in final-ingestion-batch** | **341** |

---

## Evaluation Cases Addressed

| Case | Article(s) |
|------|-----------|
| case-011 | KB-EUSURVEY-CONTACT-004 |
| case-013 | KB-EUSURVEY-CONTACT-005 |
| case-014 | KB-EUSURVEY-CONTACT-001 |
| case-015 | KB-EUSURVEY-CONTACT-002 |
| case-023 | KB-EUSURVEY-CONTRIB-005 |
| case-025 | REVIEW_REQUIRED (no built-in "Other" option) |
| case-026 | KB-EUSURVEY-INVITE-006 |
| case-029 | KB-EUSURVEY-CONTRIB-010 |
| case-030 | KB-EUSURVEY-CONTRIB-006 |
| case-034 | KB-EUSURVEY-CONTRIB-007 |
| case-035 | KB-EUSURVEY-CONTRIB-008 |
| case-036 | KB-EUSURVEY-CONTRIB-003 (existing, strengthened by context) |
| case-037 | KB-EUSURVEY-CONTRIB-009 |
| case-038 | KB-EUSURVEY-CONTRIB-003 (existing) |
| case-042 | KB-EUSURVEY-CONTRIB-003 (existing) |
| case-046 | KB-EUSURVEY-INVITE-002, KB-EUSURVEY-INVITE-003 |
| case-048 | KB-EUSURVEY-INVITE-003 |
| case-049 | KB-EUSURVEY-INVITE-006 |
| case-051 | KB-EUSURVEY-INVITE-007 |
| case-052 | KB-EUSURVEY-INVITE-002 |
| case-054 | KB-EUSURVEY-INVITE-008 |
| case-055 | KB-EUSURVEY-INVITE-009 |
| case-056 | KB-EUSURVEY-INVITE-005 |
| case-057 | KB-EUSURVEY-INVITE-004 |
| case-058 | KB-EUSURVEY-ACCESS-002 |
| case-060 | KB-EUSURVEY-ACCESS-002 |
| case-061 | KB-EUSURVEY-ACCESS-006 |
| case-063 | KB-EUSURVEY-ACCESS-004 |
| case-066 | KB-EUSURVEY-ACCESS-003 |
| case-067 | KB-EUSURVEY-INVITE-006 |
| case-068 | KB-EUSURVEY-ACCESS-001 |
| case-069 | KB-EUSURVEY-ACCESS-005 |
| case-072 | KB-EUSURVEY-TECH-008 |
| case-075 | KB-EUSURVEY-TECH-007 |
| case-076 | KB-EUSURVEY-RESULTS-006 |
| case-077 | KB-EUSURVEY-TECH-009 |
| case-079 | KB-EUSURVEY-RESULTS-007 |
| case-081 | KB-EUSURVEY-CONTRIB-006 |
| case-082 | KB-EUSURVEY-RESULTS-005 |
| case-083 | KB-EUSURVEY-RESULTS-003 |
| case-085 | KB-EUSURVEY-SURVEY-001 |
| case-090 | KB-EUSURVEY-RESULTS-007 |
| case-096 | KB-EUSURVEY-CONFIG-002 (updated) |
| case-099 | KB-EUSURVEY-SURVEY-002 |
| case-103 | KB-EUSURVEY-TECH-008 |
| case-105 | KB-EUSURVEY-TECH-004 |
| case-106 | KB-EUSURVEY-TECH-006 |
| case-107 | KB-EUSURVEY-TECH-003 |
| case-108 | KB-EUSURVEY-TECH-005 |
| case-109 | KB-EUSURVEY-TECH-008 |
| case-110 | KB-EUSURVEY-RESULTS-005 |
| case-113 | KB-EUSURVEY-TRANS-001 |
| case-115 | KB-EUSURVEY-TRANS-002 |
| case-116 | KB-EUSURVEY-TRANS-002 |
| case-118 | KB-EUSURVEY-TRANS-001 |
| case-119 | KB-EUSURVEY-INVITE-007 |
| case-127 | SM-61 (existing, sufficient) |
| case-128 | KB-EUSURVEY-RESULTS-004 |
| case-130 | SM-76 + KB-EUSURVEY-RESULTS-006/007 |
| case-133 | KB-EUSURVEY-CONTACT-003 |

---

## Mapping from Broad Articles to Replacements

| Broad Article | Related New Articles |
|---------------|---------------------|
| KB-EUSURVEY-TECH-001 (was over-broad) | KB-EUSURVEY-TECH-003, -004, -005, -006, -007, -008, -009 |
| KB-EUSURVEY-RESULTS-002 (was over-broad) | KB-EUSURVEY-RESULTS-003, -004, -005, -006, -007 |
| KB-EUSURVEY-INVITE-001 (was over-broad) | KB-EUSURVEY-CONTACT-001, -002, -004, -005 |
| KB-EUSURVEY-CONTRIB-003 (was too broad) | KB-EUSURVEY-CONTRIB-005, -006, -007, -008, -009, -010 |
