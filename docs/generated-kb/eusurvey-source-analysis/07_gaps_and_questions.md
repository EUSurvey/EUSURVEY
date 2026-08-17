# Gaps and Questions for Functional Review

## Items Requiring Functional Validation

### 1. Feature Flags and Default Behaviour

| # | Item | Issue | Priority |
|---|------|-------|----------|
| 1.1 | Delphi feature | Feature flag `ui.enabledelphi` defaults to `false`. Need confirmation: Is this feature available in production OSS deployments? | Medium |
| 1.2 | eVote feature | Feature flag `ui.enableevote` defaults to `false`. Need confirmation of deployment status and electoral procedures. | Medium |
| 1.3 | Self-Assessment feature | Feature flag `ui.enableselfassessment` defaults to `false`. Confirm if exposed to standard users. | Medium |
| 1.4 | ECF feature | Feature flag `ui.enableecf` defaults to `false`. Confirm deployment scope. | Low |
| 1.5 | OPC (BRP Public Consultation) | Feature flag `ui.enableopc` defaults to `false`. Confirm if this is EC-internal only. | Medium |
| 1.6 | Public surveys | Feature flag `ui.enablepublicsurveys` defaults to `false`. Confirm when public survey listing is enabled. | Medium |
| 1.7 | Archiving | Feature flag `ui.enablearchiving` defaults to `false`. Confirm default OSS behaviour. | High |

### 2. Machine Translation Integration

| # | Item | Issue | Priority |
|---|------|-------|----------|
| 2.1 | eTranslation | The code references `mt.use.ec.mt` and `ETranslationService`. Need confirmation: What is the callback mechanism? Is this EC-only? | Medium |
| 2.2 | Microsoft Translation | Properties `microsoft.translation.client.id/secret` exist. Need confirmation: Is this available in OSS? | Medium |
| 2.3 | Translation delivery URL | `buildDeliveryURLFromRequest` constructs a callback. Need confirmation of the full workflow. | Low |

### 3. Webhook Feature

| # | Item | Issue | Priority |
|---|------|-------|----------|
| 3.1 | Webhook configuration | `Survey.webhook` property exists. `BasicService.callHook()` is called. Need confirmation: What payload format? What triggers it? Is it documented? | High |
| 3.2 | Webhook retry logic | No visible retry logic in `callHook()`. Confirm behaviour on failure. | Medium |

### 4. Security and Authentication

| # | Item | Issue | Priority |
|---|------|-------|----------|
| 4.1 | ECAS modes | The `ecasMode` property has multiple possible values. Need exact mapping of values to UI text and behaviour. | High |
| 4.2 | Two-factor authentication | `error.WeakAuthentication` and `handle2fa` exist. Confirm: When exactly is 2FA required? | Medium |
| 4.3 | Survey creation limit | `checkSurveyCreationLimit` exists but the exact limit values and time windows are not clear from code. Need production values. | Medium |
| 4.4 | Bad login attempts | `setBadLoginAttempts` exists on User. Confirm: Is there an account lockout mechanism? What are thresholds? | Medium |

### 5. Automatic Lifecycle

| # | Item | Issue | Priority |
|---|------|-------|----------|
| 5.1 | Inactivity period | `getInactiveSurveys()` exists. Confirm: What is the inactivity threshold before auto-archiving? | High |
| 5.2 | Auto-deletion timeline | `AutomaticSurveyDeleteWorker` sends 3 notifications. Confirm: What are the intervals between notifications and final deletion? | High |
| 5.3 | DoNotDelete flag | `Survey.doNotDelete` prevents auto-deletion. Confirm: Is this visible in the UI? Under what conditions? | Medium |
| 5.4 | Automatic draft deletion | `stepAssertAutomaticDraftDeleteExceptions` in SchemaService. Confirm: Are old drafts auto-deleted? What are exceptions? | Medium |

### 6. UI Behaviour Unclear

| # | Item | Issue | Priority |
|---|------|-------|----------|
| 6.1 | Complexity warning | `info.CriticalComplexity` shows a warning. Confirm: Does this block publication or is it advisory only? | Medium |
| 6.2 | CODA Dashboard | `Survey.codaLink` and `CodaCreateAnalytics`/`CodaOpenAnalytics` labels exist. Confirm: What is CODA? Is it an external integration? | Medium |
| 6.3 | Motivation popup trigger | The popup can be triggered by progress percentage or time (minutes). Confirm: Is this widely used? | Low |
| 6.4 | Dedicated Result Privileges | `info.DedicatedResultPrivileges` describes EU Login users accessing sub-sets of results. Confirm: How is this configured step-by-step? | Medium |
| 6.5 | Trust Score | `Survey.trustScore` and `computeTrustScore()` exist. Confirm: What is the trust score? Is it user-visible? | Low |
| 6.6 | Report abuse | `HomeController.reportAbuse` exists. Confirm: What happens after reporting? Who receives the notification? | Medium |

### 7. Backend Endpoints Without Clear UI

| # | Item | Issue | Priority |
|---|------|-------|----------|
| 7.1 | `/worker/*` endpoints | Worker controller has endpoints for PDF creation and export processing. Confirm: Are these internal-only or exposed? | Low |
| 7.2 | `/testdata/*` endpoints | TestDataController creates dummy surveys. Confirm: Is this available in production? | Low |
| 7.3 | `/info/renewsession` | Session renewal endpoint. Confirm: Is this called automatically by the frontend? | Low |
| 7.4 | `/graphics/pie.png` | Graphics controller renders charts. Confirm: Is this used in current UI or legacy? | Low |

### 8. Data Privacy and Retention

| # | Item | Issue | Priority |
|---|------|-------|----------|
| 8.1 | AnswerSet anonymization | `getAnswerSetsToAnonymize()` and `anonymiseAnswerSets()` exist. Confirm: What triggers anonymization? Is it time-based? | High |
| 8.2 | User account deletion | `DeleteUserAccountsWorker` and `setUserDeleteRequested` exist. Confirm: What is the deletion workflow and timeline? | High |
| 8.3 | IP address storage | `AnswerSet.setIP()` exists. Confirm: When is IP saved? Is it suppressed in anonymous mode? | High |
| 8.4 | Export data retention | Exports are deleted after 1 month. Confirm: Is this configurable per-instance? | Medium |

### 9. Email Configuration

| # | Item | Issue | Priority |
|---|------|-------|----------|
| 9.1 | Email templates | `InvitationMailCreator` and `MailService` handle emails. Confirm: Can HTML templates be customized by admins? | Medium |
| 9.2 | Reply-to address | `InvitationTemplate.replyto` exists. Confirm: How does a survey owner set this? | Low |
| 9.3 | Disclaimer in emails | `InvitationMailCreator.insertDisclaimer()` exists. Confirm: What disclaimer text? Is it configurable? | Low |

### 10. Web Service API

| # | Item | Issue | Priority |
|---|------|-------|----------|
| 10.1 | API authentication | `WebServiceController.getLoginAndPassword()` uses HTTP Basic Auth. Confirm: Is this the only mechanism? | High |
| 10.2 | API rate limiting | `webservice.maxrequestsperday` (default 100). Confirm: Is this per-user or global? | Medium |
| 10.3 | API documentation | No OpenAPI/Swagger specification found in repository. Confirm: Is there external API documentation? | High |
| 10.4 | API completeness | The WebServiceController has many operations. Confirm: Which are officially supported vs internal? | Medium |

### 11. Reporting Database

| # | Item | Issue | Priority |
|---|------|-------|----------|
| 11.1 | OLAP tables | `ReportingService` manages OLAP tables in a separate database. Confirm: Is this used in OSS deployments? | Medium |
| 11.2 | Materialized views | `getNumberPublishedAnswersFromMaterializedView` referenced. Confirm: What DB objects are required? | Low |

### 12. Miscellaneous

| # | Item | Issue | Priority |
|---|------|-------|----------|
| 12.1 | SNC (Statistical Number Code) | `Survey.collectSNC` exists. Confirm: What is this? When is it used? | Low |
| 12.2 | Validation code workflow | `Survey.validationCode` and `Survey.validator` exist. Confirm: Full validation workflow for list forms. | Medium |
| 12.3 | Organisation charge model | `SurveyService.chargePublishedSurvey()` and `analyzeCharge()` exist. Confirm: Is there a billing/charge model? | Medium |
| 12.4 | File system migration | Multiple `migrate*` methods in FileService. Confirm: Is migration still needed or is it legacy? | Low |
| 12.5 | Chatbot integration | `ChatbotController` exists with RAG pipeline calls. Confirm: Is this the chatbot we're building the KB for? | High |

---

## Summary Statistics

* **Total gaps identified**: 46
* **High priority**: 14
* **Medium priority**: 23
* **Low priority**: 9
* **Categories**: Feature flags (7), Machine translation (3), Webhook (2), Security (4), Lifecycle (4), UI (6), Backend (4), Privacy (4), Email (3), API (4), Reporting (2), Misc (5)
