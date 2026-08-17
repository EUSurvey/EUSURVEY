# Master KB Generation Decision

## Decision: Generate Consolidation Plan Only — Do NOT Generate Full Master KB Yet

### Reason

The document-derived KB articles are in DOCX format and have not been converted to JSON/Markdown suitable for merging. Generating a full master KB would require:

1. Converting 276 DOCX articles to structured JSON (significant effort).
2. Performing the actual enrichment merges (42 articles, each needing manual review of evidence placement).
3. Resolving 2 conflicts with product owner input.
4. Confirming admin article audience decision.

Generating the master KB without these steps would risk:
- Losing document-derived content quality during conversion.
- Introducing errors in the merge process.
- Publishing unresolved conflicts to the chatbot.

### What Was Produced Instead

A complete **consolidation plan** that enables the next team to:
1. Know exactly which articles to keep, merge, retire, or review.
2. Know what evidence to add where.
3. Know how to number the final KB.
4. Know how to configure RAG ingestion.
5. Know what quality checks must pass.

### Next Steps to Generate the Master KB

Execute this sequence:

1. **Convert DOCX to JSON**: Run a conversion script on all 276 DOCX articles to extract text into the normalized JSON schema. Prompt:
   > "Convert all DOCX articles in docs/kb-docs/KnowledgeBase_2.0/ to JSON format matching the article schema used in docs/generated-kb/eusurvey-source-analysis/articles-json/. Extract title, intent, short_answer, procedure, conditions, troubleshooting, and metadata from each DOCX. Output to docs/generated-kb/eusurvey-master-kb/articles-json/."

2. **Apply enrichments**: For each row in 04_enrichment_table.md, add the specified evidence, permissions, and limitations to the DOC-derived JSON article.

3. **Add SRC-only articles**: Copy the 15 unique source-code articles (SRC-001 to SRC-015 per 07_master_numbering_proposal.md) into the master KB with their new IDs.

4. **Resolve conflicts**: Get product owner input on the 2 conflicts in 05_conflict_table.md and update articles accordingly.

5. **Decide admin audience**: Determine if admin articles should be ingested.

6. **Generate master index**: Create master-index.md and master-index.json listing all final articles.

7. **Run quality gate**: Verify against 09_quality_gate_report.md criteria.

### Output Location (Reserved)

```
/home/origabe/Projects/EUSurvey1.0-master/docs/generated-kb/eusurvey-master-kb/
├── master-index.md
├── master-index.json
├── articles/
└── articles-json/
```

This folder will be populated when the above steps are completed.
