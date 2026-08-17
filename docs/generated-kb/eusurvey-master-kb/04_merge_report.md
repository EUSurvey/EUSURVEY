# Merge Report

## Actions Applied

### 1. Source-Code-Only Articles Added (15)

| SRC ID | Original ES ID | Title | Ingestion Status |
|--------|---------------|-------|-----------------|
| SRC-001 | ES-009 | How do I unpublish a survey? | Ready for ingestion |
| SRC-002 | ES-011 | How do I clear unapplied changes? | Ready for ingestion |
| SRC-003 | ES-027 | How do I send reminders? | Ready for ingestion |
| SRC-004 | ES-037 | How do I restore an archived survey? | Ready for ingestion |
| SRC-005 | ES-040 | How do I share my address book? | Ready for ingestion |
| SRC-006 | ES-041 | How do administrators manage users? | Ingest with low confidence filter |
| SRC-007 | ES-042 | How do administrators search surveys? | Ingest with low confidence filter |
| SRC-008 | ES-043 | How do administrators freeze a survey? | Ingest with low confidence filter |
| SRC-009 | ES-044 | How do administrators configure system messages? | Ingest with low confidence filter |
| SRC-010 | ES-045 | How do administrators ban a user? | Ingest with low confidence filter |
| SRC-011 | ES-048 | How do I perform bulk operations? | Ready for ingestion |
| SRC-012 | ES-057 | How do I enable confirmation emails? | Ready for ingestion |
| SRC-013 | ES-064 | How do I recalculate quiz scores? | Ready for ingestion |
| SRC-014 | ES-067 | How does the progress bar work? | Ready for ingestion |
| SRC-015 | ES-068 | How do I change the survey owner? | Ready for ingestion |

### 2. Articles Retired as Duplicates (8)

| Retired ID | Title | Replaced By | Reason |
|-----------|-------|-------------|--------|
| ES-001 | How do I create a new survey? | SM-16_creating_a_survey__01 | Same intent, DOC has better user-facing steps |
| ES-002 | How do I copy an existing survey? | SM-22_creating_a_survey__07 | Same intent |
| ES-003 | How do I import a survey? | SM-18_creating_a_survey__03 | Same intent |
| ES-004 | How do I export a survey structure? | SM-21 + SM-10_14 | Same intent covered by DOC |
| ES-008 | How do I publish a survey? | SM-61_publishing_a_survey__01 | Near duplicate |
| ES-017 | How do I export survey results? | SM-77 + SM-90 | Same intent |
| ES-034 | What is anonymous survey mode? | SM-15_05 | SM article more comprehensive |
| ES-046 | Web Service API overview | WS-001 to WS-050 | WS series far more detailed |

### 3. Articles Flagged for Review (2)

| Article ID | Title | Reason | Recommendation |
|-----------|-------|--------|---------------|
| ES-024 | How do I request machine translation? | Server config dependent; availability unclear | Do not ingest until deployment confirmed |
| ES-059 | How do webhooks work? | Payload format undocumented; distinct from WS-019 | Do not ingest until product owner clarifies |

### 4. Enrichment Decisions (42 articles referenced)

The consolidation review's `04_enrichment_table.md` identifies 42 document-derived articles that should receive source-code evidence. The enrichment metadata is documented in that table. The actual content enrichment (adding evidence into article bodies) is designated as a Phase 2 activity requiring manual review per article.

For this initial master KB generation:
- All 42 target articles are included as-is from document sources.
- The enrichment table serves as the work plan for Phase 2.
- Articles are marked as "Ready for ingestion" since their document-derived content is already accurate.

### 5. Conflicts Handled (2)

| Conflict | Resolution Applied |
|----------|-------------------|
| Machine translation availability | SM-59 included as-is; ES-024 placed in review-required/ |
| Webhook scope (submission vs export) | WS-019 included as-is; ES-059 placed in review-required/ |

Both conflicts are documented but NOT silently resolved. The affected source-code articles are held back while the document-derived versions proceed to ingestion.
