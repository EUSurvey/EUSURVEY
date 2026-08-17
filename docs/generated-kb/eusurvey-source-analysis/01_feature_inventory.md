# Feature Inventory

| Feature ID | Feature Name | User Role | UI Location | Backend Location | Required Permission | Related Entity | Evidence Files | Article(s) Generated | Confidence | Review Status |
|-----------|-------------|-----------|-------------|-----------------|-------------------|---------------|---------------|---------------------|-----------|--------------|
| F-001 | Create New Survey | Registered User | Dashboard / Forms page | ManagementController.createNewSurvey | Authenticated user | Survey | ManagementController.java, forms.jsp | ES-001 | High | Complete |
| F-002 | Copy Survey | Survey Owner | Forms list | ManagementController (overview) | Survey owner | Survey | ManagementController.java | ES-002 | High | Complete |
| F-003 | Import Survey | Registered User | Forms page dialog | ManagementController.importSurvey | Authenticated user | Survey | ManagementController.java, import-survey-dialog.jsp | ES-003 | High | Complete |
| F-004 | Export Survey Structure | Survey Owner/Form Manager | Survey overview | ManagementController.exportSurvey | FormManagement | Survey | ManagementController.java | ES-004 | High | Complete |
| F-005 | Delete Survey | Survey Owner | Forms list / Admin | ManagementController | Survey owner | Survey | ManagementController.java, SurveyController.java | ES-005 | High | Complete |
| F-006 | Edit Survey (Editor) | Survey Owner/Form Manager | Editor page | ManagementController.edit, editPOST | FormManagement | Survey, Element | ManagementController.java, edit.js | ES-006 | High | Complete |
| F-007 | Survey Properties | Survey Owner/Form Manager | Properties page | ManagementController.properties, propertiesPost | FormManagement | Survey | ManagementController.java | ES-007 | High | Complete |
| F-008 | Publish Survey | Survey Owner | Overview/Properties | ManagementController.publish | Survey owner | Survey | ManagementController.java | ES-008 | High | Complete |
| F-009 | Unpublish Survey | Survey Owner | Overview/Properties | ManagementController.unpublish | Survey owner | Survey | ManagementController.java | ES-009 | High | Complete |
| F-010 | Apply Changes (after publication) | Survey Owner | Overview | ManagementController.applyChanges | Survey owner | Survey | ManagementController.java | ES-010 | High | Complete |
| F-011 | Clear Changes | Survey Owner | Overview | ManagementController.clearchanges | Survey owner | Survey | ManagementController.java | ES-011 | Medium | Complete |
| F-012 | Answer/Submit Survey | Respondent | Runner page | RunnerController.processSubmit | Access depends on security | AnswerSet | RunnerController.java, runner.js | ES-012 | High | Complete |
| F-013 | Save Draft | Respondent | Runner page | RunnerController.DraftSubmit | Survey.saveAsDraft enabled | AnswerSet | RunnerController.java | ES-013 | High | Complete |
| F-014 | Edit Contribution | Respondent | Confirmation page / link | ContributionController.editcontribution | Survey.changeContribution enabled | AnswerSet | ContributionController.java | ES-014 | High | Complete |
| F-015 | Download Contribution PDF | Respondent | Confirmation page | RunnerController.createanswerpdf | Survey.downloadContribution enabled | AnswerSet | RunnerController.java, PDFService.java | ES-015 | High | Complete |
| F-016 | View Results | Survey Owner/Results Viewer | Results page | ManagementController.results | AccessResults | AnswerSet, Statistics | ManagementController.java | ES-016 | High | Complete |
| F-017 | Export Results (Content) | Survey Owner/Results Viewer | Exports page | ExportsController.startExport | AccessResults | Export | ExportsController.java | ES-017 | High | Complete |
| F-018 | Export Statistics | Survey Owner/Results Viewer | Exports page | ExportsController.startExport | AccessResults | Export, Statistics | ExportsController.java | ES-018 | High | Complete |
| F-019 | Export Uploaded Files | Survey Owner/Results Viewer | Exports page | ExportsController.startExport | AccessResults | Export | ExportsController.java | ES-019 | High | Complete |
| F-020 | Publish Results | Survey Owner | Results page | PublicationController.publication | Survey owner | Publication | PublicationController.java | ES-020 | High | Complete |
| F-021 | Manage Translations | Survey Owner/Form Manager | Translations page | TranslationController.translations | FormManagement | Translations | TranslationController.java, translations.js | ES-021 | High | Complete |
| F-022 | Add Translation Language | Survey Owner/Form Manager | Translations page | TranslationController.addtranslations | FormManagement | Translations | TranslationController.java | ES-022 | High | Complete |
| F-023 | Import Translation File | Survey Owner/Form Manager | Translations page | TranslationController.importtranslation | FormManagement | Translations | TranslationController.java, TranslationsHelper.java | ES-023 | High | Complete |
| F-024 | Request Machine Translation | Survey Owner/Form Manager | Translations page | TranslationController.translateTranslations | FormManagement | Translations, MachineTranslation | TranslationController.java, MachineTranslationService.java | ES-024 | Medium | Needs review |
| F-025 | Create Guest List | Survey Owner/Invitation Mgr | Participants page | ParticipantsController.participants | ManageInvitations | ParticipationGroup | ParticipantsController.java | ES-025 | High | Complete |
| F-026 | Send Invitations | Survey Owner/Invitation Mgr | Participants page | ParticipantsController.sendInvitations | ManageInvitations | ParticipationGroup, MailTask | ParticipantsController.java, sendinvitations.js | ES-026 | High | Complete |
| F-027 | Send Reminders | Survey Owner/Invitation Mgr | Participants page | ParticipantsController.sendInvitations | ManageInvitations | ParticipationGroup | ParticipantsController.java | ES-027 | High | Complete |
| F-028 | Manage Address Book | Registered User | Address Book page | AddressBookController | ContactManagement | Attendee | AddressBookController.java, addressbook.jsp | ES-028 | High | Complete |
| F-029 | Import Contacts | Registered User | Address Book import | AddressBookController.importAttendees | ContactManagement | Attendee | AddressBookController.java | ES-029 | High | Complete |
| F-030 | Manage Access/Privileges | Survey Owner | Access page | ManagementController.access | Survey owner | Access | ManagementController.java, access.js | ES-030 | High | Complete |
| F-031 | Configure Survey Security | Survey Owner | Properties page | ManagementController.propertiesPost | Survey owner | Survey (security fields) | ManagementController.java | ES-031 | High | Complete |
| F-032 | Set Start/End Dates | Survey Owner | Properties page | ManagementController.propertiesPost | Survey owner | Survey | ManagementController.java | ES-032 | High | Complete |
| F-033 | Enable Quiz Mode | Survey Owner | Properties page | ManagementController.propertiesPost | Survey owner | Survey | ManagementController.java | ES-033 | High | Complete |
| F-034 | Configure Anonymous Mode | Survey Owner | Properties page | ManagementController.propertiesPost | Survey owner | Survey | ManagementController.java | ES-034 | High | Complete |
| F-035 | Preview Survey | Survey Owner/Form Manager | Editor/Overview | ManagementController.test | FormManagement | Survey | ManagementController.java | ES-035 | High | Complete |
| F-036 | Archive Survey | Survey Owner | Forms list | SurveyService.markAsArchived | Survey owner | Archive | ArchiveService.java | ES-036 | High | Complete |
| F-037 | Restore Archived Survey | Survey Owner | Archives page | SurveySearchController.restore | Survey owner | Archive | SurveySearchController.java, ArchiveService.java | ES-037 | High | Complete |
| F-038 | Manage Skins | Registered User | Settings > Skins | SkinController | Authenticated user | Skin | SkinController.java, skins.jsp | ES-038 | High | Complete |
| F-039 | User Account Settings | Registered User | Settings > My Account | SettingsController.myAccount | Authenticated user | User | SettingsController.java, myAccount.jsp | ES-039 | High | Complete |
| F-040 | Share Address Book | Registered User | Settings > Shares | SettingsController.shares | Authenticated user | (Share entity) | SettingsController.java, shares.jsp | ES-040 | High | Complete |
| F-041 | Admin: User Management | Administrator | Administration > Users | UserController | UserManagement | User | UserController.java, users.jsp | ES-041 | High | Complete |
| F-042 | Admin: Survey Search | Administrator | Administration > Surveys | SurveySearchController | RightManagement | Survey | SurveySearchController.java, surveysearch.jsp | ES-042 | High | Complete |
| F-043 | Admin: Freeze/Unfreeze Survey | Administrator | Administration > Surveys | SurveySearchController.freezesurvey | RightManagement | Survey | SurveySearchController.java | ES-043 | High | Complete |
| F-044 | Admin: System Messages | Administrator | Administration > System | SystemController | SystemManagement | Message | SystemController.java, system.jsp | ES-044 | High | Complete |
| F-045 | Admin: Ban/Unban User | Administrator | Administration > Users | UserController.banuser | UserManagement | User | UserController.java | ES-045 | High | Complete |
| F-046 | Web Service API | API User | /webservice/* | WebServiceController | API auth (login/password) | WebserviceTask | WebServiceController.java | ES-046 | High | Complete |
| F-047 | Activity Logging | Survey Owner | Activity page | ActivityController.activity | Survey owner | Activity | ActivityController.java | ES-047 | Medium | Complete |
| F-048 | Bulk Operations | Survey Owner | Forms page | SurveyController.bulkchange | Survey owner | BulkChange | SurveyController.java, BulkExecutor.java | ES-048 | High | Complete |
| F-049 | Multi-Paging | Survey Owner | Properties | ManagementController.propertiesPost | FormManagement | Survey | ManagementController.java | ES-049 | High | Complete |
| F-050 | Dependencies/Visibility Rules | Survey Owner/Form Manager | Editor | edit_update.js, Element.triggers | FormManagement | Element | edit.js, Element.java | ES-050 | High | Complete |
| F-051 | Formula Questions | Survey Owner/Form Manager | Editor | FormulaQuestion | FormManagement | FormulaQuestion | FormulaQuestion.java, runnerviewmodels.js | ES-051 | High | Complete |
| F-052 | Confirmation Page Customization | Survey Owner | Properties/Editor | ManagementController | FormManagement | Survey | ManagementController.java | ES-052 | High | Complete |
| F-053 | Background Documents | Survey Owner | Properties | ManagementController.propertiesPost | FormManagement | Survey, File | ManagementController.java | ES-053 | High | Complete |
| F-054 | Useful Links | Survey Owner | Properties | ManagementController.propertiesPost | FormManagement | Survey | ManagementController.java | ES-054 | High | Complete |
| F-055 | Automatic Survey Publishing | Survey Owner | Properties | ManagementController.propertiesPost | Survey owner | Survey | ManagementController.java, SchedulerService.java | ES-055 | High | Complete |
| F-056 | Report Email | Survey Owner | Properties | ManagementController.propertiesPost | Survey owner | Survey | ManagementController.java | ES-056 | High | Complete |
| F-057 | Confirmation Email | Survey Owner | Properties | ManagementController.propertiesPost | Survey owner | Survey | ManagementController.java | ES-057 | High | Complete |
| F-058 | Maximum Contributions Limit | Survey Owner | Properties | ManagementController.propertiesPost | Survey owner | Survey | ManagementController.java | ES-058 | High | Complete |
| F-059 | Webhook Notification | Survey Owner | Properties | Survey.webhook | Survey owner | Survey | Survey.java, BasicService.callHook | ES-059 | Medium | Needs review |
| F-060 | Registration Form | Survey Owner | Properties | ManagementController.propertiesPost | Survey owner | Survey | ManagementController.java | ES-060 | High | Complete |
| F-061 | PDF Survey Download | Respondent/Public | Runner/Publication | PDFController.survey | Based on survey settings | Survey | PDFController.java, PDFService.java | ES-061 | High | Complete |
| F-062 | Contact Survey Owner | Respondent | Runner page | HomeController.contactform | Public | Survey | HomeController.java | ES-062 | High | Complete |
| F-063 | Delete Contribution | Survey Owner | Results page | ContributionController.deleteContribution | AccessResults | AnswerSet | ContributionController.java | ES-063 | High | Complete |
| F-064 | Recalculate Quiz Scores | Survey Owner | Results page | ManagementController.recalculateScore | Survey owner | AnswerSet | ManagementController.java | ES-064 | Medium | Complete |
| F-065 | Local Storage Backup | Respondent | Runner page | runner.js (client-side) | No permission needed | (localStorage) | runner.js | ES-065 | High | Complete |
| F-066 | Motivation Popup | Respondent | Runner page | Survey.motivationPopup | Configured by owner | Survey | Survey.java, runner.js | ES-066 | Medium | Complete |
| F-067 | Progress Bar | Respondent | Runner page | Survey.progressBar | Configured by owner | Survey | Survey.java, runner.js | ES-067 | High | Complete |
| F-068 | Change Survey Owner | Survey Owner/Admin | Properties/Admin | ManagementController.changeOwner | Survey owner or admin | Survey | ManagementController.java, SurveyService.java | ES-068 | High | Complete |
| F-069 | Automatic Deletion | System | Background job | AutomaticSurveyDeleteWorker | System | Survey | AutomaticSurveyDeleteWorker.java | ES-069 | High | Complete |
| F-070 | Token Guest Lists | Survey Owner/Invitation Mgr | Participants page | ParticipantsController.createTokens | ManageInvitations | ParticipationGroup | ParticipantsController.java, TokenCreator.java | ES-070 | High | Complete |
