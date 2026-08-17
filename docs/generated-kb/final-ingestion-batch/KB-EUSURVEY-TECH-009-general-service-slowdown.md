# What to do when EUSurvey appears generally slow or unavailable

## Intent / Description

Explains what to do when the EUSurvey platform as a whole appears slow or unavailable, and how to distinguish platform-wide issues from survey-specific problems.

## Applies To

* Role(s): Respondent, Survey Manager
* Feature: Platform availability
* Context: The EUSurvey website is slow to respond, pages take a long time to load, or the service appears down

## Short Answer

If EUSurvey appears generally slow or unavailable, this may be:

1. **A platform-wide issue** — The EUSurvey service is experiencing high load or a technical incident.
2. **A network issue on your side** — Your internet connection or corporate network is causing slowness.
3. **A browser issue** — Cached content or browser state is causing problems.
4. **Scheduled maintenance** — The platform may be undergoing planned maintenance.

## Steps / Procedure

**To check if it is a platform issue or a local issue:**

1. Try accessing the EUSurvey homepage: https://ec.europa.eu/eusurvey/home/welcome
2. If the homepage does not load: the issue is likely platform-wide or network-related on your side.
3. If the homepage loads but a specific survey is slow: the issue may be survey-specific (see other troubleshooting articles).
4. Try from a different device or network (e.g. mobile phone on cellular data) to rule out your local network.
5. Ask a colleague to try accessing EUSurvey to confirm whether others are affected.

**If the platform appears down:**

1. Wait 5–10 minutes and try again. Brief outages are usually resolved quickly.
2. Check for any maintenance announcements (if available through your organisation).
3. Clear your browser cache and try again.
4. If the issue persists for more than 30 minutes and is confirmed by multiple users, report it.

**What information to provide when reporting:**

- The time and date of the issue
- What you were trying to do
- The exact URL you were accessing
- Any error message or behaviour observed
- Whether other EUSurvey pages work
- Your browser version and operating system
- Whether colleagues experience the same issue

## Important Conditions / Limitations

* **EUSurvey does not have a public status page**: There is no official real-time service status page. You cannot check independently whether the platform is experiencing issues.
* **Shared infrastructure**: EUSurvey is a shared service. High activity across all users can affect performance.
* **Cannot determine current status from a KB article**: This article cannot tell you whether the service is currently up or down. It can only guide diagnostic steps.
* **Corporate proxies**: Some organisations route traffic through proxies that may add latency or block specific EUSurvey resources.
* **VPN interference**: Corporate VPN connections may cause routing issues that appear as slowness.

## Troubleshooting / Related Cases

* If only one survey is slow: the issue may be survey-specific (complex survey, high traffic). See: KB-EUSURVEY-TECH-007.
* If you see a specific error page (403, 404, 500): the issue is different from general slowness. See the relevant error article.
* If submission is slow but pages load: see KB-EUSURVEY-TECH-001 or KB-EUSURVEY-TECH-008.

## Out of Scope / Separate Topics

* Survey freezes during submission (see: KB-EUSURVEY-TECH-001)
* Survey freezes during navigation (see: KB-EUSURVEY-TECH-008)
* High concurrent submissions (see: KB-EUSURVEY-TECH-007)
* 403 Forbidden error (see: KB-EUSURVEY-ACCESS-001)
* Blank screen (see: KB-EUSURVEY-TECH-003)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation
* For platform-wide issues affecting EU staff, contact your IT helpdesk.
* For external users, report via the support link on the EUSurvey documentation page.

## Retrieval Metadata

* business_domain: technical_access
* user_role: respondent, survey_manager
* feature: platform_availability
* tags: slow, unavailable, platform down, service outage, cannot access EUSurvey, general slowness
* synonyms: EUSurvey not loading, platform is down, website slow, service unavailable, EUSurvey outage, cannot reach EUSurvey
* product_terms: EUSurvey, platform, homepage
* exclude: survey-specific errors, submission errors, 403, 404, editor errors, login errors
