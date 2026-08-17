# Survey Lifecycle Matrix

## State Definitions

| State | DB Fields | Description |
|-------|-----------|-------------|
| New/Draft | `isDraft=true, isPublished=false, isActive=false` | Survey is being created/edited |
| Published (Active) | `isDraft=false, isPublished=true, isActive=true` | Survey is live, accepting responses |
| Published (Inactive - not started) | `isPublished=true, isActive=false, start > now` | Published but start date not reached |
| Published (Inactive - ended) | `isPublished=true, isActive=false, end < now` | Published but end date passed |
| Published with Pending Changes | `isPublished=true, hasPendingChanges=true` | Published but draft has unapplied edits |
| Unpublished | `isPublished=false` (after being published) | Taken offline, not accepting responses |
| Archived | `archived=true` | Moved to cold storage |
| Deleted (soft) | `isDeleted=true` | Marked for deletion, restorable |
| Deleted (permanent) | Removed from DB | Permanently deleted |
| Frozen | `isFrozen=true` | Blocked by admin for policy violation |

## State Transitions

| From State | To State | Action | Required Role | UI Location | Backend Method |
|-----------|----------|--------|--------------|-------------|----------------|
| New/Draft | Published | Publish | Survey Owner | Overview page > Publish button | `ManagementController.publish()` → `SurveyService.publish()` |
| Published | Unpublished | Unpublish | Survey Owner | Overview page > Unpublish button | `ManagementController.unpublish()` → `SurveyService.unpublish()` |
| Unpublished | Published | Re-publish | Survey Owner | Overview page > Publish button | `ManagementController.publish()` |
| Published | Published with changes | Edit after publish | Survey Owner/Form Manager | Editor > Save | `ManagementController.editPOST()` → `SurveyService.makeDirty()` |
| Published with changes | Published (updated) | Apply Changes | Survey Owner | Overview > Apply Changes | `ManagementController.applyChanges()` → `SurveyService.applyChanges()` |
| Published with changes | Published (original) | Clear Changes | Survey Owner | Overview > Clear Changes | `ManagementController.clearchanges()` → `SurveyService.clearChanges()` |
| Any (non-archived) | Archived | Archive | Survey Owner | Forms list / Admin | `SurveyService.markAsArchived()` → `ArchiveService.archiveSurvey()` |
| Archived | Unpublished/Draft | Restore | Survey Owner | Archives page | `ArchiveService.restore()` |
| Any | Deleted (soft) | Delete | Survey Owner | Forms list | `SurveyService.markDeleted()` |
| Deleted (soft) | Draft/Unpublished | Restore | Survey Owner/Admin | Admin survey search | `SurveyService.unmarkDeleted()` |
| Deleted (soft) | Deleted (permanent) | Final delete | Administrator | Admin survey search | `SurveySearchController.finallydelete()` |
| Any | Frozen | Freeze | Administrator | Admin survey search | `SurveySearchController.freezesurvey()` → `SurveyService.freeze()` |
| Frozen | Previous state | Unfreeze | Administrator | Admin survey search | `SurveySearchController.unfreezesurvey()` → `SurveyService.unfreeze()` |
| Inactive (old) | Archived | Auto-archive | System (scheduler) | Background job | `ArchiveExecutor.run()` |
| Archived (old) | Deleted | Auto-delete | System (scheduler) | Background job | `AutomaticSurveyDeleteWorker.run()` |

## Available Actions per State

| State | Edit | Publish | Unpublish | Apply Changes | Clear Changes | Delete | Archive | View Results | Export Results | Accept Responses |
|-------|------|---------|-----------|---------------|---------------|--------|---------|-------------|---------------|-----------------|
| Draft | ✓ | ✓ | — | — | — | ✓ | — | — | — | — |
| Published (Active) | ✓* | — | ✓ | ✓** | ✓** | — | ✓ | ✓ | ✓ | ✓ |
| Published (Inactive) | ✓* | — | ✓ | ✓** | ✓** | — | ✓ | ✓ | ✓ | — |
| Unpublished | ✓ | ✓ | — | — | — | ✓ | ✓ | ✓ | ✓ | — |
| Archived | — | — | — | — | — | ✓ | — | — | — | — |
| Deleted | — | — | — | — | — | ✓*** | — | — | — | — |
| Frozen | — | — | — | — | — | — | — | ✓**** | ✓**** | — |

*Edits create pending changes when survey is published
**Only available when hasPendingChanges=true
***Final permanent delete (admin only)
****Admin access only

## Automatic Lifecycle Events

| Event | Trigger | Action | Configuration |
|-------|---------|--------|---------------|
| Auto-publish | Start date reached | Set `isActive=true` | `automaticPublishing=true` + `start` date |
| Auto-close | End date reached | Set `isActive=false` | `end` date configured |
| Auto-archive | Inactivity period | Move to archive | `SchedulerService.doNightlySchedule()` |
| Auto-delete notification 1 | Before auto-deletion | Send warning email | `AutomaticSurveyDeleteWorker` |
| Auto-delete notification 2 | Before auto-deletion | Send 2nd warning | `AutomaticSurveyDeleteWorker` |
| Auto-delete notification 3 | Before auto-deletion | Send final warning | `AutomaticSurveyDeleteWorker` |
| Auto-delete | Grace period expired | Permanently delete | `AutomaticSurveyDeleteWorker.run()` |
| Do Not Delete flag | Set by owner | Prevents auto-archive/delete | `Survey.doNotDelete=true` |

## Export States (Export.ExportState)

| State | Description |
|-------|-------------|
| Pending | Export queued |
| Started | Export in progress |
| Finished | Export complete, ready for download |
| Error | Export failed |

## Contribution States

| State | Description | DB Field |
|-------|-------------|----------|
| Draft | Saved but not submitted | `AnswerSet.isDraft=true` |
| Submitted | Finalized contribution | `AnswerSet.isDraft=false` |
| Updated | Contribution modified after submission | `AnswerSet.updateDate` set |

## Source Evidence

- `Survey.java`: Lines defining `isDraft`, `isPublished`, `isActive`, `isDeleted`, `archived`, `isFrozen`, `hasPendingChanges`
- `ManagementController.java`: Methods `publish()`, `unpublish()`, `activate()`, `applyChanges()`, `clearchanges()`
- `SurveyService.java`: Methods `publish()`, `unpublish()`, `activate()`, `applyChanges()`, `clearChanges()`, `markDeleted()`, `unmarkDeleted()`, `markAsArchived()`, `unmarkAsArchived()`
- `ArchiveService.java`: Methods `archiveSurvey()`, `restore()`
- `AutomaticSurveyDeleteWorker.java`: Auto-deletion logic
- `SchedulerService.java`: `doNightlySchedule()`, `doHourlySchedule()`
- `Export.java`: Enum `ExportState` {Pending, Started, Finished, Error}
