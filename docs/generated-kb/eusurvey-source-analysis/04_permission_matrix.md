# Permission Matrix

## Role-Based Access Matrix

| Action | Anonymous | Respondent (Invited) | Registered User | Form Manager | Results Viewer | Invitation Manager | Survey Owner | Administrator |
|--------|-----------|---------------------|-----------------|--------------|---------------|-------------------|--------------|---------------|
| View open survey | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Submit contribution | ✓* | ✓ | ✓* | ✓ | ✓ | ✓ | ✓ | ✓ |
| Save draft | ✓* | ✓ | ✓* | ✓ | ✓ | ✓ | ✓ | ✓ |
| Edit own contribution | — | ✓** | ✓** | ✓ | ✓ | ✓ | ✓ | ✓ |
| Download contribution PDF | — | ✓** | ✓** | ✓ | ✓ | ✓ | ✓ | ✓ |
| Create survey | — | — | ✓ | ✓ | — | — | ✓ | ✓ |
| Edit survey structure | — | — | — | ✓ | — | — | ✓ | — |
| View survey properties | — | — | — | ✓ | — | — | ✓ | — |
| Publish/unpublish | — | — | — | — | — | — | ✓ | — |
| View results | — | — | — | — | ✓ | — | ✓ | ✓*** |
| Export results | — | — | — | — | ✓ | — | ✓ | ✓*** |
| Delete contributions | — | — | — | — | — | — | ✓ | ✓*** |
| Manage invitations | — | — | — | — | — | ✓ | ✓ | — |
| Send invitations | — | — | — | — | — | ✓ | ✓ | — |
| Add privileged users | — | — | — | — | — | — | ✓ | — |
| Change survey owner | — | — | — | — | — | — | ✓ | ✓ |
| Delete survey | — | — | — | — | — | — | ✓ | ✓ |
| Archive survey | — | — | — | — | — | — | ✓ | ✓ |
| Manage address book | — | — | ✓ | ✓ | — | ✓ | ✓ | ✓ |
| Manage skins | — | — | ✓ | ✓ | — | — | ✓ | ✓ |
| Manage translations | — | — | — | ✓ | — | — | ✓ | — |
| Admin: manage users | — | — | — | — | — | — | — | ✓ |
| Admin: freeze surveys | — | — | — | — | — | — | — | ✓ |
| Admin: system config | — | — | — | — | — | — | — | ✓ |
| Admin: search all surveys | — | — | — | — | — | — | — | ✓ |
| Use Web Service API | — | — | — | — | — | — | ✓ | ✓ |

*Depends on survey security settings (open, password, ECAS, invitation-only)
**Depends on survey property `changeContribution` / `downloadContribution` being enabled
***Admin access via survey search

## Permission Check Locations

| Permission Check | Source File | Method/Location |
|-----------------|-------------|-----------------|
| Survey ownership | SessionService.java | `checkSurvey()`, `userIsFormAdmin()` |
| Form management | SessionService.java | `userIsFormManager()` |
| Results access | SessionService.java | `userIsResultReadAuthorized()`, `userCanEditResults()` |
| Draft access | SessionService.java | `userIsResultOrDraftReadAuthorized()` |
| Admin roles | SecurityConfig (Spring) | `hasAnyRole('ROLE_USER_ADMIN','ROLE_RIGHT_ADMIN')` |
| Survey access (runner) | RunnerController.java | `loadSurvey()` - checks security, tokens, passwords |
| Token validation | RunnerController.java | `runnerToken()`, invitation lookup |
| ECAS authentication | CustomCasAuthenticationManager.java | `loadUserByUsername()` |
| Local authentication | CustomAuthenticationManager.java | `authenticate()` |
| EC-only access | ServerEnvironmentHandlerInterceptor.java | `preHandle()` |
| Privileged employee check | EcasHelper.java | `isEmployeeTypePrivileged()` |

## Global Privilege Values

The GlobalPrivilege enum defines system-wide capabilities assigned through Roles:

| Privilege | Value 0 | Value 1 | Value 2 |
|-----------|---------|---------|---------|
| RightManagement | No access | Read | Read/Write |
| UserManagement | No access | Read | Read/Write |
| FormManagement | No access | Read | Read/Write |
| ContactManagement | No access | Read | Read/Write |
| ECAccess | No access | Has EC access | — |
| SystemManagement | No access | Read | Read/Write |

## Local Privilege Values (per survey)

| Privilege | Value 0 | Value 1 | Value 2 |
|-----------|---------|---------|---------|
| AccessDraft | No access | Can view draft | — |
| AccessResults | No access | Read only | Read/Write |
| FormManagement | No access | Can edit | Full management |
| ManageInvitations | No access | Can manage | — |

## Source Traceability

- `GlobalPrivilege.java`: src/main/java/com/ec/survey/model/administration/GlobalPrivilege.java
- `LocalPrivilege.java`: src/main/java/com/ec/survey/model/administration/LocalPrivilege.java
- `Access.java`: src/main/java/com/ec/survey/model/Access.java
- `User.java`: src/main/java/com/ec/survey/model/administration/User.java
- `Role.java`: src/main/java/com/ec/survey/model/administration/Role.java
- `SessionService.java`: src/main/java/com/ec/survey/service/SessionService.java
- `CustomAuthenticationManager.java`: src/main/java/com/ec/survey/security/CustomAuthenticationManager.java
- `ServerEnvironmentHandlerInterceptor.java`: src/main/java/com/ec/survey/handler/ServerEnvironmentHandlerInterceptor.java
