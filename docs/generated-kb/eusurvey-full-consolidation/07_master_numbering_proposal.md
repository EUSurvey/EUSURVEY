# Master Numbering Proposal

## Recommended Strategy: Option 3 — Retain Document Numbering + SRC Prefix for Source-Only

### Rationale

The document-derived KB already has an established numbering system used across multiple categories. The source-code-derived KB uses ES-xxx numbering which was created for analysis purposes only. Breaking existing document references would create unnecessary confusion.

### Proposed Numbering

| Category | Prefix | Range | Source | Example |
|----------|--------|-------|--------|---------|
| Participants FAQ | PM- or 01_xx-07_xx | 22 articles | Document KB | PM-05_03 (edit contribution) |
| Survey Manager FAQ (numbered) | (no prefix, numeric) | 108 articles | Document KB | 16 (create survey), 61 (publish) |
| Survey Manager FAQ (new) | SM- | 32 articles | Document KB | SM-09_15 (email reports) |
| User Guides | UG- | 54 articles | Document KB | UG-009 (quiz settings) |
| Web Services | WS- | 50 articles | Document KB | WS-020 (XML results) |
| Source-code unique articles | SRC- | 12 articles | Source-code KB | SRC-001 (unpublish survey) |

### Source-Code Unique Articles — New SRC IDs

| Proposed SRC ID | Original ES ID | Title |
|----------------|---------------|-------|
| SRC-001 | ES-009 | How do I unpublish a survey? |
| SRC-002 | ES-011 | How do I clear unapplied changes? |
| SRC-003 | ES-027 | How do I send reminders? |
| SRC-004 | ES-037 | How do I restore an archived survey? |
| SRC-005 | ES-040 | How do I share my address book? |
| SRC-006 | ES-041 | How do administrators manage users? |
| SRC-007 | ES-042 | How do administrators search surveys? |
| SRC-008 | ES-043 | How do administrators freeze a survey? |
| SRC-009 | ES-044 | How do administrators configure system messages? |
| SRC-010 | ES-045 | How do administrators ban a user? |
| SRC-011 | ES-048 | How do I perform bulk operations? |
| SRC-012 | ES-057 | How do I enable confirmation emails? |
| SRC-013 | ES-064 | How do I recalculate quiz scores? |
| SRC-014 | ES-067 | How does the progress bar work? |
| SRC-015 | ES-068 | How do I change the survey owner? |

### Implementation Notes

1. **Document-derived articles** keep their original IDs unchanged.
2. **Source-code articles that are exact/near duplicates** are retired (no ID assignment in master).
3. **Source-code articles that are unique** get a new SRC-xxx ID.
4. **Enrichment metadata** from source-code articles is added as internal traceability annotations to the document-derived articles, not changing their IDs.
5. **Cross-references** between articles should use the master ID (DOC or SRC prefix).

### Why Not Full Renumbering?

- 276 document articles already have stable IDs potentially referenced elsewhere.
- Full renumbering creates a massive migration effort with risk of broken references.
- The SRC prefix clearly identifies new content from source analysis.
- This approach is additive, not disruptive.
