# What to do when an EUSurvey survey opens as a blank screen

## Intent / Description

Explains possible causes and steps when a respondent or survey owner opens an EUSurvey survey and sees a blank or white screen instead of the survey content.

## Applies To

* Role(s): Respondent, Survey Manager
* Feature: Survey rendering
* Context: The survey URL loads but the page is empty or shows only the browser frame without survey content

## Short Answer

A blank screen when opening a survey typically indicates a client-side rendering issue rather than a missing survey. Common causes include:

1. **JavaScript disabled or blocked** — EUSurvey requires JavaScript to render surveys.
2. **Browser incompatibility** — An outdated or unsupported browser version.
3. **Browser extensions interfering** — Ad blockers, privacy extensions, or script blockers may prevent the survey from loading.
4. **Network issue** — Static resources (CSS, JavaScript files) failed to load due to network restrictions.
5. **Corporate proxy or firewall** — Some organisations block specific resource domains.
6. **Survey rendering error** — In rare cases, an invalid configuration in the survey may prevent rendering.

## Steps / Procedure

1. **Check JavaScript**: Ensure JavaScript is enabled in your browser.
2. **Try a different browser**: Use a current version of Chrome, Firefox, Edge, or Safari.
3. **Disable extensions**: Temporarily disable ad blockers, privacy tools, and script blockers.
4. **Clear browser cache**: Clear the cache and reload the page.
5. **Check the browser console**: Press F12, open the Console tab, and look for error messages. This may indicate which resource failed to load.
6. **Try a different network**: If you are on a corporate network, try using a personal device or mobile data to rule out proxy issues.
7. **Contact the survey owner**: If the issue persists, the survey owner should check whether the survey renders correctly for them and whether the survey configuration is valid.

**For survey owners:**

1. Open the survey in preview mode. If preview also shows a blank screen, there may be a configuration issue.
2. Check if the survey has complex custom HTML or scripts that could interfere with rendering.
3. Try the survey in a different browser.
4. If the issue is only for specific respondents, it is likely a client-side environment issue.

## Important Conditions / Limitations

* **Supported browsers**: EUSurvey supports current versions of Chrome, Firefox, Edge, and Safari.
* **JavaScript is required**: The survey interface relies on JavaScript for rendering questions, navigation, and validation.
* **Not the same as 404 or 403**: A blank screen with a loaded page frame is different from an error page. Error pages display specific messages.
* **Custom skins**: A custom skin with CSS errors may cause visual issues including blank-appearing pages.

## Troubleshooting / Related Cases

* If only the survey content area is blank but headers appear: a JavaScript error is preventing survey rendering. Check the browser console.
* If the entire page is white: the page may not have loaded at all. Check your network connection.
* If other EUSurvey pages work but this specific survey does not: the issue may be survey-specific. Report it to the survey owner.

## Out of Scope / Separate Topics

* Why EUSurvey displays a 403 error (see: KB-EUSURVEY-ACCESS-001)
* Why the survey says "not yet published" (see: PM-03_03)
* Why preview mode shows an error (see: KB-EUSURVEY-TECH-003)
* General service slowdown (see: KB-EUSURVEY-TECH-008)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: technical_access
* user_role: respondent, survey_manager
* feature: survey_rendering
* tags: blank screen, white page, survey not loading, empty page, nothing displays
* synonyms: survey shows nothing, blank page when opening survey, white screen survey, survey page empty, content not appearing
* product_terms: browser, JavaScript, Chrome, Firefox, Edge, Safari, cache
* exclude: 403 error, 404 error, login issues, submission errors
