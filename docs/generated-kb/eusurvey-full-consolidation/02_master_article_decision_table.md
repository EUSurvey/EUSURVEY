# Master Article Decision Table

## Legend

- **Match type**: Exact / Near / Complementary / Conflict / Unique-SRC / Unique-DOC
- **Action**: Keep-DOC / Keep-SRC / Merge-into-DOC / Merge-into-SRC / Keep-both / Retire / Review
- **Priority**: P1 (immediate) / P2 (soon) / P3 (when time allows)

## Matched Articles (Source-Code ↔ Document-Derived)

| Proposed ID | Master Title | SRC Article | DOC Article(s) | Match Type | Action | Reason | Review | Priority |
|-------------|-------------|-------------|----------------|-----------|--------|--------|--------|----------|
| KB-001 | How do I create a new survey? | ES-001 | SM-16 (creating_a_survey__01) | Complementary | Merge-into-DOC | DOC has user steps; SRC adds permissions, limits, error messages | Complete | P1 |
| KB-002 | How do I copy an existing survey? | ES-002 | SM-22 (creating_a_survey__07) | Near | Merge-into-DOC | DOC has user guidance; SRC adds conditions about DoNotDelete flag | Complete | P2 |
| KB-003 | How do I import a survey? | ES-003 | SM-18 (creating_a_survey__03) | Near | Merge-into-DOC | DOC has procedure; SRC adds error messages and validation | Complete | P2 |
| KB-004 | How do I export a survey structure? | ES-004 | SM-21 (creating_a_survey__06), SM-10_14 | Near | Merge-into-DOC | Multiple DOC articles cover export; SRC adds backend evidence | Complete | P2 |
| KB-005 | How do I delete a survey? | ES-005 | SM-23 (creating_a_survey__08) | Complementary | Merge-into-DOC | DOC has steps; SRC reveals soft-delete, admin restore, auto-deletion | Complete | P1 |
| KB-006 | How do I edit a survey? | ES-006 | SM-26-33 (editing series) | Complementary | Keep-both | SRC is overview; DOC articles cover specific editing tasks in detail | Complete | P3 |
| KB-007 | Survey properties overview | ES-007 | Multiple SM articles | Complementary | Keep-both | SRC is overview; DOC articles cover individual properties | Complete | P3 |
| KB-008 | How do I publish a survey? | ES-008 | SM-61 (publishing__01) | Near | Merge-into-DOC | DOC has official steps; SRC adds translation completeness check, OLAP update | Complete | P1 |
| KB-009 | How do I unpublish a survey? | ES-009 | (implicit in SM-61) | Unique-SRC | Keep-SRC | DOC doesn't have a separate unpublish article | Complete | P1 |
| KB-010 | How do I apply changes after publication? | ES-010 | SM-67 (managing__01 correcting errors) | Complementary | Merge-into-DOC | DOC explains the concept; SRC adds technical details | Complete | P2 |
| KB-011 | How do I clear unapplied changes? | ES-011 | — | Unique-SRC | Keep-SRC | Not documented in DOC KB | Complete | P2 |
| KB-012 | How do respondents submit a contribution? | ES-012 | Multiple participant FAQ articles | Complementary | Keep-both | SRC covers full flow; DOC covers specific participant scenarios | Complete | P2 |
| KB-013 | How do I save a survey as draft? | ES-013 | PM-05_10 (where to find saved drafts) | Complementary | Merge-into-DOC | DOC has participant perspective; SRC adds owner config details | Complete | P2 |
| KB-014 | Edit contribution after submission | ES-014 | PM-05_03, SM-50 | Near | Merge-into-DOC | DOC covers both perspectives; SRC adds error conditions | Complete | P1 |
| KB-015 | Download contribution as PDF | ES-015 | PM-05_01, PM-05_02, SM-49 | Near | Merge-into-DOC | DOC comprehensive; SRC adds async details | Complete | P2 |
| KB-016 | View survey results | ES-016 | SM-76 (where to find contributions) | Near | Merge-into-DOC | DOC has steps; SRC adds filter limits and permissions | Complete | P2 |
| KB-017 | Export survey results | ES-017 | SM-77, SM-90 | Near | Merge-into-DOC | DOC has procedures; SRC adds async behavior, timeouts, auto-delete | Complete | P1 |
| KB-018 | Export statistics | ES-018 | SM-77 (partial) | Complementary | Keep-SRC | SRC provides specific statistics export detail not in DOC | Complete | P2 |
| KB-019 | Export uploaded files | ES-019 | SM-88 | Near | Merge-into-DOC | DOC explains retrieval; SRC adds ZIP details | Complete | P2 |
| KB-020 | Publish results publicly | ES-020 | SM-81, SM-82 | Near | Merge-into-DOC | DOC has steps; SRC adds filter options | Complete | P2 |
| KB-021 | Manage translations | ES-021 | SM-53-60 (translation series) | Complementary | Keep-both | SRC overview; DOC series covers specifics | Complete | P3 |
| KB-022 | Add translation language | ES-022 | SM-53 (partial) | Complementary | Merge-into-DOC | DOC has steps; SRC adds language count, validation | Complete | P2 |
| KB-023 | Import translation file | ES-023 | SM-54 | Near | Merge-into-DOC | Same topic; SRC adds format validation errors | Complete | P2 |
| KB-024 | Request machine translation | ES-024 | SM-59, UG-018 | Near | Merge-into-DOC | DOC has guidance; SRC adds service details | Review | P1 |
| KB-025 | Create a guest list | ES-025 | SM-106 | Near | Merge-into-DOC | DOC has steps; SRC adds token limits | Complete | P2 |
| KB-026 | Send invitations | ES-026 | SM-108 | Near | Merge-into-DOC | DOC has steps; SRC adds async mail, SMTP check | Complete | P1 |
| KB-027 | Send reminders | ES-027 | (part of SM-108) | Complementary | Keep-SRC | Separate intent not fully covered in DOC | Complete | P2 |
| KB-028 | Manage address book | ES-028 | SM-99-105 (contacts series) | Complementary | Keep-both | SRC overview; DOC covers specifics | Complete | P3 |
| KB-029 | Import contacts | ES-029 | SM-103 | Near | Merge-into-DOC | Same topic; SRC adds file format validation | Complete | P2 |
| KB-030 | Manage access/privileges | ES-030 | SM-36, SM-74 | Near | Merge-into-DOC | DOC has steps; SRC adds privilege enum details | Complete | P1 |
| KB-031 | Secure my survey | ES-031 | SM-44-47 (security series) | Complementary | Keep-both | SRC overview; DOC covers each security option | Complete | P2 |
| KB-032 | Set start/end dates | ES-032 | SM-64 | Near | Merge-into-DOC | DOC has steps; SRC adds scheduler details | Complete | P2 |
| KB-033 | Enable quiz mode | ES-033 | UG-001-010 (quiz series) | Complementary | Keep-both | SRC overview; DOC series covers quiz in depth | Complete | P3 |
| KB-034 | Anonymous survey mode | ES-034 | SM-15_05 | Near | Merge-into-DOC | SM-15_05 is comprehensive; SRC adds IP storage detail | Complete | P1 |
| KB-035 | Preview survey | ES-035 | SM-51, SM-52 | Near | Merge-into-DOC | DOC covers testing; SRC adds test data marking | Complete | P2 |
| KB-036 | Archive a survey | ES-036 | SM-73 | Near | Merge-into-DOC | DOC has steps; SRC adds file warning details | Complete | P2 |
| KB-037 | Restore archived survey | ES-037 | SM-73 (partial) | Complementary | Keep-SRC | DOC mentions archiving; restore is separate intent | Complete | P2 |
| KB-038 | Manage skins | ES-038 | SM-92, SM-98 | Near | Merge-into-DOC | DOC covers themes; SRC adds file format validation | Complete | P3 |
| KB-039 | Account settings | ES-039 | SM-110, SM-111, SM-112 | Complementary | Keep-both | DOC has individual articles per setting; SRC is overview | Complete | P3 |
| KB-040 | Share address book | ES-040 | — | Unique-SRC | Keep-SRC | Not in DOC KB | Complete | P3 |
| KB-041 | Admin: manage users | ES-041 | — | Unique-SRC | Keep-SRC | Admin-only, not in DOC | Complete | P2 |
| KB-042 | Admin: search surveys | ES-042 | — | Unique-SRC | Keep-SRC | Admin-only | Complete | P2 |
| KB-043 | Admin: freeze survey | ES-043 | — | Unique-SRC | Keep-SRC | Admin-only | Complete | P2 |
| KB-044 | Admin: system messages | ES-044 | — | Unique-SRC | Keep-SRC | Admin-only | Complete | P3 |
| KB-045 | Admin: ban user | ES-045 | — | Unique-SRC | Keep-SRC | Admin-only | Complete | P3 |
| KB-046 | Web Service API overview | ES-046 | WS-001 through WS-050 | Complementary | Retire-SRC | WS series is far more comprehensive; ES-046 is redundant overview | Complete | P1 |
| KB-047 | Activity logging | ES-047 | SM-75 | Near | Merge-into-DOC | DOC has user guidance; SRC adds enable/disable details | Complete | P3 |
| KB-048 | Bulk operations | ES-048 | — | Unique-SRC | Keep-SRC | Not covered in DOC | Complete | P2 |
| KB-049 | Multi-paging | ES-049 | SM-96 | Near | Merge-into-DOC | DOC has steps; SRC adds validation, prevent-going-back | Complete | P2 |
| KB-050 | Dependencies/visibility | ES-050 | SM-34, UG-041, UG-053 | Complementary | Keep-both | DOC covers user steps; SRC adds OR/AND logic, chaining rules | Complete | P2 |
| KB-051 | Formula questions | ES-051 | SM-42 | Near | Merge-into-DOC | DOC explains usage; SRC adds min/max/mean functions, error messages | Complete | P2 |
| KB-052 | Confirmation page | ES-052 | SM-71 | Near | Merge-into-DOC | DOC has steps; SRC adds placeholders, metadata variables | Complete | P2 |
| KB-053 | Background documents | ES-053 | SM-95 | Near | Merge-into-DOC | Same feature, different detail levels | Complete | P3 |
| KB-054 | Useful links | ES-054 | SM-94 | Near | Merge-into-DOC | Same feature | Complete | P3 |
| KB-055 | Automatic publishing | ES-055 | SM-64 | Complementary | Merge-into-DOC | DOC mentions auto-publish; SRC adds scheduler behavior | Complete | P2 |
| KB-056 | Report emails | ES-056 | SM-09_15 through SM-09_20 | Complementary | Keep-both | DOC has 6 detailed articles; SRC adds backend evidence | Complete | P2 |
| KB-057 | Confirmation emails | ES-057 | SM-48 (partial) | Complementary | Keep-SRC | Separate feature not fully covered by DOC | Complete | P2 |
| KB-058 | Max contributions limit | ES-058 | SM-46 | Near | Merge-into-DOC | DOC has steps; SRC adds per-user vs total distinction | Complete | P2 |
| KB-059 | Webhooks | ES-059 | WS-019 | Complementary | Merge-into-DOC | WS-019 covers webhook for API; SRC adds survey-level webhook | Review | P1 |
| KB-060 | Registration form | ES-060 | SM-102 | Near | Merge-into-DOC | DOC explains the feature; SRC adds automatic contact creation | Complete | P2 |
| KB-061 | Download survey as PDF | ES-061 | SM-05_08 | Near | Merge-into-DOC | DOC comprehensive; SRC adds async details | Complete | P3 |
| KB-062 | Contact survey owner | ES-062 | PM-02_01 | Near | Merge-into-DOC | DOC is participant-facing; SRC adds backend | Complete | P3 |
| KB-063 | Delete a contribution | ES-063 | SM-118, WS-042 | Complementary | Keep-both | DOC covers GDPR deletion; API covers programmatic; SRC adds UI flow | Complete | P2 |
| KB-064 | Recalculate quiz scores | ES-064 | — | Unique-SRC | Keep-SRC | Not in DOC | Complete | P3 |
| KB-065 | Local storage backup | ES-065 | PM-07_01 | Complementary | Merge-into-DOC | DOC explains cookies/storage; SRC adds backup/restore logic | Complete | P3 |
| KB-066 | Motivation popup | ES-066 | SM-25 | Near | Merge-into-DOC | DOC explains the feature; SRC adds trigger types | Complete | P3 |
| KB-067 | Progress bar | ES-067 | — | Unique-SRC | Keep-SRC | Not specifically in DOC | Complete | P3 |
| KB-068 | Change survey owner | ES-068 | (part of SM-74) | Complementary | Keep-SRC | Separate intent from general access management | Complete | P2 |
| KB-069 | Automatic deletion | ES-069 | SM-09_09 through SM-09_14 | Complementary | Keep-both | DOC has 6 detailed articles; SRC adds code-level evidence | Complete | P1 |
| KB-070 | Token guest lists | ES-070 | SM-109 | Near | Merge-into-DOC | DOC has user steps; SRC adds limits, activation logic | Complete | P2 |

## Unique Document-Derived Articles (not matched to any SRC article)

These 196+ articles have no source-code-derived equivalent and should be **kept as-is** in the master KB:

### Categories of unique DOC articles:

| Category | Count | Examples | Action |
|----------|-------|---------|--------|
| General questions (SM-01 to SM-13) | 13 | What is EUSurvey, features, limits, browsers, support | Keep |
| Login/EU Login (SM-14, SM-15) | 2 | EU Login requirement, connection methods | Keep |
| Creating surveys (SM-17, SM-24, SM-25) | 3 | Survey types, WCAG, motivation popup | Keep |
| Editing details (SM-27-35, SM-38-43) | 15 | Questionnaire creation, elements, UTF-8, formulas, tables | Keep |
| Survey security details (SM-45, SM-47-48) | 3 | Passwords, CAPTCHA, contribution access | Keep |
| Testing (SM-51, SM-52) | 2 | Preview, colleague testing | Keep |
| Translation details (SM-55-58, SM-60) | 5 | Online editing, offline, non-EU languages, EU staff | Keep |
| Publication details (SM-62-66) | 5 | Custom URLs, translation links, end reminders, OPC | Keep |
| Managing survey details (SM-67-72) | 6 | Error correction, title change, contact, escape message | Keep |
| Results details (SM-78-90) | 13 | Subsets, drafts, downloads, ranking scores, zip issues | Keep |
| Design/layout (SM-91-98) | 8 | Themes, logos, links, numbering, multi-page, skins | Keep |
| Contacts details (SM-99-105) | 7 | Attributes, batch edit, export contacts | Keep |
| Invitations details (SM-106-109) | 4 | Guest lists, edit/remove, tokens | Keep |
| Account management (SM-110-112) | 3 | Password, email, language change | Keep |
| Data protection (SM-113-118) | 6 | Cookies, stored data, privacy, DPA, GDPR rights, bulk delete | Keep |
| Email reports (SM-09_15 to SM-09_20) | 6 | Feature, config, frequency, recipients, personal data | Keep |
| Inactive deletion (SM-09_09 to SM-09_14) | 6 | Definition, process, notifications, help | Keep |
| EU transition (SM-16_01 to SM-16_16) | 16 | Policy on external users, timelines, alternatives | Keep |
| Regular expressions (SM-04_20) | 1 | RegEx validation | Keep |
| PDF download (SM-05_08) | 1 | Respondent PDF download | Keep |
| Anonymous mode (SM-15_05) | 1 | Anonymous survey explanation | Keep |
| User Guides — Quiz (UG-001 to UG-010) | 10 | Quiz creation, question types, scoring, publishing | Keep |
| User Guides — Publication with PII (UG-011 to UG-013) | 3 | Personal data in published results | Keep |
| User Guides — Translation (UG-014 to UG-018) | 5 | Translation options, editor, offline, DGT, machine | Keep |
| User Guides — URL Prefill (UG-019 to UG-029) | 11 | Prefill feature, URL building, special cases | Keep |
| User Guides — WCAG (UG-030 to UG-032) | 3 | Accessibility compliance | Keep |
| User Guides — Survey Design (UG-033 to UG-042) | 10 | Best practices, design principles | Keep |
| User Guides — Editor how-to (UG-043 to UG-054) | 12 | Workspace, toolbox, navigation, elements, dependencies | Keep |
| Web Services — Full API (WS-001 to WS-050) | 50 | Complete API reference | Keep |
| Participants FAQ (01_xx to 07_xx) | 22 | Participant-facing FAQ articles | Keep |
