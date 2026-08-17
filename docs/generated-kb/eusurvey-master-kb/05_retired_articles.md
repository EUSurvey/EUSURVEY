# Retired Articles

These articles have been retired from the active master KB. They are duplicates of document-derived articles that provide better user-facing coverage.

**Do NOT ingest these articles into the chatbot/RAG system.**

| Retired ID | Title | Replaced By | Location |
|-----------|-------|-------------|----------|
| ES-001 | How do I create a new survey? | SM-16_creating_a_survey__01 | retired/ES-001_retired.json |
| ES-002 | How do I copy an existing survey? | SM-22_creating_a_survey__07 | retired/ES-002_retired.json |
| ES-003 | How do I import a survey? | SM-18_creating_a_survey__03 | retired/ES-003_retired.json |
| ES-004 | How do I export a survey structure? | SM-21, SM-10_14 | retired/ES-004_retired.json |
| ES-008 | How do I publish a survey? | SM-61_publishing_a_survey__01 | retired/ES-008_retired.json |
| ES-017 | How do I export survey results? | SM-77, SM-90 | retired/ES-017_retired.json |
| ES-034 | What is anonymous survey mode? | SM-15_05 | retired/ES-034_retired.json |
| ES-046 | Web Service API overview | WS-001 to WS-050 series | retired/ES-046_retired.json |

## Retirement Rationale

These source-code-derived articles were created during source analysis to document features. However, the document-derived KB already contains equivalent articles written in user-facing support language with official procedures. The source-code evidence from these retired articles is preserved in:
1. The enrichment table (04_enrichment_table.md in consolidation review)
2. The source traceability report (06_source_traceability_report.md in source analysis)
3. The retired JSON files themselves (in the retired/ folder)

## Enrichment Value Preserved

The retired articles contain valuable source-code evidence (message keys, backend methods, permissions, configuration keys) that should be applied to their document-derived replacements during Phase 2 enrichment. See `04_enrichment_table.md` in the consolidation review for specific enrichment instructions per article.
