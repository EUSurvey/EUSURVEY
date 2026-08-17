# Final Consistency & Packaging Review

**Reviewed**: 2026-07-13T09:41
**Reviewer**: Automated + manual review of all 20 articles, manifest, decision log, and validation mapping

---

## 1. Duplicate or Overlapping Intents

**Status: ✅ No blocking overlaps found**

Pairs reviewed for potential retrieval collision:

| Pair | Finding |
|------|---------|
| ABUSE-001 (report spam) vs ABUSE-003 (abuse types) | Distinct: 001 = "how to report" (action), 003 = "what types exist" (reference). Titles are clearly different. |
| ABUSE-002 (phishing emails) vs ABUSE-005 (org guidance) | Distinct: 002 = individual user, 005 = organisation-level response. Different roles and context. |
| CONTRIB-001 (manager resets) vs CONTRIB-003 (respondent changes) | Distinct: 001 = manager action, 003 = respondent self-service. Different roles, different entry point. |
| RESULTS-001 (count mismatch) vs RESULTS-002 (export stale) | Distinct: 001 = "why numbers differ", 002 = "why export is missing data". Different trigger questions. |
| DELETE-001 (consequences) vs DELETE-002 (undo) | Distinct: 001 = "what happens", 002 = "can I recover". Different user state (before vs after mistake). |

**Minor overlap**: ABUSE-001 mentions all 6 abuse types in its procedure section, which overlaps with ABUSE-003's purpose. This is acceptable — ABUSE-001 provides the full workflow including type selection, while ABUSE-003 is a standalone reference for "what do the types mean". They serve different retrieval intents.

---

## 2. Terminology Consistency

**Status: ✅ Consistent**

| Term | Usage |
|------|-------|
| "contribution" | Used consistently as the primary EUSurvey term for a submitted response |
| "response" / "answer" | Used sparingly as natural-language synonyms in context/metadata |
| "draft" | Consistently means "saved but not submitted" |
| "survey manager" vs "survey owner" | Both used where appropriate (owner = creator, manager = privileged user) |
| "Apply Changes" | Capitalised consistently when referring to the specific UI action |
| "EU Login" | Capitalised consistently |
| "Report Abuse" | Capitalised when referring to the specific UI element |

---

## 3. Old Article → New Article Mappings

**Status: ✅ Complete and unambiguous**

| Old Article | Mapping | Status |
|-------------|---------|--------|
| PM-06_01 | → KB-EUSURVEY-ABUSE-001 (primary replacement) | Clear in manifest |
| SM-23 | → KB-EUSURVEY-DELETE-001 + DELETE-002 | Clear in manifest |
| SM-87 | → de-prioritized, replaced by RESULTS-001 + RESULTS-002 | Clear in manifest |
| SM-50 | → de-prioritized for respondent intent, replaced by CONTRIB-003; SM-50 kept for config | Clear in manifest |
| SM-48 | → de-prioritized, replaced by CONTRIB-003 | Clear in manifest |
| WS-042 | → kept for API users only; EUSURVEY-DELETE-003 is end-user version | Clear in manifest |

---

## 4. Conflicts with Retained Legacy Articles

**Status: ✅ No dangerous conflicts**

| Retained Article | Potential Conflict | Assessment |
|-----------------|-------------------|------------|
| SM-34 (visibility/dependencies how-to) | CONFIG-001 (troubleshooting) | No conflict: SM-34 explains "how to set up", CONFIG-001 explains "why it doesn't work". Complementary. |
| SM-103 (import contacts procedure) | INVITE-001 (why contacts skipped) | No conflict: SM-103 = procedure, INVITE-001 = troubleshooting when it fails. Complementary. |
| SM-09_11 (inactive survey deletion) | DELETE-001 (manual deletion) | No conflict: different triggers (automatic vs manual). Clearly distinct intents. |
| SM-46/SM-47 (prevent duplicates config) | CONTRIB-004 (what happens if submit twice) | No conflict: SM-46/47 = prevention setup, CONTRIB-004 = what actually occurs. Different angles. |
| SM-67 (correct error in published survey) | CONFIG-002 (edit after publishing) | **Mild overlap** but acceptable: SM-67 de-prioritized, CONFIG-002 provides the complete Apply Changes workflow. SM-67's broader framing ("can I correct it?") still valid for imprecise queries. |

---

## 5. Title Quality (User Wording Match)

**Status: ✅ All titles match user wording**

Every title is phrased as a question users would naturally ask. Verified patterns:

- "How do I..." (action queries) — 3 titles
- "Why..." (troubleshooting) — 7 titles
- "What happens..." (consequence queries) — 3 titles
- "Can I..." (possibility queries) — 4 titles
- "What does..." (definition queries) — 1 title
- "What should I do..." (guidance queries) — 1 title
- "What abuse types..." (reference queries) — 1 title

No titles use internal jargon or developer language.

---

## 6. Role / Condition / Limitation Details

**Status: ✅ All articles include conditions**

Spot-checked:
- CONTRIB-001: States privilege level ≥ 2 required, eVote restriction documented
- DELETE-001: States soft-delete, grace period, "do not delete" flag
- INVITE-001: States email validation, duplicate detection, supported formats
- CONFIG-001: States "Apply Changes required", dependency order, page boundaries

All 20 articles have populated "Important Conditions / Limitations" sections.

---

## 7. Validation Cases Coverage

**Status: ✅ All 25 VAL cases map to articles in this batch**

Verified: Every VAL-001 through VAL-025 references at least one article that exists in the 20-article package.

---

## 8. Decision Log Completeness

**Status: ⚠️ Minor discrepancy (non-blocking)**

The decision log contains 26 items (D-001 through D-026). Of these:
- **20 items** have corresponding new articles in this batch
- **3 items** (D-014, D-016, D-017) are correctly marked as lower priority and documented in the backlog
- **2 items** (D-020 charts, D-023 technical problem message) are partially covered by adjacent articles (RESULTS-002 mentions charts; TECH-001 mentions "technical problem" message) but don't have dedicated standalone articles
- **1 item** (D-026 translations) is correctly marked as backlog

This is acceptable: the manifest explicitly lists these as Phase 2 backlog items.

---

## Corrections Applied During This Review

| # | Issue | Fix Applied |
|---|-------|------------|
| 1 | ABUSE-001 referenced ABUSE-002 as "see separate article" instead of specific ID | Fixed: now says "(see: KB-EUSURVEY-ABUSE-002)" |
| 2 | ABUSE-003 referenced ABUSE-002 generically | Fixed: specific ID |
| 3 | ABUSE-004 referenced ABUSE-002 generically | Fixed: specific ID |
| 4 | CONTRIB-001 referenced CONTRIB-002 generically | Fixed: specific ID |
| 5 | DELETE-001 referenced DELETE-002 generically | Fixed: specific ID |
| 6 | DELETE-002 referenced DELETE-001 generically | Fixed: specific ID |
| 7 | TECH-001 referenced TECH-002 generically | Fixed: specific ID |

---

## Remaining Issues (Non-Blocking)

| # | Issue | Severity | Recommendation |
|---|-------|----------|----------------|
| 1 | D-020 (charts not updated) has no standalone article | Low | RESULTS-002 mentions charts in "Out of Scope". Add to Phase 2 backlog if chart queries fail post-reingestion. |
| 2 | D-023 (technical problem message) has no standalone article | Low | TECH-001 includes this as a troubleshooting bullet. Monitor post-reingestion; create standalone if needed. |
| 3 | Role field format varies slightly ("Survey Manager" vs "Survey Manager, Form Administrator") | Cosmetic | Does not affect retrieval. Normalize in a future batch if needed. |

---

## ✅ READY FOR REINGESTION

**Confirmation**: The package passes all consistency checks. No major issues remain. The 7 cross-reference fixes have been applied. The 20 articles are structurally consistent, terminologically aligned, and properly mapped to the manifest, decision log, and validation cases.

**Proceed with ingestion of all 20 KB-EUSURVEY-*.md files** following the instructions in PHASE-3_reingestion-manifest.md.
