# How a survey owner can see who has answered

## Intent / Description

Explains how a survey owner can determine which participants have submitted contributions, including the distinction between anonymous and identified surveys.

## Applies To

* Role(s): Survey Manager
* Feature: Results, Contributions, Participation groups
* Context: A survey owner wants to see who has responded and who has not

## Short Answer

How you identify who has answered depends on the survey configuration:

- **Invitation-based surveys**: Check the **Participants** section. Each attendee's record shows the Answers count, indicating whether they submitted.
- **Results page**: Lists all submitted contributions with metadata including date, language, and (if not anonymous) respondent information.
- **Anonymous surveys**: You can see the number of contributions and their content but cannot link them to specific individuals.
- **Identified surveys (EU Login)**: Contributions can be linked to authenticated users.

## Steps / Procedure

**Using Participants (for invitation-based surveys):**

1. Open the survey and go to **Participants**.
2. Select the relevant participation group.
3. Review the attendee list. The **Answers** column shows the number of submissions for each invitee.
4. Attendees with Answers = 0 have not submitted.
5. You can filter or sort to find non-responders.

**Using the Results page:**

1. Open the survey and go to **Results**.
2. The contribution list shows all submitted responses.
3. Each contribution has a timestamp, language code, and Contribution ID.
4. If the survey is not anonymous, the respondent's email or identifier may be available.
5. Use filters to search for specific contributions by date, status, or other criteria.

**Checking draft status:**

1. The survey Overview page shows separate counts for **Contributions** (submitted) and **Drafts** (saved but not submitted).
2. Drafts are not visible in the Results page — they represent in-progress responses.

## Important Conditions / Limitations

* **Anonymous surveys restrict identification**: If the survey uses anonymous mode, you cannot see who submitted each contribution. You can see the total count and content but not the respondent's identity.
* **Invitation tracking**: Even in anonymous surveys, if invitations were used, you can see from the Participants section that a specific invitee has answered (their Answers count increases), but you cannot link that person to a specific contribution's content.
* **No real-time notification by default**: Survey owners are not notified in real time when a contribution is submitted (unless report emails are configured).
* **Filters on Results page**: The Results page supports filtering by date range, language, and other criteria. Use these to narrow down the list.
* **Multiple contributions**: If the survey allows multiple submissions per person, one attendee may have an Answers count greater than 1.

## Troubleshooting / Related Cases

* If you cannot see respondent identifiers in Results: check whether the survey is in anonymous mode.
* If the Participants section shows no answers but Results shows contributions: the contributions may have come through direct links rather than invitation links.
* If you want a report of non-respondents: use the Participants list and filter for Answers = 0.

## Out of Scope / Separate Topics

* Why a submitted contribution is not visible (see: KB-EUSURVEY-CONTRIB-006)
* How to check invitation status (see: KB-EUSURVEY-INVITE-002)
* How to filter or search contributions (see: KB-EUSURVEY-RESULTS-004)
* What "incomplete contribution" means (see: KB-EUSURVEY-CONTRIB-002)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: contribution_management
* user_role: survey_manager
* feature: respondent_tracking, results_view
* tags: see who answered, who responded, track respondents, non-responders, answers count
* synonyms: how to know who completed the survey, check who submitted, find non-respondents, list of people who answered, track participation
* product_terms: Results, Participants, Answers, anonymous, Contribution ID, filters
* exclude: edit contributions, export results, delete contributions
