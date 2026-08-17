# Consolidation Summary

## Review Scope

| Source | Article Count | Format | Location |
|--------|--------------|--------|----------|
| Source-Code-Derived KB | 70 | MD + JSON | docs/generated-kb/eusurvey-source-analysis/ |
| Document-Derived KB — Participants FAQ | 22 | DOCX | docs/kb-docs/KnowledgeBase_2.0/01-FAQs_Participants/ |
| Document-Derived KB — Survey Manager FAQ (old numbered) | 108 | DOCX | docs/kb-docs/KnowledgeBase_2.0/02-FAQs_SurveyManager/ |
| Document-Derived KB — Survey Manager FAQ (SM- prefix) | 32 | DOCX | docs/kb-docs/KnowledgeBase_2.0/02-FAQs_SurveyManager/ |
| Document-Derived KB — User Guides (UG-) | 54 | DOCX | docs/kb-docs/KnowledgeBase_2.0/03-Users Guides/ |
| Document-Derived KB — Web Services (WS-) | 50 | DOCX | docs/kb-docs/KnowledgeBase_2.0/04-WebServices/ |
| **Total Source-Code-Derived** | **70** | | |
| **Total Document-Derived** | **276** (excl. 4 index files) | | |
| **Grand Total** | **346** | | |

## Consolidation Results

| Metric | Count |
|--------|-------|
| Source-code-derived articles reviewed | 70 |
| Document-derived articles reviewed | 276 |
| Exact duplicates identified | 8 |
| Near duplicates identified | 28 |
| Complementary pairs identified | 34 |
| Conflicts identified | 2 |
| Unique source-code articles (no doc equivalent) | 12 |
| Unique document-derived articles (no source equivalent) | 196 |
| Articles needing functional review | 9 |
| Articles recommended for enrichment | 42 |

## Classification Breakdown

### Source-Code-Derived Articles (70)

| Classification | Count | Description |
|---------------|-------|-------------|
| Has complementary doc-derived match | 34 | Same feature but doc adds user guidance, source adds evidence |
| Has near-duplicate doc match | 28 | Same topic, overlapping content |
| Has exact duplicate doc match | 8 | Same intent and same scope |
| Unique to source-code KB | 12 | Features discovered only in code (admin, bulk ops, webhooks, etc.) |

### Document-Derived Articles (276)

| Classification | Count | Description |
|---------------|-------|-------------|
| Matched to source-code article | 70 | Has a corresponding ES-xxx article |
| Unique to document KB | 196 | No source-code article equivalent |
| Of which: Survey Manager FAQ (unique) | ~75 | Detailed editing, question types, general questions |
| Of which: Web Services API (unique) | ~49 | API-specific, not covered by single ES-046 |
| Of which: User Guides (unique) | ~42 | Quiz guides, prefill guides, WCAG, design best practices |
| Of which: Participants FAQ (unique) | ~14 | Browser support, PDF issues, mobile, troubleshooting |
| Of which: Transition / Policy (unique) | ~16 | SM-16_xx articles on EU institutional transition |

## Recommended Master KB Strategy

**Strategy: Layered Consolidation with Document-Derived as Primary**

1. **Keep all 276 document-derived articles as the user-facing layer.** They are written in support language suitable for chatbot delivery.

2. **Enrich ~42 document-derived articles with source-code evidence** (permissions, message keys, backend methods, configuration flags, limitations discovered in code).

3. **Add ~12 unique source-code articles** to the master KB as new articles (features not documented elsewhere: admin functions, bulk operations, webhooks, automatic deletion internals, etc.).

4. **Retire 8 exact-duplicate source-code articles** (their content is fully covered by the document-derived versions with better user-facing language).

5. **Merge 28 near-duplicate pairs** by enriching the document-derived version with source-code traceability, keeping the document version as master.

6. **Flag 2 conflicts** for product owner review before ingestion.

7. **Keep the 50 WS-xxx API articles** as the definitive API reference layer — they provide far more detail than the single ES-046 source-code article.

8. **Preserve the 16 SM-16_xx transition articles** as-is — they are policy content with no source-code equivalent.

### Numbering Strategy

Recommended: **Keep document-derived numbering as primary, add source-only articles in a new SRC- prefix range.**

- Participant FAQ: retain original numbering (01_xx through 07_xx)
- Survey Manager FAQ: retain original numbering (01 through 118, SM-xx)
- User Guides: retain UG-xxx
- Web Services: retain WS-xxx
- Source-code unique articles: new range SRC-001 through SRC-012

This avoids breaking any existing references while cleanly adding new content.
