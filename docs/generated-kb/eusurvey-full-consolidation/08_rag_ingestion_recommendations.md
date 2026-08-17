# RAG Ingestion Recommendations

## 1. Format for Ingestion

**Recommendation: Ingest JSON for structured retrieval, with Markdown as fallback.**

- **Primary**: JSON articles (if available) — enables structured field extraction, metadata filtering, and typed retrieval.
- **Secondary**: Markdown articles — for articles only available in MD format (SRC-only articles).
- **DOCX articles**: Must be converted to JSON or Markdown before ingestion. DOCX is not suitable for direct RAG ingestion.

**Action needed**: Convert the 276 document-derived DOCX articles to JSON format matching the schema used by the source-code articles.

## 2. Metadata Fields as Filters

Use these fields as **filterable facets** in the vector store:

| Field | Filter Use | Example Values |
|-------|-----------|----------------|
| `roles` / `user_type` | Route questions to role-specific answers | Respondent, Survey Owner, Administrator, API User |
| `article_type` | Distinguish explanations from procedures | How-To, Concept, Troubleshooting, Reference |
| `eusurvey_area` | Scope by functional module | Survey Editor, Results, Invitations, Translations |
| `survey_status` | Filter by lifecycle state | Published, Draft, Any |
| `environment` | Deployment-specific filtering | All, OSS, EC Production |

## 3. Metadata Fields for Boosting

Boost retrieval relevance using:

| Field | Boost Strategy |
|-------|---------------|
| `keywords` | Include in embedding text for semantic matching |
| `synonyms` | Add to embedding text as alternate phrasings |
| `search_boost_terms` | Prepend to chunk for retrieval emphasis |
| `title` | High weight — user questions often match titles |
| `short_answer` | Medium weight — contains the direct answer |
| `message_keys` (error texts) | Include actual error message text for troubleshooting matching |

## 4. Handling Duplicates and Retired Articles

- **Do NOT ingest retired articles** (8 exact duplicates identified in 03_duplicate_retirement_table.md).
- If both DOC and SRC versions exist for the same intent, ingest ONLY the master (document-derived after enrichment).
- Mark retired ES-xxx articles with `"ingestion_status": "retired"` in their JSON metadata.
- Keep the enrichment metadata (permissions, backend evidence) as internal fields not shown to users.

## 5. Handling "Needs Review" Articles

- **Ingest with caveat**: Include a `"review_status": "needs_review"` flag.
- The chatbot should still return these articles but may add a qualifier: "This information may vary depending on your EUSurvey deployment."
- Do NOT suppress Needs Review articles entirely — they still answer valid user questions.
- Admin articles (SRC-006 to SRC-010): Only ingest if the chatbot serves administrators. Add a `"restricted_audience": "administrator"` flag.

## 6. Source Traceability

- **Do NOT expose source traceability to end users.** Backend file paths, class names, and method names are internal evidence.
- Store traceability in a separate metadata field: `"internal_evidence"`.
- Use traceability for:
  - Maintenance (knowing which articles to update when code changes).
  - Verification (confirming article accuracy).
  - Debugging (when chatbot gives incorrect answers).
- Never surface `backend_files`, `classes`, `methods`, or `line_references` in chatbot responses.

## 7. User-Facing vs Internal Content

| Content Type | Show to Users | Store in Metadata |
|-------------|--------------|-------------------|
| Short answer | ✓ | ✓ |
| Procedure steps | ✓ | ✓ |
| Prerequisites | ✓ | ✓ |
| Conditions/limitations | ✓ | ✓ |
| Troubleshooting | ✓ | ✓ |
| Error messages (text) | ✓ | ✓ |
| Message keys (code) | ✗ | ✓ |
| Backend files/classes | ✗ | ✓ |
| Configuration keys | ✗ (except user-visible settings) | ✓ |
| Line numbers | ✗ | ✓ |

## 8. Prioritizing DOC vs SRC Content

When both exist for the same topic:

1. **Use DOC short_answer and procedure** — they are written for end users.
2. **Use SRC conditions and limitations** — they reveal real constraints.
3. **Use DOC troubleshooting** — user-friendly wording.
4. **Supplement with SRC error messages** — exact text helps match user queries.
5. **Use DOC metadata/keywords** — they include user-natural wording.
6. **Add SRC permissions** — they clarify who can do what.

## 9. Synonyms, Acronyms, UI Labels, Error Messages

- **Synonyms**: Include in the embedding chunk to catch alternative user phrasings.
- **Acronyms**: Map to full terms (EU Login = ECAS, PDF = Portable Document Format).
- **UI labels**: Include exact button/menu names for precise matching (e.g., "Apply Changes", "Save as Draft", "Publish").
- **Error messages**: Include the FULL user-visible error text as a searchable field. Users often paste error messages verbatim.
  - Example: "The survey has not yet been published. Please try again later." → maps to ES-008/SM-61.
  - Example: "This access-link has already been used." → maps to ES-012.

## 10. Avoiding Conflicting Answers

- **Never ingest both a retired article and its replacement** for the same intent.
- **Deduplication at ingestion time**: Before ingesting, check title + user_intent + eusurvey_area for collision.
- **Role-based routing**: If the same topic has different answers for different roles, use the `roles` field to route correctly:
  - "How do I download a PDF?" → Respondent gets PM-05_02; Survey Owner gets SM-05_08.
- **For the 2 identified conflicts**: Add a disambiguation note in the article body rather than having two conflicting answers.

## 11. Participant vs Survey Manager Questions

- **Tag every article** with its target audience in `user_type`.
- **Chatbot should ask clarifying questions** if intent is ambiguous:
  - "Are you asking as a survey participant or as a survey manager?"
- **Participant articles** (PM-xx, 01_xx-07_xx): Use simple, non-technical language. Never mention backend details.
- **Survey Manager articles** (SM-xx, numeric): Can include configuration steps and settings.
- **API articles** (WS-xx): Technical audience. Include request/response examples.

## 12. Technical Integration Questions

- **Web Services articles (WS-001 to WS-050)** should be ingested as a separate collection or tagged with `"audience": "integrator"`.
- Use `"article_type": "Reference"` for API articles.
- Include HTTP methods, endpoints, and parameter names in embeddings for API questions.
- Route questions containing words like "API", "endpoint", "HTTP", "XML", "JSON", "token", "authenticate" preferentially to WS-xx articles.
- The single ES-046 overview should be retired in favor of WS-001 (more comprehensive overview).
