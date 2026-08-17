# EUSurvey Application Map

## Technology Stack

| Layer | Technology |
|-------|-----------|
| Backend Framework | Spring MVC 5.x |
| Language | Java 11 |
| View Layer | JSP (JavaServer Pages) |
| ORM | Hibernate |
| Database | MySQL 8.0 |
| Build | Maven |
| Application Server | Tomcat 9 |
| Frontend | jQuery, Knockout.js, Bootstrap, TinyMCE |
| Authentication | Spring Security, EU Login (CAS), Local accounts |
| PDF Generation | Custom PDFRenderer |
| Email | JavaMail / SMTP |
| Export | Apache POI (XLS/XLSX), ODF Toolkit, custom CSV/XML |
| Charting | Chart.js, D3.js |
| i18n | Spring MessageSource (24 EU languages) |

## Main Modules

| Module | Package/Path | Description |
|--------|-------------|-------------|
| Survey Management | `controller/ManagementController` | Create, edit, publish, configure surveys |
| Runner (Respondent) | `controller/RunnerController` | Answer/submit surveys |
| Dashboard | `controller/DashboardController` | Survey overview, statistics widgets |
| Results & Contributions | `controller/ContributionController` | View/edit/print contributions |
| Exports | `controller/ExportsController` | Generate and download exports |
| Translations | `controller/TranslationController` | Manage survey translations |
| Participants/Invitations | `controller/ParticipantsController` | Guest lists, tokens, send invitations |
| Address Book | `controller/AddressBookController` | Contact management |
| Publication (Public Results) | `controller/PublicationController` | Publish results publicly |
| Administration | `controller/AdministrationController` | System admin, users, roles |
| Settings | `controller/SettingsController` | User account, shares, skins |
| Web Services API | `controller/WebServiceController` | REST API for external integrations |
| PDF | `controller/PDFController` | PDF generation for surveys/answers |
| Skins | `controller/SkinController` | Theme management |
| Self-Assessment | `controller/SelfAssessmentController` | SA criteria, target datasets |
| Delphi | `controller/DelphiController` | Online Delphi method support |
| eVote | embedded in ManagementController | Electronic voting/seat allocation |
| Activity Logging | `controller/ActivityController` | Audit trail |
| File Management | `controller/FileManagementController` | Admin file operations |

## Main User Roles

| Role | Internal Name | Description |
|------|--------------|-------------|
| System Administrator | `ROLE_USER_ADMIN` + `ROLE_RIGHT_ADMIN` | Full system access |
| Survey Owner | Owner of a Survey entity | Full control over own survey |
| Form Manager | `LocalPrivilege.FormManagement` on Access | Can edit survey structure |
| Results Viewer | `LocalPrivilege.AccessResults` on Access | Can view/export results |
| Invitation Manager | `LocalPrivilege.ManageInvitations` on Access | Can manage participants |
| Draft Accessor | `LocalPrivilege.AccessDraft` on Access | Can access draft survey |
| Registered User | Any authenticated user | Can create surveys, manage address book |
| EU Staff (ECAS) | User authenticated via EU Login (CAS) | May have EC Access privilege |
| External User | User without EU Login corporate account | May be restricted by `ecasMode` |
| Anonymous Respondent | No authentication | Can answer open/unsecured surveys |
| Invited Participant | Has a valid token/invitation link | Can answer secured surveys |
| Password-authenticated Participant | Knows survey password | Can answer password-protected surveys |

## Global Privileges (enum GlobalPrivilege)

| Privilege | Description |
|-----------|-------------|
| RightManagement | Manage roles and permissions |
| UserManagement | Manage user accounts |
| FormManagement | System-wide form management |
| ContactManagement | System-wide contact management |
| ECAccess | Access to EC-specific features |
| SystemManagement | System configuration and monitoring |

## Local Privileges (enum LocalPrivilege — per-survey)

| Privilege | Description |
|-----------|-------------|
| AccessDraft | View the survey in draft state |
| AccessResults | View and export results |
| FormManagement | Edit survey structure and settings |
| ManageInvitations | Manage guest lists and send invitations |

## Main UI Areas

| Area | URL Pattern | View Folder | Description |
|------|-------------|-------------|-------------|
| Welcome/Home | `/home/*` | `views/home/` | Landing page, public surveys, documentation |
| Login | `/auth/login` | `views/auth/` | Authentication pages |
| Dashboard | `/dashboard` | `views/dashboard.jsp` | Survey listing, stats widgets |
| Survey List | `/forms` | `views/forms/` | My surveys list, bulk operations |
| Survey Editor | `/{shortname}/management/edit` | `views/management/` | Survey form editor |
| Survey Properties | `/{shortname}/management/properties` | `views/management/` | Survey settings |
| Survey Overview | `/{shortname}/management/overview` | `views/management/` | Survey overview page |
| Results | `/{shortname}/management/results` | `views/management/` | Results viewer |
| Participants | `/{shortname}/management/participants` | (inline) | Guest lists and invitations |
| Translations | `/{shortname}/management/translations` | (inline) | Translation management |
| Exports | `/exports/list` | `views/exports/` | Export management |
| Publication | `/publication/{shortname}` | `views/publication/` | Public results page |
| Runner | `/runner/{shortname}` | `views/runner/` | Survey answering form |
| Address Book | `/addressbook` | `views/addressbook/` | Contact management |
| Settings | `/settings` | `views/settings/` | Account, shares, skins |
| Administration | `/administration/*` | `views/administration/` | System admin pages |

## Main Workflows

### 1. Survey Creation and Publication
1. User creates new survey (title, language, alias)
2. User edits survey (add questions, sections, configure properties)
3. User saves draft
4. User publishes survey → creates published version
5. Survey becomes accessible to respondents

### 2. Respondent Contribution
1. Respondent accesses survey URL (public, token-based, or password)
2. Respondent fills in answers
3. Respondent can save draft (if enabled)
4. Respondent submits contribution
5. Confirmation page shown with Contribution ID
6. Respondent can edit contribution later (if enabled)

### 3. Invitation Workflow
1. Survey owner creates guest list (contact list, EU list, or token list)
2. Owner adds contacts/users/tokens
3. Owner composes invitation email
4. Owner sends invitations
5. Participants receive unique links
6. Owner can send reminders
7. Owner monitors invitation status

### 4. Export Workflow
1. Survey owner starts export (content, statistics, files, etc.)
2. Export runs asynchronously
3. User receives notification when ready
4. User downloads export file
5. Exports auto-deleted after 1 month

### 5. Translation Workflow
1. Survey owner adds new language translation
2. Owner edits translation online OR exports for offline editing
3. Owner can request machine translation
4. Owner imports completed translation
5. Owner activates translation
6. Published survey becomes available in new language

## Main Entities

| Entity | Table | Description |
|--------|-------|-------------|
| Survey | `SURVEYS` | Core survey entity with all settings |
| Element | (various subclasses) | Survey questions and structural elements |
| AnswerSet | `ANSWERSET` | A respondent's complete submission |
| Answer | `ANSWERS` | Individual answer to a question |
| User | `USERS` | User account |
| Role | `ROLES` | System role |
| Access | `ACCESS` | Per-survey privilege assignment |
| ResultAccess | (table) | Dedicated result access for users |
| Translations | `TRANSLATIONS` | Survey translation set |
| ParticipationGroup | (table) | Guest list definition |
| Invitation | (table) | Individual invitation record |
| Attendee | (table) | Contact in address book |
| Export | `EXPORTS` | Export job record |
| Archive | `ARCHIVES` | Archived survey record |
| Skin | `SKINS` | UI theme/skin |
| Activity | `ACTIVITIES` | Audit log entry |
| Publication | embedded | Public results settings |
| Statistics | (table) | Cached statistics data |
| WebserviceTask | (table) | API async task |
| BulkChange | (table) | Bulk operation record |
| File | `FILES` | Uploaded file metadata |

## Main Lifecycle States

| State | Field(s) | Description |
|-------|----------|-------------|
| Draft | `isDraft=true` | Survey is being edited, not yet published |
| Published | `isPublished=true, isActive=true` | Survey is live and accepting responses |
| Unpublished | `isPublished=false` (was published before) | Survey is no longer accepting responses |
| Has Pending Changes | `hasPendingChanges=true` | Edits made after publication not yet applied |
| Archived | `archived=true` | Survey moved to archive |
| Deleted (soft) | `isDeleted=true` | Marked for deletion |
| Frozen | `isFrozen=true` | Blocked by administrator |

## Main Security Model

### Survey Access
- **Open**: No restriction, anyone can answer
- **Password**: Requires password entry
- **EU Login (ECAS)**: Requires EU Login authentication
  - All EU Login users
  - EU institution staff only
  - Contact list members only (matching email)
- **Token/Invitation**: Requires unique invitation link

### Configuration Properties
- `security`: Survey access mode
- `ecasMode`: ECAS restriction level
- `ecasSecurity`: Additional ECAS settings
- `captcha`: CAPTCHA requirement
- `password`: Survey password

## Main Integrations

| Integration | Description | Configuration |
|-------------|-------------|---------------|
| EU Login (CAS) | Single Sign-On for EU institutions | `casoss`, `ecashost` properties |
| LDAP | User directory for EU staff | `LdapUrl`, `LdapContextFactory` properties |
| Machine Translation | eTranslation/Microsoft Translate | `mt.*` properties |
| SMTP | Email sending | `smtpserver`, `sender` properties |
| Reporting Database | Separate DB for analytics | `enablereportingdatabase` property |
| Webhook | HTTP callback on contribution | `webhook` survey property |
| CODA Dashboard | External analytics | `codaLink` survey property |

## Feature Flags (spring.properties)

| Flag | Default | Description |
|------|---------|-------------|
| `ui.enablearchiving` | false | Enable survey archiving |
| `ui.enabledelphi` | false | Enable Online Delphi surveys |
| `ui.enableresponsive` | false | Enable responsive UI |
| `ui.enablefilemanagement` | true | Enable admin file management |
| `ui.enableopc` | false | Enable BRP Public Consultation |
| `ui.enablepublicsurveys` | false | Enable public survey listing |
| `ui.enableselfassessment` | false | Enable Self-Assessment surveys |
| `ui.enableecf` | false | Enable ECF framework |
| `ui.enableevote` | false | Enable electronic voting |
| `oss` | true | Open Source Software mode |
| `show.privacy` | true | Show privacy features |
| `captcha.bypass` | false | Bypass CAPTCHA |

## Survey Types

| Type | Flag/Property | Description |
|------|---------------|-------------|
| Standard Survey | default | Normal questionnaire |
| Quiz | `isQuiz=true` | Scored quiz with correct answers |
| Online Delphi | `isDelphi=true` | Multi-round expert consultation |
| Self-Assessment | `isSelfAssessment=true` | Self-evaluation with scoring |
| ECF Self-Assessment | `isECF=true` | European Competency Framework assessment |
| BRP Public Consultation (OPC) | `isOPC=true` | Public consultation format |
| eVote | `isEVote=true` | Electronic voting/elections |

## Question/Element Types

| Type | Class | Description |
|------|-------|-------------|
| Section | `Section` | Page divider, enables multi-paging |
| Text | (text element) | Static text block |
| Image | `Image` | Static image |
| Ruler/Line | (ruler element) | Visual separator |
| Free Text | `FreeTextQuestion` | Open text answer |
| Single Choice | `SingleChoiceQuestion` | Radio/dropdown selection |
| Multiple Choice | `MultipleChoiceQuestion` | Checkbox/listbox selection |
| Number | `NumberQuestion` | Numeric input with optional slider |
| Date | (date element) | Date picker (DD/MM/YYYY) |
| Time | (time element) | Time picker (hh:mm:ss) |
| Email | (email element) | Email address input |
| RegEx | `RegExQuestion` | Validated text input |
| Matrix | `Matrix` | Grid of choices |
| Table | `Table` | Data entry table |
| Complex Table | `ComplexTable` | Customizable table cells |
| Ranking | `RankingQuestion` | Drag-and-drop ordering |
| Rating | `RatingQuestion` | Star/icon rating |
| Gallery | `GalleryQuestion` | Image selection |
| File Upload | (upload element) | File attachment |
| File Download | (download element) | Downloadable files |
| Formula | `FormulaQuestion` | Calculated value |
| Confirmation | `Confirmation` | Consent checkbox |
