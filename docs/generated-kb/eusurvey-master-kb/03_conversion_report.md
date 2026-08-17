# Conversion Report

## DOCX to Structured Article Conversion

| Metric | Value |
|--------|-------|
| DOCX files found | 276 |
| Successfully converted | 276 |
| Conversion errors | 0 |
| Skipped (index files) | 4 (00_index files excluded) |

## Conversion Details

### Source Folders Processed

| Folder | Files Found | Converted | Notes |
|--------|------------|-----------|-------|
| 01-FAQs_Participants | 22 | 22 | Participant-facing FAQ articles |
| 02-FAQs_SurveyManager | 140 | 140 | Survey manager FAQ (108 numbered + 32 SM-prefix) |
| 03-Users Guides | 54 | 54 | User guides (UG-prefix) |
| 04-WebServices | 50 | 50 | Web services API articles (WS-prefix) |

### Parsing Methodology

1. Extracted all paragraph text from `word/document.xml` within each DOCX ZIP archive.
2. Identified section headers by matching known patterns: "Direct answer", "Procedure or guidance", "Important notes", "Related articles", "User question examples", "RAG metadata".
3. Extracted key-value metadata: Product, Audience, FAQ section, Article type, Keywords, Source, Status.
4. Structured content into normalized JSON schema with: title, intent, applies_to, short_answer, procedure, important_conditions, related_articles, metadata.

### Parsing Quality Notes

| Issue | Count | Impact | Resolution |
|-------|-------|--------|-----------|
| Articles with "No additional procedure" text | ~80 | Low | Procedure field left empty (as designed) |
| Articles with "No specific limitation" text | ~60 | Low | Conditions field left empty (as designed) |
| Articles with minimal keywords | ~30 | Low | Title words used as fallback keywords |
| Articles without explicit UI location | ~276 | Medium | Field left as "Not specified" — can be enriched later |
| Articles without backend location | ~276 | Low | Expected for document-derived articles |

### ID Assignment

| Pattern | Example Input | Assigned ID |
|---------|--------------|-------------|
| Participants FAQ | `02_01_how-to-contact-the-survey-owner.docx` | `PM-02_01_how-to-contact-the-survey-owner` |
| SM numbered | `16_creating_a_survey__01_how_do_i_create_a_new_survey.docx` | `SM-16_creating_a_survey__01_how_do_i_create_a_new_survey` |
| SM new prefix | `SM-09_15_what_the_email_report_feature_does.docx` | `SM-09_15_what_the_email_report_feature_does` |
| User Guide | `UG-009_how-to-configure-quiz-settings.docx` | `UG-009_how-to-configure-quiz-settings` |
| Web Service | `WS-001_eusurvey_web_services_api_overview_for_integrators.docx` | `WS-001_eusurvey_web_services_api_overview_for_integrators` |

### No Manual Cleanup Required

All 276 articles were parsed without errors. The structured extraction successfully identified:
- Titles for all articles
- Direct answers for 270+ articles
- Procedures for ~196 articles
- Keywords for ~240 articles
- Related articles for ~250 articles
