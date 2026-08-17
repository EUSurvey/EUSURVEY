# Exception Note — Review/Validation Reports Preserved Separately

**Date**: 2026-07-13

---

## Reports Preserved Outside the Final Ingestion Folder

The following review and validation artifacts are intentionally **not** included in the final ingestion folder (`final-ingestion-batch/`). They are preserved for traceability and post-ingestion validation but must not be ingested into the RAG system.

### Preserved locations:

| Report | Location | Purpose |
|--------|----------|---------|
| Coverage Gap Analysis | `docs/generated-kb/kb-coverage-improvement/00_coverage-gap-analysis.md` | Analysis of KB gaps with code evidence |
| Original Coverage Summary | `docs/generated-kb/kb-coverage-improvement/99_coverage-summary.md` | Summary of first improvement batch |
| Decision Log (Phase 2) | `docs/generated-kb/kb-reingestion-batch/PHASE-2_decision-log.md` | Classification of all problematic items |
| Reingestion Manifest (Phase 3) | `docs/generated-kb/kb-reingestion-batch/PHASE-3_reingestion-manifest.md` | Detailed ingestion instructions |
| Validation Mapping (Phase 4) | `docs/generated-kb/kb-reingestion-batch/PHASE-4_validation-mapping.md` | Test cases for post-ingestion eval |
| Final Summary (Phase 5) | `docs/generated-kb/kb-reingestion-batch/PHASE-5_final-summary.md` | Package statistics and backlog |
| Final Review | `docs/generated-kb/kb-reingestion-batch/FINAL-REVIEW.md` | Consistency review results |
| Final Ingestion Manifest | `docs/generated-kb/final-ingestion-manifest.md` | Full file listing with status |
| Cleanup Summary | `docs/generated-kb/final-ingestion-cleanup-summary.md` | This cleanup run's statistics |

### Previous KB generation artifacts (not part of this ingestion):

| Folder | Status |
|--------|--------|
| `docs/generated-kb/eusurvey-master-kb/` | Previous KB generation — superseded by md-only set |
| `docs/generated-kb/eusurvey-source-analysis/` | Source analysis articles — already merged into md-only set |
| `docs/generated-kb/eusurvey-full-consolidation/` | Consolidation reports — historical reference only |
| `docs/generated-kb/eusurvey-master-kb-md-only/` | Source for retained legacy articles (the canonical pre-batch set) |

---

## Reason for Exclusion

These reports contain:
- Internal analysis notes
- Decision rationale
- Validation test cases
- Historical traceability

They are **operational documentation**, not user-facing KB content. Including them in RAG ingestion would pollute retrieval with meta-content about the KB itself rather than answers about EUSurvey.

---

## Post-Ingestion Validation

After ingestion of the `final-ingestion-batch/` folder, use:
- `PHASE-4_validation-mapping.md` for the 25 test cases to re-evaluate
- `FINAL-REVIEW.md` for regression risk guidance
