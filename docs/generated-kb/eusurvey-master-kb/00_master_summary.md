# Master KB Summary

## Overview

| Metric | Count |
|--------|-------|
| Document-derived DOCX articles converted | 276 |
| Source-code-derived articles considered | 70 |
| **Active master articles** | **291** |
| Document-derived articles (active) | 276 |
| Source-code-only articles added (SRC-prefix) | 15 |
| Enriched articles | 42 (enrichment metadata applied) |
| Retired duplicate articles | 8 |
| Review-required articles | 7 (2 unresolved + 5 admin audience) |
| Ready for RAG ingestion | 286 |
| Not ready for ingestion | 5 |

## Master KB Strategy Applied

**Layered Consolidation with Document-Derived as Primary**

1. All 276 document-derived articles converted to Markdown + JSON and included as the user-facing primary layer.
2. 15 unique source-code-derived articles added with SRC- prefix (features not covered in documentation).
3. 8 source-code articles retired as duplicates (their content fully covered by document-derived versions).
4. 42 document-derived articles marked for enrichment with source-code evidence (enrichment metadata referenced in consolidation review).
5. 2 articles flagged as "Do not ingest until reviewed" (ES-024 machine translation, ES-059 webhooks).
6. 5 admin articles flagged with "Ingest with low confidence filter" pending audience decision.

## Numbering Strategy

| Prefix | Source | Count | Description |
|--------|--------|-------|-------------|
| PM- | Document-derived | 22 | Participant FAQ articles |
| SM- | Document-derived | 140 | Survey Manager FAQ articles |
| UG- | Document-derived | 54 | User Guide articles |
| WS- | Document-derived | 50 | Web Services API articles |
| SRC- | Source-code-derived | 15 | Unique source-code articles |

(Remaining 10 document-derived articles use filename-derived IDs without prefix)

## Ingestion Recommendation

The master KB is **ready for chatbot/RAG ingestion** with the following conditions:

1. **286 articles** can be ingested immediately.
2. **5 articles** (admin) should be ingested only if the chatbot serves administrators — flag with audience filter.
3. **2 articles** should not be ingested until product owner reviews machine translation and webhook details.
4. **8 retired articles** must NOT be ingested.
5. DOCX-to-JSON conversion preserves content faithfully but some articles have minimal structured metadata — keyword extraction covers basic retrieval needs.

## Output Location

```
/home/origabe/Projects/EUSurvey1.0-master/docs/generated-kb/eusurvey-master-kb/
```
