# Can I edit a survey after it has been published?

## Intent / Description

Explains whether and how a survey manager can modify a survey that is already published and accepting responses.

## Applies To

* Role(s): Survey Manager
* Feature: Survey Editing, Apply Changes
* Context: Survey is live and receiving contributions but needs correction or modification

## Short Answer

Yes, you can edit a published survey at any time. However, your changes are not immediately visible to respondents. After making edits, you must click **"Apply Changes"** on the survey's Overview page to push the updates to the live version.

Be cautious: structural changes (adding/removing questions) after people have already responded may affect data comparability.

## Steps / Procedure

1. Open your survey from the survey list.
2. Click **Edit** to open the editor.
3. Make your changes (fix typos, add questions, change options, etc.).
4. **Save** your changes in the editor.
5. Return to the **Overview** page.
6. Click **"Apply Changes"** to push the updates to the live (published) survey.
7. New respondents will now see the updated version.

## Important Conditions / Limitations

* **Apply Changes is mandatory**: Until you click "Apply Changes", respondents continue to see the old version. The editor works on a draft copy.
* **Existing contributions are preserved**: Contributions already submitted are not deleted when you apply changes. However, if you deleted or reorganized questions, existing answers to those questions will remain in the database but may not align with the new structure.
* **Data comparability**: If you change the structure (add/remove questions, change answer options), earlier contributions may have answered different questions than later ones. This can make analysis harder.
* **Translations may be invalidated**: If you add new elements, existing translations are marked as incomplete and need to be updated separately.
* **Published results reset**: When you apply changes, published result charts and statistics are reset and need to be re-generated.
* **Version tracking**: Each "Apply Changes" increments the survey version number internally.
* **Frozen surveys cannot be edited**: If an administrator has frozen your survey, you cannot make changes until it is unfrozen.
* **Deleting questions**: If you delete a question that has already received answers, those answers remain in the database but will no longer be associated with a visible question. The data is not lost but may be difficult to interpret.
* **When to copy instead**: If you need to make major structural changes and want to preserve the original survey and data intact, consider copying the survey and making changes on the copy. This way, the original remains unchanged for reference or continued data collection.
* **Pending changes indicator**: The Overview page shows a "Pending changes" notification when saved editor changes have not yet been applied to the live survey.

## Troubleshooting / Related Cases

* If respondents still see the old version: you forgot to click "Apply Changes". Do it now.
* If you see a "Pending changes" indicator on the Overview page: changes are waiting to be applied.
* If you want to test changes before applying: use the Preview/Test function in the editor.
* If you accidentally applied changes and want to revert: there is no "undo Apply Changes" — you would need to re-edit manually.

## Out of Scope / Separate Topics

* Will I lose submitted answers when I change my form (see: SM-68)
* How to publish a survey for the first time (see: How do I publish my survey)
* How translations work after changes (see: translation synchronization)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: survey_configuration
* user_role: survey_manager
* feature: edit_published_survey, apply_changes
* tags: edit after publishing, modify live survey, apply changes, pending changes, update published survey
* synonyms: can I change a published survey, edit survey while live, modify questionnaire after publishing, update running survey, apply changes EUSurvey
* product_terms: Apply Changes, Editor, pending changes, published version, draft version
