# ES-050 — How do I set up dependencies/visibility rules?

## Intent / Description

This article explains how to configure visibility rules (dependencies) so that questions or elements appear or hide based on the respondent's answers.

## Applies To

* Role(s): Survey Owner, Form Manager
* EUSurvey area: Survey Editor
* Environment: All
* Article type: How-To
* UI location: Editor page — Element properties
* Backend location: Element.java (triggers), edit.js, runner.js

## Short Answer

Dependencies (visibility rules) allow you to show or hide survey elements based on a respondent's answers to previous questions. For example, a follow-up question can appear only when a specific answer is selected. Configure dependencies in the editor by selecting the target element, opening its properties, and defining which answers from other questions trigger its visibility. Use OR logic (any trigger shows the element) or AND logic (all triggers must be selected).

## Prerequisites / Required Permissions

* The user must have `FormManagement` privilege on the survey or be the survey owner.
* The survey must contain at least one choice-based question (single choice, multiple choice, matrix) to serve as a trigger.
* The dependent element must be positioned after the triggering element(s) in the survey.

## Procedure

1. Open the survey in the editor.
2. Select the element you want to make conditionally visible.
3. Open its properties panel.
4. Look for the "Visibility" or "Dependencies" section.
5. Click to configure visibility rules.
6. A dialog appears showing available trigger elements and their possible answers.
7. Select one or more answers from one or more questions that should trigger this element.
8. Choose the logic:
   - **OR**: At least one of the selected elements needs to be chosen for the question to be displayed.
   - **AND**: All selected elements need to be chosen for the question to be displayed.
9. Save the element properties.
10. The element is now marked as "dependent" in the editor navigation.

## Important Conditions / Limitations

* Dependencies work with choice-based questions (single choice, multiple choice, matrix answers).
* An element can depend on multiple trigger elements.
* Nested (chained) dependencies are supported — a dependent element can itself trigger other elements.
* Chained dependencies increase survey complexity. The complexity score accounts for this (`weightDoubleDependency`).
* When a triggering element is deleted, the dependency is removed. A warning appears: "Please check the visibility settings of all other elements that depend on these answers."
* Dependencies interact with multi-paging: dependent elements on other pages are evaluated when navigating between pages.
* The progress bar behaviour is affected by dependencies: "dependent questions will dynamically move the progress bar back and forth."
* Dependencies are evaluated client-side in real-time using Knockout.js observables.
* For matrix questions, dependencies can be set on individual matrix rows/answers.
* If a dependent element is hidden, its answers are NOT submitted (treated as unanswered).
* The message "There are no other elements in the survey that could trigger this element" appears when no suitable trigger elements exist.

## Troubleshooting

* **Element not showing despite answer selected**: Check that the dependency logic (OR/AND) is correct. Check for chained dependencies where an intermediate element may also be hidden.
* **"Please check the visibility settings" warning**: This appears after editing answers that other elements depend on. Review all dependent elements.
* **Progress bar jumping**: This is expected behaviour when dependencies show/hide elements. Consider disabling the progress bar for surveys with many dependencies.

## Related Articles

* ES-006 — How do I edit a survey?
* ES-049 — How do I enable multi-paging?
* ES-067 — How does the progress bar work?

## Evidence / Source Traceability

* Backend: `src/main/java/com/ec/survey/model/survey/Element.java` — fields `triggers`, methods `getIsTriggerOrDependent()`, `getIsDependent()`
* Backend: `src/main/java/com/ec/survey/model/survey/Survey.java` — method `computeTriggers()`
* Backend: `src/main/java/com/ec/survey/model/survey/PossibleAnswer.java` — field `dependentElements`
* Frontend: `src/main/webapp/resources/js/runner.js` — functions `checkDependencies()`, `hideDependencies()`, `isTriggered()`
* Frontend: `src/main/webapp/resources/js/edit.js` — functions `checkDependenciesAsync()`, `updateDependenciesView()`
* Frontend: `src/main/webapp/resources/js/edit_complexity.js` — dependency scoring
* Message keys: `info.visibility`, `info.PleaseChooseLogic`, `info.NoTriggersFound`, `info.checkVisibilities`
* Configuration: Element property `useAndLogic` controls OR vs AND logic

## Confidence and Review Status

High — behaviour is directly visible in UI and backend code.

## Metadata

* Domain: Survey Editor
* EUSurvey area: Dependencies
* Feature: Visibility Rules
* User intent: How do I set up dependencies/visibility rules?
* Article type: How-To
* User type: Form Manager
* Required permission: FormManagement
* Survey status: Draft or Published (with pending changes)
* Environment: All
* Keywords: dependency, visibility, conditional, show, hide, trigger, rules, logic
* Synonyms: conditional questions, skip logic, branching, show/hide rules, question logic
* Acronyms: N/A
* Related entities: Element, PossibleAnswer, Survey
* Security / privacy relevance: None
* Search boost terms: conditional questions, visibility rules, skip logic, dependencies
* Source files: Element.java, PossibleAnswer.java, runner.js, edit.js
* Duplicate status: New
