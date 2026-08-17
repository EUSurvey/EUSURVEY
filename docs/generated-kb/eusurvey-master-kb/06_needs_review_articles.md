# Needs Review Articles

These articles require product owner or functional review before full ingestion.

## Category 1: Unresolved Technical Questions (2)

| Article ID | Title | Issue | Risk | Recommendation |
|-----------|-------|-------|------|---------------|
| ES-024 | How do I request machine translation? | Machine translation requires `mt.use.ec.mt` or Microsoft keys — disabled by default. Availability is deployment-specific. | Medium — users may attempt to use unavailable feature | **Do not ingest until reviewed.** Ask PO: "Which MT services are enabled in production?" |
| ES-059 | How do webhooks work? | Webhook payload format not documented. No retry logic found. May be distinct from WS-019 API export webhook. | Low — feature works but details unclear | **Do not ingest until reviewed.** Ask PO: "What payload does the per-submission webhook send?" |

**Location**: `review-required/ES-024_review.json`, `review-required/ES-059_review.json`

## Category 2: Admin Audience Decision (5)

| Article ID | Title | Issue | Recommendation |
|-----------|-------|-------|---------------|
| SRC-006 | How do administrators manage users? | Admin-only feature; no DOC equivalent. Should chatbot expose admin procedures? | **Ingest with low confidence filter** if chatbot serves admins |
| SRC-007 | How do administrators search surveys? | Admin-only | Same as above |
| SRC-008 | How do administrators freeze a survey? | Admin-only; describes policy enforcement | Same as above |
| SRC-009 | How do administrators configure system messages? | Admin-only | Same as above |
| SRC-010 | How do administrators ban a user? | Admin-only; describes punitive action | Same as above |

**Decision needed**: Does the chatbot serve administrators or only survey managers and participants?

- If **yes** (chatbot serves admins): Change ingestion_status to "Ready for ingestion" for SRC-006 through SRC-010.
- If **no** (chatbot is participant/manager only): Change ingestion_status to "Do not ingest - audience mismatch".

## Category 3: Conflict-Adjacent (2 document-derived articles)

| Article ID | Title | Issue | Recommendation |
|-----------|-------|-------|---------------|
| SM-59 (in Survey Manager FAQ) | What does request machine translation mean? | References feature that may be unavailable in some deployments | **Ingest with note**: "Availability depends on deployment configuration" |
| WS-019 | Use webhooks for result export completion | May describe different mechanism than per-submission webhook | **Ingest as-is** — it covers API-level webhooks specifically |

## Summary

| Status | Count |
|--------|-------|
| Do not ingest until reviewed | 2 |
| Ingest with low confidence filter (admin) | 5 |
| Ingest with caveat note | 2 |
| **Total requiring attention** | **9** |
