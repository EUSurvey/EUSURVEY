# Ingestion Rules

## What to Ingest

Ingest **only** the Markdown files in:

```
/home/origabe/Projects/EUSurvey1.0-master/docs/generated-kb/eusurvey-master-kb-md-only/articles/*.md
```

Each `.md` file is one complete article ready for RAG retrieval.

## What NOT to Ingest

Do not ingest:

* Any file outside the `articles/` folder
* DOCX files (from any location)
* Retired articles (not present in this folder)
* Review-required articles (not present in this folder)
* Source traceability reports as standalone answer content
* Consolidation reports
* Quality reports
* The mixed master KB folder directly
* Index or manifest files (these are for governance only)

## File Format

All ingestible content is in **Markdown** format.

No other file format is needed for ingestion. No companion files are required.

## Chunking Recommendations

* Each article is designed as **one retrieval unit** — avoid splitting articles across chunks if possible.
* If chunking is required due to size limits, split at `## ` section headers.
* Always include the Title and Short Answer in any chunk for context.
* The `## Short Answer` section alone can serve as a quick-response chunk.

## Recommended Retrieval Boosts

Boost these sections for better retrieval relevance:

* **Title** (highest weight — user questions often match titles)
* **Short Answer** (high weight — the direct answer)
* **Intent / Description** (high weight — alternative phrasing of the question)
* **Keywords** (in Metadata section — exact terms for matching)
* **Synonyms** (in Metadata section — alternate user phrasings)
* **Procedure steps** (medium weight — operational content)
* **EUSurvey area** (for topic scoping)
* **Article type** (for response style selection)

## Recommended Metadata Filters

If the RAG system supports faceted filtering:

| Filter | Purpose |
|--------|---------|
| Role | Route questions to correct audience (Respondent, Survey Owner, API User) |
| Article type | Distinguish How-To vs Concept vs Troubleshooting |
| EUSurvey area | Scope by module (Editor, Results, Invitations, Translations, etc.) |
| Authority / Source type | Prefer document-derived for user guidance |

## Deduplication

* No duplicate articles exist in this folder.
* All retired duplicates have been removed before this folder was created.
* If the RAG system detects similar embeddings, prefer the article with the longer Short Answer.
