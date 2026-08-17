# Needs Review Table

| Article ID | Title | Source Type | Reason for Review | Affected Feature | Source Evidence | Suggested Question for Product Owner | Publication Recommendation |
|-----------|-------|-------------|-------------------|-----------------|---------------|--------------------------------------|---------------------------|
| ES-024 | How do I request machine translation? | Source-code | Machine translation requires server config; unclear if available in all deployments | Machine Translation | MachineTranslationService.java, mt.use.ec.mt, microsoft.translation.client.id | "Which machine translation services are enabled in production? Is eTranslation available to all users or only EC staff?" | Hold until confirmed; mark as "availability depends on deployment" |
| ES-059 | How do webhooks work? | Source-code | Webhook payload format not documented; no retry logic found; unclear if this is the same as WS-019 webhooks | Webhook notifications | BasicService.callHook(), Survey.webhook | "What payload does the per-submission webhook send? Is there retry logic? Is this distinct from the API export-completion webhook?" | Hold until payload format confirmed |
| SM-59 | What does request machine translation mean? | Document-derived | May describe a feature not available in all deployments (conflict #1) | Machine Translation | mt.use.ec.mt=false by default | "Should the article include a note about deployment-dependent availability?" | Ingest with caveat note |
| WS-019 | Use webhooks for result export completion | Document-derived | May describe a different mechanism than the per-submission webhook in ES-059 (conflict #2) | Webhooks (API) | WebServiceController async tasks | "Are there two distinct webhook mechanisms? One for submissions and one for API export completion?" | Ingest but clarify scope |
| ES-041 | How do administrators manage users? | Source-code | Admin-only feature; no DOC equivalent; may expose internal admin procedures | User Management | UserController.java | "Should admin procedures be available to the chatbot or restricted to internal documentation?" | Ingest only if chatbot serves admins |
| ES-042 | How do administrators search surveys? | Source-code | Admin-only | Admin Survey Search | SurveySearchController.java | Same as above | Ingest only if chatbot serves admins |
| ES-043 | How do administrators freeze a survey? | Source-code | Admin-only; describes policy enforcement action | Survey Freezing | SurveySearchController.freezesurvey | "Should freeze/unfreeze procedures be exposed to general users or only admin documentation?" | Ingest only if chatbot serves admins |
| ES-044 | How do administrators configure system messages? | Source-code | Admin-only | System Messages | SystemController.java | Same as ES-041 | Ingest only if chatbot serves admins |
| ES-045 | How do administrators ban a user? | Source-code | Admin-only; describes punitive action | User Banning | UserController.banuser | Same as ES-041 | Ingest only if chatbot serves admins |

## Summary

| Category | Count |
|----------|-------|
| Total items needing review | 9 |
| Machine translation uncertainty | 2 (ES-024, SM-59) |
| Webhook scope uncertainty | 2 (ES-059, WS-019) |
| Admin audience question | 5 (ES-041 through ES-045) |

## Recommendations

1. **Machine translation**: Add a deployment-dependent availability note to all MT articles. Proceed with ingestion but include the caveat.
2. **Webhooks**: Create two distinct articles — one for per-submission webhook (survey Properties) and one for API export-completion webhook (WS-019). Do not merge them.
3. **Admin articles**: Decide based on chatbot target audience. If the chatbot serves survey managers AND administrators, ingest all. If participant-only, exclude admin articles.
