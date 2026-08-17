# Troubleshooting Index

| Problem / Error | Symptom | Likely Cause | User Role | UI Location | Related Article | Evidence File | Confidence |
|----------------|---------|-------------|-----------|-------------|----------------|--------------|-----------|
| "You have reached the maximum number of {0} surveys" | Cannot create new survey | Survey creation limit exceeded | Registered User | Forms page | ES-001 | ManagementController.java, SurveyService.checkSurveyCreationLimit | High |
| "This login already exists" | Cannot register | Login name already taken | New user | Registration | ES-039 | AdministrationService.java | High |
| "You have entered an invalid login or password!" | Cannot log in | Wrong credentials | Any | Login page | TR-001 | CustomAuthenticationManager.java | High |
| "This survey has been blocked due to an infringement" | Cannot access survey | Survey frozen by admin | Respondent | Runner page | ES-043 | HttpErrorController.java (error.FrozenSurvey) | High |
| "The survey has not yet been published" | Cannot access survey | Survey not published or ended | Respondent | Runner page | ES-008 | RunnerController.java (error.SurveyNotActive) | High |
| "The survey has been closed" | Cannot submit contribution | Survey end date passed | Respondent | Runner page | ES-012 | RunnerController.java (error.ContributionClosedSurvey) | High |
| "This access-link has already been used" | Cannot access survey | Token already consumed | Invited Participant | Runner | ES-012 | RunnerController.java (error.InvitationUsed) | High |
| "The access for this guest-list has not yet been activated" | Cannot access survey | Guest list deactivated | Invited Participant | Runner | ES-026 | RunnerController.java (error.InvitationDeactivated) | High |
| "This account has already been used to submit a contribution" | Cannot submit | Multiple submission prohibited | Registered Respondent | Runner | ES-012 | RunnerController.java (error.UserAlreadySubmitted) | High |
| "You are not authorized to submit to this survey without a proper invitation" | No access | Not in guest list | Respondent | Runner | ES-031 | RunnerController.java (error.NoInvitation) | High |
| "You don't have the necessary privileges to access this page" | Access denied | Missing permissions | Any | Various | ES-030 | BasicController.java (error.NoAccessPrivileges) | High |
| "Your session has expired" | Action fails | Session timeout | Any | Various | TR-002 | BasicController.java (error.Session) | High |
| "The translation is not complete" | Cannot publish | Incomplete translations | Survey Owner | Overview | ES-008 | SurveyService.java (error.MissingTranslation) | High |
| "There was a problem during save" | Data loss risk | Server error during save | Form Manager | Editor | TR-003 | ManagementController.java (error.ProblemDuringSave) | High |
| "The file is too large" | Cannot upload | File exceeds limit | Any | Various | TR-004 | FileController.java (error.FileTooLarge) | High |
| "You used too many search filters" | Cannot filter | Max 3 filters | Results Viewer | Results page | ES-017 | AnswerService.java (error.TooManyFilters) | High |
| "The file could not be imported" | Import fails | Invalid file format | Survey Owner | Import dialog | ES-003 | ManagementController.java (error.FileImportFailed) | High |
| "The provided translation is not valid" | Translation import fails | Invalid translation file | Form Manager | Translations | ES-023 | TranslationController.java (error.TranslationFileInvalid) | High |
| "The provided translation does not match the loaded survey" | Translation mismatch | Wrong survey file | Form Manager | Translations | ES-023 | TranslationController.java (error.TranslationWrongSurvey) | High |
| "A file with this name already exists" | Upload blocked | Duplicate filename | Any | File upload | TR-005 | (error.FileNameAlreadyExists) | High |
| "This skin is being used in a survey and can therefore not be deleted" | Cannot delete skin | Skin in use | Registered User | Skins page | ES-038 | SkinController.java (error.SkinInUse) | High |
| "You cannot delete a role when there are users with that role" | Cannot delete role | Role has assigned users | Administrator | Roles | ES-041 | RoleController.java (error.CannotDeleteRole) | High |
| "Your browser seems to have no Internet connection" | Cannot save/submit | Network issue | Respondent | Runner page | ES-012 | runner.js (error.InternetConnection) | High |
| "Cookies are disabled in your browser" | Cannot use app | Cookies not enabled | Any | Any | TR-006 | (info.CookiesDisabled) | High |
| "Javascript is disabled in your browser" | Cannot use app | JS not enabled | Any | Any | TR-007 | (info.JavascriptDisabled) | High |
| "Invalid formula" | Cannot save question | Formula syntax error | Form Manager | Editor | ES-051 | edit_validation.js (error.invalidFormula) | High |
| "You exceeded the limit of 1 million tokens per guestlist" | Cannot create tokens | Token limit reached | Invitation Manager | Participants | ES-070 | ParticipantsController.java (error.MaxTokenNumberExceeded) | High |
| "The data could not be saved" (error.holf) | Draft not saved | F5 hit during processing | Respondent | Runner | ES-013 | RunnerController.java (error.holf) | High |
| "The time limit for this quiz has been exceeded" | Cannot submit | Quiz time expired | Respondent | Quiz runner | ES-033 | runner.js (info.CountdownExceeded) | High |
| "Log in using two-factor authentication" | Cannot log in | 2FA required | Any | Login | TR-008 | HttpErrorController.java (error.WeakAuthentication) | High |
| "This survey does not allow to change a contribution" | Cannot edit | Edit disabled by owner | Respondent | Edit page | ES-014 | ContributionController.java (error.ContributionEditNotAllowed) | High |
| "This survey cannot be loaded" | Cannot open survey | Survey not found or no access | Any | Management | TR-009 | SessionService.java (error.NoAccessToSurvey) | High |
| "Your survey is highly complex" | Complexity warning | Too many elements/dependencies | Survey Owner | Editor | TR-010 | edit_complexity.js (info.CriticalComplexity) | Medium |
| "The SMTP server is not configured" | Cannot send email | Server misconfiguration | Any | Registration/Invitations | ES-026 | AdministrationService.java (error.SmtpServerNotConfigured) | High |
| "Please choose a password between 8 and 16 characters" | Cannot register | Weak password | New user | Registration | TR-011 | Tools.java (error.PasswordWeak) | High |
| "This e-mail address belongs to a banned user" | Cannot register | Email banned | New user | Registration | TR-012 | AdministrationService.java (error.EmailBanned) | High |
