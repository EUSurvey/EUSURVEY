# Survey performance when many respondents submit at the same time

## Intent / Description

Explains what happens when a large number of respondents access or submit a survey simultaneously, and what the survey owner can do to prepare.

## Applies To

* Role(s): Survey Manager
* Feature: Survey performance, Concurrent access
* Context: A survey owner expects a high volume of simultaneous respondents and wants to understand potential impacts

## Short Answer

EUSurvey is designed to handle multiple concurrent respondents. However, very high simultaneous submission volumes may result in slower response times for individual users. The platform queues and processes submissions, so contributions are not lost, but respondents may experience longer loading or processing times during peak periods.

## Steps / Procedure

**Before launching a high-traffic survey:**

1. **Keep the survey simple**: Reduce the number of pages, complex elements, and file upload questions where possible.
2. **Avoid peak-time launches**: If possible, stagger the opening so respondents do not all arrive at the same moment.
3. **Inform respondents**: Let participants know that brief delays may occur during high-traffic periods and advise them to wait rather than refreshing repeatedly.
4. **Enable Save as Draft**: This allows respondents to save progress if they experience slowness, and return later.
5. **Monitor submissions**: Check the Results page periodically to confirm contributions are being received.

**During a high-traffic period:**

1. If respondents report slowness: advise them to wait and not refresh repeatedly, as this adds more load.
2. If submissions seem stalled: check whether contributions are appearing on the Results page. A delay does not mean loss.
3. If the survey becomes unresponsive for extended periods: this may indicate a platform-wide issue. Check with EUSurvey support.

## Important Conditions / Limitations

* **No guaranteed concurrency limit published**: EUSurvey does not publish specific maximum concurrent user numbers. Performance depends on survey complexity and overall platform load.
* **Submissions are queued**: The system processes submissions sequentially per survey. High volumes may cause brief queuing.
* **No data loss from concurrency**: Even if a respondent experiences a delay, their submission is processed once it reaches the server.
* **File uploads increase load**: Surveys with file upload questions consume more resources per submission.
* **Reporting database lag**: The reporting database (used for exports and statistics) has its own synchronisation cycle and may lag further behind during high-volume periods.
* **Platform is shared**: EUSurvey serves many surveys simultaneously. Performance depends on overall platform activity, not just your survey.

## Troubleshooting / Related Cases

* If respondents report the survey freezing during submit: this may be normal delay during high volume. Advise patience.
* If submissions stop appearing entirely: this may indicate a platform issue. Contact support.
* If you need guaranteed capacity for a specific event: consult with your organisation's IT contact or EUSurvey support in advance.

## Out of Scope / Separate Topics

* Why a survey freezes during submission (individual cases) (see: KB-EUSURVEY-TECH-001)
* General service slowdown (see: KB-EUSURVEY-TECH-008)
* Why a survey freezes on Next (see: KB-EUSURVEY-TECH-007)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation
* For planned high-volume events, contact EUSurvey support in advance.

## Retrieval Metadata

* business_domain: technical_access
* user_role: survey_manager
* feature: performance, concurrency
* tags: high traffic, many submissions, concurrent users, performance, capacity, simultaneous respondents
* synonyms: many people submitting at once, survey performance under load, capacity limits, how many respondents at same time, survey slowdown during peak
* product_terms: Submit, Save as Draft, Results, performance
* exclude: individual submission errors, browser issues, network problems, export timeouts
