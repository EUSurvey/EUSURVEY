# Source Traceability Report

## Controller → Feature → Article Mapping

| Source File | Detected Feature(s) | Generated Article(s) | Confidence | Review Needs |
|-------------|-------------------|---------------------|-----------|-------------|
| ManagementController.java | Create survey, Edit survey, Properties, Publish, Unpublish, Apply Changes, Clear Changes, Results, Test/Preview, Upload, Access management, Change owner, eVote seat allocation | ES-001, ES-006, ES-007, ES-008, ES-009, ES-010, ES-011, ES-016, ES-030, ES-035, ES-068 | High | None |
| RunnerController.java | Submit contribution, Save draft, Token access, Invitation access, PDF creation, File upload | ES-012, ES-013, ES-015 | High | None |
| ContributionController.java | Edit contribution, Delete contribution, Print contribution, Quiz results, SA results | ES-014, ES-063, ES-064 | High | None |
| ExportsController.java | Start export, Check export status, Download export, Delete export | ES-017, ES-018, ES-019 | High | None |
| TranslationController.java | Manage translations, Add language, Import/Export translation, Machine translation, Delete translation | ES-021, ES-022, ES-023, ES-024 | High | ES-024 needs MT config review |
| ParticipantsController.java | Create guest list, Send invitations, Manage tokens, Upload voter file, Delete participants | ES-025, ES-026, ES-027, ES-070 | High | None |
| AddressBookController.java | Manage contacts, Import contacts, Configure attributes, Batch edit | ES-028, ES-029 | High | None |
| PublicationController.java | Publish results publicly, Authenticate for published results | ES-020 | High | None |
| SurveyController.java | List surveys, Bulk change, Tags, Shortname check | ES-048 | High | None |
| SettingsController.java | My account, Change password, Change email, Shares, Language | ES-039, ES-040 | High | None |
| SkinController.java | Create/edit/delete/copy skins, Upload skin | ES-038 | High | None |
| LoginLogoutController.java | Login, Logout, ECAS login, Forgot password, Reset password | (Auth articles needed) | High | Needs separate articles |
| HomeController.java | Welcome page, Public surveys, Help pages, Contact form, Support, Report abuse | ES-062 | High | None |
| DashboardController.java | Dashboard widgets, Survey states, Contributions overview | (Dashboard article needed) | Medium | UI-specific |
| WebServiceController.java | REST API operations, Token management, Survey management via API | ES-046 | High | API documentation needed |
| AdministrationController.java | System configuration, File migration, Department sync, OLAP, Languages | ES-041, ES-042, ES-044 | High | None |
| SurveySearchController.java | Admin survey search, Freeze/unfreeze, Delete, Archive management | ES-042, ES-043 | High | None |
| UserController.java | User CRUD, Ban/unban | ES-041, ES-045 | High | None |
| SystemController.java | System messages, Complexity config, Report config, Trust indicator | ES-044 | High | None |
| DelphiController.java | Delphi graphs, Tables, Comments, Likes, Explanations, Median | (Delphi articles needed) | Medium | Feature flag dependent |
| SelfAssessmentController.java | Criteria, Target datasets, Scores, Report configuration | (SA articles needed) | Medium | Feature flag dependent |
| PDFController.java | Survey PDF, Answer PDF, Published survey PDF | ES-061, ES-015 | High | None |
| ActivityController.java | Activity log viewing | ES-047 | Medium | None |
| FileManagementController.java | Admin file management, Recreate files | (Admin article needed) | High | Admin only |

## Service → Feature Mapping

| Source File | Detected Feature(s) | Related Articles | Confidence |
|-------------|-------------------|-----------------|-----------|
| SurveyService.java | Complete survey lifecycle, Publication, Translation sync, Ownership, OLAP, Notification | ES-001–ES-011, ES-068, ES-069 | High |
| AnswerService.java | Contribution storage, Statistics, Drafts, Anonymization, Completion rates | ES-012, ES-016, ES-017 | High |
| ExportService.java | Export lifecycle, Cleanup, Async export management | ES-017, ES-018, ES-019 | High |
| FileService.java | File storage, Cleanup, Migration, PDF files | ES-019, ES-061 | High |
| ParticipationService.java | Guest list management, Token handling, Invitation tracking | ES-025, ES-026, ES-027, ES-070 | High |
| AttendeeService.java | Contact CRUD, Invitation management | ES-028, ES-029 | High |
| MailService.java | Email sending, Template management | ES-026, ES-056, ES-057 | High |
| TranslationService.java | Translation persistence, Language management | ES-021, ES-022, ES-023 | High |
| MachineTranslationService.java | eTranslation/Microsoft integration | ES-024 | Medium |
| ArchiveService.java | Archive creation, Restore | ES-036, ES-037 | High |
| AdministrationService.java | User management, Validation, Ban logic | ES-041, ES-045 | High |
| SessionService.java | Session management, Permission checks, Form loading | ES-030 (permissions) | High |
| PDFService.java | PDF generation for surveys and answers | ES-015, ES-061 | High |
| SchedulerService.java | Nightly/hourly tasks, Auto-archive, Auto-publish | ES-055, ES-069 | High |
| ActivityService.java | Activity logging, Audit trail | ES-047 | Medium |
| ReportingService.java | OLAP tables, Reporting database | (Admin reporting) | Medium |
| SkinService.java | Skin CRUD | ES-038 | High |
| WebserviceService.java | API task management | ES-046 | High |
| ECFService.java | ECF competency assessment | (ECF articles needed) | Medium |
| EVoteService.java | Electronic voting, Seat allocation | (eVote articles needed) | Medium |
| LdapService.java | LDAP user lookup, EU Login integration | ES-031 | Medium |

## Frontend → Feature Mapping

| Source File | Detected Feature(s) | Related Articles | Confidence |
|-------------|-------------------|-----------------|-----------|
| runner.js | Survey answering, Dependencies, Paging, Draft save, Local backup, File upload | ES-012, ES-013, ES-065, ES-067 | High |
| runner2.js | Delphi features, Contribution link, Comments, Likes | (Delphi articles) | Medium |
| edit.js | Survey editor main logic, Dependencies, Content checks | ES-006, ES-050 | High |
| edit_actions.js | Editor toolbar actions | ES-006 | High |
| edit_add.js | Add new elements | ES-006 | High |
| edit_update.js | Element update logic | ES-006 | High |
| edit_properties.js | Element property editing | ES-006 | High |
| edit_properties_helper.js | Property UI helpers | ES-006 | High |
| edit_validation.js | Editor validation rules | ES-006 | High |
| translations.js | Translation editor UI | ES-021, ES-022, ES-023 | High |
| participants.js | Participants page logic | ES-025, ES-026 | High |
| sendinvitations.js | Invitation email composition | ES-026 | High |
| addressbook.js | Address book management | ES-028, ES-029 | High |
| shares.js | Share management | ES-040 | High |
| access.js | Access/privilege management | ES-030 | High |
| dashboard.js | Dashboard widgets | (Dashboard article) | High |
| includes.js | Common utilities, Modal dialogs | Multiple | High |
| runnerviewmodels.js | Knockout.js viewmodels for questions | ES-012 | High |
| edit_complexity.js | Complexity scoring | TR-010 | Medium |

## Configuration → Feature Mapping

| Configuration Key | Feature | Related Articles | Source |
|------------------|---------|-----------------|--------|
| server.prefix | Survey URL generation | ES-008, ES-012 | spring.properties |
| smtpserver / sender | Email sending | ES-026, ES-057 | spring.properties |
| captcha.bypass / captcha.key | CAPTCHA | ES-012 | spring.properties |
| ui.enablearchiving | Archive feature | ES-036 | spring.properties |
| ui.enabledelphi | Delphi surveys | (Delphi) | spring.properties |
| ui.enableevote | Electronic voting | (eVote) | spring.properties |
| ui.enableselfassessment | Self-assessment | (SA) | spring.properties |
| ui.enableecf | ECF framework | (ECF) | spring.properties |
| ui.enableopc | BRP consultations | (OPC) | spring.properties |
| export.timeout | Export time limit | ES-017 | spring.properties |
| export.deleteexportstimeout | Export retention | ES-017 | spring.properties |
| webservice.maxrequestsperday | API rate limit | ES-046 | spring.properties |
| mt.use.ec.mt | Machine translation | ES-024 | spring.properties |
| showecas / casoss | EU Login integration | ES-031 | spring.properties |
| admin.user / admin.password | Default admin account | ES-041 | spring.properties |
| monitoring.recipient | Monitoring emails | (Admin) | spring.properties |
