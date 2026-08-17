# Conflict Table

Only 2 potential conflicts were identified between the source-code-derived and document-derived KBs.

| # | Topic | Document-Derived Statement | Source-Code-Derived Statement | Evidence | Risk | Recommended Resolution | Owner for Review |
|---|-------|---------------------------|-------------------------------|----------|------|----------------------|-----------------|
| 1 | Machine translation service availability | SM-59 and UG-018 describe machine translation as an available feature for survey managers | ES-024 notes that machine translation requires server-level configuration (`mt.use.ec.mt` or Microsoft keys) and may not be available in all deployments | spring.properties shows MT disabled by default; MachineTranslationService requires external service credentials | Medium — users may try to use the feature and find it unavailable in OSS deployments | Add a condition to the DOC article: "Machine translation availability depends on your EUSurvey deployment configuration. Contact your administrator if this option is not visible." | Product Owner / Deployment team |
| 2 | Webhook scope | WS-019 describes webhooks for "result export completion" (API-level notification when an async export is done) | ES-059 describes a webhook that "is called each time a contribution is submitted" (survey-level notification on each submission) | BasicService.callHook() is called per submission; WS-019 appears to describe a different webhook use case | Low — these may be two different webhook mechanisms (one per-submission, one per-export) rather than a true conflict | Clarify that EUSurvey has TWO webhook mechanisms: (1) per-contribution webhook configured in survey Properties, (2) API-level async task callbacks. Document both distinctly. | Product Owner |

## Summary

- **Total conflicts**: 2
- **High-risk conflicts**: 0
- **Medium-risk conflicts**: 1 (machine translation availability)
- **Low-risk conflicts**: 1 (webhook scope distinction)
- **Recommendation**: Both are resolvable with documentation clarification. Neither requires blocking ingestion.
