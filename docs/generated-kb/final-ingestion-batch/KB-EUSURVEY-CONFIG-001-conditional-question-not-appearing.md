# Why does a conditional question not appear in my survey?

## Intent / Description

Explains why a question configured with visibility dependencies (conditional logic) may not be displayed to respondents.

## Applies To

* Role(s): Survey Manager, Respondent
* Feature: Visibility / Dependencies
* Context: A question that should appear based on a trigger condition is not showing up

## Short Answer

In EUSurvey, questions can be configured with **visibility dependencies** so they only appear when a respondent selects a specific answer to a trigger question. If a conditional question is not appearing:

- **The trigger condition is not met**: The respondent has not selected the answer that triggers the visibility of the dependent question.
- **The dependency is incorrectly configured**: The visibility rule may reference the wrong trigger question or answer.
- **Changes not applied**: If you modified the dependency after publishing, you need to **Apply Changes** for the update to take effect in the live survey.
- **Test mode vs live**: The dependency may work differently in preview/test mode and the published version if changes have not been applied.

## Steps / Procedure

**To check dependency configuration (Survey Manager):**

1. Open the survey in the **Editor**.
2. Select the question that should appear conditionally.
3. Check its **Visibility** or **Dependency** settings in the properties panel.
4. Verify that:
   - The trigger question is correct.
   - The trigger answer is correct.
   - The dependency operator is correct (equals, not equals, etc.).
5. Use the **Test/Preview** function to verify the logic works.
6. If you are editing a published survey, click **Apply Changes** to push the update to the live version.

**For respondents:**
1. Make sure you have answered the trigger question with the specific answer that activates the conditional question.
2. If the survey uses multiple pages, the conditional question may appear on a different page.
3. Try refreshing the page.

## Important Conditions / Limitations

* **Apply Changes required**: After modifying dependencies on a published survey, changes only take effect for respondents after you click "Apply Changes". Until then, the live version still uses the old logic.
* **Multiple dependencies**: A question can depend on multiple conditions. All conditions must be met for the question to appear (AND logic by default).
* **Page boundaries**: A dependency can only trigger visibility for questions on the same page or subsequent pages, not previous pages.
* **Question order**: The trigger question must appear before the dependent question in the survey structure.
* **Deleted trigger**: If the trigger question or answer was deleted, the dependency becomes invalid and the dependent question may never show.
* **Translation interaction**: Dependencies are based on the question structure, not on translated text. They work the same regardless of the respondent's language.

## Troubleshooting / Related Cases

* If the question works in Preview but not in the published version, you forgot to Apply Changes.
* If a question is always hidden, check if it has a dependency on a deleted or modified answer.
* To debug complex logic, temporarily remove all dependencies, Apply Changes, verify the question shows, then add dependencies back one at a time.

## Out of Scope / Separate Topics

* How to configure visibility dependencies (see: How do I use the visibility feature)
* How to test a survey before publishing (see: Test the survey before publishing)
* How changes are applied to a published survey (see: Apply pending changes)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: survey_configuration
* user_role: survey_manager
* feature: visibility, dependencies, conditional_logic
* tags: conditional question, visibility, dependency, not showing, hidden question, trigger
* synonyms: question not appearing, conditional logic not working, visibility dependency broken, dependent question hidden, why is question invisible, branching not working
* product_terms: Visibility, Dependency, Apply Changes, trigger question, conditional question
