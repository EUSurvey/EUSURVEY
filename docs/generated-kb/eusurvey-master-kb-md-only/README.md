# EUSurvey Master Knowledge Base — Markdown-Only (RAG Ingestion)

## Purpose

This is the **finalized Markdown-only** EUSurvey Knowledge Base prepared for chatbot/RAG pipeline ingestion.

All articles are self-contained Markdown files. No external files (JSON, DOCX, PDF) are required.

## Key Facts

| Metric | Value |
|--------|-------|
| Articles available for ingestion | 286 |
| Articles excluded (pending review) | 5 |
| File format | Markdown (.md) only |
| Self-contained articles | Yes |
| DOCX files required | No |
| JSON files required | No |
| JSON files present | None |
| Ready for RAG ingestion | Yes |
| Date finalized | 2026-07-09 |

## RAG Ingestion Path

Ingest **only** the Markdown files in:

```
articles/*.md
```

Nothing else in this folder is intended for RAG ingestion. The other `.md` files (README, indexes, reports) are governance documents for human use only.

## What Is NOT Needed

- JSON files — none exist, none are required
- DOCX source files — content has been fully extracted into Markdown
- The mixed master KB folder (`eusurvey-master-kb/`) — do not ingest directly
- External source code files — not needed for the chatbot

## Confirmations

- ✓ All 286 Markdown articles are self-contained
- ✓ No article depends on opening a JSON, DOCX, or any other external file
- ✓ No JSON files exist in this folder
- ✓ No DOCX files exist in this folder
- ✓ Only articles with ingestion approval are included
- ✓ Retired duplicate articles are excluded
- ✓ DOCX filenames appear only in Source Traceability sections (for governance tracing)
- ✓ No application source code was modified

## Source

Generated from the consolidated EUSurvey Knowledge Base at:
```
docs/generated-kb/eusurvey-master-kb/
```

Which was built from:
- Document-derived KB: `docs/kb-docs/KnowledgeBase_2.0/` (276 DOCX articles)
- Source-code-derived KB: `docs/generated-kb/eusurvey-source-analysis/` (70 articles)
- Consolidation review: `docs/generated-kb/eusurvey-full-consolidation/`
